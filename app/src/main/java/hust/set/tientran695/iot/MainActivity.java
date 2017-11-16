package hust.set.tientran695.iot;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {
    private final int REQ_CODE_SPEECH_INPUT = 100;

    TextView txt_nhietdo, txt_doam, test;
    Switch sw1, sw2, sw3, sw4;
    boolean isCheck1, isCheck2, isCheck3, isCheck4;
    WebView webView, webView2;
    //Url ThinkSpeak
    String Url = "<iframe width=\"450\" height=\"260\" style=\"border: " +
            "1px solid #cccccc;\" src=\"https://thingspeak.com/channels/253548/charts/1?bgcolor=" +
            "%23ffffff&color=%23d62020&dynamic=true&results=60&title=Bi%E1%BB%83u+%C4%91%E1%B" +
            "B%93+Nhi%E1%BB%87t+%C4%91%E1%BB%99&type=line&xaxis=Th%E1%BB%9Di+gian&yaxis=Nhi" +
            "%E1%BB%87t+%C4%91%E1%BB%99+%28Celsius%29\"></iframe>";
    String Url2 = "<iframe width=\"450\" height=\"260\" style=\"border: " +
            "1px solid #cccccc;\" src=\"https://thingspeak.com/channels/253548/charts" +
            "/2?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&title=Bi%E1%BB%83u+%C" +
            "4%91%E1%BB%93+%C4%90%E1%BB%99+%E1%BA%A9m&type=line&xaxis=Th%E1%BB%9Di+gian&yaxis=%C4" +
            "%90%E1%BB%99+%E1%BA%A9m+%28%25%29\"></iframe>";

    String ip_socket;
    private Socket mSocket;
   /* {
        try {
            mSocket = IO.socket(ip_socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent callerIntent = getIntent();
        Bundle packetBundle = callerIntent.getBundleExtra("packet");//Lấy dữ liệu từ Login Activity
        String ip = packetBundle.getString("ip_add");
        ip_socket = "http://" + ip + "/android";
        //Kết nối tới Server
        {
            try {
                mSocket = IO.socket(ip_socket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        txt_nhietdo = (TextView) findViewById(R.id.txt_nhietdo);
        txt_doam = (TextView) findViewById(R.id.txt_doam);
        sw1 = (Switch) findViewById(R.id.switch1);
        sw2 = (Switch) findViewById(R.id.switch2);
        sw3 = (Switch) findViewById(R.id.switch3);
        sw4 = (Switch) findViewById(R.id.switch4);
        test = (TextView) findViewById(R.id.test);

        mSocket.emit("UPDATE");
        mSocket.on("SENSOR", Update);
        mSocket.on("DECIVE", Swith_Status);
        mSocket.connect();

        webView = (WebView) findViewById(R.id.Webview);
        webView2 = (WebView) findViewById(R.id.Webview2);
        webView.getSettings().setJavaScriptEnabled(true);
        webView2.getSettings().setJavaScriptEnabled(true);
        webView.loadData(Url, "text/html", null);
        webView2.loadData(Url2, "text/html", null);

        sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCheck1 = isChecked;
                Control();
            }
        });

        sw2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCheck2 = isChecked;
                Control();
            }
        });

        sw3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCheck3 = isChecked;
                Control();
            }
        });

        sw4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCheck4 = isChecked;
                Control();
            }
        });


    }

    public void Control() {
        JSONArray array = new JSONArray();
        JSONObject data = new JSONObject();
        array.put(isCheck1);
        array.put(isCheck2);
        array.put(isCheck3);
        array.put(isCheck4);
        try {
            data.put("Status", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit("CONTROL", data);
    }

    private Emitter.Listener Update = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String nhiet_do = "";
                    String do_am = "";
                    try {
                        nhiet_do = data.getString("Temperature");
                        do_am = data.getString("Humidity");
                    } catch (JSONException e) {
                        return;
                    }
                    txt_nhietdo.setText(nhiet_do + "\u2103");
                    txt_doam.setText(do_am + "%");
                }
            });
        }//end call()
    }; //end Listener

    private Emitter.Listener Swith_Status = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String status_sw1, status_sw2, status_sw3, status_sw4;
                    try {
                        status_sw1 = data.getString("decive_1_Status");
                        status_sw2 = data.getString("decive_2_Status");
                        status_sw3 = data.getString("decive_3_Status");
                        status_sw4 = data.getString("decive_4_Status");
                    } catch (JSONException e) {
                        return;
                    }
                    if (status_sw1 == "1")
                        sw1.setChecked(true);
                    else
                        sw1.setChecked(false);

                    if (status_sw2 == "1")
                        sw2.setChecked(true);
                    else
                        sw2.setChecked(false);

                    if (status_sw3 == "1")
                        sw3.setChecked(true);
                    else
                        sw3.setChecked(false);

                    if (status_sw4 == "1")
                        sw4.setChecked(true);
                    else
                        sw4.setChecked(false);
                }
            });
        }//end call()
    }; //end Listener

    public void btb_UpdateProcess(View v) {
        mSocket.emit("UPDATE");
        Toast.makeText(this, "Dữ liệu đã được cập nhật", Toast.LENGTH_LONG).show();
    }

    //Hàm tạo menu option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    //Xử lý sự kiện khi click vao menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.voice:
                promptSpeechInput();
                //Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
                return (true);

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    test.setText(result.get(0));
                }
                break;
            }

        }
    }
}