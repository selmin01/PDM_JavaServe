package com.serve;

import java.io.*;
import java.net.*;

public class Servidor {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(12345);
        System.out.println("Servidor iniciado");

        while (true) {
            Socket clientSocket = server.accept();
            System.out.println("Nova conexão estabelecida");

            try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
            ) {
                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("Cliente digitou: " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
