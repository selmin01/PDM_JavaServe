package com.serve;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private int clientId;
    private List<ClientHandler> connectedClients;
    private String clientName;

    public ClientHandler(Socket clientSocket, int clientId, List<ClientHandler> connectedClients) {
        this.clientSocket = clientSocket;
        this.clientId = clientId;
        this.connectedClients = connectedClients;
    }

    public int getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    @Override
    public void run() {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            clientName = reader.readLine();

            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println(clientName + " digitou: " + message);

                // Verifique se o cliente está enviando uma imagem
                if ("/imagem".equals(message)) {
                    receberImagem();
                }

                // Verifique se o cliente enviou o comando /sair
                if ("/sair".equals(message)) {
                    writer.println("Comando de saída recebido. Encerrando conexão.");
                    break; // Encerra o loop e desconecta o cliente
                }
                // Verifique se o cliente enviou o comando /users
                else if ("/users".equals(message)) {
                    Servidor.listarClientes(writer);
                } else {
                    System.out.println("Enviando mensagem para os clientes conectados...");
                    broadcastMessage(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                System.out.println(clientName + " desconectado.");
                // remove cliente
                connectedClients.remove(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastMessage(String message) {
        System.out.println("Dentro do broadcast message");
        System.out.println("Clientes conectados: " + connectedClients.size());
        for (ClientHandler clientHandler : connectedClients) {
            // manda mensagem pra todos exceto o próprio cliente
            if (clientHandler != this) {
                try {
                    PrintStream writer = new PrintStream(clientHandler.clientSocket.getOutputStream());
                    writer.println(clientName + " diz: " + message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void receberImagem() {
    }
}
