<img width="1105" height="192" alt="image" src="https://github.com/user-attachments/assets/786ff570-e21e-4771-a720-208f8131d092" />#Console based UDP Chat
 -Simple Chat app with a server that can handle multiple clients.

##Feature
-Registration: If the user has no account, user will need to create one using "/register [username]" command.
Password will be asked after sending correct register command format.
-Login: User can login on their account using "/login [username]" command.
Password will be asked after sending the correct login command format.
-List Available Users: User can use "/list" command to list all online users available to chat with.
-DirectMessage Command: User can use "/w [recipient username]" command to select desired recipient from the available users.
-DirectMessage: Once the user has selected a recipient, all messages will be re-routed to the recipient unless message is a command
-History: User can use command "/history [recipient username]" to see past messages with [recipient username]
-Heartbeat: Server checks every 10 secs when users last send a message. If user is inactive for 5 minutes, user will be forced logout by the server
-Logout: User can use "/logout" command to logout. Logging out including forced logout will also terminate the Client. Logout will also remove the user from the active users, thus, is also removed from "/list" command
-Password hashing: Use BCrypt to hash password before saving on the database
-Server Side Messages: All messages are displayed in the server.

##How run it
1. Run the ChatAppV2.Java (Server)
2. Run the User.Java(Client) - can run multiple clients at the same time

##Future improvement:
1. password format validation


##Sample Output

Server starts:
<img width="1105" height="192" alt="image" src="https://github.com/user-attachments/assets/2ecd60b0-ba14-4bc1-8786-494186146d3f" />

Client starts:
<img width="1133" height="194" alt="image" src="https://github.com/user-attachments/assets/cc3f4c37-a2e9-4da1-9309-8bfb1e0d4317" />

After client using /register and /login command incorrectly. /register and /login must be followed by username:
<img width="1170" height="199" alt="image" src="https://github.com/user-attachments/assets/9f22966b-60b6-4a42-88ec-3d45608cad32" />
<img width="876" height="187" alt="image" src="https://github.com/user-attachments/assets/725211fd-4b3f-4083-82ef-15a93d474657" />

When user logs in an account that does not exist:
<img width="1261" height="200" alt="image" src="https://github.com/user-attachments/assets/3441aacd-fba1-4ec4-af40-4c5f43eb1c04" />

When user tries to login the account that is currently online:
<img width="944" height="203" alt="image" src="https://github.com/user-attachments/assets/366703fa-151a-44e7-84f5-20aff34a24e9" />


User registers an account successfully:
<img width="1246" height="187" alt="image" src="https://github.com/user-attachments/assets/4175c650-3fdc-4b8a-90fa-9a550b20a6ba" />

User logs in successfully: 
<img width="1303" height="198" alt="image" src="https://github.com/user-attachments/assets/8b781ce1-5791-462c-9595-efb708b867d9" />

User uses /list command(No other user online):
<img width="1276" height="188" alt="image" src="https://github.com/user-attachments/assets/b8543279-6050-424c-b869-e307bdcffcd5" />

User uses /list command(Other users are online) - Server sees 
<img width="1294" height="204" alt="image" src="https://github.com/user-attachments/assets/70bbd7e6-553a-433e-9355-53c7b7e2b0ea" />

User uses /w [recipient username] command but recipient is also himself:
<img width="1149" height="197" alt="image" src="https://github.com/user-attachments/assets/c8984a84-7b9c-4ed3-a041-fc30a4438164" />

User uses /w [recipient username] command but recipient is either offline or does not exist:
<img width="1226" height="208" alt="image" src="https://github.com/user-attachments/assets/8259e7b4-0070-45da-b737-8c48729773a2" />

User uses /w [recipient username] command with valid recipient:
<img width="1165" height="189" alt="image" src="https://github.com/user-attachments/assets/9e58a8d6-f8aa-4f6d-b9ee-5fb1a4381476" />

User sends a message (not a command):
<img width="1187" height="184" alt="image" src="https://github.com/user-attachments/assets/df362e81-9a50-4395-a39d-8280590310e3" />

User receives a message from a different user:
<img width="969" height="139" alt="image" src="https://github.com/user-attachments/assets/8b008b06-f3a3-41d6-93e7-0ad850c4cc04" />

User sends a direct message but recipient logged out already:
<img width="1177" height="201" alt="image" src="https://github.com/user-attachments/assets/221af718-08d2-4f6c-adcd-bb3ef9c6f748" />

User uses /history [recipient username] command:
<img width="1161" height="351" alt="image" src="https://github.com/user-attachments/assets/3341d8b5-44e1-49ed-9c54-64086db8c9c0" />

User gets logged out automatically by the server due to 5 mins inactivity:
<img width="765" height="209" alt="image" src="https://github.com/user-attachments/assets/a47997f5-5a71-45a8-a4d5-f77494c0ac0f" />

User gets logged out by the server automatically (Server side):
<img width="908" height="190" alt="image" src="https://github.com/user-attachments/assets/bf302d0a-097e-48d2-9850-12b4d7a62d13" />

User used /logout command:
<img width="619" height="194" alt="image" src="https://github.com/user-attachments/assets/52431026-202c-4988-a703-f58d50886114" />






