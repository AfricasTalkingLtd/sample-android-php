package com.africastalking.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    // Http Client
    OkHttpClient client = new OkHttpClient();

    @BindView(R.id.txtAmount)
    EditText txtAmount;

    @BindView(R.id.txtAirtimeRecipients)
    EditText txtAirtimeRecipients;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Africa's Talking is Awesome", Snackbar.LENGTH_LONG)
                        .setAction("More", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String url = "https://www.africastalking.com";
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            }
                        }).show();
            }
        });

    }

    @OnClick(R.id.btnSendAirtime)
    public void sendAirtime() {
        String amount = txtAmount.getText().toString();
        Pattern p = Pattern.compile("^[a-zA-Z]{3} \\d+(:?\\.\\d+)?$");
        Matcher m = p.matcher(amount);
        if (!m.find()) {
            Snackbar.make(fab, "Invalid amount " + amount, Snackbar.LENGTH_SHORT).show();
            return;
        }

        String recipients = txtAirtimeRecipients.getText().toString();
        if (TextUtils.isEmpty(recipients)) {
            Snackbar.make(fab, "Invalid recipients " + recipients, Snackbar.LENGTH_SHORT).show();
            return;
        }

        // prepare json to send
        StringBuilder params = new StringBuilder("[");
        String[] phoneNumbers = recipients.split(",");
        for(String phone:phoneNumbers) {
            params.append("{\"phoneNumber\": \"" + phone + "\", \"amount\": \"" + amount + "\"}");
        }
        params.append("]");

        RequestBody formBody = new FormBody.Builder()
                .add("recipients", params.toString())
                .build();
        Request request = new Request.Builder()
                .url("http://192.168.1.39:4646")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Snackbar.make(fab, e.getMessage() + "", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Snackbar.make(fab, "Success!", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(fab, response.message(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
