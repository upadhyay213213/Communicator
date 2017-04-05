package subcodevs.communicator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

import apputils.PrefrensUtils;
import subcodevs.communicator.ui.LoginScreen;

public class SettingScreen extends Activity {
    ArrayList<String> categories = new ArrayList<>();
    ArrayList<String> categories1 = new ArrayList<>();
    public static String mSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Spinner element
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        Button mSave = (Button) findViewById(R.id.saveButton);
        // Spinner Drop down elements
        categories.add("PRODUCTION");
        categories.add("STAGING");
        categories1.add("https://residentcommunicator.net/");
        categories1.add("https://aptoseniorcare.com/");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        if (PrefrensUtils.getBaseURL(this).equals("https://residentcommunicator.net/")) {
            spinner.setSelection(0);
        } else {
            spinner.setSelection(1);
        }

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("SelectedURL" + categories1.get(spinner.getSelectedItemPosition()));
                PrefrensUtils.setBaseURL(SettingScreen.this, categories1.get(spinner.getSelectedItemPosition()));

                SettingScreen.this.finish();
                startActivity(new Intent(SettingScreen.this, LoginScreen.class));
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        startActivity(new Intent(this, LoginScreen.class));
    }
}
