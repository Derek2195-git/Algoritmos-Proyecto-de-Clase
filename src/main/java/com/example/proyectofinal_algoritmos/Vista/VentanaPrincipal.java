package com.example.proyectofinal_algoritmos.Vista;

import com.example.proyectofinal_algoritmos.Vista.Secciones.SeccionResultados;
import com.example.proyectofinal_algoritmos.Vista.Secciones.SeccionSuperior;
import com.example.proyectofinal_algoritmos.Vista.Secciones.SeccionTabla;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class VentanaPrincipal {
    private Stage stagePrincipal;
    private BorderPane contenedor;
    private SeccionSuperior seccionSuperior;
    private SeccionTabla seccionTabla;
    private SeccionResultados seccionResultados;
    private VentanaGrafo ventanaGrafo;
    private VentanaAnalisis ventanaAnalisis;

    public VentanaPrincipal(Stage stagePrincipal) {
        this.stagePrincipal = stagePrincipal;

        contenedor = new BorderPane();
        contenedor.getStyleClass().add("ventana-principal");

        seccionSuperior = new SeccionSuperior();
        seccionTabla = new SeccionTabla();
        seccionResultados = new SeccionResultados();

        ventanaGrafo = new VentanaGrafo(stagePrincipal);
        ventanaAnalisis = new VentanaAnalisis(stagePrincipal);

        contenedor.setTop(seccionSuperior.getContenedor());
        contenedor.setCenter(seccionTabla.getContenedor());
        contenedor.setBottom(seccionResultados.getContenedor());
    }

    public File seleccionarArchivoAbrir() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Seleccionar archivo CSV de grafo");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv")
        );
        return selector.showOpenDialog(stagePrincipal);
    }

    public File seleccionarArchivoGuardar() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar grafo como CSV");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv")
        );
        return selector.showSaveDialog(stagePrincipal);
    }

    public BorderPane getContenedor() { return contenedor; }
    public SeccionSuperior getSeccionSuperior() { return seccionSuperior; }
    public SeccionTabla getSeccionTabla() { return seccionTabla; }
    public SeccionResultados getSeccionResultados() { return seccionResultados; }
    public VentanaGrafo getVentanaGrafo() { return ventanaGrafo; }
    public VentanaAnalisis getVentanaAnalisis() { return ventanaAnalisis; }
}