package ProgramaCliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class ProtocoloCliente {
    public static void procesar(BufferedReader entradaConsola, BufferedReader lector, PrintWriter envio, int numPeticiones)
            throws IOException {
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
        fromServer = lector.readLine();
        String[] resp = fromServer.split("\\|");
        String host = resp[0];
        int port = Integer.parseInt(resp[1]);
        System.out.println("Conectado al delegado en " + host + ":" + port);

        try (Socket sockDel = new Socket(host, port);
                PrintWriter escritorDel = new PrintWriter(sockDel.getOutputStream(), true);
                BufferedReader lectorDel = new BufferedReader(new InputStreamReader(sockDel.getInputStream()))) {

            
            
            if (numPeticiones == 32) {
                    for (int i = 1; i <= 32; i++) {
                        String consulta = "Consulta " + i;
                        escritorDel.println(consulta);
                        String respuesta = lectorDel.readLine();
                        System.out.printf("Respuesta %2d: %s%n", i, respuesta);
                    }}
            
            if(numPeticiones ==1){
                    Random rand = new Random();
                    String mensaje;
                        mensaje = rand.ints(1, 3).toString();
                        escritorDel.println(mensaje);
                        String respuesta = lectorDel.readLine();
                        System.out.println(respuesta);
                    
                }
            }
        }
    
}
