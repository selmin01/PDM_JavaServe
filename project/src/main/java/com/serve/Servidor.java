package com.serve;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import com.client.ClientHandler;

public class Servidor {

    private static AtomicInteger clientCounter = new AtomicInteger(0);
    private static ArrayList<ClientHandler> connectedClients = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(12345);
        System.out.println("Servidor iniciado");

        while (true) {
            Socket clientSocket = server.accept();
            System.out.println("Nova conexão estabelecida");

            // Obtém o endereço IP do cliente
            InetAddress clientAddress = clientSocket.getInetAddress();

            // Obtém a data e hora da conexão
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date connectionTime = new Date();

            // Obtém o ID do cliente
            int clientId = clientCounter.incrementAndGet();

            // Cria uma entrada de log
            String logEntry = String.format("[%s] Cliente #%d conectado: %s (%s)",
                    dateFormat.format(connectionTime), clientId, clientAddress.getHostAddress(), clientAddress.getHostName());

            // Registra o log em um arquivo
            registrarLog(logEntry);

            // Crie uma nova thread para lidar com o cliente
            ClientHandler clientHandler = new ClientHandler(clientSocket, clientId);
            connectedClients.add(clientHandler);
            Thread clientThread = new Thread(clientHandler);
            clientThread.start();
        }
    }

    private static void registrarLog(String logEntry) {
        try (FileWriter fileWriter = new FileWriter("log.txt", true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter out = new PrintWriter(bufferedWriter)) {
            out.println(logEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void listarClientes(PrintWriter writer) {
        writer.println("Clientes conectados:");
        for (ClientHandler clientHandler : connectedClients) {
            writer.println("Cliente #" + clientHandler.getClientId());
        }
    }

    public static synchronized void removeClient(PrintWriter writer) {
        writer.println("Clientes conectados:");
        for (ClientHandler clientHandler : connectedClients) {
            writer.println("Cliente #" + clientHandler.getClientId());
        }
    }

    // public static void removeClient(int clientId) {
    //     ClientHandler thisClientHandler;
    //     for (ClientHandler clientHandler : connectedClients) {
    //         if(clientHandler.getClientId() == clientId){
    //             thisClientHandler = clientHandler;
    //         }
    //     }
    //     connectedClients.remove(thisClientHandler);
    // }
}
