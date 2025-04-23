package ProgramaCliente;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClienteDelegado implements Runnable  {
    private final String host;
    private final int puerto;
    private final int idCliente;
    private final int numPeticiones;

    public ClienteDelegado(String host, int puerto, int idCliente, int numPeticiones){
        this.host = host;
        this.puerto = puerto;
        this.idCliente = idCliente;
        this.numPeticiones = numPeticiones;

    }
    @Override
    public void run(){
        try {
            for (int i = 1; i <= numPeticiones; i++) {
                Socket socket = new Socket(host,puerto);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ProtocoloCliente.procesar(out, in, idCliente, i);
                out.close();
                in.close();
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            }
            
    
        } catch (Exception e) {
            e.printStackTrace();
            
        }
    }

}
