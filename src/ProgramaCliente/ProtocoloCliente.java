package ProgramaCliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ProtocoloCliente {
    public static void procesar(BufferedReader entradaConsola, BufferedReader lector, PrintWriter envio) throws IOException{
        String fromServer;
        String fromUser;
        boolean ejecutar = true;

        while (ejecutar) {

            System.out.println("Escribe el mensaje a enviar: ");
            fromUser = entradaConsola.readLine();

            if(fromUser !=null){
                System.out.println("El usuario escribio: "+ fromUser);

                envio.println(fromUser);
                if(fromUser.equalsIgnoreCase("OK")){
                    ejecutar=false;
                }
                

            }
            if((fromServer = lector.readLine()) != null){
                System.out.println("Respuesta del servidor:"+ fromServer);
            }
            
        }
    }
}
