package com.example.proyectofinal_algoritmos.Modelo.Grafos;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Clase que emula a un grafo
 * @version 23/11/2026
 * @author Derek Ramón Garzón Vizcarra
 */
public class Grafo {
    private String nombre;
    private boolean dirigido;
    private HashMap<Integer, Vertice> vertices;
    private ArrayList<Integer> orden;

    public Grafo(String nombre, boolean dirigido) {
        this.nombre = nombre;
        this.dirigido = dirigido;
        vertices = new HashMap<>();
        orden = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isDirigido() {
        return dirigido;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public HashMap<Integer, Vertice> getVertices() {
        return vertices;
    }

    public ArrayList<Integer> getOrden() {
        return orden;
    }

    public boolean agregarVertice(int id) {
        if (!vertices.containsKey(id)) {
            System.out.println("El vertice ya existe");
            return false;
        }
        vertices.put(id, new Vertice(id));
        orden.add(id);
        return true;
    }

    public Vertice eliminarVertice(int id) {
        if (!validarVertice(id)) return null;
        Vertice verticeEliminado = vertices.remove(id);
        orden.remove(id);
        for (Vertice v : vertices.values()) {
            v.eliminarArista(id);
        }
        return verticeEliminado;
    }

    public boolean existeVertice(int id) {
        return vertices.containsKey(id);
    }

    public Vertice getVertice(int id) {
        return !validarVertice(id) ? null : vertices.get(id);
    }

    public ArrayList<Integer> obtenerIds() {
        return new ArrayList<>(orden);
    }

    public int cantidadVertices() {
        return vertices.size();
    }

    private boolean validarVertice(int id) {
        return vertices.containsKey(id);

    }
}
