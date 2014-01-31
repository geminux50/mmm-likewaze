package org.istic.mmm_likewaze;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.istic.mmm_likewaze.model.User;
import com.istic.mmm_likewaze.remote.controller.RemoteUserController;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "";

	// Values for email and password at the time of the login attempt.
	private String mPseudo;
	private String mPassword;

	// UI references.
	private EditText mEmailEditTxt;
	private EditText mPasswordEditTxt;
	
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	// user login web service
	
	private RemoteUserController _usrcntrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// Set up the login form.
		mPseudo = "";// getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailEditTxt = (EditText) findViewById(R.id.pseudo);
		mEmailEditTxt.setText(mPseudo);

		mPasswordEditTxt = (EditText) findViewById(R.id.password);
		/*mPasswordEditTxt
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});*/

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						
						
					//  to delte just for test now 
						
						if( attemptLogin()  == true){
                           Log.i("RESULT LOGIN :", "  login succeded  ! ");
						//Intent intent = new Intent(LoginActivity.this, VehiculeModeActivity.class);
					    //startActivity(intent);							
						}else{
							
							Log.i("RESULT LOGIN:","login failed " );
						}

					}}	
				
				
				);
		
		TextView textView_signup = (TextView) this.findViewById(R.id.sign_up_smessage_id);
		textView_signup.setOnClickListener(
				
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
					    startActivity(intent);
						
						}}	
	
				);
		
		_usrcntrl = new RemoteUserController();
			
		//  To delate later
	
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
 
	
	
	/**
	 * 
	 * @param pseudo : pseudo to login
	 * @param password  : password to login 
	 * @return  an insance of user 
	 */
	private User loginOperation(String pseudo, String password){
		
		  return _usrcntrl.login(pseudo, password);
	}
	
	
	
	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public boolean attemptLogin() {
		

		// Reset errors.
		mEmailEditTxt.setError(null);
		mPasswordEditTxt.setError(null);

		// Store values at the time of the login attempt.
		mPseudo = mEmailEditTxt.getText().toString();
		mPassword = mPasswordEditTxt.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordEditTxt.setError(getString(R.string.error_field_required));
			focusView = mPasswordEditTxt;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordEditTxt.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordEditTxt;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mPseudo)) {
			mEmailEditTxt.setError(getString(R.string.error_field_required));
			focusView = mEmailEditTxt;
			cancel = true;
		} 

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
			return false;
		} else {
					// Show a progress spinner, and kick off a background task to
					// perform the user login attempt.
					mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
					showProgress(true);
		
					if(loginOperation(mPseudo,mPassword) != null){
		
						Toast.makeText(getBaseContext(),(String)"  login accepted  ", 
				                Toast.LENGTH_SHORT).show();
						for(int i=0; i< 30000000; i++){};
						 return true;
						// Call the activity to display the map. 
					}else{
						
		
						Toast.makeText(getBaseContext(),(String)" login failed ", 
				                Toast.LENGTH_SHORT).show();
						showProgress(false);
						return false;
					}
					
		    }
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

}
