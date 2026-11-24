package com.example.proyectofinal_algoritmos.Modelo.Grafos;

import java.util.ArrayList;

/**
 * Clase que modela al vertice de un grafo
 * @version 23/11/2026
 * @author Derek Ramón Garzón Vizcarra
 */
public class Vertice {
    // Vertice de un grafo
    private int id;
    private ArrayList<Arista> aristasAdyacentes;

    public Vertice(int id) {
        this.id = id;
        aristasAdyacentes = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public ArrayList<Arista> getAristasAdyacentes() {
        return aristasAdyacentes;
    }

    public int getGrado() {
        return aristasAdyacentes.size();
    }

    public void agregarArista(Arista arista) {
        aristasAdyacentes.add(arista);
    }

    public Arista buscarArista(int destino) {
        for (int i = 0; i < aristasAdyacentes.size(); i++) {
            if (aristasAdyacentes.get(i).getDestino() == destino) {
                return aristasAdyacentes.get(i);
            }
        }
        return null;
    }

    public boolean eliminarArista(int destino) {
        for (int i = 0; i < aristasAdyacentes.size(); i++) {
            if (aristasAdyacentes.get(i).getDestino() == destino) {
                aristasAdyacentes.remove(i);
                return true;
            }
        }
        return false;
    }
}
