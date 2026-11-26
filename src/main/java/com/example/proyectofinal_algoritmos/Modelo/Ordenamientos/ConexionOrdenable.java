package com.example.proyectofinal_algoritmos.Modelo.Ordenamientos;

import com.example.proyectofinal_algoritmos.Modelo.Grafos.Arista;

// Envuelve una arista para poder ordenarla por peso o por nombre sin tocar la clase Arista
public class ConexionOrdenable implements Comparable<ConexionOrdenable> {
    public static final int POR_PESO = 0;
    public static final int POR_NOMBRE = 1;

    private final Arista arista;
    private final String nombreOrigen;
    private final String nombreDestino;
    private final int criterio;

    public ConexionOrdenable(Arista arista, String nombreOrigen, String nombreDestino, int criterio) {
        this.arista = arista;
        this.nombreOrigen = nombreOrigen;
        this.nombreDestino = nombreDestino;
        this.criterio = criterio;
    }

    public Arista getArista() {
        return arista;
    }

    public String getNombreOrigen() {
        return nombreOrigen;
    }

    public String getNombreDestino() {
        return nombreDestino;
    }

    public int getPeso() {
        return arista.getPeso();
    }

    @Override
    public int compareTo(ConexionOrdenable otra) {
        if (criterio == POR_PESO) {
            int c = Integer.compare(getPeso(), otra.getPeso());
            return c != 0 ? c : compararNombres(otra);
        }
        int c = compararNombres(otra);
        return c != 0 ? c : Integer.compare(getPeso(), otra.getPeso());
    }

    private int compararNombres(ConexionOrdenable otra) {
        int c = compararTexto(nombreOrigen, otra.nombreOrigen);
        return c != 0 ? c : compararTexto(nombreDestino, otra.nombreDestino);
    }

    // Si los dos textos son numeros enteros los compara como numero (para que "2" vaya antes que "10")
    private static int compararTexto(String a, String b) {
        if (esEntero(a) && esEntero(b)) {
            return Long.compare(Long.parseLong(a), Long.parseLong(b));
        }
        return a.compareToIgnoreCase(b);
    }

    private static boolean esEntero(String s) {
        if (s == null || s.isEmpty() || s.length() > 18) return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nombreOrigen + " -> " + nombreDestino + " (" + getPeso() + ")";
    }
}
