package net.floodlightcontroller.mactracker;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketOut;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFPort;
import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;

import net.floodlightcontroller.core.IFloodlightProviderService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.Set;

import net.floodlightcontroller.packet.ARP;
import net.floodlightcontroller.packet.Data;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPacket;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.UDP;
import net.floodlightcontroller.statistics.StatisticsCollector;
import net.floodlightcontroller.statistics.SwitchPortBandwidth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.security.ntlm.Client;

public class MACTracker implements IOFMessageListener, IFloodlightModule {

	protected IFloodlightProviderService floodlightProvider;
	protected Set<Long> macAddresses;
	protected static Logger logger;
	protected int i;
	protected Map<IPv4Address, MacAddress> ipMac = new HashMap<>();
	protected Map<IPv4Address, Double> cpus = new HashMap<>();
	protected Map<Integer, Long > band_port = new HashMap<>();
	protected int lastDns = 0;
	protected int n_balancer = 0;
	protected long last_statistic = System.currentTimeMillis();
	protected StatisticsCollector statistics = new StatisticsCollector();

	@Override
	public String getName() {
		return MACTracker.class.getSimpleName();
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
	    return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		 Collection<Class<? extends IFloodlightService>> l =
	     new ArrayList<Class<? extends IFloodlightService>>();
	     l.add(IFloodlightProviderService.class);
		 return l;
	}

	@Override
	public void init(FloodlightModuleContext context) throws FloodlightModuleException {
		 i = 0;
		 floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
		 macAddresses = new ConcurrentSkipListSet<Long>();
		 logger = LoggerFactory.getLogger(MACTracker.class);
	}

	@Override
	public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);

	}

	@Override
	public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		IPv4Address any_dns = IPv4Address.of("10.0.0.249");
		IPv4Address any_fileServer = IPv4Address.of("10.0.0.250");
		IPv4Address broadcast = IPv4Address.of("10.0.0.254");
		MacAddress broad_mac = MacAddress.of("11:11:11:11:11:11");
		IPv4Address dns_1 = IPv4Address.of("10.0.0.3");
		IPv4Address dns_2 = IPv4Address.of("10.0.0.4");
		long s = sw.getId().getLong();
		Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		String infoCpu = "";
		
		if(msg.getType() == OFType.PACKET_IN ) {
			/* Recebe informação dos CPUs dos Servidores de ficheiros */
			if(eth.getEtherType() == EthType.IPv4) {
				IPv4 ipv4 = (IPv4) eth.getPayload();
				System.out.println(ipv4.getDestinationAddress() + " " + ipv4.getSourceAddress());
				if(ipv4.getDestinationAddress().equals(broadcast) && ipv4.getProtocol() == IpProtocol.UDP) {
					UDP udp = (UDP) ipv4.getPayload();
					Data info = (Data) udp.getPayload();
					byte[] a = info.getData();
					for(int i = 0; i < info.getData().length; i++ ) {
						infoCpu += (char) a[i];
					}
					String [] result = infoCpu.split(",");
					String output = result[0] + "." + result[1];
					double cpu_idle = Double.parseDouble(output);
					cpus.put(ipv4.getSourceAddress(), cpu_idle);
					ipMac.put(ipv4.getSourceAddress(), eth.getSourceMACAddress());
					return Command.STOP;
				}
				else if(ipv4.getDestinationAddress().equals(any_fileServer) && ipv4.getProtocol() == IpProtocol.UDP) {
					double min = Integer.MAX_VALUE;
					IPv4Address ip_forward = null;
					for(IPv4Address ip : cpus.keySet()) {
						double cpu_idle = cpus.get(ip);
						if(cpu_idle <= min ) {
							min = cpu_idle;
							ip_forward = ip;
						}
					}
					System.out.println("HEY" + ip_forward + " " + s);
					buildIpv4Udp(sw, eth, ipv4, ip_forward);
					return Command.STOP;
				}
				else if(s == 2) {
					if(ipv4.getDestinationAddress().equals(any_dns) && ipv4.getProtocol() == IpProtocol.UDP) {
						if(lastDns == 0) {
							buildIpv4Udp(sw, eth, ipv4, dns_1);
							lastDns = 1;
						}
						else {
							buildIpv4Udp(sw, eth, ipv4, dns_2);
							lastDns = 0;
						}
						return Command.STOP;
					}
				}
				else if(s == 9) {
					if(ipv4.getDestinationAddress().equals(any_dns) && ipv4.getProtocol() == IpProtocol.UDP) {
						buildIpv4Udp(sw, eth, ipv4, dns_1);
						return Command.STOP;
					}
				}
			}
			/* Envia MAC com broadcast associando este ao ip do pedido */
			if(eth.getEtherType() == EthType.ARP) {
				ARP arp = (ARP) eth.getPayload();
				if(arp.getTargetProtocolAddress().equals(broadcast)) {
					buildArp(sw, arp, eth, broadcast, broad_mac);
					if(i == 0) {
						request_arp(arp, eth, sw, any_dns, broad_mac);
						i++;
					}
					return Command.STOP;
				}
				else if(arp.getTargetProtocolAddress().equals(any_fileServer)){
						System.out.println("heloo");
						buildArp(sw, arp, eth, any_fileServer, broad_mac);
						return Command.STOP;
					}
				else if(arp.getTargetProtocolAddress().equals(any_dns)) {
						buildArp(sw, arp, eth, any_dns, broad_mac);
						return Command.STOP;
					}
				else if(s == 3) {
					if(arp.getSenderProtocolAddress().equals(any_dns)) {
						ipMac.put(dns_1, arp.getSenderHardwareAddress());
						return Command.STOP;
					}
				}
				else if(s == 7) {
					if(arp.getSenderProtocolAddress().equals(any_dns)) {
						ipMac.put(dns_2, arp.getSenderHardwareAddress());
						return Command.STOP;
					}
				}
			
			}
		}
		
		
		/*************************BANDWIDTH**********************/
		/*
		DatapathId dId = sw.getId();
		Collection<OFPort> ports = sw.getEnabledPortNumbers();
		for(OFPort p : ports) {
			SwitchPortBandwidth bandwidth =  statistics.getBandwidthConsumption(dId, p);
			if(bandwidth != null) {
				long now = System.currentTimeMillis();
				if(now - last_statistic >= 10000) {
					long rx = bandwidth.getBitsPerSecondRx().getValue();
					long tx = bandwidth.getBitsPerSecondRx().getValue();
					long link_speed = bandwidth.getLinkSpeedBitsPerSec().getValue();
					long used_bandwidth = rx + tx;
					if (used_bandwidth < 0) {used_bandwidth = 0;}
					long available = link_speed - used_bandwidth;
					logger.info("SW: "+s+" Port: "+ p.getPortNumber() + " Link Speed: " + link_speed);
					logger.info("SW: "+s+" Port: "+ p.getPortNumber() + " RX: " + rx);
					logger.info("SW: "+s+" Port: "+ p.getPortNumber() + " TX: " + tx);
					logger.info("SW: "+s+" Port: "+ p.getPortNumber() + " Bandwidth available: " + available);
					if(s==2) {
						band_port.put(p.getPortNumber(), available);
					}
					last_statistic = System.currentTimeMillis();
				}
			}
		}
		/*******************FIM_BANDWIDTH*****************/
		
	    return Command.CONTINUE;
	}

	private void buildIpv4Udp(IOFSwitch sw, Ethernet eth, IPv4 ipv4, IPv4Address ip_forward) {
		UDP udp = (UDP) ipv4.getPayload();
		Data data = (Data) udp.getPayload();
		IPacket udp_ree = new Ethernet()
				.setSourceMACAddress(eth.getSourceMACAddress())
				.setDestinationMACAddress(ipMac.get(ip_forward).toString())
				.setEtherType(EthType.IPv4)
				.setPayload(
						new IPv4()
						.setSourceAddress(ipv4.getSourceAddress())
						.setDestinationAddress(ip_forward)
						.setTtl((byte) 64)
						.setProtocol(IpProtocol.UDP)
						.setPayload(
								new UDP()
								.setSourcePort(udp.getSourcePort())
								.setDestinationPort(udp.getDestinationPort())
								.setPayload(
										new Data()
										.setData(data.getData())
										)
								)
						);
		sendPacket(udp_ree, sw);
		
	}

	private void buildArp(IOFSwitch sw, ARP arp, Ethernet eth, IPv4Address ip, MacAddress broad_mac) {
			IPacket arp_rep = new Ethernet()
					.setSourceMACAddress(broad_mac.toString())
					.setDestinationMACAddress(eth.getSourceMACAddress())
					.setEtherType(EthType.ARP)
					.setPriorityCode((byte)8000)
					.setPayload(
							new ARP()
							.setHardwareType(ARP.HW_TYPE_ETHERNET)
							.setProtocolType(ARP.PROTO_TYPE_IP)
							.setHardwareAddressLength((byte)6)
							.setProtocolAddressLength((byte)4)
							.setOpCode(ARP.OP_REPLY)
							.setSenderHardwareAddress(broad_mac)
							.setSenderProtocolAddress(ip)
							.setTargetHardwareAddress(arp.getSenderHardwareAddress())
							.setTargetProtocolAddress(arp.getSenderProtocolAddress()));
			sendPacket(arp_rep, sw);
		
	}
	
	private void request_arp(ARP arp, Ethernet eth, IOFSwitch sw, IPv4Address ip, MacAddress mac) {
		IPacket new_arp_req;
		new_arp_req = new Ethernet()
				.setSourceMACAddress(eth.getSourceMACAddress())
				.setDestinationMACAddress(mac.toString())
				.setEtherType(EthType.ARP)
				.setPriorityCode((byte)8000)
				.setPayload(
						new ARP()
						.setHardwareType(ARP.HW_TYPE_ETHERNET)
						.setProtocolType(ARP.PROTO_TYPE_IP)
						.setHardwareAddressLength((byte)6)
						.setProtocolAddressLength((byte)4)
						.setOpCode(ARP.OP_REQUEST)
						.setSenderHardwareAddress(eth.getSourceMACAddress())
						.setSenderProtocolAddress(arp.getSenderProtocolAddress())
						.setTargetHardwareAddress(mac)
						.setTargetProtocolAddress(ip));
		sendPacket(new_arp_req, sw);		
	}

	private void sendPacket(IPacket arp_rep, IOFSwitch sw) {
		List<OFAction> list = new ArrayList<>();
		list.add(sw.getOFFactory().actions().output(OFPort.FLOOD,  0xffFFffFF));
		OFPacketOut po = sw.getOFFactory().buildPacketOut()
				.setData(arp_rep.serialize())
				.setActions(list)
				.setInPort(OFPort.CONTROLLER)
				.build();
		sw.write(po);
		
	}
	
}