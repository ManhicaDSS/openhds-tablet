package org.openhds.mobile.database;

import org.openhds.mobile.OpenHDS;
import org.openhds.mobile.model.Individual;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class IndividualVisitedUpdate {
	private String event;
				
	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}		

	public void updateDatabase(ContentResolver resolver, Individual individual) {
        try {
            if (individual == null) {
                return;
            }

            Cursor cursor = resolver.query(OpenHDS.Individuals.CONTENT_ID_URI_BASE,
                    new String[] { OpenHDS.Individuals._ID, OpenHDS.Individuals.COLUMN_INDIVIDUAL_VISITED_FORMS }, OpenHDS.Individuals.COLUMN_INDIVIDUAL_EXTID + " = ?",
                    new String[] { individual.getExtId() }, null);
            if (cursor.moveToNext()) {
            	ContentValues cv = new ContentValues();
                cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_VISITED, "Yes");
                cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_VISITED_FORMS, Individual.addVisitedForms(cursor.getString(1), event));	
            	
                Uri uri = ContentUris.withAppendedId(OpenHDS.Individuals.CONTENT_ID_URI_BASE, cursor.getLong(0));
                resolver.update(uri, cv, null, null);
            }
            
            cursor.close();
        } catch (Exception e) {
            Log.e(IndividualVisitedUpdate.class.getName(), "Exception in IndividualVisitedUpdate");
        }
    }
	
	public void updateDatabase(ContentResolver resolver, String individualId) {
        try {
            if (individualId == null || individualId.isEmpty()) {
                return;
            }
            
            Cursor cursor = resolver.query(OpenHDS.Individuals.CONTENT_ID_URI_BASE,
                    new String[] { OpenHDS.Individuals._ID, OpenHDS.Individuals.COLUMN_INDIVIDUAL_VISITED_FORMS }, OpenHDS.Individuals.COLUMN_INDIVIDUAL_EXTID + " = ?",
                    new String[] { individualId }, null);
            if (cursor.moveToNext()) {
            	ContentValues cv = new ContentValues();
                cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_VISITED, "Yes");
                cv.put(OpenHDS.Individuals.COLUMN_INDIVIDUAL_VISITED_FORMS, Individual.addVisitedForms(cursor.getString(1), event));
            	
                Uri uri = ContentUris.withAppendedId(OpenHDS.Individuals.CONTENT_ID_URI_BASE, cursor.getLong(0));
                resolver.update(uri, cv, null, null);
            }
            
            cursor.close();
        } catch (Exception e) {
            Log.e(IndividualVisitedUpdate.class.getName(), "Exception in IndividualVisitedUpdate");
        }
    }

}