package com.example.proyectofinal_algoritmos.Vista.ObjetosGUI;

import com.example.proyectofinal_algoritmos.Modelo.Grafos.Arista;
import com.example.proyectofinal_algoritmos.Modelo.Grafos.Grafo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

/**
 * Clase que modela la tabla de visualización de conexiones y matriz de adyacencia en la vista
 * @version 25/11/2026
 * @author Derek Ramón Garzón Vizcarra
 */
public class Tabla {
    private TableView<ArrayList<String>> tablaView;
    private VBox contenedor;

    /**
     * Constructor de la tabla de visualización del grafo
     */
    public Tabla() {
        tablaView = new TableView<>();
        tablaView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tablaView.getStyleClass().add("tabla-grafo");

        contenedor = new VBox(tablaView);
        VBox.setVgrow(tablaView, Priority.ALWAYS);
    }

    /**
     * Getter del contenedor donde se ubica la tabla
     * @return Contenedor donde se está almacenando la tabla
     */
    public VBox getContenedor() {
        return contenedor;
    }


    public void mostrarConexiones(ArrayList<ArrayList<String>> filas) {
        cargarFilasAristas(filas);
    }


    public void mostrarListaAdyacencia(ArrayList<ArrayList<String>> filas) {
        tablaView.getColumns().clear();

        TableColumn<ArrayList<String>, String> colVertice = new TableColumn<>("Vértice");
        colVertice.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().get(0)));

        TableColumn<ArrayList<String>, String> colConexiones = new TableColumn<>("Conexiones (vecino: peso)");
        colConexiones.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().get(1)));

        tablaView.getColumns().addAll(colVertice, colConexiones);

        ObservableList<ArrayList<String>> datos = FXCollections.observableArrayList();
        datos.addAll(filas);
        tablaView.setItems(datos);
    }

    /**
     * Metodo auxiliar que configura las columnas de origen, destino y peso y carga las filas
     * @param filas Lista de filas con los valores en texto de cada arista
     */
    private void cargarFilasAristas(ArrayList<ArrayList<String>> filas) {
        tablaView.getColumns().clear();

        TableColumn<ArrayList<String>, String> colOrigen = new TableColumn<>("Origen");
        colOrigen.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().get(0)));

        TableColumn<ArrayList<String>, String> colDestino = new TableColumn<>("Destino");
        colDestino.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().get(1)));

        TableColumn<ArrayList<String>, String> colPeso = new TableColumn<>("Peso");
        colPeso.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().get(2)));

        tablaView.getColumns().addAll(colOrigen, colDestino, colPeso);

        ObservableList<ArrayList<String>> datos = FXCollections.observableArrayList();
        datos.addAll(filas);
        tablaView.setItems(datos);
    }

    public void mostrarMatriz(ArrayList<Integer> ids, ArrayList<ArrayList<String>> filas) {
        tablaView.getColumns().clear();

        TableColumn<ArrayList<String>, String> colCabecera = new TableColumn<>("Vértice");
        colCabecera.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().get(0)));
        tablaView.getColumns().add(colCabecera);

        for (int j = 0; j < ids.size(); j++) {
            final int indiceColumna = j + 1;
            TableColumn<ArrayList<String>, String> col = new TableColumn<>(String.valueOf(ids.get(j)));
            col.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().get(indiceColumna)));
            tablaView.getColumns().add(col);
        }

        ObservableList<ArrayList<String>> datos = FXCollections.observableArrayList();
        datos.addAll(filas);
        tablaView.setItems(datos);
    }
}