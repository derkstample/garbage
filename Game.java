import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.geom.AffineTransform;
import java.awt.Graphics2D;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
public class Game extends JFrame implements KeyListener{
    private static final long serialVersionUID = -8908839269479863460L;
    private final boolean debug=true;
    private final double globalImgScale=3.0;
    private Graphics g;

    private int dispWidth;
    private int dispHeight;
    private double vel=0;
    private double friction=.96;//higher=slipperier

    private Player me;
    private Client client;
    private Camera camera;

    private boolean forwardPressed=false;
    private boolean leftPressed=false;
    private boolean rightPressed=false;
    private boolean downPressed=false;
    private boolean turboPressed=false;
    private boolean handBrakePressed=false;

    private final int BOARD_WIDTH=1920;
    private final int BOARD_HEIGHT=1080;
    private final int TRACK_LENGTH=50;
    private final int TURN_SPEED=2;
    private int[][] tireTracks;
    private int trackIndexer=0;

    private BufferedImage truck0;//facing up
    private BufferedImage truck1;//between up and sideways
    private BufferedImage truck2;//sideways
    private BufferedImage truck3;//between sideways and down
    private BufferedImage truck4;//down
    public static void main(String[] args){
        new Game();
    }
    public Game(){
        try{
            truck0=scale(ImageIO.read(new File("art\\truck0.png")),globalImgScale);
            truck1=scale(ImageIO.read(new File("art\\truck1.png")),globalImgScale);
            truck2=scale(ImageIO.read(new File("art\\truck2.png")),globalImgScale);
            truck3=scale(ImageIO.read(new File("art\\truck3.png")),globalImgScale);
            truck4=scale(ImageIO.read(new File("art\\truck4.png")),globalImgScale);
        }catch(Exception e){
            if(debug)e.printStackTrace();
        }
        GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs=ge.getScreenDevices();
        DisplayMode disp=gs[0].getDisplayMode();
        dispHeight=disp.getHeight();
        dispWidth=disp.getWidth();

        setTitle("this game is garbage");
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        //setUndecorated(true);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        createBufferStrategy(2);
        BufferStrategy strategy=getBufferStrategy();
        addKeyListener(this);
        repaint();//safety repaint

        me=new Player(dispWidth/2,dispHeight/2,0,0,5,128);
        client=new Client(Client.getIpAddress());//TODO: set the ip address of client properly (use a new frame??)
        int temp=client.joinGame();//TODO: check if server connection is lost on every request somehow?
        if(temp==-1){
            System.out.println("error: no server running");
            System.exit(0);//TODO: maybe don't crash the whole damn thing and just show a popup or something
        }else{
            me.setId(temp);
        }

        camera=new Camera();//TODO: initialize this properly

        tireTracks=new int[2][TRACK_LENGTH];//TODO: figure out how this is going to work with Camera
        for(int i=0;i<TRACK_LENGTH;i++){
            tireTracks[0][i]=me.getX();
            tireTracks[1][i]=me.getY();
        }
        
        while(true){
            do{
                try{
                    g=strategy.getDrawGraphics();
                    //drawBG(g);
                    //drawTracks(g);
                    //drawMe(g);
                    //TODO: clearScreen();
                    camera.drawView(g,client.getPlayers(),client.getGarbage());
                    
                    moveMe();
                    updateTracks();
                    processInputs();
                }finally{
                    g.dispose();
                    try { Thread.sleep(10); } catch (Exception e) {}
                }
                strategy.show();
            }while(strategy.contentsLost());
        }
    }

    public void moveMe(){
        int newX=me.getX()-(int)(approxCos(me.getHeading())*vel*me.getSpeed());
        int newY=me.getY()+(int)(approxSin(me.getHeading())*vel*me.getSpeed());
        if(newX>0&&newX<BOARD_WIDTH){//TODO: set this to check based on the camera's bounds
            if(client.setPlayerX(newX,me.getId())==1)
                me.setX(newX);
        }
        if(newY>0&&newY<BOARD_HEIGHT){//this too
            if(client.setPlayerY(newY,me.getId())==1)
                me.setY(newY);
        } 
        if(Math.abs(vel)>0)vel*=friction;
    }
    public void drawBG(Graphics g){
        g.setColor(Color.WHITE);
        g.fillRect(0,0,dispWidth,dispHeight);
    }
    public void drawMe(Graphics g){
        int x=me.getX();
        int y=me.getY();
        int score=me.getScore();
        int heading=me.getHeading();
        int size=(int)(globalImgScale*(score+Player.MINSIZE));
        if((heading+256)%256>240)
            g.drawImage(flip(truck2),x-size/2,y-size/2,null);
        else if((heading+256)%256>208)
            g.drawImage(flip(truck1),x-size/2,y-size/2,null);
        else if((heading+256)%256>176)
            g.drawImage(truck0,x-size/2,y-size/2,null);
        else if((heading+256)%256>144)
            g.drawImage(truck1,x-size/2,y-size/2,null);
        else if((heading+256)%256>112)
            g.drawImage(truck2,x-size/2,y-size/2,null);
        else if((heading+256)%256>80)
            g.drawImage(truck3,x-size/2,y-size/2,null);
        else if((heading+256)%256>48)
            g.drawImage(truck4,x-size/2,y-size/2,null);
        else if((heading+256)%256>16) 
           g.drawImage(flip(truck3),x-size/2,y-size/2,null);
        else
            g.drawImage(flip(truck2),x-size/2,y-size/2,null);
    }
    public void drawTracks(Graphics g){
        g.setColor(Color.DARK_GRAY);
        for(int i=0;i<TRACK_LENGTH-1;i++){
            g.drawLine(tireTracks[0][(trackIndexer+i)%TRACK_LENGTH],tireTracks[1][(trackIndexer+i)%TRACK_LENGTH],tireTracks[0][((trackIndexer+i)+1)%TRACK_LENGTH],tireTracks[1][((trackIndexer+i)+1)%TRACK_LENGTH]);
        }
    }
    public void updateTracks(){
        tireTracks[0][trackIndexer]=me.getX();
        tireTracks[1][trackIndexer]=me.getY();
        trackIndexer=(trackIndexer+1)%TRACK_LENGTH;
    }
    public void processInputs(){
        int mult=1;
        if(turboPressed)mult=2;
        if(handBrakePressed)mult=0;
        if(forwardPressed)vel+=(.1*mult);
        if(leftPressed)me.shiftHeading((int)(TURN_SPEED*(2.0/(mult+1))));
        if(rightPressed)vel-=(.1*mult);
        if(downPressed)me.shiftHeading((int)(-TURN_SPEED*(2.0/(mult+1))));
    }

    public double approxCos(int deg){//only works from 0-255
        if(deg>191)return (-1.0/4000)*(deg-255)*(deg-255)+1;
        if(deg>63)return (1.0/4000)*(deg-127.5)*(deg-127.5)-1;
        return (-1.0/4000)*deg*deg+1;
    }
    public double approxSin(int deg){//only works from 0-255
        if(deg>127)return (1.0/4000)*(deg-191.25)*(deg-191.25)-1;
        return (-1.0/4000)*(deg-63.75)*(deg-63.75)+1;
    }
    public BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {

        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, this);
        g2d.dispose();

        return rotated;
    }
    public BufferedImage scale(BufferedImage sprite,double scale){
        BufferedImage img = new BufferedImage((int)(sprite.getWidth()*scale),(int)(sprite.getHeight()*scale),BufferedImage.TYPE_INT_ARGB);
        for(int xx = 0;xx<(int)(sprite.getWidth()*scale);xx++){
            for(int yy = 0;yy <(int)(sprite.getHeight()*scale);yy++){
                img.setRGB(xx, yy, sprite.getRGB((int)(xx/scale), (int)(yy/scale)));
            }
        }
        return img;
    }
    public BufferedImage flip(BufferedImage sprite){
        BufferedImage img = new BufferedImage(sprite.getWidth(),sprite.getHeight(),BufferedImage.TYPE_INT_ARGB);
        for(int xx = sprite.getWidth()-1;xx>0;xx--){
            for(int yy = 0;yy < sprite.getHeight();yy++){
                img.setRGB(sprite.getWidth()-xx, yy, sprite.getRGB(xx, yy));
            }
        }
        return img;
    }
    @Override
    public void keyTyped(KeyEvent e) {
    }
    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()){//nvm you can totally use switches here
            case KeyEvent.VK_ESCAPE:System.exit(0);
            case KeyEvent.VK_W:forwardPressed=true;break;
            case KeyEvent.VK_A:leftPressed=true;break;
            case KeyEvent.VK_S:rightPressed=true;break;
            case KeyEvent.VK_D:downPressed=true;break;
            case KeyEvent.VK_SPACE:handBrakePressed=true;break;
            case KeyEvent.VK_SHIFT:turboPressed=true;break;
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()){
            case KeyEvent.VK_ESCAPE:System.exit(0);
            case KeyEvent.VK_W:forwardPressed=false;break;
            case KeyEvent.VK_A:leftPressed=false;break;
            case KeyEvent.VK_S:rightPressed=false;break;
            case KeyEvent.VK_D:downPressed=false;break;
            case KeyEvent.VK_SPACE:handBrakePressed=false;break;
            case KeyEvent.VK_SHIFT:turboPressed=false;break;
        }
    }
}