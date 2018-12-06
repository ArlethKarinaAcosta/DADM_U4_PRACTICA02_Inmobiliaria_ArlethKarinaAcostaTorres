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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    EditText idMueble, domicilioMueble, precioVentaMueble, precioRentaMueble, fechaMueble;
    Button insertarM, consultarM, eliminarM, actualizarM;
    BaseDatos base;
    Spinner propietarios;
    String[] nombres;
    String[] ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        idMueble = findViewById(R.id.idMuebleEdit);
        domicilioMueble = findViewById(R.id.domicilioMuebleEdit);
        precioVentaMueble = findViewById(R.id.precioventaMuebleEdit);
        precioRentaMueble = findViewById(R.id.preciorentaMuebleEdit);
        fechaMueble = findViewById(R.id.fechaMuebleEdit);

        insertarM = findViewById(R.id.insertarBotonMueble);
        consultarM = findViewById(R.id.consultarBotonMueble);
        eliminarM = findViewById(R.id.eliminarBotonMueble);
        actualizarM = findViewById(R.id.actualizarBotonMueble);

        propietarios = findViewById(R.id.llaveforanea);

        try{
            base = new BaseDatos(this, "inmobiliaria", null, 3);


            SQLiteDatabase bd = base.getReadableDatabase();

            String SQL = "SELECT IDP, NOMBRE FROM PROPIETARIO ORDER BY IDP";

            Cursor fila = bd.rawQuery(SQL, null);

            if (fila.moveToFirst()==false){
                Toast.makeText(this, "No hay propietarios agregados!! por lo cual no podra hacer compras de ningun inmueble", Toast.LENGTH_LONG).show();
            }

            if(fila.getCount()>0){
                ids = new String[fila.getCount()];
                nombres = new String[fila.getCount()];
                for(int i=0; i<fila.getCount(); i++){
                    ids[i]=fila.getString(0);
                    nombres[i] = fila.getString(1);
                    fila.moveToNext();
                }
            }

            ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nombres);
            propietarios.setAdapter(adaptador);

        }catch (Exception e){

        }

        insertarM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    guardarDatos();
            }
        });

        consultarM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(1);//Contendra el AlertDialog
            }
        });

        eliminarM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(2);
            }
        });

        actualizarM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actualizarM.getText().toString().startsWith("CONFIRMAR ACTUALIZACION")){
                    invocarConfirmacionActualizacion();
                }else{
                    pedirID(3);
                }
            }
        });
    }

    private void guardarDatos() {
        try{
            SQLiteDatabase tabla = base.getWritableDatabase();

            String SQL = "INSERT INTO INMUEBLE VALUES(%1, '%2', %3, %4, '%5', %6)";
            SQL = SQL.replace("%1", idMueble.getText().toString());
            SQL = SQL.replace("%2", domicilioMueble.getText().toString());
            SQL = SQL.replace("%3", precioVentaMueble.getText().toString());
            SQL = SQL.replace("%4", precioRentaMueble.getText().toString());
            SQL = SQL.replace("%5", fechaMueble.getText().toString());
            SQL = SQL.replace("%6", ids[propietarios.getSelectedItemPosition()]);

            tabla.execSQL(SQL);

            Toast.makeText(this, "Se guardo exitosamente el registro", Toast.LENGTH_LONG).show();

            idMueble.setText("");
            domicilioMueble.setText("");
            precioVentaMueble.setText("");
            precioRentaMueble.setText("");
            fechaMueble.setText("");

            tabla.close();

        }catch (Exception e){
            Toast.makeText(this, "ERROR: No se pudo guardar su compra favor de agregar un propietario", Toast.LENGTH_LONG).show();
        }
    }

    private void pedirID(final int i) {

        final EditText pidoID = new EditText(this);
        pidoID.setInputType(InputType.TYPE_CLASS_NUMBER);
        pidoID.setHint("VALOR ENTERO MAYOR DE 0");

        String mensaje = "";
        String mensajeButton = "";

        switch (i)
        {
            case 1:
                mensaje ="¿QUÉ ID DESEAS BUSCAR?";
                mensajeButton = "BUSCAR";
                break;
            case 2:
                mensaje = "¿QUÉ ID QUIERES ELIMINAR?";
                mensajeButton ="ELIMINAR";
                break;
            case 3:
                mensaje="¿QUÉ ID DESEAS MODIFICAR?";
                mensajeButton = "MODIFICAR";
                break;
        }

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("ATENCION").setMessage(mensaje).setView(pidoID).setPositiveButton(mensajeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (pidoID.getText().toString().isEmpty()){
                    Toast.makeText(Main2Activity.this, "DEBES ESCRIBIR VALOR", Toast.LENGTH_LONG).show();
                    return;
                }
                buscarDato(pidoID.getText().toString(), i);
                dialog.dismiss();
            }
        }).setNegativeButton("CANCELAR", null).show();

    }

    private void buscarDato(String idABuscar, int origen){
        try{
            SQLiteDatabase tabla = base.getReadableDatabase();

            String SQL = "SELECT * FROM INMUEBLE WHERE IDINMUEBLE="+idABuscar;

            Cursor resultado = tabla.rawQuery(SQL, null);

            if (resultado.moveToFirst()){
                if (origen==2){
                    String datos = idABuscar+"&"+resultado.getString(1)+"&"+resultado.getString(2)+"&"+resultado.getString(3)+"&"+resultado.getString(4)+"&"+resultado.getString(5);
                    invocarConfirmacionEliminacion(datos);
                    return;
                }

                idMueble.setText(resultado.getString(0));
                domicilioMueble.setText(resultado.getString(1));
                precioVentaMueble.setText(resultado.getString(2));
                precioRentaMueble.setText(resultado.getString(3));
                fechaMueble.setText(resultado.getString(4));
                propietarios.setSelection(Integer.parseInt(resultado.getString(5))-1);



                if (origen==3){
                    insertarM.setEnabled(false);
                    consultarM.setEnabled(false);
                    eliminarM.setEnabled(false);
                    actualizarM.setText("CONFIRMAR ACTUALIZACION");
                    idMueble.setEnabled(false);
                    propietarios.setEnabled(false);
                }
            }else{
                Toast.makeText(this, "ERROR: No se pudo buscar tu compra", Toast.LENGTH_LONG).show();
            }
            tabla.close();

        }catch (SQLiteException e){
            Toast.makeText(this, "ERROR: NO SE ENCONTRO RESULTADO", Toast.LENGTH_LONG).show();
        }

    }

    private void invocarConfirmacionEliminacion(String datos) {

        String cadenaDatos[] = datos.split("&");
        final String id = cadenaDatos[0];
        String domicilio = cadenaDatos[1];
        String precioVenta = cadenaDatos[2];
        String precioRenta = cadenaDatos[3];
        String fechaTransaccion = cadenaDatos[4];
        String propietario = cadenaDatos[5];

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("ATENCION").setMessage("Deseas eliminar \nId del propietario: "+propietario+" \nId de Compra: "+id+"\nDomicilio: "+domicilio+" \nPrecio Venta: "+precioVenta+" \nPrecio Renta: "+precioRenta+"\nFecha Transaccion: "+fechaTransaccion+" ?").setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarDato(id);
                dialog.dismiss();
            }
        }).setNegativeButton("NO", null).show();
    }

    private void eliminarDato(String id) {
        try{
            SQLiteDatabase tabla = base.getWritableDatabase();

            String SQL = "DELETE FROM INMUEBLE WHERE IDINMUEBLE="+id;

            tabla.execSQL(SQL);

            idMueble.setText("");
            domicilioMueble.setText("");
            precioVentaMueble.setText("");
            precioRentaMueble.setText("");
            fechaMueble.setText("");

            Toast.makeText(this, "Eliminado correctamente", Toast.LENGTH_LONG).show();
            tabla.close();

        }catch (SQLiteException e){
            Toast.makeText(this, "ERROR: NO SE PUDO ELIMINAR", Toast.LENGTH_LONG).show();
        }
    }

    private void invocarConfirmacionActualizacion() {
        AlertDialog.Builder confir = new AlertDialog.Builder(this);

        confir.setTitle("IMPORTANTE").setMessage("¿Estas seguro que deseas aplicar los cambios?").setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                aplicarActualizacion();
                propietarios.setEnabled(true);
                dialog.dismiss();
            }
        }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                habilitarBotonesYLimpiarCampos();
                propietarios.setEnabled(true);
                dialog.cancel();
            }
        }).show();
    }

    private void aplicarActualizacion(){
        try{
            SQLiteDatabase tabla = base.getWritableDatabase();
            String SQL = "UPDATE INMUEBLE SET DOMICILIO='"+domicilioMueble.getText().toString()+"', PRECIOVENTA="+precioVentaMueble.getText().toString()+", PRECIORENTA="
                    +precioRentaMueble.getText().toString()+", FECHATRANSACCION='"+fechaMueble.getText().toString()+"' WHERE IDINMUEBLE=" +idMueble.getText().toString();
            tabla.execSQL(SQL);
            tabla.close();
            Toast.makeText(this, "Se actualizaron correctamente los datos", Toast.LENGTH_LONG).show();
        }catch (SQLiteException e){
            Toast.makeText(this, "ERROR: NO SE PUDO ACTUALIZAR", Toast.LENGTH_LONG).show();
        }

        habilitarBotonesYLimpiarCampos();
    }

    private void habilitarBotonesYLimpiarCampos(){
        idMueble.setText("");
        domicilioMueble.setText("");
        precioVentaMueble.setText("");
        precioRentaMueble.setText("");
        fechaMueble.setText("");
        insertarM.setEnabled(true);
        consultarM.setEnabled(true);
        eliminarM.setEnabled(true);
        actualizarM.setText("ACTUALIZAR");
        idMueble.setEnabled(true);
    }
}