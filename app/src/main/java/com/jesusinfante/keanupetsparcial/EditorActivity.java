package com.jesusinfante.keanupetsparcial;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.jesusinfante.keanupetsparcial.datos.MascotaContract.MascotaEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MASCOTA_LOADER = 1;

    /*Views*/
    private EditText nombreEditText;
    private EditText razaEditText;
    private EditText pesoEditText;
    private Spinner tipoSpinner;
    private Spinner sexoSpinner;

    private int mTipo = MascotaEntry.TIPO_DESCONOCIDO;
    private int mSexo = MascotaEntry.SEXO_DESCONOCIDO;

    private Uri actualMascotaUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        actualMascotaUri = getIntent().getData();
        if (actualMascotaUri != null) {
            setTitle(getString(R.string.editor_activity_title_edit_pet));
            // Iniciar el Loader
            getSupportLoaderManager().initLoader(MASCOTA_LOADER, null, this);
        } else {
            setTitle(getString(R.string.editor_activity_title_new_pet));
            invalidateOptionsMenu();
        }

        nombreEditText = findViewById(R.id.edit_mascota_nombre);
        tipoSpinner = findViewById(R.id.spinner_tipo);
        razaEditText = findViewById(R.id.edit_mascota_raza);
        pesoEditText = findViewById(R.id.edit_mascota_peso);
        sexoSpinner = findViewById(R.id.spinner_sexo);

        setupSpinner();
        setupSpinnerDos();
    }


    /* Drop-down para sexo */
    private void setupSpinner() {
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        sexoSpinner.setAdapter(genderSpinnerAdapter);

        sexoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(seleccion)) {
                    if (seleccion.equals(getString(R.string.gender_male))) {
                        mSexo = MascotaEntry.SEXO_MACHO;
                    } else if (seleccion.equals(getString(R.string.gender_female))) {
                        mSexo = MascotaEntry.SEXO_HEMBRA;
                    } else {
                        mSexo = MascotaEntry.SEXO_DESCONOCIDO;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSexo = MascotaEntry.SEXO_DESCONOCIDO;
            }
        });
    }

    /* Drop-down para tipo */
    private void setupSpinnerDos() {
        ArrayAdapter typeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_type_options, android.R.layout.simple_spinner_item);

        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        tipoSpinner.setAdapter(typeSpinnerAdapter);

        tipoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccion = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(seleccion)) {
                    if (seleccion.equals(getString(R.string.type_perro))) {
                        mTipo = MascotaEntry.TIPO_PERRO;
                    } else if (seleccion.equals(getString(R.string.type_gato))) {
                        mTipo = MascotaEntry.TIPO_GATO;
                    } else if (seleccion.equals(getString(R.string.type_ave))){
                        mTipo = MascotaEntry.TIPO_AVE;
                    } else if (seleccion.equals(getString(R.string.type_roedor))){
                        mTipo = MascotaEntry.TIPO_ROEDOR;
                    }else{
                        mTipo = MascotaEntry.TIPO_DESCONOCIDO;
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mTipo = MascotaEntry.TIPO_DESCONOCIDO;
            }
        });
    }


    private void setTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            ((ActionBar) actionBar).setTitle(title);
        }
    }

    private boolean savePet() {
        // Leer desde campos de entrada
        String nombreString = nombreEditText.getText().toString().trim();
        String razaString = razaEditText.getText().toString().trim();
        String pesoString = pesoEditText.getText().toString().trim();
        int peso = 0;
        if (!TextUtils.isEmpty(pesoString)) {
            peso = Integer.parseInt(pesoString);
        }

        if (actualMascotaUri == null && TextUtils.isEmpty(nombreString) && TextUtils.isEmpty(pesoString) && mSexo == MascotaEntry.SEXO_DESCONOCIDO && mTipo == MascotaEntry.TIPO_DESCONOCIDO) {
            return false;
        }

        // Cree un objeto ContentValues donde los nombres de las columnas sean las claves.
        // y los atributos de la mascota son los valores.
        ContentValues values = new ContentValues();
        values.put(MascotaEntry.COLUMNA_NOMBRE_MASCOTA, nombreString);
        values.put(MascotaEntry.COLUMNA_TIPO_MASCOTA, mTipo);
        values.put(MascotaEntry.COLUMNA_RAZA_MASCOTA, razaString);
        values.put(MascotaEntry.COLUMNA_SEXO_MASCOTA, mSexo);
        values.put(MascotaEntry.COLUMNA_PESO_MASCOTA, peso);

        if (actualMascotaUri != null) {
            int filaActualizada = getContentResolver().update(actualMascotaUri, values, null, null);

            if (filaActualizada != 0) {
                Toast.makeText(this, "Mascota actualizada.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // insertando nueva mascota
            Uri newUri = getContentResolver().insert(MascotaEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, "Error con el guardado de la mascota", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Mascota guardada", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    private void deletePet() {
        int filasEliminadas = getContentResolver().delete(actualMascotaUri, null, null);

        if (filasEliminadas != 0) {
            Toast.makeText(this, "Mascota Eliminada", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "¡Error elimando la mascota!", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarConfirmacionEliminarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //  Eliminar la mascota
                deletePet();
                dialog.dismiss();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_save) {
            if (savePet()) {
                finish();
            } else {
                Toast.makeText(this, "¡No se pudo guardar, completa los detalles!", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (itemId == R.id.action_delete) {
            mostrarConfirmacionEliminarDialog();
            return true;
        } else if (itemId == android.R.id.home) {
            // Volver a la activity parent (CatalogActivity)
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // Si se trata de una mascota nueva, oculte el elemento del menú "Eliminar".
        if (actualMascotaUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] proyeccion = {
                MascotaEntry._ID,
                MascotaEntry.COLUMNA_NOMBRE_MASCOTA,
                MascotaEntry.COLUMNA_TIPO_MASCOTA,
                MascotaEntry.COLUMNA_RAZA_MASCOTA,
                MascotaEntry.COLUMNA_SEXO_MASCOTA,
                MascotaEntry.COLUMNA_PESO_MASCOTA
        };

        return new CursorLoader(this,
                actualMascotaUri,
                proyeccion,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            // Encuentre las columnas de atributos de mascotas que nos interesan
            int nombreColumnIndex = cursor.getColumnIndex(MascotaEntry.COLUMNA_NOMBRE_MASCOTA);
            int tipoColumnIndex = cursor.getColumnIndex(MascotaEntry.COLUMNA_TIPO_MASCOTA);
            int razaColumnIndex = cursor.getColumnIndex(MascotaEntry.COLUMNA_RAZA_MASCOTA);
            int sexoColumnIndex = cursor.getColumnIndex(MascotaEntry.COLUMNA_SEXO_MASCOTA);
            int pesoColumnIndex = cursor.getColumnIndex(MascotaEntry.COLUMNA_PESO_MASCOTA);

            // Extraiga el valor del cursor para el índice de columna dado
            String nombre = cursor.getString(nombreColumnIndex);
            int tipo = cursor.getInt(tipoColumnIndex);
            String raza = cursor.getString(razaColumnIndex);
            int sexo = cursor.getInt(sexoColumnIndex);
            int peso = cursor.getInt(pesoColumnIndex);

            // Actualiza las vistas en pantalla con los valores de la base de datos.
            nombreEditText.setText(nombre);
            razaEditText.setText(raza);
            pesoEditText.setText(String.valueOf(peso));

            switch (tipo) {
                case MascotaEntry.TIPO_PERRO:
                    tipoSpinner.setSelection(1);
                    break;
                case MascotaEntry.TIPO_GATO:
                    tipoSpinner.setSelection(2);
                    break;
                case MascotaEntry.TIPO_AVE:
                    tipoSpinner.setSelection(3);
                    break;
                case MascotaEntry.TIPO_ROEDOR:
                    tipoSpinner.setSelection(4);
                    break;
                default:
                    tipoSpinner.setSelection(0);
                    break;
            }

            switch (sexo) {
                case MascotaEntry.SEXO_MACHO:
                    sexoSpinner.setSelection(1);
                    break;
                case MascotaEntry.SEXO_HEMBRA:
                    sexoSpinner.setSelection(2);
                    break;
                default:
                    sexoSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        nombreEditText.setText(null);
        tipoSpinner.setSelection(0);
        razaEditText.setText(null);
        pesoEditText.setText(null);
        sexoSpinner.setSelection(0);
    }
}

