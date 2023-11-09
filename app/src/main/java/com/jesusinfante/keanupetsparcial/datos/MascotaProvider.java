package com.jesusinfante.keanupetsparcial.datos;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class MascotaProvider extends ContentProvider {
    private final String TAG = MascotaProvider.class.getSimpleName();
    private MascotaDBHelper mDbHelper;

    private static final int MASCOTAS = 100;
    private static final int MASCOTA_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(MascotaContract.CONTENT_AUTHORITY, MascotaContract.PATH_MASCOTAS, MASCOTAS);
        sUriMatcher.addURI(MascotaContract.CONTENT_AUTHORITY, MascotaContract.PATH_MASCOTAS + "/#", MASCOTA_ID);
    }


    @Override
    public boolean onCreate() {
        mDbHelper = new MascotaDBHelper(getContext());
        return false;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projeccion, @Nullable String seleccion, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case MASCOTAS:
                cursor = database.query(
                        MascotaContract.MascotaEntry.NOMBRE_TABLA,
                        projeccion,
                        seleccion,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case MASCOTA_ID:
                seleccion = MascotaContract.MascotaEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(MascotaContract.MascotaEntry.NOMBRE_TABLA, projeccion, seleccion, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("No se puede consultar URI desconocido " + uri);
        }


        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);

        return cursor;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MASCOTAS:
                return MascotaContract.MascotaEntry.CONTENT_LIST_TYPE;
            case MASCOTA_ID:
                return MascotaContract.MascotaEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("URI desconocida " + uri + " con match " + match);
        }
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MASCOTAS:
                assert contentValues != null;
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("La inserción no es compatible con " + uri);
        }
    }


    @Override
    public int delete(@NonNull Uri uri, @Nullable String seleccion, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int filasEliminadas;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MASCOTAS:
                filasEliminadas = database.delete(MascotaContract.MascotaEntry.NOMBRE_TABLA, seleccion, selectionArgs);
                break;
            case MASCOTA_ID:
                seleccion = MascotaContract.MascotaEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                filasEliminadas = database.delete(MascotaContract.MascotaEntry.NOMBRE_TABLA, seleccion, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("La eliminación no es compatible con " + uri);
        }

        if (filasEliminadas != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }

        return filasEliminadas;
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String seleccion, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MASCOTAS:
                assert contentValues != null;
                return updatePet(uri, contentValues, seleccion, selectionArgs);
            case MASCOTA_ID:
                // Para el código PET_ID, extrae el ID del URI,
                // así sabemos qué fila actualizar. La selección será "_id=?" y selección
                // los argumentos serán una matriz de cadenas que contiene el ID real.
                seleccion = MascotaContract.MascotaEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                assert contentValues != null;
                return updatePet(uri, contentValues, seleccion, selectionArgs);
            default:
                throw new IllegalArgumentException("La actualizacion no esta optimizada para " + uri);
        }
    }


    private Uri insertPet(Uri uri, ContentValues values) {
        String nombre = values.getAsString(MascotaContract.MascotaEntry.COLUMNA_NOMBRE_MASCOTA);
        if (nombre == null) {
            throw new IllegalArgumentException("la mascota requiere un nombre");
        }

        // Comprueba que el tipo es válido.
        Integer tipo = values.getAsInteger(MascotaContract.MascotaEntry.COLUMNA_TIPO_MASCOTA);
        if (tipo == null || !MascotaContract.MascotaEntry.isValidTipo(tipo)) {
            throw new IllegalArgumentException("la mascota requiere un tipo válido");
        }

        // Comprueba que el género es válido.
        Integer sexo = values.getAsInteger(MascotaContract.MascotaEntry.COLUMNA_SEXO_MASCOTA);
        if (sexo == null || !MascotaContract.MascotaEntry.isValidSexo(sexo)) {
            throw new IllegalArgumentException("la mascota requiere un sexo válido");
        }

        // Si se proporciona el peso, comprobar que sea mayor o igual a 0 kg.
        Integer peso = values.getAsInteger(MascotaContract.MascotaEntry.COLUMNA_PESO_MASCOTA);
        if (peso != null && peso < 0) {
            throw new IllegalArgumentException("la mascota requiere un peso valido");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long nuevaFilaId = database.insert(MascotaContract.MascotaEntry.NOMBRE_TABLA, null, values);

        if (nuevaFilaId == -1) {
            Log.e(TAG, "Fallo al insertar fila para " + uri);
            return null;
        }

        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, nuevaFilaId);
    }


    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(MascotaContract.MascotaEntry.COLUMNA_NOMBRE_MASCOTA)) {
            String nombre = values.getAsString(MascotaContract.MascotaEntry.COLUMNA_NOMBRE_MASCOTA);
            if (nombre == null) {
                throw new IllegalArgumentException("La mascota requiere un nombre");
            }
        }

        if (values.containsKey(MascotaContract.MascotaEntry.COLUMNA_TIPO_MASCOTA)) {
            Integer tipo = values.getAsInteger(MascotaContract.MascotaEntry.COLUMNA_TIPO_MASCOTA);
            if (tipo == null || !MascotaContract.MascotaEntry.isValidTipo(tipo)) {
                throw new IllegalArgumentException("La mascota requiere un valor valido para tipo");
            }
        }

        if (values.containsKey(MascotaContract.MascotaEntry.COLUMNA_SEXO_MASCOTA)) {
            Integer sexo = values.getAsInteger(MascotaContract.MascotaEntry.COLUMNA_SEXO_MASCOTA);
            if (sexo == null || !MascotaContract.MascotaEntry.isValidSexo(sexo)) {
                throw new IllegalArgumentException("La mascota requiere un valor valido para sexo");
            }
        }

        if (values.containsKey(MascotaContract.MascotaEntry.COLUMNA_PESO_MASCOTA)) {
            // Comprobar que el peso sea mayor o igual a 0 kg.
            Integer peso = values.getAsInteger(MascotaContract.MascotaEntry.COLUMNA_PESO_MASCOTA);
            if (peso != null && peso < 0) {
                throw new IllegalArgumentException("La mascota requiere un peso válido.");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int actualizarFilas = database.update(MascotaContract.MascotaEntry.NOMBRE_TABLA, values, selection, selectionArgs);

        if (actualizarFilas != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }

        return actualizarFilas;
    }

}
