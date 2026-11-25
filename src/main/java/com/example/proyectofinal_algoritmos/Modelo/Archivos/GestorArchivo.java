package com.example.proyectofinal_algoritmos.Modelo.Archivos;

import com.example.proyectofinal_algoritmos.Modelo.Grafos.Arista;
import com.example.proyectofinal_algoritmos.Modelo.Grafos.Grafo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * La clase lee, valida y escribe archivos CSV con el formato origen,destino,peso.
 */
public class GestorArchivo {
    public static final String COL_ORIGEN = "origen";
    public static final String COL_DESTINO = "destino";
    public static final String COL_PESO = "peso";
    public static final String ENCABEZADO = COL_ORIGEN + "," + COL_DESTINO + "," + COL_PESO;

    private static final char BOM = '\uFEFF';

    /**
     * Lee y valida un CSV sin construir el grafo.
     * @param archivo archivo CSV
     * @param dirigido si el grafo es dirigido (afecta la detección de duplicados)
     * @return resultado con las conexiones válidas y los problemas encontrados
     */
    public ResultadoImportacion leerConexiones(File archivo, boolean dirigido) {
        String nombre = archivo == null ? "(sin archivo)" : archivo.getName();
        ResultadoImportacion resultado = new ResultadoImportacion(nombre, dirigido);

        if (archivo == null || !archivo.isFile() || !archivo.canRead()) {
            resultado.agregarProblema(new ErrorImportacion(0, ErrorImportacion.ARCHIVO,
                    "El archivo no existe o no se puede leer."));
            return resultado;
        }

        try (BufferedReader lector = new BufferedReader(new FileReader(archivo, StandardCharsets.UTF_8))) {
            int numLinea = 0;
            String linea;

            // El encabezado es la primera línea no vacía
            do {
                linea = lector.readLine();
                numLinea++;
            } while (linea != null && linea.trim().isEmpty());

            if (linea == null) {
                resultado.agregarProblema(new ErrorImportacion(0, ErrorImportacion.ENCABEZADO,
                        "El archivo está vacío."));
                return resultado;
            }

            if (!linea.isEmpty() && linea.charAt(0) == BOM) {
                linea = linea.substring(1);
            }
            char separador = detectarSeparador(linea);
            int[] indices = leerEncabezado(linea, separador, numLinea, resultado);
            if (indices == null) {
                return resultado;
            }
            resultado.setEncabezadoValido(true);

            int columnasRequeridas = Math.max(indices[0], Math.max(indices[1], indices[2])) + 1;
            HashMap<String, ConexionCSV> vistas = new HashMap<>();

            while ((linea = lector.readLine()) != null) {
                numLinea++;
                if (linea.trim().isEmpty()) {
                    continue;
                }
                resultado.incrementarRegistrosLeidos();
                procesarRegistro(linea, separador, numLinea, indices, columnasRequeridas,
                        vistas, resultado);
            }
        } catch (IOException e) {
            resultado.agregarProblema(new ErrorImportacion(0, ErrorImportacion.ARCHIVO,
                    "Error al leer el archivo: " + e.getMessage()));
        }
        return resultado;
    }

    /**
     * Busca las columnas origen, destino y peso en el encabezado.
     * @param linea línea del encabezado
     * @param separador separador de columnas
     * @param numLinea número de línea del encabezado
     * @param resultado resultado donde se registran los errores
     * @return índices {origen, destino, peso} o null si falta alguna columna
     */
    private int[] leerEncabezado(String linea, char separador, int numLinea,
                                 ResultadoImportacion resultado) {
        ArrayList<String> columnas = separarCampos(linea, separador);
        int[] indices = {-1, -1, -1};
        String[] requeridas = {COL_ORIGEN, COL_DESTINO, COL_PESO};

        for (int i = 0; i < columnas.size(); i++) {
            String col = columnas.get(i).trim().toLowerCase();
            for (int r = 0; r < requeridas.length; r++) {
                if (col.equals(requeridas[r]) && indices[r] == -1) {
                    indices[r] = i;
                }
            }
        }

        StringBuilder faltantes = new StringBuilder();
        for (int r = 0; r < requeridas.length; r++) {
            if (indices[r] == -1) {
                if (faltantes.length() > 0) faltantes.append(", ");
                faltantes.append(requeridas[r]);
            }
        }
        if (faltantes.length() > 0) {
            resultado.agregarProblema(new ErrorImportacion(numLinea, ErrorImportacion.ENCABEZADO,
                    "Faltan las columnas: " + faltantes + ". Se esperaba el encabezado \""
                            + ENCABEZADO + "\" y se encontró \"" + linea.trim() + "\"."));
            return null;
        }
        return indices;
    }

    /**
     * Valida una línea de datos y si es correcta, la agrega a las conexiones válidas.
     * @param linea línea a validar
     * @param separador separador de columnas
     * @param numLinea número de línea en el archivo
     * @param indices posición de las columnas origen, destino y peso
     * @param columnasRequeridas mínimo de columnas que debe tener la línea
     * @param vistas conexiones ya registradas, para detectar duplicados
     * @param resultado resultado donde se guardan conexiones y errores
     */
    private void procesarRegistro(String linea, char separador, int numLinea, int[] indices,
                                  int columnasRequeridas, HashMap<String, ConexionCSV> vistas,
                                  ResultadoImportacion resultado) {
        ArrayList<String> campos = separarCampos(linea, separador);

        if (campos.size() < columnasRequeridas) {
            resultado.agregarProblema(new ErrorImportacion(numLinea,
                    ErrorImportacion.REGISTRO_INCOMPLETO,
                    "Se esperaban " + columnasRequeridas + " columnas y se encontraron "
                            + campos.size() + ": \"" + linea.trim() + "\"."));
            return;
        }

        String origen = campos.get(indices[0]).trim();
        String destino = campos.get(indices[1]).trim();
        String textoPeso = campos.get(indices[2]).trim();

        if (textoPeso.isEmpty() || (origen.isEmpty() && destino.isEmpty())) {
            resultado.agregarProblema(new ErrorImportacion(numLinea,
                    ErrorImportacion.REGISTRO_INCOMPLETO,
                    "Faltan datos en el registro: \"" + linea.trim() + "\"."));
            return;
        }

        if (origen.isEmpty() || destino.isEmpty()) {
            resultado.agregarProblema(new ErrorImportacion(numLinea,
                    ErrorImportacion.VERTICE_VACIO,
                    "El vértice de " + (origen.isEmpty() ? "origen" : "destino") + " está vacío."));
            return;
        }

        Integer peso = convertirPeso(textoPeso, numLinea, resultado);
        if (peso == null) {
            return;
        }

        if (origen.equals(destino)) {
            resultado.agregarProblema(new ErrorImportacion(numLinea, ErrorImportacion.LAZO,
                    "El vértice \"" + origen + "\" se conecta consigo mismo."));
            return;
        }

        ConexionCSV conexion = new ConexionCSV(origen, destino, peso, numLinea);
        String llave = conexion.generarLlave(resultado.isDirigido());
        ConexionCSV anterior = vistas.get(llave);
        if (anterior != null) {
            String detalle = anterior.getPeso() == peso ? "" :
                    " con distinto peso (" + anterior.getPeso() + " vs " + peso + ")";
            resultado.agregarProblema(new ErrorImportacion(numLinea, ErrorImportacion.DUPLICADO,
                    "La conexión " + origen + " - " + destino + " ya aparece en la línea "
                            + anterior.getLinea() + detalle + "; se conserva la primera."));
            return;
        }

        vistas.put(llave, conexion);
        resultado.agregarConexion(conexion);
    }

    /**
     * Convierte el texto del peso a un entero no negativo.
     * @param texto texto del peso
     * @param numLinea número de línea en el archivo
     * @param resultado resultado donde se registran los errores
     * @return peso convertido o null si no es válido
     */
    private Integer convertirPeso(String texto, int numLinea, ResultadoImportacion resultado) {
        double valor;
        try {
            valor = Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            resultado.agregarProblema(new ErrorImportacion(numLinea,
                    ErrorImportacion.PESO_NO_NUMERICO,
                    "El peso \"" + texto + "\" no es un número."));
            return null;
        }
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            resultado.agregarProblema(new ErrorImportacion(numLinea,
                    ErrorImportacion.PESO_NO_NUMERICO,
                    "El peso \"" + texto + "\" no es un número válido."));
            return null;
        }
        if (valor < 0) {
            resultado.agregarProblema(new ErrorImportacion(numLinea,
                    ErrorImportacion.PESO_NEGATIVO,
                    "El peso " + texto + " es negativo; los pesos deben ser no negativos."));
            return null;
        }
        if (valor != Math.floor(valor) || valor > Integer.MAX_VALUE) {
            resultado.agregarProblema(new ErrorImportacion(numLinea,
                    ErrorImportacion.PESO_NO_NUMERICO,
                    "El peso \"" + texto + "\" debe ser un número entero."));
            return null;
        }
        return (int) valor;
    }

    /**
     * Separa una línea en campos respetando los textos entre comillas dobles.
     * @param linea línea a separar
     * @param separador separador de columnas
     * @return lista de campos
     */
    static ArrayList<String> separarCampos(String linea, char separador) {
        ArrayList<String> campos = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean entreComillas = false;
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (c == '"') {
                if (entreComillas && i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
                    actual.append('"');
                    i++;
                } else {
                    entreComillas = !entreComillas;
                }
            } else if (c == separador && !entreComillas) {
                campos.add(actual.toString());
                actual.setLength(0);
            } else {
                actual.append(c);
            }
        }
        campos.add(actual.toString());
        return campos;
    }

    /**
     * Detecta si el archivo usa ',' o ';' como separador.
     * @param encabezado línea del encabezado
     * @return separador detectado
     */
    private char detectarSeparador(String encabezado) {
        return encabezado.indexOf(',') < 0 && encabezado.indexOf(';') >= 0 ? ';' : ',';
    }

    /**
     * Guarda un grafo en formato CSV (los vértices sin conexiones no se guardan).
     * @param grafo grafo a guardar
     * @param mapa nombres de los vértices; si es null se escriben los ids
     * @param archivo archivo destino
     * @return número de conexiones escritas
     * @throws IOException si no se puede escribir el archivo
     */
    public int guardarGrafo(Grafo grafo, MapaVertices mapa, File archivo) throws IOException {
        ArrayList<Arista> aristas = grafo.obtenerAristas();
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo, StandardCharsets.UTF_8))) {
            escritor.write(ENCABEZADO);
            escritor.newLine();
            for (int i = 0; i < aristas.size(); i++) {
                Arista a = aristas.get(i);
                String origen = mapa == null ? String.valueOf(a.getOrigen()) : mapa.getNombre(a.getOrigen());
                String destino = mapa == null ? String.valueOf(a.getDestino()) : mapa.getNombre(a.getDestino());
                escribirRegistro(escritor, origen, destino, a.getPeso());
            }
        }
        return aristas.size();
    }

    private void escribirRegistro(BufferedWriter escritor, String origen, String destino, int peso)
            throws IOException {
        escritor.write(escaparCampo(origen) + "," + escaparCampo(destino) + "," + peso);
        escritor.newLine();
    }

    /**
     * Guarda una lista de conexiones en formato CSV.
     * @param conexiones conexiones a guardar
     * @param archivo archivo destino
     * @throws IOException si no se puede escribir el archivo
     */
    public void guardarConexiones(ArrayList<ConexionCSV> conexiones, File archivo) throws IOException {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo, StandardCharsets.UTF_8))) {
            escritor.write(ENCABEZADO);
            escritor.newLine();
            for (int i = 0; i < conexiones.size(); i++) {
                ConexionCSV c = conexiones.get(i);
                escribirRegistro(escritor, c.getOrigen(), c.getDestino(), c.getPeso());
            }
        }
    }

    /**
     * Encierra el texto entre comillas si contiene comas, comillas o punto y coma.
     * @param texto texto del campo
     * @return texto listo para escribirse en el CSV
     */
    public static String escaparCampo(String texto) {
        if (texto == null) return "";
        if (texto.indexOf(',') >= 0 || texto.indexOf('"') >= 0 || texto.indexOf(';') >= 0) {
            return "\"" + texto.replace("\"", "\"\"") + "\"";
        }
        return texto;
    }
}
