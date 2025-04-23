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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

public class pruebacifrados {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidParameterSpecException, IOException, ClassNotFoundException {
        // Ejemplo de uso de los métodos de cifrado y descifrado
        PrivateKey clavePrivada;
        PublicKey clavePublica;
        SecretKey simetricaCifrado;
        SecretKey simetricaHash;
        byte[] vectorCifrado;
        BigInteger p;
        BigInteger g;
        BigInteger x;
        BigInteger y;

        Map <Integer, String> tablaServicios = new LinkedHashMap<>(); // Tabla de servicios

        tablaServicios.put(1, "Consulta estado de vuelo");
        tablaServicios.put(2, "Disponibilidad de vuelos");
        tablaServicios.put(3, "Costo de vuelo");


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

        //Pasar a bytes la tabla de servicios
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); // Crear un flujo de bytes
        ObjectOutputStream oos = new ObjectOutputStream(baos); // Crear un flujo de salida de objetos
        oos.writeObject(tablaServicios); // Escribir la tabla de servicios en el flujo
        byte[] bytesTablaServicios = baos.toByteArray(); // Obtener los bytes del flujo
        oos.close(); // Cerrar el flujo de salida
        baos.close();

        // Cifrar la tabla de servicios
        byte[] tablaServiciosCifrada = Csimetrico.cifrar(bytesTablaServicios, simetricaCifrado, vectorCifrado);
        byte[] tabladescifrada = Csimetrico.decifrar(tablaServiciosCifrada, simetricaCifrado, vectorCifrado); // Descifrar la tabla de servicios
        System.out.println("Tabla de servicios decifrada: " + new String(tabladescifrada)); // Imprimir tabla de servicios cifrada

        ByteArrayInputStream bais = new ByteArrayInputStream(tabladescifrada); // Crear un flujo de entrada de bytes
        ObjectInputStream ois = new ObjectInputStream(bais); // Crear un flujo de entrada de objetos
        Map<Integer, String> tablaServiciosDescifrada = (Map<Integer, String>) ois.readObject(); // Leer la tabla de servicios del flujo
        ois.close(); // Cerrar el flujo de entrada de objetos
        bais.close(); // Cerrar el flujo de bytes
        System.out.println("Tabla de servicios descifrada: " + tablaServiciosDescifrada); // Imprimir tabla de servicios descifrada
        
        

        
        
    }

}
