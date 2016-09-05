package org.openhds.mobile.dss.database;

import java.util.Collection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class Database {
	
	public static final String DATABASE_NAME = "dss.db";
	public static final int DATABASE_VERSION = 1;

	
	public static final class ImunizationTable implements BaseColumns {
		public static final String TABLE_NAME = "imunization";
		
		public static final String COLUMN_ID = "id";
		public static final String COLUMN_HOUSE_NUMBER = "houseNumber";
        public static final String COLUMN_INDIVIDUAL_ID = "individualId";
        public static final String COLUMN_PERM_ID = "permId";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_DOB = "dob";
        public static final String COLUMN_HAS_ALL_VACS = "hasAllVacs";
        public static final String COLUMN_VACS_ON_DATABASE = "vacsOnDatabase";
        public static final String COLUMN_VAC_BCG = "vacBcg";
        public static final String COLUMN_VAC_POLIO_DOSE_0 = "vacPolioDose0";
        public static final String COLUMN_VAC_POLIO_DOSE_1 = "vacPolioDose1";
        public static final String COLUMN_VAC_POLIO_DOSE_2 = "vacPolioDose2";
        public static final String COLUMN_VAC_POLIO_DOSE_3 = "vacPolioDose3";
        public static final String COLUMN_VAC_DPT_DOSE_1 = "vacDptDose1";
        public static final String COLUMN_VAC_DPT_DOSE_2 = "vacDptDose2";
        public static final String COLUMN_VAC_DPT_DOSE_3 = "vacDptDose3";
        public static final String COLUMN_VAC_PCV_10_DOSE_1 = "vacPcv10Dose1";
        public static final String COLUMN_VAC_PCV_10_DOSE_2 = "vacPcv10Dose2";
        public static final String COLUMN_VAC_PCV_10_DOSE_3 = "vacPcv10Dose3";
        public static final String COLUMN_VAC_SARAMPO = "vacSarampo";
        public static final String COLUMN_VAC_ROTAVIRUS_DOSE_1 = "vacRotavirusDose1";
        public static final String COLUMN_VAC_ROTAVIRUS_DOSE_2 = "vacRotavirusDose2";
        public static final String COLUMN_VAC_ROTAVIRUS_DOSE_3 = "vacRotavirusDose3";
        public static final String COLUMN_VAC_VITAMINA_A_DOSE_1 = "vacVitaminaADose1";
        public static final String COLUMN_VAC_VITAMINA_A_DOSE_2 = "vacVitaminaADose2";
        public static final String COLUMN_VAC_VITAMINA_A_DOSE_3 = "vacVitaminaADose3";
        public static final String COLUMN_VAC_VITAMINA_A_DOSE_4 = "vacVitaminaADose4";
        public static final String COLUMN_VAC_VITAMINA_A_DOSE_5 = "vacVitaminaADose5";
        public static final String COLUMN_VAC_VITAMINA_A_DOSE_6 = "vacVitaminaADose6";
        public static final String COLUMN_VAC_VITAMINA_A_DOSE_7 = "vacVitaminaADose7";
        public static final String COLUMN_VAC_VITAMINA_A_DOSE_8 = "vacVitaminaADose8";
        public static final String COLUMN_VAC_VITAMINA_A_DOSE_9 = "vacVitaminaADose9";
        public static final String COLUMN_VAC_VITAMINA_A_DOSE_10 = "vacVitaminaADose10";
        public static final String COLUMN_VAC_VITAMINA_A_TOTAL = "vacVitaminaATotal";
        public static final String COLUMN_VAC_MEBENDAZOL_DOSE_1 = "vacMebendazolDose1";
        public static final String COLUMN_VAC_MEBENDAZOL_DOSE_2 = "vacMebendazolDose2";
        public static final String COLUMN_VAC_MEBENDAZOL_DOSE_3 = "vacMebendazolDose3";
        public static final String COLUMN_VAC_MEBENDAZOL_DOSE_4 = "vacMebendazolDose4";
        public static final String COLUMN_VAC_MEBENDAZOL_DOSE_5 = "vacMebendazolDose5";
        public static final String COLUMN_VAC_MEBENDAZOL_DOSE_6 = "vacMebendazolDose6";
        public static final String COLUMN_VAC_MEBENDAZOL_DOSE_7 = "vacMebendazolDose7";
        public static final String COLUMN_VAC_MEBENDAZOL_DOSE_8 = "vacMebendazolDose8";
        public static final String COLUMN_VAC_MEBENDAZOL_DOSE_9 = "vacMebendazolDose9";
        public static final String COLUMN_VAC_MEBENDAZOL_DOSE_10 = "vacMebendazolDose10";
        public static final String COLUMN_VAC_MEBENDAZOL_TOTAL = "vacMebendazolTotal";
        public static final String COLUMN_VAC_OTHERS_1 = "vacOthers1";
        public static final String COLUMN_VAC_OTHERS_2 = "vacOthers2";
        public static final String COLUMN_VAC_OTHERS_3 = "vacOthers3";
        public static final String COLUMN_VAC_OTHERS_4 = "vacOthers4";
        public static final String COLUMN_VAC_OTHERS_5 = "vacOthers5";
        public static final String COLUMN_VAC_OTHERS_6 = "vacOthers6";
        public static final String COLUMN_VAC_OTHERS_7 = "vacOthers7";
        public static final String COLUMN_VAC_OTHERS_8 = "vacOthers8";
        public static final String COLUMN_VAC_OTHERS_9 = "vacOthers9";
        public static final String COLUMN_VAC_OTHERS_10 = "vacOthers10";
        public static final String COLUMN_VAC_OTHERS_TOTAL = "vacOthersTotal";
        public static final String COLUMN_CONTENT_URI = "lastContentUri";
		
		public static final String[] ALL_COLUMNS = {COLUMN_ID, COLUMN_HOUSE_NUMBER, COLUMN_INDIVIDUAL_ID, COLUMN_PERM_ID,
				COLUMN_NAME, COLUMN_GENDER, COLUMN_DOB, COLUMN_HAS_ALL_VACS, COLUMN_VACS_ON_DATABASE, COLUMN_VAC_BCG,
				COLUMN_VAC_POLIO_DOSE_0, COLUMN_VAC_POLIO_DOSE_1, COLUMN_VAC_POLIO_DOSE_2, COLUMN_VAC_POLIO_DOSE_3,
				COLUMN_VAC_DPT_DOSE_1, COLUMN_VAC_DPT_DOSE_2, COLUMN_VAC_DPT_DOSE_3, COLUMN_VAC_PCV_10_DOSE_1, 
				COLUMN_VAC_PCV_10_DOSE_2, COLUMN_VAC_PCV_10_DOSE_3, COLUMN_VAC_SARAMPO, COLUMN_VAC_ROTAVIRUS_DOSE_1, 
				COLUMN_VAC_ROTAVIRUS_DOSE_2, COLUMN_VAC_ROTAVIRUS_DOSE_3, COLUMN_VAC_VITAMINA_A_DOSE_1, 
				COLUMN_VAC_VITAMINA_A_DOSE_2, COLUMN_VAC_VITAMINA_A_DOSE_3, COLUMN_VAC_VITAMINA_A_DOSE_4, 
				COLUMN_VAC_VITAMINA_A_DOSE_5, COLUMN_VAC_VITAMINA_A_DOSE_6, COLUMN_VAC_VITAMINA_A_DOSE_7, 
				COLUMN_VAC_VITAMINA_A_DOSE_8, COLUMN_VAC_VITAMINA_A_DOSE_9, COLUMN_VAC_VITAMINA_A_DOSE_10, 
				COLUMN_VAC_VITAMINA_A_TOTAL, COLUMN_VAC_MEBENDAZOL_DOSE_1, COLUMN_VAC_MEBENDAZOL_DOSE_2, 
				COLUMN_VAC_MEBENDAZOL_DOSE_3, COLUMN_VAC_MEBENDAZOL_DOSE_4, COLUMN_VAC_MEBENDAZOL_DOSE_5, 
				COLUMN_VAC_MEBENDAZOL_DOSE_6, COLUMN_VAC_MEBENDAZOL_DOSE_7, COLUMN_VAC_MEBENDAZOL_DOSE_8, 
				COLUMN_VAC_MEBENDAZOL_DOSE_9, COLUMN_VAC_MEBENDAZOL_DOSE_10, COLUMN_VAC_MEBENDAZOL_TOTAL, 
				COLUMN_VAC_OTHERS_1, COLUMN_VAC_OTHERS_2, COLUMN_VAC_OTHERS_3, COLUMN_VAC_OTHERS_4, COLUMN_VAC_OTHERS_5,
				COLUMN_VAC_OTHERS_6, COLUMN_VAC_OTHERS_7, COLUMN_VAC_OTHERS_8, COLUMN_VAC_OTHERS_9, COLUMN_VAC_OTHERS_10,
				COLUMN_VAC_OTHERS_TOTAL, COLUMN_CONTENT_URI};
		}
	
	private DatabaseHelper dbHelper;
	private SQLiteDatabase database;
	
	public Database(Context context) {
		dbHelper = new DatabaseHelper(context);
	}
	
	public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	}
	
	public void beginTransaction(){
		database.beginTransaction();
	}

	public void endTransaction(){
		database.endTransaction();
	}

	public void setTransactionSuccessful(){
		database.setTransactionSuccessful();
	}

    public void close() {
	    dbHelper.close();
	}
    
    public long insert(Table entity){  	    	
    	long insertId = -1;
    	
    	insertId = database.insert(entity.getTableName(), null,  entity.getContentValues());
    	
    	return insertId;
    }
    
    public long insert(Collection<? extends Table> entities){  	    	
    	long insertId = -1;
    	
    	for (Table entity : entities){
    		insertId = database.insert(entity.getTableName(), null,  entity.getContentValues());
    	}
    	
    	return insertId;
    }
    
    public int delete(Class<? extends Table> table, String whereClause, String[] whereArgs){
    	Table entity = newInstance(table);
    	
    	int deleteRows = database.delete(entity.getTableName(), whereClause, whereArgs);
    	return deleteRows;
    }
    
    public int update(Class<? extends Table> table, ContentValues values, String whereClause, String[] whereArgs){    	
    	Table entity = newInstance(table);
    	
    	int rows = database.update(entity.getTableName(), values, whereClause, whereArgs);
    	
    	return rows;
    }
    
    /*
    public int update(Table entity){    	
    	    	
    	long rows = database.update(entity.getTableName(), entity.getContentValues(), BaseColumns._ID + " = ?", new String{entity.get});
    	
    	return 0;
    }
    */
    
    public Cursor query(Class<? extends Table> table, String selection, String[] selectionArgs, String groupBy, String having, String orderBy){    	
    	Table entity = newInstance(table);
    	
    	Cursor cursor = database.query(entity.getTableName(), entity.getColumnNames(), selection, selectionArgs, groupBy, having, orderBy);
        	
    	return cursor;
    }
    
    public Cursor query(Class<? extends Table> table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy){
    	Table entity = newInstance(table);
    	
    	Cursor cursor = database.query(entity.getTableName(), columns, selection, selectionArgs, groupBy, having, orderBy);
        	
    	return cursor;
    }
    
    private Table newInstance(Class<? extends Table> entity){
    	try {
			Table obj =  entity.newInstance();
			return obj;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return null;
    }
    
    
}
