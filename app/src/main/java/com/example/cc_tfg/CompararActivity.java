package com.example.cc_tfg;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.cc_tfg.Conexion.DatabaseHelper;
import java.util.ArrayList;
import java.util.HashMap;

public class CompararActivity extends AppCompatActivity {

    private Spinner spinnerVehiculo1, spinnerVehiculo2;
    private Button btnComparar;
    private TextView txtResultado;
    private DatabaseHelper dbHelper;

    private ArrayList<HashMap<String, String>> listaVehiculos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.comparar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        spinnerVehiculo1 = findViewById(R.id.spnVehiculo1);
        spinnerVehiculo2 = findViewById(R.id.spnVehiculo2);
        btnComparar = findViewById(R.id.btnComparar);
        txtResultado = findViewById(R.id.txtResultado);

        // copia la DB desde assets si no existe
        dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.createDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        cargarVehiculosEnSpinners();

        btnComparar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos1 = spinnerVehiculo1.getSelectedItemPosition();
                int pos2 = spinnerVehiculo2.getSelectedItemPosition();

                if (pos1 < 0 || pos2 < 0) {
                    txtResultado.setText("Selecciona ambos vehículos.");
                    return;
                }

                String id1 = listaVehiculos.get(pos1).get("id");
                String id2 = listaVehiculos.get(pos2).get("id");

                HashMap<String, String> vehiculo1 = dbHelper.MostrarInfoVehiculo(id1);
                HashMap<String, String> vehiculo2 = dbHelper.MostrarInfoVehiculo(id2);

                String info1 = formatearVehiculo(vehiculo1);
                String info2 = formatearVehiculo(vehiculo2);

                txtResultado.setText("Vehículo 1:\n" + info1 + "\n\nVehículo 2:\n" + info2);
            }
        });
    }

    private void cargarVehiculosEnSpinners() {
        listaVehiculos = new ArrayList<>();
        ArrayList<String> textoVehiculos = new ArrayList<>();

        // Recorremos todas las marcas y sus vehículos
        ArrayList<String> marcas = dbHelper.obtenerMarcasUnicas();
        for (String marca : marcas) {
            ArrayList<HashMap<String, String>> vehiculosPorMarca = dbHelper.obtenerVehiculosPorMarca(marca);
            for (HashMap<String, String> v : vehiculosPorMarca) {
                String texto = marca + " " + v.get("modelo") + " (" + v.get("color") + ")";
                textoVehiculos.add(texto);
                listaVehiculos.add(v);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, textoVehiculos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerVehiculo1.setAdapter(adapter);
        spinnerVehiculo2.setAdapter(adapter);
    }

    private String formatearVehiculo(HashMap<String, String> v) {
        if (v == null || v.isEmpty()) return "Sin datos";

        StringBuilder info = new StringBuilder();
        info.append(" º Marca: ").append(v.get("MARCA")).append("\n");
        info.append(" º Modelo: ").append(v.get("MODELO")).append("\n");
        info.append(" º Color: ").append(v.get("COLOR")).append("\n");
        info.append(" º Puertas: ").append(v.get("N_PUERTAS")).append("\n");
        info.append(" º Caballos: ").append(v.get("CABALLOS")).append("\n");
        info.append(" º Kilometraje: ").append(v.get("KILOMETRAJE")).append("\n");
        info.append(" º Antigüedad: ").append(v.get("ANTIGUEDAD")).append("\n");
        info.append(" º Precio: ").append(v.get("PRECIO")).append(" €").append("\n");

        //Aquí insertamos la sección de modificaciones
        info.append("\nModificaciones:\n");

        boolean hayMods = false;
        String[] modsKeys = {"SUSPENSION", "MOTOR", "T_ESCAPE", "LLANTAS", "LUCES", "ANTIVUELCO", "REMOLQUE"};

        for (String m : modsKeys) {
            if (v.containsKey(m) && v.get(m) != null && !v.get(m).isEmpty()) {
                hayMods = true;
                info.append(" º ")
                        .append(formatearNombre(m))
                        .append(": ")
                        .append(v.get(m))
                        .append("\n");
            }
        }

        if (!hayMods) {
            info.append(" (No se registraron modificaciones)\n");
        }
        return info.toString();
    }


    private String formatearNombre(String texto) {
        texto = texto.replace("T_", "Tubo ").toLowerCase();
        if (texto.isEmpty()) return texto;
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }
}