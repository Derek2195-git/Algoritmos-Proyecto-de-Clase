module com.example.proyectofinal_algoritmos {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.proyectofinal_algoritmos to javafx.fxml;
    exports com.example.proyectofinal_algoritmos;
}