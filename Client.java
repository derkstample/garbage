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
        return Integer.parseInt(sendReceive("num:p"));
    }
    public int getPlayerX(int id){
        return Integer.parseInt(sendReceive("xpos:"+id));
    }
    public int getPlayerY(int id){
        return Integer.parseInt(sendReceive("ypos:"+id));
    }
    public int getGarbageX(int id){
        return Integer.parseInt(sendReceive("garx:"+id));
    }
    public int getGarbageY(int id){
        return Integer.parseInt(sendReceive("gary:"+id));
    }
    public int getScore(int id){
        return Integer.parseInt(sendReceive("scor:"+id));
    }
    public int getValue(int id){
        return Integer.parseInt(sendReceive("valu:"+id));
    }
    public int getID(int id){
        return Integer.parseInt(sendReceive("id:"+id));
    }
    public int getNumGarbage(){
        return Integer.parseInt(sendReceive("num:g"));
    }
    public int quitGame(int id){
        return Integer.parseInt(sendReceive("quit:"+id));
    }
    public int setPlayerX(int x,int id){
        return Integer.parseInt(sendReceive("setx:"+id+":"+x));
    }
    public int setPlayerY(int y,int id){
        return Integer.parseInt(sendReceive("sety:"+id+":"+y));
    }
    public int joinGame(){
        int temp=Integer.parseInt(sendReceive("join"));
        if(temp==255)return -1;
        return temp;
    }
    public int getHeading(int id){
        return Integer.parseInt(sendReceive("head:"+id));
    }
    public int getWidth(){
        return Integer.parseInt(sendReceive("dim:w"));
    }
    public int getHeight(){
        return Integer.parseInt(sendReceive("dim:h"));
    }
    public String getName(int id){
        return sendReceive("name"+id);
    }
    public int setName(String name,int id){
        return Integer.parseInt(sendReceive("snam:"+id+":"+name));
    }
    private String sendReceive(String send){//actual communication w/ server
        if(debug)System.out.println("   Client:"+send);
        String receive="";
        try{
            Socket s=new Socket(ipaddress,8080);
            BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream()));
            BufferedWriter out=new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            out.write(send);
            out.flush();
            receive=in.readLine();
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