import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Server{
    private final static boolean debug=true;

    private final int BOARD_WIDTH;//biggest is 2bytes worth (65535) not sure what happens above
    private final int BOARD_HEIGHT;
    private ServerSocket ss;
    private Socket s;

    private ArrayList<Player> players;
    private ArrayList<Garbage> garbage;
    public static void main(String[] args){
        new Server(128,128);
    }
    public Server(int width,int height){
        BOARD_WIDTH=width;
        BOARD_HEIGHT=height;
        players=new ArrayList<Player>();
        garbage=new ArrayList<Garbage>();
        System.out.println(getIpAddress());
        Thread dataThread=new DataProcessor();
        try{
            ss=new ServerSocket(8080);
            dataThread.start();
            while(true){
                s=ss.accept();
                Thread clientThread=new HandleRequest(s);
                clientThread.start();
            }
        }catch(Exception e){
            if(debug)e.printStackTrace();
        }
    }

    public String getIpAddress(){//based on code found at https://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java
        String out="err";
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            out=socket.getLocalAddress().getHostAddress();
        }catch(Exception e){
            if(debug)e.printStackTrace();
        }
        return out;
    }
    private class DataProcessor extends Thread{
        public DataProcessor(){
        }
        public synchronized void run(){
            while(true){
                //TODO: check for collisions
                //TODO: ScoreManager??
            }
        }
    }

    private class HandleRequest extends Thread{
        Socket s;
        public HandleRequest(Socket s){
            this.s=s;
        }
        public synchronized void run(){
            try{
                String connection=s.getInetAddress().toString().substring(1);
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                String received=in.readLine();
                StringTokenizer arg=new StringTokenizer(received,":");
                if(debug)System.out.println(connection+":"+received);//using Client.readFriendly() because it was already there and i didnt want to ctrlc ctrlv
                switch(arg.nextToken()){
                    case "name":out.write(getName(arg.nextToken()));break;//get name of player xxxx
                    case "num":out.write(getNum(arg.nextToken()));break;//get number of players
                    case "xpos":out.write(getPlayerX(arg.nextToken()));break;//get xpos of player xxxx
                    case "ypos":out.write(getPlayerY(arg.nextToken()));break;//get ypos of player xxxx
                    case "garx":out.write(getGarbageX(arg.nextToken()));break;//get xpos of garbage xxxx
                    case "gary":out.write(getGarbageY(arg.nextToken()));break;//get ypos of garbage xxxx
                    case "scor":out.write(getScore(arg.nextToken()));break;//get score of player xxxx
                    case "valu":out.write(getValue(arg.nextToken()));break;//get value of garbage xxxx
                    case "id":out.write(getId(arg.nextToken()));break;//get id of player xxxx
                    case "snam":out.write(setName(arg.nextToken(),arg.nextToken()));break;//get number of garbage
                    case "quit":out.write(leaveGame(arg.nextToken()));break;//makes player xxxx leave the game
                    case "setx":out.write(setXPos(arg.nextToken(),arg.nextToken()));break;//set xpos of player xxxx
                    case "sety":out.write(setYPos(arg.nextToken(),arg.nextToken()));break;//set ypos of player xxxx
                    case "join":out.write(joinGame());break;//join game
                    case "head":out.write(getHeading(arg.nextToken()));break;//get heading of player xxxx
                    case "dim":out.write(getDimension(arg.nextToken()));break;//get dimensions of the game board 0=w 1=h
                }
                out.close();
            }catch(Exception e){
                if(debug)e.printStackTrace();
            }
        }
        private String getNum(String which){
            if(which.equals("p"))return ""+players.size();
            else if(which.equals("g"))return ""+garbage.size();
            else return "err";
        }
        private String getPlayerX(String index){
            return ""+players.get(Integer.parseInt(index)).getX();
        }
        private String getPlayerY(String index){
            return ""+players.get(Integer.parseInt(index)).getY();
        }
        private String getGarbageX(String index){
            return ""+garbage.get(Integer.parseInt(index)).getX();
        }
        private String getGarbageY(String index){
            return ""+garbage.get(Integer.parseInt(index)).getY();
        }
        private String getScore(String index){
            return ""+players.get(Integer.parseInt(index)).getScore();
        }
        private String getValue(String index){
            return ""+garbage.get(Integer.parseInt(index)).getValue();
        }
        private String getId(String index){
            return ""+players.get(Integer.parseInt(index)).getId();
        }
        private String leaveGame(String index){
            players.remove(Integer.parseInt(index));
            return "1";
        }
        private String setXPos(String index,String newPos){
            players.get(Integer.parseInt(index)).setX(Integer.parseInt(newPos));
            return "1";
        }
        private String setYPos(String index,String newPos){
            players.get(Integer.parseInt(index)).setY(Integer.parseInt(newPos));
            return "1";
        }
        private String joinGame(){
            players.add(new Player());
            players.get(players.size()-1).setId(players.size()-1);
            return ""+(players.size()-1);
        }
        private String getHeading(String index){
            return ""+players.get(Integer.parseInt(index)).getHeading();
        }
        private String getDimension(String which){
            switch(which){
                case "w":return ""+BOARD_WIDTH;
                case "h":return ""+BOARD_HEIGHT;
                default:return "err";
            }
        }
        private String getName(String index){
            return players.get(Integer.parseInt(index)).getName();
        }
        private String setName(String index,String newName){
            players.get(Integer.parseInt(index)).setName(newName);
            return "1";
        }
    }
}

/*
    name:n get name of player n         
    num:x get number of p=players g=garbage 
    xpos:n get xpos of player n        
    ypos:n get ypos of player n      
    garx:n get xpos of garbage n               
    gary:n get ypos of garbage n         
    scor:n get score of player n         
    valu:n get value of garbage n        
    id:n get id of player n              
    snam:n:name set name of player n to name     
    quit:n quit game of player n                            returns 1
    setx:n:x set xpos of player n to x                      returns 1
    sety:n:x set ypos of player n to x                      returns 1
    join join game                              
    head:n get heading of player n      
    dim:x get board dimensions w=width h=height
*/