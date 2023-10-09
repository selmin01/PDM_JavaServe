package com.serve;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 12345);
        System.out.println("Conexão estabelecida com o servidor");

        PrintStream serverOut = new PrintStream(socket.getOutputStream());
        Scanner scanner = new Scanner(System.in);
        Scanner serverIn = new Scanner(socket.getInputStream());

        System.out.print("Digite seu nome: ");
        String userName = scanner.nextLine();

        serverOut.println(userName);

        Thread messageReceiverThread = new Thread(new RecebedorMensagem(serverIn));
        messageReceiverThread.start();

        while (true) {
            System.out.print("");
            if (scanner.hasNextLine()) {
                String message = scanner.nextLine();
                serverOut.println(message);
            }
        }
    }
}
