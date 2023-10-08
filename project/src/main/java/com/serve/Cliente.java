package com.serve;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
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
            // Verifique se o cliente enviou o comando /sair
            if ("/sair".equals(message)) {
                break; // Encerra o loop e desconecta o cliente
            }else if ("/imagem".equals(message)) {

            // Envie um comando 

            // Envie a imagem para o servidor
            File imagem = new File("../../../imagem.jpg");
            FileInputStream fileInputStream = new FileInputStream(imagem);
            IOUtils.copy(fileInputStream, FileOutputStream);

            System.out.println("Imagem enviada com sucesso.");

            System.out.println("Imagem enviada com sucesso.");

            }else{
                serverOut.println(message);
            }
        }
    }
}
