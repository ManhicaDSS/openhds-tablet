package org.openhds.mobile.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A filled form represents an ODK form that has been prefilled with values from
 * the OpenHDS application.
 */
public class FilledForm {
    private String formName;

    private String visitExtId;
    private String visitDate;
    private String roundNumber;
    private String locationId;
    private String houseName;
    private String fieldWorkerId;


    private String householdId;
    private String householdName;
    private String socialGroupType;

    private String individualExtId;
    private String motherExtId;
    private String fatherExtId;
    private String individualFirstName;
    private String individualLastName;
    private String individualMiddleName;
    private String individualGender;
    private String individualDob;
    private String hierarchyId;
    private String latlong;

    private String individualA;
    private String individualB;
    private String intervieweeId;
    private String origin;
    private String originHouseNo;
    private String migrationType;
    private int nboutcomes;
    
    private List<Child> children = new ArrayList<Child>();
    private List<Individual> hhMembers = new ArrayList<Individual>();

    /*New var from manhica dss*/
    private String locationName;
    private String groupHeadId;
    private String groupHeadPermId;
    private String groupHeadDob;
    private String groupHeadName;
    private String groupHeadGender;
    
    private String motherPermId;
    private String fatherPermId;
    private String motherName;
    private String fatherName;
    
    private String hasSubsHead;
    private String subsHeadName;
    private String subsHeadPermId;
    
    private String spousePermId;
    private String spouseName;
    
    /*Extra Variables Variables*/
    private Map<String, String> extraParams = new LinkedHashMap<String, String>();
    
    public FilledForm(String formName) {
        this.formName = formName;
    }

    public String getFormName() {
        return formName;
    }

    public String getVisitExtId() {
        return visitExtId;
    }

    public void setVisitExtId(String visitExtId) {
        this.visitExtId = visitExtId;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public String getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(String roundNumber) {
        this.roundNumber = roundNumber;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getFieldWorkerId() {
        return fieldWorkerId;
    }

    public void setFieldWorkerId(String fieldWorkerId) {
        this.fieldWorkerId = fieldWorkerId;
    }

    public String getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(String householdId) {
        this.householdId = householdId;
    }

    public String getHouseholdName() {
        return householdName;
    }

    public void setHouseholdName(String householdName) {
        this.householdName = householdName;
    }

    public String getIndividualExtId() {
        return individualExtId;
    }

    public void setIndividualExtId(String individualExtId) {
        this.individualExtId = individualExtId;
    }

    public String getIntervieweeId() {
		return intervieweeId;
	}

	public void setIntervieweeId(String intervieweeId) {
		this.intervieweeId = intervieweeId;
	}

	public String getMotherExtId() {
        return motherExtId;
    }

    public void setMotherExtId(String motherExtId) {
        this.motherExtId = motherExtId;
    }

    public String getFatherExtId() {
        return fatherExtId;
    }

    public void setFatherExtId(String fatherExtId) {
        this.fatherExtId = fatherExtId;
    }

    public String getIndividualFirstName() {
        return individualFirstName;
    }

    public void setIndividualFirstName(String individualFirstName) {
        this.individualFirstName = individualFirstName;
    }

    public String getIndividualLastName() {
        return individualLastName;
    }

    public void setIndividualLastName(String individualLastName) {
        this.individualLastName = individualLastName;
    }

    public String getIndividualGender() {
        return individualGender;
    }

    public void setIndividualGender(String individualGender) {
        this.individualGender = individualGender;
    }

    public String getIndividualDob() {
        return individualDob;
    }

    public void setIndividualDob(String individualDob) {
        this.individualDob = individualDob;
    }

    public String getHierarchyId() {
        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    public String getLatlong() {
        return latlong;
    }

    public void setLatlong(String latlong) {
        this.latlong = latlong;
    }

    public void setIndividualA(String extId) {
        this.individualA = extId;
    }

    public String getIndividualB() {
        return individualB;
    }

    public void setIndividualB(String individualB) {
        this.individualB = individualB;
    }

    public String getIndividualA() {
        return individualA;
    }

    public void setMigrationType(String string) {
        this.migrationType = string;
    }

    public String getMigrationType() {
        return this.migrationType;
    }

    public List<Child> getPregOutcomeChildren() {
        return children;
    }
    
    public void addChild(Child child) {
        children.add(child);
    }
    
    public void addHouseHoldMember(Individual member) {
        hhMembers.add(member);
    }    
    
    public List<Individual> getHouseHoldMembers(){
    	return hhMembers;
    }
    
    public void setHouseHoldMembers(List<Individual> members){
    	this.hhMembers = members;
    }    

	public String getIndividualMiddleName() {
		return individualMiddleName;
	}

	public void setIndividualMiddleName(String individualMiddleName) {
		this.individualMiddleName = individualMiddleName;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
	public String getOriginHouseNo() {
		return originHouseNo;
	}
	
	public void setOriginHouseNo(String originHouseNo) {
		this.originHouseNo = originHouseNo;
	}

	public int getNboutcomes() {
		return nboutcomes;
	}

	public void setNboutcomes(int nboutcomes) {
		this.nboutcomes = nboutcomes;
	}

	public String getSocialGroupType() {
		return socialGroupType;
	}

	public void setSocialGroupType(String socialGroupType) {
		this.socialGroupType = socialGroupType;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getGroupHeadId() {
		return groupHeadId;
	}

	public void setGroupHeadId(String groupHeadId) {
		this.groupHeadId = groupHeadId;
	}

	public String getGroupHeadPermId() {
		return groupHeadPermId;
	}

	public void setGroupHeadPermId(String groupHeadPermId) {
		this.groupHeadPermId = groupHeadPermId;
	}

	public String getGroupHeadDob() {
		return groupHeadDob;
	}

	public void setGroupHeadDob(String groupHeadDob) {
		this.groupHeadDob = groupHeadDob;
	}

	public String getGroupHeadName() {
		return groupHeadName;
	}

	public void setGroupHeadName(String groupHeadName) {
		this.groupHeadName = groupHeadName;
	}

	public String getGroupHeadGender() {
		return groupHeadGender;
	}

	public void setGroupHeadGender(String groupHeadGender) {
		this.groupHeadGender = groupHeadGender;
	}

	public String getMotherPermId() {
		return motherPermId;
	}

	public void setMotherPermId(String motherPermId) {
		this.motherPermId = motherPermId;
	}

	public String getFatherPermId() {
		return fatherPermId;
	}

	public void setFatherPermId(String fatherPermId) {
		this.fatherPermId = fatherPermId;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getHasSubsHead() {
		return hasSubsHead;
	}

	public void setHasSubsHead(String hasSubsHead) {
		this.hasSubsHead = hasSubsHead;
	}

	public String getSubsHeadName() {
		return subsHeadName;
	}

	public void setSubsHeadName(String subsHeadName) {
		this.subsHeadName = subsHeadName;
	}

	public String getSubsHeadPermId() {
		return subsHeadPermId;
	}

	public void setSubsHeadPermId(String subsHeadPermId) {
		this.subsHeadPermId = subsHeadPermId;
	}

	public String getSpousePermId() {
		return spousePermId;
	}

	public void setSpousePermId(String spousePermId) {
		this.spousePermId = spousePermId;
	}

	public String getSpouseName() {
		return spouseName;
	}

	public void setSpouseName(String spouseName) {
		this.spouseName = spouseName;
	}
	
	public void addExtraParam(String key, String value){
		this.extraParams.put(key, value);
	}
	
	public Map<String,String> getExtraParams(){
		return this.extraParams;
	}
		
}
