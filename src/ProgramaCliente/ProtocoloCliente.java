package ProgramaCliente;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import HerramientasCifrado.Autenticacion;
import HerramientasCifrado.Casimetrico;
import HerramientasCifrado.ConversorByte;
import HerramientasCifrado.Csimetrico;
import HerramientasCifrado.GenLlaves;


public class ProtocoloCliente {
    public static void procesar(ObjectOutputStream out, ObjectInputStream in, int idCliente, int numPeticion) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        try{
            //0
            PublicKey clavePublica=Cliente.getClavePublica();
            System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Iniciando conexion con el servidor, recuperando llave pública.");

            System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Iniciando peticion " + numPeticion);
            //1
            out.writeObject("HELLO");
            out.flush();
            System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Enviando HELLO al servidor.");
            //2
            Integer reto = (int) (Math.random() * 10000);
            //2b
            out.writeObject(reto);
            out.flush();
            System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Enviando reto: " + reto);
            //4
            byte[] retoCifrado = (byte[]) in.readObject();
            System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Recibiendo reto cifrado");
            //5a
            byte[] retoDescifrado = Casimetrico.decifrar(retoCifrado, clavePublica);
            Integer retoDescifradoInt = ConversorByte.convertirByteAInteger(retoDescifrado);
            System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Reto descifrado: " + retoDescifradoInt);
            //5b
            if (!retoDescifradoInt.equals(reto)) {
                System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Reto incorrecto. Cerrando conexion con servidor.");
                out.writeObject("ERROR");
                out.flush();
                return;
            }
            System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Reto correcto verificado.");
            //6
            out.writeObject("OK");
            out.flush();
            System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Reto correcto. Enviando OK al servidor.");
            //8
            BigInteger p= (BigInteger) in.readObject();
            BigInteger g= (BigInteger) in.readObject();
            BigInteger gx=(BigInteger) in.readObject();
            System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Recibiendo parametros de Diffie-Hellman");
            byte[] firmaP= (byte[]) in.readObject();
            byte[] firmaG= (byte[]) in.readObject();
            byte[] firmaGx= (byte[]) in.readObject();
            System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Recibiendo firmas de los parametros de Diffie-Hellman.");

            //9
            boolean firmaPValida=Autenticacion.verificarFirma(ConversorByte.convertirBintAbyte(p), firmaP, clavePublica);
            boolean firmaGValida=Autenticacion.verificarFirma(ConversorByte.convertirBintAbyte(g), firmaG, clavePublica);
            boolean firmaGxValida=Autenticacion.verificarFirma(ConversorByte.convertirBintAbyte(gx), firmaGx, clavePublica);
            //10
            if (!firmaPValida || !firmaGValida || !firmaGxValida) {
                System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Una de las firmas de los parametros de Diffie-Hellman no es valida. Cerrando conexion con servidor.");
                out.writeObject("ERROR");
                out.flush();
                return;
            }
            System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Firmas de los parametros de Diffie-Hellman verificadas.");
            out.writeObject("OK");
            out.flush();
            BigInteger y=GenLlaves.generarParamSecretoDh(p);
            BigInteger gy=GenLlaves.generarParamPublicoDh(g, y, p);
            System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Parametros de Diffie-Hellman verificados. Enviando gy al servidor: " + gy);
            //11
            out.writeObject(gy);
            out.flush();
            SecretKey[] llavesSimetricas=GenLlaves.generarLlavesSimetricas(GenLlaves.generarLlaveDh(gx, y, p));
            SecretKey k_ab1=llavesSimetricas[0];
            SecretKey k_ab2=llavesSimetricas[1];
            System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Llaves simetricas generadas.");
            //12
            byte[] vectorI=GenLlaves.generarVectorI();
            System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Generando vector de inicializacion");
            out.writeObject(vectorI);
            out.flush();
            System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Enviando vector de inicializacion al servidor.");
            //13
            byte[] tablaCifrada= (byte[]) in.readObject();
            System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Recibiendo tabla cifrada del servidor.");
            byte[] hmacTabla= (byte[]) in.readObject();
            System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Recibiendo HMAC de la tabla cifrada del servidor.");
            
            byte[] tablaDecifrada=Csimetrico.decifrar(tablaCifrada, k_ab1, vectorI);
            boolean hmacValido=Autenticacion.verificarHmac(tablaDecifrada, hmacTabla, k_ab2);
            //13b
            if (tablaDecifrada==null||!hmacValido){
                System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Error al recibir la tabla de servicios. HMAC podría estar comprometido. Cerrando conexion con servidor.");
                out.writeObject("ERROR");
                out.flush();
                return;
            }
            System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Tabla de servicios descifrada y HMAC verificado. Enviando OK al servidor.");
            out.writeObject("OK");
            out.flush();
            //14
            Map<Integer, String> tablaServicio=ConversorByte.convertirByteAMapa(tablaDecifrada);
            List<Integer> listaServicios=new ArrayList<>(tablaServicio.keySet());
            int indexServicio=(int) (Math.random() * listaServicios.size());
            Integer idServicio=listaServicios.get(indexServicio);
            String nombreServicio=tablaServicio.get(idServicio);
            System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Servicio seleccionado: " + nombreServicio + " con id: " + idServicio);
            byte[] idServicioCifrado=Csimetrico.cifrar(ConversorByte.convertirIntegerAByte(idServicio), k_ab1, vectorI);
            byte[] hmacIdServicio=Autenticacion.generarHmac(ConversorByte.convertirIntegerAByte(idServicio), k_ab2);
            out.writeObject(idServicioCifrado);
            out.flush();
            System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Enviando id del servicio cifrado al servidor.");
            out.writeObject(hmacIdServicio);
            out.flush();
            System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Enviando HMAC del id del servicio al servidor.");
            String respuestaIdServicio = (String) in.readObject();
            if (respuestaIdServicio.equals("ERROR")|| respuestaIdServicio==null) {
                System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Error al recibir el id Y/O verificar HMAC del servicio. Cerrando conexion con servidor.");
                return;
            }
            //16
            byte[] ipServicioCifrado= (byte[]) in.readObject();
            byte[] puertoServicioCifrado= (byte[]) in.readObject();
            byte[] hmacIpServicio= (byte[]) in.readObject();
            byte[] hmacPuertoServicio= (byte[]) in.readObject();

            byte[] ipServicioDescifrado=Csimetrico.decifrar(ipServicioCifrado, k_ab1, vectorI);
            byte[] puertoServicioDescifrado=Csimetrico.decifrar(puertoServicioCifrado, k_ab1, vectorI);
            boolean hmacIpValido=Autenticacion.verificarHmac(ipServicioDescifrado, hmacIpServicio, k_ab2);
            boolean hmacPuertoValido=Autenticacion.verificarHmac(puertoServicioDescifrado, hmacPuertoServicio, k_ab2);

            if (ipServicioDescifrado==null||puertoServicioDescifrado==null||!hmacIpValido||!hmacPuertoValido) {
                System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Error al recibir la IP o puerto del servicio o HMAC no valido. Cerrando conexion con servidor.");
                out.writeObject("ERROR");
                out.flush();
                return;
            }
            System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Recibiendo IP y puerto del servicio cifrados y HMACs validos. Peticion completada. Cerrando conexion.");
            out.writeObject("OK");
            out.flush();
        }
        catch (Exception e){
            System.out.println("[Cliente " + idCliente + " pet" +numPeticion+ "] Error en la peticion " + numPeticion + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
         

}
