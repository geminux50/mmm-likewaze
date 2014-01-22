package com.istic.mmm_likewaze.api;

import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

import com.istic.mmm_likewaze.model.User;

public interface UserService {

	public User login(String pseudo, String passwd);
	public void    signUp(User usr);
	
}
