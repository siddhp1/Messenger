import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginClient extends JFrame implements ActionListener
{
    // UI Elements
    JFrame frame; 
    public String username; 
    String password; 
    JLabel usernameTitle, passwordTitle, signUpTitle; 
    JTextField usernameField;
    JPasswordField passwordField; 
    JButton enter, signUp; 

    // Networking Elements
    private Socket socket; 
    private BufferedReader input;
    private PrintWriter output; 

    // SignUpClient (only created if needed)
    private SignUpClient signUpClient; 

    public LoginClient(Socket socket)
    {
        // Assigns global variables for information transfer to and from the server
        try 
        {
            this.socket = socket;
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream(),true);
        }   
        catch (Exception e) 
        {
            System.err.println("Login Client could not assign variables for server information transfer.");
        } 
        
        // GUI
        usernameField = new JTextField(); 
        usernameTitle = new JLabel("Username");
        
        passwordField = new JPasswordField();
        passwordTitle = new JLabel("Password");
    
        enter = new JButton("Login"); 
        enter.setForeground(Color.WHITE);
        enter.setBackground(Color.BLACK);
        enter.addActionListener(this);

        signUp = new JButton("Click Here");
        signUp.setForeground(Color.WHITE);
        signUp.setBackground(Color.LIGHT_GRAY);
        signUp.addActionListener(this);

        signUpTitle = new JLabel("Don't have an account?");

        setPreferredSize(new Dimension(400, 240));
        setLayout(null);

        add(usernameField); 
        add(passwordField);
        add(passwordTitle);
        add(usernameTitle); 
        add(enter);
        add(signUpTitle);
        add(signUp);
        
        usernameField.setBounds (100, 27, 193, 28);
        passwordField.setBounds (100, 75, 193, 28);
        usernameTitle.setBounds (100, 8, 70, 20);
        passwordTitle.setBounds (100, 55, 70, 20);
        enter.setBounds(100, 110, 193, 25);
        signUpTitle.setBounds(100, 135, 193, 20);
        signUp.setBounds(100, 155, 193, 28);

        setTitle("Login");
        setVisible(true); 
        pack();
    }

    // Actionlistener for the login button and the sign up button
    public void actionPerformed (ActionEvent e) 
    {
        if (e.getSource() == enter)
        {
            // Gets information from the text fields
            username = usernameField.getText();
            password = String.valueOf(passwordField.getPassword());
    
            // Checks if they are not empty and sends to server thread
            if (!username.isEmpty() && !password.isEmpty())
            {
                output.println("<<<LOGINATTEMPT>>>" + username + "," + password);
            }
            // Otherwise calls error function
            else
            {
                loginFailed();
            }
        }
        if (e.getSource() == signUp)
        {
            // Launches sign up client if button is pressed
            signUpClient = new SignUpClient(socket); 
        }
    }
    
    public void loginFailed()
    {
        // Displays a login failure prompt
        JOptionPane.showMessageDialog(null, "Username or Password Incorrect", "Login Failed", JOptionPane.INFORMATION_MESSAGE, null);
    }

    public void userAlreadyOnline()
    {
        // Displays a user already online prompt
        JOptionPane.showMessageDialog(null, "User Already Online", "Login Failed", JOptionPane.INFORMATION_MESSAGE, null);
    }

    public void duplicateUsername()
    {
        // Displays a duplicate username prompt
        JOptionPane.showMessageDialog(null, "Username is Taken", "Sign Up Failed", JOptionPane.INFORMATION_MESSAGE, null);
    }

    // Closes the Sign Up Client
    public void closeSignUp()
    {
        signUpClient.close(); 
    }

    // Closes the Login Client
    public void close()
    {
        setVisible(false);
        dispose();
    }
}