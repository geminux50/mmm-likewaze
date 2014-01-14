package com.istic.mmm_likewaze.api;

import java.io.OutputStream;

import com.istic.mmm_likewaze.model.User;

public interface UserAPI {

	public boolean login(String pseudo, String passwd);
	public void    signUp(User usr, OutputStream USER_PATH);
	
}
