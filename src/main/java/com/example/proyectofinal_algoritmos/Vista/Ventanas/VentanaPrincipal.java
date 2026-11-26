package com.example.proyectofinal_algoritmos.Vista.Ventanas;

import com.example.proyectofinal_algoritmos.Vista.Secciones.SeccionResultados;
import com.example.proyectofinal_algoritmos.Vista.Secciones.SeccionSuperior;
import com.example.proyectofinal_algoritmos.Vista.Secciones.SeccionTabla;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Clase que modela la ventana principal de la aplicación e integra las secciones y ventanas secundarias
 * @version 25/11/2026
 * @author Derek Ramón Garzón Vizcarra
 */
public class VentanaPrincipal {
    private Stage stagePrincipal;
    private BorderPane contenedor;
    private SeccionSuperior seccionSuperior;
    private SeccionTabla seccionTabla;
    private SeccionResultados seccionResultados;
    private VentanaGrafo ventanaGrafo;
    private VentanaAnalisis ventanaAnalisis;

    /**
     * Constructor de la ventana principal
     * @param stagePrincipal Escenario principal de JavaFX de la aplicación
     */
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

    /**
     * Metodo que despliega un cuadro de diálogo para seleccionar un archivo CSV a importar
     * @return Archivo CSV seleccionado o null si el usuario cancela la operación
     */
    public File seleccionarArchivoAbrir() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Seleccionar archivo CSV de grafo");

        File directorioInicial = new File(System.getProperty("user.dir"), "src/main/resources/recursos");
        if (directorioInicial != null && directorioInicial.exists() && directorioInicial.isDirectory()) {
            selector.setInitialDirectory(directorioInicial);
        }

        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv")
        );
        return selector.showOpenDialog(stagePrincipal);
    }
    /**
     * Metodo que despliega un cuadro de diálogo para elegir la ruta donde guardar un archivo CSV
     * @return Archivo CSV de destino seleccionado o null si el usuario cancela la operación
     */
    public File seleccionarArchivoGuardar() {
        FileChooser selector = new FileChooser();

        File directorioInicial = new File(System.getProperty("user.dir"), "src/main/resources/recursos");
        if (directorioInicial != null && directorioInicial.exists() && directorioInicial.isDirectory()) {
            selector.setInitialDirectory(directorioInicial);
        }


        selector.setTitle("Guardar grafo como CSV");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv")
        );
        return selector.showSaveDialog(stagePrincipal);
    }

    /**
     * Getter del contenedor raíz de la ventana principal
     * @return BorderPane principal de la interfaz
     */
    public BorderPane getContenedor() { return contenedor; }

    /**
     * Getter de la sección superior de controles
     * @return Instancia de SeccionSuperior
     */
    public SeccionSuperior getSeccionSuperior() { return seccionSuperior; }

    /**
     * Getter de la sección central de tabla
     * @return Instancia de SeccionTabla
     */
    public SeccionTabla getSeccionTabla() { return seccionTabla; }

    /**
     * Getter de la sección inferior de resultados y análisis
     * @return Instancia de SeccionResultados
     */
    public SeccionResultados getSeccionResultados() { return seccionResultados; }

    /**
     * Getter de la ventana secundaria de edición y visualización del grafo
     * @return Instancia de VentanaGrafo
     */
    public VentanaGrafo getVentanaGrafo() { return ventanaGrafo; }

    /**
     * Getter de la ventana secundaria de gráficas de análisis empírico
     * @return Instancia de VentanaAnalisis
     */
    public VentanaAnalisis getVentanaAnalisis() { return ventanaAnalisis; }
}