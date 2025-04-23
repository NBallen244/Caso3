package ProgramaServidor;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;

public class ServidorDelegado extends Thread {
    private Socket cliente;
    private int id;

    public ServidorDelegado(Socket cliente, int id) {
        this.cliente = cliente;
        this.id = id;
    }

    @Override
    public void run() {
        System.out.println("[Servidor Delegado " + this.id + "] Escuchando al cliente en " + cliente.getInetAddress());
        try {
            ObjectOutputStream out = new ObjectOutputStream(cliente.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(cliente.getInputStream());
            ProtocoloServidor.procesar(out, in, this.id, cliente.getInetAddress());
            out.close();
            in.close();
            System.out.println("[Servidor Delegado " + this.id + "] Cerrando conexión con el cliente " + cliente.getInetAddress());
            cliente.close();
            
            
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
}
