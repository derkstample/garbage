import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
public class Client{
    private final static boolean debug=true;
    private String ipaddress;

    public Client(String address){
        ipaddress=address;
    }
    public static String getIpAddress(){//based on code found at https://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java
        String out="err:";
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            out=socket.getLocalAddress().getHostAddress();
        }catch(Exception e){
            out+=e.getLocalizedMessage();
        }
        return out;
    }

    public int getNumPlayers(){
        return sendReceive(0b00010000);
    }
    public int getPlayerX(int id){
        return sendReceive(0b00100000+(id&0b1111));
    }
    public int getPlayerY(int id){
        return sendReceive(0b00110000+(id&0b1111));
    }
    public int getGarbageX(int id){
        return sendReceive(0b01000000+(id&0b1111));
    }
    public int getGarbageY(int id){
        return sendReceive(0b01010000+(id&0b1111));
    }
    public int getScore(int id){
        return sendReceive(0b01100000+(id&0b1111));
    }
    public int getValue(int id){
        return sendReceive(0b01110000+(id&0b1111));
    }
    public int getID(int id){
        return sendReceive(0b10000000+(id&0b1111));
    }
    public int getNumGarbage(){
        return sendReceive(0b10010000);
    }
    public int quitGame(int id){
        return sendReceive(0b1010+(id&0b1111));
    }
    public int setPlayerX(int x,int id){
        return sendReceive(x<<8+0b1011+(id&0b1111));
    }
    public int setPlayerY(int y,int id){
        return sendReceive(y<<8+0b1100+(id&0b1111));
    }
    public int joinGame(){
        return sendReceive(0b11010000);
    }

    private int sendReceive(int send){//actual communication w/ server
        if(debug)System.out.println("   Client:"+send);
        int receive=0b11111111;
        try{
            Socket s=new Socket(ipaddress,8080);
            BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream()));
            BufferedWriter out=new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            out.write(send);
            out.flush();
            receive=in.read();
            s.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        if(debug)System.out.println("Server:"+receive);
        return receive;
    }
    @Override
    public String toString() {
        return "Client [ipaddress=" + ipaddress + "]";
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ipaddress == null) ? 0 : ipaddress.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Client other = (Client) obj;
        if (ipaddress == null) {
            if (other.ipaddress != null)
                return false;
        } else if (!ipaddress.equals(other.ipaddress))
            return false;
        return true;
    }
}