package com.example.cc_tfg;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.cc_tfg.Conexion.DatabaseHelper;
import java.util.HashMap;

public class PerfilActivity extends AppCompatActivity {

    TextView txtName, txtLastname, txtUsuario, txtMovil;
    LinearLayout contenedorVehiculos;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtName = findViewById(R.id.txtName);
        txtLastname = findViewById(R.id.txtLastname);
        txtUsuario = findViewById(R.id.txtUsuario);
        txtMovil = findViewById(R.id.txtMovil);
        contenedorVehiculos = findViewById(R.id.contenedorVehiculos);

        dbHelper = new DatabaseHelper(this);

        SharedPreferences prefs = getSharedPreferences("usuario_prefs", MODE_PRIVATE);
        String usuario = prefs.getString("USUARIO", null);

        if (usuario != null) {
            cargarDatosUsuario(usuario);
            cargarVehiculosUsuario(usuario);
        } else {
            Toast.makeText(this, "No hay usuario guardado", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarDatosUsuario(String usuario) {
        HashMap<String, String> datos = dbHelper.obtenerDatosUsuario(usuario);

        txtName.setText("Nombre: " + datos.get("NAME"));
        txtLastname.setText("Apellido: " + datos.get("LASTNAME"));
        txtUsuario.setText("Usuario: " + datos.get("USUARIO"));
        txtMovil.setText("Móvil: " + datos.get("MOVIL"));
    }


    private void cargarVehiculosUsuario(String usuario) {

        contenedorVehiculos.removeAllViews();

        var lista = dbHelper.obtenerVehiculosDeUsuario(usuario);

        if (lista.isEmpty()) {
            TextView noVehiculos = new TextView(this);
            noVehiculos.setText("No tienes vehículos registrados.");
            contenedorVehiculos.addView(noVehiculos);
            return;
        }

        for (HashMap<String, String> vehiculo : lista) {

            int idVehiculo = Integer.parseInt(vehiculo.get("ID_VEHICULO"));
            String marca = vehiculo.get("MARCA");
            String modelo = vehiculo.get("MODELO");
            String color = vehiculo.get("COLOR");

            Button btnVehiculo = new Button(this);
            btnVehiculo.setText(marca + " " + modelo + " (" + color + ")");
            btnVehiculo.setAllCaps(false);
            btnVehiculo.setBackgroundResource(R.drawable.botoncustom);
            btnVehiculo.setTextColor(Color.WHITE);
            btnVehiculo.setTextSize(16);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            int margin = (int) (10 * getResources().getDisplayMetrics().density);
            params.setMargins(0, margin, 0, 0);
            btnVehiculo.setLayoutParams(params);

            btnVehiculo.setOnClickListener(v -> {
                mostrarDialogoVehiculo(String.valueOf(idVehiculo));
            });

            contenedorVehiculos.addView(btnVehiculo);
        }
    }


    private void mostrarDialogoVehiculo(String idVehiculo) {
        HashMap<String, String> datos = dbHelper.MostrarInfoVehiculo(idVehiculo);

        if (datos.isEmpty()) {
            Toast.makeText(this, "Error al obtener la información del vehículo", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder info = new StringBuilder();
        info.append("Marca: ").append(datos.get("MARCA")).append("\n");
        info.append("Modelo: ").append(datos.get("MODELO")).append("\n");
        info.append("Color: ").append(datos.get("COLOR")).append("\n");
        info.append("Puertas: ").append(datos.get("N_PUERTAS")).append("\n");
        info.append("Caballos: ").append(datos.get("CABALLOS")).append("\n");
        info.append("Kilometraje: ").append(datos.get("KILOMETRAJE")).append("\n");
        info.append("Antigüedad: ").append(datos.get("ANTIGUEDAD")).append("\n");
        info.append("Precio: ").append(datos.get("PRECIO")).append(" €\n\n");

        if (datos.containsKey("SUSPENSION") || datos.containsKey("MOTOR")) {
            info.append("Modificaciones:\n");
            if (datos.containsKey("SUSPENSION")) info.append("Suspensión: ").append(datos.get("SUSPENSION")).append("\n");
            if (datos.containsKey("MOTOR")) info.append("Motor: ").append(datos.get("MOTOR")).append("\n");
            if (datos.containsKey("T_ESCAPE")) info.append("Tubo de escape: ").append(datos.get("T_ESCAPE")).append("\n");
            if (datos.containsKey("LLANTAS")) info.append("Llantas: ").append(datos.get("LLANTAS")).append("\n");
            if (datos.containsKey("LUCES")) info.append("Luces: ").append(datos.get("LUCES")).append("\n");
            if (datos.containsKey("ANTIVUELCO")) info.append("Antivuelco: ").append(datos.get("ANTIVUELCO")).append("\n");
            if (datos.containsKey("REMOLQUE")) info.append("Remolque: ").append(datos.get("REMOLQUE")).append("\n");
        }

        new AlertDialog.Builder(this)
                .setTitle("Información del vehículo")
                .setMessage(info.toString())
                .setPositiveButton("Cerrar", null)
                .setNegativeButton("Eliminar", (dialog, which) -> {
                    eliminarVehiculo(idVehiculo);
                })
                .show();
    }

    private void eliminarVehiculo(String idVehiculo) {

        boolean eliminado = dbHelper.eliminarVehiculo(idVehiculo);

        if (eliminado) {
            Toast.makeText(this, "Vehículo eliminado correctamente", Toast.LENGTH_SHORT).show();

            SharedPreferences prefs = getSharedPreferences("usuario_prefs", MODE_PRIVATE);
            String usuario = prefs.getString("USUARIO", null);

            if (usuario != null) cargarVehiculosUsuario(usuario);

        } else {
            Toast.makeText(this, "Error al eliminar el vehículo", Toast.LENGTH_SHORT).show();
        }
    }
}