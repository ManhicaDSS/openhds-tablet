package org.openhds.mobile.model;

public class Child {

    private String id;
    private String socialGroupId;
    private String permId;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSocialGroupId() {
        return socialGroupId;
    }

    public void setSocialGroupId(String socialGroupId) {
        this.socialGroupId = socialGroupId;
    }
    
    public String getPermId() {
		return permId;
	}
    
    public void setPermId(String permId) {
		this.permId = permId;
	}
}
