package ca.mcgill.ecse321.rsms_android;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import cz.msebera.android.httpclient.Header;

public class ShowShifts extends AppCompatActivity {
    String dateRequired;
    ListView myListView;
    List<String> shiftId;
    List<String> timeStart;
    List<String> timeEnd;
    String error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_shifts);
        final TextView errorMe=findViewById(R.id.errorM);
        myListView = findViewById(R.id.shiftsListView);
        RequestParams rp=new RequestParams();
        rp.add("dates", dateRequired);
        HttpUtils.get("schedules/shifts/get_for_dates",rp,new JsonHttpResponseHandler(){

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    if(response!=null) {
                        shiftId = getValuesForGivenKey(response, "shiftId");
                        timeStart = getValuesForGivenKey(response, "startTime");
                        timeEnd = getValuesForGivenKey(response, "endTime");
                    }
                    else{
                        error="no shifts for this date!";
                        errorMe.setText(error);
                    }
                } catch (Exception e) {
                    error = e.getMessage();
                    errorMe.setText(error);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try{
                    error=errorResponse.get("message").toString();}
                catch(Exception e){
                    error=e.getMessage();
                    errorMe.setText(error);
                }
                Intent pageBack=new Intent(getApplicationContext(),MakeAppointmentSelectDate.class);
                startActivity(pageBack);
            }
        });

        ShiftAdapter shiftAdapter = new ShiftAdapter(this, shiftId, timeStart, timeEnd, dateRequired);
        myListView.setAdapter(shiftAdapter);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent selectServiceAndCar = new Intent(getApplicationContext(), SelectServiceAndCar.class);
                selectServiceAndCar.putExtra("shiftId", shiftId.get(position));
                startActivity(selectServiceAndCar);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<String> getValuesForGivenKey(JSONArray jsonArray, String key) throws JSONException {
        return IntStream.range(0, jsonArray.length())
                .mapToObj(index -> {
                    try {
                        return ((JSONObject) jsonArray.get(index)).optString(key);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }
}