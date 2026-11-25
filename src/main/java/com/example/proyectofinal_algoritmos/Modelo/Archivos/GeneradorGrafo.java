package com.example.proyectofinal_algoritmos.Modelo.Archivos;

import com.example.proyectofinal_algoritmos.Modelo.Grafos.Grafo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

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

    /**
     * Genera conexiones aleatorias de un grafo conexo, sin duplicados ni lazos.
     * @param numVertices número de vértices (mínimo 2)
     * @param numAristas número de aristas (entre numVertices-1 y el máximo posible)
     * @param dirigido si el grafo es dirigido
     * @param pesoMinimo peso mínimo (no negativo)
     * @param pesoMaximo peso máximo
     * @param semilla semilla del generador aleatorio
     * @return lista de conexiones generadas
     */
    public ArrayList<ConexionCSV> generarConexionesAleatorias(int numVertices, int numAristas,
                                                             boolean dirigido, int pesoMinimo,
                                                             int pesoMaximo, long semilla) {
        validarParametros(numVertices, numAristas, dirigido, pesoMinimo, pesoMaximo);
        Random random = new Random(semilla);
        String[] nombres = generarNombres(numVertices);

        // Orden aleatorio de los vértices (Fisher-Yates)
        int[] orden = new int[numVertices];
        for (int i = 0; i < numVertices; i++) orden[i] = i;
        for (int i = numVertices - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int aux = orden[i];
            orden[i] = orden[j];
            orden[j] = aux;
        }

        ArrayList<ConexionCSV> conexiones = new ArrayList<>(numAristas);
        HashSet<String> usadas = new HashSet<>();

        // Árbol que une a todos los vértices para que el grafo sea conexo
        for (int i = 1; i < numVertices; i++) {
            String origen = nombres[orden[random.nextInt(i)]];
            String destino = nombres[orden[i]];
            agregarSiNoExiste(conexiones, usadas, origen, destino,
                    pesoAleatorio(random, pesoMinimo, pesoMaximo), dirigido);
        }

        // Aristas extra al azar
        while (conexiones.size() < numAristas) {
            int a = random.nextInt(numVertices);
            int b = random.nextInt(numVertices);
            if (a == b) {
                continue;
            }
            agregarSiNoExiste(conexiones, usadas, nombres[a], nombres[b],
                    pesoAleatorio(random, pesoMinimo, pesoMaximo), dirigido);
        }
        return conexiones;
    }

    /**
     * Genera un grafo aleatorio conexo sin pasar por un archivo.
     * @param nombre nombre del grafo
     * @param numVertices número de vértices
     * @param numAristas número de aristas
     * @param dirigido si el grafo es dirigido
     * @param semilla semilla del generador aleatorio
     * @param mapa mapa donde se registran los nombres de los vértices
     * @return grafo generado
     */
    public Grafo generarGrafoAleatorio(String nombre, int numVertices, int numAristas,
                                       boolean dirigido, long semilla, MapaVertices mapa) {
        ArrayList<ConexionCSV> conexiones =
                generarConexionesAleatorias(numVertices, numAristas, dirigido, 1, 500, semilla);
        return construirGrafo(nombre, dirigido, conexiones, mapa);
    }

    /**
     * Calcula el máximo de aristas posibles sin duplicados ni lazos.
     * @param numVertices número de vértices
     * @param dirigido si el grafo es dirigido
     * @return máximo número de aristas
     */
    public static long maximoAristas(int numVertices, boolean dirigido) {
        long n = numVertices;
        return dirigido ? n * (n - 1) : n * (n - 1) / 2;
    }

    private void validarParametros(int numVertices, int numAristas, boolean dirigido,
                                   int pesoMinimo, int pesoMaximo) {
        if (numVertices < 2) {
            throw new IllegalArgumentException("Se necesitan al menos 2 vértices.");
        }
        if (numAristas < numVertices - 1) {
            throw new IllegalArgumentException("Se necesitan al menos " + (numVertices - 1)
                    + " aristas para que todos los vértices aparezcan en el archivo.");
        }
        if (numAristas > maximoAristas(numVertices, dirigido)) {
            throw new IllegalArgumentException("Con " + numVertices + " vértices el máximo es "
                    + maximoAristas(numVertices, dirigido) + " aristas.");
        }
        if (pesoMinimo < 0 || pesoMaximo < pesoMinimo) {
            throw new IllegalArgumentException("Rango de pesos inválido.");
        }
    }

    private String[] generarNombres(int numVertices) {
        int digitos = String.valueOf(numVertices).length();
        String formato = PREFIJO_VERTICE + "%0" + digitos + "d";
        String[] nombres = new String[numVertices];
        for (int i = 0; i < numVertices; i++) {
            nombres[i] = String.format(formato, i + 1);
        }
        return nombres;
    }

    private int pesoAleatorio(Random random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    private void agregarSiNoExiste(ArrayList<ConexionCSV> conexiones, HashSet<String> usadas,
                                   String origen, String destino, int peso, boolean dirigido) {
        ConexionCSV c = new ConexionCSV(origen, destino, peso);
        if (usadas.add(c.generarLlave(dirigido))) {
            conexiones.add(c);
        }
    }
}
