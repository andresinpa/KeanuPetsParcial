package com.jesusinfante.keanupetsparcial.datos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.jesusinfante.keanupetsparcial.datos.MascotaContract.MascotaEntry;
public class MascotaDBHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = MascotaDBHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "mascotas.db";

    private static final int DATABASE_VERSION = 1;

    public MascotaDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Cree una cadena que contenga la declaración SQL para crear la tabla de mascotas
        String SQL_CREATE_MASCOTAS_TABLE =  "CREATE TABLE " + MascotaEntry.NOMBRE_TABLA + " ("
                + MascotaEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MascotaEntry.COLUMNA_NOMBRE_MASCOTA + " TEXT NOT NULL, "
                + MascotaEntry.COLUMNA_RAZA_MASCOTA + " TEXT, "
                + MascotaEntry.COLUMNA_SEXO_MASCOTA + " INTEGER NOT NULL, "
                + MascotaEntry.COLUMNA_TIPO_MASCOTA + " INTEGER NOT NULL, "
                + MascotaEntry.COLUMNA_PESO_MASCOTA + " INTEGER NOT NULL DEFAULT 0);";

        // Ejecutar el SQL statement
        db.execSQL(SQL_CREATE_MASCOTAS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }

}
