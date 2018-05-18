import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.net.*;
import java.io.IOException;

public class CpuAgent {


	public static String getCpuIdle(){
		Process p;
		StringBuilder sb = new StringBuilder();
		String line = "";
		String iddle = "";
		try{
			p = Runtime.getRuntime().exec("mpstat");
	    	p.waitFor();

		    BufferedReader reader = 
		         new BufferedReader(new InputStreamReader(p.getInputStream()));
		    
		    reader.readLine();
		    reader.readLine();
		    reader.readLine();
		    line = reader.readLine();
		    String [] a = line.split("\\s+");
		    iddle = a[a.length - 1];
		    
		}catch(Exception e){
			e.printStackTrace();
		}
		return iddle;

	}

	public static void main(String [] args) {
		while(true) {
			forwardingUDP("10.0.0.254", getCpuIdle());
			try {
				Thread.sleep(5000);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private static void forwardingUDP(String ip, String msg) {
        try {
            InetAddress dest = InetAddress.getByName(ip);
            byte[] sendData = new byte[1024];
            DatagramSocket clientUDP = new DatagramSocket();
            clientUDP.setBroadcast(true);
            sendData = msg.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, dest, 9999);
            clientUDP.send(sendPacket);
            clientUDP.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}