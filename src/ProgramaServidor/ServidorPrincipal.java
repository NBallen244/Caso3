package ProgramaServidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorPrincipal {
    private static final int PUERTO = 3400;

    public static void main(String[] args) throws IOException {
        int numeroThreads = 10;
        ServerSocket ss = null;
        final ExecutorService pool = Executors.newFixedThreadPool(numeroThreads);
        boolean continuar = true;
        
        
        try {
            ss = new ServerSocket(PUERTO);
            System.out.println("Listo para recibir conexiones");
            while (continuar) {
                Socket socket = ss.accept();
                pool.execute(new ProtocoloServidor(socket));
    
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }finally{
            try {
                ss.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        

    }

}