import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClienteHandler implements Runnable {
    private DatagramSocket socket;
    private DatagramPacket paqueteEntrada;

    public ClienteHandler(DatagramSocket socket, DatagramPacket paqueteEntrada) {
        this.socket = socket;
        this.paqueteEntrada = paqueteEntrada;
    }

    @Override
    public void run() {
        try {
            String mensajeRecibido = new String(paqueteEntrada.getData(), 0, paqueteEntrada.getLength()).trim();
            InetAddress direccionCliente = paqueteEntrada.getAddress();
            int puertoCliente = paqueteEntrada.getPort();
            String idCliente = direccionCliente.toString() + ":" + puertoCliente;

            int numeroDePregunta = Servidor.getProgresoClientes().getOrDefault(idCliente, 0);
            int total = Servidor.getPuntuacionClientes().getOrDefault(idCliente, 0);

            if (numeroDePregunta > 0) {
                Preguntas preguntaActual = Servidor.PREGUNTAS[numeroDePregunta - 1];
                boolean correcto = preguntaActual.verificarRespuesta(mensajeRecibido);
                if (correcto) total++;
                Servidor.getPuntuacionClientes().put(idCliente, total);

                registrarRespuesta(mensajeRecibido, direccionCliente.getHostAddress());
            }

            String mensajeRespuesta;
            if (numeroDePregunta == Servidor.PREGUNTAS.length) {
                mensajeRespuesta = "Finalizó la encuesta, tu puntuación es: " + total * 4 + "/20";
                Servidor.getProgresoClientes().remove(idCliente);
                Servidor.getPuntuacionClientes().remove(idCliente);
            } else {
                Preguntas preguntaActual = Servidor.PREGUNTAS[numeroDePregunta];
                mensajeRespuesta = (numeroDePregunta > 0 ? 
                    "Respuesta " + (Servidor.PREGUNTAS[numeroDePregunta - 1].verificarRespuesta(mensajeRecibido) ? "Correcta. " : "Incorrecta. La respuesta es: " + Servidor.PREGUNTAS[numeroDePregunta - 1].getRespuestaCorrecta()) 
                    : "")
                    + "\nPregunta: " + preguntaActual.getPregunta();
                Servidor.getProgresoClientes().put(idCliente, numeroDePregunta + 1);
            }

            byte[] bufferSalida = mensajeRespuesta.getBytes();
            DatagramPacket paqueteSalida = new DatagramPacket(bufferSalida, bufferSalida.length, direccionCliente, puertoCliente);
            socket.send(paqueteSalida);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registrarRespuesta(String respuesta, String ip) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("respuestas.txt", true))) {
            String fechaHora = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String entrada = String.format("Respuesta #%d | Fecha y Hora: %s | IP: %s | Respuesta: %s",
                                            Servidor.getContadorRespuestas(), fechaHora, ip, respuesta);
            writer.println(entrada);
            Servidor.incrementarContadorRespuestas();  // Incrementar el contador de respuestas
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
