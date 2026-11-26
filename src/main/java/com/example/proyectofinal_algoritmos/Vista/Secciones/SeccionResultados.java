package com.example.proyectofinal_algoritmos.Vista.Secciones;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Clase que modela la sección inferior de salida de resultados y análisis en la vista
 * @version 25/11/2026
 * @author Derek Ramón Garzón Vizcarra
 */
public class SeccionResultados {
    private VBox contenedor;
    private Label lblTitulo;
    private TextArea areaResultados;
    private Button btnVerComplejidad;
    private Button btnAnalisisEmpirico;
    private Button btnLimpiar;

    /**
     * Constructor de la sección de resultados y análisis
     */
    public SeccionResultados() {
        contenedor = new VBox(8);
        contenedor.setPadding(new Insets(10));
        contenedor.getStyleClass().add("seccion-resultados");

        lblTitulo = new Label("Resultados y Análisis");
        lblTitulo.getStyleClass().add("titulo-seccion");

        btnVerComplejidad = new Button("Ver Complejidad Teórica");
        btnLimpiar = new Button("Limpiar");

        btnAnalisisEmpirico = new Button("Análisis Empírico");

        HBox barraSuperior = new HBox(10, lblTitulo, btnVerComplejidad, btnLimpiar, btnAnalisisEmpirico);
        barraSuperior.setAlignment(Pos.CENTER_LEFT);

        areaResultados = new TextArea();
        areaResultados.setEditable(false);
        areaResultados.setWrapText(false);
        areaResultados.setPrefRowCount(7);
        areaResultados.getStyleClass().add("area-resultados");
        VBox.setVgrow(areaResultados, Priority.ALWAYS);

        contenedor.getChildren().addAll(barraSuperior, areaResultados);
    }

    /**
     * Getter del contenedor donde se ubica la sección de resultados
     * @return Contenedor donde se está almacenando la sección
     */
    public VBox getContenedor() {
        return contenedor;
    }

    /**
     * Metodo que reemplaza el contenido del área de resultados con un nuevo texto
     * @param texto Texto a mostrar en el área de resultados
     */
    public void mostrarTexto(String texto) {
        areaResultados.setText(texto);
    }

    /**
     * Metodo que añade una nueva línea de texto al final del área de resultados sin borrar lo anterior
     * @param texto Texto adicional a concatenar en el área de resultados
     */
    public void agregarTexto(String texto) {
        if (areaResultados.getText().isEmpty()) {
            areaResultados.setText(texto);
        } else {
            areaResultados.appendText("\n" + texto);
        }
    }

    /**
     * Metodo que borra todo el texto contenido en el área de resultados
     */
    public void limpiar() {
        areaResultados.clear();
    }

    /**
     * Metodo que asigna la acción a realizar al presionar el botón de complejidad teórica
     * @param accion Acción a realizar
     */
    public void alVerComplejidad(Runnable accion) {
        btnVerComplejidad.setOnAction(e -> accion.run());
    }

    /**
     * Metodo que asigna la acción a realizar al presionar el botón de análisis empírico
     * @param accion Acción a realizar
     */
    public void alEjecutarAnalisisEmpirico(Runnable accion) { btnAnalisisEmpirico.setOnAction(e -> accion.run()); }


    /**
     * Metodo que asigna la acción a realizar al presionar el botón de limpiar
     * @param accion Acción a realizar
     */
    public void alLimpiar(Runnable accion) {
        btnLimpiar.setOnAction(e -> accion.run());
    }
}