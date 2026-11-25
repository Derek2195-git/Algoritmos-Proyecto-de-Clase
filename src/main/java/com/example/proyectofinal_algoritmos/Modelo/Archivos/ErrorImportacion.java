package com.example.proyectofinal_algoritmos.Modelo.Archivos;

/**
 * Esta clase sirve para detectar un problems
 * encontrado al leer un archivo CSV.
 */
public class ErrorImportacion {
    public static final String ARCHIVO = "Archivo no legible";
    public static final String ENCABEZADO = "Columnas requeridas faltantes";
    public static final String REGISTRO_INCOMPLETO = "Registro incompleto";
    public static final String VERTICE_VACIO = "Vértice vacío";
    public static final String PESO_NO_NUMERICO = "Peso no numérico";
    public static final String PESO_NEGATIVO = "Peso negativo";
    public static final String LAZO = "Conexión de un vértice consigo mismo";
    public static final String DUPLICADO = "Conexión duplicada";

    private final int linea;
    private final String tipo;
    private final String mensaje;

    /**
     * Crea un problema de importación.
     * @param linea número de línea del archivo (0 si no aplica)
     * @param tipo tipo de problema, usar las constantes de esta clase
     * @param mensaje descripción del problema
     */
    public ErrorImportacion(int linea, String tipo, String mensaje) {
        this.linea = linea;
        this.tipo = tipo;
        this.mensaje = mensaje;
    }

    public int getLinea() {
        return linea;
    }

    public String getTipo() {
        return tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    /**
     * Indica si el problema es solo una advertencia
     * (los duplicados no invalidan el archivo).
     * @return true si es advertencia
     */
    public boolean esAdvertencia() {
        return DUPLICADO.equals(tipo);
    }

    @Override
    public String toString() {
        String prefijo = linea > 0 ? "Línea " + linea + ": " : "";
        return prefijo + "[" + tipo + "] " + mensaje;
    }
}
