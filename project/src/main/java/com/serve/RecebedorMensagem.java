package com.serve;

import java.io.FileOutputStream;
import java.util.Scanner;
import java.io.*;
public class RecebedorMensagem implements Runnable {
    private Scanner serverIn;
    private FileOutputStream fileOutputStream;
    private String currentFileName;

    public RecebedorMensagem(Scanner serverIn) {
        this.serverIn = serverIn;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (serverIn.hasNextLine()) {
                    String serverMessage = serverIn.nextLine();

                    if (serverMessage.startsWith("/start_file")) {
                        String[] parts = serverMessage.split(" ");
                        System.out.println("Recebendo arquivo: " + parts[2]);
                        currentFileName = parts[2].substring(parts[2].lastIndexOf('/') + 1);
                        fileOutputStream = new FileOutputStream(currentFileName);
                    } else if (serverMessage.equals("/fim_envio")) {
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                            System.out.println("Arquivo recebido com sucesso: " + currentFileName);
                            currentFileName = null;
                        }
                    } else {
                        if (fileOutputStream != null) {
                            byte[] data = serverMessage.getBytes();
                            fileOutputStream.write(data);
                        } else {
                            System.out.println(serverMessage);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
