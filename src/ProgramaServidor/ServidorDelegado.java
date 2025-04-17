package ProgramaServidor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorDelegado extends Thread {
    private ServerSocket serverSocket;
    private int id;

    public ServidorDelegado(ServerSocket socket, int id) {
        this.serverSocket = socket;
        this.id = id;
    }

    public void run() {
        System.out.println("[Delegado " + id + "] Escuchando en puerto " + serverSocket.getLocalPort());
        try (Socket cliente = serverSocket.accept()) {
            System.out.println("[Delegado " + id + "] Conexión recibida de " + cliente.getInetAddress());
            PrintWriter escritor = new PrintWriter(cliente.getOutputStream(), true);
            BufferedReader lector = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            String linea;
            while ((linea = lector.readLine()) != null) {
                System.out.println("[Delegado " + id + "] Recepción: " + linea);
                escritor.println("[Resp del servicio " + id + "] " + linea);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (Exception ignored) {
            }
        }

    }
}
