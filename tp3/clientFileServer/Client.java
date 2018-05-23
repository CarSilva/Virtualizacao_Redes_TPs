import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.*;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Client {

    public static void main(String args[]) {
        Scanner scan = new Scanner(System.in);
        try {
            byte[] buf;
            DatagramPacket recv;
            DatagramSocket server = new DatagramSocket(9999);
            String recv_str;
            String cleaned;
            StringBuilder sb = new StringBuilder();
            System.out.println(initMenu());
            while (true) {
                String next = scan.next();
                switch (next) {

                    case "1":
                        forwardingUDP(InetAddress.getByName("10.0.0.250"), next);
                        buf = new byte[1024];
                        recv = new DatagramPacket(buf, buf.length);
                        server.receive(recv);
                        recv_str = new String(recv.getData());
                        cleaned = recv_str.substring(0, cmp(recv_str));
                        System.out.println(cleaned);
                        System.out.println(initMenu());
                        break;

                    case "2":
                        System.out.println("Escolha o ficheiro a fazer Download");
                        next = scan.next();
                        String send = "2&" + next;
                        forwardingUDP(InetAddress.getByName("10.0.0.250"), send);
                        buf = new byte[1024];
                        recv = new DatagramPacket(buf, buf.length);
                        server.receive(recv);
                        recv_str = new String(recv.getData());
                        cleaned = recv_str.substring(0, cmp(recv_str));
                        while(true){
                            if(cleaned.contains("END")){
                                System.out.println(initMenu());
                                break;
                            }
                            sb.append(cleaned);
                            buf = new byte[1024];
                            recv = new DatagramPacket(buf, buf.length);
                            server.receive(recv);
                            recv_str = new String(recv.getData());
                            cleaned = recv_str.substring(0, cmp(recv_str));
                        }
                        writeFile(next, sb.toString());
                        sb = new StringBuilder();
                        break;

                    case "3":
                        System.out.println("Escolha o ficheiro a fazer Upload");
                        next = scan.next();
                        List<String> content = readFile(next);
                        sb.append("3").append("&").append(next).append("&");
                        for(String b : content){
                            sb.append(b).append("\n");
                        }
                        forwardingUDP(InetAddress.getByName("10.0.0.250"), sb.toString());
                        sb = new StringBuilder();
                        System.out.println(initMenu());
                        break;
                    case "0":
                        System.exit(1);
                        break;
                    default:
                        System.out.println("NOT SUPPORTED OPTION");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static List<String> readFile(String name){
        List<String> file = new ArrayList<>();
        Path path = Paths.get(name);
        try {
            file = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
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

    public static void forwardingUDP(InetAddress dest, String msg) {

        try {
            byte[] sendData = new byte[1024];
            DatagramSocket clientUDP = new DatagramSocket();
            sendData = msg.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, dest, 11111);
            clientUDP.send(sendPacket);
            clientUDP.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static String initMenu(){
        StringBuilder sb = new StringBuilder();
        sb.append("0.Sair\n");
        sb.append("1.Listar Ficheiros\n");
        sb.append("2.Efetuar Download\n");
        sb.append("3.Efetuar Upload\n");
        return sb.toString();

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
