package com.example.proyectofinal_algoritmos.Vista.ObjetosGUI.Grafos;

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Clase que modela a un grafo en la pantalla
 * @version 24/11/2026
 * @author Derek Ramón Garzón Vizcarra
 */
public class GrafoGUI {
    private Pane contenedor;
    private HashMap<Integer, VerticeGUI> vertices;
    private HashMap<String, AristaGUI> aristas;
    private Consumer<Integer> accionClicVertice;

    // Datos del último grafo recibido, para poder volver a acomodarlo
    private ArrayList<Integer> ids;
    private int[][] conexiones;
    private boolean dirigido;
    private ArrayList<Integer> rutaActual;

    /**
     * Constructor de la representación gráfica del Grafo
     * @param ancho Ancho preferido inicial del área de dibujo
     * @param alto Alto preferido inicial del área de dibujo
     */
    public GrafoGUI(double ancho, double alto) {
        contenedor = new Pane();
        contenedor.setPrefSize(ancho, alto);
        contenedor.setMinSize(300, 260);
        vertices = new HashMap<>();
        aristas = new HashMap<>();
        ids = new ArrayList<>();
        conexiones = new int[0][3];
        rutaActual = new ArrayList<>();

        // Si cambia el tamaño del área, se vuelve a acomodar para seguir centrado
        contenedor.widthProperty().addListener((obs, anterior, nuevo) -> redibujar());
        contenedor.heightProperty().addListener((obs, anterior, nuevo) -> redibujar());
    }

    /**
     * Getter del contenedor donde se obtiene el grafo
     * @return Objeto donde se contiene al grafo, teniendo en cuenta lo que posea dentro
     */
    public Pane getContenedor() {
        return contenedor;
    }

    /**
     * Metodo que dibuja un grafo nuevo, descartando cualquier resaltado anterior
     * @param ids ids de cada vertice
     * @param conexiones Matriz donde se indica la conexión de cada vertice
     * @param dirigido Boolean que indica si el grafo debe ser dirigido o no
     */
    public void dibujar(ArrayList<Integer> ids, int[][] conexiones, boolean dirigido) {
        this.ids = new ArrayList<>(ids);
        this.conexiones = conexiones;
        this.dirigido = dirigido;
        this.rutaActual = new ArrayList<>();
        redibujar();
    }

    // Vuelve a crear el dibujo, centrado en el tamaño actual del contenedor
    private void redibujar() {
        contenedor.getChildren().clear();
        vertices.clear();
        aristas.clear();

        int n = ids.size();
        if (n == 0) return;

        // Antes de que JavaFX acomode el contenedor su tamaño es 0, así que se usa el preferido
        double ancho = contenedor.getWidth() > 0 ? contenedor.getWidth() : contenedor.getPrefWidth();
        double alto = contenedor.getHeight() > 0 ? contenedor.getHeight() : contenedor.getPrefHeight();

        double centroX = ancho / 2;
        double centroY = alto / 2;
        double margen = 40;
        double radioCirculo = Math.max(Math.min(ancho, alto) / 2 - margen, 20);
        // Con muchos vértices, cada círculo se hace más pequeño para no encimarse
        double radioVertice = Math.min(20, Math.PI * radioCirculo / n * 0.8);

        // Creamos cada vertice sin añadirlos
        for (int i = 0; i < n; i++) {
            double x = centroX;
            double y = centroY;
            // Si solo hay un vértice, se queda en el centro
            if (n > 1) {
                double angulo = 2 * Math.PI * i / n - Math.PI / 2;
                x += radioCirculo * Math.cos(angulo);
                y += radioCirculo * Math.sin(angulo);
            }

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

        // Si había una ruta resaltada, se vuelve a pintar
        pintarRuta();
    }

    /**
     * Metodo que limpia el grafo, con los vertices y aristas que tuviera adentro
     */
    public void limpiar() {
        contenedor.getChildren().clear();
        vertices.clear();
        aristas.clear();
        ids = new ArrayList<>();
        conexiones = new int[0][3];
        rutaActual = new ArrayList<>();
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
     * Metodo que resalta una ruta dada. Se recuerda para volver a pintarla
     * si el dibujo se reacomoda al cambiar el tamaño de la ventana.
     * @param ruta Ruta que posee los ids de los vertices a resaltar
     */
    public void resaltarRuta(ArrayList<Integer> ruta) {
        rutaActual = new ArrayList<>(ruta);
        pintarRuta();
    }

    /**
     * Metodo que limpia los vertices y aristas resaltados
     */
    public void limpiarResaltado() {
        rutaActual = new ArrayList<>();
        pintarRuta();
    }

    // Quita todo el resaltado y pinta solo el de la ruta guardada
    private void pintarRuta() {
        for (VerticeGUI vertice : vertices.values()) {
            vertice.resaltar(false);
        }
        for (AristaGUI arista : aristas.values()) {
            arista.resaltar(false);
        }
        for (int i = 0; i < rutaActual.size(); i++) {
            resaltarVertice(rutaActual.get(i), true);
            if (i > 0) resaltarArista(rutaActual.get(i - 1), rutaActual.get(i), true);
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