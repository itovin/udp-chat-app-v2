#UDP Chat Application (Console based)

#Description A very beginner simple UDP Chat System with a server that accepts commands, process the commands and routes message to the selected recipient.

#Feature
-User Registration: actual registration with username and password saved on the database
-User list: User can enter command "/list" to get a list of all active users
-Select user to chat with: User can enter command "/w" followed by the desired other's username
-Concurrent message listening
-History. User can use "/history [username]" command to see entire history of message with [username]
-Password hashing using BCrypt


#Technologies
Java
UDP Networking (DatagramSocket / DragramPacket)
Sqlite

#How it works (How I run it)
1. Run the server (ChatAppv2.class) in the IDE or CMD
2. Run clients (User.class) from different CMD windows. Can run multiple. I tried 4 at most.
3. Once the client runs, user can either "/register" or "/login" followed by the username.
4. If correct format of login or registered is entered, password will be asked
5. Server will process the registration and login.
6. Once logged in, user can start sending commands like "/list", "/w [username]", "/end"
7. If no user is selected. Server replies "No one can hears you"
8. History - user can enter "/history [username]" to get the history chat with the specified user
7. If no user is selected. Server replies "No one hears you"

To run clients from CMD.
1. Change directory to the classes folder of the project (Example: D:\[user]\Java\Java - NetBeans\ChatAppv2\build\classes)
2. Enter "java chatappv2.User"

#Future improvements
<<<<<<< HEAD
1. Fix bugs
2. Report a user
=======
1. Customized Exception handling
2. Logout
3. Unintentional disconnection handling
>>>>>>> c81bef6 (Added JBCrypt library for password hashing. Will try adding customized)
