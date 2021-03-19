//fuck this bullshit leave it to someone else





import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
public class IPInput extends JFrame implements KeyListener,ActionListener{
    private int dispWidth;
    private int dispHeight;

    private JButton exit;
    private JButton start;
    private JTextField input;

    private Graphics g;
    private Thread graphicRunner;
    public static void main(String[] args){
        new IPInput();
    }
    public IPInput(){
        GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs=ge.getScreenDevices();
        DisplayMode disp=gs[0].getDisplayMode();
        dispHeight=disp.getHeight();
        dispWidth=disp.getWidth();

        setTitle("this game is garbage");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        exit=new JButton("quit");
        exit.setBounds(0,dispHeight/3,dispWidth/3,dispHeight/3);
        //exit.setOpaque(false);
        //exit.setContentAreaFilled(false);
        //exit.setBorderPainted(false);
        exit.addActionListener(this);
        add(exit);
        start=new JButton("start");
        start.setBounds(dispWidth/3,dispHeight/3,dispWidth/3,dispHeight/3);
        start.addActionListener(this);
        add(start);
        createBufferStrategy(2);
        BufferStrategy strategy=getBufferStrategy();
        addKeyListener(this);
        repaint();

        graphicRunner=new Thread(new GraphicUpdater(strategy));
        graphicRunner.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {  
    }
    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println(e.getKeyCode());
        switch(e.getKeyCode()){
            case KeyEvent.VK_ESCAPE:System.exit(0);
            case KeyEvent.VK_W:break;
            case KeyEvent.VK_A:break;
            case KeyEvent.VK_S:break;
            case KeyEvent.VK_D:break;
        }
    }
    @Override
    public void keyReleased(KeyEvent e) { 
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(exit.getActionCommand())){
            System.exit(0);
        }else if(e.getActionCommand().equals(start.getActionCommand())){
            graphicRunner.stop();
            dispose();
            Game.main(null);
        }
    }

    private class GraphicUpdater extends Thread{
        BufferStrategy strat;
        public GraphicUpdater(BufferStrategy s){
            strat=s;
        }
        public void run(){
            while(true){
                do{
                    try{
                        g=strat.getDrawGraphics();
                        
                    }finally{
                        g.dispose();
                        try { Thread.sleep(10); } catch (Exception e) {}
                    }
                    strat.show();
                }while(strat.contentsLost());
            }
        }
    }
}