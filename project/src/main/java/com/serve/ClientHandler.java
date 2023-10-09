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
            String messageContent = parts[3];
            
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
        try {
            File sourceFile = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
    
            ClientHandler recipientClient = null;
            for (ClientHandler client : connectedClients) {
                if (client.getClientName().equals(recipientName)) {
                    recipientClient = client;
                    break;
                }
            }
    
            if (recipientClient != null) {
                PrintWriter recipientWriter = new PrintWriter(recipientClient.clientSocket.getOutputStream(), true);
    
                // Envia comando para indicar o início do envio do arquivo
                String startFileCommand = "/start_file " + clientName + " " + filePath;
                recipientWriter.println(startFileCommand);
    
                byte[] buffer = new byte[8192];
                int bytesRead;
    
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    recipientClient.clientSocket.getOutputStream().write(buffer, 0, bytesRead);
                }
    
                // Envia comando para indicar o fim do envio do arquivo
                String endFileCommand = "/fim_envio";
                recipientWriter.println(endFileCommand);
    
                System.out.println("Arquivo " + filePath + " enviado com sucesso para " + recipientName);
    
                fileInputStream.close();
            } else {
                System.out.println("Usuário não encontrado: " + recipientName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
