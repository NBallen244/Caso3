package ProgramaServidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorPrincipal {
    private static final int PUERTO = 3400;
    private static final Map<Integer, String> tablaServicios = new LinkedHashMap<>();
    private static final Map<Integer, Integer> delegacionPuertos = new LinkedHashMap<>(); // ← ya no lo usamos aquí
    private static final Queue<Integer> delegadosDispo = new LinkedList<>(); // ← cola de puertos

    static {
        tablaServicios.put(1, "Consulta estado de vuelo");
        tablaServicios.put(2, "Disponibilidad de vuelos");
        tablaServicios.put(3, "Costo de vuelo");
    }

    public static Map<Integer, String> getTablaServicios() {
        return tablaServicios;
    }

    public static int getDelegatePort(int id) { 
        return delegacionPuertos.getOrDefault(id, -1);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Escribe el numero de delegados");
        Scanner scan = new Scanner(System.in);
        int numerodelegados = scan.nextInt();
        scan.close(); // ← cerramos el scanner

        int base = 3500;
        for (int i = 1; i <= numerodelegados; i++) {
            delegadosDispo.add(base + i); 
        }
        for (int port : delegadosDispo) {
            ServerSocket ssDel = new ServerSocket(port);
            new ServidorDelegado(ssDel, port).start(); 
        }

        ExecutorService pool = Executors.newFixedThreadPool(numerodelegados);
        try (ServerSocket ss = new ServerSocket(PUERTO)) {
            System.out.println("[Principal] Listo en puerto " + PUERTO);
            while (true) {
                Socket cliente = ss.accept();
                pool.execute(new ProtocoloServidor(cliente));
            }
        }
    }

    public static synchronized int getNextDelegatePort() { 
        return delegadosDispo.isEmpty()
                ? -1
                : delegadosDispo.poll();
    }
}
