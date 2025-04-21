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
            procesar();
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
    
    
    
    public void procesar() throws IOException {
       

        String linea = lector.readLine();
        String[] parts = linea.split("\\|");                   
        int clienteId = Integer.parseInt(parts[0]);            
        
        System.out.printf("Cliente %d solicitó servicio ", clienteId);
       
        String hostDelegado = sockCliente.getLocalAddress().getHostAddress();
        int puertoDelegado = ServidorPrincipal.getNextDelegatePort();
        escritor.println(hostDelegado + "|" + puertoDelegado);
        System.out.printf("Cliente %d asignado a delegado en %s:%d%n",
        clienteId, hostDelegado, puertoDelegado);
    }
}
