public class Preguntas {
    private String pregunta;
    private String respuestaCorrecta;

    public Preguntas(String pregunta, String respuestaCorrecta) {
        this.pregunta = pregunta;
        this.respuestaCorrecta = respuestaCorrecta;
    }

    public String getPregunta() {
        return pregunta;
    }

    public String getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public boolean verificarRespuesta(String respuesta) {
        return respuestaCorrecta.equalsIgnoreCase(respuesta);
    }
}
