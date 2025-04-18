package ProgramaServidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorPrincipal {
    private static final int PUERTO = 3400;
    private static final Map<Integer, String> tablaServicios = new LinkedHashMap<>();
    private static final Map<Integer, Integer> delegacionPuertos = new LinkedHashMap<>();

    

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

        int base = 3500;
        for (int id =1; id <= numerodelegados;id++) {
            delegacionPuertos.put(id, base + id);
        }

        for (Map.Entry<Integer, Integer> entry : delegacionPuertos.entrySet()) {
            int id = entry.getKey();
            int port = entry.getValue();
            ServerSocket ssDel = new ServerSocket(port);
            new ServidorDelegado(ssDel, id).start();
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

}