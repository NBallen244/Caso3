package ProgramaServidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ServidorDelegado extends Thread {
    private ServerSocket serverSocket;
    private int puerto;

    public ServidorDelegado(ServerSocket socket, int puerto) {
        this.serverSocket = socket;
        this.puerto       = puerto;
    }

    @Override
    public void run() {
        System.out.println("[Delegado " + puerto + "] Escuchando en puerto " + puerto);
        Socket cliente = null;
        try {
            cliente = serverSocket.accept();
            System.out.println("[Delegado " + puerto + "] Conexión recibida de " + cliente.getInetAddress());

            try (
                PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()))
            ) {
                String linea;
                while (true) {
                    try {
                        linea = in.readLine();
                        if (linea == null) break;  
                        System.out.println("[Delegado " + puerto + "] Recepción: " + linea);
                        out.println("[Resp del servicio " + puerto + "] " + linea);
                    } catch (SocketException se) {
                        System.out.println("[Delegado " + puerto + "] Conexión interrumpida por el cliente.");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { if (cliente != null) cliente.close(); } catch (IOException ignored) {}
            try { serverSocket.close(); } catch (IOException ignored) {}
            System.out.println("[Delegado " + puerto + "] Finalizado.");
        }
    }
}
