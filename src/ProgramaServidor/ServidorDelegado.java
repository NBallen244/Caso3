package ProgramaServidor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServidorDelegado extends Thread {
    private Socket sktCliente = null;
    private int id;
    public ServidorDelegado(Socket socket, int id){
        this.sktCliente = socket;
        this.id =id;
    }
    public void run(){
        System.out.println("Inicio de un nuevo thread: "+ id);

        try {
            PrintWriter escritor = new PrintWriter(sktCliente.getOutputStream(),true);
            BufferedReader lector = new BufferedReader(new InputStreamReader(sktCliente.getInputStream()));
            ProtocoloServidor.procesar(lector, escritor);
            escritor.close();
            lector.close();
            sktCliente.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
