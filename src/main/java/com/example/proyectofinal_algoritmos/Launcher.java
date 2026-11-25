package com.example.proyectofinal_algoritmos;

import com.example.proyectofinal_algoritmos.Controlador.Controlador;
import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application{
    @Override
    public void start(Stage stage) throws Exception {
        Controlador controlador = new Controlador(stage);
    }
}
