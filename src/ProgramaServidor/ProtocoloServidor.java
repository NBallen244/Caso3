package ProgramaServidor;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidParameterSpecException;
import java.util.Map;

import javax.crypto.SecretKey;

import HerramientasCifrado.Autenticacion;
import HerramientasCifrado.Casimetrico;
import HerramientasCifrado.ConversorByte;
import HerramientasCifrado.Csimetrico;
import HerramientasCifrado.GenLlaves;


public class ProtocoloServidor {
    public static void procesar(ObjectOutputStream out, ObjectInputStream in, int sdelegado, InetAddress cliente, boolean cifradoAsimetrico) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidParameterSpecException {
        try{
            //0a
            PublicKey clavePublica=ServidorPrincipal.getClavePublica();
            PrivateKey clavePrivada=ServidorPrincipal.getClavePrivada();
            Key claveSimetricaRespuesta=ServidorPrincipal.getClaveSimetricaRespuesta();
            long tiempoInicio;
            long tiempoFin;


            //1
            String mensaje = (String) in.readObject();
            if (!mensaje.equals("HELLO")) {
                System.out.println("[Servidor Delegado a Puerto "+ sdelegado + "] El cliente "+ cliente + " no ha iniciado correctamente la conexión.Cerrando conexión.");
                return;
            }
            //2b
            Integer reto= (Integer) in.readObject();
            
            System.out.println("[Servidor Delegado a Puerto "+ sdelegado + "] El cliente "+ cliente + " ha enviado el reto: " + reto);
            //3
            byte[] retoBytes= ConversorByte.convertirIntegerAByte(reto);
            byte[] retoCifrado;
            if (cifradoAsimetrico) {
                tiempoInicio=System.nanoTime();
                retoCifrado=Casimetrico.cifrar(retoBytes, clavePrivada);
                tiempoFin=System.nanoTime();
                ServidorPrincipal.agregarTiempoCifradoRespuesta(tiempoFin-tiempoInicio);
            }
            else{
                tiempoInicio=System.nanoTime();
                retoCifrado=Csimetrico.cifrarRespuesta(retoBytes, claveSimetricaRespuesta);
                tiempoFin=System.nanoTime();
                ServidorPrincipal.agregarTiempoCifradoRespuesta(tiempoFin-tiempoInicio);
            }
            //4
            out.writeObject(retoCifrado);
            out.flush();
            System.out.println("[Servidor Delegado a Puerto "+ sdelegado + "] Enviando reto cifrado al cliente: " + retoCifrado);
            //6
            String respuesta = (String) in.readObject();
            if (respuesta.equals("ERROR")) {
                System.out.println("[Servidor Delegado a Puerto "+ sdelegado + "] El cliente "+ cliente + " no pudo descifrar su reto correctamente. Cerrando conexion");
                return;
            }
            //7
            BigInteger[] parametrosDh=GenLlaves.generarParametroDh();
            BigInteger p=parametrosDh[0];
            BigInteger g=parametrosDh[1];
            BigInteger x=GenLlaves.generarParamSecretoDh(p);
            BigInteger gx=GenLlaves.generarParamPublicoDh(g, x, p);

            tiempoInicio=System.nanoTime();
            byte[] firmaP=Autenticacion.generarFirma(ConversorByte.convertirBintAbyte(p), clavePrivada);
            tiempoFin=System.nanoTime();
            ServidorPrincipal.agregarTiempoFirmas(tiempoFin-tiempoInicio);

            byte[] firmaG=Autenticacion.generarFirma(ConversorByte.convertirBintAbyte(g), clavePrivada);
            byte[] firmaGx=Autenticacion.generarFirma(ConversorByte.convertirBintAbyte(gx), clavePrivada);
            out.writeObject(p);
            out.writeObject(g);
            out.writeObject(gx);
            out.writeObject(firmaP);
            out.writeObject(firmaG);
            out.writeObject(firmaGx);
            out.flush();
            System.out.println("[Servidor Delegado a Puerto "+ sdelegado + "] Enviando parametros DH al cliente (P, G, G^X) y sus firmas");
            //10
            String respuestaDh = (String) in.readObject();
            if (respuestaDh.equals("ERROR")) {
                System.out.println("[Servidor Delegado a Puerto "+ sdelegado + "] El cliente "+ cliente + " no pudo verificar la firma de uno de los parametros DH. Cerrando conexion");
                return;
            }
            //11
            BigInteger gy= (BigInteger) in.readObject();
            
            //11b
            System.out.println("[Servidor Delegado a Puerto "+ sdelegado + "] El cliente "+ cliente + " ha enviado G^Y: " + gy);
            SecretKey[] llavesSimetricas=GenLlaves.generarLlavesSimetricas(GenLlaves.generarLlaveDh(gy, x, p));
            SecretKey k_ab1=llavesSimetricas[0];
            SecretKey k_ab2=llavesSimetricas[1];
            System.out.println("[Servidor Delegado a Puerto "+ sdelegado + "] Llaves simetricas generadas");
            //12b
            byte[] vectorI=(byte[]) in.readObject();
            
            System.out.println("[Servidor Delegado a Puerto "+ sdelegado + "] El cliente "+ cliente + " ha enviado el vector de inicializacion");
            //13
            Map<Integer, String> mapa= ServidorPrincipal.getTablaServicios();
            byte[] tablaBytes=ConversorByte.convertirObjetoAByte(mapa);

            tiempoInicio=System.nanoTime();
            byte[] tablaCifrada=Csimetrico.cifrar(tablaBytes, k_ab1, vectorI);
            tiempoFin=System.nanoTime();
            ServidorPrincipal.agregarTiempoCifradoTabla(tiempoFin-tiempoInicio);

            out.writeObject(tablaCifrada);
            out.flush();
            System.out.println("[Servidor Delegado a Puerto "+ sdelegado + "] Enviando tabla de servicios cifrada al cliente " + cliente);
            byte[] hmacTabla=Autenticacion.generarHmac(tablaBytes, k_ab2);
            out.writeObject(hmacTabla);
            out.flush();
            System.out.println("[Servidor Delegado a Puerto "+ sdelegado + "] Enviando HMAC de la tabla de servicios al cliente " + cliente);
            //14
            String respuestaTabla = (String) in.readObject();
            if (respuestaTabla.equals("ERROR")) {
                System.out.println("[Servidor Delegado a Puerto "+ sdelegado + "] El cliente "+ cliente + " no pudo verificar el HMAC de la tabla de servicios. Cerrando conexion");
                return;
            }
            byte[] idServicioCifrado= (byte[]) in.readObject();
            byte[] idServicioDescifrado=Csimetrico.decifrar(idServicioCifrado, k_ab1, vectorI);
            byte[] hmacIdServicio= (byte[]) in.readObject();
            if (idServicioDescifrado == null) {
                System.out.println("[Servidor Delegado a Puerto "+ sdelegado + "] El cliente "+ cliente + " no ha enviado el id del servicio correctamente. Cerrando conexion");
                return;
            }
            //15
            tiempoInicio=System.nanoTime();
            boolean verificacionHmac=Autenticacion.verificarHmac(idServicioDescifrado, hmacIdServicio, k_ab2);
            tiempoFin=System.nanoTime();
            ServidorPrincipal.agregarTiempoVerificacionHmac(tiempoFin-tiempoInicio);
            
            if (!verificacionHmac) {
                System.out.println("[Servidor Delegado a Puerto "+ sdelegado + "] El cliente "+ cliente + " envió un mensaje comprometido por falla de verificacion en el HMAC del servicio. Cerrando conexion");
                out.writeObject("ERROR");
                out.flush();
                return;
            }
            out.writeObject("OK");
            out.flush();
            //16
            Integer idServicio=ConversorByte.convertirByteAInteger(idServicioDescifrado);
            System.out.println("[Servidor Delegado a Puerto "+ sdelegado + "] El cliente "+ cliente + " ha solicitado el servicio: " + idServicio+ ": " + mapa.get(idServicio));
            Integer puertoServicio=ServidorPrincipal.getPuertoServicio(idServicio);
            Integer ipServicio=ServidorPrincipal.getIpServicio(idServicio);
            byte[] ipBytes=ConversorByte.convertirIntegerAByte(ipServicio);
            byte[] puertoBytes=ConversorByte.convertirIntegerAByte(puertoServicio);
            byte[] ipCifrado=Csimetrico.cifrar(ipBytes, k_ab1, vectorI);
            byte[] puertoCifrado=Csimetrico.cifrar(puertoBytes, k_ab1, vectorI);
            byte[] hmacIp=Autenticacion.generarHmac(ipBytes, k_ab2);
            byte[] hmacPuerto=Autenticacion.generarHmac(puertoBytes, k_ab2);
            out.writeObject(ipCifrado);
            out.flush();
            System.out.println("[Servidor Delegado a Puerto "+ sdelegado + "] Enviando IP cifrada al cliente " + cliente);
            out.writeObject(puertoCifrado);
            out.flush();
            System.out.println("[Servidor Delegado a Puerto "+ sdelegado + "] Enviando puerto cifrado al cliente " + cliente);
            out.writeObject(hmacIp);
            out.flush();
            System.out.println("[Servidor Delegado a Puerto "+ sdelegado + "] Enviando HMAC de la IP al cliente " + cliente);
            out.writeObject(hmacPuerto);
            out.flush();
            System.out.println("[Servidor Delegado a Puerto "+ sdelegado + "] Enviando HMAC del puerto al cliente " + cliente);

            //18
            String respuestaFinal = (String) in.readObject();
            if (respuestaFinal.equals("ERROR")) {
                System.out.println("[Servidor Delegado a Puerto "+ sdelegado + "] El cliente "+ cliente + " no pudo verificar el HMAC de la IP o el puerto. Cerrando conexion");
                return;
            }
            else{
                System.out.println("[Servidor Delegado a Puerto "+ sdelegado + "] El cliente "+ cliente + " verifico el ip, puerto y HMAC final, concretando el protocolo. Cerrando conexion");
            }

        }
        catch (IOException e){
            System.out.println("[Servidor Delegado a Puerto "+ sdelegado + "] Error al procesar el cliente: " + e.getMessage());
        }
        
    }
 
}
