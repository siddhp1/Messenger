package messenger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import messenger.screens.*;

public class Client {
    private Socket socket; 
    private BufferedReader input;

    private LoginClient loginClient; 
    private MainClient mainClient; 
    private Boolean mainStarted; 

    // Constructor
    public Client(Socket socket) 
    {
        // Creates an instance of the LoginClient to allow the user to login
        LoginClient loginClient = new LoginClient(socket); 
        
        try 
        {
            this.socket = socket;
            this.mainStarted = false; 
            this.loginClient = loginClient;
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }   
        catch (Exception e) 
        {

        } 
        // Starts the thread to listen for messages from the serverthread 
        listen();
    } 

    // Listen for message in new thread
    public void listen()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run() 
            {
                try 
                {
                    // Infinite loop
                    while(true) 
                    {
                        String inputString = input.readLine();

                        // Here the inputString is proccessed to see if it is a command,
                        // (denoted by the <<<) or a message (>>>)
                        if (inputString.startsWith("<<<"))
                        {   
                            inputString = inputString.replace("<<<", "");
                            processCommands(inputString);
                        }
                        else
                        {
                            processMessages(inputString);
                        }
                    }
                } 
                catch (IOException e) 
                {
                    System.err.println("Client recieved invalid response from server.");
                } 
            }
        }).start();
    }

    // Processes commands sent by the server thread
    private void processCommands(String command)
    {  
        // Breaks down the command into the code (code) and other information (command) 
        String[] tokens = command.split(">>>");
        String code = tokens[0];
        if (tokens.length > 1) {command = tokens[1];}
        
        switch (code) 
        {
            case "LOGINSUCCESS": // If login is successfull
            {
                loginClient.close(); // Close loginclient
                mainClient = new MainClient(socket, loginClient.username); // Start main client
                mainStarted = true;                 
                break; 
            }
            case "USERALREADYONLINE": // If a user attempting to login is already online
            {
                loginClient.userAlreadyOnline(); 
                break; 
            }
            case "LOGINFAILED": // If a username or password is incorrect
            {
                loginClient.loginFailed();
                break; 
            }
            case "ACCOUNTCREATED": // If an account is successfully created
            {
                loginClient.closeSignUp();
                break; 
            }
            case "DUPLICATEUSERNAME": // If a sign up attemp contains a duplicate username
            {
                loginClient.duplicateUsername(); 
                break; 
            }
            case "USERNAMELIST": // If the server sends an updated username list
            {
                mainClient.updateContactList(command);
                break; 
            }
        }
    }

    // Processes messages sent by the server thread 
    private void processMessages(String message)
    {
        if (message.contains("(gc)")) // If the message is from the global chat
        {
            // Sends the message to the mainclient with the sender to display
            mainClient.checkForBuffer(message, "gc");
        }
        if (message.contains(":")) // If the message is a direct message
        {
            // Extracts the senders name from the message
            int index = message.indexOf(":");
            String sender = message.substring(0, index);

            // Sends the message to the mainclient with the sender to either display or store in buffer
            if (mainStarted) 
            {   
                mainClient.checkForBuffer(message, sender);
            }
        }
    }

    // Main
    public static void main(String[] args) throws IOException 
    {
        // Connects to the server socket and starts the client
        Socket socket = new Socket("localhost", 5000); 
        new Client(socket); 
    }   
}