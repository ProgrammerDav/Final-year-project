package com.example.cc_tfg;

import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.cc_tfg.Conexion.DatabaseHelper;
import java.util.HashMap;

public class DetallesActivity extends AppCompatActivity {

    TextView tvInfoVehiculo;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.detalles);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvInfoVehiculo = findViewById(R.id.tvInfoVehiculo);
        dbHelper = new DatabaseHelper(this);

        String vehiculoId = getIntent().getStringExtra("vehiculo_id");

        if (vehiculoId != null) {
            HashMap<String, String> datos = dbHelper.MostrarInfoVehiculo(vehiculoId);

            if (!datos.isEmpty()) {
                StringBuilder info = new StringBuilder();

                // Datos del vehículo
                info.append("Vehículo:\n");
                info.append(" º Marca: ").append(datos.get("MARCA")).append("\n");
                info.append(" º Modelo: ").append(datos.get("MODELO")).append("\n");
                info.append(" º Color: ").append(datos.get("COLOR")).append("\n");
                info.append(" º Puertas: ").append(datos.get("N_PUERTAS")).append("\n");
                info.append(" º Caballos: ").append(datos.get("CABALLOS")).append("\n");
                info.append(" º Kilometraje: ").append(datos.get("KILOMETRAJE")).append("\n");
                info.append(" º Antigüedad: ").append(datos.get("ANTIGUEDAD")).append("\n");
                info.append(" º Precio: ").append(datos.get("PRECIO")).append(" €\n");

                // Datos de modificaciones
                boolean hayMods = false;
                StringBuilder mods = new StringBuilder("\nModificaciones:\n");
                String[] modsKeys = {"SUSPENSION", "MOTOR", "T_ESCAPE", "LLANTAS", "LUCES", "ANTIVUELCO", "REMOLQUE"};

                for (String key : modsKeys) {
                    if (datos.containsKey(key)) {
                        hayMods = true;
                        mods.append(" º ")
                                .append(formatearNombre(key))
                                .append(": ")
                                .append(datos.get(key))
                                .append("\n");
                    }
                }

                if (hayMods) {
                    info.append(mods);
                } else {
                    info.append("\n(No se registraron modificaciones)");
                }

                // Datos del usuario asociado
                info.append("\nUsuario asociado:\n");
                info.append(" º Nombre: ").append(datos.get("NAME")).append("\n");
                info.append(" º Apellidos: ").append(datos.get("LASTNAME")).append("\n");
                info.append(" º Móvil: ").append(datos.get("MOVIL")).append("\n");

                tvInfoVehiculo.setText(info.toString());
            } else {
                tvInfoVehiculo.setText("No se encontró información del vehículo.");
            }
        } else {
            tvInfoVehiculo.setText("Error: ID de vehículo no recibido.");
        }
    }

    private String formatearNombre(String texto) {
        texto = texto.replace("T_", "Tubo ").toLowerCase();
        if (texto.isEmpty()) return texto;
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }
}
