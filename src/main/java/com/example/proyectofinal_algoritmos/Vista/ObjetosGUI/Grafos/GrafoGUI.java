package com.example.proyectofinal_algoritmos.Vista.ObjetosGUI.Grafos;

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.HashMap;

public class GrafoGUI {
    private Pane contenedor;
    private double ancho;
    private double alto;
    private HashMap<Integer, VerticeGUI> vertices;
    private HashMap<String, AristaGUI> aristas;

    public GrafoGUI(double ancho, double alto) {
        this.ancho = ancho;
        this.alto = alto;

        contenedor = new Pane();
        contenedor.setPrefSize(ancho, alto);
        vertices = new HashMap<>();
        aristas = new HashMap<>();
    }

    public Pane getContenedor() {
        return contenedor;
    }

    public void dibujar(ArrayList<Integer> ids, int[][] conexiones, boolean dirigido) {
        limpiar();
        int n = ids.size();
        if (n == 0) return;

        double centroX = ancho / 2;
        double centroY = alto / 2;
        double margen = 40;
        double radioCirculo = Math.min(ancho, alto) / 2 - margen;
        // Con muchos vértices, cada círculo se hace más pequeño para no encimarse
        double radioVertice = Math.min(20, Math.PI * radioCirculo / n * 0.8);

        // 1. Crear los vértices en círculo (todavía sin agregarlos al dibujo)
        for (int i = 0; i < n; i++) {
            double angulo = 2 * Math.PI * i / n - Math.PI / 2;
            double x = centroX + radioCirculo * Math.cos(angulo);
            double y = centroY + radioCirculo * Math.sin(angulo);
            vertices.put(ids.get(i), new VerticeGUI(ids.get(i), radioVertice, x, y));
        }

        // 2. Aristas primero, para que los vértices queden encima de las líneas
        for (int i = 0; i < conexiones.length; i++) {
            int origen = conexiones[i][0];
            int destino = conexiones[i][1];
            int peso = conexiones[i][2];
            if (!vertices.containsKey(origen) || !vertices.containsKey(destino)) continue;

            AristaGUI arista = new AristaGUI(vertices.get(origen), vertices.get(destino), peso, dirigido);
            aristas.put(origen + "-" + destino, arista);
            if (!dirigido) {
                aristas.put(destino + "-" + origen, arista);
            }
            contenedor.getChildren().add(arista.getContenedor());
        }

        // 3. Vértices al final
        for (int i = 0; i < n; i++) {
            contenedor.getChildren().add(vertices.get(ids.get(i)).getContenedor());
        }
    }

    public void limpiar() {
        contenedor.getChildren().clear();
        vertices.clear();
        aristas.clear();
    }

    public void resaltarVertice(int id, boolean activo) {
        VerticeGUI vertice = vertices.get(id);
        if (vertice != null) vertice.resaltar(activo);
    }

    public void resaltarArista(int origen, int destino, boolean activo) {
        AristaGUI arista = aristas.get(origen + "-" + destino);
        if (arista != null) arista.resaltar(activo);
    }

    // Resalta los vértices de la ruta y las aristas entre vértices consecutivos
    public void resaltarRuta(ArrayList<Integer> ruta) {
        limpiarResaltado();
        for (int i = 0; i < ruta.size(); i++) {
            resaltarVertice(ruta.get(i), true);
            if (i > 0) resaltarArista(ruta.get(i - 1), ruta.get(i), true);
        }
    }

    public void limpiarResaltado() {
        for (VerticeGUI vertice : vertices.values()) {
            vertice.resaltar(false);
        }
        for (AristaGUI arista : aristas.values()) {
            arista.resaltar(false);
        }
    }
}
