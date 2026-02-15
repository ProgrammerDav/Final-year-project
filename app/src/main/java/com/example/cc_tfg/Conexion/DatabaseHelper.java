package com.example.cc_tfg.Conexion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "CCdatabase.db";
    private String DB_PATH;
    private final Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.mContext = context;
        this.DB_PATH = context.getDatabasePath(DB_NAME).getPath();
    }

    public void createDatabase() throws IOException {
        File dbFile = new File(DB_PATH);
        if (!dbFile.exists()) {
            // Solo crea el directorio, no una base vacía
            File parentDir = new File(dbFile.getParent());
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            copyDatabase();
        }

    }

    private void copyDatabase() throws IOException {
        InputStream myInput = mContext.getAssets().open(DB_NAME);
        OutputStream myOutput = new FileOutputStream(DB_PATH);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Ya existe
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Actualiza la base de datos
    }

    public SQLiteDatabase openDatabase() {
        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }

    // Clase interna para validar usuario
    public static class ValidarUsuario extends SQLiteOpenHelper {

        public ValidarUsuario(@Nullable Context context) {
            super(context, DB_NAME, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        public boolean validarUsuario(String USUARIO, String PASSWORD) {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT USUARIO, PASSWORD FROM Usuarios WHERE USUARIO=? AND PASSWORD=?";
            Cursor cursor = db.rawQuery(query, new String[]{USUARIO, PASSWORD});
            boolean existe = cursor.getCount() > 0;
            cursor.close();
            db.close();
            return existe;
        }
    }
    //Clase interna para registrar datos
    public static class RegistrarDatos extends SQLiteOpenHelper{

        public RegistrarDatos(@Nullable Context context) {super(context, DB_NAME, null, 1);}

        @Override
        public void onCreate(SQLiteDatabase db) { }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

        public boolean registrarUsuario(String USUARIO, String PASSWORD, String NAME, String LASTNAME, int MOVIL) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues valores = new ContentValues();
            valores.put("USUARIO", USUARIO);
            valores.put("PASSWORD", PASSWORD);
            valores.put("NAME", NAME);
            valores.put("LASTNAME", LASTNAME);
            valores.put("MOVIL", MOVIL);

            long resultado = db.insert("Usuarios", null, valores);
            db.close();

            // Si resultado = -1 significa que la inserción falló
            return resultado != -1;
        }

        public long registrarVehiculo(int idUsuario, String marca, String modelo, String color, int nPuertas,
                                      int caballos, int kilometraje, String antiguedad, int precio) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues valores = new ContentValues();

            valores.put("ID_USUARIO", idUsuario);
            valores.put("MARCA", marca);
            valores.put("MODELO", modelo);
            valores.put("COLOR", color);
            valores.put("N_PUERTAS", nPuertas);
            valores.put("CABALLOS", caballos);
            valores.put("KILOMETRAJE", kilometraje);
            valores.put("ANTIGUEDAD", antiguedad);
            valores.put("PRECIO", precio);

            long idVehiculo = db.insert("Vehiculos", null, valores);
            db.close();
            return idVehiculo;
        }

        public boolean registrarModificaciones(Integer idVehiculo, String suspension, String motor, String tEscape,
                                               String llantas, String luces, String antivuelco, String remolque) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues valores = new ContentValues();

            // ID_VEHICULO es obligatorio
            valores.put("ID_VEHICULO", idVehiculo);

            // Insertar solo si no están vacíos, de lo contrario dejar null
            valores.put("SUSPENSION", (suspension != null && !suspension.isEmpty()) ? suspension : null);
            valores.put("MOTOR", (motor != null && !motor.isEmpty()) ? motor : null);
            valores.put("T_ESCAPE", (tEscape != null && !tEscape.isEmpty()) ? tEscape : null);
            valores.put("LLANTAS", (llantas != null && !llantas.isEmpty()) ? llantas : null);
            valores.put("LUCES", (luces != null && !luces.isEmpty()) ? luces : null);
            valores.put("ANTIVUELCO", (antivuelco != null && !antivuelco.isEmpty()) ? antivuelco : null);
            valores.put("REMOLQUE", (remolque != null && !remolque.isEmpty()) ? remolque : null);

            long resultado = db.insert("Modificaciones", null, valores);
            db.close();

            // Si resultado = -1, la inserción falló
            return resultado != -1;
        }
    }
    //ArrayList para mostrar marcas
    public ArrayList<String> obtenerMarcasUnicas() {
        ArrayList<String> marcas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT DISTINCT MARCA FROM Vehiculos", null);
        if (cursor.moveToFirst()) {
            do {
                marcas.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return marcas;
    }

    //ArrayList para mostrar vehículos por marca
    public ArrayList<HashMap<String, String>> obtenerVehiculosPorMarca(String marca) {
        ArrayList<HashMap<String, String>> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ID_VEHICULO, MODELO, COLOR FROM Vehiculos WHERE MARCA = ?", new String[]{marca});
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> vehiculo = new HashMap<>();
                vehiculo.put("id", cursor.getString(0));
                vehiculo.put("modelo", cursor.getString(1));
                vehiculo.put("color", cursor.getString(2));
                lista.add(vehiculo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }
    //Mostrar info vehiculo
    public HashMap<String, String> MostrarInfoVehiculo(String idVehiculo) {
        HashMap<String, String> datos = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT v.MARCA, v.MODELO, v.COLOR, v.N_PUERTAS, v.CABALLOS, v.KILOMETRAJE, " +
                "v.ANTIGUEDAD, v.PRECIO, " +
                "u.NAME, u.LASTNAME, u.MOVIL, " +
                "m.SUSPENSION, m.MOTOR, m.T_ESCAPE, m.LLANTAS, " +
                "m.LUCES, m.ANTIVUELCO, m.REMOLQUE " +
                "FROM Vehiculos v " +
                "LEFT JOIN Modificaciones m ON v.ID_VEHICULO = m.ID_VEHICULO " +
                "INNER JOIN Usuarios u ON v.ID_USUARIO = u.ID_USUARIO " +
                "WHERE v.ID_VEHICULO = ?";

        Cursor cursor = db.rawQuery(query, new String[]{idVehiculo});

        if (cursor.moveToFirst()) {
            // Datos del vehículo
            datos.put("MARCA", cursor.getString(0));
            datos.put("MODELO", cursor.getString(1));
            datos.put("COLOR", cursor.getString(2));
            datos.put("N_PUERTAS", cursor.getString(3));
            datos.put("CABALLOS", cursor.getString(4));
            datos.put("KILOMETRAJE", cursor.getString(5));
            datos.put("ANTIGUEDAD", cursor.getString(6));
            datos.put("PRECIO", cursor.getString(7));

            // Datos del usuario asociado
            datos.put("NAME", cursor.getString(8));
            datos.put("LASTNAME", cursor.getString(9));
            datos.put("MOVIL", cursor.getString(10));

            // Datos de modificaciones (solo si no son nulos)
            String[] modKeys = {"SUSPENSION", "MOTOR", "T_ESCAPE", "LLANTAS", "LUCES", "ANTIVUELCO", "REMOLQUE"};
            for (int i = 0; i < modKeys.length; i++) {
                String valor = cursor.getString(11 + i);
                if (valor != null && !valor.trim().isEmpty()) {
                    datos.put(modKeys[i], valor);
                }
            }
        }

        cursor.close();
        db.close();
        return datos;
    }
    //Obtener datos del usuario
    public HashMap<String, String> obtenerDatosUsuario(String usuario) {
        HashMap<String, String> datos = new HashMap<>();
        SQLiteDatabase db = openDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT NAME, LASTNAME, USUARIO, MOVIL FROM Usuarios WHERE USUARIO = ?",
                new String[]{usuario}
        );

        if (cursor.moveToFirst()) {
            datos.put("NAME", cursor.getString(0));
            datos.put("LASTNAME", cursor.getString(1));
            datos.put("USUARIO", cursor.getString(2));
            datos.put("MOVIL", cursor.getString(3));
        }

        cursor.close();
        db.close();
        return datos;
    }
    //Obtener vehículos del usuario
    public ArrayList<HashMap<String, String>> obtenerVehiculosDeUsuario(String usuario) {

        ArrayList<HashMap<String, String>> lista = new ArrayList<>();
        SQLiteDatabase db = openDatabase();

        // Obtener ID de usuario
        Cursor cursorId = db.rawQuery("SELECT ID_USUARIO FROM Usuarios WHERE USUARIO = ?",
                new String[]{usuario});

        if (!cursorId.moveToFirst()) {
            cursorId.close();
            db.close();
            return lista;
        }

        int idUsuario = cursorId.getInt(0);
        cursorId.close();

        Cursor cursorVehiculos = db.rawQuery(
                "SELECT ID_VEHICULO, MARCA, MODELO, COLOR FROM Vehiculos WHERE ID_USUARIO = ?",
                new String[]{String.valueOf(idUsuario)}
        );

        while (cursorVehiculos.moveToNext()) {
            HashMap<String, String> vehiculo = new HashMap<>();
            vehiculo.put("ID_VEHICULO", String.valueOf(cursorVehiculos.getInt(0)));
            vehiculo.put("MARCA", cursorVehiculos.getString(1));
            vehiculo.put("MODELO", cursorVehiculos.getString(2));
            vehiculo.put("COLOR", cursorVehiculos.getString(3));
            lista.add(vehiculo);
        }

        cursorVehiculos.close();
        db.close();
        return lista;
    }
    //Eliminar vehiculos
    public boolean eliminarVehiculo(String idVehiculo) {
        SQLiteDatabase db = openDatabase();
        int filas = db.delete("Vehiculos", "ID_VEHICULO = ?", new String[]{idVehiculo});
        db.close();
        return filas > 0;
    }
}