package com.example.proyectofinal_algoritmos.Modelo.Analisis;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Mide tiempos de ejecución de los algoritmos y guarda las mediciones.
 */
public class MedidorTiempos {
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
     * Mide una tarea que necesita datos nuevos en cada repetición (por ejemplo un ordenamiento).
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

    public void agregar(Medicion medicion) {
        if (medicion != null) mediciones.add(medicion);
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
            if (mediciones.get(i).getAlgoritmo().equals(algoritmo)) resultado.add(mediciones.get(i));
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
            if (!nombres.contains(nombre)) nombres.add(nombre);
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

}
