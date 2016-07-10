package in.devzone.numberfish;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
public class MainActivity extends Activity {
    public String PhoneNum;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        Button findBtn = (Button) findViewById(R.id.findButton);
        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView numField = (TextView) findViewById(R.id.editText);
                PhoneNum = numField.getText().toString();

                new GetData().execute(PhoneNum);
            }
        });
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
                String number = jsonObject.getString("message");
                String status = jsonObject.getString("status");
                setContentView(R.layout.result_layout);
                TextView numField = (TextView) findViewById(R.id.textView);
                numField.setText(number);
                TextView statusField = (TextView) findViewById(R.id.textView2);
                statusField.setText(status);

            } catch (JSONException e) {
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
