package com.example.proyectofinal_algoritmos.Controlador;

import com.example.proyectofinal_algoritmos.Modelo.Analisis.ComplejidadTeorica;
import com.example.proyectofinal_algoritmos.Modelo.Analisis.Medicion;
import com.example.proyectofinal_algoritmos.Modelo.Grafos.Grafo;
import com.example.proyectofinal_algoritmos.Vista.Secciones.SeccionResultados;
import com.example.proyectofinal_algoritmos.Vista.Secciones.SeccionSuperior;
import com.example.proyectofinal_algoritmos.Vista.Secciones.SeccionTabla;
import com.example.proyectofinal_algoritmos.Vista.VentanaAnalisis;
import com.example.proyectofinal_algoritmos.Vista.VentanaGrafo;
import com.example.proyectofinal_algoritmos.Vista.VentanaPrincipal;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

public class Controlador {
    private Stage stage;
    private VentanaPrincipal vista;
    private Grafo grafoPrueba;

    public Controlador(Stage stage) {
        this.stage = stage;
        this.vista = new VentanaPrincipal(stage);

        inicializarGrafoDePrueba();
        conectarEventosVista();

        Scene escena = new Scene(vista.getContenedor(), 1100, 720);
        stage.setTitle("Sistema de Gestión y Análisis de Rutas mediante Grafos");
        stage.setScene(escena);
        stage.show();
    }

    private void inicializarGrafoDePrueba() {
        grafoPrueba = new Grafo("Grafo de Prueba", false);
        grafoPrueba.agregarVertice(1);
        grafoPrueba.agregarVertice(2);
        grafoPrueba.agregarVertice(3);
        grafoPrueba.agregarVertice(4);

        grafoPrueba.agregarArista(1, 2, 100);
        grafoPrueba.agregarArista(2, 3, 80);
        grafoPrueba.agregarArista(3, 4, 120);
        grafoPrueba.agregarArista(1, 4, 150);

        vista.getSeccionTabla().getTabla().mostrarConexiones(grafoPrueba);
    }

    private void conectarEventosVista() {
        SeccionSuperior superior = vista.getSeccionSuperior();
        SeccionTabla tabla = vista.getSeccionTabla();
        SeccionResultados resultados = vista.getSeccionResultados();
        VentanaGrafo ventanaGrafo = vista.getVentanaGrafo();
        VentanaAnalisis ventanaAnalisis = vista.getVentanaAnalisis();

        // --- Ventana Principal: Archivos y Ventanas Secundarias ---
        superior.alCargarCSV(() -> {
            File archivo = vista.seleccionarArchivoAbrir();
            if (archivo != null) {
                resultados.mostrarTexto("Archivo seleccionado para abrir: " + archivo.getAbsolutePath());
            }
        });

        superior.alGuardarCSV(() -> {
            File archivo = vista.seleccionarArchivoGuardar();
            if (archivo != null) {
                resultados.mostrarTexto("Ruta seleccionada para guardar: " + archivo.getAbsolutePath());
            }
        });

        superior.alGenerarPrueba(() -> {
            String texto = superior.getTextoCantidadVertices();
            int n = texto.isEmpty() ? 20 : Integer.parseInt(texto);
            resultados.mostrarTexto("Solicitud para generar grafo de " + n + " vértices (pendiente conectar GeneradorGrafo).");
        });

        superior.alAbrirVentanaGrafo(() -> {
            if (grafoPrueba.cantidadVertices() <= 50) {
                ventanaGrafo.mostrarGrafoPequeno(grafoPrueba);
            } else {
                ventanaGrafo.mostrarAvisoGrafoGrande(grafoPrueba.cantidadVertices());
            }
            ventanaGrafo.mostrar();
        });

        superior.alEjecutarBusqueda(() -> {
            ArrayList<Integer> rutaEjemplo = new ArrayList<>();
            rutaEjemplo.add(1);
            rutaEjemplo.add(2);
            rutaEjemplo.add(3);
            ventanaGrafo.resaltarRuta(rutaEjemplo);
            resultados.mostrarTexto("Búsqueda seleccionada: " + superior.getBusquedaSeleccionada()
                    + "\nRecorrido / ruta de prueba resaltada: 1 -> 2 -> 3");
        });

        superior.alEjecutarOrdenamiento(() -> {
            resultados.mostrarTexto("Ordenamiento seleccionado: " + superior.getOrdenamientoSeleccionado()
                    + " (" + superior.getCriterioOrdenamiento() + ")");
        });

        superior.alEjecutarAnalisisEmpirico(() -> {
            ArrayList<Medicion> quickSort = new ArrayList<>();
            quickSort.add(new Medicion("QuickSort", 20, 150_000));
            quickSort.add(new Medicion("QuickSort", 100, 620_000));
            quickSort.add(new Medicion("QuickSort", 500, 2_800_000));

            ArrayList<Medicion> shellSort = new ArrayList<>();
            shellSort.add(new Medicion("Shell Sort", 20, 180_000));
            shellSort.add(new Medicion("Shell Sort", 100, 740_000));
            shellSort.add(new Medicion("Shell Sort", 500, 3_400_000));

            ventanaAnalisis.actualizarComparativa("QuickSort", quickSort, "Shell Sort", shellSort);
            ventanaAnalisis.mostrar();
        });

        // --- Sección Resultados ---
        resultados.alVerComplejidad(() -> resultados.mostrarTexto(ComplejidadTeorica.generarTabla()));
        resultados.alLimpiar(resultados::limpiar);

        // --- Ventana Grafo: Estructura y Edición ---
        ventanaGrafo.alVerConexiones(() -> {
            tabla.setTitulo("Estructura del Grafo (Lista de Conexiones)");
            tabla.getTabla().mostrarConexiones(grafoPrueba);
        });



        ventanaGrafo.alVerMatriz(() -> {
            tabla.setTitulo("Matriz de Adyacencia");
            tabla.getTabla().mostrarMatrizAdyacencia(grafoPrueba);
        });

        ventanaGrafo.alAgregarVertice(() -> resultados.mostrarTexto("Botón '+ Vértice' presionado (Origen: " + ventanaGrafo.getTextoOrigen() + ")"));
        ventanaGrafo.alEliminarVertice(() -> resultados.mostrarTexto("Botón '- Vértice' presionado (Origen: " + ventanaGrafo.getTextoOrigen() + ")"));
        ventanaGrafo.alAgregarArista(() -> resultados.mostrarTexto("Botón '+ Arista' presionado (" + ventanaGrafo.getTextoOrigen() + " -> " + ventanaGrafo.getTextoDestino() + ", peso " + ventanaGrafo.getTextoPeso() + ")"));
        ventanaGrafo.alModificarPeso(() -> resultados.mostrarTexto("Botón 'Modificar Peso' presionado"));
        ventanaGrafo.alEliminarArista(() -> resultados.mostrarTexto("Botón '- Arista' presionado"));

        ventanaGrafo.alSeleccionarVertice(id -> {
            String idTexto = String.valueOf(id);
            if (ventanaGrafo.getTextoOrigen().isEmpty()) {
                ventanaGrafo.setTextoOrigen(idTexto);
            } else if (ventanaGrafo.getTextoDestino().isEmpty()) {
                ventanaGrafo.setTextoDestino(idTexto);
                // Si ya existe arista entre ambos en el modelo, precargar su peso:
                int origen = Integer.parseInt(ventanaGrafo.getTextoOrigen());
                int peso = grafoPrueba.obtenerPeso(origen, id);
                if (peso != -1) {
                    ventanaGrafo.setTextoPeso(String.valueOf(peso));
                }
            } else {
                // Si ambos estaban llenos, reinicia el ciclo poniendo el nuevo como origen
                ventanaGrafo.setTextoOrigen(idTexto);
                ventanaGrafo.setTextoDestino("");
                ventanaGrafo.setTextoPeso("");
            }
        });
    }
}