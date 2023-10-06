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

        while (true) {
            System.out.print("Digite uma mensagem para o servidor: ");
            String message = scanner.nextLine();
            serverOut.println(message);
        }
    }
}
