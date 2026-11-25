package com.example.proyectofinal_algoritmos.Vista.ObjetosGUI.Grafos;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Clase que modela a vertice en la vista
 * @version 23/11/2026
 * @author Derek Ramón Garzón Vizcarra
 */
public class VerticeGUI {
    private int id;
    private double radio;
    private Circle circulo;
    private Label textoId;
    private StackPane contenedor;

    /**
     * Constructor de la representación visual del vertice
     * @param id Id del vertice
     * @param radio Radio del vertice a dibujar
     * @param centroX Centro en X del vertice
     * @param centroY Centro en Y del vertice
     */
    public VerticeGUI(int id, double radio, double centroX, double centroY) {
        this.id = id;
        this.radio = radio;

        circulo = new Circle(radio);
        circulo.setFill(Color.LIGHTBLUE);
        circulo.setStroke(Color.STEELBLUE);

        textoId = new Label(String.valueOf(id));

        contenedor = new StackPane(circulo, textoId);
        contenedor.setLayoutX(centroX - radio);
        contenedor.setLayoutY(centroY - radio);

        contenedor.setPickOnBounds(false);
    }

    /**
     * Getter de la Id del vertice
     * @return Id del vertice
     */
    public int getId() {
        return id;
    }

    /**
     * Getter del radio del vertice
     * @return radio del vertice dibujado
     */
    public double getRadio() {
        return radio;
    }

    /**
     * Getter del lugar donde se ubica el centro en X del vertice
     * @return Ubicación en X donde se ubica el centro del vertice
     */
    public double getCentroX() {
        return contenedor.getLayoutX() + radio;
    }

    /**
     * Getter del lugar donde se ubica el centro en Y del vertice
     * @return Ubicación en Y donde se ubica el centro del vertice
     */
    public double getCentroY() {
        return contenedor.getLayoutY() + radio;
    }

    /**
     * Metodo que resalta el vertice
     * @param activo Boolean que indica si el vertice debe ser resaltado o no
     */
    public void resaltar(boolean activo) {
        circulo.setFill(activo ? Color.SEAGREEN : Color.LIGHTBLUE);
    }

    /**
     * Metodo que hace que el vertice realice una acción cuando es clickeado
     * @param accion Accion a realizar
     */
    public void alHacerClic(Runnable accion) {
        contenedor.setOnMouseClicked(e -> accion.run());
    }

    /**
     * Getter del contenedor donde se ubica el vertice
     * @return Contenedor donde se esta almacenando al vertice
     */
    public StackPane getContenedor() {
        return contenedor;
    }
}
