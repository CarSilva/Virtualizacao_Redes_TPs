package net.floodlightcontroller.mactracker;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.OFMatchV1;
import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFPacketIn;
import org.projectfloodlight.openflow.protocol.OFPacketOut;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.protocol.action.OFAction;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.EthType;
import org.projectfloodlight.openflow.types.IPv4Address;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.types.MacAddress;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.TransportPort;

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
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.IPacket;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.packet.UDP;
import net.floodlightcontroller.statistics.StatisticsCollector;
import net.floodlightcontroller.statistics.SwitchPortBandwidth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

public class MACTracker implements IOFMessageListener, IFloodlightModule {

	protected IFloodlightProviderService floodlightProvider;
	protected Set<Long> macAddresses;
	protected static Logger logger;
	protected int i;
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
		Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		if(msg.getType() == OFType.PACKET_IN && eth.getEtherType() == EthType.ARP) {
			IPv4Address newIP = IPv4Address.of("10.0.0.250");
			ARP arp_req = (ARP) eth.getPayload();
			if(arp_req.getTargetProtocolAddress().equals(newIP) && sw.getId().getLong() == 1) {
				String mac = null;
				if((i % 2) == 0) {
					mac = "00:00:00:00:00:10";
				}
				else {
					mac = "00:00:00:00:00:09";
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
								.setSenderHardwareAddress(MacAddress.of(Ethernet.toMACAddress(mac)))
								.setSenderProtocolAddress(newIP)
								.setTargetHardwareAddress(arp_req.getSenderHardwareAddress())
								.setTargetProtocolAddress(arp_req.getSenderProtocolAddress()));
				sendARP(arp_rep, sw);
				return Command.STOP;
				
			}
		}			
		long s = sw.getId().getLong();
		/*DatapathId dId = sw.getId();
		Collection<OFPort> ports = sw.getEnabledPortNumbers();
		for(OFPort p : ports) {
			SwitchPortBandwidth bandwidth =  statistics.getBandwidthConsumption(dId, p);
			if(bandwidth != null) {
				System.out.println("###### Port: " + p.getPortNumber() + " SWITCH: "+s+" #########");
				System.out.println( "RX:  " + (bandwidth.getBitsPerSecondRx().getValue()));
				System.out.println( "TX:  " + (bandwidth.getBitsPerSecondTx().getValue()));
				System.out.println("#################################");
			}
		}*/
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
	    				System.out.println("O endereço MAC origem é: "+orig);
	    				System.out.println("O endereço MAC destino é: "+dest);
	    				System.out.println("-------------------------------------------------------");
	    			}
				}
	    	break;
	    default:
	    	break;
		}
	    return Command.CONTINUE;
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