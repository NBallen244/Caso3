package ProgramaCliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Cliente {
    private static final int PUERTO = 3400;
    private static final String HOST = "localhost";

    public static void main(String[] args) throws IOException, InterruptedException {
        String[] delegado = negociarDelegado();
        Socket socket = null;
        PrintWriter escritor = null;
        BufferedReader lector = null;


        Scanner scan = new Scanner(System.in);
        System.out.println("clientes concurrentes quieres lanzar: ");
        int numClientes = scan.nextInt();
        System.out.println("cuantas peticiones deseas: ");
        int peticiones = scan.nextInt();

        ExecutorService pool = Executors.newFixedThreadPool(numClientes);
        for (int i = 1; i <= numClientes; i++) {
            pool.submit(new ClienteDelegado(delegado[0], Integer.parseInt(delegado[1]), i));
        }
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.MINUTES);


        try {
            socket = new Socket(HOST, PUERTO);
            escritor = new PrintWriter(socket.getOutputStream(), true);
            lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        BufferedReader lecturaConsola = new BufferedReader(new InputStreamReader(System.in));

        ProtocoloCliente.procesar(lecturaConsola, lector, escritor,  peticiones);
        lecturaConsola.close();
        escritor.close();
        lector.close();
        socket.close();

    }

}
