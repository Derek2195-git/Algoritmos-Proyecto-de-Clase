package com.example.proyectofinal_algoritmos.Controlador;

import com.example.proyectofinal_algoritmos.Vista.ObjetosGUI.Grafos.GrafoGUI;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Controlador {
    Stage stage;
    public Controlador(Stage stage) {
        this.stage = stage;

        GrafoGUI grafo = new GrafoGUI(100, 100);
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(1); ids.add(2); ids.add(3); ids.add(4);
        int[][] conexiones = { {1, 2, 100}, {2, 3, 80}, {3, 4, 120} };
        grafo.dibujar(ids, conexiones, false);

        Scene escena = new Scene(grafo.getContenedor(), 800, 600);
        stage.setTitle("Prueba grafos");
        stage.setScene(escena);
        stage.show();
    }
}
