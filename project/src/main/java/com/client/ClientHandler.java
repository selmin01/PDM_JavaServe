package com.client;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.serve.Servidor;

public class ClientHandler implements Runnable{
    private Socket clientSocket;
    private int clientId;

    public ClientHandler(Socket clientSocket, int clientId) {
        this.clientSocket = clientSocket;
        this.clientId = clientId;
    }

    public int getClientId() {
        return clientId;
    }

    @Override
    public void run() {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Cliente #" + clientId + " digitou: " + message);

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
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                System.out.println("Cliente #" + clientId + " desconectado.");
                // Remova o cliente da lista de clientes conectados
                // Servidor.removeClient(clientId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void receberImagem() {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            
            // Salve a imagem em um arquivo no servidor
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = "imagem_" + clientId + "_" + dateFormat.format(new Date()) + ".jpg";
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }
            
            fileOutputStream.close();
            System.out.println("Imagem recebida e salva como " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
