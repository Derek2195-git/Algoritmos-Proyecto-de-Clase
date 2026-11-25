package com.example.proyectofinal_algoritmos.Vista.Secciones;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SeccionResultados {
    private VBox contenedor;
    private Label lblTitulo;
    private TextArea areaResultados;
    private Button btnVerComplejidad;
    private Button btnLimpiar;

    public SeccionResultados() {
        contenedor = new VBox(8);
        contenedor.setPadding(new Insets(10));
        contenedor.getStyleClass().add("seccion-resultados");

        lblTitulo = new Label("Resultados y Análisis");
        lblTitulo.getStyleClass().add("titulo-seccion");

        btnVerComplejidad = new Button("Ver Complejidad Teórica");
        btnLimpiar = new Button("Limpiar");

        HBox barraSuperior = new HBox(10, lblTitulo, btnVerComplejidad, btnLimpiar);
        barraSuperior.setAlignment(Pos.CENTER_LEFT);

        areaResultados = new TextArea();
        areaResultados.setEditable(false);
        areaResultados.setWrapText(false);
        areaResultados.setPrefRowCount(7);
        areaResultados.getStyleClass().add("area-resultados");
        VBox.setVgrow(areaResultados, Priority.ALWAYS);

        contenedor.getChildren().addAll(barraSuperior, areaResultados);
    }

    public VBox getContenedor() {
        return contenedor;
    }

    public void mostrarTexto(String texto) {
        areaResultados.setText(texto);
    }

    public void agregarTexto(String texto) {
        if (areaResultados.getText().isEmpty()) {
            areaResultados.setText(texto);
        } else {
            areaResultados.appendText("\n" + texto);
        }
    }

    public void limpiar() {
        areaResultados.clear();
    }

    // Métodos delegadores para el Controlador
    public void alVerComplejidad(Runnable accion) {
        btnVerComplejidad.setOnAction(e -> accion.run());
    }

    public void alLimpiar(Runnable accion) {
        btnLimpiar.setOnAction(e -> accion.run());
    }
}