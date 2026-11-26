package com.example.proyectofinal_algoritmos.Vista.ObjetosGUI.Grafos;

import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;


/**
 * Clase que representa a la arista de en la pantalla
 * @version 24/11/2026
 * @author Derek Ramón Garzón Vizcarra
 */
public class AristaGUI {

    private Line linea;
    private Polygon flecha;
    private Text textoPeso;
    private Group contenedor;

    /**
     * Crea una representación grafica de una arista
     * @param origen Vertice de origen
     * @param destino Vertice de destino
     * @param peso Peso del arista
     * @param dirigido Indicador si el arista es dirigido o no
     */
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

        double px = -uy;
        double py = ux;

        textoPeso = new Text(String.valueOf(peso));
        textoPeso.setTextOrigin(VPos.CENTER);
        double centroTextoX = (inicioX + finX) / 2 + px * 12;
        double centroTextoY = (inicioY + finY) / 2 + py * 12;
        textoPeso.setX(centroTextoX - textoPeso.getLayoutBounds().getWidth() / 2);
        textoPeso.setY(centroTextoY);

        linea.getStyleClass().add("arista-linea");
        textoPeso.getStyleClass().add("arista-peso");
        contenedor = new Group();

        contenedor.getChildren().add(linea);

        if (dirigido) {
            double largo = 10;
            flecha = new Polygon(
                    finX, finY,
                    finX - ux * largo + px * largo / 2, finY - uy * largo + py * largo / 2,
                    finX - ux * largo - px * largo / 2, finY - uy * largo - py * largo / 2);
            flecha.getStyleClass().add("arista-flecha");
            contenedor.getChildren().add(flecha);
        }

        contenedor.getChildren().add(textoPeso);
    }

    /**
     * Getter del contenedor donde se ubica el arista
     * @return Nodo donde se contienen todos los elementos del arista
     */
    public Group getContenedor() {
        return contenedor;
    }

    /**
     * Metodo que resalta la linea en un color si ya ha sido visitada
     * @param activo Boolean que indica si ya se ha visitado el nodo
     */
    public void resaltar(boolean activo) {
        contenedor.getStyleClass().remove("arista-resaltada");
        if (activo) {
            contenedor.getStyleClass().add("arista-resaltada");
        }
    }

}
