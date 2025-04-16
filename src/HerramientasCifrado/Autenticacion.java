package HerramientasCifrado;

import java.security.PrivateKey;
import java.security.Signature;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

public class Autenticacion {
    private static final String ALGORITMO_HMAC = "HmacSHA256";
    private static final String ALGORITMO_FIRMA = "SHA256withRSA";

    //* Genera un HMAC de un mensaje usando una llave secreta dada */
    public static byte[] generarHmac(byte[] mensaje, SecretKey llave) {
        byte[] hmac = null;
        try {
            Mac mac = Mac.getInstance(ALGORITMO_HMAC);
            mac.init(llave);
            hmac = mac.doFinal(mensaje);
        } catch (Exception e) {
            return null;
        }
        return hmac;
    }

    //* Genera una firma digital de un mensaje usando una llave privada dada */
    public static byte[] generarFirma(byte[] mensaje, PrivateKey llavePrivada) {
        byte[] firma = null;
        try {
            Signature signature = Signature.getInstance(ALGORITMO_FIRMA);
            signature.initSign(llavePrivada);
            signature.update(mensaje);
            firma = signature.sign();
        } catch (Exception e) {
            return null;
        }
        return firma;
    }
}
