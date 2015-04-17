package org.openhds.mobile.model;

import org.openhds.mobile.OpenHDS;
import org.openhds.mobile.database.queries.Converter;
import org.openhds.mobile.database.queries.Queries;

import android.content.ContentResolver;
import android.database.Cursor;

/**
 * Fills in the event forms with pre-filled values based on the location visit
 * and possibly other entity types
 */
public class FormFiller {

    public FilledForm fillLocationForm(LocationVisit locationVisit) {
        FilledForm form = new FilledForm(UpdateEvent.LOCATION);

        addFieldWorker(locationVisit, form);

        form.setLocationId(locationVisit.getLocation().getExtId());
        form.setHierarchyId(locationVisit.getLatestLevelExtId());
        form.setLocationName(locationVisit.getLocation().getName());        
        return form;
    }

    private void addFieldWorker(LocationVisit locationVisit, FilledForm form) {
        form.setFieldWorkerId(locationVisit.getFieldWorker().getExtId());
    }

    public FilledForm fillVisitForm(LocationVisit locationVisit) {
        FilledForm form = new FilledForm(UpdateEvent.VISIT);

        addFieldWorker(locationVisit, form);
        addVisit(locationVisit, form);

        form.setLocationId(locationVisit.getLocation().getExtId());
        form.setRoundNumber(locationVisit.getRound().getRoundNumber());
        form.setLocationName(locationVisit.getLocation().getName());
        return form;
    }

    private void addVisit(LocationVisit locationVisit, FilledForm form) {
        form.setVisitDate(locationVisit.getVisit().getDate());
        form.setVisitExtId(locationVisit.getVisit().getExtId());
        form.setLocationName(locationVisit.getLocation().getName());
    }

    public FilledForm fillSocialGroupForm(LocationVisit locationVisit, SocialGroup sg) {
        FilledForm form = new FilledForm(UpdateEvent.SOCIALGROUP);

        addFieldWorker(locationVisit, form);
        addVisit(locationVisit, form);
        addHousehold(sg, form);

        form.setIndividualExtId(locationVisit.getSelectedIndividual().getExtId());
        form.setHouseholdName(locationVisit.getLocation().getName());

        return form;
    }

    private void addHousehold(SocialGroup sg, FilledForm form) {
        form.setHouseholdId(sg.getExtId());
        form.setHouseholdName(sg.getGroupName());
        form.setGroupHeadId(sg.getGroupHead());
    }

    public FilledForm fillMembershipForm(LocationVisit locationVisit) {
        FilledForm form = new FilledForm(UpdateEvent.MEMBERSHIP);

        addFieldWorker(locationVisit, form);

        form.setIndividualExtId(locationVisit.getSelectedIndividual().getExtId());
        form.setIndividualFirstName(locationVisit.getSelectedIndividual().getFirstName());
        form.setIndividualLastName(locationVisit.getSelectedIndividual().getLastName());

        return form;
    }

    public FilledForm appendSocialGroup(SocialGroup sg, FilledForm form) {
        addHousehold(sg, form);

        return form;
    }

    public FilledForm fillRelationships(LocationVisit locationVisit) {
        FilledForm form = new FilledForm(UpdateEvent.RELATIONSHIP);

        addFieldWorker(locationVisit, form);
        Individual indiv = locationVisit.getSelectedIndividual();
        form.setIndividualA(indiv.getExtId());

        return form;
    }

    public FilledForm appendIndividual(Individual individual, FilledForm form) {
        addIndividual(individual, form);

        return form;
    }

    private void addIndividual(Individual individual, FilledForm form) {
        form.setIndividualDob(individual.getDob());
        form.setIndividualExtId(individual.getExtId());
        form.setIndividualFirstName(individual.getFirstName());
        form.setIndividualGender(individual.getGender());
        form.setIndividualLastName(individual.getLastName());
    }

    public FilledForm fillOutMigrationForm(LocationVisit locationVisit) {
        FilledForm form = new FilledForm(UpdateEvent.OUTMIGRATION);

        addFieldWorker(locationVisit, form);
        addVisit(locationVisit, form);
        addIndividual(locationVisit.getSelectedIndividual(), form);

        return form;
    }

    public FilledForm fillPregnancyRegistrationForm(LocationVisit locationVisit) {
        FilledForm form = new FilledForm(UpdateEvent.PREGNANCYOBSERVATION);

        addFieldWorker(locationVisit, form);
        addVisit(locationVisit, form);
        addIndividual(locationVisit.getSelectedIndividual(), form);

        return form;
    }

    public FilledForm fillDeathForm(LocationVisit locationVisit, SocialGroup sg) {
    	FilledForm form = null;
    	//Check if selected individual is HoH. If yes, fill in DeathToHoH form, otherwise normal death form
    	if (locationVisit.getSelectedIndividual().getExtId().equalsIgnoreCase(locationVisit.getLocation().getHead())) {
    		form = new FilledForm(UpdateEvent.DEATHTOHOH);
    		//Set Successor extId to current HoH extId. 
    		//In the case where the current user is the HoH and the only member of said HH, this value won't change,
    		//otherwise this value will be overwritten if another member in the HH is selected as the new HH.
    		form.setIndividualA(locationVisit.getSelectedIndividual().getExtId());
    		if (sg !=null)
    			addHousehold(sg, form);
        } else {
        	form = new FilledForm(UpdateEvent.DEATH);
        }
        addFieldWorker(locationVisit, form);
        addVisit(locationVisit, form);
        addIndividual(locationVisit.getSelectedIndividual(), form);

        return form;
    }
    
    public FilledForm fillDeathOfHouseholdForm(LocationVisit locationVisit, SocialGroup sg){
    	FilledForm form = null;
    	form = new FilledForm(UpdateEvent.DEATHTOHOH);
    	
    	if(sg != null){
    		addHousehold(sg, form);
    	}
    	addFieldWorker(locationVisit, form);
        addVisit(locationVisit, form);
        addIndividual(locationVisit.getSelectedIndividual(), form);
        
//        List<Individual> people = new ArrayList<Individual>();
//        Individual p1 = new Individual();
//        p1.setExtId("id1");
//        p1.setFirstName("fn1");
//        p1.setLastName("ln1");
//        
//        Individual p2 = new Individual();
//        p2.setExtId("id2"); 
//        p2.setFirstName("fn2");
//        p2.setLastName("ln2");        
//        
//        people.add(p1);
//        people.add(p2);
//        
//        form.setHouseHoldMembers(people);
//        for(Individual p : people)
//        	form.addHouseHoldMember(p);
        
//        form.setNboutcomes(people.size());
        
//        form.setIndividualA("NEWHOHID");
        
    	return form;
    }

    public FilledForm fillInMigrationForm(LocationVisit locationVisit, Individual individual) {
        FilledForm form = new FilledForm(UpdateEvent.INMIGRATION);
        addFieldWorker(locationVisit, form);
        addVisit(locationVisit, form);
        form.setLocationId(locationVisit.getLocation().getExtId());
        if (individual != null) {
            addIndividual(individual, form);
        }

        return form;
    }

    public FilledForm fillPregnancyOutcome(LocationVisit locationVisit, PregnancyOutcome po) {
        FilledForm form = new FilledForm(UpdateEvent.PREGNANCYOUTCOME);
        int nb=0;
        addFieldWorker(locationVisit, form);
        addVisit(locationVisit, form);
        form.setLocationId(locationVisit.getLocation().getExtId());
        form.setMotherExtId(locationVisit.getSelectedIndividual().getExtId());
        form.setMotherPermId(locationVisit.getSelectedIndividual().getLastName());
        form.setMotherName(locationVisit.getSelectedIndividual().getFirstName());
        
        for(String childId : po.getChildIds()) {
            Child child = new Child();
            child.setId(childId);            
            child.setPermId(po.getChildPermIds().get(nb));
            form.addChild(child);
            nb++;
        }
        form.setNboutcomes(nb);
        
        return form;
    }

    public FilledForm fillInternalInMigrationForm(LocationVisit locationVisit, Individual individual) {
        FilledForm form = new FilledForm(UpdateEvent.INMIGRATION);

        addFieldWorker(locationVisit, form);
        addVisit(locationVisit, form);

        if (individual != null) {
            addIndividual(individual, form);
            form.setOrigin(individual.getCurrentResidence());
        }

        form.setMigrationType("INTERNAL_INMIGRATION");
        form.setLocationId(locationVisit.getLocation().getExtId());
        

        return form;
    }

    public FilledForm fillExternalInmigration(LocationVisit locationVisit, String id) {
        FilledForm form = new FilledForm(UpdateEvent.INMIGRATION);

        addFieldWorker(locationVisit, form);
        addVisit(locationVisit, form);

        form.setIndividualExtId(id);
        form.setMigrationType("INTERNAL_INMIGRATION");
        form.setLocationId(locationVisit.getLocation().getExtId());
        form.setMigrationType("EXTERNAL_INMIGRATION");
        
        return form;
    }
    
    public FilledForm fillBaseline(LocationVisit locationVisit, String id) {
        FilledForm form = new FilledForm(UpdateEvent.BASELINE);

        addFieldWorker(locationVisit, form);
        addVisit(locationVisit, form);

        form.setIndividualExtId(id);
//        form.setMigrationType("INTERNAL_INMIGRATION");
        form.setLocationId(locationVisit.getLocation().getExtId());
        form.setMigrationType("BASELINE");
        
        return form;
    }
    
    public FilledForm fillExtraForm(LocationVisit locationVisit, String formName, SocialGroup sg) {
    	  FilledForm form = new FilledForm(formName);
          
          addFieldWorker(locationVisit, form);
          addVisit(locationVisit, form);
          addIndividual(locationVisit.getSelectedIndividual(), form);
          form.setLocationId(locationVisit.getLocation().getExtId());
          form.setRoundNumber(locationVisit.getRound().getRoundNumber());          
          form.setMotherExtId(locationVisit.getSelectedIndividual().getMother());
          form.setHierarchyId(locationVisit.getLatestLevelExtId());
          if (sg !=null)
  			addHousehold(sg, form);

          return form;
    }
        
    public void appendFatherId(FilledForm filledForm, String fatherId) {
        filledForm.setFatherExtId(fatherId);
    }
    
    public void addGroupHead(FilledForm filledForm, ContentResolver resolver, SocialGroup sg){
    	if (sg != null){
    		Cursor cursor = Queries.getIndividualByExtId(resolver, sg.getGroupHead());
        	if (cursor.moveToFirst()) {
        		Individual indiv = Converter.convertToIndividual(cursor);
        		filledForm.setGroupHeadId(indiv.getExtId());
        		filledForm.setGroupHeadPermId(indiv.getLastName());
        		filledForm.setGroupHeadName(indiv.getFirstName());
        		filledForm.setGroupHeadDob(indiv.getDob());
        		filledForm.setGroupHeadGender(indiv.getGender());
        	}
    	}
    }
    
    public void addParents(FilledForm filledForm, ContentResolver resolver, String individualExtId){

		Cursor cursorIndv = Queries.getIndividualByExtId(resolver, individualExtId);

		if (cursorIndv.moveToFirst()) {
			Individual indiv = Converter.convertToIndividual(cursorIndv);
			Individual father = null;
			Individual mother = null;

			cursorIndv.close();

			if (!indiv.getFather().equalsIgnoreCase("UNK")) {
				cursorIndv = Queries.getIndividualByExtId(resolver, indiv.getFather());
				if (cursorIndv.moveToFirst()) {
					father = Converter.convertToIndividual(cursorIndv);
					cursorIndv.close();
				}
			}

			if (!indiv.getMother().equalsIgnoreCase("UNK")) {
				cursorIndv = Queries.getIndividualByExtId(resolver, indiv.getMother());
				if (cursorIndv.moveToFirst()) {
					mother = Converter.convertToIndividual(cursorIndv);
					cursorIndv.close();
				}
			}

			filledForm.setFatherExtId(father == null ? "UNK" : father.getExtId());
			filledForm.setFatherPermId(father == null ? "UNK" : father.getLastName());
			filledForm.setFatherName(father == null ? "UNK" : father.getFirstName());

			filledForm.setMotherExtId(mother == null ? "UNK" : mother.getExtId());
			filledForm.setMotherPermId(mother == null ? "UNK" : mother.getLastName());
			filledForm.setMotherName(mother == null ? "UNK" : mother.getFirstName());

		}
	
    }
    
    public void addOriginHouseNumber(FilledForm filledForm, ContentResolver resolver){

		Cursor cursorIndv = Queries.getLocationByExtId(resolver, filledForm.getOrigin());

		if (cursorIndv.moveToFirst()) {
			Location location = Converter.convertToLocation(cursorIndv);			
			filledForm.setOriginHouseNo(location.getName());			
			cursorIndv.close();
		}
	
    }

    /*
     * Review correctly the relationships
     */
	public void addSpouse(FilledForm filledForm, ContentResolver resolver, String individualExtId) {
		Cursor cursorRelat = Queries.getRelationshipByIndividualA(resolver, individualExtId);
		
		if (cursorRelat.moveToFirst()) {
			String spouseExtId = cursorRelat.getString(cursorRelat.getColumnIndex(OpenHDS.Relationships.COLUMN_RELATIONSHIP_INDIVIDUAL_B));
			cursorRelat.close();
			
			Cursor cursorIndv = Queries.getIndividualByExtId(resolver, spouseExtId);
			
			if (cursorIndv.moveToFirst()){
				Individual spouse = Converter.convertToIndividual(cursorIndv);
				
	            filledForm.setSpouseName(spouse.getFirstName());
	            filledForm.setSpousePermId(spouse.getLastName());				
			}			
            
		}
		
	}
}
