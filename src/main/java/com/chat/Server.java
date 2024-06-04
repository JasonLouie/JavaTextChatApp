package com.chat;

import java.net.*;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private Database database;
    private List<ClientThread> clients;

    public Server(int port) throws IOException, SQLException {
        serverSocket = new ServerSocket(port);
        database = new Database();
        clients = new ArrayList<>();
    }

    public static void main(String[] args) {
        int port = 8000; // Default port number
        try {
            Server server = new Server(port);
            System.out.println("Server started on port " + port);
            server.start();
        } catch (IOException | SQLException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }

    public void start() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                ClientThread client = new ClientThread(socket, database);
                clients.add(client);
                client.start();
            } catch (IOException e) {
                System.out.println("Error accepting client connection: " + e.getMessage());
            }
        }
    }

    private class ClientThread extends Thread {
        private Socket socket;
        private Database database;

        public ClientThread(Socket socket, Database database) {
            this.socket = socket;
            this.database = database;
        }

        public void run() {
            try (ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {
                while (true) {
                    String command = input.readUTF();
                    System.out.println("New command");
                    switch (command) {
                        case "login":
                            try{
                                System.out.println("Login attempted");
                                login(input, output);
                            } catch (IOException | SQLException e) {
                                System.out.println("Login error");
                            }

                            break;
                        case "register":
                            try{
                                System.out.println("Registeration attempted");
                                register(input, output);
                            } catch (IOException | SQLException e){
                                System.out.println("Registration error");
                            }
                            break;
                        case "get_friends":
                            getFriends(input, output);
                            break;
                    }
                }
            } catch (IOException | SQLException e) {
                System.out.println("Error handling client connection: " + e.getMessage());
            }
        }

        private void login(ObjectInputStream input, ObjectOutputStream output) throws IOException, SQLException {
            String username = input.readUTF();
            String password = input.readUTF();
            if (database.verifyUserPassword(username, password)) {
                System.out.println("Login success");
                output.writeUTF("success");
                output.flush();
            } else {
                System.out.println("Failed login");
                output.writeUTF("Incorrect username or password");
                output.flush();
            }
        }

        private void register(ObjectInputStream input, ObjectOutputStream output) throws IOException, SQLException {
            String username = input.readUTF();
            String nickname = input.readUTF();
            String email = input.readUTF();
            String password = input.readUTF();
            String fileName = input.readUTF();
            long fileSize = input.readLong();
            File profilePicture = new File("profile_pictures/" + fileName);
            try (FileOutputStream fileOutputStream = new FileOutputStream(profilePicture)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while (fileSize > 0 && (bytesRead = input.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                    fileSize -= bytesRead;
                }
            }
            if (database.checkUserExists(username, email)) {
                System.out.println("User exists");
                output.writeUTF("user_already_exists");
                output.flush();
            } else {
                database.registerUser(username, nickname, email, password, profilePicture.getAbsolutePath());
                System.out.println("User created");
                output.writeUTF("success");
                output.flush();
            }
        }

        private void getFriends(ObjectInputStream input, ObjectOutputStream output) throws IOException, SQLException {
            int userId = input.readInt();
            String friends = database.getFriends(userId);
            output.writeUTF(friends);
        }
    }
}