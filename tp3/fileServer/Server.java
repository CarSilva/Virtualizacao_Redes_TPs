import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Server {

    public static void main(String [] args) {
        byte[] buf;
        try {
            InetAddress bind = InetAddress.getByName("10.0.0.250");
            DatagramSocket server = new DatagramSocket(11111, bind);
            while(true){
                buf = new byte[1024];
                DatagramPacket recv = new DatagramPacket(buf, buf.length);
                server.receive(recv);
                String recv_msg = new String(recv.getData());
                String cleaned = recv_msg.substring(0, cmp(recv_msg));
                String [] a = cleaned.split("&");
                System.out.println("FUI USADO");
                InetAddress dest = recv.getAddress();
                switch(a[0]){
                    case "1":
                        String ss = getFiles();
                        forwardingUDP(dest, ss);
                        break;
                    case "2":
                        List<String> file = readFile(a[1]);
                        file.add("END");
                        for (String s : file){
                            forwardingUDP(dest, s);
                        }
                        break;
                    case "3":
                        String name = a[1];
                        String fileContent = a[2];
                        writeFile(name, fileContent);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String filesMenu(){
        StringBuilder sb = new StringBuilder();

        return sb.toString();
    }

    public static String getFiles(){
        Process p;
        StringBuilder sb = new StringBuilder();
        String line = "";
        int counter = 0;
        try{
            p = Runtime.getRuntime().exec("ls files");
            p.waitFor();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            while((line = reader.readLine()) != null) {
                sb.append(counter + ": " + line + "\n");
                counter ++;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return sb.toString();

    }

    public static void writeFile(String fileName, String content) {
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(content);
            writer.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static List<String> readFile(String name){
        List<String> file = new ArrayList<>();
        Path path = Paths.get("files/" + name);
        try {
            file = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static void forwardingUDP(InetAddress dest, String msg) {

        try {
            byte[] sendData = new byte[1024];
            DatagramSocket clientUDP = new DatagramSocket();
            sendData = msg.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, dest, 9999);
            clientUDP.send(sendPacket);
            clientUDP.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static int cmp (String a){
        int i=0;
        while(i < a.length()){
            char l = a.charAt(i);
            int ascci = (int) l;
            if(l == 0){
                break;
            }
            else{
                i++;
            }
        }
        return i;
    }


}
