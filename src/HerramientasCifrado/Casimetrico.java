package HerramientasCifrado;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class Casimetrico {
    private static final String ALGORITMO = "RSA";

    //* Cifra un mensaje en formato byte[] usando una llave publica dada */
    public static byte[] cifrar(byte[] reto, PublicKey llave){
        byte[] retoCifrado;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.ENCRYPT_MODE, llave);
            byte[] retoClaro = reto;  
            retoCifrado = cipher.doFinal(retoClaro);
            
        } catch (Exception e) {
            return null;
        }
        return retoCifrado;
    }

    //* Descifra un mensaje en formato byte[] usando una llave privada dada */
    public static byte[] decifrar(byte[] retoCifrado, PrivateKey llave){
        byte[] retoClaro;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.DECRYPT_MODE, llave);
            retoClaro = cipher.doFinal(retoCifrado);
            
        } catch (Exception e) {
            return null;
        }
        return retoClaro;
    }
}
