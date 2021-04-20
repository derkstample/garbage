import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayerEntry implements ActionListener {

    private JFrame frame;
    private JLabel playerName;
    private JLabel IP_Info;
    private JTextField playerInput;
    private JTextField IP_Address;
    private JButton entryBtn;
    private JPanel panel;
    private String player_Name;
    private String IP;

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
        panel.add(entryBtn);

        // finishing off the frame
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("garbage game");
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
        System.out.println(player_Name + ", " + IP);
        frame.dispose();
        //TODO: make sure IP is valid
        //TODO: make sure name is nonempty, nonwhitespace, 6 characters max, lowercase a-z
        //TODO: name length limit is 6 characters (it just is dont question it)
        Game.main(new String[]{IP,player_Name});
    }
}