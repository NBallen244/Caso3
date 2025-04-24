package ProgramaCliente;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClienteDelegado extends Thread  {
    private final String host;
    private final int puerto;
    private final int idCliente;
    private final int numPeticiones;
    private final boolean decifradoRespuesta;

    public ClienteDelegado(String host, int puerto, int idCliente, int numPeticiones, boolean decifradoRespuesta) {
        this.host = host;
        this.puerto = puerto;
        this.idCliente = idCliente;
        this.numPeticiones = numPeticiones;
        this.decifradoRespuesta = decifradoRespuesta;
    }
    @Override
    public void run(){
        try {
            for (int i = 1; i <= numPeticiones; i++) {
                Socket socket = new Socket(host,puerto);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ProtocoloCliente.procesar(out, in, idCliente, i, decifradoRespuesta);
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
