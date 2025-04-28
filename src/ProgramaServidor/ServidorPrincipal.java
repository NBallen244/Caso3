package ProgramaServidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import HerramientasCifrado.GenLlaves;

public class ServidorPrincipal {
    private static final int PUERTO = 3400;
    private static final Map<Integer, String> tablaServicios = new LinkedHashMap<>();
    private static final Map<Integer, Integer> ipServicios = new LinkedHashMap<>();
    private static final Map<Integer, Integer> puertosServicios = new LinkedHashMap<>();
    private static PublicKey clavePublica = null;
    private static PrivateKey clavePrivada = null;
    private static Key claveSimetricaRespuesta = null;
    
    static ServerSocket socketServidor = null;
    static boolean continuar = true;

    private static List<Long> tiemposFirmas;
    private static List<Long> tiemposCifradoTabla;
    private static List<Long> tiemposCifradoRespuesta;
    private static List<Long> tiemposVerificacionHmac;


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

    public static Key getClaveSimetricaRespuesta() {
        return claveSimetricaRespuesta;
    }

    public static synchronized void agregarTiempoFirmas(Long tiempo) {
        tiemposFirmas.add(tiempo);
    }
    public static synchronized void agregarTiempoCifradoTabla(Long tiempo) {
        tiemposCifradoTabla.add(tiempo);
    }
    public static synchronized void agregarTiempoCifradoRespuesta(Long tiempo) {
        tiemposCifradoRespuesta.add(tiempo);
    }
    public static synchronized void agregarTiempoVerificacionHmac(Long tiempo) {
        tiemposVerificacionHmac.add(tiempo);
    }

    public static Long getTiempoFirmasPromedio(){
        return tiemposFirmas.stream().mapToLong(Long::longValue).sum() / tiemposFirmas.size();
    }
    public static Long getTiempoCifradoTablaPromedio(){
        return tiemposCifradoTabla.stream().mapToLong(Long::longValue).sum() / tiemposCifradoTabla.size();
    }
    public static Long getTiempoCifradoRespuestaPromedio(){
        return tiemposCifradoRespuesta.stream().mapToLong(Long::longValue).sum() / tiemposCifradoRespuesta.size();
    }
    public static Long getTiempoVerificacionHmacPromedio(){
        return tiemposVerificacionHmac.stream().mapToLong(Long::longValue).sum() / tiemposVerificacionHmac.size();
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        tiemposFirmas = new LinkedList<>();
        tiemposCifradoTabla = new LinkedList<>();
        tiemposCifradoRespuesta = new LinkedList<>();
        tiemposVerificacionHmac = new LinkedList<>();

        //Generamos las llaves de cifrado asimetrico
        GenLlaves.generarLlavesAsimetricas();
        //Generamos las llaves de cifrado simetrico para la respuesta
        GenLlaves.generarLlaveSimetricaRespuesta();
        //Guardamos las llaves 
        clavePublica = GenLlaves.recuperarKpublica();
        clavePrivada = GenLlaves.recuperarKprivada();
        claveSimetricaRespuesta = GenLlaves.recuperarLlaveSimetricaRespuesta();

        System.out.println("Escribe el numero de delegados");
        Scanner scan = new Scanner(System.in);
        int numerodelegados = scan.nextInt();
        System.out.println("Escribe el total de solicitudes a atender");
        int totalSolicitudes = scan.nextInt();
        System.out.println("Escribe true para cifras respuesta asimetrica o false para simetrica");
        boolean cifradoRespuesta = scan.nextBoolean();

        scan.close();

        System.out.println("El servidor ha iniciado en el puerto " + PUERTO);
        System.out.println("Esperando conexiones de clientes...");

        try {
            socketServidor = new ServerSocket(PUERTO, numerodelegados);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        int id = 0;
        ExecutorService pool = Executors.newFixedThreadPool(numerodelegados);
        while (continuar&& id < totalSolicitudes) {
            try {
                Socket cliente = socketServidor.accept();
                id++;
                System.out.println("[Servidor Principal] Conexión aceptada desde " + cliente.getInetAddress() + ":" + cliente.getPort());
                ServidorDelegado delegado = new ServidorDelegado(cliente, cliente.getPort(), cifradoRespuesta);
                pool.submit(delegado);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        pool.shutdown();
        if (!pool.awaitTermination(5, TimeUnit.MINUTES)) {
            pool.shutdownNow();
        }
        try {
            socketServidor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Todos los delegados han terminado.");
        System.out.println("Tiempo promedio de firma: " + getTiempoFirmasPromedio() + " ns");
        System.out.println("Tiempo promedio de cifrado de tabla: " + getTiempoCifradoTablaPromedio() + " ns");
        System.out.println("Tiempo promedio de cifrado de respuesta: " + getTiempoCifradoRespuestaPromedio() + " ns");
        System.out.println("Tiempo promedio de verificacion de HMAC: " + getTiempoVerificacionHmacPromedio() + " ns");
        
    }

}
