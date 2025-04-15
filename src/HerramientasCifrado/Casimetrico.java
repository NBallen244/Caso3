package HerramientasCifrado;

import java.security.Key;

import javax.crypto.Cipher;

public class Casimetrico {
    private static final String ALGORITMO = "RSA";

    public static byte[] cifrar(String reto, Key llave){
        byte[] retoCifrado;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.ENCRYPT_MODE, llave);
            byte[] retoClaro = reto.getBytes();  
            retoCifrado = cipher.doFinal(retoClaro);
            
        } catch (Exception e) {
            return null;
        }
        return retoCifrado;
    }

    public static byte[] decifrar(byte[] retoCifrado, Key llave){
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
