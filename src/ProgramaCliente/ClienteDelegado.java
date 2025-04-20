package ProgramaCliente;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteDelegado implements Runnable  {
    private final String host;
    private final int puerto;
    private final int idCliente;
    private final int numPeticiones;

    public ClienteDelegado(String host, int puerto, int idCliente, int numPeticiones){
        this.host = host;
        this.puerto = puerto;
        this.idCliente = idCliente;
        this.numPeticiones = numPeticiones;

    }
    @Override
    public void run(){
        try {
            Socket socket = new Socket(host,puerto);
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            for (int i = 1; i <= numPeticiones; i++) {
                String mensaje = "C" + idCliente + " Consulta " + i;
            
                System.out.printf("[Cliente %d - #%d] Enviando: %s%n", idCliente, i, mensaje);         out.println(mensaje);  
                out.println(mensaje); 
                String resp = in.readLine();
                                                
                System.out.printf("[Cliente %d - #%d] Recibido: %s%n", idCliente, i, resp);  
            }
            
    
        } catch (Exception e) {
            e.printStackTrace();
            
        }
    }

}
