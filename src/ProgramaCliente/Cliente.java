package ProgramaCliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente  {
    private static final int PUERTO=3400;
    private static final String HOST="localhost";

    private static final int NUM_CONSULTAS = 32;
    public static void main(String[] args) throws IOException {
        Socket socket=null;
        PrintWriter escritor = null;
        BufferedReader lector = null;
        System.out.println("Cliente..");
        try {
            socket = new Socket(HOST, PUERTO);
            escritor= new PrintWriter(socket.getOutputStream(),true);
            lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        BufferedReader lecturaConsola = new BufferedReader(new InputStreamReader(System.in));
        
        ProtocoloCliente.procesar(lecturaConsola,lector,escritor);
        lecturaConsola.close();
        escritor.close();
        lector.close();
        socket.close();

    }

    
}
