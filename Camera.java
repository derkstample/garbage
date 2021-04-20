import java.awt.Graphics;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.image.BufferedImage;
public class Camera{
    private int width,height,x,y;
    private BufferedImage truck;
    private BufferedImage trash;
    private final int SPRITE_WIDTH=32;
    private final int SPRITE_HEIGHT=32;
    public Camera(){
        x=y=width=height=0;
    }
    public Camera(int width, int height, int x, int y,BufferedImage truck,BufferedImage trash) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.truck=truck;
        this.trash=trash;
    }
    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }

    public void drawView(Graphics g,ArrayList<Player> players,ArrayList<Garbage> garbage){
        g.setColor(Color.BLACK);
        for(Player p:players){
            g.drawString(p.getName(),500,500);
            System.out.println(p.getX()+","+p.getY());
                    System.out.println(x+" , "+y);
                    System.out.println(width+"   ,   "+height);
            if(p.getX()>x&&p.getX()<x+width){
                if(p.getY()>y&&p.getY()<y+height){
                    g.fillOval(p.getX()-x,p.getY()-y,50,50);
                    //g.drawImage(getTruck(p.getHeading(),p.getScore()),p.getX()-x,p.getY()-y,null);
                }
            }
        }
        g.setColor(Color.BLUE);
        for(Garbage gar:garbage){
            if(gar.getX()>x&&gar.getX()<x+width){
                if(gar.getY()>y&&gar.getY()<y+height){
                    g.fillOval(gar.getX(), gar.getY(), 10, 10);
                }
            }
        }
    }
    public BufferedImage getTruck(int heading,int score){
        int dir=0;//TODO: turn the 0-255 heading value into 0-5 based on direction
        int fullness=0;//TODO: turn the score value into 0-4
        return truck.getSubimage(fullness*SPRITE_WIDTH,dir*SPRITE_HEIGHT,SPRITE_WIDTH,SPRITE_HEIGHT);
    }
    //public BufferedImage getTrash(int value){
        //TODO:this
    //}
}