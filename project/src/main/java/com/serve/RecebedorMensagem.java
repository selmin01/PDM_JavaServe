package com.serve;

import java.util.Scanner;

public class RecebedorMensagem implements Runnable {
    private Scanner serverIn;

    public RecebedorMensagem(Scanner serverIn) {
        this.serverIn = serverIn;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (serverIn.hasNextLine()) {
                    String serverMessage = serverIn.nextLine();
                    System.out.println(serverMessage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
