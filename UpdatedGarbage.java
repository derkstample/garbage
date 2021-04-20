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


public class Garbage{
    private double x,y;
    private int value;
    private double velX,velY;
    private boolean collected = false;
    private int width,height;
    private int exitPolicy = EXIT_POLICY_BOUNCE;
    private boolean useOvalForContains;
    public static final int EXIT_POLICY_BOUNCE = 0;
	public static final int EXIT_POLICY_STOP = 1;
	public static final int EXIT_POLICY_DIE = 2;
	public static final int EXIT_POLICY_WRAP = 3;
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
    public double getX() {
        return x;
    }
    public void onCollection() {
	}
    public void setX(int x) {
        this.x = x;
    }
    public double getY() {
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
    public void setLocation(double newX, double newY) {
		x = newX;
		y = newY;
	}
    public void setVelocity(double newvelX, double newvelY) {
		velX = newvelX;
		velY = newvelY;
	}
    public void drawGarabage(Graphics g) {
	    int xi = (int)x;
	    int yi = (int)y;
        g.fillRect(xi-1,yi-1,2,2);
	}
    public void randomVelocity(double minimumSpeed, double maximumSpeed) {
		if (minimumSpeed < 0)
			throw new IllegalArgumentException("Speed can't be negative");
		if (maximumSpeed < minimumSpeed)
			throw new IllegalArgumentException("Maximum speed can't be less than Minimum speed");
		double speed = minimumSpeed + (maximumSpeed - minimumSpeed)*Math.random();
		double angle = 2*Math.PI*Math.random();
		velX = speed * Math.cos(angle);
		velY = speed * Math.sin(angle);

    }
	
	public int getGarbageWidth() {
		return width;
	}
    public int getGarbageHeight() {
		return height;
	}
    public void setSpriteWidth(int width) {
		if (width < 0)
			throw new IllegalArgumentException("Width of a sprite can't be negative.");
		this.width = width;
	}
    public int getExitPolicy() {
		return exitPolicy;
	}
    public void setExitPolicy( int policyCode ) {
		if (policyCode < 0 || policyCode > 4)
			throw new IllegalArgumentException("Illegal exit policy code");
		exitPolicy = policyCode;
	}
    public void cleaned_trash() {
		collected = true;
		onCollection();//whenever trash is collected
	}
    public boolean isCollected() {
		return collected;
	}
    public boolean contains(double a, double b) {
		if (!useOvalForContains || width == 0 || height == 0) {
			return a >= x-width/2 && a <= x+width/2 && b > y-height/2 && b < y+height/2;
		}
		else {
		   return Math.pow( (a-x)/(width/2.0), 2) + Math.pow( (b-y)/(height/2.0), 2) <= 1;
		}
	}
    public void setUseOvalForContains(boolean useOvalForContains) { //use oval just for now before uploading png garbage
		this.useOvalForContains = useOvalForContains;
	}
    void moveToNextFramePosition() { // Package-private method updates the sprite's position in each frame.
		if (velX == 0 && velY == 0)
			return;
		x += velX;
		y += velY;
		double left =  x - width/2;
		double top =  y - height/2;
		double right =  x + width/2;
		double bottom =  y + height/2;
		switch (exitPolicy) {
		case EXIT_POLICY_BOUNCE:
			if (right > home.getWidth()) { //home is wherever we are calling the garbage
				velX = -Math.abs(velX);
				x = x - 2*(right - home.getWidth());
			}
			if (bottom > home.getHeight()) {
				velY = -Math.abs(velY);
				y = y - 2*(bottom - home.getHeight());
			}
			if (left < 0) {
				velX = Math.abs(velX);
				x = x - 2*left;
			}
			if (top < 0) {
				velY = Math.abs(velY);
				y = y - 2*top;
			}
			break;
		case EXIT_POLICY_STOP:
			if (right > home.getWidth()) {
				x = home.getWidth() - width/2;
				y -= velY;
			}
			if (bottom > home.getHeight()) {
				y = home.getHeight() - height/2;
				x -= velX;
			}
			if (left < 0) {
				x = width/2;
				y -= velY;
			}
			if (top < 0) {
				y = height/2;
				x -= velX;
			}
			break;
		case EXIT_POLICY_DIE:
			if (right <= 0 || bottom <= top || left > home.getWidth() || top > home.getHeight())
				cleaned_trash();
			break;
		case EXIT_POLICY_WRAP:
			if (right < 0)
				x = home.getWidth() + width/2;
			if (left > home.getWidth())
				x = -width/2;
			if (bottom < 0)
				y = home.getHeight() + height/2;
			if (top > home.getHeight())
				y = -height/2;
			break;
		}

    }
}