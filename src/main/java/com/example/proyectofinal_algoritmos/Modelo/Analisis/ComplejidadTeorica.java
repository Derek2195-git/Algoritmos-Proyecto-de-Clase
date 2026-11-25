package com.example.proyectofinal_algoritmos.Modelo.Analisis;

/**
 * Enum que tiene la complejidad teórica de los algoritmos del proyecto.
 */
public enum ComplejidadTeorica {

    BFS("BFS (búsqueda en anchura)",
            "O(V + E)", "O(V + E)", "O(V + E)", "O(V)",
            "Cada vértice entra a la cola una vez y se recorren todas sus aristas."),

    DFS("DFS (búsqueda en profundidad)",
            "O(V + E)", "O(V + E)", "O(V + E)", "O(V)",
            "Cada vértice se visita una vez y se recorren todas sus aristas."),

    DIJKSTRA("Dijkstra (con PriorityQueue)",
            "O((V + E) log V)", "O((V + E) log V)", "O((V + E) log V)", "O(V + E)",
            "Cada arista puede insertar un elemento en la cola de prioridad con costo log V."),

    QUICKSORT("QuickSort",
            "O(n log n)", "O(n log n)", "O(n²)", "O(log n) a O(n)",
            "Con el primer elemento como pivote, un arreglo ya ordenado produce el peor caso."),

    SHELL_SORT("Shell Sort",
            "O(n log n)", "≈ O(n^1.5)", "O(n²)", "O(1)",
            "El caso promedio depende de la secuencia de incrementos."),

    LECTURA_CSV("Lectura y validación del CSV",
            "O(E)", "O(E)", "O(E)", "O(E)",
            "Cada línea se lee una vez y los duplicados se buscan en un HashMap en O(1)."),

    CONSTRUCCION_GRAFO("Construcción del grafo (lista de adyacencia)",
            "O(V + E)", "O(V + E·g)", "O(V·E)", "O(V + E)",
            "Agregar una arista revisa la lista de adyacencia del origen (g = grado máximo)."),

    MATRIZ_ADYACENCIA("Construcción de la matriz de adyacencia",
            "O(V²)", "O(V²)", "O(V²)", "O(V²)",
            "Hay que inicializar las V×V celdas aunque el grafo tenga pocas aristas.");

    private final String nombre;
    private final String mejorCaso;
    private final String casoPromedio;
    private final String peorCaso;
    private final String espacio;
    private final String explicacion;

    ComplejidadTeorica(String nombre, String mejorCaso, String casoPromedio,
                       String peorCaso, String espacio, String explicacion) {
        this.nombre = nombre;
        this.mejorCaso = mejorCaso;
        this.casoPromedio = casoPromedio;
        this.peorCaso = peorCaso;
        this.espacio = espacio;
        this.explicacion = explicacion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getMejorCaso() {
        return mejorCaso;
    }

    public String getCasoPromedio() {
        return casoPromedio;
    }

    public String getPeorCaso() {
        return peorCaso;
    }

    public String getEspacio() {
        return espacio;
    }

    public String getExplicacion() {
        return explicacion;
    }

    /**
     * Genera una tabla de texto con la complejidad de todos los algoritmos.
     * @return tabla de complejidades
     */
    public static String generarTabla() {
        StringBuilder sb = new StringBuilder();
        String formato = "%-45s %-18s %-18s %-18s %-16s%n";
        sb.append(String.format(formato, "Algoritmo", "Mejor caso", "Caso promedio", "Peor caso", "Espacio"));
        sb.append("-".repeat(118)).append('\n');
        for (ComplejidadTeorica c : values()) {
            sb.append(String.format(formato, c.nombre, c.mejorCaso, c.casoPromedio, c.peorCaso, c.espacio));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return nombre + " - peor caso " + peorCaso;
    }
}
