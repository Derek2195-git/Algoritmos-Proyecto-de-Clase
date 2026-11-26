package com.example.proyectofinal_algoritmos.Modelo.Analisis;

import com.example.proyectofinal_algoritmos.Modelo.Archivos.GestorArchivo;
import com.example.proyectofinal_algoritmos.Modelo.Archivos.ResultadoImportacion;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Mide tiempos de ejecución de los algoritmos y guarda las mediciones.
 */
public class MedidorTiempos {
    public static final String LECTURA_CSV = "Lectura CSV";
    public static final String CONSTRUCCION_GRAFO = "Construcción grafo";
    public static final String IMPORTACION_TOTAL = "Importación total";

    private final ArrayList<Medicion> mediciones;
    private int repeticionesPorDefecto;
    private int calentamiento;

    public MedidorTiempos() {
        this(5, 2);
    }

    /**
     * Crea un medidor de tiempos.
     * @param repeticionesPorDefecto ejecuciones que se promedian
     * @param calentamiento ejecuciones previas que no se miden
     */
    public MedidorTiempos(int repeticionesPorDefecto, int calentamiento) {
        mediciones = new ArrayList<>();
        this.repeticionesPorDefecto = Math.max(1, repeticionesPorDefecto);
        this.calentamiento = Math.max(0, calentamiento);
    }

    /**
     * Ejecuta una tarea una vez y regresa cuánto tardó, sin guardar la medición.
     * @param tarea código a medir
     * @return tiempo en nanosegundos
     */
    public static long cronometrar(Runnable tarea) {
        long inicio = System.nanoTime();
        tarea.run();
        return System.nanoTime() - inicio;
    }

    /**
     * Mide una tarea que no modifica sus datos (por ejemplo BFS, DFS o Dijkstra).
     * @param algoritmo nombre del algoritmo
     * @param tamanio tamaño de los datos
     * @param datos descripción del conjunto de datos
     * @param tarea código a medir
     * @return medición registrada
     */
    public Medicion medir(String algoritmo, int tamanio, String datos, Runnable tarea) {
        return medir(algoritmo, tamanio, datos, () -> null, ignorado -> tarea.run(),
                repeticionesPorDefecto);
    }

    /**
     * Mide una tarea que necesita datos nuevos en cada repetición.
     * @param algoritmo nombre del algoritmo
     * @param tamanio tamaño de los datos
     * @param datos descripción del conjunto de datos
     * @param preparar genera la entrada de cada ejecución (no se mide)
     * @param tarea código a medir, recibe lo que generó preparar
     * @param repeticiones ejecuciones que se promedian
     * @param <T> tipo de la entrada
     * @return medición registrada
     */
    public <T> Medicion medir(String algoritmo, int tamanio, String datos,
                              Supplier<T> preparar, Consumer<T> tarea, int repeticiones) {
        if (repeticiones < 1) repeticiones = 1;

        for (int i = 0; i < calentamiento; i++) {
            tarea.accept(preparar.get());
        }

        long total = 0;
        for (int i = 0; i < repeticiones; i++) {
            T entrada = preparar.get();
            long inicio = System.nanoTime();
            tarea.accept(entrada);
            total += System.nanoTime() - inicio;
        }

        Medicion m = new Medicion(algoritmo, tamanio, total / repeticiones, repeticiones, datos);
        mediciones.add(m);
        return m;
    }

    /**
     * Mide por separado la lectura del CSV, la construcción del grafo y la importación total.
     * @param gestor gestor de archivos
     * @param archivo archivo CSV a importar
     * @param dirigido si el grafo es dirigido
     * @param repeticiones veces que se importa el archivo para promediar
     * @return las tres mediciones registradas, o lista vacía si el archivo no es válido
     */
    public ArrayList<Medicion> medirImportacion(GestorArchivo gestor, File archivo,
                                                boolean dirigido, int repeticiones) {
        ArrayList<Medicion> nuevas = new ArrayList<>();
        if (repeticiones < 1) {
            repeticiones = 1;
        }

        ResultadoImportacion prueba = null;
        for (int i = 0; i < Math.max(1, calentamiento); i++) {
            prueba = gestor.importarGrafo(archivo, dirigido);
        }
        if (prueba == null || !prueba.esExitosa()) {
            return nuevas;
        }

        String nombreGrafo = GestorArchivo.obtenerNombreSinExtension(archivo);
        long totalLectura = 0;
        long totalConstruccion = 0;
        for (int i = 0; i < repeticiones; i++) {
            long inicio = System.nanoTime();
            ResultadoImportacion r = gestor.leerConexiones(archivo, dirigido);
            long finLectura = System.nanoTime();
            gestor.getGenerador().construir(r, nombreGrafo);
            long finConstruccion = System.nanoTime();
            totalLectura += finLectura - inicio;
            totalConstruccion += finConstruccion - finLectura;
        }

        int tamanio = prueba.getNumAristas();
        String datos = archivo.getName() + " (V=" + prueba.getNumVertices()
                + ", E=" + prueba.getNumAristas() + ")";
        long lectura = totalLectura / repeticiones;
        long construccion = totalConstruccion / repeticiones;

        nuevas.add(new Medicion(LECTURA_CSV, tamanio, lectura, repeticiones, datos));
        nuevas.add(new Medicion(CONSTRUCCION_GRAFO, tamanio, construccion, repeticiones, datos));
        nuevas.add(new Medicion(IMPORTACION_TOTAL, tamanio, lectura + construccion, repeticiones, datos));
        mediciones.addAll(nuevas);
        return nuevas;
    }

    public void agregar(Medicion medicion) {
        if (medicion != null) {
            mediciones.add(medicion);
        }
    }

    public ArrayList<Medicion> getMediciones() {
        return mediciones;
    }

    /**
     * Obtiene las mediciones de un algoritmo en el orden en que se tomaron.
     * @param algoritmo nombre del algoritmo
     * @return mediciones del algoritmo
     */
    public ArrayList<Medicion> filtrarPorAlgoritmo(String algoritmo) {
        ArrayList<Medicion> resultado = new ArrayList<>();
        for (int i = 0; i < mediciones.size(); i++) {
            if (mediciones.get(i).getAlgoritmo().equals(algoritmo)) {
                resultado.add(mediciones.get(i));
            }
        }
        return resultado;
    }

    /**
     * Obtiene los nombres de los algoritmos medidos sin repetir.
     * @return nombres de los algoritmos
     */
    public ArrayList<String> getAlgoritmos() {
        ArrayList<String> nombres = new ArrayList<>();
        for (int i = 0; i < mediciones.size(); i++) {
            String nombre = mediciones.get(i).getAlgoritmo();
            if (!nombres.contains(nombre)) {
                nombres.add(nombre);
            }
        }
        return nombres;
    }

    public void limpiar() {
        mediciones.clear();
    }

    public int getRepeticionesPorDefecto() {
        return repeticionesPorDefecto;
    }

    public void setRepeticionesPorDefecto(int repeticiones) {
        this.repeticionesPorDefecto = Math.max(1, repeticiones);
    }

    public int getCalentamiento() {
        return calentamiento;
    }

    public void setCalentamiento(int calentamiento) {
        this.calentamiento = Math.max(0, calentamiento);
    }

    /**
     * Genera una tabla de texto con todas las mediciones.
     * @return tabla de mediciones
     */
    public String generarTabla() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.US,"%-22s %10s %13s %6s  %s%n", "Algoritmo", "Tamaño", "Tiempo (ms)", "Reps",
                "Datos (V = Vertices, E = Aristas)"));
        sb.append("_".repeat(82)).append('\n');
        for (int i = 0; i < mediciones.size(); i++) {
            Medicion m = mediciones.get(i);
            sb.append(String.format(Locale.US, "%-22s %10d %13.4f %6d  %s%n",
                    m.getAlgoritmo(), m.getTamanio(), m.getTiempoMilisegundos(),
                    m.getRepeticiones(), m.getDatos()));
        }
        return sb.toString();
    }

    /**
     * Exporta las mediciones a un archivo CSV.
     * @param gestor gestor de archivos que escribe el archivo
     * @param archivo archivo destino
     * @throws IOException si no se puede escribir el archivo
     */
    public void exportarCSV(GestorArchivo gestor, File archivo) throws IOException {
        ArrayList<String> lineas = new ArrayList<>();
        for (int i = 0; i < mediciones.size(); i++) {
            Medicion m = mediciones.get(i);
            lineas.add(GestorArchivo.escaparCampo(m.getAlgoritmo()) + "," + m.getTamanio() + ","
                    + m.getTiempoNanos() + ","
                    + String.format(Locale.US, "%.4f", m.getTiempoMilisegundos()) + ","
                    + m.getRepeticiones() + "," + GestorArchivo.escaparCampo(m.getDatos()));
        }
        gestor.escribirLineas(archivo, "algoritmo,tamanio,tiempo_ns,tiempo_ms,repeticiones,datos", lineas);
    }

}
