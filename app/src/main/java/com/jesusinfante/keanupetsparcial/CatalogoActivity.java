package com.jesusinfante.keanupetsparcial;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jesusinfante.keanupetsparcial.datos.MascotaContract;

public class CatalogoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int PET_LOADER = 0;

    private MascotaCursorAdapter mascotaCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            /*Snackbar.make(ver, "Reemplazar con tu propia acción", Snackbar.LENGTH_LONG)
                     .setAction("Acción", nulo).show();*/
            Intent intent = new Intent(CatalogoActivity.this, EditorActivity.class);
            startActivity(intent);

        });

        ListView listView = findViewById(R.id.list_view_mascota);

        View emptyView = findViewById(R.id.vista_vacia);
        listView.setEmptyView(emptyView);

        mascotaCursorAdapter = new MascotaCursorAdapter(this, null);
        listView.setAdapter(mascotaCursorAdapter);

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            Intent intent = new Intent(CatalogoActivity.this, EditorActivity.class);

            Uri currentPetUri = ContentUris.withAppendedId(MascotaContract.MascotaEntry.CONTENT_URI, id);
            intent.setData(currentPetUri);

            startActivity(intent);
        });

        getSupportLoaderManager().initLoader(PET_LOADER, null, this);

    }


    private void insertPet() {
        // Crea un objeto ContentValues donde los nombres de las columnas son las claves,
        // y los atributos de la mascota de Bela son los valores.
        ContentValues values = new ContentValues();
        values.put(MascotaContract.MascotaEntry.COLUMNA_NOMBRE_MASCOTA, "Bela");
        values.put(MascotaContract.MascotaEntry.COLUMNA_TIPO_MASCOTA, MascotaContract.MascotaEntry.TIPO_PERRO);
        values.put(MascotaContract.MascotaEntry.COLUMNA_RAZA_MASCOTA, "Husky");
        values.put(MascotaContract.MascotaEntry.COLUMNA_SEXO_MASCOTA, MascotaContract.MascotaEntry.SEXO_HEMBRA);
        values.put(MascotaContract.MascotaEntry.COLUMNA_PESO_MASCOTA, 28);

        Uri newUri = getContentResolver().insert(MascotaContract.MascotaEntry.CONTENT_URI, values);
    }

    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(MascotaContract.MascotaEntry.CONTENT_URI, null, null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu; esto agrega elementos a la barra de acciones si está presente.
        getMenuInflater().inflate(R.menu.menu_catalogo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_insert_dummy_data) {
            insertPet();
            return true;
        } else if (id == R.id.action_delete_all_entries) {
            deleteAllPets();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                MascotaContract.MascotaEntry._ID,
                MascotaContract.MascotaEntry.COLUMNA_NOMBRE_MASCOTA,
                MascotaContract.MascotaEntry.COLUMNA_TIPO_MASCOTA,
                MascotaContract.MascotaEntry.COLUMNA_RAZA_MASCOTA,
        };

        return new CursorLoader(this,
                MascotaContract.MascotaEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mascotaCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mascotaCursorAdapter.swapCursor(null);
    }
}