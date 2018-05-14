import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

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

	public static void main(String [] args){
		if(args.length < 1) {
			System.out.println("NEED PORT NUMBER");
			System.exit(1);
		}
		int portNumber = Integer.parseInt(args[0]);
		try {
			ServerSocket server = new ServerSocket(portNumber);
			Socket client;
			while((client = server.accept()) != null){
				System.out.println("NEW CLIENT  " + client.getInetAddress());
			    PrintWriter out =
			        new PrintWriter(client.getOutputStream(), true);
			    out.println(getCpuIdle());
			}
		}catch(Exception e) {
			e.printStackTrace();
		}

	}
}