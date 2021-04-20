public class Player{
    public static final int MINSIZE=50;
    //TODO: add username
    private int x,y,score,id,heading;
    private double speed;
    private String name;
    public Player(){
        x=y=score=0;
        id=-1;
        speed=1;
        name="";
    }
    public Player(int x, int y, int score, int id, double speed,int heading,String name) {
        this.x = x;
        this.y = y;
        this.score = score;
        this.id = id;
        this.speed = speed;
        this.heading=heading;
        this.name=name;
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
    public void setName(String name){
        this.name=name;
    }
    public String getName(){
        return name;
    }
}