public class ScoreManager {
    
    private int playerscore=0;
    private int garbageamount=55;

    public void setScore(int playerscore)
    {
        this.playerscore=playerscore;
    }
    public void setAmount(int garbageamount)
    {
        this.garbageamount=garbageamount;
    }

    public int getScore()
    {
        return playerscore;
    }
    public int getAmount()
    {
        return garbageamount;
    }
    public void garbageCollision() {
        playerscore++;
        garbageamount--;
        
    }

}