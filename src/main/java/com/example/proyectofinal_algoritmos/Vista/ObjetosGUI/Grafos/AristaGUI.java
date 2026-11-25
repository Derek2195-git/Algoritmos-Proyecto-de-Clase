package com.example.proyectofinal_algoritmos.Vista.ObjetosGUI.Grafos;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

public class AristaGUI {

    private Line linea;
    private Polygon flecha;
    private Label textoPeso;
    private Group contenedor;

    public AristaGUI(VerticeGUI origen, VerticeGUI destino, int peso, boolean dirigido) {
        double dx = destino.getCentroX() - origen.getCentroX();
        double dy = destino.getCentroY() - origen.getCentroY();
        double distancia = Math.sqrt(dx * dx + dy * dy);
        double ux = dx / distancia;
        double uy = dy / distancia;

        double inicioX = origen.getCentroX() + ux * origen.getRadio();
        double inicioY = origen.getCentroY() + uy * origen.getRadio();
        double finX = destino.getCentroX() - ux * destino.getRadio();
        double finY = destino.getCentroY() - uy * destino.getRadio();

        linea = new Line(inicioX, inicioY, finX, finY);
        linea.setStroke(Color.GRAY);

        double px = -uy;
        double py = ux;


        textoPeso = new Label(String.valueOf(peso));
        double centroTextoX = (inicioX + finX) / 2 + px * 12;
        double centroTextoY = (inicioY + finY) / 2 + py * 12;
        textoPeso.layoutXProperty().bind(textoPeso.widthProperty().divide(-2).add(centroTextoX));
        textoPeso.layoutYProperty().bind(textoPeso.heightProperty().divide(-2).add(centroTextoY));
        textoPeso.setStyle("-fx-background-color: white; -fx-padding: 0 2 0 2;");

        contenedor = new Group();

        contenedor.getChildren().add(linea);

        if (dirigido) {
            double largo = 10;
            flecha = new Polygon(
                    finX, finY,
                    finX - ux * largo + px * largo / 2, finY - uy * largo + py * largo / 2,
                    finX - ux * largo - px * largo / 2, finY - uy * largo - py * largo / 2);
            flecha.setFill(Color.GRAY);
            contenedor.getChildren().add(flecha);
        }

        contenedor.getChildren().add(textoPeso);
    }

    public Group getContenedor() {
        return contenedor;
    }

    public void resaltar(boolean activo) {
        Color color = activo ? Color.SEAGREEN : Color.GRAY;
        linea.setStroke(color);
        linea.setStrokeWidth(activo ? 3 : 1);
        if (flecha != null) {
            flecha.setFill(color);
        }
    }

}
