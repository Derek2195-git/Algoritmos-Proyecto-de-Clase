package com.example.proyectofinal_algoritmos.Modelo.Archivos;

/**
 * Esta clase se encarga de validar un registro
 * (origen, destino, peso) leído de un archivo CSV.
 */
public class ConexionCSV {
    private final String origen;
    private final String destino;
    private final int peso;
    private final int linea;

    public ConexionCSV(String origen, String destino, int peso, int linea) {
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
        this.linea = linea;
    }

    public ConexionCSV(String origen, String destino, int peso) {
        this(origen, destino, peso, 0);
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public int getPeso() {
        return peso;
    }

    public int getLinea() {
        return linea;
    }

    /**
     * Genera la llave que identifica la conexión para detectar duplicados.
     * @param dirigido si es false, A-B y B-A generan la misma llave
     * @return llave única de la conexión
     */
    public String generarLlave(boolean dirigido) {
        final String SEP = "\u0000";
        if (dirigido || origen.compareTo(destino) <= 0) {
            return origen + SEP + destino;
        }
        return destino + SEP + origen;
    }

    @Override
    public String toString() {
        return origen + " --> " + destino + " (" + peso + ")";
    }
}
