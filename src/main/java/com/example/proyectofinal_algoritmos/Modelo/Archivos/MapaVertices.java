package com.example.proyectofinal_algoritmos.Modelo.Archivos;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Esta clase relaciona el nombre de cada ubicación
 * con el id entero que usa la clase Grafo.
 */
public class MapaVertices {
    private final HashMap<String, Integer> idPorNombre;
    private final HashMap<Integer, String> nombrePorId;
    private int siguienteId;

    public MapaVertices() {
        idPorNombre = new HashMap<>();
        nombrePorId = new HashMap<>();
        siguienteId = 0;
    }

    /**
     * Devuelve el id del nombre (si no existe, le asigna uno nuevo).
     * @param nombre nombre de la ubicación
     * @return id asociado al nombre
     */
    public int obtenerOAsignarId(String nombre) {
        Integer id = idPorNombre.get(nombre);
        if (id != null) {
            return id;
        }
        int nuevo = siguienteId++;
        idPorNombre.put(nombre, nuevo);
        nombrePorId.put(nuevo, nombre);
        return nuevo;
    }

    /**
     * Registra un nombre con un id específico.
     * @param nombre nombre de la ubicación
     * @param id id del vértice
     * @return false si el nombre o el id ya estaban ocupados
     */
    public boolean registrar(String nombre, int id) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        if (idPorNombre.containsKey(nombre) || nombrePorId.containsKey(id)) {
            return false;
        }
        idPorNombre.put(nombre, id);
        nombrePorId.put(id, nombre);
        if (id >= siguienteId) {
            siguienteId = id + 1;
        }
        return true;
    }

    public boolean contieneNombre(String nombre) {
        return idPorNombre.containsKey(nombre);
    }

    public boolean contieneId(int id) {
        return nombrePorId.containsKey(id);
    }

    /**
     * Cambia el nombre de una ubicación conservando su id.
     * @param nombreActual nombre registrado
     * @param nombreNuevo nombre que lo reemplaza
     * @return true si se pudo renombrar
     */
    public boolean renombrar(String nombreActual, String nombreNuevo) {
        if (nombreNuevo == null || nombreNuevo.trim().isEmpty()) {
            return false;
        }
        if (!idPorNombre.containsKey(nombreActual) || idPorNombre.containsKey(nombreNuevo)) {
            return false;
        }
        int id = idPorNombre.remove(nombreActual);
        idPorNombre.put(nombreNuevo, id);
        nombrePorId.put(id, nombreNuevo);
        return true;
    }

    /**
     * Elimina un nombre del mapa (su id no se reutiliza).
     * @param nombre nombre de la ubicación
     * @return true si existía y se eliminó
     */
    public boolean eliminar(String nombre) {
        Integer id = idPorNombre.remove(nombre);
        if (id == null) {
            return false;
        }
        nombrePorId.remove(id);
        return true;
    }

    public int cantidad() {
        return idPorNombre.size();
    }

    /**
     * Obtiene todos los nombres registrados.
     * @return lista de nombres
     */
    public ArrayList<String> getNombres() {
        return new ArrayList<>(idPorNombre.keySet());
    }

    /**
     * Busca el id de un nombre.
     * @param nombre nombre de la ubicación
     * @return id del nombre o -1 si no está registrado
     */
    public int getId(String nombre) {
        Integer id = idPorNombre.get(nombre);
        return id == null ? -1 : id;
    }

    /**
     * Busca el nombre de un id.
     * @param id id del vértice
     * @return nombre registrado (o el id como texto si no tiene nombre)
     */
    public String getNombre(int id) {
        String nombre = nombrePorId.get(id);
        return nombre == null ? String.valueOf(id) : nombre;
    }
}
