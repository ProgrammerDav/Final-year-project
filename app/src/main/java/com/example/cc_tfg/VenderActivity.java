package com.example.cc_tfg;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
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

public class VenderActivity extends AppCompatActivity {

    private LinearLayout layoutModificaciones;
    private boolean opcionalesVisibles = false;
    private TextView regmarca, regmodelo, regcolor, regpuertas, regcaballos, regkilometraje, regantiguedad, regprecio,
            regm_suspension, regm_motor, regm_escape, regm_llantas, regm_luces, regm_antivuelco, regm_remolque;
    private Button btnmodificaciones, btnguardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.vender);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        regmarca = findViewById(R.id.regmarca);
        regmodelo = findViewById(R.id.regmodelo);
        regcolor = findViewById(R.id.regcolor);
        regpuertas = findViewById(R.id.regpuertas);
        regcaballos = findViewById(R.id.regcaballos);
        regkilometraje = findViewById(R.id.regkilometraje);
        regantiguedad = findViewById(R.id.regantiguedad);
        regprecio = findViewById(R.id.regprecio);

        btnmodificaciones = findViewById(R.id.btnmodificaciones);
        btnguardar = findViewById(R.id.btnguardar);
        layoutModificaciones = findViewById(R.id.layoutModificaciones);
        regm_suspension = findViewById(R.id.regm_suspension);
        regm_motor = findViewById(R.id.regm_motor);
        regm_escape = findViewById(R.id.regm_escape);
        regm_llantas = findViewById(R.id.regm_llantas);
        regm_luces = findViewById(R.id.regm_luces);
        regm_antivuelco = findViewById(R.id.regm_antivuelco);
        regm_remolque = findViewById(R.id.regm_remolque);


        btnmodificaciones.setOnClickListener(v -> {
            opcionalesVisibles = !opcionalesVisibles;
            layoutModificaciones.setVisibility(opcionalesVisibles ? View.VISIBLE : View.GONE);
            btnmodificaciones.setText(opcionalesVisibles ?
                    "Ocultar modificaciones" :
                    "Mostrar modificaciones");
        });

        btnguardar.setOnClickListener(v -> {
            String marca = PrimeraMayuscula(regmarca.getText().toString());
            String modelo = PrimeraMayuscula(regmodelo.getText().toString());
            String color = PrimeraMayuscula(regcolor.getText().toString());
            String puertasStr = regpuertas.getText().toString().trim();
            String caballosStr = regcaballos.getText().toString().trim();
            String kilometrajeStr = regkilometraje.getText().toString().trim();
            String antiguedad = PrimeraMayuscula(regantiguedad.getText().toString());
            String precioStr = regprecio.getText().toString().trim();

            String suspension = PrimeraMayuscula(regm_suspension.getText().toString());
            String motor = PrimeraMayuscula(regm_motor.getText().toString());
            String escape = PrimeraMayuscula(regm_escape.getText().toString());
            String llantas = PrimeraMayuscula(regm_llantas.getText().toString());
            String luces = PrimeraMayuscula(regm_luces.getText().toString());
            String antivuelco = PrimeraMayuscula(regm_antivuelco.getText().toString());
            String remolque = PrimeraMayuscula(regm_remolque.getText().toString());

            // Validar campos obligatorios
            if (marca.isEmpty() || modelo.isEmpty() || color.isEmpty() || puertasStr.isEmpty() ||
                    caballosStr.isEmpty() || kilometrajeStr.isEmpty() || antiguedad.isEmpty() || precioStr.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int puertas = Integer.parseInt(puertasStr);
                int caballos = Integer.parseInt(caballosStr);
                int kilometraje = Integer.parseInt(kilometrajeStr);
                int precio = Integer.parseInt(precioStr);

                //Obtener ID_USUARIO del usuario logueado
                SharedPreferences prefs = getSharedPreferences("usuario_prefs", MODE_PRIVATE);
                String usuario = prefs.getString("USUARIO", null);

                if (usuario == null) {
                    Toast.makeText(this, "No se encontró usuario logueado", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseHelper dbHelper = new DatabaseHelper(this);
                SQLiteDatabase db = dbHelper.openDatabase();
                Cursor cursor = db.rawQuery("SELECT ID_USUARIO FROM Usuarios WHERE USUARIO = ?", new String[]{usuario});
                int idUsuario = -1;
                if (cursor.moveToFirst()) {
                    idUsuario = cursor.getInt(0);
                }
                cursor.close();
                db.close();

                if (idUsuario == -1) {
                    Toast.makeText(this, "Error al obtener el ID del usuario", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseHelper.RegistrarDatos registro = new DatabaseHelper.RegistrarDatos(this);

                // Registrar vehículo
                long idVehiculo = registro.registrarVehiculo(
                        idUsuario, marca, modelo, color, puertas, caballos, kilometraje, antiguedad, precio
                );

                if (idVehiculo != -1) {
                    Toast.makeText(this, "Vehículo registrado", Toast.LENGTH_SHORT).show();

                    // Registrar modificaciones opcionales
                    boolean exito = registro.registrarModificaciones(
                            (int) idVehiculo, suspension, motor, escape, llantas, luces, antivuelco, remolque
                    );

                    if (exito) {
                        Toast.makeText(this, "Modificaciones registradas", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Sin modificaciones o error al registrarlas", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "Error al registrar vehículo", Toast.LENGTH_SHORT).show();
                }

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Verifica los campos numéricos", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            // Volver al inicio
            Intent intent = new Intent(VenderActivity.this, InicioActivity.class);
            startActivity(intent);
        });

    }
    private String PrimeraMayuscula (String texto) {
        if (texto == null || texto.isEmpty()) return "";
        texto = texto.trim();
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }
}