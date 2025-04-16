package HerramientasCifrado;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class GenLlaves {

    public static void generarLlavesAsimetricas() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1048);
            PublicKey publicKey = keyGen.generateKeyPair().getPublic();
            PrivateKey privateKey = keyGen.generateKeyPair().getPrivate();
            
            // Guardar las llaves en archivos
            FileOutputStream  fosPublica = new FileOutputStream("llavePublica.txt");
            FileOutputStream  fosPrivada = new FileOutputStream("llavePrivada.txt");

            ObjectOutput oosPublica = new ObjectOutputStream(fosPublica);
            ObjectOutput oosPrivada = new ObjectOutputStream(fosPrivada);

            oosPublica.writeObject(publicKey);
            oosPrivada.writeObject(privateKey);

            oosPrivada.close();
            oosPublica.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static PrivateKey recuperarKprivada(){
        PrivateKey llavePrivada = null;
        try {
            FileInputStream fis = new FileInputStream("llavePrivada.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);
            llavePrivada = (PrivateKey) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return llavePrivada;
    }

    public static PublicKey recuperarKpublica(){
        PublicKey llavePrivada = null;
        try {
            FileInputStream fis = new FileInputStream("llavePublica.txt");
            ObjectInputStream ois = new ObjectInputStream(fis);
            llavePrivada = (PublicKey) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return llavePrivada;
    }

    public static byte[] generarVectorI(){
        byte[] vectorI = new byte[16];
        for (int i = 0; i < vectorI.length; i++) {
            vectorI[i] = (byte) (Math.random() * 256);
        }
        return vectorI;
    }

    public static BigInteger[] generarParametroDh() throws NoSuchAlgorithmException, InvalidParameterSpecException{
        AlgorithmParameterGenerator paramGen =AlgorithmParameterGenerator.getInstance("DiffieHellman");
        paramGen.init(1024);
        AlgorithmParameters params = paramGen.generateParameters();
        DHParameterSpec dhParams = params.getParameterSpec(DHParameterSpec.class);
        BigInteger p = dhParams.getP();
        BigInteger g = dhParams.getG();
        BigInteger[] parametros = new BigInteger[2];
        parametros[0] = p;
        parametros[1] = g;
        return parametros;
    }

    public static BigInteger generarParamSecretoDh(BigInteger p) throws NoSuchAlgorithmException{
        BigInteger paramSecreto = p.subtract(BigInteger.valueOf(1L)).multiply(BigInteger.valueOf((long) (Math.random())));
        return paramSecreto;
    }

    public static BigInteger generarParamPublicoDh(BigInteger g, BigInteger paramSecreto, BigInteger p){
        BigInteger paramPublico = g.modPow(paramSecreto, p);
        return paramPublico;
    }

    public static BigInteger generarLlaveDh(BigInteger paramPublico, BigInteger paramSecreto, BigInteger p) {
        BigInteger llaveSimetrica = paramPublico.modPow(paramSecreto, p);
        return llaveSimetrica;
    }

    public static SecretKey[] generarLlavesSimetricas(BigInteger llaveSimetrica) {
        SecretKey[] llaves = new SecretKey[2];
        SecretKey ab1;
        SecretKey ab2;
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-512");
            sha.update(llaveSimetrica.toByteArray());
            byte[] digest = sha.digest();
            byte[] llaveSimetrica1 = Arrays.copyOfRange(digest, 0, digest.length / 2);
            byte[] llaveSimetrica2 = Arrays.copyOfRange(digest, digest.length / 2, digest.length);
            ab1=new SecretKeySpec(llaveSimetrica1, "AES");
            ab2=new SecretKeySpec(llaveSimetrica2, "HmacSHA256");
            llaves[0] = ab1;
            llaves[1] = ab2;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return llaves;
    }
        
}
