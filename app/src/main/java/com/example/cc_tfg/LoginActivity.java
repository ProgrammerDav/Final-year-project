package com.example.cc_tfg;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.cc_tfg.Conexion.DatabaseHelper;
import java.io.IOException;

public class LoginActivity extends AppCompatActivity {
    private EditText txtUsuario, txtPassword;
    private Button btnInicio;
    private TextView txtregistro;
    private DatabaseHelper dbHelper;
    private DatabaseHelper.ValidarUsuario validador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtUsuario = findViewById(R.id.registrousuario);
        txtPassword = findViewById(R.id.registropssw);
        btnInicio = findViewById(R.id.btniniciar);
        txtregistro = findViewById(R.id.txtregistro);

        dbHelper = new DatabaseHelper(this);
        validador = new DatabaseHelper.ValidarUsuario(this);

        //Crea la base de datos si no existe
        try {
            dbHelper.createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al crear la base de datos", Toast.LENGTH_SHORT).show();
        }


        btnInicio.setOnClickListener(v -> {
            String usuario = txtUsuario.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();

            if (usuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean valido = validador.validarUsuario(usuario, password);

            if (valido) {
                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

                // Guarda el usuario logueado
                getSharedPreferences("usuario_prefs", MODE_PRIVATE)
                        .edit()
                        .putString("USUARIO", usuario)
                        .apply();

                // Abre el inicio
                Intent intent = new Intent(LoginActivity.this, InicioActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }

        });
        txtregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(reg);
            }
        });
    }
}
