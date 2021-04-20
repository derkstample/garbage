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
            out.add(new Player(getPlayerX(i),getPlayerY(i),getScore(i),i,-1,getHeading(i),getName(i)));
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
        //return sendReceive(0b00100000+(id&0b1111));
        return ductTape("X"+id);
    }
    public int getPlayerY(int id){
        //return sendReceive(0b00110000+(id&0b1111));
        return ductTape("Y"+id);
    }
    public int getGarbageX(int id){
        //return sendReceive(0b01000000+(id&0b1111));
        return ductTape("gX"+id);
    }
    public int getGarbageY(int id){
        //return sendReceive(0b01010000+(id&0b1111));
        return ductTape("gY"+id);
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
        return sendReceive(0b00010001);
    }
    public int quitGame(int id){
        return sendReceive(0b1010+(id&0b1111));
    }
    public int setPlayerX(int x,int id){
        //return sendReceive(((x&255)<<8)+0b10110000+(id&0b1111));
        return ductTape("setX"+id+","+x);
    }
    public int setPlayerY(int y,int id){
        //return sendReceive(((y&255)<<8)+0b11000000+(id&0b1111));
        return ductTape("setY"+id+","+y);
    }
    public int joinGame(){
        int temp=sendReceive(0b11010000);
        if(temp==255)return -1;
        return temp;
    }
    public int getHeading(int id){
        return sendReceive(0b11100000+(id&0b1111));
    }
    public int getWidth(){
        return sendReceive(0b11110000);
    }
    public int getHeight(){
        return sendReceive(0b11110001);
    }
    public String getName(int id){
        int temp=sendReceive(0b00000000+(id&0b1111));
        String out="";//i made this while super tired its probably gross i just dont care rn
        out+=(char)(97+(temp>>4)&0b1111);
        out+=(char)(97+temp&0b1111);
        return out;
    }
    public int setName(String name,int id){
        byte[] bytes=name.getBytes();
        int out=0;
        for(int i=0;i<2;i++){
            out+=((bytes[i]-97)<<(4*i));//turn 6 character a-z string into 24 bit int via magic
        }
        return sendReceive(((out&255)<<8)+0b10010000+(id&0b1111));
    }
    private int ductTape(String send){
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
        if(debug)System.out.println("Server:"+Integer.toBinaryString(receive));
        return receive;
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
            case 0b0000:return "get name of player "+(protocol&0b1111);
            case 0b0001:if((protocol&0b1111)==0)return "get numPlayers";else if((protocol&0b1111)==1)return "get numGarbage";
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
            case 0b1111:if((protocol&0b1111)==0)return "get width of the game";else if((protocol&0b1111)==1)return "get height of the game";
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