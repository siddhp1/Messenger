package messenger.clients;

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

public class SignUpClient extends JFrame implements ActionListener
{
    // UI Elements
    JFrame frame; 
    JLabel usernameTitle, passwordTitle, firstNameTitle, lastNameTitle, emailTitle; 
    JTextField usernameField, firstNameField, lastNameField, emailField;
    JPasswordField passwordField; 
    JButton enter; 

    // User Information Elements
    public static String username; 
    String password, firstName, lastName, email; 

    // Networking Elements
    private Socket socket; 
    private BufferedReader input;
    private PrintWriter output; 

    public SignUpClient(Socket socket)
    {
        // Assigns global variables for information transfer to and from the server
        try 
        {
            this.socket = socket;
            this.input = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream(),true);
        }   
        catch (Exception e) 
        {
            System.err.println("Sign Up Client could not assign variables for server information transfer.");
        } 
        
        // GUI
        firstNameField = new JTextField(); 
        firstNameTitle = new JLabel("First Name");

        lastNameField = new JTextField(); 
        lastNameTitle = new JLabel("Last Name");

        emailField = new JTextField(); 
        emailTitle = new JLabel("Email");

        usernameField = new JTextField(); 
        usernameTitle = new JLabel("Username");
        
        passwordField = new JPasswordField();
        passwordTitle = new JLabel("Password");
    
        enter = new JButton("Sign Up"); 
        enter.setForeground(Color.WHITE);
        enter.setBackground(Color.BLACK);
        enter.addActionListener(this);

        setPreferredSize(new Dimension(400, 330));
        setLayout(null);

        add(firstNameField);
        add(firstNameTitle); 
        add(lastNameField);
        add(lastNameTitle); 
        add(emailField);
        add(emailTitle); 
        add(usernameField); 
        add(passwordField);
        add(passwordTitle);
        add(usernameTitle); 
        add(enter);
        
        firstNameField.setBounds (100, 27, 193, 28);
        lastNameField.setBounds (100, 74, 193, 28);
        emailField.setBounds (100, 121, 193, 28);
        usernameField.setBounds (100, 168, 193, 28);
        passwordField.setBounds (100, 215, 193, 28);

        firstNameTitle.setBounds (100, 8, 70, 20);
        lastNameTitle.setBounds (100, 55, 70, 20);
        emailTitle.setBounds (100, 102, 70, 20);
        usernameTitle.setBounds (100, 149, 70, 20);
        passwordTitle.setBounds (100, 196, 70, 20);

        enter.setBounds(100, 250, 193, 25);

        setTitle("Sign Up");
        setVisible(true); 
        pack();
    }

    // Action listener for the sign up button
    public void actionPerformed (ActionEvent e) 
    {
        // Retrieves all of the information from the text fields
        firstName = firstNameField.getText();
        lastName = lastNameField.getText();
        email = emailField.getText();
        username = usernameField.getText();
        password = String.valueOf(passwordField.getPassword());

        // Checks if information is incomplete
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty())
        {
            incompleteInformation(); // Calls error function
        }
        else
        {
            // Sends information to the server if it is complete
            output.println("<<<SIGNUPATTEMPT>>>" + firstName + "," + lastName + "," + email + "," + username + "," + password);
        }
    }

    public void incompleteInformation()
    {
        // Displays an incomplete information prompt
        JOptionPane.showMessageDialog(null, "Please Provide Required Information", "Sign Up Failed", JOptionPane.INFORMATION_MESSAGE, null);
    }

    // Closes the sign up client
    public void close()
    {
        setVisible(false);
        dispose();
    }
}