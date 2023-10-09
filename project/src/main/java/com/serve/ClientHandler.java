package com.serve;

import java.io.*;
import java.net.Socket;
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
                    listarClientes(writer);
                } else if (message.startsWith("/send ")) {
                    handleSendCommand(message);
                } else {
                    notValidCommandError(message);
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

    private void handleSendCommand(String message) {
        String[] parts = message.split(" ", 4);
        if (parts.length >= 3 && "/send".equals(parts[0])) {
            String recipientName = parts[2];
            String messageType = parts[1];
            String messageContent = message.substring(message.indexOf(parts[2]) + parts[2].length() + 1);
            
            System.out.println("messageType: " + messageType);
            if ("message".equals(messageType)) {
                sendMessage(recipientName, messageContent);
            } else if ("file".equals(messageType)) {
                sendFile(recipientName, messageContent);
            } else {
                invalidSendFormat();
            }
        } else {
            invalidSendFormat();
        }
    }
    
    private void sendMessage(String recipientName, String message) {
        String formattedMessage = clientName + " escreveu: " + message;
        sendDirectMessage(recipientName, formattedMessage);
    }
    
    private void sendFile(String recipientName, String filePath) {
        // Add logic to send the file to the specified recipient here
        // You can use the sendFile method from the previous response
        // and send the file to the recipient using recipientName
    }
    

    private void sendDirectMessage(String recipientName, String message) {
        for (ClientHandler clientHandler : connectedClients) {
            if (clientHandler.getClientName().equals(recipientName)) {
                try {
                    PrintStream writer = new PrintStream(clientHandler.clientSocket.getOutputStream());
                    writer.println(message);
                    return; // Envio concluído
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        notValidUserError();
    }

    private void invalidSendFormat() {
        for (ClientHandler clientHandler : connectedClients) {
            if (clientHandler == this) {
                try {
                    PrintStream writer = new PrintStream(clientHandler.clientSocket.getOutputStream());
                    writer.println("Formato inválido para mensagem direta. Use '/send [message || file] [nome] [mensagem]'.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void notValidUserError() {
        for (ClientHandler clientHandler : connectedClients) {
            if (clientHandler == this) {
                try {
                    PrintStream writer = new PrintStream(clientHandler.clientSocket.getOutputStream());
                    writer.println("Usuário não encontrado: utilize /users para listar os usuários conectados");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void listarClientes(PrintWriter writer) {
        writer.println("Usuários conectados:");
        for (ClientHandler clientHandler : connectedClients) {
            writer.println("Usuário: " + clientHandler.getClientName());
        }
    }

    private void notValidCommandError(String message) {
        for (ClientHandler clientHandler : connectedClients) {
            if (clientHandler == this) {
                try {
                    PrintStream writer = new PrintStream(clientHandler.clientSocket.getOutputStream());
                    writer.println("Comando inválido: utilize /send /users /imagem /sair");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void receberImagem() {
        // Implemente a lógica de recebimento de imagem aqui
        // ...
    }
}
