package org.istic.mmm_likewaze;

import com.istic.mmm_likewaze.model.User;
import com.istic.mmm_likewaze.remote.controller.RemoteUserController;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends Activity {

	private String _email;
	private String _pseudo;
	private String _password;
	
	
	private EditText _emailEditText;
	private EditText _pseudoEditText;
	private EditText _passwordEditText;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		
		//  GET the edit text references
		
		_pseudoEditText =(EditText) findViewById(R.id.pseudoSignUpEditText);
		_passwordEditText = (EditText)  findViewById(R.id.passwordSignUpEditText);
		_emailEditText = (EditText) findViewById(R.id.emailSignUpEditText);
		
		
		
		//  configure the listeners 
		
         findViewById(R.id.signUpButton).setOnClickListener(
				
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						
						
					//  to delte just for test now 
						
						if( attempSignUp()  == true){
                           Log.i("RESULT  SIGNUP :", " sign up succeded  ! ");
						   Intent intent = new Intent(SignUpActivity.this, VehiculeModeActivity.class);
					       startActivity(intent);							
						}else{
							
							Log.i("RESULT LSIGNUP:","sign up failed " );
						}

					}}	
				
				
				);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up, menu);
		return true;
	}

	 /**
	 *   An attemp to sign up the user 
	 */
	public boolean attempSignUp(){
		
		       //  clear des erreurs 
		       _pseudoEditText.setError(null);
		       _passwordEditText.setError(null);
		       _emailEditText.setError(null);
		       
				// sauvegarde des valeurs
		       _pseudo =_pseudoEditText.getText().toString();
		       _password = _passwordEditText.getText().toString();	   
		       _email=	_emailEditText.getText().toString();
		       
				boolean cancel = false;
				View focusView = null;

				// Check for a valid password.
				if (TextUtils.isEmpty(_password) ) {
					_passwordEditText.setError(getString(R.string.error_field_required));
					focusView = _passwordEditText;
					cancel = true;
				} else if (_password.length() < 4) {
				    _passwordEditText.setError(getString(R.string.error_invalid_password));
					focusView = _passwordEditText;
					cancel = true;
				}

				// Check for a valid email address.
				if (TextUtils.isEmpty(_pseudo)) {
					_pseudoEditText.setError(getString(R.string.error_field_required));
					focusView = _pseudoEditText;
					cancel = true;
				} 

				if (TextUtils.isEmpty(_email)) {
					_emailEditText.setError(getString(R.string.error_field_required));
					focusView = _emailEditText;
					cancel = true;
				} 
				
				if (cancel) {
					// There was an error; don't attempt login and focus the first
					// form field with an error.
					focusView.requestFocus();
					return false;
				} else {
					        
					     /* Toast.makeText(getBaseContext(),(String)" sign up : pseudo "+_pseudo+" email :"+
					      _email+" pass :"+_password, 
				                Toast.LENGTH_LONG).show();
					      */
							RemoteUserController usrcntrl = new RemoteUserController();
						    User u = new User();
						    u.setEmail(_email);
						    u.setPasswd(_password);
						    u.setPseudo(_pseudo);
						    
						    User signedUser = usrcntrl.signUp(u);
							if(signedUser==null) return false;
									else 
							return true;       
							
				    }
	}
	
}
