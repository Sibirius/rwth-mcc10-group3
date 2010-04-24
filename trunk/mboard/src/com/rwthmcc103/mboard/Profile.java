package com.rwthmcc103.mboard;

import java.util.List;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import com.google.appengine.api.users.User;
import javax.jdo.PersistenceManager;

import com.google.appengine.api.blobstore.BlobKey;
//import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Profile {
	//TODO: makes unique for each user
	//TODO: use nickname as the primary key!? 

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private String nickname;  	
	
    @Persistent
    private User user;
    
    @Persistent
    private BlobKey img;
	
	//TODO: picture field and get/set (serializable object?)

    public Profile(User user, BlobKey img) {
        this.user = user;
        this.nickname = user.getNickname();
        this.img = img;
    }
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNickname() {
        return nickname;
    }   
    
    public BlobKey getImg() {
        return img;
    }
    
    public void setImg(BlobKey img) {
        this.img = img;
    }
    
    public static Profile getProfile(User user){
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Profile result = null;
        String query = "select from " + Profile.class.getName() + " where nickname == '" + user.getNickname() + "' range 0,1";    
        if( !(( List<Profile> ) pm.newQuery(query).execute()).isEmpty() ){
        	result = pm.getObjectById(Profile.class,user.getNickname());
        }
        return result;   	
    }

}
