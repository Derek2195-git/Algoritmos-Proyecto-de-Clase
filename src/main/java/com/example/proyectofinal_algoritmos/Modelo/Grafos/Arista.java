package com.example.proyectofinal_algoritmos.Modelo.Grafos;

/**
 * Clase que modela a la arista de un grafo
 * @version 23/11/2026
 * @author Derek Ramón Garzón Vizcarra
 */
public class Arista {
    // Arista de un grafo
    private int peso;
    private int origen;
    private int destino;

    public Arista(int peso, int origen, int destino) {
        this.peso = peso;
        this.origen = origen;
        this.destino = destino;
    }

    public int getOrigen() {
        return origen;
    }

    public int getPeso() {
        return peso;
    }

    public int getDestino() {
        return destino;
    }

    public void setPeso(int peso) {
        this.peso = peso;
    }


}
