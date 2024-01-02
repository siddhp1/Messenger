package messenger.helpers;

import java.util.ArrayList;

public class Buffer {
    // 2 dimensional arraylist to store the sender and the messages from each sender
    public ArrayList<ArrayList<String>> buffer;
    
    public Buffer()
    {
        // Creates an empty arraylist
        buffer = new ArrayList<ArrayList<String>>(); 
    }

    // Adds a message to the buffer
    public void addToBuffer(String sender, String message)
    {
        // If the sender has already sent a message, append to end of messages list
        for (int i = 0; i < buffer.size(); i++)
        {
            if (sender.equals(buffer.get(i).get(0)))
            {
                // append the message to the end of the inside list
                buffer.get(i).add(message);
                return;
            }
        }

        // Otherwise, create a new Arraylist, add the message, and add the Arraylist to the outer list
        ArrayList<String> temp = new ArrayList<String>();
        temp.add(sender);
        temp.add(message);
        buffer.add(temp);
    }

    // Gets messages from the buffer
    public ArrayList<String> getMessagesFromBuffer(String sender)
    {
        // Searches for the arraylist of messages from the sender
        for (int i = 0; i < buffer.size(); i++) 
        {
            if (sender.equals(buffer.get(i).get(0)))
            {
                // Removes the messages from the buffer and returns to the client
                ArrayList<String> temp = buffer.get(i);
                buffer.remove(i);
                return(temp);
            }
        }
        // If there are no messages, return nothing 
        return(null); 
    }
}