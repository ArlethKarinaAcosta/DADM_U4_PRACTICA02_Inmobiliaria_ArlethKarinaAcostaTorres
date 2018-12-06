package com.example.arlet.dadm_u4_practica02_inmobiliaria_arlethkarinaacostatorres;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText idPropietario, nombrePropietario, domicilioPropietario, telefonoPropietario;
    Button eliminar, insertar, actualizar, consultar, registrarM;
    BaseDatos base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idPropietario = findViewById(R.id.idEdit);
        nombrePropietario = findViewById(R.id.nombreEdit);
        domicilioPropietario = findViewById(R.id.domicilioEdit);
        telefonoPropietario = findViewById(R.id.telefonoEdit);
        eliminar = findViewById(R.id.eliminarBoton);
        actualizar = findViewById(R.id.actualizarBoton);
        insertar = findViewById(R.id.insertarBoton);
        consultar = findViewById(R.id.consultarBoton);
        registrarM = findViewById(R.id.registrar);





        base = new BaseDatos(this, "inmobiliaria", null, 3);
                insertar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        codigoInsertar();
                    }
                });
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(2);
            }
        });
        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(1);
            }
        });
        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actualizar.getText().toString().startsWith("CONFIRMAR"))
                {
                    confirmarActualizacion();
                }
                pedirID(3);
            }
        });


        registrarM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambio();
            }
        });

    }

    private void cambio() {
        Intent cambio = new Intent(MainActivity.this, Main2Activity.class);
        startActivity(cambio);

    }


    private void codigoInsertar() {
        try {
            SQLiteDatabase tabla = base.getWritableDatabase();
            String SQL = "INSERT INTO PROPIETARIO VALUES(" + idPropietario.getText().toString() + ",'" + nombrePropietario.getText().toString() + "','" + domicilioPropietario.getText().toString() + "', '"+ telefonoPropietario.getText().toString()+ "')";
            tabla.execSQL(SQL);
            Toast.makeText(this, "SE INSERTÓ EXITOSAMENTE", Toast.LENGTH_LONG).show();
            tabla.close();
            vaciarCampos();
        }
        catch (SQLiteException e)
        {
            Toast.makeText(this, "NO SE PUDO INSERTAR", Toast.LENGTH_LONG).show();
        }
    }


    private void confirmarActualizacion() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("ACTUALIZAR")
                .setMessage("¿Estás seguro que deseas actualizar la información del propietario?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        actualizar();
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                idPropietario.setText("");
                nombrePropietario.setText("");
                domicilioPropietario.setText("");
                telefonoPropietario.setText("");
                insertar.setEnabled(true);
                eliminar.setEnabled(true);
                consultar.setEnabled(true);
                dialog.cancel();
            }
        }).show();
    }

    private void actualizar() {
        try{
            SQLiteDatabase tabla = base.getWritableDatabase();
            String SQL= "UPDATE PROPIETARIO SET NOMBRE='"+nombrePropietario.getText().toString()+"', DOMICILIO='"+domicilioPropietario.getText().toString()+"', TELEFONO='"+telefonoPropietario.getText().toString()+"' WHERE IDP="+idPropietario.getText().toString();
            tabla.execSQL(SQL);
            tabla.close();
            Toast.makeText(this, "SE ACTUALIZÓ CON ÉXITO", Toast.LENGTH_LONG).show();
        }
        catch (SQLiteException e)
        {
            Toast.makeText(this, "NO SE PUEDE ACTUALIZAR", Toast.LENGTH_LONG).show();
        }
        idPropietario.setText("");
        nombrePropietario.setText("");
        domicilioPropietario.setText("");
        telefonoPropietario.setText("");
        insertar.setEnabled(true);
        insertar.setEnabled(true);
        consultar.setEnabled(true);
        eliminar.setEnabled(true);
        idPropietario.setEnabled(true);
    }

    private void pedirID(final int i) {
        final EditText numeroID = new EditText(this);
        String mensaje ="";
        String mensajeButton="";
        numeroID.setInputType(InputType.TYPE_CLASS_NUMBER);
        switch (i)
        {
            case 1:
                mensaje ="¿QUÉ ID DESEAS BUSCAR?";
                mensajeButton = "BUSCAR";
                break;
            case 2:
                mensaje ="¿QUÉ ID QUIERES ELIMINAR?";
                mensajeButton="ELIMINAR";
                break;
            case 3:
                mensaje="¿QUÉ ID DESEAS MODIFICAR?";
                mensajeButton = "MODIFICAR";
                break;
        }
        numeroID.setHint(mensaje);
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("ATENCIÓN")
                .setView(numeroID)
                .setPositiveButton(mensajeButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(numeroID.getText().toString().isEmpty())
                        {
                            Toast.makeText(MainActivity.this, "DEBES INGRESAR UN NUMERO ENTERO", Toast.LENGTH_LONG).show();
                            return;
                        }
                        buscarDato(numeroID.getText().toString(), i);
                        dialog.dismiss();
                    }
                }).setNegativeButton("CANCELAR", null).show();
    }


    private void vaciarCampos() {
        idPropietario.setText("");
        nombrePropietario.setText("");
        domicilioPropietario.setText("");
        telefonoPropietario.setText("");

    }

    private void buscarDato(String idBuscar, int i)
    {
        try
        {
            SQLiteDatabase tabla = base.getReadableDatabase();
            String SQL = "SELECT * FROM PROPIETARIO WHERE IDP="+idBuscar;
            Cursor resultado = tabla.rawQuery(SQL, null);

            if(resultado.moveToFirst())
            {
                idPropietario.setText(resultado.getString(0));
                nombrePropietario.setText(resultado.getString(1));
                domicilioPropietario.setText(resultado.getString(2));
                telefonoPropietario.setText(resultado.getString(3));

                if(i==2)
                {
                    String dato = idBuscar+"&"+resultado.getString(1)+"&"+resultado.getString(2)+"&"+resultado.getString(3);
                    confirmarEliminacion(dato);
                    return;
                }
                if(i==3)
                {
                    insertar.setEnabled(false);
                    eliminar.setEnabled(false);
                    consultar.setEnabled(false);
                    idPropietario.setEnabled(false);
                    actualizar.setText("CONFIRMAR");
                }
            }
            else
            {
                Toast.makeText(this, "NO SE ENCONTRÓ RESULTADO, PROBABLEMENTE NO EXISTA REGISTRO CON ESE IDENTIFICADOR", Toast.LENGTH_LONG).show();
            }
            tabla.close();
        }
        catch (SQLiteException e)
        {
            Toast.makeText(this, "ERROR: NO SE PUDO", Toast.LENGTH_LONG).show();

        }
    }

    private void confirmarEliminacion(final String dato) {
        String datos[] = dato.split("&");
        final String id = datos[0];
        String nombre = datos[1];
        String domicilio = datos[2];
        String telefono = datos[3];

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("CUIDADO").setMessage("¿Estás seguro que deseaas eliminar al propietario: "+id+" Nombre: "+nombre+" con domicilio: " + domicilio + " y telefono: " + telefono + " ?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminar(id);
                        dialog.dismiss();
                    }
                }).setNegativeButton("No", null).show();

    }

    private void eliminar(String id) {
        try{
            SQLiteDatabase tabla = base.getWritableDatabase();
            String SQL = "DELETE FROM PROPIETARIO WHERE IDP="+id;
            tabla.execSQL(SQL);
            tabla.close();
            Toast.makeText(this, "¡BORRADO!", Toast.LENGTH_LONG).show();
            vaciarCampos();
        }
        catch (SQLiteException e)
        {
            Toast.makeText(this, "NO SE PUDO ELIMINAR", Toast.LENGTH_LONG).show();
        }
    }




}
