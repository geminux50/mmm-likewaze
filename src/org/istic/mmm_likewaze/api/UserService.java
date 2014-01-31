package org.istic.mmm_likewaze.api;

import org.istic.mmm_likewaze.model.User;

public interface UserService {
    
	/**
	 *   Sign up an existing user 
	 * @param pseudo  : her pseudo
	 * @param passwd  : her  password
	 * @return user signed up with all its information
	 * 
	 */
	public User login(String pseudo, String passwd);
     
	/**
      * Sign up a new user
      * @param usr  : and object representing the user
      * @return  the object representing the user signed up 
      * 
      */
	public User signUp(User usr);

}
