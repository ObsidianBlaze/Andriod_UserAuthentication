package com.obsidian.demoapi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = "DeleteActivity";
	private static final String URL_FOR_LOGIN = "http://192.168.43.196:8080/ANDROIDAPI/delete.php";
	ProgressDialog progressDialog;
	private EditText emailInput;
	private Button btnDelete;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		emailInput = findViewById(R.id.delete_email);
		btnDelete = findViewById(R.id.delete_btn);
		// Progress dialog
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);

		btnDelete.setOnClickListener(e -> deleteUser(emailInput.getText().toString()));

	}

	private void deleteUser(final String email) {
		String cancel_req_tag = "delete";

		StringRequest strReq = new StringRequest(Request.Method.POST,
				URL_FOR_LOGIN, response -> {
			Log.d(TAG, "Register Response: " + response);
			try {
				JSONObject jObj = new JSONObject(response);
				boolean error = jObj.getBoolean("error");

				if (!error) {
					String user = jObj.getString("success");
					Toast.makeText(MainActivity.this, user, Toast.LENGTH_SHORT).show();

				} else {

					String errorMsg = jObj.getString("error_msg");
					Toast.makeText(getApplicationContext(),
							errorMsg, Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}, error -> {
			Log.e(TAG, "Delete Error: " + error.getMessage());
			Toast.makeText(getApplicationContext(),
					error.getMessage(), Toast.LENGTH_LONG).show();
		}) {
			@Override
			protected Map<String, String> getParams() {
				// Posting params to login url
				Map<String, String> params = new HashMap<>();
				params.put("email", email);
				return params;
			}
		};
		// Adding request to request queue
		AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);

	}

}
