package ProgramaServidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ProtocoloServidor implements Runnable{
    private Socket sockCliente;
    private BufferedReader lector;
    private PrintWriter escritor;
    public ProtocoloServidor(Socket s){
        this.sockCliente = s;
        try {
            escritor = new PrintWriter(sockCliente.getOutputStream(),true);
            lector = new BufferedReader(new InputStreamReader(sockCliente.getInputStream()));
        } catch (Exception e) {
           e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            procesar(lector, escritor);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                sockCliente.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    
    public static void procesar(BufferedReader lector, PrintWriter escritor) throws IOException {
        String inputLine = lector.readLine();
        System.out.println("Entrada a procesar: "+ inputLine);

        String outputLine = inputLine;

        escritor.println(outputLine);
        System.out.println("Salida procesada: "+outputLine);
    }
}
