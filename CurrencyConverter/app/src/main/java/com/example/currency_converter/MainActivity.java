package com.example.currency_converter;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView convertFromDropdownTextView, convertToDropdownTextView, conversionRateText;
    EditText amountToConvert;
    ArrayList<String> arrayList;
    Dialog fromDialog;
    Dialog toDialog;
    Button conversionButton, refreshButton;
    String convertFromValue, convertToValue, conversionValue;
    String[] country = {"AUD", "BRL", "CAD", "CHF", "CNY", "EUR", "GBP", "HKD", "IDR", "INR", "JPY", "KRW", "MXN", "MYR", "NOK", "NZD", "PHP", "PKR", "RUB", "SEK", "SGD", "THB", "TRY", "USD", "ZAR"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        convertFromDropdownTextView = findViewById(R.id.convert_from_dropdown_menu);
        convertToDropdownTextView = findViewById(R.id.convert_to_dropdown_menu);
        conversionButton = findViewById(R.id.conversionButton);
        conversionRateText = findViewById(R.id.conversionRateText);
        amountToConvert = findViewById(R.id.amountToConvertValueEditText);
        refreshButton = findViewById(R.id.refreshButton);

        arrayList = new ArrayList<>();
        for (String i : country) {
            arrayList.add(i);
        }

        convertFromDropdownTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFromDialog();
            }
        });

        convertToDropdownTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToDialog();
            }
        });

        conversionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Double amount = Double.valueOf(amountToConvert.getText().toString());
                    getConversionRate(convertFromValue, convertToValue, amount);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    showToast("Invalid input. Please enter a valid number.");
                }
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshExchangeRates();
            }
        });
    }

    private void refreshExchangeRates() {

        try {
            Double amount = Double.valueOf(amountToConvert.getText().toString());
            getConversionRate(convertFromValue, convertToValue, amount);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            showToast("Invalid input. Please enter a valid number.");
        }
    }

    private void showFromDialog() {
        fromDialog = new Dialog(MainActivity.this);
        fromDialog.setContentView(R.layout.from_spinner);
        fromDialog.getWindow().setLayout(650, 800);
        fromDialog.show();

        EditText editText = fromDialog.findViewById(R.id.edit_text);
        ListView listView = fromDialog.findViewById(R.id.list_view);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {}

        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                convertFromDropdownTextView.setText(adapter.getItem(position));
                fromDialog.dismiss();
                convertFromValue = adapter.getItem(position);
            }
        });
    }

    private void showToDialog() {
        toDialog = new Dialog(MainActivity.this);
        toDialog.setContentView(R.layout.to_spinner);
        toDialog.getWindow().setLayout(650, 800);
        toDialog.show();

        EditText editText = toDialog.findViewById(R.id.edit_text);
        ListView listView = toDialog.findViewById(R.id.list_view);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                convertToDropdownTextView.setText(adapter.getItem(position));
                toDialog.dismiss();
                convertToValue = adapter.getItem(position);
            }
        });
    }

    public void getConversionRate(String conversionFrom, String convertTo, Double amountToConvert) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://v6.exchangerate-api.com/v6/e2d78aa1ec5f6419965c5086/latest/" + conversionFrom;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject conversionRates = jsonObject.getJSONObject("conversion_rates");

                    LinearLayout exchangeRatesLayout = findViewById(R.id.exchangeRatesLayout);
                    exchangeRatesLayout.removeAllViews();

                    for (int i = 0; i < country.length; i++) {
                        String currency = country[i];
                        if (conversionRates.has(currency)) {
                            Double rate = conversionRates.getDouble(currency);
                            TextView textView = new TextView(MainActivity.this);
                            textView.setText(currency + ": " + rate);
                            textView.setTextColor(getResources().getColor(android.R.color.white));
                            exchangeRatesLayout.addView(textView);
                        }
                    }

                    if (conversionRates.has(convertTo)) {
                        Double conversionRateValue = conversionRates.getDouble(convertTo);
                        conversionValue = String.valueOf(round(conversionRateValue * amountToConvert, 2));
                        Log.d("Conversion", "Conversion Rate Value: " + conversionRateValue);
                        Log.d("Conversion", "Conversion Value: " + conversionValue);
                        conversionRateText.setText(conversionValue);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("Error processing conversion data. Please try again later.");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if (volleyError.networkResponse == null) {
                    showToast("No internet connection");
                } else {
                    showToast("Failed to fetch conversion rates. Please try again later.");
                }
            }
        });
        queue.add(stringRequest);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
