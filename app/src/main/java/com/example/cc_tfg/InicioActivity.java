package com.example.cc_tfg;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InicioActivity extends AppCompatActivity {

    private Button btncompra, btnvender, btncomparar, btnperfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.inicio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btncompra = findViewById(R.id.btncompra);
        btnvender = findViewById(R.id.btnvender);
        btncomparar = findViewById(R.id.btncomparar);
        btnperfil= findViewById(R.id.btnperfil);

        btncompra.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, ComprarActivity.class);
            startActivity(intent);
        });
        btnvender.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, VenderActivity.class);
            startActivity(intent);
        });
        btncomparar.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, CompararActivity.class);
            startActivity(intent);
        });
        btnperfil.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, PerfilActivity.class);
            startActivity(intent);
        });
    }
}