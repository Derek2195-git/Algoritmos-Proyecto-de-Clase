package com.example.proyectofinal_algoritmos.Controlador;

import com.example.proyectofinal_algoritmos.Modelo.Analisis.ComplejidadTeorica;
import com.example.proyectofinal_algoritmos.Modelo.Analisis.Medicion;
import com.example.proyectofinal_algoritmos.Modelo.Analisis.MedidorTiempos;
import com.example.proyectofinal_algoritmos.Modelo.Archivos.ConexionCSV;
import com.example.proyectofinal_algoritmos.Modelo.Archivos.GeneradorGrafo;
import com.example.proyectofinal_algoritmos.Modelo.Archivos.GestorArchivo;
import com.example.proyectofinal_algoritmos.Modelo.Archivos.MapaVertices;
import com.example.proyectofinal_algoritmos.Modelo.Archivos.ResultadoImportacion;
import com.example.proyectofinal_algoritmos.Modelo.Busquedas.Busquedas;
import com.example.proyectofinal_algoritmos.Modelo.Busquedas.Dijkstra;
import com.example.proyectofinal_algoritmos.Modelo.Busquedas.ResultadoRuta;
import com.example.proyectofinal_algoritmos.Modelo.Grafos.Arista;
import com.example.proyectofinal_algoritmos.Modelo.Grafos.ConjuntoGrafos;
import com.example.proyectofinal_algoritmos.Modelo.Grafos.Grafo;
import com.example.proyectofinal_algoritmos.Modelo.Ordenamientos.ConexionOrdenable;
import com.example.proyectofinal_algoritmos.Modelo.Ordenamientos.OrdenadorConexiones;
import com.example.proyectofinal_algoritmos.Vista.Ventanas.VentanaPrincipal;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Controlador {
    private VentanaPrincipal ventana;

    // Modelo
    private final ConjuntoGrafos conjunto = new ConjuntoGrafos();
    // Guarda los nombres de los vertices por cada grafo
    private final HashMap<String, MapaVertices> mapas = new HashMap<>();
    private final GestorArchivo gestor = new GestorArchivo();
    private final OrdenadorConexiones ordenador = new OrdenadorConexiones();

    // Estado de la vista
    private static final int VISTA_CONEXIONES = 0;
    private static final int VISTA_LISTA = 1;
    private static final int VISTA_MATRIZ = 2;
    private int vistaTabla = VISTA_CONEXIONES;

    // Maximo de vertices que se dibujan (indicacion de la profesora)
    private static final int MAX_VERTICES_DIBUJO = 50;
    // Tope de vertices para la matriz en la tabla, porque crea una columna por vertice
    private static final int MAX_VERTICES_MATRIZ = 200;
    // Pila grande para que QuickSort o DFS recursivos no truenen con grafos grandes
    private static final long TAMANIO_PILA = 256L * 1024 * 1024;

    private int contadorGrafos = 0;
    private boolean trabajando = false;
    // Ultima ruta o recorrido resaltado, para volverlo a pintar si se redibuja el grafo
    private ArrayList<Integer> rutaResaltada = new ArrayList<>();

    public Controlador(Stage stage) {
        this.ventana = new VentanaPrincipal(stage);

        conectarEventosVista();

        Scene escena = new Scene(ventana.getContenedor(), 1100, 720);
        stage.setTitle("Sistema de Gestión y Análisis de Rutas mediante Grafos");
        stage.setScene(escena);
        stage.show();

        aplicarEstilos(escena);
        ventana.getSeccionResultados().mostrarTexto(textoBienvenida());
    }


    private void conectarEventosVista() {

        // Botones de la seccion de arriba en la ventana principal
        ventana.getSeccionSuperior().alCargarCSV(this::manejarCargaCSV);
        ventana.getSeccionSuperior().alGuardarCSV(this::manejarGuardarCSV);
        ventana.getSeccionSuperior().alGenerarPrueba(this::manejarGeneracionGrafo);
        ventana.getSeccionSuperior().alCambiarDirigido(this::manejarCambioDirigido);
        ventana.getSeccionSuperior().alAbrirVentanaGrafo(this::manejarAbrirVentanaGrafo);
        ventana.getSeccionSuperior().alEjecutarBusqueda(this::manejarEjecucionBusqueda);
        ventana.getSeccionSuperior().alEjecutarOrdenamiento(this::manejarEjecucionOrdenamiento);
        ventana.getSeccionSuperior().alEjecutarAnalisisEmpirico(this::manejarAnalisisEmpirico);

        // Acciones de los botones de la parte de abajo en la ventana principal
        ventana.getSeccionResultados().alVerComplejidad(this::manejarVerComplejidad);
        ventana.getSeccionResultados().alLimpiar(this::manejarLimpiarResultados);

        // Acciones de los botones de la ventana del grafo
        ventana.getVentanaGrafo().alVerConexiones(this::manejarVerConexiones);
        ventana.getVentanaGrafo().alVerMatriz(this::manejarVerMatriz);
        ventana.getVentanaGrafo().alAgregarVertice(this::manejarAgregarVertice);
        ventana.getVentanaGrafo().alEliminarVertice(this::manejarEliminarVertice);
        ventana.getVentanaGrafo().alAgregarArista(this::manejarAgregarArista);
        ventana.getVentanaGrafo().alModificarPeso(this::manejarModificarPeso);
        ventana.getVentanaGrafo().alEliminarArista(this::manejarEliminarArista);
        ventana.getVentanaGrafo().alSeleccionarVertice(this::manejarSeleccionVertice);
        ventana.getVentanaGrafo().alVerListaAdyacencia(this::manejarVerListaAdyacencia);
    }

    // Seccion de arriba

    // Abre el selector para cargar un archivo CSV y arma el grafo
    private void manejarCargaCSV() {
        File archivo = ventana.seleccionarArchivoAbrir();
        if (archivo == null) return;

        boolean dirigido = ventana.getSeccionSuperior().esDirigido();
        ResultadoImportacion resultado = gestor.importarGrafo(archivo, dirigido);

        if (!resultado.esExitosa()) {
            ventana.getSeccionResultados().mostrarTexto(resultado.generarResumen());
            mostrarError("No se pudo importar el archivo " + archivo.getName()
                    + ". Revisa los errores en el área de resultados.");
            return;
        }

        Grafo grafo = resultado.getGrafo();
        MapaVertices mapa = resultado.getMapa();

        // Si todos los vertices del archivo son numeros, los usamos directo como id
        Object[] numerico = construirConIdsNumericos(grafo.getNombre(), dirigido,
                resultado.getConexionesValidas());
        if (numerico != null) {
            grafo = (Grafo) numerico[0];
            mapa = (MapaVertices) numerico[1];
        }

        String nombre = registrarGrafo(grafo, mapa);
        StringBuilder sb = new StringBuilder();
        sb.append("Grafo \"").append(nombre).append("\" importado.\n");
        sb.append(resultado.generarResumen());
        if (numerico == null && grafo.cantidadVertices() <= MAX_VERTICES_DIBUJO) {
            sb.append('\n').append(textoLeyenda(grafo, mapa));
        }
        ventana.getSeccionResultados().mostrarTexto(sb.toString());
    }

    // Abre el selector para guardar el grafo activo en un CSV
    private void manejarGuardarCSV() {
        Grafo grafo = grafoActivo();
        if (grafo == null) {
            mostrarError("No hay un grafo para guardar. Carga, genera o crea uno primero.");
            return;
        }
        File archivo = ventana.seleccionarArchivoGuardar();
        if (archivo == null) return;
        if (!archivo.getName().toLowerCase(Locale.ROOT).endsWith(".csv")) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + ".csv");
        }

        try {
            int escritas = gestor.guardarGrafo(grafo, mapaActivo(), archivo);
            int aislados = contarVerticesSinConexiones(grafo);
            String texto = "Grafo \"" + grafo.getNombre() + "\" guardado en:\n" + archivo.getAbsolutePath()
                    + "\nConexiones escritas: " + escritas;
            if (aislados > 0) {
                texto += "\nVértices sin conexiones omitidos: " + aislados
                        + " (no aportan información al archivo).";
            }
            ventana.getSeccionResultados().mostrarTexto(texto);
        } catch (IOException e) {
            mostrarError("No se pudo guardar el archivo: " + e.getMessage());
        }
    }

    // Genera un grafo aleatorio con la cantidad de vertices que escribio el usuario
    private void manejarGeneracionGrafo() {
        String texto = ventana.getSeccionSuperior().getTextoCantidadVertices();
        int n;
        try {
            n = Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            mostrarError("Escribe la cantidad de vértices a generar (por ejemplo 20, 100, 500 o 1000).");
            return;
        }
        if (n < 2 || n > 5000) {
            mostrarError("La cantidad de vértices debe estar entre 2 y 5000.");
            return;
        }

        boolean dirigido = ventana.getSeccionSuperior().esDirigido();
        long maximo = GeneradorGrafo.maximoAristas(n, dirigido);
        int aristas = (int) Math.min(maximo, 2L * n);

        long inicio = System.nanoTime();
        ArrayList<ConexionCSV> generadas = gestor.getGenerador()
                .generarConexionesAleatorias(n, aristas, dirigido, 1, 500, System.nanoTime());
        // Quita el prefijo "Ubicacion_" para que los ids queden como enteros
        ArrayList<ConexionCSV> numericas = new ArrayList<>(generadas.size());
        for (ConexionCSV c : generadas) {
            numericas.add(new ConexionCSV(quitarPrefijo(c.getOrigen()), quitarPrefijo(c.getDestino()),
                    c.getPeso()));
        }
        Object[] construido = construirConIdsNumericos("Aleatorio_" + n, dirigido, numericas);
        long tiempo = System.nanoTime() - inicio;

        Grafo grafo = (Grafo) construido[0];
        String nombre = registrarGrafo(grafo, (MapaVertices) construido[1]);
        ventana.getSeccionResultados().mostrarTexto(String.format(Locale.US,
                "Grafo \"%s\" generado (%s).%nVértices: %d%nAristas: %d%nTiempo de generación: %.3f ms",
                nombre, dirigido ? "dirigido" : "no dirigido",
                grafo.cantidadVertices(), grafo.cantidadAristas(), tiempo / 1_000_000.0));
    }

    // Cambia el grafo activo entre dirigido y no dirigido reconstruyendo sus aristas
    private void manejarCambioDirigido() {
        boolean dirigido = ventana.getSeccionSuperior().esDirigido();
        Grafo actual = grafoActivo();
        if (actual == null || actual.isDirigido() == dirigido) {
            ventana.getSeccionResultados().mostrarTexto("Los siguientes grafos se crearán como "
                    + (dirigido ? "dirigidos." : "no dirigidos."));
            return;
        }

        // Vuelve a armar el grafo activo con el nuevo tipo
        Grafo nuevo = new Grafo(actual.getNombre(), dirigido);
        for (int id : actual.obtenerIds()) {
            nuevo.agregarVertice(id);
        }
        for (Arista a : actual.obtenerAristas()) {
            nuevo.agregarArista(a.getOrigen(), a.getDestino(), a.getPeso());
            // Una arista no dirigida A-B se convierte en A->B y B->A
            if (dirigido) nuevo.agregarArista(a.getDestino(), a.getOrigen(), a.getPeso());
        }
        MapaVertices mapa = mapaActivo();
        conjunto.eliminarGrafo(actual.getNombre());
        conjunto.agregarBooleano(nuevo);
        mapas.put(nuevo.getNombre(), mapa);
        refrescarVistas();

        ventana.getSeccionResultados().mostrarTexto("El grafo \"" + nuevo.getNombre() + "\" ahora es "
                + (dirigido ? "dirigido: cada conexión A-B se convirtió en A->B y B->A."
                : "no dirigido: las conexiones A->B y B->A se unieron en una sola A-B.")
                + "\nAristas: " + actual.cantidadAristas() + " -> " + nuevo.cantidadAristas());
    }

    // Abre la ventana secundaria donde se dibuja y edita el grafo
    private void manejarAbrirVentanaGrafo() {

    }

    // Corre BFS, DFS o Dijkstra sobre el grafo activo y resalta el resultado
    private void manejarEjecucionBusqueda() {

    }

    // Ordena las conexiones del grafo con QuickSort o Shell Sort y las muestra en la tabla
    private void manejarEjecucionOrdenamiento() {

    }

    // Corre las pruebas empiricas en segundo plano y abre la ventana con la grafica
    private void manejarAnalisisEmpirico() {

    }

    // Seccion de resultados

    // Muestra la tabla de complejidad teorica en el area de resultados
    private void manejarVerComplejidad() {

    }

    // Limpia el texto del area de resultados
    private void manejarLimpiarResultados() {

    }

    // Seccion de la ventana del grafo

    // Cambia la tabla para mostrar la lista de conexiones
    private void manejarVerConexiones() {

    }

    // Cambia la tabla para mostrar la matriz de adyacencia (si no pasa del limite)
    private void manejarVerMatriz() {

    }

    // Agrega un vertice nuevo al grafo activo desde el campo Origen
    private void manejarAgregarVertice() {

    }

    // Elimina un vertice y todas sus aristas del grafo activo
    private void manejarEliminarVertice() {

    }

    // Agrega una arista entre origen y destino con el peso indicado
    private void manejarAgregarArista() {

    }

    // Cambia el peso de una arista que ya existe
    private void manejarModificarPeso() {

    }

    // Elimina la arista entre origen y destino
    private void manejarEliminarArista() {

    }

    // Al hacer clic en un vertice del dibujo, llena origen o destino automaticamente
    private void manejarSeleccionVertice(int valor) {

    }

    // Cambia la tabla para mostrar la lista de adyacencia del grafo activo
    private void manejarVerListaAdyacencia() {

    }

    // Metodos auxiliares

    private Grafo grafoActivo() {
        return conjunto.getGrafoActivo();
    }

    private MapaVertices mapaActivo() {
        Grafo grafo = grafoActivo();
        if (grafo == null) return null;
        MapaVertices mapa = mapas.get(grafo.getNombre());
        if (mapa == null) {
            mapa = new MapaVertices();
            mapas.put(grafo.getNombre(), mapa);
        }
        return mapa;
    }

    // Registra el grafo con un nombre unico, lo deja activo y actualiza la tabla y el dibujo
    private String registrarGrafo(Grafo grafo, MapaVertices mapa) {
        String base = grafo.getNombre() == null || grafo.getNombre().isBlank() ? "Grafo" : grafo.getNombre();
        String nombre = base;
        while (conjunto.existeGrafo(nombre)) {
            contadorGrafos++;
            nombre = base + "_" + contadorGrafos;
        }
        grafo.setNombre(nombre);
        conjunto.agregarBooleano(grafo);
        mapas.put(nombre, mapa == null ? new MapaVertices() : mapa);
        ventana.getSeccionSuperior().setDirigido(grafo.isDirigido());
        vistaTabla = VISTA_CONEXIONES;
        refrescarVistas();
        return nombre;
    }

    // Si todos los vertices son numeros enteros, los usa directo como id para que coincidan con el dibujo
    private Object[] construirConIdsNumericos(String nombre, boolean dirigido, ArrayList<ConexionCSV> conexiones) {
        for (ConexionCSV c : conexiones) {
            if (!esEnteroCanonico(c.getOrigen()) || !esEnteroCanonico(c.getDestino())) return null;
        }
        Grafo grafo = new Grafo(nombre, dirigido);
        MapaVertices mapa = new MapaVertices();
        for (ConexionCSV c : conexiones) {
            int o = Integer.parseInt(c.getOrigen());
            int d = Integer.parseInt(c.getDestino());
            if (grafo.agregarVertice(o)) mapa.registrar(c.getOrigen(), o);
            if (grafo.agregarVertice(d)) mapa.registrar(c.getDestino(), d);
            grafo.agregarArista(o, d, c.getPeso());
        }
        return new Object[]{grafo, mapa};
    }

    // Revisa si el texto es un entero positivo o cero sin ceros a la izquierda (ej. "7", no "007")
    private static boolean esEnteroCanonico(String texto) {
        Integer n = parsearEntero(texto);
        return n != null && n >= 0 && String.valueOf(n).equals(texto);
    }

    private static Integer parsearEntero(String texto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String quitarPrefijo(String nombre) {
        String numero = nombre.startsWith(GeneradorGrafo.PREFIJO_VERTICE)
                ? nombre.substring(GeneradorGrafo.PREFIJO_VERTICE.length()) : nombre;
        return String.valueOf(Integer.parseInt(numero));
    }

    // Regresa el nombre del vertice o su id en texto si no tiene nombre
    private String nombre(int id) {
        MapaVertices mapa = mapaActivo();
        return mapa == null ? String.valueOf(id) : mapa.getNombre(id);
    }

    // Busca el id de un vertice a partir de su nombre o numero, o null si no existe
    private Integer resolverVertice(String texto) {
        Grafo grafo = grafoActivo();
        if (grafo == null || texto == null || texto.isEmpty()) return null;
        MapaVertices mapa = mapaActivo();
        if (mapa != null && mapa.contieneNombre(texto)) {
            int id = mapa.getId(texto);
            if (grafo.existeVertice(id)) return id;
        }
        Integer numero = parsearEntero(texto);
        if (numero != null && grafo.existeVertice(numero)) return numero;
        return null;
    }

    private int contarVerticesSinConexiones(Grafo grafo) {
        HashMap<Integer, Boolean> conectado = new HashMap<>();
        for (Arista a : grafo.obtenerAristas()) {
            conectado.put(a.getOrigen(), true);
            conectado.put(a.getDestino(), true);
        }
        int total = 0;
        for (int id : grafo.obtenerIds()) {
            if (!conectado.containsKey(id)) total++;
        }
        return total;
    }

    // Arma la leyenda para saber que nombre corresponde a cada numero en el dibujo
    private String textoLeyenda(Grafo grafo, MapaVertices mapa) {
        StringBuilder sb = new StringBuilder("Vértices en el dibujo (número = nombre):\n");
        for (int id : grafo.obtenerIds()) {
            sb.append("  ").append(id).append(" = ").append(mapa.getNombre(id)).append('\n');
        }
        return sb.toString();
    }

    private static ArrayList<String> fila(String... valores) {
        ArrayList<String> fila = new ArrayList<>();
        for (String v : valores) fila.add(v);
        return fila;
    }

    private void refrescarVistas() {
        // Como cambio el grafo, la ruta resaltada anterior ya no sirve
        rutaResaltada = new ArrayList<>();
        refrescarTabla();
        refrescarDibujo();
    }

    // Vuelve a llenar la tabla segun la vista seleccionada (conexiones, lista o matriz)
    private void refrescarTabla() {
        Grafo grafo = grafoActivo();
        var tabla = ventana.getSeccionTabla().getTabla();
        if (grafo == null) {
            tabla.mostrarConexiones(new ArrayList<>());
            return;
        }
        String encabezado = " - " + grafo.getNombre() + " (" + grafo.cantidadVertices() + " vértices, "
                + grafo.cantidadAristas() + " aristas, " + (grafo.isDirigido() ? "dirigido" : "no dirigido") + ")";

        if (vistaTabla == VISTA_MATRIZ && grafo.cantidadVertices() > MAX_VERTICES_MATRIZ) {
            vistaTabla = VISTA_LISTA;
        }

        if (vistaTabla == VISTA_LISTA) {
            ArrayList<ArrayList<String>> filas = new ArrayList<>();
            for (int id : grafo.obtenerIds()) {
                StringBuilder vecinos = new StringBuilder();
                for (Arista a : grafo.getVertice(id).getAristasAdyacentes()) {
                    if (vecinos.length() > 0) vecinos.append(", ");
                    vecinos.append(nombre(a.getDestino())).append(": ").append(a.getPeso());
                }
                filas.add(fila(nombre(id), vecinos.length() == 0 ? "(sin conexiones)" : vecinos.toString()));
            }
            tabla.mostrarListaAdyacencia(filas);
            ventana.getSeccionTabla().setTitulo("Lista de Adyacencia" + encabezado);
        } else if (vistaTabla == VISTA_MATRIZ) {
            ArrayList<Integer> ids = grafo.obtenerIds();
            int[][] matriz = grafo.crearMatrizAdyacencia();
            ArrayList<ArrayList<String>> filas = new ArrayList<>();
            for (int i = 0; i < ids.size(); i++) {
                ArrayList<String> f = new ArrayList<>();
                // Las columnas muestran el id del vertice y la primera celda incluye el nombre
                String nombreFila = nombre(ids.get(i));
                f.add(nombreFila.equals(String.valueOf(ids.get(i))) ? nombreFila
                        : ids.get(i) + " (" + nombreFila + ")");
                for (int j = 0; j < ids.size(); j++) {
                    f.add(matriz[i][j] < 0 ? "-" : String.valueOf(matriz[i][j]));
                }
                filas.add(f);
            }
            tabla.mostrarMatriz(ids, filas);
            ventana.getSeccionTabla().setTitulo("Matriz de Adyacencia" + encabezado + "  (\"-\" = sin conexión)");
        } else {
            ArrayList<ArrayList<String>> filas = new ArrayList<>();
            for (Arista a : grafo.obtenerAristas()) {
                filas.add(fila(nombre(a.getOrigen()), nombre(a.getDestino()), String.valueOf(a.getPeso())));
            }
            tabla.mostrarConexiones(filas);
            ventana.getSeccionTabla().setTitulo("Lista de Conexiones" + encabezado);
        }
    }

    // Dibuja el grafo activo solo si tiene 50 vertices o menos
    private void refrescarDibujo() {
        Grafo grafo = grafoActivo();
        if (grafo == null) {
            ventana.getVentanaGrafo().getSeccionGrafica().limpiar();
            return;
        }
        if (grafo.cantidadVertices() > MAX_VERTICES_DIBUJO) {
            ventana.getVentanaGrafo().mostrarAvisoGrafoGrande(grafo.cantidadVertices());
            return;
        }
        ArrayList<Arista> aristas = grafo.obtenerAristas();
        int[][] conexiones = new int[aristas.size()][3];
        for (int i = 0; i < aristas.size(); i++) {
            conexiones[i][0] = aristas.get(i).getOrigen();
            conexiones[i][1] = aristas.get(i).getDestino();
            conexiones[i][2] = aristas.get(i).getPeso();
        }
        ventana.getVentanaGrafo().mostrarGrafoPequeno(grafo.getNombre(), grafo.obtenerIds(), conexiones,
                grafo.isDirigido());
        if (!rutaResaltada.isEmpty()) {
            ventana.getVentanaGrafo().resaltarRuta(rutaResaltada);
        }
    }

    private void mostrarError(String mensaje) {
        ventana.getSeccionResultados().mostrarTexto("Error: " + mensaje);
        Alert alerta = new Alert(Alert.AlertType.WARNING, mensaje);
        alerta.setHeaderText(null);
        alerta.setTitle("Aviso");
        alerta.show();
    }

    private void aplicarEstilos(Scene escena) {
        if (escena == null) return;
        URL css = Controlador.class.getResource("/recursos/estilos.css");
        if (css != null && !escena.getStylesheets().contains(css.toExternalForm())) {
            escena.getStylesheets().add(css.toExternalForm());
        }
    }

    private String textoBienvenida() {
        return "Bienvenido. Para empezar:\n"
                + "  - \"Cargar CSV\" importa un archivo con columnas origen,destino,peso.\n"
                + "  - \"Generar Grafo\" crea un grafo aleatorio con la cantidad de vértices escrita.\n"
                + "  - \"Ver Grafo\" abre la ventana para dibujar y editar el grafo a mano.\n"
                + "Marca \"Grafo Dirigido\" antes de cargar o generar si quieres conexiones de un solo sentido.";
    }
}
