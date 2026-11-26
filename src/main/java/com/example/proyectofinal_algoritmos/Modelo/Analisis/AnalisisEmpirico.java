package com.example.proyectofinal_algoritmos.Modelo.Analisis;

import com.example.proyectofinal_algoritmos.Modelo.Archivos.ConexionCSV;
import com.example.proyectofinal_algoritmos.Modelo.Archivos.GeneradorGrafo;
import com.example.proyectofinal_algoritmos.Modelo.Archivos.GestorArchivo;
import com.example.proyectofinal_algoritmos.Modelo.Archivos.ResultadoImportacion;
import com.example.proyectofinal_algoritmos.Modelo.Busquedas.Busquedas;
import com.example.proyectofinal_algoritmos.Modelo.Busquedas.Dijkstra;
import com.example.proyectofinal_algoritmos.Modelo.Grafos.Grafo;
import com.example.proyectofinal_algoritmos.Modelo.Ordenamientos.ConexionOrdenable;
import com.example.proyectofinal_algoritmos.Modelo.Ordenamientos.OrdenadorConexiones;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

// Clase para correr las pruebas de tiempo con grafos de distintos tamanios
// Genera los CSV, mide lectura/construccion, busquedas y ordenamientos, y guarda todo en mediciones.csv
public class AnalisisEmpirico {
    public static final String BFS = "BFS";
    public static final String DFS = "DFS";
    public static final String DIJKSTRA = "Dijkstra";

    // Tamanios de prueba: {vertices, aristas}. El ultimo repite 1000 vertices con mas aristas
    public static final int[][] TAMANIOS = {
            {20, 40},
            {100, 300},
            {500, 1500},
            {1000, 3000},
            {1000, 10000}
    };

    private static final int REPETICIONES = 5;

    private final GestorArchivo gestor;
    private final MedidorTiempos medidor;
    private final OrdenadorConexiones ordenador;
    private final ArrayList<File> archivosGenerados;

    public AnalisisEmpirico(GestorArchivo gestor) {
        this.gestor = gestor;
        this.medidor = new MedidorTiempos(REPETICIONES, 2);
        this.ordenador = new OrdenadorConexiones();
        this.archivosGenerados = new ArrayList<>();
    }

    public MedidorTiempos getMedidor() {
        return medidor;
    }

    public ArrayList<File> getArchivosGenerados() {
        return archivosGenerados;
    }

    // Corre todas las pruebas y regresa el CSV con las mediciones
    public File ejecutar(File carpeta, boolean dirigido, Consumer<String> progreso) throws IOException {
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta " + carpeta.getAbsolutePath());
        }
        medidor.limpiar();
        archivosGenerados.clear();

        for (int[] t : TAMANIOS) {
            int v = t[0];
            int e = (int) Math.min(t[1], GeneradorGrafo.maximoAristas(v, dirigido));
            avisar(progreso, "Midiendo grafo con " + v + " vértices y " + e + " aristas...");

            File archivo = new File(carpeta, "grafo_" + v + "v_" + e + "a.csv");
            generarArchivo(archivo, v, e, dirigido, 1000L * v + e);
            archivosGenerados.add(archivo);
            medirGrafo(archivo, dirigido);
        }

        File resultados = new File(carpeta, "mediciones.csv");
        medidor.exportarCSV(gestor, resultados);
        return resultados;
    }

    // Genera un CSV aleatorio conexo con vertices numerados del 1 al v
    private void generarArchivo(File archivo, int v, int e, boolean dirigido, long semilla) throws IOException {
        ArrayList<ConexionCSV> generadas = gestor.getGenerador()
                .generarConexionesAleatorias(v, e, dirigido, 1, 500, semilla);
        ArrayList<ConexionCSV> numericas = new ArrayList<>(generadas.size());
        for (ConexionCSV c : generadas) {
            numericas.add(new ConexionCSV(aNumero(c.getOrigen()), aNumero(c.getDestino()), c.getPeso()));
        }
        gestor.guardarConexiones(numericas, archivo);
    }

    // Quita el prefijo para dejar solo el numero, ej: "Ubicacion_0007" -> "7"
    private static String aNumero(String nombre) {
        String numero = nombre.substring(GeneradorGrafo.PREFIJO_VERTICE.length());
        return String.valueOf(Integer.parseInt(numero));
    }

    private void medirGrafo(File archivo, boolean dirigido) {
        // Mide lectura del CSV y construccion del grafo
        medidor.medirImportacion(gestor, archivo, dirigido, REPETICIONES);

        ResultadoImportacion r = gestor.importarGrafo(archivo, dirigido);
        if (!r.esExitosa()) return;
        Grafo grafo = r.getGrafo();
        int v = grafo.cantidadVertices();
        int e = grafo.cantidadAristas();
        String datos = archivo.getName() + " (V=" + v + ", E=" + e + ")";

        ArrayList<Integer> ids = grafo.obtenerIds();
        int inicio = ids.get(0);
        int destino = ids.get(ids.size() - 1);

        medidor.medir(BFS, v, datos, () -> Busquedas.bfs(grafo, inicio));
        medidor.medir(DFS, v, datos, () -> Busquedas.dfs(grafo, inicio));
        medidor.medir(DIJKSTRA, v, datos, () -> Dijkstra.calcular(grafo, inicio, destino));

        // Se usa una copia del mismo arreglo para comparar los dos ordenamientos en igualdad
        ConexionOrdenable[] original = OrdenadorConexiones.crearArreglo(grafo, String::valueOf,
                ConexionOrdenable.POR_PESO);
        medidor.medir(OrdenadorConexiones.QUICKSORT, e, datos,
                () -> OrdenadorConexiones.copiar(original),
                arreglo -> ordenador.ordenar(arreglo, OrdenadorConexiones.QUICKSORT), REPETICIONES);
        medidor.medir(OrdenadorConexiones.SHELL_SORT, e, datos,
                () -> OrdenadorConexiones.copiar(original),
                arreglo -> ordenador.ordenar(arreglo, OrdenadorConexiones.SHELL_SORT), REPETICIONES);
    }

    // Saca una etiqueta corta como "V=100 E=300" para mostrar en la grafica
    public static String etiquetaDatos(Medicion m) {
        String d = m.getDatos();
        int i = d.indexOf("(V=");
        if (i < 0) return d;
        return d.substring(i + 1, d.length() - 1).replace(",", "");
    }

    private static void avisar(Consumer<String> progreso, String mensaje) {
        if (progreso != null) progreso.accept(mensaje);
    }
}
