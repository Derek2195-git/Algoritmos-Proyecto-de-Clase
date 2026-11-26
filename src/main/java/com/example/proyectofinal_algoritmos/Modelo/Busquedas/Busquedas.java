package com.example.proyectofinal_algoritmos.Modelo.Busquedas;

import com.example.proyectofinal_algoritmos.Modelo.Grafos.Arista;
import com.example.proyectofinal_algoritmos.Modelo.Grafos.Grafo;

import java.util.ArrayList;
import java.util.HashSet;

// Recorridos BFS y DFS para el grafo. Regresan la lista de ids en el orden en que se visitan
public class Busquedas {

    // Busqueda en anchura (va visitando por niveles usando una cola)
    public static ArrayList<Integer> bfs(Grafo grafo, int inicio) {
        ArrayList<Integer> orden = new ArrayList<>();
        if (grafo == null || !grafo.existeVertice(inicio)) return orden;

        HashSet<Integer> visitados = new HashSet<>();
        // Como cada vertice entra una sola vez, la cola no necesita mas espacio que V
        ColaSimple<Integer> cola = new ColaSimple<>(grafo.cantidadVertices());

        visitados.add(inicio);
        cola.insertarDato(inicio);

        while (!cola.estaVacia()) {
            int actual = cola.eliminarDato();
            orden.add(actual);

            for (Arista arista : grafo.getVertice(actual).getAristasAdyacentes()) {
                int vecino = arista.getDestino();
                // Se marca al meterlo a la cola para no encolarlo repetido
                if (!visitados.contains(vecino)) {
                    visitados.add(vecino);
                    cola.insertarDato(vecino);
                }
            }
        }
        return orden;
    }

    // Busqueda en profundidad (se mete hasta el fondo de cada rama antes de regresar)
    public static ArrayList<Integer> dfs(Grafo grafo, int inicio) {
        ArrayList<Integer> orden = new ArrayList<>();
        if (grafo == null || !grafo.existeVertice(inicio)) return orden;

        dfsRecursivo(grafo, inicio, new HashSet<>(), orden);
        return orden;
    }

    private static void dfsRecursivo(Grafo grafo, int actual,
                                     HashSet<Integer> visitados, ArrayList<Integer> orden) {
        visitados.add(actual);
        orden.add(actual);

        for (Arista arista : grafo.getVertice(actual).getAristasAdyacentes()) {
            int vecino = arista.getDestino();
            if (!visitados.contains(vecino)) {
                dfsRecursivo(grafo, vecino, visitados, orden);
            }
        }
    }
}
