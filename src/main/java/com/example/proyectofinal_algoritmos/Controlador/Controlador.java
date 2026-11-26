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
        refrescarDibujo();
        ventana.getVentanaGrafo().mostrar();
        aplicarEstilos(ventana.getVentanaGrafo().getContenedor().getScene());
        Grafo grafo = grafoActivo();
        MapaVertices mapa = mapaActivo();
        if (grafo != null && mapa != null && grafo.cantidadVertices() <= MAX_VERTICES_DIBUJO
                && tieneNombresNoNumericos(grafo, mapa)) {
            ventana.getSeccionResultados().mostrarTexto(textoLeyenda(grafo, mapa));
        }
    }

    // Corre BFS, DFS o Dijkstra sobre el grafo activo y resalta el resultado
    private void manejarEjecucionBusqueda() {
        Grafo grafo = grafoActivo();
        if (grafo == null) {
            mostrarError("No hay un grafo activo. Carga, genera o crea uno primero.");
            return;
        }
        String algoritmo = ventana.getSeccionSuperior().getBusquedaSeleccionada();
        String textoInicio = ventana.getSeccionSuperior().getTextoInicioBusqueda();
        Integer inicio = resolverVertice(textoInicio);
        if (inicio == null) {
            mostrarError("El vértice inicial \"" + textoInicio + "\" no existe en el grafo.");
            return;
        }

        if (algoritmo != null && algoritmo.startsWith("Dijkstra")) {
            String textoFin = ventana.getSeccionSuperior().getTextoFinBusqueda();
            Integer fin = resolverVertice(textoFin);
            if (fin == null) {
                mostrarError("Para Dijkstra escribe un vértice destino que exista (\"" + textoFin + "\" no existe).");
                return;
            }
            long t0 = System.nanoTime();
            ResultadoRuta ruta = Dijkstra.calcular(grafo, inicio, fin);
            long tiempo = System.nanoTime() - t0;

            StringBuilder sb = new StringBuilder("Dijkstra (ruta óptima)\n");
            sb.append("Vértice de origen: ").append(nombre(inicio)).append('\n');
            sb.append("Vértice de destino: ").append(nombre(fin)).append('\n');
            if (ruta.existeRuta()) {
                sb.append("Secuencia de la ruta: ").append(unirNombres(ruta.getRuta(), " -> ")).append('\n');
                sb.append("Costo total del recorrido: ").append(ruta.getCostoTotal()).append('\n');
                resaltar(ruta.getRuta());
            } else {
                sb.append("No existe una ruta entre esos vértices.\n");
                resaltar(new ArrayList<>());
            }
            sb.append(String.format(Locale.US, "Tiempo: %.4f ms", tiempo / 1_000_000.0));
            ventana.getSeccionResultados().mostrarTexto(sb.toString());
            return;
        }

        boolean esDfs = algoritmo != null && algoritmo.startsWith("DFS");
        long t0 = System.nanoTime();
        ArrayList<Integer> orden = esDfs ? Busquedas.dfs(grafo, inicio) : Busquedas.bfs(grafo, inicio);
        long tiempo = System.nanoTime() - t0;

        resaltar(orden);
        ventana.getSeccionResultados().mostrarTexto(String.format(Locale.US,
                "%s desde el vértice %s%nOrden de visita: %s%nVértices visitados: %d de %d%nTiempo: %.4f ms",
                esDfs ? "DFS (búsqueda en profundidad)" : "BFS (búsqueda en anchura)",
                nombre(inicio), unirNombres(orden, ", "), orden.size(), grafo.cantidadVertices(),
                tiempo / 1_000_000.0));
    }

    // Ordena las conexiones del grafo con QuickSort o Shell Sort y las muestra en la tabla
    private void manejarEjecucionOrdenamiento() {
        Grafo grafo = grafoActivo();
        if (grafo == null) {
            mostrarError("No hay un grafo activo. Carga, genera o crea uno primero.");
            return;
        }
        if (trabajando) return;

        String algoritmoElegido = ventana.getSeccionSuperior().getOrdenamientoSeleccionado();
        String algoritmo = "Shell Sort".equals(algoritmoElegido)
                ? OrdenadorConexiones.SHELL_SORT : OrdenadorConexiones.QUICKSORT;
        boolean porPeso = !"Por Nombre de Vértice".equals(ventana.getSeccionSuperior().getCriterioOrdenamiento());
        int criterio = porPeso ? ConexionOrdenable.POR_PESO : ConexionOrdenable.POR_NOMBRE;

        ConexionOrdenable[] arreglo = OrdenadorConexiones.crearArreglo(grafo, this::nombre, criterio);

        // Corre en un hilo con pila grande por si QuickSort recursivo recibe datos casi ordenados
        trabajando = true;
        ejecutarConPilaGrande(() -> {
            long t0 = System.nanoTime();
            ordenador.ordenar(arreglo, algoritmo);
            long tiempo = System.nanoTime() - t0;

            Platform.runLater(() -> {
                trabajando = false;
                ArrayList<ArrayList<String>> filas = new ArrayList<>();
                for (ConexionOrdenable c : arreglo) {
                    filas.add(fila(c.getNombreOrigen(), c.getNombreDestino(), String.valueOf(c.getPeso())));
                }
                vistaTabla = VISTA_CONEXIONES;
                ventana.getSeccionTabla().getTabla().mostrarConexiones(filas);
                ventana.getSeccionTabla().setTitulo("Conexiones ordenadas " + (porPeso ? "por peso" : "por nombre de vértice")
                        + " (" + algoritmo + ")");
                ventana.getSeccionResultados().mostrarTexto(String.format(Locale.US,
                        "%s ordenó %d conexiones %s.%nTiempo: %.4f ms",
                        algoritmo, arreglo.length, porPeso ? "por peso" : "por nombre de vértice",
                        tiempo / 1_000_000.0));
            });
        }, error -> {
            trabajando = false;
            mostrarError("Error al ordenar: " + error);
        });
    }

    // Corre las pruebas empiricas en segundo plano y abre la ventana con la grafica
    private void manejarAnalisisEmpirico() {

    }

    // Seccion de resultados

    // Muestra la tabla de complejidad teorica en el area de resultados
    private void manejarVerComplejidad() {
        StringBuilder sb = new StringBuilder("Complejidad teórica (V = vértices, E = aristas, n = elementos)\n\n");
        sb.append(ComplejidadTeorica.generarTabla()).append('\n');
        for (ComplejidadTeorica c : ComplejidadTeorica.values()) {
            sb.append("- ").append(c.getNombre()).append(": ").append(c.getExplicacion()).append('\n');
        }
        ventana.getSeccionResultados().mostrarTexto(sb.toString());
    }

    // Limpia el texto del area de resultados
    private void manejarLimpiarResultados() {
        ventana.getSeccionResultados().limpiar();
    }

    // Seccion de la ventana del grafo

    // Cambia la tabla para mostrar la lista de conexiones
    private void manejarVerConexiones() {
        vistaTabla = VISTA_CONEXIONES;
        refrescarTabla();
    }

    // Cambia la tabla para mostrar la matriz de adyacencia (si no pasa del limite)
    private void manejarVerMatriz() {
        Grafo grafo = grafoActivo();
        if (grafo != null && grafo.cantidadVertices() > MAX_VERTICES_MATRIZ) {
            mostrarError("El grafo tiene " + grafo.cantidadVertices() + " vértices; la matriz solo se muestra hasta "
                    + MAX_VERTICES_MATRIZ + ". Usa la lista de conexiones o la lista de adyacencia.");
            return;
        }
        vistaTabla = VISTA_MATRIZ;
        refrescarTabla();
    }

    // Agrega un vertice nuevo al grafo activo desde el campo Origen
    private void manejarAgregarVertice() {
        String texto = ventana.getVentanaGrafo().getTextoOrigen();
        if (texto.isEmpty()) {
            mostrarError("Escribe en \"Origen\" el número (o nombre) del vértice a agregar.");
            return;
        }
        Grafo grafo = asegurarGrafoActivo();
        MapaVertices mapa = mapaActivo();

        int id;
        Integer numero = parsearEntero(texto);
        if (numero != null) {
            if (numero < 0) {
                mostrarError("El número de vértice no puede ser negativo.");
                return;
            }
            if (grafo.existeVertice(numero) || mapa.contieneNombre(texto)) {
                mostrarError("El vértice " + texto + " ya existe.");
                return;
            }
            id = numero;
        } else {
            if (mapa.contieneNombre(texto)) {
                mostrarError("Ya existe un vértice llamado \"" + texto + "\".");
                return;
            }
            id = siguienteIdLibre(grafo, mapa);
            mapa.registrar(texto, id);
        }
        grafo.agregarVertice(id);
        ventana.getVentanaGrafo().limpiarCampos();
        refrescarVistas();
        ventana.getSeccionResultados().mostrarTexto("Vértice " + nombre(id)
                + (numero == null ? " (id " + id + ")" : "") + " agregado. Total de vértices: "
                + grafo.cantidadVertices());
    }

    // Elimina un vertice y todas sus aristas del grafo activo
    private void manejarEliminarVertice() {
        Grafo grafo = grafoActivo();
        if (grafo == null) {
            mostrarError("No hay un grafo activo.");
            return;
        }
        String texto = ventana.getVentanaGrafo().getTextoOrigen();
        Integer id = resolverVertice(texto);
        if (id == null) {
            mostrarError("Escribe en \"Origen\" un vértice existente para eliminarlo.");
            return;
        }
        String nombreVertice = nombre(id);
        grafo.eliminarVertice(id);
        MapaVertices mapa = mapaActivo();
        if (mapa.contieneId(id)) mapa.eliminar(mapa.getNombre(id));
        ventana.getVentanaGrafo().limpiarCampos();
        refrescarVistas();
        ventana.getSeccionResultados().mostrarTexto("Vértice " + nombreVertice
                + " eliminado junto con sus conexiones.");
    }

    // Agrega una arista entre origen y destino con el peso indicado
    private void manejarAgregarArista() {
        int[] datos = leerArista(true);
        if (datos == null) return;
        Grafo grafo = grafoActivo();
        if (grafo.existeArista(datos[0], datos[1])) {
            mostrarError("La conexión " + nombre(datos[0]) + " - " + nombre(datos[1])
                    + " ya existe. Usa \"Modificar Peso\" para cambiar su peso.");
            return;
        }
        if (!grafo.agregarArista(datos[0], datos[1], datos[2])) {
            mostrarError("No se pudo agregar la conexión.");
            return;
        }
        refrescarVistas();
        ventana.getSeccionResultados().mostrarTexto("Conexión " + nombre(datos[0])
                + (grafo.isDirigido() ? " -> " : " - ") + nombre(datos[1]) + " con peso " + datos[2] + " agregada.");
    }

    // Cambia el peso de una arista que ya existe
    private void manejarModificarPeso() {
        int[] datos = leerArista(true);
        if (datos == null) return;
        Grafo grafo = grafoActivo();
        int anterior = grafo.obtenerPeso(datos[0], datos[1]);
        if (!grafo.modificarPeso(datos[0], datos[1], datos[2])) {
            mostrarError("No existe la conexión " + nombre(datos[0]) + " - " + nombre(datos[1]) + ".");
            return;
        }
        refrescarVistas();
        ventana.getSeccionResultados().mostrarTexto("Peso de " + nombre(datos[0]) + " - " + nombre(datos[1])
                + " cambiado de " + anterior + " a " + datos[2] + ".");
    }

    // Elimina la arista entre origen y destino
    private void manejarEliminarArista() {
        int[] datos = leerArista(false);
        if (datos == null) return;
        Grafo grafo = grafoActivo();
        if (!grafo.existeArista(datos[0], datos[1])) {
            mostrarError("No existe la conexión " + nombre(datos[0]) + " - " + nombre(datos[1]) + ".");
            return;
        }
        grafo.eliminarArista(datos[0], datos[1]);
        refrescarVistas();
        ventana.getSeccionResultados().mostrarTexto("Conexión " + nombre(datos[0]) + " - "
                + nombre(datos[1]) + " eliminada.");
    }

    // Al hacer clic en un vertice del dibujo, llena origen o destino automaticamente
    private void manejarSeleccionVertice(int valor) {
        String origen = ventana.getVentanaGrafo().getTextoOrigen();
        String destino = ventana.getVentanaGrafo().getTextoDestino();
        String nombreVertice = nombre(valor);

        // Primer clic llena origen, segundo clic llena destino y al tercero reinicia
        if (origen.isEmpty() || !destino.isEmpty()) {
            ventana.getVentanaGrafo().setTextoOrigen(nombreVertice);
            ventana.getVentanaGrafo().setTextoDestino("");
            ventana.getVentanaGrafo().setTextoPeso("");
            return;
        }
        ventana.getVentanaGrafo().setTextoDestino(nombreVertice);
        Integer idOrigen = resolverVertice(origen);
        Grafo grafo = grafoActivo();
        if (grafo != null && idOrigen != null && grafo.existeArista(idOrigen, valor)) {
            ventana.getVentanaGrafo().setTextoPeso(String.valueOf(grafo.obtenerPeso(idOrigen, valor)));
        }
    }

    // Cambia la tabla para mostrar la lista de adyacencia del grafo activo
    private void manejarVerListaAdyacencia() {
        vistaTabla = VISTA_LISTA;
        refrescarTabla();
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

    // Si no hay grafo activo, crea uno vacio para poder agregar vertices a mano
    private Grafo asegurarGrafoActivo() {
        if (grafoActivo() == null) {
            registrarGrafo(new Grafo("Manual", ventana.getSeccionSuperior().esDirigido()), new MapaVertices());
        }
        return grafoActivo();
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

    // Lee y valida origen, destino y peso desde los campos de la ventana del grafo
    private int[] leerArista(boolean conPeso) {
        Grafo grafo = grafoActivo();
        if (grafo == null) {
            mostrarError("No hay un grafo activo. Agrega vértices primero.");
            return null;
        }
        String textoOrigen = ventana.getVentanaGrafo().getTextoOrigen();
        String textoDestino = ventana.getVentanaGrafo().getTextoDestino();
        Integer origen = resolverVertice(textoOrigen);
        Integer destino = resolverVertice(textoDestino);
        if (origen == null || destino == null) {
            mostrarError("El vértice " + (origen == null ? "de origen \"" + textoOrigen : "de destino \"" + textoDestino)
                    + "\" no existe. Agrégalo primero con \"+ Vértice\".");
            return null;
        }
        if (origen.equals(destino)) {
            mostrarError("No se permiten conexiones de un vértice consigo mismo.");
            return null;
        }
        int peso = 0;
        if (conPeso) {
            Integer p = parsearEntero(ventana.getVentanaGrafo().getTextoPeso());
            if (p == null || p < 0) {
                mostrarError("El peso debe ser un número entero no negativo.");
                return null;
            }
            peso = p;
        }
        return new int[]{origen, destino, peso};
    }

    private int siguienteIdLibre(Grafo grafo, MapaVertices mapa) {
        int id = 0;
        for (int existente : grafo.obtenerIds()) {
            if (existente >= id) id = existente + 1;
        }
        while (grafo.existeVertice(id) || mapa.contieneId(id)) id++;
        return id;
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

    private String unirNombres(ArrayList<Integer> ids, String separador) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            if (i > 0) sb.append(separador);
            sb.append(nombre(ids.get(i)));
        }
        return sb.toString();
    }

    private boolean tieneNombresNoNumericos(Grafo grafo, MapaVertices mapa) {
        for (int id : grafo.obtenerIds()) {
            if (!mapa.getNombre(id).equals(String.valueOf(id))) return true;
        }
        return false;
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

    // Resalta una ruta en el dibujo y la guarda para repintarla si se redibuja
    private void resaltar(ArrayList<Integer> ruta) {
        rutaResaltada = ruta;
        ventana.getVentanaGrafo().resaltarRuta(ruta);
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

    // Corre una tarea en otro hilo con pila grande para que no se trabe la interfaz ni de StackOverflow
    private void ejecutarConPilaGrande(Runnable trabajo, java.util.function.Consumer<String> alFallar) {
        Thread hilo = new Thread(null, () -> {
            try {
                trabajo.run();
            } catch (Throwable t) {
                String mensaje = t.getClass().getSimpleName() + (t.getMessage() == null ? "" : ": " + t.getMessage());
                Platform.runLater(() -> alFallar.accept(mensaje));
            }
        }, "trabajo-algoritmos", TAMANIO_PILA);
        hilo.setDaemon(true);
        hilo.start();
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
