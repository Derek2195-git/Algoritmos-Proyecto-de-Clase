package com.example.proyectofinal_algoritmos.Vista.Secciones;

import com.example.proyectofinal_algoritmos.Vista.ObjetosGUI.Tabla;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SeccionTabla {
    private VBox contenedor;
    private Tabla tabla;
    private Button btnVistaConexiones;
    private Button btnVistaMatriz;
    private Label lblTitulo;

    public SeccionTabla() {
        contenedor = new VBox(8);
        contenedor.setPadding(new Insets(10));
        contenedor.getStyleClass().add("seccion-tabla");

        lblTitulo = new Label("Estructura del Grafo (Lista de Conexiones)");
        lblTitulo.getStyleClass().add("titulo-seccion");

        btnVistaConexiones = new Button("Ver Conexiones");
        btnVistaMatriz = new Button("Ver Matriz de Adyacencia");

        HBox barraSuperior = new HBox(10, lblTitulo, btnVistaConexiones, btnVistaMatriz);
        barraSuperior.setAlignment(Pos.CENTER_LEFT);

        tabla = new Tabla();
        VBox.setVgrow(tabla.getContenedor(), Priority.ALWAYS);

        contenedor.getChildren().addAll(barraSuperior, tabla.getContenedor());
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

    public void alVerConexiones(Runnable accion) {
        btnVistaConexiones.setOnAction(e -> accion.run());
    }

    public void alVerMatriz(Runnable accion) {
        btnVistaMatriz.setOnAction(e -> accion.run());
    }


}
