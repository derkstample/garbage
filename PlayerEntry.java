import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PlayerEntry implements ActionListener {

    private JFrame frame;
    private JLabel playerName;
    private JLabel IP_Info;
    private JLabel IP_Incorrect;
    private JTextField playerInput;
    private JTextField IP_Address;
    private JButton entryBtn;
    private JPanel panel;
    private String player_Name;
    private String IP;
    private InetAddress myIP;
    private String ip_string;

    public PlayerEntry() {

        // setting upt the frame
        frame = new JFrame();

        // Label for player
        playerName = new JLabel("Enter your name:");
        playerName.setForeground(Color.BLUE);
        playerName.setFont(new Font("Verdana", Font.BOLD, 25));

        // making the Enter button
        entryBtn = new JButton("Enter");
        entryBtn.setFont(new Font("Verdana", Font.BOLD, 20));
        entryBtn.addActionListener((ActionListener) this);

        // Text field for player name
        playerInput = new JTextField();
        playerInput.setFont(new Font("Verdana", Font.BOLD, 25));

        // Label for IP Address
        IP_Info = new JLabel("Enter your IP Address:");
        IP_Info.setForeground(Color.BLUE);
        IP_Info.setFont(new Font("Verdana", Font.BOLD, 18));

        // Label for incorrect IP
        IP_Incorrect = new JLabel("Invalid IP! try again");
        IP_Incorrect.setForeground(Color.BLUE);
        IP_Incorrect.setFont(new Font("Verdana", Font.BOLD, 18));

        // Text field for player's IP address
        IP_Address = new JTextField();
        IP_Address.setFont(new Font("Verdana", Font.BOLD, 25));

        // creating a panel for the labels, textfields and button
        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        panel.setLayout(new GridLayout(0, 1));
        panel.add(playerName);
        panel.add(playerInput);
        panel.add(IP_Info);
        panel.add(IP_Address);
        panel.add(IP_Incorrect);
        panel.add(entryBtn);

        IP_Incorrect.setVisible(false); // will only appear if IP is incorrect

        // finishing off the frame
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Player Entry");
        frame.setLocation(480, 200);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String args[]) throws Exception {
        new PlayerEntry();
    }

    // The events that happen when button is pressed
    @Override
    public void actionPerformed(ActionEvent e) {
        player_Name = playerInput.getText();
        IP = IP_Address.getText();
        try {
            myIP = InetAddress.getLocalHost();
        } catch (UnknownHostException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        ip_string = String.valueOf(myIP.getHostAddress());
        
        // comparing IP addresses
        if(IP.equals(ip_string)) {
            System.out.println(player_Name + ", " + IP);
            // entry window will close if IP is correct
            frame.dispose();
        }
        else {
            System.out.println(ip_string);
            IP_Incorrect.setVisible(true);
        }
        
        

    }
}