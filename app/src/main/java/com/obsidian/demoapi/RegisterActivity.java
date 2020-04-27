package com.obsidian.demoapi;

import android.app.ProgressDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

	private static final String TAG = "RegisterActivity";
	private static final String URL_FOR_REGISTRATION = "http://192.168.43.196:8080/ANDROIDAPI/register.php";
	ProgressDialog progressDialog;

	private EditText signupInputName, signupInputEmail, signupInputPassword, signupInputAge;
	private Button btnSignUp;
	private Button btnLinkLogin;
	private RadioGroup genderRadioGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		// Progress dialog
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);

		signupInputName = findViewById(R.id.signup_input_name);
		signupInputEmail = findViewById(R.id.signup_input_email);
		signupInputPassword = findViewById(R.id.signup_input_password);
		signupInputAge = findViewById(R.id.signup_input_age);

		btnSignUp =  findViewById(R.id.btn_signup);
		btnLinkLogin =  findViewById(R.id.btn_link_login);

		genderRadioGroup =  findViewById(R.id.gender_radio_group);
		btnSignUp.setOnClickListener(view -> submitForm());
		btnLinkLogin.setOnClickListener(view -> {

			Intent i = new Intent(getApplicationContext(),LoginActivity.class);
			startActivity(i);
		});
	}

	private void submitForm() {

		int selectedId = genderRadioGroup.getCheckedRadioButtonId();
		String gender;
		if(selectedId == R.id.female_radio_btn)
			gender = "Female";
		else
			gender = "Male";

		registerUser(signupInputName.getText().toString(),
				signupInputEmail.getText().toString(),
				signupInputPassword.getText().toString(),
				gender,
				signupInputAge.getText().toString());
	}

	private void registerUser(final String name,  final String email, final String password,
	                          final String gender, final String dob) {
		// Tag used to cancel the request
		String cancel_req_tag = "register";

		progressDialog.setMessage("Adding you ...");
		showDialog();

		StringRequest strReq = new StringRequest(Request.Method.POST,
				URL_FOR_REGISTRATION, response -> {
					Log.d(TAG, "Register Response: " + response);
					hideDialog();

					try {
						JSONObject jObj = new JSONObject(response);
						boolean error = jObj.getBoolean("error");

						if (!error) {
							String user = jObj.getJSONObject("user").getString("name");
							Toast.makeText(getApplicationContext(), "Hi " + user +", You are successfully Added!", Toast.LENGTH_SHORT).show();

							// Launch login activity
							Intent intent = new Intent(
									RegisterActivity.this,
									LoginActivity.class);
							startActivity(intent);
							finish();
						} else {

							String errorMsg = jObj.getString("error_msg");
							Toast.makeText(getApplicationContext(),
									errorMsg, Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}, error -> {
			Log.e(TAG, "Registration Error: " + error.getMessage());
			Toast.makeText(getApplicationContext(),
					error.getMessage(), Toast.LENGTH_LONG).show();
			hideDialog();
		}) {
			@Override
			protected Map<String, String> getParams() {
				// Posting params to register url
				Map<String, String> params = new HashMap<>();
				params.put("name", name);
				params.put("email", email);
				params.put("password", password);
				params.put("gender", gender);
				params.put("age", dob);
				return params;
			}
		};
		// Adding request to request queue
		AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(strReq, cancel_req_tag);
	}

	private void showDialog() {
		if (!progressDialog.isShowing())
			progressDialog.show();
	}

	private void hideDialog() {
		if (progressDialog.isShowing())
			progressDialog.dismiss();
	}
}