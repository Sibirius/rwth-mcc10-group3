package com.rwthmcc103.mboard;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import com.google.appengine.api.users.User;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Profile {
	//TODO: makes unique for each user

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;	
	
    @Persistent
    private User user;
	
    @Persistent
    private Blob img;
	
	//TODO: picture field and get/set (serializable object?)

    public Profile(User user, Blob img) {
        this.user = user;
        this.img = img;
    }

    public Key getKey() {
        return key;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }

    public Blob getImg() {
        return img;
    }
    
    public void setImg(Blob img) {
        this.img = img;
    }

}
