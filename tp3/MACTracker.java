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

public class MACTracker implements IOFMessageListener, IFloodlightModule {

	protected IFloodlightProviderService floodlightProvider;
	protected Set<Long> macAddresses;
	protected static Logger logger;
	protected int i;
	protected boolean need_reply = false;
	protected boolean sending_udp = false;
	protected MacAddress[] macs_Balancer = new MacAddress[2];
	protected IPv4Address[] ips_Balancer = new IPv4Address[2];
	protected Map<Integer, Long > band_port = new HashMap<>();
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
		long s = sw.getId().getLong();
		Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		
		/*******************TP3_PARTE 1*********************/
		switch (msg.getType()) {
			case PACKET_IN:
				if(s == 1) {
	    			if (eth.getEtherType() == EthType.IPv4) {
	    				System.out.println("-------------------------------------------------------");
	    				System.out.println("Pacote Internet Protocol recebido");
	    				IPv4 ipv4 = (IPv4) eth.getPayload();
	    				IPv4Address orig = ipv4.getSourceAddress();
	    				IPv4Address dest = ipv4.getDestinationAddress();
	    				System.out.println("O endereço IP origem é: "+orig);
	    				System.out.println("O endereço IP destino é: "+dest);
	    				if (ipv4.getProtocol() == IpProtocol.TCP) {
	    					System.out.println("O protocolo de transporte é TCP");
	    				} else if (ipv4.getProtocol() == IpProtocol.UDP) {
	    					System.out.println("O protocolo de transporte é UDP");
	    					i++;
	    				}
	    				System.out.println("-------------------------------------------------------");
	    			}
	    			else if(eth.getEtherType() == EthType.ARP) {
	    				System.out.println("-------------------------------------------------------");
	    				System.out.println("Pacote Address Resolution Protocol recebido");
	    				MacAddress orig = eth.getSourceMACAddress();
	    				MacAddress dest = eth.getDestinationMACAddress();
	    				ARP arp = (ARP) eth.getPayload();
	    				System.out.println("O endereço MAC origem é: "+orig);
	    				System.out.println("O endereço MAC destino é: "+dest);
	    				
	    				System.out.println("-------------------------------------------------------");
	    			}
				}
			if(s == 2) {
				if (eth.getEtherType() == EthType.IPv4) {
    				System.out.println("---------------------------2----------------------------");
    				System.out.println("Pacote Internet Protocol recebido");
    				IPv4 ipv4 = (IPv4) eth.getPayload();
    				IPv4Address orig = ipv4.getSourceAddress();
    				IPv4Address dest = ipv4.getDestinationAddress();
    				System.out.println("O endereço IP origem é: "+orig);
    				System.out.println("O endereço IP destino é: "+dest);
    				if (ipv4.getProtocol() == IpProtocol.TCP) {
    					System.out.println("O protocolo de transporte é TCP");
    				} else if (ipv4.getProtocol() == IpProtocol.UDP) {
    					System.out.println("O protocolo de transporte é UDP");
    				}
    				System.out.println("---------------------------2----------------------------");
    			}
    			else if(eth.getEtherType() == EthType.ARP) {
    				System.out.println("--------------------------2-----------------------------");
    				System.out.println("Pacote Address Resolution Protocol recebido");
    				MacAddress orig = eth.getSourceMACAddress();
    				MacAddress dest = eth.getDestinationMACAddress();
    				ARP arp = (ARP) eth.getPayload();
    				System.out.println("O endereço MAC origem é: "+orig);
    				System.out.println("O endereço MAC destino é: "+dest);
    				System.out.println("O endereço IP origem é: "+arp.getSenderProtocolAddress());
    				System.out.println("O endereço IP Destino é: "+arp.getTargetProtocolAddress());
    				System.out.println("--------------------------2-----------------------------");
    			}
			}
	    	break;
	    default:
	    	break;
		}
		/*******************FIM TP3_PARTE 1*********************/
		
		
		/*******************TESTE SEM ARPS*************************************/
		
		if(n_balancer >= 2 && msg.getType() == OFType.PACKET_IN && sw.getId().getLong() == 1 && 
				                                                  eth.getEtherType() == EthType.IPv4) {
			IPv4 ipv4 = (IPv4) eth.getPayload();
			if(ipv4.getProtocol() == IpProtocol.UDP) {
				IPv4Address dest = ipv4.getDestinationAddress();
				IPv4Address dest_r = IPv4Address.of("10.0.0.250");
				UDP udp = (UDP) ipv4.getPayload();
				Data data = (Data) udp.getPayload();
				if(dest.equals(dest_r)) {
					MacAddress mac = null;
					IPv4Address ip = null;
					if((i % 2) == 0) {
						mac = macs_Balancer[0];
						ip = ips_Balancer[0];
					}
					else {
						mac = macs_Balancer[1];
						ip = ips_Balancer[1];
					}
					IPacket udp_ree = new Ethernet()
							.setSourceMACAddress(eth.getSourceMACAddress())
							.setDestinationMACAddress(mac)
							.setEtherType(EthType.IPv4)
							.setPayload(
									new IPv4()
									.setSourceAddress(ipv4.getSourceAddress())
									.setDestinationAddress(ip)
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
					sendUDP(udp_ree, sw);
					return Command.STOP;
					
				}
			}
		}
		
		/******************FIM TESTES SEM ARPS**********************************/
		

		/*******************PARTE 2 COM MACS STATIC**************/
		if(msg.getType() == OFType.PACKET_IN && eth.getEtherType() == EthType.ARP) {
			IPv4Address newIP = IPv4Address.of("10.0.0.250");
			IPv4Address n1 = IPv4Address.of("10.0.0.2");
			IPv4Address n2 = IPv4Address.of("10.0.0.3");
			ARP arp = (ARP) eth.getPayload();
			
			if(arp.getTargetProtocolAddress().equals(newIP) && sw.getId().getLong() == 1 &&
																	n_balancer < 2) {
				System.out.println("HEY YO");
				request_host_macs(eth, sw);
				this.need_reply = true;
				return Command.STOP;
			}
			
			if(arp.getSenderProtocolAddress().equals(n1)) {
				macs_Balancer[0] = arp.getSenderHardwareAddress();
				ips_Balancer[0] = arp.getSenderProtocolAddress();
				this.n_balancer++;
			}else if(arp.getSenderProtocolAddress().equals(n2)) {
				macs_Balancer[1] = arp.getSenderHardwareAddress();
				ips_Balancer[1] = arp.getSenderProtocolAddress();
				this.n_balancer++;
			}
				
			if(n_balancer >= 2 && sw.getId().getLong() == 1 && need_reply) {		
					MacAddress mac = null;
					if((i % 2) == 0) {
						mac = macs_Balancer[0];
					}
					else {
						mac = macs_Balancer[1];
					}
					IPacket arp_rep = new Ethernet()
							.setSourceMACAddress(mac)
							.setDestinationMACAddress(eth.getSourceMACAddress())
							.setEtherType(EthType.ARP)
							.setPriorityCode(eth.getPriorityCode())
							.setPayload(
									new ARP()
									.setHardwareType(ARP.HW_TYPE_ETHERNET)
									.setProtocolType(ARP.PROTO_TYPE_IP)
									.setHardwareAddressLength((byte)6)
									.setProtocolAddressLength((byte)4)
									.setOpCode(ARP.OP_REPLY)
									.setSenderHardwareAddress(mac)
									.setSenderProtocolAddress(newIP)
									.setTargetHardwareAddress(arp.getSenderHardwareAddress())
									.setTargetProtocolAddress(arp.getSenderProtocolAddress()));
					sendARP(arp_rep, sw);
					return Command.STOP;
				}
		}
		/*******************FIM PARTE 2 COM MACS STATIC**************/
		
		
		
		/*************************BANDWIDTH**********************/
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

	private void sendUDP(IPacket udp_ree, IOFSwitch sw) {
		List<OFAction> list = new ArrayList<>();
		list.add(sw.getOFFactory().actions().output(OFPort.FLOOD,  0xffFFffFF));
		OFPacketOut po = sw.getOFFactory().buildPacketOut()
			    .setData(udp_ree.serialize())
			    .setActions(list)
			    .setInPort(OFPort.CONTROLLER)
			    .build();
			  
			sw.write(po);
	}

	private void request_host_macs(Ethernet eth, IOFSwitch sw) {
		IPacket new_arp_req;
		new_arp_req = new Ethernet()
				.setSourceMACAddress(eth.getSourceMACAddress())
				.setDestinationMACAddress("ff:ff:ff:ff:ff:ff")
				.setEtherType(EthType.ARP)
				.setPriorityCode(eth.getPriorityCode())
				.setPayload(
						new ARP()
						.setHardwareType(ARP.HW_TYPE_ETHERNET)
						.setProtocolType(ARP.PROTO_TYPE_IP)
						.setHardwareAddressLength((byte)6)
						.setProtocolAddressLength((byte)4)
						.setOpCode(ARP.OP_REQUEST)
						.setSenderHardwareAddress(eth.getSourceMACAddress())
						.setSenderProtocolAddress(IPv4Address.of("10.0.0.1"))
						.setTargetHardwareAddress(MacAddress.of(Ethernet.toMACAddress("00:00:00:00:00:00")))
						.setTargetProtocolAddress(IPv4Address.of("10.0.0.2")));
		sendARP(new_arp_req, sw);
		new_arp_req = new Ethernet()
				.setSourceMACAddress(eth.getSourceMACAddress())
				.setDestinationMACAddress("ff:ff:ff:ff:ff:ff")
				.setEtherType(EthType.ARP)
				.setPriorityCode(eth.getPriorityCode())
				.setPayload(
						new ARP()
						.setHardwareType(ARP.HW_TYPE_ETHERNET)
						.setProtocolType(ARP.PROTO_TYPE_IP)
						.setHardwareAddressLength((byte)6)
						.setProtocolAddressLength((byte)4)
						.setOpCode(ARP.OP_REQUEST)
						.setSenderHardwareAddress(eth.getSourceMACAddress())
						.setSenderProtocolAddress(IPv4Address.of("10.0.0.1"))
						.setTargetHardwareAddress(MacAddress.of(Ethernet.toMACAddress("00:00:00:00:00:00")))
						.setTargetProtocolAddress(IPv4Address.of("10.0.0.3")));
		sendARP(new_arp_req, sw);
		
	}

	private void sendARP(IPacket arp_rep, IOFSwitch sw) {
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