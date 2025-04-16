package ProgramaServidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ProtocoloServidor {
    public static void procesar(BufferedReader lector, PrintWriter escritor) throws IOException {
        String inputline;
        String outputLine;

        inputline =lector.readLine();
        System.out.println("Entrada a procesar: "+inputline);
        outputLine =inputline;

        escritor.println(outputLine);
        System.out.println("Salida procesada: "+outputLine);
    }
}
