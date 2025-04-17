package ProgramaServidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProtocoloServidor implements Runnable{
    private static Socket sockCliente;
    private BufferedReader lector;
    private PrintWriter escritor;
    private static final Map<Integer, String> tablaServicios = ServidorPrincipal.getTablaServicios();

 

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
        for (Map.Entry<Integer, String> entry : tablaServicios.entrySet()) {
            escritor.println(entry.getKey() + "|" + entry.getValue());
        }
        escritor.println("END");

        String linea = lector.readLine();
        System.out.println("Cliente solicitó servicio ID: " + linea);

        int elegido;
        try {
            elegido = Integer.parseInt(linea);
        } catch (NumberFormatException e) {
            escritor.println("-1|-1");  
            return;
        }
        String hostDelegado = sockCliente.getLocalAddress().getHostAddress();
        int puertoDelegado = ServidorPrincipal.getDelegatePort(elegido);

        escritor.println(hostDelegado + "|" + puertoDelegado);
        System.out.println("Asignado cliente a delegado en " + hostDelegado + ":" + puertoDelegado);

        String inputLine;
        while ((inputLine = lector.readLine()) != null) {
            System.out.println("Entrada a procesar: " + inputLine);
            escritor.println(inputLine);
        }

    }
}
