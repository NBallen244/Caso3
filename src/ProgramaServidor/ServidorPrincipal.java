package ProgramaServidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorPrincipal {
    private static final int PUERTO = 3400;
    private static final Map<Integer, String> tablaServicios = new LinkedHashMap<>();
    private static final Map<Integer, Integer> delegatePorts = new LinkedHashMap<>();

    static {
        tablaServicios.put(1, "Consulta estado de vuelo");
        tablaServicios.put(2, "Disponibilidad de vuelos");
        tablaServicios.put(3, "Costo de vuelo");
        int base = 3500;
        for (Integer id : tablaServicios.keySet()) {
            delegatePorts.put(id, base + id);
        }
    }

    public static Map<Integer, String> getTablaServicios() {
        return tablaServicios;
    }
    public static int getDelegatePort(int id) {
        return delegatePorts.getOrDefault(id, -1);
    }
    public static void main(String[] args) throws IOException {

        for (Map.Entry<Integer, Integer> entry : delegatePorts.entrySet()) {
            int id = entry.getKey();
            int port = entry.getValue();
            ServerSocket ssDel = new ServerSocket(port);
            new ServidorDelegado(ssDel, id).start();
        }

        ExecutorService pool = Executors.newFixedThreadPool(10);
        try (ServerSocket ss = new ServerSocket(PUERTO)) {
            System.out.println("[Principal] Listo en puerto " + PUERTO);
            while (true) {
                Socket cliente = ss.accept();
                pool.execute(new ProtocoloServidor(cliente));
            }
        }
        

    }

}