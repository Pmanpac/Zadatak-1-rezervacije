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

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private EditText pinInput, restoran, broj_osoba, ime;
    private ImageView abort, commit;
    private String date, time = null;

    private int pin;

    private Rezervacija rezervacija;
    private int rezervacijaIndex;

    private ArrayList<Rezervacija> rezervacije;

    private ImageView makeReservation, editReservation, deletereservation, readreservation;

    private TextView timepickerPlaceHolder, datepickerPlaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String json = intent.getStringExtra("rezervacija");
        Gson gson = new Gson();
        Type type = new TypeToken<Rezervacija>() {}.getType();
        Rezervacija r = gson.fromJson(json, type);

        loadData();

        if(rezervacije == null){
            rezervacije = new ArrayList<Rezervacija>();
        }

        pinInput = findViewById(R.id.pin_input);
        restoran = findViewById(R.id.restoran_input);
        broj_osoba = findViewById(R.id.broj_input);
        ime = findViewById(R.id.ime_input);

        timepickerPlaceHolder = findViewById(R.id.timepicker);
        datepickerPlaceHolder = findViewById(R.id.datepicker);

        if(r != null){
            pinInput.setText(String.valueOf(r.pin));
            restoran.setText(r.restoran);
            broj_osoba.setText(String.valueOf(r.broj_osoba));
            ime.setText(r.ime);

            timepickerPlaceHolder.setText(r.vrijeme);
            datepickerPlaceHolder.setText(r.datum);
        }

        makeReservation = findViewById(R.id.make_reservation);
        editReservation = findViewById(R.id.edit_reservation);
        deletereservation = findViewById(R.id.delete_reservation);
        readreservation = findViewById(R.id.read_reservation);


        makeReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openRezervacijaActivity();
            }
        });

        editReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPin();
                if (updateRezervacija()){
                    shortToast("Rezervacija s PIN-om " + pin + "je izmijenjena");
                }
            }
        });

        deletereservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteRezervacija()){
                    shortToast("Rezervacija s PIN-on " + pin + " je izbrisana!");
                }
            }
        });

        readreservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readRezervacija();
            }
        });


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

    public void openRezervacijaActivity(){
        Intent intent = new Intent(this, RezervacijaActivity.class);
        startActivity(intent);
    }

    private boolean validateInputFields(){
        if (restoran.getText().toString().trim().length() < 1){
            shortToast("Polje 'Restoran' ne smije biti prazno.");
            return false;
        }else if(broj_osoba.getText().toString().trim().length() < 1 || broj_osoba.getText() == null || Integer.parseInt(broj_osoba.getText().toString()) < 1){
            shortToast("Polje 'Broj osoba' mora biti broj veći od 0");
            return false;
        }else if(ime.getText().toString().trim().length() < 1){
            shortToast("Polje 'Na ime' ne smije biti prazno");
            return false;
        }else if(time == null || date == null) {
            shortToast("Polja 'Datum' i 'Vrijeme' moraju biti popunjeni");
            return false;
        } return true;
    }

    public boolean readRezervacija(){
        getPin();
        if(!isPinUnique(pin)){
            getRezervacija(pin);

                broj_osoba.setText(String.valueOf(rezervacija.broj_osoba));
                datepickerPlaceHolder.setText(rezervacija.datum);
                date = rezervacija.datum;
                time = rezervacija.vrijeme;
                timepickerPlaceHolder.setText(rezervacija.vrijeme);
                restoran.setText(rezervacija.restoran);
                ime.setText(rezervacija.ime);

                return true;

        }else{
            shortToast("Rezervacija ne postoji");
            return false;
        }

    }

    private boolean updateRezervacija(){
        getPin();
        if(!isPinUnique(pin)){
            getRezervacija(pin);
            if(validateInputFields()){
                rezervacije.get(rezervacijaIndex).broj_osoba = Integer.parseInt(broj_osoba.getText().toString());
                rezervacije.get(rezervacijaIndex).datum = date;
                rezervacije.get(rezervacijaIndex).vrijeme = time;
                rezervacije.get(rezervacijaIndex).restoran = restoran.getText().toString();
                rezervacije.get(rezervacijaIndex).ime = ime.getText().toString();

                saveData();
                return true;

            }else{ shortToast("Naso sam te...");return false;}
        }else{
            shortToast("Rezervacija ne postoji");
            return false;
        }

    }

    private boolean deleteRezervacija(){
        getPin();
        if(!isPinUnique(pin)){
            getRezervacija(pin);
            rezervacije.remove(rezervacijaIndex);
            saveData();
            pinInput.setText("");
            restoran.setText("");
            datepickerPlaceHolder.setText("Odaberite datum");
            timepickerPlaceHolder.setText("Odaberite vrijeme");
            broj_osoba.setText("");
            ime.setText("");
            return true;
        }else{
            shortToast("Rezervacija ne postoji");
            return false;
        }
    }

    private void getPin(){
        if(pinInput.getText().toString().trim().length() > 0){
            pin = Integer.parseInt(pinInput.getText().toString());
        }
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

    // Preuzeto s primjera youtube kanala  'Coding in Flow' predloženog na stranici kolegija
    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString("rezervacije", null);
        Type type = new TypeToken<ArrayList<Rezervacija>>() {}.getType();
        rezervacije = gson.fromJson(json, type);

    }

    private boolean isPinUnique(int pinToCheck){
        for(Iterator<Rezervacija> i = rezervacije.iterator(); i.hasNext();){
            if(pinToCheck == i.next().pin){
                return false;
            }
        }

        return true;
    }

    private void getRezervacija(int pin){
        if(!isPinUnique(pin)){
            for(Rezervacija r: rezervacije){
                if(pin == r.pin){
                    rezervacija = r;
                    rezervacijaIndex = rezervacije.indexOf(r);
                }
            }
        }
    }



    public void shortToast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    public void longToast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

}
