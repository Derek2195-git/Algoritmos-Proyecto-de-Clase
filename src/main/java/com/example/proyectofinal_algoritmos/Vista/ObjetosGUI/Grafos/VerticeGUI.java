package com.example.proyectofinal_algoritmos.Vista.ObjetosGUI.Grafos;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class VerticeGUI {
    private int id;
    private double radio;
    private Circle circulo;
    private Label textoId;
    private StackPane contenedor;

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

    public int getId() {
        return id;
    }

    public double getRadio() {
        return radio;
    }

    public double getCentroX() {
        return contenedor.getLayoutX() + radio;
    }

    public double getCentroY() {
        return contenedor.getLayoutY() + radio;
    }

    public void resaltar(boolean activo) {
        circulo.setFill(activo ? Color.SEAGREEN : Color.LIGHTBLUE);
    }

    public StackPane getContenedor() {
        return contenedor;
    }
}
