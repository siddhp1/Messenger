# Messenger

<p align="center"><img width="600" alt="Thumbnail Image of Messenger" src="https://raw.githubusercontent.com/siddhp1/Messenger/refs/heads/main/Messenger.png"></p>

A desktop GUI app for WAN messaging with direct messages and global chat.

Built with Java using Maven, Swing for the GUI, multithreading, and WebSockets.

## About

Users can send direct messages and participate in a global chat. Features include message caching, a list of online users, and notifications for new messages.

## Setup

### Server

1. **Download Server Files:**
   
   Go to the [Releases](https://github.com/siddhp1/Messenger/releases) page and download the `server-1.0.jar` file and `credentials.txt`.

2. **Place Server Jar and Credentials File:**
   
   Place the downloaded `server-1.0.jar` file in the same directory as `credentials.txt`.

3. **Port Forwarding:**
   
   This step can be skipped if the server is hosted on a local network (LAN). 
   
   Forward port `5000` to the server's local IP address. Use the following settings:
     ```
     External Port: 5000
     Internal Port: 5000
     Protocol: TCP
     ```

4. **Start the Server:**
   
   Run the server using the following command:
     ```bash
     java -jar server-1.0.jar
     ```
   Ensure the server is running and accessible externally.

### Client


1. **Download Client Files:**
   
   Go to the [Releases](https://github.com/siddhp1/Messenger/releases) page and download the `client-1.0.jar` file and `config.txt`.

2. **Place Client Jar and Config File:**
   
   Place the downloaded `client-1.0.jar` file in the same directory as `config.txt`.

3. **Configure IP:**
   
   Set the IP address inside the `config.txt` file to the public IPv4 address of the server. If the server is hosted on a local network (LAN), use the private IP address.

4. **Start the Client:**
   
   Run the client using the following command:
     ```bash
     java -jar client-1.0.jar
     ```

## Usage

To use the application, create an account and log in to the client. Clients are automatically connected to the global chat upon successful launch.

To send a direct message, click the refresh list button, select the user you want to message, type your message in the input box, and send it.

The application includes notifications and message caching for messages received when the recipient is not actively viewing the sender's direct message.

Enjoy messaging!

## License

This project is licensed under the MIT License.
