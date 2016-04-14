package org.openhds.mobile.dss.model;

import java.io.Serializable;

import org.openhds.mobile.dss.database.Database;
import org.openhds.mobile.dss.database.Table;

import android.content.ContentValues;

public class Imunization implements Serializable, Table {

	private int id;
	String houseNumber;
	String individualId;
	String permId;
	String name;
	String gender;
	String dob;
	String hasAllVacs = "2";
	String vacsOnDatabase = "2";

	String vacBcg;
	String vacPolioDose0;
	String vacPolioDose1;
	String vacPolioDose2;
	String vacPolioDose3;
	String vacDptDose1;
	String vacDptDose2;
	String vacDptDose3;
	String vacPcv10Dose1;
	String vacPcv10Dose2;
	String vacPcv10Dose3;
	String vacSarampo;

	String vacRotavirusDose1;
	String vacRotavirusDose2;
	String vacRotavirusDose3;

	String vacVitaminaADose1;
	String vacVitaminaADose2;
	String vacVitaminaADose3;
	String vacVitaminaADose4;
	String vacVitaminaADose5;
	String vacVitaminaADose6;
	String vacVitaminaADose7;
	String vacVitaminaADose8;
	String vacVitaminaADose9;
	String vacVitaminaADose10;
	String vacVitaminaATotal = "0";

	String vacMebendazolDose1;
	String vacMebendazolDose2;
	String vacMebendazolDose3;
	String vacMebendazolDose4;
	String vacMebendazolDose5;
	String vacMebendazolDose6;
	String vacMebendazolDose7;
	String vacMebendazolDose8;
	String vacMebendazolDose9;
	String vacMebendazolDose10;
	String vacMebendazolTotal = "0";

	String vacOthers1;
	String vacOthers2;
	String vacOthers3;
	String vacOthers4;
	String vacOthers5;
	String vacOthers6;
	String vacOthers7;
	String vacOthers8;
	String vacOthers9;
	String vacOthers10;
	String vacOthersTotal = "0";
	private String lastContentUri = "";
			
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getIndividualId() {
		return individualId;
	}

	public void setIndividualId(String individualId) {
		this.individualId = individualId;
	}

	public String getPermId() {
		return permId;
	}

	public void setPermId(String permId) {
		this.permId = permId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getHasAllVacs() {
		return hasAllVacs;
	}

	public void setHasAllVacs(String hasAllVacs) {
		this.hasAllVacs = hasAllVacs;
	}

	public String getVacsOnDatabase() {
		return vacsOnDatabase;
	}

	public void setVacsOnDatabase(String vacsOnDatabase) {
		this.vacsOnDatabase = vacsOnDatabase;
	}

	public String getVacBcg() {
		return vacBcg;
	}

	public void setVacBcg(String vacBcg) {
		this.vacBcg = vacBcg;
	}

	public String getVacPolioDose0() {
		return vacPolioDose0;
	}

	public void setVacPolioDose0(String vacPolioDose0) {
		this.vacPolioDose0 = vacPolioDose0;
	}

	public String getVacPolioDose1() {
		return vacPolioDose1;
	}

	public void setVacPolioDose1(String vacPolioDose1) {
		this.vacPolioDose1 = vacPolioDose1;
	}

	public String getVacPolioDose2() {
		return vacPolioDose2;
	}

	public void setVacPolioDose2(String vacPolioDose2) {
		this.vacPolioDose2 = vacPolioDose2;
	}

	public String getVacPolioDose3() {
		return vacPolioDose3;
	}

	public void setVacPolioDose3(String vacPolioDose3) {
		this.vacPolioDose3 = vacPolioDose3;
	}

	public String getVacDptDose1() {
		return vacDptDose1;
	}

	public void setVacDptDose1(String vacDptDose1) {
		this.vacDptDose1 = vacDptDose1;
	}

	public String getVacDptDose2() {
		return vacDptDose2;
	}

	public void setVacDptDose2(String vacDptDose2) {
		this.vacDptDose2 = vacDptDose2;
	}

	public String getVacDptDose3() {
		return vacDptDose3;
	}

	public void setVacDptDose3(String vacDptDose3) {
		this.vacDptDose3 = vacDptDose3;
	}

	public String getVacPcv10Dose1() {
		return vacPcv10Dose1;
	}

	public void setVacPcv10Dose1(String vacPcv10Dose1) {
		this.vacPcv10Dose1 = vacPcv10Dose1;
	}

	public String getVacPcv10Dose2() {
		return vacPcv10Dose2;
	}

	public void setVacPcv10Dose2(String vacPcv10Dose2) {
		this.vacPcv10Dose2 = vacPcv10Dose2;
	}

	public String getVacPcv10Dose3() {
		return vacPcv10Dose3;
	}

	public void setVacPcv10Dose3(String vacPcv10Dose3) {
		this.vacPcv10Dose3 = vacPcv10Dose3;
	}

	public String getVacSarampo() {
		return vacSarampo;
	}

	public void setVacSarampo(String vacSarampo) {
		this.vacSarampo = vacSarampo;
	}

	public String getVacRotavirusDose1() {
		return vacRotavirusDose1;
	}

	public void setVacRotavirusDose1(String vacRotavirusDose1) {
		this.vacRotavirusDose1 = vacRotavirusDose1;
	}

	public String getVacRotavirusDose2() {
		return vacRotavirusDose2;
	}

	public void setVacRotavirusDose2(String vacRotavirusDose2) {
		this.vacRotavirusDose2 = vacRotavirusDose2;
	}

	public String getVacRotavirusDose3() {
		return vacRotavirusDose3;
	}

	public void setVacRotavirusDose3(String vacRotavirusDose3) {
		this.vacRotavirusDose3 = vacRotavirusDose3;
	}

	public String getVacVitaminaADose1() {
		return vacVitaminaADose1;
	}

	public void setVacVitaminaADose1(String vacVitaminaADose1) {
		this.vacVitaminaADose1 = vacVitaminaADose1;
	}

	public String getVacVitaminaADose2() {
		return vacVitaminaADose2;
	}

	public void setVacVitaminaADose2(String vacVitaminaADose2) {
		this.vacVitaminaADose2 = vacVitaminaADose2;
	}

	public String getVacVitaminaADose3() {
		return vacVitaminaADose3;
	}

	public void setVacVitaminaADose3(String vacVitaminaADose3) {
		this.vacVitaminaADose3 = vacVitaminaADose3;
	}

	public String getVacVitaminaADose4() {
		return vacVitaminaADose4;
	}

	public void setVacVitaminaADose4(String vacVitaminaADose4) {
		this.vacVitaminaADose4 = vacVitaminaADose4;
	}

	public String getVacVitaminaADose5() {
		return vacVitaminaADose5;
	}

	public void setVacVitaminaADose5(String vacVitaminaADose5) {
		this.vacVitaminaADose5 = vacVitaminaADose5;
	}

	public String getVacVitaminaADose6() {
		return vacVitaminaADose6;
	}

	public void setVacVitaminaADose6(String vacVitaminaADose6) {
		this.vacVitaminaADose6 = vacVitaminaADose6;
	}

	public String getVacVitaminaADose7() {
		return vacVitaminaADose7;
	}

	public void setVacVitaminaADose7(String vacVitaminaADose7) {
		this.vacVitaminaADose7 = vacVitaminaADose7;
	}

	public String getVacVitaminaADose8() {
		return vacVitaminaADose8;
	}

	public void setVacVitaminaADose8(String vacVitaminaADose8) {
		this.vacVitaminaADose8 = vacVitaminaADose8;
	}

	public String getVacVitaminaADose9() {
		return vacVitaminaADose9;
	}

	public void setVacVitaminaADose9(String vacVitaminaADose9) {
		this.vacVitaminaADose9 = vacVitaminaADose9;
	}

	public String getVacVitaminaADose10() {
		return vacVitaminaADose10;
	}

	public void setVacVitaminaADose10(String vacVitaminaADose10) {
		this.vacVitaminaADose10 = vacVitaminaADose10;
	}

	public String getVacVitaminaATotal() {
		return vacVitaminaATotal;
	}

	public void setVacVitaminaATotal(String vacVitaminaATotal) {
		this.vacVitaminaATotal = vacVitaminaATotal;
	}

	public String getVacMebendazolDose1() {
		return vacMebendazolDose1;
	}

	public void setVacMebendazolDose1(String vacMebendazolDose1) {
		this.vacMebendazolDose1 = vacMebendazolDose1;
	}

	public String getVacMebendazolDose2() {
		return vacMebendazolDose2;
	}

	public void setVacMebendazolDose2(String vacMebendazolDose2) {
		this.vacMebendazolDose2 = vacMebendazolDose2;
	}

	public String getVacMebendazolDose3() {
		return vacMebendazolDose3;
	}

	public void setVacMebendazolDose3(String vacMebendazolDose3) {
		this.vacMebendazolDose3 = vacMebendazolDose3;
	}

	public String getVacMebendazolDose4() {
		return vacMebendazolDose4;
	}

	public void setVacMebendazolDose4(String vacMebendazolDose4) {
		this.vacMebendazolDose4 = vacMebendazolDose4;
	}

	public String getVacMebendazolDose5() {
		return vacMebendazolDose5;
	}

	public void setVacMebendazolDose5(String vacMebendazolDose5) {
		this.vacMebendazolDose5 = vacMebendazolDose5;
	}

	public String getVacMebendazolDose6() {
		return vacMebendazolDose6;
	}

	public void setVacMebendazolDose6(String vacMebendazolDose6) {
		this.vacMebendazolDose6 = vacMebendazolDose6;
	}

	public String getVacMebendazolDose7() {
		return vacMebendazolDose7;
	}

	public void setVacMebendazolDose7(String vacMebendazolDose7) {
		this.vacMebendazolDose7 = vacMebendazolDose7;
	}

	public String getVacMebendazolDose8() {
		return vacMebendazolDose8;
	}

	public void setVacMebendazolDose8(String vacMebendazolDose8) {
		this.vacMebendazolDose8 = vacMebendazolDose8;
	}

	public String getVacMebendazolDose9() {
		return vacMebendazolDose9;
	}

	public void setVacMebendazolDose9(String vacMebendazolDose9) {
		this.vacMebendazolDose9 = vacMebendazolDose9;
	}

	public String getVacMebendazolDose10() {
		return vacMebendazolDose10;
	}

	public void setVacMebendazolDose10(String vacMebendazolDose10) {
		this.vacMebendazolDose10 = vacMebendazolDose10;
	}

	public String getVacMebendazolTotal() {
		return vacMebendazolTotal;
	}

	public void setVacMebendazolTotal(String vacMebendazolTotal) {
		this.vacMebendazolTotal = vacMebendazolTotal;
	}

	public String getVacOthers1() {
		return vacOthers1;
	}

	public void setVacOthers1(String vacOthers1) {
		this.vacOthers1 = vacOthers1;
	}

	public String getVacOthers2() {
		return vacOthers2;
	}

	public void setVacOthers2(String vacOthers2) {
		this.vacOthers2 = vacOthers2;
	}

	public String getVacOthers3() {
		return vacOthers3;
	}

	public void setVacOthers3(String vacOthers3) {
		this.vacOthers3 = vacOthers3;
	}

	public String getVacOthers4() {
		return vacOthers4;
	}

	public void setVacOthers4(String vacOthers4) {
		this.vacOthers4 = vacOthers4;
	}

	public String getVacOthers5() {
		return vacOthers5;
	}

	public void setVacOthers5(String vacOthers5) {
		this.vacOthers5 = vacOthers5;
	}

	public String getVacOthers6() {
		return vacOthers6;
	}

	public void setVacOthers6(String vacOthers6) {
		this.vacOthers6 = vacOthers6;
	}

	public String getVacOthers7() {
		return vacOthers7;
	}

	public void setVacOthers7(String vacOthers7) {
		this.vacOthers7 = vacOthers7;
	}

	public String getVacOthers8() {
		return vacOthers8;
	}

	public void setVacOthers8(String vacOthers8) {
		this.vacOthers8 = vacOthers8;
	}

	public String getVacOthers9() {
		return vacOthers9;
	}

	public void setVacOthers9(String vacOthers9) {
		this.vacOthers9 = vacOthers9;
	}

	public String getVacOthers10() {
		return vacOthers10;
	}

	public void setVacOthers10(String vacOthers10) {
		this.vacOthers10 = vacOthers10;
	}

	public String getVacOthersTotal() {
		return vacOthersTotal;
	}

	public void setVacOthersTotal(String vacOthersTotal) {
		this.vacOthersTotal = vacOthersTotal;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return Database.ImunizationTable.TABLE_NAME;
	}
		
	public String getLastContentUri() {
		return lastContentUri;
	}

	public void setLastContentUri(String lastContentUri) {
		this.lastContentUri = lastContentUri;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues cv = new ContentValues();
				
		cv.put(Database.ImunizationTable.COLUMN_HOUSE_NUMBER, houseNumber);
		cv.put(Database.ImunizationTable.COLUMN_INDIVIDUAL_ID, individualId);
		cv.put(Database.ImunizationTable.COLUMN_PERM_ID, permId);
		cv.put(Database.ImunizationTable.COLUMN_NAME, name);
		cv.put(Database.ImunizationTable.COLUMN_GENDER, gender);
		cv.put(Database.ImunizationTable.COLUMN_DOB, dob);
		cv.put(Database.ImunizationTable.COLUMN_HAS_ALL_VACS, hasAllVacs);
		cv.put(Database.ImunizationTable.COLUMN_VACS_ON_DATABASE, vacsOnDatabase);
		cv.put(Database.ImunizationTable.COLUMN_VAC_BCG, vacBcg);
		cv.put(Database.ImunizationTable.COLUMN_VAC_POLIO_DOSE_0, vacPolioDose0);
		cv.put(Database.ImunizationTable.COLUMN_VAC_POLIO_DOSE_1, vacPolioDose1);
		cv.put(Database.ImunizationTable.COLUMN_VAC_POLIO_DOSE_2, vacPolioDose2);
		cv.put(Database.ImunizationTable.COLUMN_VAC_POLIO_DOSE_3, vacPolioDose3);
		cv.put(Database.ImunizationTable.COLUMN_VAC_DPT_DOSE_1, vacDptDose1);
		cv.put(Database.ImunizationTable.COLUMN_VAC_DPT_DOSE_2, vacDptDose2);
		cv.put(Database.ImunizationTable.COLUMN_VAC_DPT_DOSE_3, vacDptDose3);
		cv.put(Database.ImunizationTable.COLUMN_VAC_PCV_10_DOSE_1, vacPcv10Dose1);
		cv.put(Database.ImunizationTable.COLUMN_VAC_PCV_10_DOSE_2, vacPcv10Dose2);
		cv.put(Database.ImunizationTable.COLUMN_VAC_PCV_10_DOSE_3, vacPcv10Dose3);
		cv.put(Database.ImunizationTable.COLUMN_VAC_SARAMPO, vacSarampo);
		cv.put(Database.ImunizationTable.COLUMN_VAC_ROTAVIRUS_DOSE_1, vacRotavirusDose1);
		cv.put(Database.ImunizationTable.COLUMN_VAC_ROTAVIRUS_DOSE_2, vacRotavirusDose2);
		cv.put(Database.ImunizationTable.COLUMN_VAC_ROTAVIRUS_DOSE_3, vacRotavirusDose3);
		cv.put(Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_1, vacVitaminaADose1);
		cv.put(Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_2, vacVitaminaADose2);
		cv.put(Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_3, vacVitaminaADose3);
		cv.put(Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_4, vacVitaminaADose4);
		cv.put(Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_5, vacVitaminaADose5);
		cv.put(Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_6, vacVitaminaADose6);
		cv.put(Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_7, vacVitaminaADose7);
		cv.put(Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_8, vacVitaminaADose8);
		cv.put(Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_9, vacVitaminaADose9);
		cv.put(Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_DOSE_10, vacVitaminaADose10);
		cv.put(Database.ImunizationTable.COLUMN_VAC_VITAMINA_A_TOTAL, vacVitaminaATotal);
		cv.put(Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_1, vacMebendazolDose1);
		cv.put(Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_2, vacMebendazolDose2);
		cv.put(Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_3, vacMebendazolDose3);
		cv.put(Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_4, vacMebendazolDose4);
		cv.put(Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_5, vacMebendazolDose5);
		cv.put(Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_6, vacMebendazolDose6);
		cv.put(Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_7, vacMebendazolDose7);
		cv.put(Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_8, vacMebendazolDose8);
		cv.put(Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_9, vacMebendazolDose9);
		cv.put(Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_DOSE_10, vacMebendazolDose10);
		cv.put(Database.ImunizationTable.COLUMN_VAC_MEBENDAZOL_TOTAL, vacMebendazolTotal);
		cv.put(Database.ImunizationTable.COLUMN_VAC_OTHERS_1, vacOthers1);
		cv.put(Database.ImunizationTable.COLUMN_VAC_OTHERS_2, vacOthers2);
		cv.put(Database.ImunizationTable.COLUMN_VAC_OTHERS_3, vacOthers3);
		cv.put(Database.ImunizationTable.COLUMN_VAC_OTHERS_4, vacOthers4);
		cv.put(Database.ImunizationTable.COLUMN_VAC_OTHERS_5, vacOthers5);
		cv.put(Database.ImunizationTable.COLUMN_VAC_OTHERS_6, vacOthers6);
		cv.put(Database.ImunizationTable.COLUMN_VAC_OTHERS_7, vacOthers7);
		cv.put(Database.ImunizationTable.COLUMN_VAC_OTHERS_8, vacOthers8);
		cv.put(Database.ImunizationTable.COLUMN_VAC_OTHERS_9, vacOthers9);
		cv.put(Database.ImunizationTable.COLUMN_VAC_OTHERS_10, vacOthers10);
		cv.put(Database.ImunizationTable.COLUMN_VAC_OTHERS_TOTAL, vacOthersTotal);
		cv.put(Database.ImunizationTable.COLUMN_CONTENT_URI, lastContentUri);
				
		return cv;
	}

	@Override
	public String[] getColumnNames() {
		return Database.ImunizationTable.ALL_COLUMNS;
	}

	public static Imunization emptyImunization(){
		Imunization imunization = new Imunization();
		
		imunization.houseNumber = "";
		imunization.individualId = "";
		imunization.permId = "";
		imunization.name = "";
		imunization.gender = "";
		imunization.dob = "";
		imunization.hasAllVacs = "2";
		imunization.vacsOnDatabase = "2";

		imunization.vacBcg = "";
		imunization.vacPolioDose0 = "";
		imunization.vacPolioDose1 = "";
		imunization.vacPolioDose2 = "";
		imunization.vacPolioDose3 = "";
		imunization.vacDptDose1 = "";
		imunization.vacDptDose2 = "";
		imunization.vacDptDose3 = "";
		imunization.vacPcv10Dose1 = "";
		imunization.vacPcv10Dose2 = "";
		imunization.vacPcv10Dose3 = "";
		imunization.vacSarampo = "";

		imunization.vacRotavirusDose1 = "";
		imunization.vacRotavirusDose2 = "";
		imunization.vacRotavirusDose3 = "";

		imunization.vacVitaminaADose1 = "";
		imunization.vacVitaminaADose2 = "";
		imunization.vacVitaminaADose3 = "";
		imunization.vacVitaminaADose4 = "";
		imunization.vacVitaminaADose5 = "";
		imunization.vacVitaminaADose6 = "";
		imunization.vacVitaminaADose7 = "";
		imunization.vacVitaminaADose8 = "";
		imunization.vacVitaminaADose9 = "";
		imunization.vacVitaminaADose10 = "";
		imunization.vacVitaminaATotal = "0";

		imunization.vacMebendazolDose1 = "";
		imunization.vacMebendazolDose2 = "";
		imunization.vacMebendazolDose3 = "";
		imunization.vacMebendazolDose4 = "";
		imunization.vacMebendazolDose5 = "";
		imunization.vacMebendazolDose6 = "";
		imunization.vacMebendazolDose7 = "";
		imunization.vacMebendazolDose8 = "";
		imunization.vacMebendazolDose9 = "";
		imunization.vacMebendazolDose10 = "";
		imunization.vacMebendazolTotal = "0";

		imunization.vacOthers1 = "";
		imunization.vacOthers2 = "";
		imunization.vacOthers3 = "";
		imunization.vacOthers4 = "";
		imunization.vacOthers5 = "";
		imunization.vacOthers6 = "";
		imunization.vacOthers7 = "";
		imunization.vacOthers8 = "";
		imunization.vacOthers9 = "";
		imunization.vacOthers10 = "";
		imunization.vacOthersTotal = "0";
		imunization.lastContentUri = "";
		
		return imunization;
	}
}
