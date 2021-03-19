public class Player{
    public static final int MINSIZE=50;

    private int x,y,score,id,heading;
    private double speed;
    public Player(){
        x=y=score=0;
        id=-1;
        speed=1;
    }
    public Player(int x, int y, int score, int id, double speed,int heading) {
        this.x = x;
        this.y = y;
        this.score = score;
        this.id = id;
        this.speed = speed;
        this.heading=heading;
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
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public double getSpeed() {
        return speed;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    public int getHeading() {
        return heading;
    }
    public void setHeading(int heading) {
        this.heading = heading;
    }

    public void shiftHeading(int shift){
        heading=(heading+shift+256)%256;
    }
}