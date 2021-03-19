public class Garbage{
    private int x,y,value;
    private double velX,velY;
    public Garbage(){
        x=y=0;
        value=1;
        velX=0;
        velY=0;
    }
    public Garbage(int x, int y, int value, double velX, double velY) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.velX = velX;
        this.velY = velY;
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
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }
    public double getVelX() {
        return velX;
    }
    public void setVelX(double velX) {
        this.velX = velX;
    }
    public double getVelY() {
        return velY;
    }
    public void setVelY(double velY) {
        this.velY = velY;
    }
}