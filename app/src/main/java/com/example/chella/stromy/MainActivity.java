package com.example.chella.stromy;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private  CurrentWeather mCurrentWeather;

    @InjectView(R.id.timeLabel)
    TextView mTimeLabel;
    @InjectView(R.id.temperatureTextView) TextView mTemperatureLabel;
    @InjectView(R.id.humidityValue) TextView mHumidityValue;
    @InjectView(R.id.percepValue) TextView mPrecipValue;
    @InjectView(R.id.summaryLabel) TextView mSummaryLabel;
    @InjectView(R.id.iconImageView) ImageView mIconImageView;
    @InjectView(R.id.refreshImage) ImageView mrefreshImageView;
    @InjectView(R.id.progressBar) ProgressBar mprogressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        final double logitude = 37.8267;
        final double lattitude = -122.423;

        mprogressBar.setVisibility(View.INVISIBLE);

        mrefreshImageView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 getForecast(logitude, lattitude);
             }
         });


        getForecast(logitude, lattitude);
        Log.d(TAG,"Mainactiity is running");
    }

    private void getForecast(double logitude, double lattitude) {
        String apiKey = "c086161bf48a151a93468ffefc7d79d9";

        String forcastURL = "https://api.forecast.io/forecast/"+apiKey+"/"+logitude+","+lattitude;

        //String forcastURL = "https://api.forecast.io/forecast/c086161bf48a151a93468ffefc7d79d9/37.8267,-122.423";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(forcastURL).build();
        if(idNetworkAvailable()) {

            toggleRefresh();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAbutFailure();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mCurrentWeather = getCurrentWeatherDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDetails();
                                }
                            });
                        } else {
                            alertUserAbutFailure();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception in ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception in ", e);
                    }

                }
            });
        }
        else{
            Toast.makeText(this, "Network Connectivity not available", Toast.LENGTH_LONG).show();
        }
    }

    private void toggleRefresh() {
        if(mprogressBar.getVisibility() == View.INVISIBLE) {
            mprogressBar.setVisibility(View.VISIBLE);
            mrefreshImageView.setVisibility(View.INVISIBLE);
        }else{
            mprogressBar.setVisibility(View.INVISIBLE);
            mrefreshImageView.setVisibility(View.VISIBLE);
        }

    }

    public void alertUserAbutFailure(){
        AlertDialogueFragment dialog = new AlertDialogueFragment();
        dialog.show(getFragmentManager(),"error_message");
    }

    public boolean idNetworkAvailable(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailbale = false;
        if(networkInfo != null && networkInfo.isConnected()){
            isAvailbale = true;
        }

        return isAvailbale;

    }
    public CurrentWeather getCurrentWeatherDetails(String jsonData) throws JSONException{

        JSONObject jsonObject = new JSONObject(jsonData);
        String timeZone = jsonObject.getString("timezone");

        JSONObject currently = jsonObject.getJSONObject("currently");

        CurrentWeather currentWeather = new CurrentWeather();

        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setPercepChance(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemp(currently.getDouble("temperature"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setTimeZone(timeZone);


        Log.d(TAG,"formatted time "+ currentWeather.getFormattedTime());
        return currentWeather;

    }

    public void updateDetails(){
        mTemperatureLabel.setText(mCurrentWeather.getTemp()+"");
        mTimeLabel.setText("At "+ mCurrentWeather.getFormattedTime() + " it will be");
        mHumidityValue.setText(mCurrentWeather.getHumidity()+"");
        mPrecipValue.setText(mCurrentWeather.getPercepChance()+"%");
        mSummaryLabel.setText(mCurrentWeather.getSummary());

        Drawable drawable = getResources().getDrawable(mCurrentWeather.getIconId());
        mIconImageView.setImageDrawable(drawable);


    }
}
