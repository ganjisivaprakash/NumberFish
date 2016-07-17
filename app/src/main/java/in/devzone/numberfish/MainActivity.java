package in.devzone.numberfish;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by hari on 10/7/16.
 */
public class MainActivity extends Activity implements OnItemSelectedListener{
    public String PhoneNum;
    public String Code;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.country_list,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
            Button findBtn = (Button) findViewById(R.id.findButton);
            findBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView numField = (TextView) findViewById(R.id.editText);
                    PhoneNum = numField.getText().toString();
                    if (PhoneNum.isEmpty()) {
                        TextView alertField = (TextView) findViewById(R.id.alertView);
                        alertField.setText("Please Enter Number");
                    } else {
                        new GetData().execute(PhoneNum);
                    }
                }
            });


    }
    @Override
    public void onItemSelected(AdapterView<?> parent ,View view,int position,long id){
        String code = parent.getItemAtPosition(position).toString();
        Code = code;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Code = "+91";
    }
    public boolean isNetAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null && activeNetInfo.isConnected();
    }
    private class GetData extends AsyncTask<String, String, String>{
        String data;
        protected String doInBackground(String... get){
            String link = "http://www.devzone.in/app/mobo.php?number="+get[0];
            try {
                URL url = new URL(link);
                URLConnection urlConnection = url.openConnection();
                HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
                httpURLConnection.setAllowUserInteraction(false);
                httpURLConnection.setInstanceFollowRedirects(true);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                int respCode = httpURLConnection.getResponseCode();
                if(respCode == httpURLConnection.HTTP_OK){
                    InputStream dataStream = new BufferedInputStream(httpURLConnection.getInputStream());;
                    data = convertInputStreamToString(dataStream);
                }
                else{
                    return "nothing";
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }
        protected void onProgressUpdate(String... get){

        }
        protected void onPostExecute(String get){
                try {
                    JSONObject jsonObject = new JSONObject(get.toString());
                    String number = jsonObject.getString("number");
                    String status = jsonObject.getString("status");
                    setContentView(R.layout.result_layout);
                    Button backBtn = (Button) findViewById(R.id.backButton);
                    backBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setContentView(R.layout.main_activity_layout);
                        }
                    });
                    if (status.equals("0")) {
                        String recycled = jsonObject.getString("recycled");
                        String date = jsonObject.getString("date");
                        TextView numField = (TextView) findViewById(R.id.numberView);
                        numField.setText(number);
                        TextView statusField = (TextView) findViewById(R.id.statusView);
                        statusField.setText("In Active");
                        TextView dateField = (TextView) findViewById(R.id.dateView);
                        dateField.setText(date);
                        if (recycled.equals("0")) {
                            TextView rdateField = (TextView) findViewById(R.id.rdateView);
                            rdateField.setText("Not Recycled");
                        } else if (recycled.equals("1")) {
                            String r_date = jsonObject.getString("r_date");
                            TextView rdateField = (TextView) findViewById(R.id.rdateView);
                            rdateField.setText("Recycled on");
                            TextView rdateDateField = (TextView) findViewById(R.id.rdateDateView);
                            rdateDateField.setText(r_date);
                        }
                    } else if (status.equals("1")) {
                        String recycled = jsonObject.getString("recycled");
                        String date = jsonObject.getString("date");
                        TextView numField = (TextView) findViewById(R.id.numberView);
                        numField.setText(number);
                        TextView statusField = (TextView) findViewById(R.id.statusView);
                        statusField.setText("Active");
                        TextView dateField = (TextView) findViewById(R.id.dateView);
                        dateField.setText(date);
                        if (recycled.equals("0")) {
                            TextView rdateField = (TextView) findViewById(R.id.rdateView);
                            rdateField.setText("Not Recycled");
                        } else if (recycled.equals("1")) {
                            String r_date = jsonObject.getString("r_date");
                            TextView rdateField = (TextView) findViewById(R.id.rdateView);
                            rdateField.setText("Recycled on");
                            TextView rdateDateField = (TextView) findViewById(R.id.rdateDateView);
                            rdateDateField.setText(r_date);
                        }

                    } else if (status.equals("2")) {
                        TextView numField = (TextView) findViewById(R.id.numberView);
                        numField.setText("Number Unavailable");
                        TextView stextField = (TextView) findViewById(R.id.statusTextView);
                        stextField.setVisibility(View.INVISIBLE);
                        TextView rtextField = (TextView) findViewById(R.id.rdateTextView);
                        rtextField.setVisibility(View.INVISIBLE);
                    } else {
                        TextView numField = (TextView) findViewById(R.id.numberView);
                        numField.setText("Unexpected Error!");
                        TextView stextField = (TextView) findViewById(R.id.statusTextView);
                        stextField.setVisibility(View.INVISIBLE);
                        TextView rtextField = (TextView) findViewById(R.id.rdateTextView);
                        rtextField.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    TextView numField = (TextView) findViewById(R.id.numberView);
                    numField.setText("Connection Unavailable!");
                    e.printStackTrace();
                }


        }
        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null){
                result += line;
            }

            /* Close Stream */
            if(null!=inputStream){
                inputStream.close();
            }
            return result;
        }

    }
}
