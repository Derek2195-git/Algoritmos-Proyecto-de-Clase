package com.example.proyectofinal_algoritmos.Vista.Secciones;

import com.example.proyectofinal_algoritmos.Vista.ObjetosGUI.Tabla;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Clase que modela la sección central que contiene la tabla de estructura del grafo en la vista
 * @version 25/11/2026
 * @author Derek Ramón Garzón Vizcarra
 */
public class SeccionTabla {
    private VBox contenedor;
    private Tabla tabla;
    private Label lblTitulo;

    /**
     * Constructor de la sección de tabla del grafo
     */
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

    /**
     * Getter del contenedor donde se ubica la sección de la tabla
     * @return Contenedor donde se está almacenando la sección
     */
    public VBox getContenedor() {
        return contenedor;
    }

    /**
     * Getter del objeto visual Tabla contenido en esta sección
     * @return Instancia de Tabla administrada por la sección
     */
    public Tabla getTabla() {
        return tabla;
    }

    /**
     * Setter que actualiza el texto del encabezado de la sección
     * @param titulo Nuevo título a mostrar sobre la tabla
     */
    public void setTitulo(String titulo) {
        lblTitulo.setText(titulo);
    }
}