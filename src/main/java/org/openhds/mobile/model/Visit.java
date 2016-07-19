package org.openhds.mobile.model;

import java.io.Serializable;

public class Visit implements Serializable {
	
	private static final long serialVersionUID = -1429712555458116315L;
	private String extId;
	private String location;
	private String date;
	private String round;
	private String intervieweeId;
	private String completedQuest; /*Is it possible to complete the visit*/
	
	public String getIntervieweeId() {
		return intervieweeId;
	}

	public void setIntervieweeId(String intervieweeId) {
		this.intervieweeId = intervieweeId;
	}

	public String getExtId() {
		return extId;
	}
	
	public void setExtId(String extId) {
		this.extId = extId;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getRound() {
		return round;
	}
	
	public void setRound(String round) {
		this.round = round;
	}

	public String getCompletedQuest() {
		return completedQuest;
	}

	public void setCompletedQuest(String completedQuest) {
		this.completedQuest = completedQuest;
	}	
	
}
