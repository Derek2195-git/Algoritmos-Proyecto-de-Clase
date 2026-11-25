package com.example.proyectofinal_algoritmos.Vista.ObjetosGUI;

import com.example.proyectofinal_algoritmos.Modelo.Grafos.Arista;
import com.example.proyectofinal_algoritmos.Modelo.Grafos.Grafo;
import com.example.proyectofinal_algoritmos.Modelo.Grafos.Vertice;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;

public class Tabla {
    private TableView<ArrayList<String>> tablaView;
    private VBox contenedor;

    public Tabla() {
        tablaView = new TableView<>();
        tablaView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tablaView.getStyleClass().add("tabla-grafo");

        contenedor = new VBox(tablaView);
        VBox.setVgrow(tablaView, Priority.ALWAYS);
    }

    public VBox getContenedor() {
        return contenedor;
    }

    public void mostrarConexiones(Grafo grafo) {
        mostrarAristasOrdenadas(grafo.obtenerAristas());
    }

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