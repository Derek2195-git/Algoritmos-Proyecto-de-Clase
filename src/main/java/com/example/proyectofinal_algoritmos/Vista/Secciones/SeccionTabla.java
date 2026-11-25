package com.example.proyectofinal_algoritmos.Vista.Secciones;

import com.example.proyectofinal_algoritmos.Vista.ObjetosGUI.Tabla;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SeccionTabla {
    private VBox contenedor;
    private Tabla tabla;
    private Label lblTitulo;

    public SeccionTabla() {
        contenedor = new VBox(8);
        contenedor.setPadding(new Insets(10));
        contenedor.getStyleClass().add("seccion-tabla");

        lblTitulo = new Label("Estructura del Grafo (Lista de Conexiones)");
        lblTitulo.getStyleClass().add("titulo-seccion");

        tabla = new Tabla();
        VBox.setVgrow(tabla.getContenedor(), Priority.ALWAYS);

        contenedor.getChildren().addAll(lblTitulo, tabla.getContenedor());
    }

    public VBox getContenedor() {
        return contenedor;
    }

    public Tabla getTabla() {
        return tabla;
    }

    public void setTitulo(String titulo) {
        lblTitulo.setText(titulo);
    }
}