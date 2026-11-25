package com.example.proyectofinal_algoritmos.Vista.ObjetosGUI.Grafos;

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Clase que modela a un grafo en la pantalla
 * @version 23/11/2026
 * @author Derek Ramón Garzón Vizcarra
 */
public class GrafoGUI {
    private Pane contenedor;
    private double ancho;
    private double alto;
    private HashMap<Integer, VerticeGUI> vertices;
    private HashMap<String, AristaGUI> aristas;
    private Consumer<Integer> accionClicVertice;

    /**
     * Constructor de la representación gráfica del Grafo
     * @param ancho Ancho dado al grafo
     * @param alto Alto dado al grafo
     */
    public GrafoGUI(double ancho, double alto) {
        this.ancho = ancho;
        this.alto = alto;

        contenedor = new Pane();
        contenedor.setPrefSize(ancho, alto);
        vertices = new HashMap<>();
        aristas = new HashMap<>();
    }

    /**
     * Getter del contenedor donde se obtiene el grafo
     * @return Objeto donde se contiene al grafo, teniendo en cuenta  lo que posea dentro
     */
    public Pane getContenedor() {
        return contenedor;
    }

    /**
     * Metodo que dibuja al grafo
     * @param ids ids de cada vertice
     * @param conexiones Matriz donde se indica la conexión de cada vertice
     * @param dirigido Boolean que indica si el grafo debe ser dirigido o no
     */
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

        // Creamos cada vertice sin añadirlos
        for (int i = 0; i < n; i++) {
            double angulo = 2 * Math.PI * i / n - Math.PI / 2;
            double x = centroX + radioCirculo * Math.cos(angulo);
            double y = centroY + radioCirculo * Math.sin(angulo);
            vertices.put(ids.get(i), new VerticeGUI(ids.get(i), radioVertice, x, y));

            int idActual = ids.get(i);
            VerticeGUI vGui = new VerticeGUI(idActual, radioVertice, x, y);
            vGui.alHacerClic(() -> {
                if (accionClicVertice != null) {
                    accionClicVertice.accept(idActual);
                }
            });
            vertices.put(idActual, vGui);
        }

        // Luego se crean y dibujan las aristas
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

        // Se añaden los vertices para encimarlos a las aristas
        for (int i = 0; i < n; i++) {
            contenedor.getChildren().add(vertices.get(ids.get(i)).getContenedor());
        }
    }

    /**
     * Metodo que limpia el grafo, con los vertices y aristas que tuviera adentro
     */
    public void limpiar() {
        contenedor.getChildren().clear();
        vertices.clear();
        aristas.clear();
    }

    /**
     * Metodo que resalta un vertice
     * @param id Id del vertice
     * @param activo Boolean que indica si el vertice debe resaltarse
     */
    public void resaltarVertice(int id, boolean activo) {
        VerticeGUI vertice = vertices.get(id);
        if (vertice != null) vertice.resaltar(activo);
    }

    /**
     * Metodo que resalta un arista
     * @param origen Vertice de origen del arista
     * @param destino Vertice de destino del arista
     * @param activo Boolean que indica si el arista debe resaltarse
     */
    public void resaltarArista(int origen, int destino, boolean activo) {
        AristaGUI arista = aristas.get(origen + "-" + destino);
        if (arista != null) arista.resaltar(activo);
    }

    /**
     * Metodo que resalta una ruta dada
     * @param ruta Ruta que posee los ids de los vertices a resaltar
     */
    public void resaltarRuta(ArrayList<Integer> ruta) {
        limpiarResaltado();
        for (int i = 0; i < ruta.size(); i++) {
            resaltarVertice(ruta.get(i), true);
            if (i > 0) resaltarArista(ruta.get(i - 1), ruta.get(i), true);
        }
    }

    /**
     * Metodo que limpia los vertices y aristas resaltados
     */
    public void limpiarResaltado() {
        for (VerticeGUI vertice : vertices.values()) {
            vertice.resaltar(false);
        }
        for (AristaGUI arista : aristas.values()) {
            arista.resaltar(false);
        }
    }

    /**
     * Metodo que hace que el vertice realice una acción al darse click
     * @param accion Accion a realizar, esta debe tener obligatoriamente un parametro de tipo entero en el controlador
     */
    public void alHacerClicVertice(Consumer<Integer> accion) {
        this.accionClicVertice = accion;
    }
}
