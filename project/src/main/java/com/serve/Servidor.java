package com.serve;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Servidor {

    private static AtomicInteger clientCounter = new AtomicInteger(0);
    private static List<ClientHandler> connectedClients = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(12345);
        System.out.println("Servidor iniciado");

        while (true) {
            Socket clientSocket = server.accept();
            System.out.println("Nova conexão estabelecida");

            // Obter a data e hora da conexão
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date connectionTime = new Date();

            // Obter o ID do cliente
            int clientId = clientCounter.incrementAndGet();

            // Criar uma entrada de log
            String logEntry = String.format("[%s] Cliente #%d conectado: %s (%s)",
                    dateFormat.format(connectionTime), clientId, clientSocket.getInetAddress().getHostAddress(), clientSocket.getInetAddress().getHostName());

            // Registrar o log usando a classe RegistradorDeLog
            RegistradorDeLog.registrarLog(logEntry);

            // Criar uma nova thread para lidar com o cliente
            ClientHandler clientHandler = new ClientHandler(clientSocket, clientId, connectedClients);
            connectedClients.add(clientHandler);
            Thread clientThread = new Thread(clientHandler);
            clientThread.start();
        }
    }
}
