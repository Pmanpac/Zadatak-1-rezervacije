package com.pprcan.rezervacije;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Random;

public class RezervacijaActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener , TimePickerDialog.OnTimeSetListener{

    private EditText restoran, broj_osoba, ime;
    private ImageView abort, commit;
    private String date, time = null;

    private int pin;

    private ArrayList<Rezervacija> rezervacije;

    private TextView timepickerPlaceHolder, datepickerPlaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rezervacija);

        loadData();

        if(rezervacije == null){
            rezervacije = new ArrayList<Rezervacija>();
            pin = 1000;
        }

        restoran = findViewById(R.id.restoran_input);
        broj_osoba = findViewById(R.id.broj_input);
        ime = findViewById(R.id.ime_input);


        abort = findViewById(R.id.abort);
        commit = findViewById(R.id.commit);

        abort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMain();
            }
        });

        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (restoran.getText().toString().trim().length() < 1){
                    shortToast("Polje 'Restoran' ne smije biti prazno.");
                    return;
                }else if(broj_osoba.getText().toString().trim().length() < 1 || broj_osoba.getText() == null || Integer.parseInt(broj_osoba.getText().toString()) < 1){
                    shortToast("Polje 'Broj osoba' mora biti broj veći od 0");
                    return;
                }else if(ime.getText().toString().trim().length() < 1){
                    shortToast("Polje 'Na ime' ne smije biti prazno");
                    return;
                }else if(time == null || date == null){
                    shortToast("Polja 'Datum' i 'Vrijeme' moraju biti popunjeni");
                    return;
                }else{




                    if(rezervacije.size() > 0){
                        pin = rezervacije.get(rezervacije.size() - 1).pin + 1;
                        if (pin > 9999){
                            for (int i = 1000; i < 10000; i++){
                                if(isPinUnique(i)){
                                    pin = i;
                                    break;
                                }
                            }

                            shortToast("Trenutno name slobodnih stolova na raspolaganju. Molimo vas pokusajte kasnije");
                        }
                    }else{
                        pin = 1000;
                    }



                    Rezervacija r = new Rezervacija(pin, Integer.parseInt(broj_osoba.getText().toString()), restoran.getText().toString(), date, time, ime.getText().toString());
                    rezervacije.add(r);
                    saveData();
                    longToast("Uspješno Rezerviran termin za " + broj_osoba.getText().toString() + " u restoranu " + restoran.getText().toString() + ". PIN: " + pin);
                    sendDataToMain(r);
                }


            }
        });



        timepickerPlaceHolder = findViewById(R.id.timepicker);
        datepickerPlaceHolder = findViewById(R.id.datepicker);


        // Preuzeto s primjera youtube kanala  'Coding in Flow' predloženog na stranici kolegija

        datepickerPlaceHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datepicker = new DatePickerFragment();
                datepicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        timepickerPlaceHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timepicker = new TimePickerFragment();
                timepicker.show(getSupportFragmentManager(), "time picker");
            }
        });

    }


    // Preuzeto s primjera youtube kanala  'Coding in Flow' predloženog na stranici kolegija
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance().format(c.getTime());
        datepickerPlaceHolder.setText(currentDateString);
        date = currentDateString;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        timepickerPlaceHolder.setText(hourOfDay + ": " + minute);
        time = hourOfDay + ": " + minute;
    }


    private boolean isPinUnique(int pinToCheck){
        for(Iterator<Rezervacija> i = rezervacije.iterator(); i.hasNext();){
            if(pinToCheck == i.next().pin){
                return false;
            }
        }

        return true;
    }

    // Preuzeto s primjera youtube kanala  'Coding in Flow' predloženog na stranici kolegija

    private void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(rezervacije);
        editor.putString("rezervacije", json);
        editor.apply();
    }

    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString("rezervacije", null);
        Type type = new TypeToken<ArrayList<Rezervacija>>() {}.getType();
        rezervacije = gson.fromJson(json, type);

    }


    public void returnToMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void sendDataToMain(Rezervacija r){
        Gson gson = new Gson();
        String json = gson.toJson(r);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("rezervacija", json);
        startActivity(intent);
    }

    public void shortToast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void longToast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}
