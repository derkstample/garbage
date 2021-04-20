import java.io.*;
import java.net.*;
import java.util.ArrayList;

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
                int received=0;
                String specialRequest="";
                  
                try{//TODO: rewrite the entire network protocol to use strings instead of ints
                    specialRequest=in.readLine();
                    if(specialRequest.startsWith("X")){
                        out.write(players.get(Integer.parseInt(specialRequest.substring(1))).getX());
                    }else if(specialRequest.startsWith("Y")){
                        out.write(players.get(Integer.parseInt(specialRequest.substring(1))).getY());
                    }else if(specialRequest.startsWith("setX")){
                        players.get(Integer.parseInt(specialRequest.substring(4,specialRequest.indexOf(",")))).setX(Integer.parseInt(specialRequest.substring(specialRequest.indexOf(","))));
                        out.write(1);
                    }else if(specialRequest.startsWith("setY")){
                        players.get(Integer.parseInt(specialRequest.substring(4,specialRequest.indexOf(",")))).setY(Integer.parseInt(specialRequest.substring(specialRequest.indexOf(","))));
                        out.write(1);
                    }else if(specialRequest.startsWith("gX")){
                        out.write(garbage.get(Integer.parseInt(specialRequest.substring(1))).getX());
                    }else if(specialRequest.startsWith("gY")){
                        out.write(garbage.get(Integer.parseInt(specialRequest.substring(1))).getY());
                    }
                    out.close();
                    return;
                }catch(Exception e){
                    received = in.read();
                }
                
                int op=(received&0b11110000)>>4;
                int arg=received&0b00001111;
                if(debug)System.out.println(connection+":"+Client.readFriendly(received));//using Client.readFriendly() because it was already there and i didnt want to ctrlc ctrlv
                switch(op){
                    case 0b0000:out.write(getName(arg));break;//get name of player xxxx
                    case 0b0001:out.write(getNum(arg));break;//get number of players
                    case 0b0010:out.write(getPlayerX(arg));break;//get xpos of player xxxx
                    case 0b0011:out.write(getPlayerY(arg));break;//get ypos of player xxxx
                    case 0b0100:out.write(getGarbageX(arg));break;//get xpos of garbage xxxx
                    case 0b0101:out.write(getGarbageY(arg));break;//get ypos of garbage xxxx
                    case 0b0110:out.write(getScore(arg));break;//get score of player xxxx
                    case 0b0111:out.write(getValue(arg));break;//get value of garbage xxxx
                    case 0b1000:out.write(getId(arg));break;//get id of player xxxx
                    case 0b1001:out.write(setName(arg,received>>8));break;//get number of garbage
                    case 0b1010:out.write(leaveGame(arg));break;//makes player xxxx leave the game
                    case 0b1011:out.write(setXPos(arg,received>>8));break;//set xpos of player xxxx
                    case 0b1100:out.write(setYPos(arg,received>>8));break;//set ypos of player xxxx
                    case 0b1101:out.write(joinGame());break;//join game
                    case 0b1110:out.write(getHeading(arg));break;//get heading of player xxxx
                    case 0b1111:out.write(getDimension(arg));break;//get dimensions of the game board 0=w 1=h
                }
                out.close();
            }catch(Exception e){
                if(debug)e.printStackTrace();
            }
        }
        private int getNum(int which){
            if(which==0)return players.size();
            else if(which==1)return garbage.size();
            else return -1;
        }
        private int getPlayerX(int index){
            return players.get(index).getX();
        }
        private int getPlayerY(int index){
            return players.get(index).getY();
        }
        private int getGarbageX(int index){
            return garbage.get(index).getX();
        }
        private int getGarbageY(int index){
            return garbage.get(index).getY();
        }
        private int getScore(int index){
            return players.get(index).getScore();
        }
        private int getValue(int index){
            return garbage.get(index).getValue();
        }
        private int getId(int index){
            return players.get(index).getId();
        }
        private int leaveGame(int index){
            players.remove(index);
            return 1;
        }
        private int setXPos(int index,int newPos){
            players.get(index).setX(newPos);
            return 1;
        }
        private int setYPos(int index,int newPos){
            players.get(index).setY(newPos);
            return 1;
        }
        private int joinGame(){
            players.add(new Player());
            players.get(players.size()-1).setId(players.size()-1);
            return players.size()-1;
        }
        private int getHeading(int index){
            return players.get(index).getHeading();
        }
        private int getDimension(int which){
            switch(which){
                case 0:return BOARD_WIDTH;
                case 1:return BOARD_HEIGHT;
                default:return -1;
            }
        }
        private int getName(int index){
            int out=0;
            for(int i=0;i<2;i++){
                out+=(((int)(players.get(index).getName().charAt(i))-97)<<(4*i));//god this is crunch time
            }
            return out;
        }
        private int setName(int index,int newName){
            String out="";
            out+=(char)(97+(newName>>4)&0b1111);
            out+=(char)(97+newName&0b1111);
            players.get(index).setName(out);
            return 1;
        }
    }
}

/*
    0000:xxxx get name of player xxxx                   returns 16 bits
    0001:xxxx get number of 0000=players 0001=garbage   returns xxxxxxxx
    0010:xxxx get xpos of player xxxx                   returns xxxxxxxx 
    0011:xxxx get ypos of player xxxx                   returns xxxxxxxx
    0100:xxxx get xpos of garbage xxxx                  returns xxxxxxxx
    0101:xxxx get ypos of garbage xxxx                  returns xxxxxxxx
    0110:xxxx get score of player xxxx                  returns xxxxxxxx
    0111:xxxx get value of garbage xxxx                 returns xxxxxxxx
    1000:xxxx get id of player xxxx                     returns xxxxxxxx
    xxxxxxxx:1001:xxxx set name of player xxxx          returns xxxxxxxx
    1010:xxxx quit game of player xxxx                  returns 1
    xxxxxxxx:1011:xxxx set xpos of player xxxx          returns 1
    xxxxxxxx:1100:xxxx set ypos of player xxxx          returns 1
    1101:xxxx join game                                 returns xxxxxxxx
    1110:xxxx get heading of player xxxx                returns xxxxxxxx
    1111:xxxx get board dimensions 0000=w 0001=h        returns xxxxxxxx
*/