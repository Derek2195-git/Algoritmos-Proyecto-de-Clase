package com.example.proyectofinal_algoritmos.Modelo.Busquedas;

import com.example.proyectofinal_algoritmos.Modelo.Grafos.Arista;
import com.example.proyectofinal_algoritmos.Modelo.Grafos.Grafo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

// Algoritmo de Dijkstra para sacar la ruta mas barata entre origen y destino (solo pesos >= 0)
public class Dijkstra {

    private static final int INFINITO = Integer.MAX_VALUE;

    // Calcula la ruta minima desde origen hasta destino; si no hay camino regresa existeRuta() == false
    public static ResultadoRuta calcular(Grafo grafo, int origen, int destino) {
        if (grafo == null || !grafo.existeVertice(origen) || !grafo.existeVertice(destino)) {
            return ResultadoRuta.sinRuta(origen, destino);
        }

        // Guarda la mejor distancia encontrada hasta ahora para cada vertice
        HashMap<Integer, Integer> distancia = new HashMap<>();
        // Guarda de que vertice venimos para poder armar el camino al final
        HashMap<Integer, Integer> anterior = new HashMap<>();
        // Vertices que ya tienen su distancia minima fija
        HashSet<Integer> visitados = new HashSet<>();
        // Cola de prioridad con {distancia, idVertice} para sacar siempre el mas cercano
        PriorityQueue<int[]> cola = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));

        for (int id : grafo.obtenerIds()) {
            distancia.put(id, INFINITO);
        }
        distancia.put(origen, 0);
        cola.add(new int[]{0, origen});

        while (!cola.isEmpty()) {
            int[] par = cola.poll();
            int actual = par[1];

            // Si ya lo procesamos con una distancia menor antes, se brinca
            if (visitados.contains(actual)) continue;
            visitados.add(actual);

            // En cuanto sale el destino de la cola ya tenemos su costo minimo
            if (actual == destino) break;

            for (Arista arista : grafo.getVertice(actual).getAristasAdyacentes()) {
                int vecino = arista.getDestino();
                if (visitados.contains(vecino)) continue;

                // Revisa si sale mas barato llegar al vecino pasando por actual
                int nuevaDistancia = distancia.get(actual) + arista.getPeso();
                if (nuevaDistancia < distancia.get(vecino)) {
                    distancia.put(vecino, nuevaDistancia);
                    anterior.put(vecino, actual);
                    cola.add(new int[]{nuevaDistancia, vecino});
                }
            }
        }

        if (distancia.get(destino) == INFINITO) {
            return ResultadoRuta.sinRuta(origen, destino);
        }

        return new ResultadoRuta(origen, destino,
                reconstruirRuta(anterior, origen, destino), distancia.get(destino));
    }

    // Arma la lista de vertices yendo hacia atras desde el destino hasta el origen
    private static ArrayList<Integer> reconstruirRuta(HashMap<Integer, Integer> anterior,
                                                      int origen, int destino) {
        ArrayList<Integer> alReves = new ArrayList<>();
        int actual = destino;
        alReves.add(actual);
        while (actual != origen) {
            actual = anterior.get(actual);
            alReves.add(actual);
        }

        ArrayList<Integer> ruta = new ArrayList<>();
        for (int i = alReves.size() - 1; i >= 0; i--) {
            ruta.add(alReves.get(i));
        }
        return ruta;
    }
}
