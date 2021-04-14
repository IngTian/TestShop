package ca.mcgill.ecse321.rsms_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class SelectServiceAndCar extends AppCompatActivity {
    private ImageButton button_wash;
    private ImageButton button_maintenance;
    private ImageButton button_road_assistance;
    private ImageButton button_tire_change;
    private ImageButton button_towing;
    private ImageButton button_car_inspection;
    private Button button_confirm;

    Date date;
    String UName;
    String Password;
    String plateNo;
    String serviceType = "";
    String error;
    Time startTime;
    Time endTime;
    Integer weight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_service_and_car);
        button_confirm = findViewById(R.id.button);
        button_confirm.setOnClickListener(v -> make_appointment());

        button_wash = findViewById(R.id.imageButton);
        button_wash.setOnClickListener(v -> wash());

        button_maintenance = findViewById(R.id.imageButton2);
        button_maintenance.setOnClickListener(v -> maintenance());

        button_road_assistance = findViewById(R.id.imageButton3);
        button_road_assistance.setOnClickListener(v -> road_assistance());

        button_tire_change = findViewById(R.id.imageButton4);
        button_tire_change.setOnClickListener(v -> tire_change());

        button_towing = findViewById(R.id.imageButton5);
        button_towing.setOnClickListener(v -> towing());

        button_car_inspection = findViewById(R.id.imageButton6);
        button_car_inspection.setOnClickListener(v -> car_inspection());
    }

    private void make_appointment(){
        RequestParams rp=new RequestParams();
        rp.add("serviceType",serviceType);
        rp.add("username",UName);
        rp.add("plateNo",plateNo);
        rp.add("date", String.valueOf(date));
        rp.add("startTime", String.valueOf(startTime));
        rp.add("endTime", String.valueOf(endTime));
        rp.add("weight", String.valueOf(weight));

        HttpUtils.post("/appointment/make_appointment" , rp, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                refreshErrorMessage();
                ((TextView) findViewById(R.id.error)).setText("");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    error += errorResponse.get("message").toString();
                } catch (JSONException e) {
                    error += e.getMessage();
                }
                refreshErrorMessage();
            }
        });

        Intent homepage = new Intent(getApplicationContext(), HomePageActivity.class);
        homepage.putExtra("ca.mcgill.ecse321.rsms.android.CURRUNAME",UName);
        homepage.putExtra("ca.mcgill.ecse321.rsms_android.CURRPASSWORD",Password);

        startActivity(homepage);
    }
    private void wash(){
        serviceType = "car wash";
    }
    private void maintenance(){
        serviceType = "maintenance";
    }
    private void road_assistance(){
        serviceType = "road assistance";
    }
    private void tire_change(){
        serviceType = "tire change";
    }
    private void towing(){
        serviceType = "towing";
    }
    private void car_inspection(){
        serviceType = "car inspection";
    }
    private void refreshErrorMessage() {
        // set the error message
        TextView tvError = (TextView) findViewById(R.id.error);
        tvError.setText(error);

        if (error == null || error.length() == 0) {
            tvError.setVisibility(View.GONE);
        } else {
            tvError.setVisibility(View.VISIBLE);
        }
    }
}