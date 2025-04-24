package ProgramaCliente;

import java.io.IOException;
import java.security.Key;
import java.security.PublicKey;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import HerramientasCifrado.GenLlaves;

public class Cliente {
    private static final int PUERTO = 3400;
    private static final String HOST = "localhost";
    private static PublicKey clavePublica;
    private static Key claveSimetricaRespuesta;

    public static PublicKey getClavePublica() {
        return clavePublica;
    }
    public static Key getClaveSimetricaRespuesta() {
        return claveSimetricaRespuesta;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // Se obtiene la clave publica del servidor y la simetrica
        clavePublica = GenLlaves.recuperarKpublica();
        claveSimetricaRespuesta = GenLlaves.recuperarLlaveSimetricaRespuesta();
       

        Scanner scan = new Scanner(System.in);
        System.out.println("clientes concurrentes quieres lanzar: ");
        int numClientes = scan.nextInt();
        System.out.println("cuantas peticiones desea por cliente: ");
        int peticiones = scan.nextInt();
        System.out.println("Indique si la respuesta del servidor se descifrara con asimetrico (true/false): ");
        boolean decifradoRespuesta = scan.nextBoolean();
        scan.close();

        ExecutorService pool = Executors.newFixedThreadPool(numClientes);
        for (int i = 1; i <= numClientes; i++) {
            pool.submit(new ClienteDelegado(HOST, PUERTO, i, peticiones, decifradoRespuesta));
        }
        pool.shutdown();
        if (!pool.awaitTermination(5, TimeUnit.MINUTES)) {
            pool.shutdownNow();
        }
        System.out.println("Todos los clientes han terminado.");
    }


}
