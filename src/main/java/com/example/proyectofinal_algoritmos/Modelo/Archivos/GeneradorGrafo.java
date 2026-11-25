package com.example.proyectofinal_algoritmos.Modelo.Archivos;

import com.example.proyectofinal_algoritmos.Modelo.Grafos.Grafo;

import java.util.ArrayList;

/**
 * Construye grafos a partir de conexiones del CSV y genera conexiones aleatorias.
 */
public class GeneradorGrafo {
    public static final String PREFIJO_VERTICE = "Ubicacion_";

    /**
     * Construye un grafo a partir de conexiones ya validadas.
     * @param nombre nombre del grafo
     * @param dirigido si el grafo es dirigido
     * @param conexiones conexiones válidas
     * @param mapa mapa donde se registran los nombres de los vértices
     * @return grafo construido
     */
    public Grafo construirGrafo(String nombre, boolean dirigido,
                                ArrayList<ConexionCSV> conexiones, MapaVertices mapa) {
        Grafo grafo = new Grafo(nombre, dirigido);
        for (int i = 0; i < conexiones.size(); i++) {
            ConexionCSV c = conexiones.get(i);
            int idOrigen = registrarVertice(grafo, mapa, c.getOrigen());
            int idDestino = registrarVertice(grafo, mapa, c.getDestino());
            grafo.agregarArista(idOrigen, idDestino, c.getPeso());
        }
        return grafo;
    }

    /**
     * Construye el grafo de un resultado de importación y lo guarda en él.
     * @param resultado resultado de la lectura del CSV
     * @param nombreGrafo nombre del grafo
     */
    public void construir(ResultadoImportacion resultado, String nombreGrafo) {
        MapaVertices mapa = new MapaVertices();
        Grafo grafo = construirGrafo(nombreGrafo, resultado.isDirigido(),
                resultado.getConexionesValidas(), mapa);
        resultado.setGrafo(grafo, mapa);
    }

    private int registrarVertice(Grafo grafo, MapaVertices mapa, String nombre) {
        int id = mapa.obtenerOAsignarId(nombre);
        if (!grafo.existeVertice(id)) {
            grafo.agregarVertice(id);
        }
        return id;
    }

}
