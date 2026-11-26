package com.example.proyectofinal_algoritmos.Modelo.Busquedas;

import java.util.ArrayList;

// Guarda el resultado de Dijkstra: origen, destino, lista de vertices y costo total
public class ResultadoRuta {
    private final int origen;
    private final int destino;
    private final ArrayList<Integer> ruta;
    private final int costoTotal;
    private final boolean existeRuta;

    public ResultadoRuta(int origen, int destino, ArrayList<Integer> ruta, int costoTotal) {
        this.origen = origen;
        this.destino = destino;
        this.ruta = ruta;
        this.costoTotal = costoTotal;
        this.existeRuta = true;
    }

    // Para cuando no hay camino posible entre origen y destino
    public static ResultadoRuta sinRuta(int origen, int destino) {
        return new ResultadoRuta(origen, destino);
    }

    private ResultadoRuta(int origen, int destino) {
        this.origen = origen;
        this.destino = destino;
        this.ruta = new ArrayList<>();
        this.costoTotal = -1;
        this.existeRuta = false;
    }

    public int getOrigen() {
        return origen;
    }

    public int getDestino() {
        return destino;
    }

    // Lista de vertices desde el origen hasta el destino (vacia si no hay ruta)
    public ArrayList<Integer> getRuta() {
        return ruta;
    }

    // Costo total del camino, o -1 si no existe ruta
    public int getCostoTotal() {
        return costoTotal;
    }

    public boolean existeRuta() {
        return existeRuta;
    }

    // Convierte la ruta a texto tipo "1 -> 2 -> 4"
    public String rutaComoTexto() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ruta.size(); i++) {
            if (i > 0) sb.append(" -> ");
            sb.append(ruta.get(i));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        if (!existeRuta) {
            return "No existe ruta de " + origen + " a " + destino;
        }
        return "Origen: " + origen + ", Destino: " + destino
                + ", Ruta: " + rutaComoTexto() + ", Costo total: " + costoTotal;
    }
}
