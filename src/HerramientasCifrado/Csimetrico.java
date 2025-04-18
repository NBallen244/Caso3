package HerramientasCifrado;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;

public class Csimetrico {
    private static final String ALGORITMO = "AES/CBC/PKCS5Padding";

    public static byte[] cifrar(String reto, Key llave, byte[] iv){
        byte[] retoCifrado;
        try {
            IvParameterSpec vectorI = new IvParameterSpec(iv);
            // Se inicializa el cifrador en modo cifrado
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.ENCRYPT_MODE, llave, vectorI);
            byte[] retoClaro = reto.getBytes();  
            retoCifrado = cipher.doFinal(retoClaro);
            
        } catch (Exception e) {
            return null;
        }
        return retoCifrado;
    }

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
}
