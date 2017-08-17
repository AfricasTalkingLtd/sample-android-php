package com.africastalking.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    static final String BACKEND_URL = BuildConfig.BACKEND_URL;

    @BindView(R.id.txtAmount)
    EditText txtAmount;

    @BindView(R.id.txtAirtimeRecipients)
    EditText txtAirtimeRecipients;

    @BindView(R.id.txtSmsRecipients)
    EditText txtSmsRecipients;

    @BindView(R.id.txtMessage)
    EditText txtSmsMessage;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progress)
    ProgressBar progress;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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

        handler = new Handler();

    }

    @OnClick(R.id.btnSendAirtime)
    public void sendAirtime(final View button) {
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
                .url(BACKEND_URL + "/send/airtime")
                .post(formBody)
                .build();

        progress.setVisibility(View.VISIBLE);
        button.setEnabled(false);
        txtAirtimeRecipients.setEnabled(false);
        txtAmount.setEnabled(false);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progress.setVisibility(View.GONE);
                        txtAirtimeRecipients.setEnabled(true);
                        txtAmount.setEnabled(true);
                        button.setEnabled(true);
                        Snackbar.make(fab, e.getMessage() + "", Snackbar.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                Log.e("Airtime Response", response.body().string() + "");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progress.setVisibility(View.GONE);
                        button.setEnabled(true);
                        txtAirtimeRecipients.setEnabled(true);
                        txtAmount.setEnabled(true);
                        if (response.isSuccessful()) {
                            Snackbar.make(fab, "We got a response!", Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(fab, response.message(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    @OnClick(R.id.btnSendSMS)
    public void sendSms(final View button) {

        String message = txtSmsMessage.getText().toString();
        if (TextUtils.isEmpty(message)) {
            Snackbar.make(fab, "Invalid message", Snackbar.LENGTH_SHORT).show();
            return;
        }

        String recipients = txtSmsRecipients.getText().toString();
        if (TextUtils.isEmpty(recipients)) {
            Snackbar.make(fab, "Invalid recipients", Snackbar.LENGTH_SHORT).show();
            return;
        }

        RequestBody formBody = new FormBody.Builder()
                .add("recipients", recipients)
                .add("message", message)
                .build();
        Request request = new Request.Builder()
                .url(BACKEND_URL + "/send/sms")
                .post(formBody)
                .build();


        progress.setVisibility(View.VISIBLE);
        button.setEnabled(false);
        txtSmsMessage.setEnabled(false);
        txtSmsRecipients.setEnabled(false);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progress.setVisibility(View.GONE);
                        button.setEnabled(true);
                        txtSmsMessage.setEnabled(true);
                        txtSmsRecipients.setEnabled(true);
                        Snackbar.make(fab, e.getMessage() + "", Snackbar.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                Log.e("Sms Response", response.body().string() + "");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progress.setVisibility(View.GONE);
                        button.setEnabled(true);
                        txtSmsMessage.setEnabled(true);
                        txtSmsRecipients.setEnabled(true);
                        if (response.isSuccessful()) {
                            Snackbar.make(fab, "We got a response!", Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(fab, response.message(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
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
