package com.example.cc_tfg;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.cc_tfg.Conexion.DatabaseHelper;

public class RegistroActivity extends AppCompatActivity {

    private TextView registrousuario, registropssw, registroname, registrolastname, registromovil;
    private Button btnregistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        registrousuario = findViewById(R.id.registrousuario);
        registropssw = findViewById(R.id.registropssw);
        registroname = findViewById(R.id.registroname);
        registrolastname = findViewById(R.id.registrolastname);
        registromovil = findViewById(R.id.registromovil);
        btnregistro = findViewById(R.id.btnregistro);

        DatabaseHelper.RegistrarDatos registro = new DatabaseHelper.RegistrarDatos(this);

        btnregistro.setOnClickListener(v ->{
            String usuario = registrousuario.getText().toString().trim();
            String password = registropssw.getText().toString().trim();
            String nombre = registroname.getText().toString().trim();
            String apellido = registrolastname.getText().toString().trim();
            String movilStr = registromovil.getText().toString().trim();


            if (usuario.isEmpty() || password.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || movilStr.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!movilStr.matches("\\d{9}")) {
                Toast.makeText(this, "Movil no válido", Toast.LENGTH_SHORT).show();
                return;
            }

            int movil = Integer.parseInt(movilStr);

            boolean exito = registro.registrarUsuario(usuario, password, nombre, apellido, movil);
            if (exito) {
                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }
}