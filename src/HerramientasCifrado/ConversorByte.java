package HerramientasCifrado;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Map;

public class ConversorByte {
    public static byte[] convertirIntegerAByte(Integer numero) {
        String numeroString = Integer.toString(numero); // Convertir el número a cadena
        return numeroString.getBytes(); // Convertir la cadena a bytes
    }

    public static Integer convertirByteAInteger(byte[] bytes) {
        String numeroString = new String(bytes); // Convertir los bytes a cadena
        return Integer.parseInt(numeroString); // Convertir la cadena a número
    }

    public static byte[] convertirObjetoAByte(Object objeto) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); // Crear un flujo de bytes
        ObjectOutputStream oos = new ObjectOutputStream(baos); // Crear un flujo de salida de objetos
        oos.writeObject(objeto); // Escribir un objeto en el flujo
        byte[] bytes = baos.toByteArray(); // Obtener los bytes del flujo
        oos.close(); // Cerrar el flujo de salida
        baos.close(); // Cerrar el flujo de bytes
        return bytes; // Obtener los bytes de la lista
    }

    public static Map<Integer, String> convertirByteAMapa(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes); // Crear un flujo de entrada de bytes
        ObjectInputStream ois = new ObjectInputStream(bais); // Crear un flujo de entrada de objetos
        Map<Integer, String> mapa = (Map<Integer, String>) ois.readObject(); // Leer el mapa del flujo
        ois.close(); // Cerrar el flujo de entrada de objetos
        bais.close(); // Cerrar el flujo de bytes
        return mapa; // Leer el mapa del flujo
    }

    public static byte[] convertirBintAbyte(BigInteger numero) {
        return numero.toByteArray(); // Convertir el BigInteger a bytes
    }
}
