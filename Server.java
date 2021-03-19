import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server{
    private final static boolean debug=true;
    private ServerSocket ss;
    private Socket s;

    private ArrayList<Player> players;
    private ArrayList<Garbage> garbage;

    public static void main(String[] args){
        new Server();
    }
    public Server(){
        players=new ArrayList<Player>();
        garbage=new ArrayList<Garbage>();
        System.out.println(getIpAddress());
        try{
            ss=new ServerSocket(8080);
            while(true){
                if(debug)System.out.println("waiting for connection:");
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

    private class HandleRequest extends Thread{
        Socket s;
        public HandleRequest(Socket s){
            this.s=s;
        }
        public void run(){
            try{
                String connection=s.getInetAddress().toString().substring(1);
                if(debug)System.out.println("connected to:"+connection);
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                int received = in.read();
                int op=(received&0b11110000)>>4;
                int arg=received&0b00001111;
                if(debug)System.out.println("received:"+op+":"+arg);
                switch(op){
                    case 0b0000:break;
                    case 0b0001:out.write(getNumPlayers());break;//get number of players
                    case 0b0010:out.write(getPlayerX(arg));break;//get xpos of player xxxx
                    case 0b0011:out.write(getPlayerY(arg));break;//get ypos of player xxxx
                    case 0b0100:out.write(getGarbageX(arg));break;//get xpos of garbage xxxx
                    case 0b0101:out.write(getGarbageY(arg));break;//get ypos of garbage xxxx
                    case 0b0110:out.write(getScore(arg));break;//get score of player xxxx
                    case 0b0111:out.write(getValue(arg));break;//get value of garbage xxxx
                    case 0b1000:out.write(getId(arg));break;//get id of player xxxx
                    case 0b1001:out.write(getNumGarbage());break;//get number of garbage
                    case 0b1010:out.write(leaveGame(arg));break;//makes player xxxx leave the game
                    case 0b1011:out.write(setXPos(arg,received>>8));break;//set xpos of player xxxx
                    case 0b1100:out.write(setYPos(arg,received>>8));break;//set ypos of player xxxx
                    case 0b1101:out.write(joinGame());break;//join game
                    case 0b1110:break;//
                    case 0b1111:break;//
                }

                out.close();
                if(debug)System.out.println("closing connection");
            }catch(Exception e){
                if(debug)e.printStackTrace();
            }
        }
        private int getNumPlayers(){
            return players.size();
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
        private int getNumGarbage(){
            return garbage.size();
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
            System.out.println(players.size());
            players.add(new Player());
            players.get(players.size()-1).setId(players.size()-1);
            System.out.println(players.size()-1);
            return players.size()-1;
        }
    }
}

/*
    0000:xxxx noop
    0001:xxxx get number of players                     returns xxxxxxxx
    0010:xxxx get xpos of player xxxx                   returns xxxxxxxx xxxxxxxx
    0011:xxxx get ypos of player xxxx                   returns xxxxxxxx xxxxxxxx
    0100:xxxx get xpos of garbage xxxx                  returns xxxxxxxx xxxxxxxx
    0101:xxxx get ypos of garbage xxxx                  returns xxxxxxxx xxxxxxxx
    0110:xxxx get score of player xxxx                  returns xxxxxxxx
    0111:xxxx get value of garbage xxxx                 returns xxxxxxxx
    1000:xxxx get id of player xxxx                     returns xxxxxxxx
    1001:xxxx get number of garbage                     returns xxxxxxxx
    1010:xxxx quit game of player xxxx                  returns 1
    xxxxxxxx:xxxxxxxx:1011:xxxx set xpos of player xxxx returns 1
    xxxxxxxx:xxxxxxxx:1100:xxxx set ypos of player xxxx returns 1
    1101:xxxx join game                                 returns xxxxxxxx
    1110:xxxx unused
    1111:xxxx unused
*/