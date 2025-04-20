package HerramientasCifrado;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

public class pruebacifrados {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidParameterSpecException, IOException, ClassNotFoundException {
        // Ejemplo de uso de los métodos de cifrado y descifrado
        Integer reto;
        List<Integer> tablaid;
        PrivateKey clavePrivada;
        PublicKey clavePublica;
        SecretKey simetricaCifrado;
        SecretKey simetricaHash;
        byte[] vectorCifrado;
        BigInteger p;
        BigInteger g;
        BigInteger x;
        BigInteger y;

        reto= 123456; // Reto de ejemplo
        tablaid = new ArrayList<>(); // Tabla de ID de ejemplo
        for (int i = 0; i < 10; i++) {
            tablaid.add(i);
        }
        GenLlaves.generarLlavesAsimetricas(); // Generar llaves asimétricas
        clavePrivada = GenLlaves.recuperarKprivada(); // Recuperar llave privada
        clavePublica = GenLlaves.recuperarKpublica(); // Recuperar llave pública

        vectorCifrado=GenLlaves.generarVectorI(); // Generar vector de cifrado
        BigInteger[] dH = GenLlaves.generarParametroDh();
        p = dH[0]; // Obtener el primer parámetro de Diffie-Hellman
        g= dH[1]; // Obtener el segundo parámetro de Diffie-Hellman
        x=GenLlaves.generarParamSecretoDh(p); // Generar el valor x
        y=GenLlaves.generarParamSecretoDh(p); // Generar el valor y
        BigInteger  gx=GenLlaves.generarParamPublicoDh(g, x, p); // Calcular gx
        BigInteger llaveMaestra=GenLlaves.generarLlaveDh(gx, y, p); // Calcular llave maestra

        SecretKey[] llavesSimetricas=GenLlaves.generarLlavesSimetricas(llaveMaestra); // Generar llaves simétricas
        simetricaCifrado=llavesSimetricas[0]; // Obtener llave de cifrado
        simetricaHash=llavesSimetricas[1]; // Obtener llave de hash

        //Pasar a bytes el reto y la tabla de ID
        byte[] retoBytes = Integer.toString(reto).getBytes(); // Convertir reto a bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); // Crear un flujo de bytes
        ObjectOutputStream oos = new ObjectOutputStream(baos); // Crear un flujo de salida de objetos   
        oos.writeObject(tablaid); // Escribir la tabla de ID en el flujo
        byte[] tablaidBytes = baos.toByteArray(); // Obtener los bytes de la tabla de ID

        //Prueba de cifrado asimetrico

        byte[] retoCifrado=Casimetrico.cifrar(retoBytes, clavePublica); // Cifrar reto
        byte[] retoDescifrado=Casimetrico.decifrar(retoCifrado, clavePrivada); // Descifrar reto
        System.out.println("Reto cifrado:"+retoDescifrado);
        String retoDescifradoString = new String(retoDescifrado); // Convertir bytes a string
        Integer retoDescifradoInt = Integer.parseInt(retoDescifradoString); // Convertir string a entero
        if (retoDescifradoInt.equals(reto)) { // Comparar el reto original con el descifrado
            System.out.println("El reto se cifró y descifró correctamente.");
        } else {
            System.out.println("Error en el cifrado/descifrado del reto.");
        }

        //Prueba de cifrado simetrico
        byte[] tablaidCifrada=Csimetrico.cifrar(tablaidBytes, simetricaCifrado, vectorCifrado); // Cifrar tabla de ID
        byte[] tablaidDescifrada=Csimetrico.decifrar(tablaidCifrada, simetricaCifrado, vectorCifrado); // Descifrar tabla de ID
        ByteArrayInputStream bais = new ByteArrayInputStream(tablaidDescifrada); // Crear un flujo de bytes de entrada
        ObjectInputStream ois = new ObjectInputStream(bais); // Crear un flujo de entrada de objetos
        List<Integer> tablaidDescifradaList = (List<Integer>) ois.readObject(); // Leer la tabla de ID descifrada
        boolean sonIguales = true; // Variable para verificar si las tablas son iguales
        for (int i = 0; i < tablaidDescifradaList.size(); i++) { // Comparar la tabla de ID original con la descifrada
            if (!tablaid.get(i).equals(tablaidDescifradaList.get(i))) {
                System.out.println("Error en el cifrado/descifrado de la tabla de ID.");
                sonIguales = false; // Si hay una diferencia, se establece en falso
                break;
            }
        }
        if (sonIguales) { // Si todas las tablas son iguales, se imprime el mensaje
            System.out.println("La tabla de ID se cifró y descifró correctamente.");
        }
        baos.close();
        oos.close(); // Cerrar el flujo de salida de objetos
        bais.close(); // Cerrar el flujo de bytes de entrada
        ois.close(); // Cerrar el flujo de entrada de objetos

        //PruebaFirmas
        byte[] bytesG=g.toByteArray();
        byte[] bytesP=p.toByteArray(); // Convertir p a bytes
        byte[] bytesGx=gx.toByteArray(); // Convertir gx a bytes

        byte[] firmaG=Autenticacion.generarFirma(bytesG, clavePrivada); // Generar firma de g
        byte[] firmaP=Autenticacion.generarFirma(bytesP, clavePrivada); // Generar firma de p
        byte[] firmaGx=Autenticacion.generarFirma(bytesGx, clavePrivada); // Generar firma de gx

        if (firmaG != null && firmaP != null && firmaGx != null) { // Verificar si las firmas son válidas
            System.out.println("Las firmas se generaron correctamente.");
        } else {
            System.out.println("Error al generar las firmas.");
        }

        boolean firmaVerificadaG=Autenticacion.verificarFirma(bytesG, firmaG, clavePublica); // Verificar firma de g
        boolean firmaVerificadaP=Autenticacion.verificarFirma(bytesP, firmaP, clavePublica); // Verificar firma de p
        boolean firmaVerificadaGx=Autenticacion.verificarFirma(bytesGx, firmaGx, clavePublica); // Verificar firma de gx

        if (firmaVerificadaG && firmaVerificadaP && firmaVerificadaGx) { // Verificar si las firmas son válidas
            System.out.println("Las firmas se verificaron correctamente.");
        } else {
            System.out.println("Error al verificar las firmas.");
        }

        //PruebaHMAC
        byte[] hmacReto=Autenticacion.generarHmac(retoBytes, simetricaHash); // Generar HMAC del reto
        
        if (hmacReto != null) { // Verificar si el HMAC es válido
            System.out.println("El HMAC se generó correctamente.");
        } else {
            System.out.println("Error al generar el HMAC.");
        }
        boolean hmacVerificado=Autenticacion.verificarHmac(retoBytes, hmacReto, simetricaHash); // Verificar HMAC del reto
        if (hmacVerificado) { // Verificar si el HMAC es válido
            System.out.println("El HMAC se verificó correctamente.");
        } else {
            System.out.println("Error al verificar el HMAC.");
        }
        

        
        
    }

}
