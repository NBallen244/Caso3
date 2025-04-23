package ProgramaCliente;

import java.io.IOException;
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
        System.out.println("cuantas peticiones desea por cliente: ");
        int peticiones = scan.nextInt();
        scan.close();

        ExecutorService pool = Executors.newFixedThreadPool(numClientes);
        for (int i = 1; i <= numClientes; i++) {
            pool.execute(new ClienteDelegado(HOST, PUERTO, i, peticiones));
        }
        pool.shutdown();
        if (!pool.awaitTermination(5, TimeUnit.MINUTES)) {
            pool.shutdownNow();
        }
        System.out.println("Todos los clientes han terminado.");
    }


}
