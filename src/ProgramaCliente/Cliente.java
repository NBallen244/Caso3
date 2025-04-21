package ProgramaCliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Cliente {
    private static final int PUERTO = 3400;
    private static final String HOST = "localhost";

    public static void main(String[] args) throws IOException, InterruptedException {
       

        Scanner scan = new Scanner(System.in);
        System.out.println("clientes concurrentes quieres lanzar: ");
        int numClientes = scan.nextInt();
        System.out.println("cuantas peticiones deseas: ");
        int peticiones = scan.nextInt();
        scan.close();

        ExecutorService pool = Executors.newFixedThreadPool(numClientes);
        for (int i = 1; i <= numClientes; i++) {
            String[] delegado = negociarDelegado(i);
            String hostDelegado = delegado[0];
            int puertoDelegado = Integer.parseInt(delegado[1]);
            pool.submit(new ClienteDelegado(hostDelegado, puertoDelegado, i, peticiones));
        }
        pool.shutdown();
        if (!pool.awaitTermination(5, TimeUnit.MINUTES)) {
            pool.shutdownNow();
        }
        System.out.println("Todos los clientes han terminado.");
    }

    private static String[] negociarDelegado(int id) throws IOException {
        try (
                Socket socket = new Socket(HOST, PUERTO);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ) {
            
            Random rand = new Random();
            int servicio = rand.nextInt(1, 4);
            out.println(id + "|" + servicio);
            String resp = in.readLine();
            String[] parts = resp.split("\\|");
            System.out.printf("Delegado asignado al cliente "+id+" en %s:%s%n", parts[0], parts[1] );
            return parts;

        }

    }

}
