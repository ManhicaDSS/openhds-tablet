package org.openhds.mobile.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.openhds.mobile.OpenHDS;
import org.openhds.mobile.database.queries.Converter;
import org.openhds.mobile.model.FormXmlReader;
import org.openhds.mobile.model.Individual;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.util.Log;

public class IndividualDetailsUpdate implements Updatable {

	@Override
	public void updateDatabase(ContentResolver resolver, String filepath, String jrFormId) {
		FormXmlReader xmlReader = new FormXmlReader();
        try {
            Individual individual = xmlReader.readIndividualDetails(new FileInputStream(new File(filepath)), jrFormId);
            Log.d("running", ""+individual+", "+jrFormId);
            if (individual == null) {
                return;
            }
            
            individual.setVisitedForms("1");
            
            ContentValues cv = new ContentValues();
            cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_DOB, individual.getDob());
            cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_EXTID, individual.getExtId());
            cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_FATHER, individual.getFather());
            cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_FIRSTNAME, individual.getFirstName());
            cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_GENDER, individual.getGender());
            cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_LASTNAME, individual.getLastName());
            cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_MOTHER, individual.getMother());
            cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_VISITED, "Yes");
            cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_VISITED_FORMS, individual.getVisitedForms());
            //cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_RESIDENCE, individual.getCurrentResidence());
            //cv.put(OpenHDS.Individuals.COLUMN_RESIDENCE_END_TYPE, "NA");
            
            resolver.update(OpenHDS.Individuals.CONTENT_ID_URI_BASE, cv, OpenHDS.Individuals.COLUMN_INDIVIDUAL_EXTID+"=?", new String[]{ individual.getExtId()} );
        } catch (FileNotFoundException e) {
            Log.e(BaselineUpdate.class.getName(), "Could not read Individuals Details file");
        }
	}

}
