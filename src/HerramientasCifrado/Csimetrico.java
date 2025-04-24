package HerramientasCifrado;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

public class Csimetrico {
    private static final String ALGORITMO = "AES/CBC/PKCS5Padding";
    private static final String ALGORITMO_RESPUESTA = "AES/ECB/PKCS5Padding";

    //Cifra un mensaje en formato byte[] usando una llave simetrica y un vector de inicializacion dados
    public static byte[] cifrar(byte[] reto, Key llave, byte[] iv){
        byte[] retoCifrado;
        try {
            IvParameterSpec vectorI = new IvParameterSpec(iv);
            // Se inicializa el cifrador en modo cifrado
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.ENCRYPT_MODE, llave, vectorI);
            byte[] retoClaro = reto;  
            retoCifrado = cipher.doFinal(retoClaro);
            
        } catch (Exception e) {
            return null;
        }
        return retoCifrado;
    }

    //*Descifra un mensaje en formato byte[] usando una llave simetrica y un vector de inicializacion dados*/
    public static byte[] decifrar(byte[] retoCifrado, Key llave, byte[] iv){
        byte[] retoClaro;
        try {
            IvParameterSpec vectorI = new IvParameterSpec(iv);
            // Se inicializa el cifrador en modo descifrado
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.DECRYPT_MODE, llave, vectorI);
            retoClaro = cipher.doFinal(retoCifrado);
            
        } catch (Exception e) {
            return null;
        }
        return retoClaro;
    }

    //Cifrar con AES EBC
    public static byte[] cifrarRespuesta(byte[] respuesta, Key llave){
        byte[] respuestaCifrado;
        try {
            // Se inicializa el cifrador en modo cifrado
            Cipher cipher = Cipher.getInstance(ALGORITMO_RESPUESTA);
            cipher.init(Cipher.ENCRYPT_MODE, llave);
            byte[] respuestaClaro = respuesta;  
            respuestaCifrado = cipher.doFinal(respuestaClaro);
            
        } catch (Exception e) {
            return null;
        }
        return respuestaCifrado;
    }
    //Descifrar con AES EBC
    public static byte[] decifrarRespuesta(byte[] respuestaCifrado, Key llave){
        byte[] respuestaClaro;
        try {
            // Se inicializa el cifrador en modo descifrado
            Cipher cipher = Cipher.getInstance(ALGORITMO_RESPUESTA);
            cipher.init(Cipher.DECRYPT_MODE, llave);
            respuestaClaro = cipher.doFinal(respuestaCifrado);
            
        } catch (Exception e) {
            return null;
        }
        return respuestaClaro;
    }
}
