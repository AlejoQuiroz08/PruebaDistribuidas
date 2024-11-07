import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class Servidor {
    private static final int PUERTO = 1234;
    public static final Preguntas[] PREGUNTAS = {
        new Preguntas("¿Cuál es el océano más grande?", "pacifico"),
        new Preguntas("¿Qué fruta es conocida por tener vitamina C?", "naranja"),
        new Preguntas("¿Qué animal es el símbolo de Australia?", "canguro"),
        new Preguntas("¿Qué elemento químico tiene el símbolo O?", "oxigeno"),
        new Preguntas("¿En qué año terminó la Segunda Guerra Mundial?", "1945")
    };

    private static Map<String, Integer> progresoClientes = new HashMap<>();
    private static Map<String, Integer> puntuacionClientes = new HashMap<>();
    private static int contadorRespuestas = 1;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PUERTO)) {
            System.out.println("Servidor UDP esperando conexiones...");

            while (true) {
                byte[] bufferEntrada = new byte[1024];
                DatagramPacket paqueteEntrada = new DatagramPacket(bufferEntrada, bufferEntrada.length);
                socket.receive(paqueteEntrada);

                // Al recibir un paquete, se maneja la conexión del cliente en un hilo independiente
                new Thread(new ClienteHandler(socket, paqueteEntrada)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getter para progresoClientes
    public static Map<String, Integer> getProgresoClientes() {
        return progresoClientes;
    }

    // Getter para puntuacionClientes
    public static Map<String, Integer> getPuntuacionClientes() {
        return puntuacionClientes;
    }

    // Getter para contadorRespuestas
    public static int getContadorRespuestas() {
        return contadorRespuestas;
    }

    // Incrementar contadorRespuestas
    public static void incrementarContadorRespuestas() {
        contadorRespuestas++;
    }
}
