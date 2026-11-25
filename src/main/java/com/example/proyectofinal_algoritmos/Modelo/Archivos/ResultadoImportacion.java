package com.example.proyectofinal_algoritmos.Modelo.Archivos;

import com.example.proyectofinal_algoritmos.Modelo.Grafos.Grafo;

import java.util.ArrayList;

/**
 * La clase representa el resultado de importar un archivo CSV
 * (grafo, errores, advertencias y tiempos).
 */
public class ResultadoImportacion {
    private final String nombreArchivo;
    private final boolean dirigido;
    private final ArrayList<ConexionCSV> conexionesValidas;
    private final ArrayList<ErrorImportacion> errores;
    private final ArrayList<ErrorImportacion> advertencias;
    private boolean encabezadoValido;
    private int registrosLeidos;
    private Grafo grafo;
    private MapaVertices mapa;
    private long tiempoLecturaNs;
    private long tiempoConstruccionNs;

    public ResultadoImportacion(String nombreArchivo, boolean dirigido) {
        this.nombreArchivo = nombreArchivo;
        this.dirigido = dirigido;
        conexionesValidas = new ArrayList<>();
        errores = new ArrayList<>();
        advertencias = new ArrayList<>();
        encabezadoValido = false;
        registrosLeidos = 0;
    }

    void agregarConexion(ConexionCSV conexion) {
        conexionesValidas.add(conexion);
    }

    /**
     * Agrega el problema a la lista de errores o de advertencias según su tipo.
     * @param problema problema encontrado
     */
    void agregarProblema(ErrorImportacion problema) {
        if (problema.esAdvertencia()) {
            advertencias.add(problema);
        } else {
            errores.add(problema);
        }
    }

    void setEncabezadoValido(boolean encabezadoValido) {
        this.encabezadoValido = encabezadoValido;
    }

    void incrementarRegistrosLeidos() {
        registrosLeidos++;
    }

    void setGrafo(Grafo grafo, MapaVertices mapa) {
        this.grafo = grafo;
        this.mapa = mapa;
    }

    void setTiempoLecturaNs(long tiempoLecturaNs) {
        this.tiempoLecturaNs = tiempoLecturaNs;
    }

    void setTiempoConstruccionNs(long tiempoConstruccionNs) {
        this.tiempoConstruccionNs = tiempoConstruccionNs;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public boolean isDirigido() {
        return dirigido;
    }

    public ArrayList<ConexionCSV> getConexionesValidas() {
        return conexionesValidas;
    }

    public ArrayList<ErrorImportacion> getErrores() {
        return errores;
    }

    public ArrayList<ErrorImportacion> getAdvertencias() {
        return advertencias;
    }

    public boolean isEncabezadoValido() {
        return encabezadoValido;
    }

    public int getRegistrosLeidos() {
        return registrosLeidos;
    }

    public Grafo getGrafo() {
        return grafo;
    }

    public MapaVertices getMapa() {
        return mapa;
    }

    public long getTiempoLecturaNs() {
        return tiempoLecturaNs;
    }

    public long getTiempoConstruccionNs() {
        return tiempoConstruccionNs;
    }

    public long getTiempoTotalNs() {
        return tiempoLecturaNs + tiempoConstruccionNs;
    }

    public int getNumVertices() {
        return grafo == null ? 0 : grafo.cantidadVertices();
    }

    public int getNumAristas() {
        return grafo == null ? 0 : conexionesValidas.size();
    }

    public boolean tieneErrores() {
        return !errores.isEmpty();
    }

    public boolean tieneAdvertencias() {
        return !advertencias.isEmpty();
    }

    /**
     * Indica si se generó un grafo con al menos una arista.
     * @return true si la importación fue exitosa
     */
    public boolean esExitosa() {
        return encabezadoValido && grafo != null && !conexionesValidas.isEmpty();
    }

    /**
     * Cuenta los errores y advertencias de un tipo.
     * @param tipo tipo de problema, usar las constantes de ErrorImportacion
     * @return número de problemas de ese tipo
     */
    public int contarProblemas(String tipo) {
        int total = 0;
        for (ErrorImportacion e : errores) {
            if (e.getTipo().equals(tipo)) {
                total++;
            }
        }
        for (ErrorImportacion e : advertencias) {
            if (e.getTipo().equals(tipo)) {
                total++;
            }
        }
        return total;
    }

    /**
     * Genera un texto con el resumen de la importación para mostrarlo al usuario.
     * @return resumen de la importación
     */
    public String generarResumen() {
        StringBuilder sb = new StringBuilder();
        sb.append("Archivo: ").append(nombreArchivo).append('\n');
        sb.append("Tipo de grafo: ").append(dirigido ? "dirigido" : "no dirigido").append('\n');
        if (!encabezadoValido) {
            sb.append("No se pudo importar el archivo.\n");
            for (ErrorImportacion e : errores) {
                sb.append("  - ").append(e).append('\n');
            }
            return sb.toString();
        }
        sb.append("Registros leídos: ").append(registrosLeidos).append('\n');
        sb.append("Vértices importados: ").append(getNumVertices()).append('\n');
        sb.append("Aristas importadas: ").append(getNumAristas()).append('\n');
        sb.append("Registros con error: ").append(errores.size()).append('\n');
        sb.append("Conexiones duplicadas omitidas: ").append(advertencias.size()).append('\n');
        sb.append(String.format("Tiempo de lectura: %.3f ms%n", tiempoLecturaNs / 1_000_000.0));
        sb.append(String.format("Tiempo de construcción: %.3f ms%n", tiempoConstruccionNs / 1_000_000.0));
        if (!errores.isEmpty()) {
            sb.append("Errores:\n");
            for (ErrorImportacion e : errores) {
                sb.append("  - ").append(e).append('\n');
            }
        }
        if (!advertencias.isEmpty()) {
            sb.append("Advertencias:\n");
            for (ErrorImportacion a : advertencias) {
                sb.append("  - ").append(a).append('\n');
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return generarResumen();
    }
}
