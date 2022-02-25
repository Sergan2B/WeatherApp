package kg.geektech.weatherapp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

import kg.geektech.weatherapp.databinding.ActivityMainBinding;
import kg.geektech.weatherapp.databinding.FragmentWeatherBinding;

public class Weather_Fragment extends Fragment {
    private FragmentWeatherBinding binding;
    private String city;
    private String key;
    private String url;
    public Weather_Fragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWeatherBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FocusListener();
        OnClick();
    }


    private void OnClick() {
        binding.button.setOnClickListener(view -> {
            if (binding.userCity.getText().toString().trim().equals(""))
                Toast.makeText(requireActivity(), "Заполните поле", Toast.LENGTH_SHORT).show();
            else {
                city = binding.userCity.getText().toString();
                key = "a5f0ece2d473d3a6319e91cef81147cb";
                url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=ru";
                new GetURLData().execute(url);

            }
        });
    }

    private void FocusListener() {
        binding.userCity.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) binding.userCity.setText("");
        });
    }

    private class GetURLData extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            binding.resultInfoString.setText("Загрузка");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line;
                while ((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                DateFormat df = DateFormat.getDateTimeInstance();
                JSONObject jsonObject = new JSONObject(result);
                JSONObject details = jsonObject.getJSONArray("weather").getJSONObject(0);
                JSONObject main = jsonObject.getJSONObject("main");
                JSONObject sys = jsonObject.getJSONObject("sys");


                String updatedOn = df.format(new Date(jsonObject.getLong("dt") * 1000));
                binding.tvTime.setText("Данные на: " + updatedOn);

                binding.resultInfoDouble.setText("Температура: " + main.getDouble("temp") + "°C"
                        + "\n Ощущяется как: " + main.getDouble("feels_like") + "°C"
                        + "\n Минимальная температура: " + main.getDouble("temp_min") + "°C"
                        + "\n Максимальная температура: " + main.getDouble("temp_max") + "°C");

                binding.resultInfoString.setText("Погода: " + details.getString("description")
                                + "\n Состояние: " + details.getString("main")
                        /*+ "" + details.getString("icon")*/);

                binding.resultInfoString2.setText("Страна: " + sys.getString("country")
                        + "\n Город: " + jsonObject.getString("name"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}