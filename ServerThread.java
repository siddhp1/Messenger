import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread extends Thread {
    
    // Networking Elements
    private Socket socket;
    private PrintWriter output;
    public int id; 
    private ArrayList<ServerThread> threadList;

    // Credentials 
    private ArrayList<String> usernameList, passwordList, emailList, firstNameList, lastNameList;
    
    // Client Manager
    private LiveClientManager liveClientManager;  

    // Assigns global variables for information transfer to and from the server
    public ServerThread(Socket socket, ArrayList<ServerThread> threads, int id, ArrayList<String> usernameList, 
                        ArrayList<String> passwordList, ArrayList<String>emailList, ArrayList<String>firstNameList, 
                        ArrayList<String>lastNameList, LiveClientManager liveClientManager) 
    {
        this.socket = socket;
        this.threadList = threads;
        this.id = id; 
        this.usernameList = usernameList; 
        this.passwordList = passwordList; 
        this.emailList = emailList; 
        this.firstNameList = firstNameList; 
        this.lastNameList = lastNameList;         
        this.liveClientManager = liveClientManager; 
    }

    // Separate thread receives information from clients and processes it
    // The information is either a command (for the server) or a message (intended to be send to another client)
    @Override
    public void run() {
        try {
            // Creates buffered reader and print writer to pass information to the server socket
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(),true);

            // Infinite loop to recieve information from the clients
            while(true) {
                String outputString = input.readLine();
                // Debugging 
                System.out.println("Server received " + outputString + " from: " + threadList.get(id)); 
                // Here the outputstring is proccessed to see if it is a command,
                // (denoted by the <<<) or a message (denoted by the ```)
                if (outputString.startsWith("<<<"))
                {    
                    outputString = outputString.replace("<<<", ""); 
                    processCommands(outputString);
                }
                else
                {
                    processMessages(outputString.replace("```", ""));
                }
            }
        } catch (Exception e) {
            System.err.println("Error occured receiving from client.");
        }
    }

    // Processes commands sent by the client
    public void processCommands(String command)
    {
        // Breaks down the command into the code (code) and other information (command) 
        String[] tokens = command.split(">>>");
        String code = tokens[0];
        if (tokens.length > 1) {command = tokens[1];}

        switch (code) 
        {
            case "LOGINATTEMPT": // If client attempts a login
            {
                authenticateClient(command);
                break;
            }
            case "SIGNUPATTEMPT": // If client attemps a sign up
            {
                try
                {
                    createAccount(command);
                }
                catch (IOException e)
                {
                    System.err.println("Error occured attempting to create account.");
                }
                break;
            }
            case "ADDTOMANAGER": // Add client to client manager once main menu opens
            {
                liveClientManager.addClient(command, threadList.get(id));
                break; 
            }
            case "GETUSERNAMES": // If client requests username list
            {
                try 
                {
                    sendUsernameList();
                }
                catch (IOException e)
                {
                    System.err.println("Could not send username list."); 
                }
            }
            case "KILLCLIENT": // If client exits program
            {
                liveClientManager.removeClient(command);
                break;
            }
        }
    }

    // Processes messages sent by the client 
    private void processMessages(String message)
    {
        // Breaks down the command into the recipient and the message
        String[] tokens = message.split("~~~");
        String recipient = tokens[0];
        if (tokens.length > 1) {message = tokens[1];}

        switch (recipient)
        {
            case "Global Chat": // If the recipient is the global chat
            {
                broadcast(message);
                break;
            }
            default: // If the recipient is anyone else
            {
                directMessage(message, recipient);
                break;
            }
        }
    }

    // Gets the username list from the client manager and sends it to the client
    private void sendUsernameList() throws IOException
    {     
        String temp = String.join(",", liveClientManager.getClients());
        returnToClient("<<<USERNAMELIST>>>" + temp); 
    }
    
    // Authenticates the client when a login is requested
    private void authenticateClient(String input) 
    {
        // Separates the username and password
        String[] tokens = input.split(","); 

        // Checks if the client is not already online
        if (!liveClientManager.checkIfOnline(tokens[0]))
        {
            // Searches for the username in the username list 
            int index = searchForInformation(usernameList, tokens[0]); 
            
            // Checks if the username was found
            // If so, compares the username and password to the inputted information 
            if (index >= 0 && tokens[1].equals(passwordList.get(index)) ||
            (usernameList.get(usernameList.size() - 1).equals(tokens[0]) && passwordList.get(passwordList.size() - 1).equals(tokens[1]))) 
            {
                returnToClient("<<<LOGINSUCCESS>>>"); // Successfull if username and password match
            }
            else
            {
                returnToClient("<<<LOGINFAILED>>>"); // Failed if usename and password do not match
            }
        }
        // If the client is already online
        else
        {
            returnToClient("<<<USERALREADYONLINE>>>");
        }
    }

    // Creates an account if the sign up client requests
    // Exception is caught in the switch block that calls the function
    private void createAccount(String input) throws IOException 
    {
        String toPrint = input; // Saves a copy of the information to print to the credentials file
        String[] tokens = input.split(","); // Extracts the information from the string

        // Checks if there is duplicate information
        if (searchForInformation(usernameList, tokens[3]) > -1)
        {
            returnToClient("<<<DUPLICATEUSERNAME>>>");
        }
        // Otherwise prints information to file and appends to the credentials lists
        else
        {
            FileWriter outFile = new FileWriter ("credentials.txt", true);
            PrintWriter fileOut = new PrintWriter (outFile);

            fileOut.println(toPrint);

            outFile.close();
            fileOut.close();

            firstNameList.add(tokens[0]);
            lastNameList.add(tokens[1]);
            emailList.add(tokens[2]);
            usernameList.add(tokens[3]);
            passwordList.add(tokens[4]);

            returnToClient("<<<ACCOUNTCREATED>>>");    
        }    
    }

    // Binary search algorithm that searches an arraylist for a target string
    private int searchForInformation(ArrayList<String> list, String target) {
        int low = 0;
        int high = list.size() - 1;
        int mid = (low + high)  / 2;

        while (low <= high) {
            mid = (low + high)  / 2;

            if (list.get(mid).compareTo(target) < 0) {
                low = mid + 1;
            } else if (list.get(mid).compareTo(target) > 0) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -1;
    }

    // Broadcasts message to all clients
    private void broadcast(String outputString)
    {
        // Iterates through the list of threads
        for (int i = 0; i < threadList.size(); i++)
        {
            if (i != id) // Checks that the message is not sent back to the client 
            {
                threadList.get(i).output.println("(gc) " + outputString);
            }
        }
    }

    // Sends message to a specific client
    private void directMessage(String outputString, String recipient)
    {
        // Gets a users thread from the client manager
        ServerThread thread = liveClientManager.getThread(recipient);
        thread.output.println(outputString); // Outputs to that thread
    }

    // Returns information to the client that originally sent it
    private void returnToClient(String outputString)
    {
        // Gets thread of original client
        ServerThread sT = threadList.get(id);
        sT.output.println(outputString); // Outputs to that thread
    }
}