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

    /**
     * Metodo que muestra en la tabla todas las conexiones (aristas) del grafo recibido
     * @param grafo Grafo del cual se obtendrán las aristas a mostrar
     */
    public void mostrarConexiones(Grafo grafo) {
        mostrarAristasOrdenadas(grafo.obtenerAristas());
    }

    /**
     * Metodo que llena la tabla a partir de una lista específica de aristas
     * @param aristas Lista de aristas a desplegar en las filas de la tabla
     */
    public void mostrarAristasOrdenadas(ArrayList<Arista> aristas) {
        ArrayList<ArrayList<String>> filas = new ArrayList<>();
        for (int i = 0; i < aristas.size(); i++) {
            Arista a = aristas.get(i);
            ArrayList<String> fila = new ArrayList<>();
            fila.add(String.valueOf(a.getOrigen()));
            fila.add(String.valueOf(a.getDestino()));
            fila.add(String.valueOf(a.getPeso()));
            filas.add(fila);
        }
        cargarFilasAristas(filas);
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

    /**
     * Metodo que construye dinámicamente las columnas y filas para mostrar la matriz de adyacencia del grafo
     * @param grafo Grafo del cual se generará la matriz de adyacencia
     */
    public void mostrarMatrizAdyacencia(Grafo grafo) {
        tablaView.getColumns().clear();
        ArrayList<Integer> ordenIds = grafo.getOrden();
        int[][] matriz = grafo.crearMatrizAdyacencia();

        TableColumn<ArrayList<String>, String> colCabecera = new TableColumn<>("Vértice");
        colCabecera.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().get(0)));
        tablaView.getColumns().add(colCabecera);

        for (int j = 0; j < ordenIds.size(); j++) {
            final int indiceColumna = j + 1;
            TableColumn<ArrayList<String>, String> col = new TableColumn<>(String.valueOf(ordenIds.get(j)));
            col.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().get(indiceColumna)));
            tablaView.getColumns().add(col);
        }

        ObservableList<ArrayList<String>> filas = FXCollections.observableArrayList();
        for (int i = 0; i < ordenIds.size(); i++) {
            ArrayList<String> fila = new ArrayList<>();
            fila.add(String.valueOf(ordenIds.get(i)));
            for (int j = 0; j < ordenIds.size(); j++) {
                int val = matriz[i][j];
                if (val == -1) {
                    fila.add("-");
                } else {
                    fila.add(String.valueOf(val));
                }
            }
            filas.add(fila);
        }
        tablaView.setItems(filas);
    }
}