package hust.set.tientran695.iot;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by ngoct on 5/30/2017.
 */

public class Login_Activity extends AppCompatActivity {
    Button btnLogin;
    EditText edt_ip;
    String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edt_ip = (EditText) findViewById(R.id.ip_add);
        btnLogin = (Button) findViewById(R.id.btn_login);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Login_Activity.this, MainActivity.class);
                Bundle bundle = new Bundle();

                //checkInternet();
                checkEditTextnotNull();
                if (checkEditTextnotNull()) {
                    ip = edt_ip.getText().toString();
                    bundle.putString("ip_add", ip);
                    myIntent.putExtra("packet", bundle);
                    startActivity(myIntent);
                }
            }
        });
    }

    /*private boolean checkInternet() {
        boolean checkInternet = true;
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo == null) {
            checkInternet = false;
            Toast.makeText(Login_Activity.this, "Không có kết nối Internet!", Toast.LENGTH_LONG).show();
        }
        return checkInternet;
    }*/

    private boolean checkEditTextnotNull() {
        boolean checkEditTextNotNull = true;
        String ipCheck = edt_ip.getText().toString();
        if (ipCheck.equals("")) {
            checkEditTextNotNull = false;
            Toast.makeText(Login_Activity.this, "Nhập địa chỉ máy chủ", Toast.LENGTH_SHORT).show();
        }
        return checkEditTextNotNull;
    }
}
