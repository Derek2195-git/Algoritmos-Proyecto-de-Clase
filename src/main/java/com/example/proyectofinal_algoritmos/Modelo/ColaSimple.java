Cpackage com.example.proyectofinal_algoritmos.Modelo;

public class ColaSimple<T> {
    T[] objetos;
    int inicio, fin;

    public ColaSimple(int capacidad) {
        objetos = (T[]) new Object[capacidad];
        inicio = -1;
        fin = -1;
    }

    public void insertarDato(T dato) {
        if(fin < objetos.length - 1) {
            fin += 1;
            objetos[fin] = dato;
            if (fin == 0) inicio = 0;
        }
    }

    public T eliminarDato() {
        T dato = null;
        if (inicio != -1) {
            dato = objetos[inicio];
            if (inicio == fin) {
                inicio = -1;
                fin = -1;
            } else {
                inicio += 1;
            }
        }
        return dato;
    }
}
