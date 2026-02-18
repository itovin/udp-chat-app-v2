#UDP Chat Application (Console based)

#Description A very beginner simple UDP Chat System with a server that accepts commands, process the commands and routes message to the selected recipient.

#Feature
User Registration: App asks for a username once the client runs
User list: User can enter command "/list" to get a list of all active users
Select user to chat with: User can enter command "/w" followed by the desired other's username
Logout: User can enter command "/end" to safely end the session
Concurrent message listening

#Technologies
Java
UDP Networking (DatagramSocket / DragramPacket)

#How it works (How I run it)
1. Run the server (ChatAppv2.class) in the IDE or CMD
2. Run clients (User.class) from different CMD windows. Can run multiple. I tried 4 at most.
3. Once the client runs, user is asked to provide a username
4. User can start sending commands like "/list", "/w [username]", "/end"
5. If no user is selected. Server replies "No one can hears you"

To run clients from CMD.
1. Change directory to the classes folder of the project (D:\[user]\Java\Java - NetBeans\ChatAppv2\build\classes)
2. Enter "java chatappv2.User"

#Future improvements
1. Actual database for users
2. Chat history
3. Report a user
