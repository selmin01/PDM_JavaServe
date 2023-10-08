package com.serve;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class RegistradorDeLog {

    public static void registrarLog(String logEntry) {
        try (FileWriter fileWriter = new FileWriter("log.txt", true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
             PrintWriter out = new PrintWriter(bufferedWriter)) {
            out.println(logEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
