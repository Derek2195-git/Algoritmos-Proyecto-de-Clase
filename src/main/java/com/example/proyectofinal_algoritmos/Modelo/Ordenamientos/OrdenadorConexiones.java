package com.example.proyectofinal_algoritmos.Modelo.Ordenamientos;

import com.example.proyectofinal_algoritmos.Modelo.Grafos.Arista;
import com.example.proyectofinal_algoritmos.Modelo.Grafos.Grafo;

import java.util.ArrayList;
import java.util.function.IntFunction;

// Pasa las aristas del grafo a un arreglo y las ordena con QuickSort o Shell Sort
public class OrdenadorConexiones {
    public static final String QUICKSORT = "QuickSort";
    public static final String SHELL_SORT = "Shell Sort";

    private final Ordenamientos<ConexionOrdenable> ordenamientos = new Ordenamientos<>();

    // Crea el arreglo de conexiones listo para ordenarse segun el criterio elegido
    public static ConexionOrdenable[] crearArreglo(Grafo grafo, IntFunction<String> nombres, int criterio) {
        ArrayList<Arista> aristas = grafo.obtenerAristas();
        ConexionOrdenable[] arreglo = new ConexionOrdenable[aristas.size()];
        for (int i = 0; i < aristas.size(); i++) {
            Arista a = aristas.get(i);
            arreglo[i] = new ConexionOrdenable(a, nombres.apply(a.getOrigen()),
                    nombres.apply(a.getDestino()), criterio);
        }
        return arreglo;
    }

    // Copia el arreglo para poder repetir las mediciones con los mismos datos sin ordenar
    public static ConexionOrdenable[] copiar(ConexionOrdenable[] original) {
        ConexionOrdenable[] copia = new ConexionOrdenable[original.length];
        for (int i = 0; i < original.length; i++) {
            copia[i] = original[i];
        }
        return copia;
    }

    // Ordena el arreglo usando Shell Sort o QuickSort
    public void ordenar(ConexionOrdenable[] arreglo, String algoritmo) {
        int n = arreglo.length;
        // Con 0 o 1 elementos ya esta ordenado (ademas QuickSort falla si n == 0)
        if (n < 2) return;
        if (SHELL_SORT.equals(algoritmo)) {
            ordenamientos.shellDirecto(arreglo, n);
        } else {
            ordenamientos.quickSort(arreglo, n);
        }
    }
}
