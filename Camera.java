import java.awt.Graphics;
import java.util.ArrayList;
public class Camera{
    private int width,height,x,y;
    public Camera(){
        x=y=width=height=0;
    }
    public Camera(int width, int height, int x, int y) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
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
        //TODO: draw players and garbage onto g if the camera can see it
        //TODO: import spritesheets and figure out which sprite to draw based on heading+score
    }
}