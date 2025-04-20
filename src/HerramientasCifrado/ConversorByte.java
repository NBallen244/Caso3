package HerramientasCifrado;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.List;

public class ConversorByte {
    public static byte[] convertirIntegerAByte(Integer numero) {
        String numeroString = Integer.toString(numero); // Convertir el número a cadena
        return numeroString.getBytes(); // Convertir la cadena a bytes
    }

    public static Integer convertirByteAInteger(byte[] bytes) {
        String numeroString = new String(bytes); // Convertir los bytes a cadena
        return Integer.parseInt(numeroString); // Convertir la cadena a número
    }

    public static byte[] convertirListaAByte(List<Integer> lista) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); // Crear un flujo de bytes
        ObjectOutputStream oos = new ObjectOutputStream(baos); // Crear un flujo de salida de objetos
        oos.writeObject(lista); // Escribir la lista en el flujo
        return baos.toByteArray(); // Obtener los bytes de la lista
    }

    public static List<Integer> convertirByteALista(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes); // Crear un flujo de entrada de bytes
        ObjectInputStream ois = new ObjectInputStream(bais); // Crear un flujo de entrada de objetos
        return (List<Integer>) ois.readObject(); // Leer la lista del flujo
    }

    public static byte[] convertirBintAbyte(BigInteger numero) {
        return numero.toByteArray(); // Convertir el BigInteger a bytes
    }
}
