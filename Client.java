import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
public class Client{
    private final static boolean debug=true;
    private String ipaddress;

    public Client(String address){
        ipaddress=address;
    }

    public ArrayList<Player> getPlayers(){
        ArrayList<Player> out=new ArrayList<Player>();
        int numPlayers=getNumPlayers();
        for(int i=0;i<numPlayers;i++){
            out.add(new Player(getPlayerX(i),getPlayerY(i),getScore(i),i,-1,getHeading(i)));
        }
        return out;
    }
    public ArrayList<Garbage> getGarbage(){
        ArrayList<Garbage> out=new ArrayList<Garbage>();
        int numGarbage=getNumGarbage();
        for(int i=0;i<numGarbage;i++){
            out.add(new Garbage(getGarbageX(i),getGarbageY(i),getValue(i),-1,-1));
        }
        return out;
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
        int temp=sendReceive(0b11010000);
        if(temp==255)return -1;
        return temp;
    }
    public int getHeading(int id){
        return sendReceive(0b11100000+(id&0b1111));
    }

    private int sendReceive(int send){//actual communication w/ server
        if(debug)System.out.println("   Client:"+readFriendly(send));
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
        if(debug)System.out.println("Server:"+Integer.toBinaryString(receive));
        return receive;
    }
    public static String readFriendly(int protocol){
        switch((protocol&0b11110000)>>4){
            case 0b0000:return "noop";
            case 0b0001:return "get numPlayers";
            case 0b0010:return "get xPos of player "+(protocol&0b1111);
            case 0b0011:return "get yPos of player "+(protocol&0b1111);
            case 0b0100:return "get xPos of garbage "+(protocol&0b1111);
            case 0b0101:return "get yPos of garbage "+(protocol&0b1111);
            case 0b0110:return "get score of player "+(protocol&0b1111);
            case 0b0111:return "get value of garbage "+(protocol&0b1111);
            case 0b1000:return "get id of player "+(protocol&0b1111);
            case 0b1001:return "get numGarbage";
            case 0b1010:return "quit game of player "+(protocol&0b1111);
            case 0b1011:return "set xPos of player "+(protocol&0b1111)+" to "+(protocol>>8);
            case 0b1100:return "set yPos of player "+(protocol&0b1111)+" to "+(protocol>>8);
            case 0b1101:return "join game";
            case 0b1110:return "get heading of player "+(protocol&0b1111);
            case 0b1111:return "noop";
        }
        return "error: unknown protocol";
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