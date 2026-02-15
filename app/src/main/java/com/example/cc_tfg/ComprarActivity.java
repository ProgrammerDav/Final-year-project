package com.example.cc_tfg;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.cc_tfg.Conexion.DatabaseHelper;
import java.util.ArrayList;
import java.util.HashMap;

public class ComprarActivity extends AppCompatActivity {

    LinearLayout buttonContainer;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.comprar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonContainer = findViewById(R.id.buttonContainer);
        LinearLayout contenedor = findViewById(R.id.buttonContainer);
        dbHelper = new DatabaseHelper(this);

        ArrayList<String> marcas = dbHelper.obtenerMarcasUnicas();

        for (String marca : marcas) {
            // Contenedor de grupo (marca + detalles)
            LinearLayout grupo = new LinearLayout(this);
            grupo.setOrientation(LinearLayout.VERTICAL);
            grupo.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            // Botón principal (marca)
            Button btnMarca = new Button(this);
            btnMarca.setText(marca);
            btnMarca.setAllCaps(false);
            btnMarca.setTextColor(Color.parseColor("#FFFFFF"));
            btnMarca.setBackgroundResource(R.drawable.botoncustom);
            btnMarca.setPadding(0, 20, 0, 20);
            btnMarca.setTextSize(16);

            // Crear margen superior de 10dp
            LinearLayout.LayoutParams paramsMarca = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            int marginInDp = 10;
            float scale = getResources().getDisplayMetrics().density;
            int marginInPx = (int) (marginInDp * scale + 0.5f);
            paramsMarca.setMargins(0, marginInPx, 0, 0);
            btnMarca.setLayoutParams(paramsMarca);

            // Añadir botón de marca al contenedor principal
            contenedor.addView(btnMarca);

            // Contenedor oculto para los vehículos
            LinearLayout detallesContainer = new LinearLayout(this);
            detallesContainer.setOrientation(LinearLayout.VERTICAL);
            detallesContainer.setVisibility(View.GONE);
            grupo.addView(detallesContainer);

            // Añadir todo al layout principal
            buttonContainer.addView(grupo);

            // Evento del botón principal
            btnMarca.setOnClickListener(new View.OnClickListener() {
                boolean visible = false;

                @Override
                public void onClick(View v) {
                    if (!visible) {
                        detallesContainer.removeAllViews();
                        ArrayList<HashMap<String, String>> vehiculos = dbHelper.obtenerVehiculosPorMarca(marca);

                        for (HashMap<String, String> vData : vehiculos) {
                            Button btnVehiculo = new Button(ComprarActivity.this);
                            btnVehiculo.setText("• " + marca + " " + vData.get("modelo") + " - " + vData.get("color"));
                            btnVehiculo.setTextSize(14);
                            btnVehiculo.setPadding(50, 0, 0, 0);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                btnVehiculo.setBackground(getResources().getDrawable(R.drawable.boton_redondeado, null));
                            } else {
                                btnVehiculo.setBackgroundDrawable(getResources().getDrawable(R.drawable.boton_redondeado));
                            }

                            // Añadir margen superior de 5dp a cada botón de vehículo
                            LinearLayout.LayoutParams paramsVehiculo = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            int marginVehiculoDp = 5;
                            int marginVehiculoPx = (int) (marginVehiculoDp * scale + 0.5f);
                            paramsVehiculo.setMargins(0, marginVehiculoPx, 0, 0);
                            btnVehiculo.setLayoutParams(paramsVehiculo);

                            detallesContainer.addView(btnVehiculo);

                            btnVehiculo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ComprarActivity.this, DetallesActivity.class);
                                    intent.putExtra("vehiculo_id", vData.get("id"));
                                    startActivity(intent);
                                }
                            });
                        }

                        detallesContainer.setVisibility(View.VISIBLE);
                        visible = true;
                    } else {
                        detallesContainer.setVisibility(View.GONE);
                        visible = false;
                    }
                }
            });
        }
    }
}