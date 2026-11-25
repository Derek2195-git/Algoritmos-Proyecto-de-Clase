package com.example.proyectofinal_algoritmos.Vista.Secciones;

import javafx.scene.control.Button;

public class SeccionResultados {

    public Button boton;

    public void alHacerAccion(Runnable accion) {
        boton.setOnAction(e-> accion.run());
    }
}
