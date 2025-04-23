package ProgramaServidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import HerramientasCifrado.GenLlaves;

public class ServidorPrincipal {
    private static final int PUERTO = 3400;
    private static final Map<Integer, String> tablaServicios = new LinkedHashMap<>();
    private static final Map<Integer, Integer> ipServicios = new LinkedHashMap<>();
    private static final Map<Integer, Integer> puertosServicios = new LinkedHashMap<>();
    private static PublicKey clavePublica = null;
    private static PrivateKey clavePrivada = null;
    
    static ServerSocket socketServidor = null;
    static boolean continuar = true;

    static {
        // Inicializamos la tabla de servicios con los puertos y las IPs
        tablaServicios.put(1, "Consulta estado de vuelo");
        tablaServicios.put(2, "Disponibilidad de vuelos");
        tablaServicios.put(3, "Costo de vuelo");

        ipServicios.put(1, 127128);
        ipServicios.put(2, 127127);
        ipServicios.put(3, 123123);

        puertosServicios.put(1, 3401);
        puertosServicios.put(2, 3402);
        puertosServicios.put(3, 3403);
    }

    public static Map<Integer, String> getTablaServicios() {
        return tablaServicios;
    }

    public static Integer getIpServicio(Integer id) {
        return ipServicios.getOrDefault(id, -1);
    }

    public static Integer getPuertoServicio(Integer id) {
        return puertosServicios.getOrDefault(id, -1);
    }

    public static PublicKey getClavePublica() {
        return clavePublica;
    }
    public static PrivateKey getClavePrivada() {
        return clavePrivada;
    }

    public static void main(String[] args) throws IOException {
        //Generamos las llaves de cifrado asimetrico
        GenLlaves.generarLlavesAsimetricas();
        //Guardamos las llaves 
        clavePublica = GenLlaves.recuperarKpublica();
        clavePrivada = GenLlaves.recuperarKprivada();

        System.out.println("Escribe el numero de delegados");
        Scanner scan = new Scanner(System.in);
        int numerodelegados = scan.nextInt();
        scan.close();

        System.out.println("El servidor ha iniciado en el puerto " + PUERTO);
        System.out.println("Esperando conexiones de clientes...");

        try {
            socketServidor = new ServerSocket(PUERTO);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        ExecutorService pool = Executors.newFixedThreadPool(numerodelegados);
        int id = 0;
        while (continuar) {
            try {
                Socket cliente = socketServidor.accept();
                id++;
                System.out.println("[Servidor Principal] Conexión aceptada desde " + cliente.getInetAddress() + ":" + cliente.getPort());
                ServidorDelegado delegado = new ServidorDelegado(cliente, id);
                pool.execute(delegado);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }

}
