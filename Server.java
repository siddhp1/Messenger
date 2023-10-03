import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {

    // Constructor 
    public Server(int idCounter, ServerSocket serversocket, ArrayList<ServerThread> threadList)
    {
        // Lists for credentials   
        ArrayList<String> firstNameList = new ArrayList<>();
        ArrayList<String> lastNameList = new ArrayList<>();
        ArrayList<String> emaiList = new ArrayList<>();
        ArrayList<String> usernameList = new ArrayList<>();
        ArrayList<String> passwordList = new ArrayList<>();

        // Reads from the credentials file and adds information to lists
        try 
        {
            File myObj = new File("credentials.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] tokens = data.split(",");
                firstNameList.add(tokens[0]);
                lastNameList.add(tokens[1]);
                emaiList.add(tokens[2]);
                usernameList.add(tokens[3]);
                passwordList.add(tokens[4]);
            }
            myReader.close();
        } 
        catch (FileNotFoundException e) 
        {
            System.err.println("Credentials File Not Found");
        }

        // Passes the lists through a merge sorting algorithm
        sortLists(usernameList, passwordList, emaiList, firstNameList, lastNameList, 0, usernameList.size() - 1);

        // Create a live client manager
        LiveClientManager liveClientManager = new LiveClientManager(); 

        // Opens the server socket to accept new clients
        try {
            while(true) {
                Socket socket = serversocket.accept();
                // Creates a new serverthread class for each client that connects
                ServerThread serverThread = new ServerThread(socket, threadList, idCounter, usernameList, passwordList, emaiList, firstNameList, lastNameList, liveClientManager);
                threadList.add(serverThread); 
                serverThread.start();
                idCounter++; // ID counter gets incremented to give each server thread and client pairing a unique id
            }
        } catch (Exception e) {
            System.err.println("Error when searching for clients");
        }
    }

    // Merge sort algorithm for sorting credential lists
    public void sortLists (ArrayList<String> usernameList, ArrayList<String>passwordList, ArrayList<String>emaiList, ArrayList<String>firstNameList, ArrayList<String>lastNameList, int start, int end)
    {
        int pivotIndex = start + (end - start) / 2; 
        String pivot = usernameList.get(pivotIndex);

        int i = start, j = end; 

        while (i <= j) {
            while (usernameList.get(i).compareTo(pivot) < 0) {
              i++;
            }
      
            while (usernameList.get(j).compareTo(pivot) > 0) {
              j--;
            }
      
            if (i <= j) {
              swap(usernameList, passwordList, emaiList, firstNameList, lastNameList, i, j);
              i++;
              j--;
            }
        }
      
          if (start < j) {
                sortLists(usernameList, passwordList, emaiList, firstNameList, lastNameList, start, j);
          }
          if (i < end) {
            sortLists(usernameList, passwordList, emaiList, firstNameList, lastNameList, i, end);
          }
    }

    // Sorting algorithm calls swap
    private void swap(ArrayList<String> usernameList, ArrayList<String>passwordList, ArrayList<String>emaiList, ArrayList<String>firstNameList, ArrayList<String>lastNameList, int i, int j)
    {
        String temp = usernameList.get(i);
        usernameList.set(i, usernameList.get(j));
        usernameList.set(j, temp);

        temp = passwordList.get(i);
        passwordList.set(i, passwordList.get(j));
        passwordList.set(j, temp);

        temp = emaiList.get(i);
        emaiList.set(i, emaiList.get(j));
        emaiList.set(j, temp);

        temp = firstNameList.get(i);
        firstNameList.set(i, firstNameList.get(j));
        firstNameList.set(j, temp);

        temp = lastNameList.get(i);
        lastNameList.set(i, lastNameList.get(j));
        lastNameList.set(j, temp);
    } 

    // Main method opens the serversocket and creats an arraylist of threads
    public static void main(String[] args) throws IOException 
    {
        ServerSocket serversocket = new ServerSocket(5000); 
        ArrayList<ServerThread> threadList = new ArrayList<>();
        int idCounter = 0; 

        new Server(idCounter, serversocket, threadList);    
    }
}