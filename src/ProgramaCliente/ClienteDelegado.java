package ProgramaCliente;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteDelegado implements Runnable  {
    private final String host;
    private final int puerto;
    private final int idCliente;

    public ClienteDelegado(String host, int puerto, int idCliente){
        this.host = host;
        this.puerto = puerto;
        this.idCliente = idCliente;
    }
    @Override
    public void run(){
        try {
            Socket socket = new Socket(host,puerto);
            PrintWriter escritor = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String consulta = "Consulta desde cliente id :"+ idCliente;
            escritor.println(consulta);

            String respuesta = lector.readLine();
            System.out.printf("[Cliente %d] Recibido: %s%n", idCliente, respuesta);
            
        } catch (Exception e) {
            e.printStackTrace();
            
        }
    }

}
