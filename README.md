# Messenger

WAN messenger application that can be hosted on an external server. Users can install the client and connect with the server to direct message other users or participate in global chat. Built with Java and packaged with Maven for a high school (grade 11) computer science final project. 

## Table of Contents

1. [Setup](#setup)
2. [Usage](#usage)
3. [License](#license)

## Setup

### Server Setup

1. **Clone Repository:** 
   
   Clone this repository to your server:
     ```bash
     git clone https://github.com/siddhp1/Messenger.git
     ```

2. **Place Server Jar and Credentials File:**
   
   Place the `server-1.0.jar` file in the same directory as `credentials.txt`.

3. **Port Forwarding:**
   
   Set up port forwarding for port `5000` on your router to the server's local IP address.
     This allows external connections to reach the messenger server:
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

### Client Setup

1. **Clone Repository:** 
   
   Clone this repository to your client:
     ```bash
     git clone https://github.com/siddhp1/Messenger.git
     ```

2. **Place Client Jar and Config File:**
   
   Place the `client-1.0.jar` file in the same directory as `config.txt`.

3. **Configure IP:**
   
   Set the IP address inside the config file to the public IPv4 address of the server. If the server is hosted on a local network (LAN) use the private IP address.

4. **Start the Client:**
   
   Run the client using the following command:
     ```bash
     java -jar client-1.0.jar
     ```

## Usage

To use the application, create an account and login to the client. Clients are automatically connected to global chat when they successfully launch.

To directly message another user, click the refresh button and then click on the user you would like to message. Then type in the box and your message will be sent. 
The application has notifications and caching for messages received when the recipient is not focused on the sender's direct message. 

Have fun messaging!

## License

This project is licensed under the MIT License.
