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
        if (vertices.containsKey(id)) {
            return false;
        }
        vertices.put(id, new Vertice(id));
        orden.add(id);
        return true;
    }

    public Vertice eliminarVertice(int id) {
        if (!validarVertice(id)) return null;
        Vertice verticeEliminado = vertices.remove(id);
        orden.remove(Integer.valueOf(id));
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

    public boolean agregarArista(int origen, int destino, int peso) {
        if (!validarVertice(origen) || !validarVertice(destino)) return false;
        // No se pueden conexiones del mismo vertice
        if (origen == destino) return false;
        // No hay pesos negativos
        if (peso < 0) return false;
        // La arista no existe
        if (existeArista(origen, destino)) return false;
        vertices.get(destino).agregarArista(new Arista(destino, origen, peso));
        if (!dirigido) {
            vertices.get(destino).agregarArista(new Arista(destino, origen, peso));
        }
        return true;
    }

    public boolean modificarPeso(int origen, int destino, int peso) {
        if (peso<0) return false;
        Arista arista = buscarAristaExistente(origen, destino);
        if (arista == null) return false;
        arista.setPeso(peso);
        if (!dirigido) vertices.get(destino).buscarArista(origen).setPeso(peso);
        return true;
    }

    public void eliminarArista(int origen, int destino) {
        if (buscarAristaExistente(origen, destino) == null) return;

        vertices.get(origen).eliminarArista(destino);
        if (!dirigido) vertices.get(destino).eliminarArista(origen);
    }

    public boolean existeArista(int origen, int destino) {
        return vertices.containsKey(origen) && vertices.get(origen).buscarArista(destino) != null;
    }

    public int obtenerPeso(int origen, int destino) {
        return buscarAristaExistente(origen, destino).getPeso();
    }

    public ArrayList<Arista> obtenerAristas() {
        ArrayList<Arista> resultado = new ArrayList<>();
        for (int i = 0; i < orden.size(); i++) {
            ArrayList<Arista> adyacentes =  vertices.get(orden.get(i)).getAristasAdyacentes();
            for (int j = 0; j < adyacentes.size(); j++) {
                Arista a = adyacentes.get(j);
                if (dirigido || a.getOrigen() < a.getDestino()) resultado.add(a);
            }
        }
        return resultado;
    }

    public int cantidadAristas() {
        int total = 0;
        for (Vertice v : vertices.values()) {
            total += v.getGrado();
        }
        return dirigido ? total : total/2;
    }

    public int[][] crearMatrizAdyacencia() {
        int n = orden.size();
        HashMap<Integer, Integer> indice = new HashMap<>();
        for (int i = 0; i < n; i++) {
            indice.put(orden.get(i), i);
        }
        int[][] matriz = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matriz[i][j] = -1;
            }
        }
        for (int i = 0; i < n; i++) {
            ArrayList<Arista> adyacencia = vertices.get(orden.get(i)).getAristasAdyacentes();
            for (Arista arista : adyacencia) {
                matriz[i][indice.get(arista.getDestino())] =
                        arista.getPeso();
            }
        }
        return matriz;
    }

    private boolean validarVertice(int id) {
        return vertices.containsKey(id);

    }

    private Arista buscarAristaExistente(int origen, int destino) {
        if (!validarVertice(origen) || !validarVertice(destino)) return null;
        Arista arista = vertices.get(origen).buscarArista(destino);
        return (arista == null) ? null : arista;
    }
}
