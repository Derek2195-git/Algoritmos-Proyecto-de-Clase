package com.example.proyectofinal_algoritmos.Modelo.Analisis;

/**
 * Esta clase representa el resultado de medir un algoritmo
 * (nombre, tamaño de los datos y tiempo).
 */
public class Medicion {
    private final String algoritmo;
    private final int tamanio;
    private final long tiempoNanos;
    private final int repeticiones;
    private final String datos;

    public Medicion(String algoritmo, int tamanio, long tiempoNanos,
                    int repeticiones, String datos) {
        this.algoritmo = algoritmo;
        this.tamanio = tamanio;
        this.tiempoNanos = tiempoNanos;
        this.repeticiones = repeticiones;
        this.datos = datos == null ? "" : datos;
    }

    public Medicion(String algoritmo, int tamanio, long tiempoNanos) {
        this(algoritmo, tamanio, tiempoNanos, 1, "");
    }

    public String getAlgoritmo() {
        return algoritmo;
    }

    public int getTamanio() {
        return tamanio;
    }

    public long getTiempoNanos() {
        return tiempoNanos;
    }

    public double getTiempoMicrosegundos() {
        return tiempoNanos / 1_000.0;
    }

    public double getTiempoMilisegundos() {
        return tiempoNanos / 1_000_000.0;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public String getDatos() {
        return datos;
    }

    @Override
    public String toString() {
        return String.format("%s | n = %d | %.4f ms (promedio de %d)",
                algoritmo, tamanio, getTiempoMilisegundos(), repeticiones);
    }
}
