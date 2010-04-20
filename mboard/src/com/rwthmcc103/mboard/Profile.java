package com.rwthmcc103.mboard;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import com.google.appengine.api.users.User;

@PersistenceCapable
public class Profile {
	//TODO: makes unique?
	
	@PrimaryKey
    @Persistent
    private User user;
	//TODO: picture field and get/set (serializable object?)
	
    public Profile(User user) {
        this.user = user;
    }    

    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
}
