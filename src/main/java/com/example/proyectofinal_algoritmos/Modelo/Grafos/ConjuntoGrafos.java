package com.example.proyectofinal_algoritmos.Modelo.Grafos;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Clase que modela a un conjunto de varios grafos
 * @version 24/11/2026
 * @author Derek Ramón Garzón Vizcarra
 */
public class ConjuntoGrafos {
    // Logica principal
    private HashMap<String, Grafo> grafos;
    private Grafo grafoActivo;

    public ConjuntoGrafos() {
        grafos = new HashMap<>();
        grafoActivo = null;
    }

    public boolean agregarBooleano(Grafo grafo) {
        if (grafo == null) return false;
        String nombre = grafo.getNombre();
        if (nombre == null || nombre.trim().isEmpty()) return false;
        if (grafos.containsKey(nombre)) return false;
        grafos.put(nombre, grafo);
        grafoActivo = grafo;
        return true;
    }

    public boolean crearGrafo(String nombre, boolean dirigido) {
        return agregarBooleano(new Grafo(nombre, dirigido));
    }

    public boolean eliminarGrafo(String nombre) {
        Grafo eliminado = grafos.remove(nombre);
        if (eliminado == null) return false;
        if (eliminado == grafoActivo) {
            grafoActivo = null;
            for (Grafo grafo : grafos.values()) {
                grafoActivo = grafo;
                break;
            }
        }
        return true;
    }

    public boolean seleccionarActivo(String nombre) {
        Grafo grafo = grafos.get(nombre);
        if (grafo == null) return false;
        grafoActivo = grafo;
        return true;
    }

    public boolean renombrarGrafos(String nombreActual, String nombreNuevo) {
        if (nombreNuevo == null || nombreNuevo.trim().isEmpty()) return false;
        if (!grafos.containsKey(nombreActual) || grafos.containsKey(nombreNuevo)) return false;
        Grafo grafo = grafos.remove(nombreActual);
        grafo.setNombre(nombreNuevo);
        grafos.put(nombreNuevo, grafo);
        return true;
    }

    public Grafo getGrafo(String nombre) {
        return grafos.get(nombre);
    }

    public Grafo getGrafoActivo() {
        return grafoActivo;
    }

    public boolean hayGrafoActivo() {
        return grafoActivo != null;
    }

    public boolean existeGrafo(String nombre) {
        return grafos.containsKey(nombre);
    }

    public ArrayList<String> getNombres() {
        return new ArrayList<>(grafos.keySet());
    }

    public int getCantidadGrafos() {
        return grafos.size();
    }

}
