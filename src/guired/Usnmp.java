package guired;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.JTextArea;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.CommunityTarget;
import org.snmp4j.MessageDispatcher;
import org.snmp4j.MessageDispatcherImpl;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.Priv3DES;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.AbstractTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

public class Usnmp implements CommandResponder {
	public final String
	sysName = "1.3.6.1.2.1.1.5.0",			//mib-2 > system > sysName
	sysDescr = "1.3.6.1.2.1.1.1.0",			//mib-2 > system > sysDescr
	sysContact = "1.3.6.1.2.1.1.4.0",		//mib-2 > system > sysContact
	sysUpTime = "1.3.6.1.2.1.1.3.0",		//mib-2 > system > sysUpTime
	cpuName = "1.3.6.1.2.1.25.3.2.1.3",		//mib-2 > host > hrDevice > hrDeviceTable > hrDeviceDescr
	cpuUse = "1.3.6.1.2.1.25.3.3.1.2",		//mib-2 > host > hrDevice > hrProcessorTable > hrProcessorLoad
	cpuTemperature = "1.3.6.1.4.1.8072.1.3.2.3.1.2.14.116.101.109.112.101.114.97.116.117.114.101.67.80.85", //snmptranslate -On NET-SNMP-EXTEND-MIB::nsExtendOutputFull.\"temperatureCPU\"
	hddTemperature = "1.3.6.1.4.1.8072.1.3.2.3.1.2.14.116.101.109.112.101.114.97.116.117.114.101.72.68.68", //snmptranslate -On NET-SNMP-EXTEND-MIB::nsExtendOutputFull.\"temperatureHDD\"
	ipRouteDest = "1.3.6.1.2.1.4.21.1.1",
	ipRouteMask = "1.3.6.1.2.1.4.21.1.11",
	ipRouteType = "1.3.6.1.2.1.4.21.1.8",
	ipRouteNextHop = "1.3.6.1.2.1.4.21.1.7",
	ipAdEntIfIndex = "1.3.6.1.2.1.4.20.1.2",
	ifInOctets = "1.3.6.1.2.1.2.2.1.10",
	ifOutOctets = "1.3.6.1.2.1.2.2.1.16";
	
	private Snmp snmp;

	public Usnmp() {
		TransportMapping transport;
		try {
			transport = new DefaultUdpTransportMapping();
			snmp = new Snmp(transport);		
			transport.listen();
		} catch (IOException e) {
			//e.printStackTrace();
			Guimain.LOG.output("ERROR EN new snmp!!", true);
		}		
	}	
		
	public String getRouteTable(String address) {
		String route = "";
		
		ArrayList<String> routeDest = build(address, ipRouteDest);
		ArrayList<String> routeMask = build(address, ipRouteMask);
		ArrayList<String> routeType = build(address, ipRouteType);
		ArrayList<String> routeNextHop = build(address, ipRouteNextHop);
				
		route = route + 
				getPaddedString("routeDest") + "-" +
				getPaddedString("routeMask") + "-" +
				getPaddedString("routeType") + "-" +
				getPaddedString("routeNextHop") + "\n";
		
		for (int i = 0; i < routeDest.size(); i++) {
			route = route + 
					getPaddedString(routeDest.get(i)) + "-" +
					getPaddedString(routeMask.get(i)) + "-" +
					getPaddedString(routeType.get(i)) + "-" +
					getPaddedString(routeNextHop.get(i)) + "\n";
		}
		
		return route;
	}
	
	public String getInOctects(String address) {
		String index = get(ipAdEntIfIndex+"."+address, address);
		return get(ifInOctets+"."+index, address);
	}
	
	public String getOutOctects(String address) {
		String index = get(ipAdEntIfIndex+"."+address, address);
		return get(ifOutOctets+"."+index, address);		
	}
		
	public String get(String oid, String address) {		
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(oid)));
		pdu.setType(PDU.GET);
		
		return getResul(pdu, address, true);
	}
	
	public String get(String oid, String address, int timeout) {
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(oid)));
		pdu.setType(PDU.GET);
		
		return getResul(pdu, address, true, timeout);
	}
	
	public String getNext(String oid, String address) {
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(oid)));
		pdu.setType(PDU.GETNEXT);
		
		return getResul(pdu, address, true);
	}
	
	public String getNextConfirma(String oid, String address) {
		String resu = getNext(oid, address, true);
		String oid2 = getNext(oid, address, false);
		
		if (!oid2.contains(oid)) {
			resu = "No se encuentra oid";
		}
		
		return resu;
	}
	
	public String set(String oid, String address, String value) {
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(oid), new OctetString(value)));
		pdu.setType(PDU.SET);
		
		return getResul(pdu, address, true);
	}

	private String getResul(PDU pdu, String address, boolean value) {
		return getResul(pdu, address, value, 100);
	}
		
	private String getResul(PDU pdu, String address, boolean value, int timeout) {
		String res = "ERROR";
		ResponseEvent event = null;
		
		try {
			event = snmp.send(pdu, getTarget(address, timeout));
		} catch (IOException e) {
			//e.printStackTrace();
			Guimain.LOG.output("ERROR en UsnmpGETSTRING", true);
		}
		
		if (event != null) {
			Guimain.LOG.output("EVENT RESPONSE: "+event.getResponse(), event.getResponse()==null);
			
			if (event.getResponse() != null) {
				if (value) {
					res = event.getResponse().get(0).getVariable().toString();
				} else {
					res = event.getResponse().get(0).getOid().toString();
				}
			}
		}
		
		return res;
	}
	
	private Target getTarget(String address, int timeout) {
		Address targetAddress = GenericAddress.parse("udp:"+address+"/161");
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString("public"));
		target.setAddress(targetAddress);
		target.setRetries(2);
		target.setTimeout(timeout);
		target.setVersion(SnmpConstants.version2c);
		return target;
	}
	
	private ArrayList<String> build (String address, String oid) {
		ArrayList<String> array = new ArrayList<String>();
		String oid2 = oid;
		String resu = "";
				
		while (oid2.contains(oid)) {
			resu = getNext(oid2, address, true);
			oid2 = getNext(oid2, address, false);
			
			if (oid2.contains(oid)) {
				array.add(resu);
			}
		}
		return array;
	}
	
	private String getNext(String oid, String address, boolean value) {
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(oid)));
		pdu.setType(PDU.GETNEXT);
		
		return getResul(pdu, address, value);
	}
	
	private String getPaddedString(String str) {
		char paddingChar = ' ';
	    if (str == null) {
	        throw new NullPointerException("Can not add padding in null String!");
	    }

	    int maxPadding = 20;
	    int length = str.length();
	    int padding = (maxPadding - length) / 2;
	    if (padding <= 0) {
	        return str;
	    }

	    String empty = "", hash = "#";

	    int extra = (length % 2 == 0) ? 1 : 0;

	    String leftPadding = "%" + padding + "s";
	    String rightPadding = "%" + (padding - extra) + "s";

	    String strFormat = leftPadding + "%s" + rightPadding;
	    String formattedString = String.format(strFormat, empty, hash, empty);

	    String paddedString = formattedString.replace(' ', paddingChar).replace(hash, str);
	    return paddedString;
	}
	
	// TRAP LISTENER
	
	private AbstractTransportMapping transport;
	private JTextArea traps;
	
	public synchronized void listen(JTextArea traps, String ip) throws IOException {		
		this.traps = traps;
		
        transport = new DefaultUdpTransportMapping(new UdpAddress(ip+"/162"));

	    ThreadPool threadPool = ThreadPool.create("DispatcherPool", 10);
	    MessageDispatcher mtDispatcher = new MultiThreadedMessageDispatcher(threadPool, new MessageDispatcherImpl());

	    // add message processing models
	    mtDispatcher.addMessageProcessingModel(new MPv1());
	    mtDispatcher.addMessageProcessingModel(new MPv2c());

	    // add all security protocols
	    SecurityProtocols.getInstance().addDefaultProtocols();
	    SecurityProtocols.getInstance().addPrivacyProtocol(new Priv3DES());

	    //Create Target
	    CommunityTarget target = new CommunityTarget();
	    target.setCommunity( new OctetString("public"));
	   
	    Snmp snmp = new Snmp(mtDispatcher, transport);
	    snmp.addCommandResponder(this);
	   
	    transport.listen();
		traps.setText("Escuchando traps en "+ip+"/162");

	    try
	    {
	      this.wait();
	    }
	    catch (InterruptedException ex)
	    {
	      Thread.currentThread().interrupt();
	    }		
	}	
	
	public void listenStop() {
		try {
			transport.close();
		} catch (IOException e) {
			Guimain.LOG.output("ERROR EN TRAPSTOP", true);
		}
	}
	
	public synchronized void processPdu(CommandResponderEvent cmdRespEvent) {
	    traps.setText(traps.getText() + "\n\n PDU recibida de " + cmdRespEvent.getPeerAddress() + " ... \n\n");
	    PDU pdu = cmdRespEvent.getPDU();
	    if (pdu != null) {
	    	traps.setText(
	    			traps.getText()+"\n"+
	    			pdu + "\n" +
	    			"Trap Type = " + pdu.getType() + "\n" +
	    			"Variable Bindings = " + pdu.getVariableBindings() + "\n"
			);	  
	    }
	}
}