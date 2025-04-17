package ProgramaCliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ProtocoloCliente {
    public static void procesar(BufferedReader entradaConsola, BufferedReader lector, PrintWriter envio) throws IOException{
        String fromServer;
        List<Integer> ids = new ArrayList<>();
        List<String> nombres = new ArrayList<>();
        String linea;
        System.out.println("Servicios disponibles:");
        while (!(linea = lector.readLine()).equals("END")) {
            String[] parts = linea.split("\\|", 2);
            int id = Integer.parseInt(parts[0]);
            String nombre = parts[1];
            ids.add(id);    
            nombres.add(nombre);
            System.out.printf("%d: %s\n", id, nombre);
        }
        System.out.print("Elige el ID del servicio: ");
        String seleccion = entradaConsola.readLine();
        envio.println(seleccion);

        fromServer = lector.readLine();
        String[] resp = fromServer.split("\\|");
        String host = resp[0];
        int port = Integer.parseInt(resp[1]);
        System.out.println("Conectado al delegado en " + host + ":" + port);

        try (Socket sockDel = new Socket(host, port);
                PrintWriter escritorDel = new PrintWriter(sockDel.getOutputStream(), true);
                BufferedReader lectorDel = new BufferedReader(new InputStreamReader(sockDel.getInputStream()))) {

            String mensaje;
            while ((mensaje = entradaConsola.readLine()) != null) {
                escritorDel.println(mensaje);
                String respuesta = lectorDel.readLine();
                System.out.println(respuesta);
            }
        }
    }
}
