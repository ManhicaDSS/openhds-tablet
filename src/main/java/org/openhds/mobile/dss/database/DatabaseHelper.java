package org.openhds.mobile.dss.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

		
	public DatabaseHelper(Context context) {
		super(context, Database.DATABASE_NAME, null, Database.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_IMUNIZATION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	

	
	public static final String CREATE_TABLE_IMUNIZATION = " "
	 		+ "CREATE TABLE " + Database.ImunizationTable.TABLE_NAME + "(" 
			 + Database.ImunizationTable.COLUMN_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			 + Database.ImunizationTable.COLUMN_HOUSE_NUMBER + " TEXT,"
			 + Database.ImunizationTable.COLUMN_INDIVIDUAL_ID + " TEXT,"
			 + Database.ImunizationTable.COLUMN_PERM_ID + " TEXT,"
			 + Database.ImunizationTable.COLUMN_NAME + " TEXT,"
			 + Database.ImunizationTable.COLUMN_GENDER + " TEXT,"
			 + Database.ImunizationTable.COLUMN_DOB + " TEXT,"
			 + Database.ImunizationTable.COLUMN_HAS_ALL_VACS + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VACS_ON_DATABASE + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_BCG + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_POLIO_DOSE_0 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_POLIO_DOSE_1 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_POLIO_DOSE_2 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_POLIO_DOSE_3 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_DPT_DOSE_1 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_DPT_DOSE_2 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_DPT_DOSE_3 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_PCV_10_DOSE_1 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_PCV_10_DOSE_2 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_PCV_10_DOSE_3 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_SARAMPO + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_ROTAVIRUS_DOSE_1 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_ROTAVIRUS_DOSE_2 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_ROTAVIRUS_DOSE_3 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_1 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_2 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_3 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_4 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_5 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_6 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_7 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_8 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_9 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_10 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_TOTAL + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_1 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_2 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_3 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_4 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_5 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_6 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_7 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_8 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_9 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_10 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_TOTAL + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_OTHERS_1 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_OTHERS_2 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_OTHERS_3 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_OTHERS_4 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_OTHERS_5 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_OTHERS_6 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_OTHERS_7 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_OTHERS_8 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_OTHERS_9 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_OTHERS_10 + " TEXT,"
			 + Database.ImunizationTable.COLUMN_VAC_OTHERS_TOTAL + " TEXT,"
			 + Database.ImunizationTable.COLUMN_CONTENT_URI + " TEXT);"
		
			 
			 + " CREATE UNIQUE INDEX IDX_ID ON " + Database.ImunizationTable.TABLE_NAME
             + "(" +  Database.ImunizationTable.COLUMN_INDIVIDUAL_ID + ");"
	 		;

}
