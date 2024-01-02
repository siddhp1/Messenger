package messenger.screens;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import messenger.helpers.*;

public class MainClient extends JFrame implements ActionListener, FocusListener, KeyListener
{
    // UI Elements
    JFrame frame;
    JLabel messageText, recipientName; 
    JTextArea messageBox, typeBox; 
    JButton send, refresh;
    JMenu file, settings, help;
    JMenuBar menuBar;
    JMenuItem openChatLog, saveChatLog, preferences, status, helpItem; 
    JPanel messageTextBar, recipientBar; 
    JScrollPane messageScroll, typeScroll, contactScroll; 
    JList<String> contactList; 
    private Map<String, ImageIcon> imageMap;

    // Networking Elements
    private Socket socket;
    private PrintWriter output; 
    private String username, selected;

    // Buffer
    private Buffer buffer;

    // Constants
    private final String prompt = "Type Message Here"; // User textbox prompt

    public MainClient(Socket socket, String username)
    {
        // Assigns global variables for information transfer to and from the server
        try
        {
            this.socket = socket; 
            this.username = username;
            this.selected = ""; 
            this.buffer = new Buffer(); 
            this.output = new PrintWriter(socket.getOutputStream(),true);
        }
        catch (Exception e) 
        {
            System.err.println("Main Client could not assign variables for server information transfer.");
        } 

        // Send request to get information added to the live client manager
        liveClientRequest();

        final String name = username;
        
        // GUI
        messageBox = new JTextArea();
        messageBox.setEditable(false);
        messageScroll = new JScrollPane(messageBox); 
        
        typeBox = new JTextArea(prompt);
        typeBox.addFocusListener(this);
        typeBox.addKeyListener(this);
        typeScroll = new JScrollPane(typeBox);
        
        send = new JButton("Send");
        send.addActionListener(this);
        send.setForeground(Color.WHITE);
        send.setBackground(Color.BLACK);

        Border line = BorderFactory.createLineBorder(Color.GRAY);

        messageTextBar = new JPanel(); 
        messageTextBar.setBackground(Color.WHITE); 
        messageTextBar.setBorder(line);

        recipientBar = new JPanel(); 
        recipientBar.setBackground(Color.WHITE); 
        recipientBar.setBorder(line);

        messageText = new JLabel("Messages");
        recipientName = new JLabel("Select a Recipient to Begin Chatting"); 

        add(messageText);
        add(recipientName);
        add(messageScroll);
        add(typeScroll); 
        add(send);
        add(messageTextBar);
        add(recipientBar); 

        messageText.setBounds(15, 10, 279, 30); 
        recipientName.setBounds(305, 10, 479, 30); 
        messageTextBar.setBounds(10, 10, 279, 30);
        recipientBar.setBounds(300, 10, 479, 30); 
        messageScroll.setBounds(300, 40, 480, 350);
        typeScroll.setBounds(300, 398, 370, 50);
        send.setBounds(675, 398, 104, 49); 

        // List Items
        contactList = new JList<String>();
        
        // MouseListener for the JList
        MouseListener mouseListener = new MouseAdapter() 
        {
            public void mouseClicked(MouseEvent e) 
            {
                // Checks if an item in the JList is double clicked on
                if (e.getClickCount() >= 2) 
                {
                    // Sets that item as the recipient and displays at the top of the message text area
                    selected = contactList.getSelectedValue(); 
                    recipientName.setText("Recipient: " + selected);
                    messageBox.setText("");
                    getFromBuffer(selected); 
                }
            }
        };

        contactList.addMouseListener(mouseListener);
        contactScroll = new JScrollPane(contactList);

        refresh = new JButton("Refresh List");
        refresh.addActionListener(this);
        refresh.setForeground(Color.WHITE);
        refresh.setBackground(Color.BLACK);

        add(contactScroll);
        updateContactList("");
        add(refresh);

        contactScroll.setBounds(10, 40, 280, 350);
        refresh.setBounds(10, 398, 280, 49);

        // Window listener
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            // Listens if the window is closed
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) 
            {
                // Sends a command to the server thread to remove the client from the client manager
                output.println("<<<KILLCLIENT>>>" + name);
                System.exit(0); // Closes the window
            }
        }
        );

        setPreferredSize(new Dimension(800, 495));
        setLayout(null);
        setTitle("Main Menu - " + username);
        setVisible(true);
        pack();
    }

    // Key Listener
    public void keyPressed(KeyEvent e) {
        // Runs send message method if enter key is pressed
        if (e.getKeyCode()==KeyEvent.VK_ENTER){
            Send();
        }
    }

    // Focus Listeners
    public void focusGained (FocusEvent fe)
    {
        // If the type box is clicked, the prompt disappears
        if (typeBox.getText().equals(prompt))
        {
            typeBox.setText("");
        }
    }
    public void focusLost (FocusEvent fe)
    {
        // If the type box is no longer in focus, the prompt reappears
        if (typeBox.getText().length() == 0)
        {
            typeBox.setText(prompt);
        }
    }

    // Action listener for the refresh and send buttons
    public void actionPerformed (ActionEvent e) 
    {       
        if (e.getSource()==refresh)
        {
            // Requests the username list from the server if refresh button is pressed
            usernameListRequest(); 
        }
        if (e.getSource()==send)
        {
            // Runs send message method if send button is pressed
            Send();
        }
        
    }

    // Send message method
    private void Send()
    {
        String textInBox = typeBox.getText().trim(); // Gets text from the type box
            
        // Checks if there is text in the box
        if (textInBox.length() > 0) {                
            
            // Checks if there is a recipient selected
            if (!selected.isBlank())
            {
                // Checks if the text is the prompt itself
                if (!textInBox.equals(prompt))
                {
                    // Sends message to the serverthread
                    output.println("```" + selected + "~~~" + username + ": " + textInBox);
                    // Updates the message box
                    updateTA(username + ": " + textInBox); 
                    // Clears the type box
                    typeBox.setText(null);
                }
            }
            // If there is no recipient selected, error prompt is shown
            else 
            {
                JOptionPane.showMessageDialog(null, "User Must Select a Recipient", "Message Failed to Send", JOptionPane.INFORMATION_MESSAGE, null);
            }
        }        
    }
    
    // Requests that the server thread adds this client to the client manager
    public void liveClientRequest()
    {
        output.println("<<<ADDTOMANAGER>>>" + username);
    }

    // Requests a username list from the server thread
    public void usernameListRequest()
    {
        output.println("<<<GETUSERNAMES>>>");
    }

    // Class for modifying the cell render of the JList
    public class ContactListRenderer extends DefaultListCellRenderer {
        // Creates a font 
        Font font = new Font("Segoe UI", Font.BOLD, 14);
    
        @Override
        public Component getListCellRendererComponent(
            JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) 
        {
    
            JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus); // Sets the label contents
            label.setIcon(imageMap.get((String) value)); // Sets the icon from the imagemap
            label.setHorizontalTextPosition(JLabel.RIGHT); // Sets the text position
            label.setFont(font); // Sets the font
            return label;
        }
    }

    // Creates image map for the different images for each list component
    private Map<String, ImageIcon> createImageMap(String[] list) {
        Map<String, ImageIcon> map = new HashMap<>();

        try {
            Random randomNumber = new Random(); 
            
            // Getting images from URL
            ImageIcon blackIcon = new ImageIcon(new URL("https://i.ibb.co/4mSvGkp/image.png"));
            ImageIcon redIcon = new ImageIcon(new URL("https://i.ibb.co/dJ3kJq5/image.png"));
            ImageIcon yellowIcon = new ImageIcon(new URL("https://i.ibb.co/c6JrCDk/image.png"));
            ImageIcon blueIcon = new ImageIcon(new URL("https://i.ibb.co/17vQ9hT/image.png"));
            ImageIcon greenIcon = new ImageIcon(new URL("https://i.ibb.co/Hn9c774/image.png"));
            ImageIcon groupIcon = new ImageIcon(new URL("https://i.ibb.co/r34xz9r/image.png"));
            ImageIcon[] soloIcons = new ImageIcon[] {blackIcon, redIcon, yellowIcon, greenIcon, blueIcon};  

            // Puts the group icon as the image for the global chat
            map.put(list[0], groupIcon); 

            // Randomizes the images for the other online clients 
            for (int i = 1; i < list.length; i++)
            {
                map.put(list[i], soloIcons[randomNumber.nextInt(5)]); 
            }
        }
        catch (Exception e) 
        {
            System.err.println("Image Map could not be created.");
        }
        return map; // Returns the map
    }


    // Updates the contact list using string of names sent by the server thread
    public void updateContactList(String names)
    {
        ArrayList<String> list = new ArrayList<String>(); 
        
        // If the string of names is empty, the contact list is empty
        if (names.equals(""))
        {  

        }
        else
        {
            // Add each of the names to the list if they are not the client
            String[] temp = names.split(","); 

            for (int i = 0; i < temp.length; i++)
            {
                if (temp[i].equals(username))
                {

                }
                else
                {
                    list.add(temp[i]);
                }
            }

        }
        
        // Add the global chat button to the list
        list.add(0, "Global Chat");

        // Convert the arraylist to an array
        String[] array = list.toArray(new String[list.size()]);
        imageMap = createImageMap(array); // Create the image map
        contactList.setListData(array); // Set the data to the list
        contactList.setCellRenderer(new ContactListRenderer()); // Use the custom renderer
    }

    // Check if an incoming message can be displayed or needs to be sent to the buffer
    public void checkForBuffer(String incomingMessage, String sender)
    {
        // If the message is from global chat, do not store it, and only display it if the client is in the global chat
        if (sender.equals("gc") && selected.equals("Global Chat"))
        {
            incomingMessage = incomingMessage.replace("(gc) ", "");
            updateTA(incomingMessage);
        }
        // If a direct message is from the selected user, display it instantly
        else if(selected.equals(sender))
        {
            updateTA(incomingMessage);
        }
        // Otherwise add the message to the buffer
        else 
        {
            buffer.addToBuffer(sender, incomingMessage);
        }
    }

    // Gets the messages from the selected user from the buffer
    public void getFromBuffer(String selected)
    {
        // Loads the messages to an arraylist from the buffer
        ArrayList<String> messages = buffer.getMessagesFromBuffer(selected);
        
        // Update the message box
        if (messages != null)
        {
            // Ignores the first index in the arraylist, as that is the name of the sender
            for (int i = 1; i < messages.size(); i++)
            {
                updateTA(messages.get(i));
            }
        }
    }

    // Updates the messagebox
    public void updateTA(String incomingMessage)
    {
        // Gets the time that the message is recieved
        LocalDateTime currentDateTime = LocalDateTime.now(); 
        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");

        // Processes the incoming message
        int index = incomingMessage.indexOf(":");
        String sender = incomingMessage.substring(0, index);
        incomingMessage = incomingMessage.replace(sender + ": ", "");
        
        // Append and update text area
        messageBox.append(sender + "@" + currentDateTime.format(format) + ": " + incomingMessage + "\n");
        //messageBox.append(incomingMessage + "\n");
        messageBox.update(messageBox.getGraphics());
    }

    // Functions are not used but are required to inherit the KeyListener
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}    
}