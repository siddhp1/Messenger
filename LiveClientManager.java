import java.util.ArrayList;

public class LiveClientManager {
    // Creates an Arraylist of type User to store information about clients that are connected
    public ArrayList<User> liveClients;

    public LiveClientManager()
    {
        // Creates an empty Arraylist
        liveClients = new ArrayList<User>();
    }

    // Adds a new client to the list
    public void addClient(String username, ServerThread thread)
    {
        // Creates a new user and adds to the arraylist
        liveClients.add(new User(username, thread)); 
    }

    // Removes the client from the arraylist
    public void removeClient(String username)
    {
        // Searches for the username within the arraylist and removes if found
        for (int i = 0; i < liveClients.size(); i++)
        {
            if (username.equals(liveClients.get(i).username))
            {
                liveClients.remove(i);
            }
        }
    } 

    // Returns the serverthread of a client given the username
    public ServerThread getThread(String recipient)
    {
        ServerThread thread = null; 

        // Searches through the usernames and gets the thread
        for (int i = 0; i < liveClients.size(); i++)
        {
            if (recipient.equals(liveClients.get(i).username))
            {
                thread = liveClients.get(i).thread; 
            }
        }
        
        return(thread); // Returns to serverthread
    }
    
    // Gets a list of all of the clients that are online
    public String[] getClients()
    {        
        // Iterates through the arraylist and adds all of the usernames to a array

        String[] clients = new String[liveClients.size()]; 
        for (int i = 0; i < liveClients.size(); i++)
        {
            clients[i] = liveClients.get(i).username;         
        }

        // Returns the array to serverthread
        return(clients); 
    }

    // Checks if a specfic client is online
    public boolean checkIfOnline(String username)
    {
        // Searches if username is in the arraylist
        for (int i = 0; i < liveClients.size(); i++)
        {
            if (username.equals(liveClients.get(i).username))
            {
                return(true); // Returns true if found
            }
        }
        return(false); // Returns false if not found
    }
}