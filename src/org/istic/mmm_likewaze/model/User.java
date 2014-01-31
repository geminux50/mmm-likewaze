package  org.istic.mmm_likewaze.model;
import java.io.Serializable;

/**
 *   Entity Client 
 *  
 * */

public class User implements Serializable  {
   
   private static final long serialVersionUID = 1L;
   private Long userId; 	
   private String pseudo;
   private String email;
   private String passwd;
  
   
			public Long getUserId() {
				return userId;
			}
			public void setUserId(Long userId) {
				this.userId = userId;
			}
			public String getPseudo() {
				return pseudo;
			}
			public void setPseudo(String pseudo) {
				this.pseudo = pseudo;
			}
			public String getEmail() {
				return email;
			}
			public void setEmail(String email) {
				this.email = email;
			}
			public String getPasswd() {
				return passwd;
			}
			public void setPasswd(String passwd) {
				this.passwd = passwd;
			}
			  
}