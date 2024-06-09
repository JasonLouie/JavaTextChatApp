package com.chat.models;

import java.io.*;

public class RegisterMessage extends Message {
    private String username;
    private String nickname;
    private String email;
    private String password;
    private File profilePicture;

    public RegisterMessage(String username, String nickname, String email, String password, File profilePicture) {
        super(TYPE_REGISTER);
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.profilePicture = profilePicture;
    }

    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeByte(type);
        out.writeInt(username.length() + nickname.length() + email.length() + password.length() + (int) profilePicture.length() + 20); // 20 is the size of the file name
        out.writeUTF(username);
        out.writeUTF(nickname);
        out.writeUTF(email);
        out.writeUTF(password);
        out.writeUTF(profilePicture.getName());
        try (FileInputStream fileInputStream = new FileInputStream(profilePicture)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    public static RegisterMessage readFrom(DataInputStream in, int length) throws IOException {
        String username = in.readUTF();
        String nickname = in.readUTF();
        String email = in.readUTF();
        String password = in.readUTF();
        String fileName = in.readUTF();
        File profilePicture = new File("received_pfps", fileName);
        try (FileOutputStream fileOutputStream = new FileOutputStream(profilePicture)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while (length > 0 && (bytesRead = in.read(buffer, 0, Math.min(buffer.length, length))) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
                length -= bytesRead;
            }
        }
        return new RegisterMessage(username, nickname, email, password, profilePicture);
    }
}