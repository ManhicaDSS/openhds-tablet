package org.openhds.mobile.dss.database;


import org.openhds.mobile.dss.model.Imunization;

import android.database.Cursor;

public class Converter {
	
	public static Imunization cursorToImunization(Cursor cursor){
		Imunization imu = new Imunization();
		
		
		imu.setId(cursor.getInt(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_ID)));
		imu.setHouseNumber(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_HOUSE_NUMBER)));
		imu.setIndividualId(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_INDIVIDUAL_ID)));
		imu.setPermId(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_PERM_ID)));
		imu.setName(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_NAME)));
		imu.setGender(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_GENDER)));
		imu.setDob(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_DOB)));
		imu.setHasAllVacs(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_HAS_ALL_VACS)));
		imu.setVacsOnDatabase(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VACS_ON_DATABASE)));
		imu.setVacBcg(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_BCG)));
		imu.setVacPolioDose0(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_POLIO_DOSE_0)));
		imu.setVacPolioDose1(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_POLIO_DOSE_1)));
		imu.setVacPolioDose2(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_POLIO_DOSE_2)));
		imu.setVacPolioDose3(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_POLIO_DOSE_3)));
		imu.setVacDptDose1(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_DPT_DOSE_1)));
		imu.setVacDptDose2(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_DPT_DOSE_2)));
		imu.setVacDptDose3(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_DPT_DOSE_3)));
		imu.setVacPcv10Dose1(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_PCV_10_DOSE_1)));
		imu.setVacPcv10Dose2(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_PCV_10_DOSE_2)));
		imu.setVacPcv10Dose3(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_PCV_10_DOSE_3)));
		imu.setVacSarampo(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_SARAMPO)));
		imu.setVacRotavirusDose1(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_ROTAVIRUS_DOSE_1)));
		imu.setVacRotavirusDose2(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_ROTAVIRUS_DOSE_2)));
		imu.setVacRotavirusDose3(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_ROTAVIRUS_DOSE_3)));
		imu.setVacVitaminaADose1(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_1)));
		imu.setVacVitaminaADose2(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_2)));
		imu.setVacVitaminaADose3(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_3)));
		imu.setVacVitaminaADose4(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_4)));
		imu.setVacVitaminaADose5(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_5)));
		imu.setVacVitaminaADose6(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_6)));
		imu.setVacVitaminaADose7(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_7)));
		imu.setVacVitaminaADose8(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_8)));
		imu.setVacVitaminaADose9(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_9)));
		imu.setVacVitaminaADose10(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_10)));
		imu.setVacVitaminaATotal(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_TOTAL)));
		imu.setVacMebendazolDose1(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_1)));
		imu.setVacMebendazolDose2(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_2)));
		imu.setVacMebendazolDose3(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_3)));
		imu.setVacMebendazolDose4(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_4)));
		imu.setVacMebendazolDose5(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_5)));
		imu.setVacMebendazolDose6(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_6)));
		imu.setVacMebendazolDose7(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_7)));
		imu.setVacMebendazolDose8(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_8)));
		imu.setVacMebendazolDose9(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_9)));
		imu.setVacMebendazolDose10(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_10)));
		imu.setVacMebendazolTotal(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_TOTAL)));
		imu.setVacOthers1(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_OTHERS_1)));
		imu.setVacOthers2(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_OTHERS_2)));
		imu.setVacOthers3(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_OTHERS_3)));
		imu.setVacOthers4(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_OTHERS_4)));
		imu.setVacOthers5(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_OTHERS_5)));
		imu.setVacOthers6(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_OTHERS_6)));
		imu.setVacOthers7(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_OTHERS_7)));
		imu.setVacOthers8(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_OTHERS_8)));
		imu.setVacOthers9(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_OTHERS_9)));
		imu.setVacOthers10(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_OTHERS_10)));
		imu.setVacOthersTotal(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_VAC_OTHERS_TOTAL)));
		imu.setLastContentUri(cursor.getString(cursor.getColumnIndex(Database.ImunizationTable.COLUMN_CONTENT_URI)));
		
		return imu;
	}
		
}
