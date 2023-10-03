// Class that stores information about each user
public class User {
    // Contains the username and the thread
    public String username;
    public ServerThread thread;
    
    public User(String username, ServerThread thread)
    {
        this.username = username;
        this.thread = thread; 
    }

    // Returns the thread
    public ServerThread getThread()
    {
        return(thread); 
    }
}