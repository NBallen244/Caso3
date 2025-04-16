package ProgramaCliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ProtocoloCliente {
    public static void procesar(BufferedReader lectura, BufferedReader lector, PrintWriter envio) throws IOException{
        System.out.println("Escribe el mensaje");
        String usuario = lectura.readLine();
        envio.println(usuario);
        String fromServer="";
        if((fromServer = lector.readLine())!= null){
            System.out.println("Respuesta del servidor: "+fromServer);
        }
    }
}
