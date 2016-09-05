package org.openhds.mobile.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.openhds.mobile.R;
import org.openhds.mobile.dss.model.Imunization;
import org.openhds.mobile.listener.SyncDatabaseListener;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

/**
 * AsyncTask responsible for downloading the OpenHDS "database", that is a
 * subset of the OpenHDS database records. It does the downloading
 * incrementally, by downloading parts of the data one at a time. For example,
 * it gets all locations and then retrieves all individuals. Ordering is
 * somewhat important here, because the database has a few foreign key
 * references that must be satisfied (e.g. individual references a location
 * location)
 */
public class SyncImunizationTask extends AsyncTask<Void, Integer, HttpTask.EndResult> {

	private static final String API_PATH = "/api/rest";

	private SyncDatabaseListener listener;
	private ContentResolver resolver;

	private UsernamePasswordCredentials creds;
	private ProgressDialog dialog;
	private HttpGet httpGet;
	private HttpClient client;

	private String baseurl;
	private String username;
	private String password;

	String lastExtId;

	private final List<ContentValues> values = new ArrayList<ContentValues>();
	private final ContentValues[] emptyArray = new ContentValues[] {};

	private State state;
	private Entity entity;
	
	private boolean isDownloadingZipFile;

	private enum State {
		DOWNLOADING, SAVING
	}

	private enum Entity {
		LOCATION_HIERARCHY, LOCATION, ROUND, VISIT, RELATIONSHIP, INDIVIDUAL, SOCIALGROUP, LOCATION_HIERARCHY_LEVELS, SETTINGS, IMUNIZATION
	}

	private Context mContext;
	
	public SyncImunizationTask(String url, String username, String password,
			ProgressDialog dialog, Context context,
			SyncDatabaseListener listener) {
		this.baseurl = url;
		this.username = username;
		this.password = password;
		this.dialog = dialog;
		this.listener = listener;
		this.resolver = context.getContentResolver();
		this.mContext = context;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		StringBuilder builder = new StringBuilder();
		switch (state) {
		case DOWNLOADING:
			builder.append(mContext.getString(R.string.sync_task_downloading) + " ");
			break;
		case SAVING:
			builder.append(mContext.getString(R.string.sync_task_saving) + " ");
			break;
		}

		switch (entity) {
		case IMUNIZATION:
			builder.append(mContext.getString(R.string.sync_task_imunizations));
			break;			
		}

		if (values.length > 0) {
			String msg = " " + mContext.getString(R.string.sync_task_saved)  + " " + values[0]  + " " +  mContext.getString(R.string.sync_task_items);
			if (state == State.DOWNLOADING && isDownloadingZipFile){
				msg = " " + mContext.getString(R.string.sync_task_saved)  + " " + values[0]  + "KB";
			}
			builder.append(msg);
		}

		dialog.setMessage(builder.toString());
	}

	@Override
	protected HttpTask.EndResult doInBackground(Void... params) {
		creds = new UsernamePasswordCredentials(username, password);

		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 60000);
		HttpConnectionParams.setSoTimeout(httpParameters, 90000);
		HttpConnectionParams.setSocketBufferSize(httpParameters, 8192);
		client = new DefaultHttpClient(httpParameters);

		// at this point, we don't care to be smart about which data to
		// download, we simply download it all
		deleteAllTables();
		//deleteAllClipDatabase();
		try {			
			entity = Entity.IMUNIZATION;
			processUrl("http://sap.manhica.net:4780/manhica-dbsync/api/dss-explorer/imunizations/zip"); //changing to control
		} catch (Exception e) {
			e.printStackTrace();
			return HttpTask.EndResult.FAILURE;
		}

		return HttpTask.EndResult.SUCCESS;
	}

	private void deleteAllTables() {
		// ordering is somewhat important during delete. a few tables have
		// foreign keys	
		deleteAllDssDatabase();
	}
	
	private String getAppStoragePath(){
		File root = Environment.getExternalStorageDirectory();
		String destinationPath = root.getAbsolutePath() + File.separator
				+ "Android" + File.separator + "data" + File.separator
				+ "org.openhds.mobile" + File.separator + "files" + File.separator + "downloads" + File.separator;

		File baseDir = new File(destinationPath);
		if (!baseDir.exists()) {
			boolean created = baseDir.mkdirs();
			if (!created) {
				return destinationPath;
			}
		}
		
		return destinationPath;
	}
	
	private InputStream saveFileToStorage(InputStream inputStream) throws Exception {

		String path = getAppStoragePath() + "imunizations.zip";
		FileOutputStream fout = new FileOutputStream(path);
		byte[] buffer = new byte[10*1024];
		int len = 0;
		long total = 0;

		publishProgress();

		while ((len = inputStream.read(buffer)) != -1){
			fout.write(buffer, 0, len);
			total += len;
			int perc =  (int) ((total/(1024)));
			publishProgress(perc);
		}

		fout.close();
		inputStream.close();

		FileInputStream fin = new FileInputStream(path);

		return fin;
	}
	
	private void processZIPDocument(InputStream inputStream) throws Exception {

		Log.d("zip", "processing zip file");


		ZipInputStream zin = new ZipInputStream(inputStream);
		ZipEntry entry = zin.getNextEntry();

		Log.d("zip-entry", ""+entry);
		
		if (entry != null){
			processXMLDocument(zin);
			zin.closeEntry();
		}

		zin.close();
	}

	private void processUrl(String url) throws Exception {
		state = State.DOWNLOADING;
		publishProgress();
		
		this.isDownloadingZipFile = url.endsWith("zip");

		httpGet = new HttpGet(url);
		processResponse();
	}

	private void processResponse() throws Exception {
		InputStream inputStream = getResponse();
		
		if (this.isDownloadingZipFile){
			InputStream zipInputStream = saveFileToStorage(inputStream);
			if (zipInputStream != null){
				Log.d("download", "zip = "+zipInputStream);
				processZIPDocument(zipInputStream);
				zipInputStream.close();
			}
				
		}else{
			if (inputStream != null)
				processXMLDocument(inputStream);
		}
	}

	private InputStream getResponse() throws AuthenticationException,
			ClientProtocolException, IOException {
		HttpResponse response = null;

		//httpGet.addHeader(new BasicScheme().authenticate(creds, httpGet));
		//httpGet.addHeader("content-type", "application/xml");
		response = client.execute(httpGet);
		
		//Handle 404
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND){
			throw new RuntimeException("404 Not found.");
		}		

		HttpEntity entity = response.getEntity();
		return entity.getContent();
	}

	private void processXMLDocument(InputStream content) throws Exception {
		state = State.SAVING;

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);

		XmlPullParser parser = factory.newPullParser();
		parser.setInput(new InputStreamReader(content));

		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT && !isCancelled()) {
			String name = null;

			switch (eventType) {
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("count")) {
					parser.next();
					int cnt = Integer.parseInt(parser.getText());
					publishProgress(cnt);
					parser.nextTag();
				} else if(name.equalsIgnoreCase("imunizations")) {
					processImunizationParams(parser);
				}
				break;
			}
			eventType = parser.next();
		}
	}
				
	private boolean notEndOfXmlDoc(String element, XmlPullParser parser)
			throws XmlPullParserException {
		return !element.equals(parser.getName())
				&& parser.getEventType() != XmlPullParser.END_TAG
				&& !isCancelled();
	}
	
	protected void onPostExecute(HttpTask.EndResult result) {
		listener.collectionComplete(result);
	}
	
	private void processImunizationParams(XmlPullParser parser) throws XmlPullParserException, IOException {
		
		org.openhds.mobile.dss.database.Database database = new org.openhds.mobile.dss.database.Database(mContext);
        database.open();
        database.beginTransaction();
		
        values.clear();
		
		int count = 0;
		List<Imunization> valuesTb = new ArrayList<Imunization>();
		
		Log.d("executing", "imunization xml");
        
		parser.nextTag();
		
		while (notEndOfXmlDoc("imunizations", parser)) {
			count++;
						
			Imunization imunization = new Imunization();
			
			//Log.d("houseNumber", parser.nextText());
			parser.nextTag();
			imunization.setHouseNumber(parser.nextText());

			//Log.d("individualId", parser.nextText());
			parser.nextTag();
			imunization.setIndividualId(parser.nextText());

			//Log.d("permId", parser.nextText());
			parser.nextTag();
			imunization.setPermId(parser.nextText());

			//Log.d("name", parser.nextText());
			parser.nextTag();
			imunization.setName(parser.nextText());

			//Log.d("gender", parser.nextText());
			parser.nextTag();
			imunization.setGender(parser.nextText());

			//Log.d("dob", parser.nextText());
			parser.nextTag();
			imunization.setDob(parser.nextText());

			//Log.d("hasAllVacs", parser.nextText());
			parser.nextTag();
			imunization.setHasAllVacs(parser.nextText());

			//Log.d("vacsOnDatabase", parser.nextText());
			parser.nextTag();
			imunization.setVacsOnDatabase(parser.nextText());

			//Log.d("vacBcg", parser.nextText());
			parser.nextTag();
			imunization.setVacBcg(parser.nextText());

			//Log.d("vacPolioDose0", parser.nextText());
			parser.nextTag();
			imunization.setVacPolioDose0(parser.nextText());

			//Log.d("vacPolioDose1", parser.nextText());
			parser.nextTag();
			imunization.setVacPolioDose1(parser.nextText());

			//Log.d("vacPolioDose2", parser.nextText());
			parser.nextTag();
			imunization.setVacPolioDose2(parser.nextText());

			//Log.d("vacPolioDose3", parser.nextText());
			parser.nextTag();
			imunization.setVacPolioDose3(parser.nextText());

			//Log.d("vacDptDose1", parser.nextText());
			parser.nextTag();
			imunization.setVacDptDose1(parser.nextText());

			//Log.d("vacDptDose2", parser.nextText());
			parser.nextTag();
			imunization.setVacDptDose2(parser.nextText());

			//Log.d("vacDptDose3", parser.nextText());
			parser.nextTag();
			imunization.setVacDptDose3(parser.nextText());

			//Log.d("vacPcv10Dose1", parser.nextText());
			parser.nextTag();
			imunization.setVacPcv10Dose1(parser.nextText());

			//Log.d("vacPcv10Dose2", parser.nextText());
			parser.nextTag();
			imunization.setVacPcv10Dose2(parser.nextText());

			//Log.d("vacPcv10Dose3", parser.nextText());
			parser.nextTag();
			imunization.setVacPcv10Dose3(parser.nextText());

			//Log.d("vacSarampo", parser.nextText());
			parser.nextTag();
			imunization.setVacSarampo(parser.nextText());

			//Log.d("vacRotavirusDose1", parser.nextText());
			parser.nextTag();
			imunization.setVacRotavirusDose1(parser.nextText());

			//Log.d("vacRotavirusDose2", parser.nextText());
			parser.nextTag();
			imunization.setVacRotavirusDose2(parser.nextText());

			//Log.d("vacRotavirusDose3", parser.nextText());
			parser.nextTag();
			imunization.setVacRotavirusDose3(parser.nextText());

			//Log.d("vacVitaminaADose1", parser.nextText());
			parser.nextTag();
			imunization.setVacVitaminaADose1(parser.nextText());

			//Log.d("vacVitaminaADose2", parser.nextText());
			parser.nextTag();
			imunization.setVacVitaminaADose2(parser.nextText());

			//Log.d("vacVitaminaADose3", parser.nextText());
			parser.nextTag();
			imunization.setVacVitaminaADose3(parser.nextText());

			//Log.d("vacVitaminaADose4", parser.nextText());
			parser.nextTag();
			imunization.setVacVitaminaADose4(parser.nextText());

			//Log.d("vacVitaminaADose5", parser.nextText());
			parser.nextTag();
			imunization.setVacVitaminaADose5(parser.nextText());

			//Log.d("vacVitaminaADose6", parser.nextText());
			parser.nextTag();
			imunization.setVacVitaminaADose6(parser.nextText());

			//Log.d("vacVitaminaADose7", parser.nextText());
			parser.nextTag();
			imunization.setVacVitaminaADose7(parser.nextText());

			//Log.d("vacVitaminaADose8", parser.nextText());
			parser.nextTag();
			imunization.setVacVitaminaADose8(parser.nextText());

			//Log.d("vacVitaminaADose9", parser.nextText());
			parser.nextTag();
			imunization.setVacVitaminaADose9(parser.nextText());

			//Log.d("vacVitaminaADose10", parser.nextText());
			parser.nextTag();
			imunization.setVacVitaminaADose10(parser.nextText());

			//Log.d("vacVitaminaATotal", parser.nextText());
			parser.nextTag();
			imunization.setVacVitaminaATotal(parser.nextText());

			//Log.d("vacMebendazolDose1", parser.nextText());
			parser.nextTag();
			imunization.setVacMebendazolDose1(parser.nextText());

			//Log.d("vacMebendazolDose2", parser.nextText());
			parser.nextTag();
			imunization.setVacMebendazolDose2(parser.nextText());

			//Log.d("vacMebendazolDose3", parser.nextText());
			parser.nextTag();
			imunization.setVacMebendazolDose3(parser.nextText());

			//Log.d("vacMebendazolDose4", parser.nextText());
			parser.nextTag();
			imunization.setVacMebendazolDose4(parser.nextText());

			//Log.d("vacMebendazolDose5", parser.nextText());
			parser.nextTag();
			imunization.setVacMebendazolDose5(parser.nextText());

			//Log.d("vacMebendazolDose6", parser.nextText());
			parser.nextTag();
			imunization.setVacMebendazolDose6(parser.nextText());

			//Log.d("vacMebendazolDose7", parser.nextText());
			parser.nextTag();
			imunization.setVacMebendazolDose7(parser.nextText());

			//Log.d("vacMebendazolDose8", parser.nextText());
			parser.nextTag();
			imunization.setVacMebendazolDose8(parser.nextText());

			//Log.d("vacMebendazolDose9", parser.nextText());
			parser.nextTag();
			imunization.setVacMebendazolDose9(parser.nextText());

			//Log.d("vacMebendazolDose10", parser.nextText());
			parser.nextTag();
			imunization.setVacMebendazolDose10(parser.nextText());

			//Log.d("vacMebendazolTotal", parser.nextText());
			parser.nextTag();
			imunization.setVacMebendazolTotal(parser.nextText());

			//Log.d("vacOthers1", parser.nextText());
			parser.nextTag();
			imunization.setVacOthers1(parser.nextText());

			//Log.d("vacOthers2", parser.nextText());
			parser.nextTag();
			imunization.setVacOthers2(parser.nextText());

			//Log.d("vacOthers3", parser.nextText());
			parser.nextTag();
			imunization.setVacOthers3(parser.nextText());

			//Log.d("vacOthers4", parser.nextText());
			parser.nextTag();
			imunization.setVacOthers4(parser.nextText());

			//Log.d("vacOthers5", parser.nextText());
			parser.nextTag();
			imunization.setVacOthers5(parser.nextText());

			//Log.d("vacOthers6", parser.nextText());
			parser.nextTag();
			imunization.setVacOthers6(parser.nextText());

			//Log.d("vacOthers7", parser.nextText());
			parser.nextTag();
			imunization.setVacOthers7(parser.nextText());

			//Log.d("vacOthers8", parser.nextText());
			parser.nextTag();
			imunization.setVacOthers8(parser.nextText());

			//Log.d("vacOthers9", parser.nextText());
			parser.nextTag();
			imunization.setVacOthers9(parser.nextText());

			//Log.d("vacOthers10", parser.nextText());
			parser.nextTag();
			imunization.setVacOthers10(parser.nextText());

			//Log.d("vacOthersTotal", parser.nextText());
			parser.nextTag();
			imunization.setVacOthersTotal(parser.nextText());
						
			//valuesTb.add(imunization);
			
			database.insert(imunization);
			
			if (count % 100 == 0) {
				publishProgress(count);
			}

			parser.nextTag(); // </pregnancyIdentification>
			parser.nextTag(); // <pregnancyIdentification>			
			
		}
		
		state = State.SAVING;
		entity = Entity.IMUNIZATION;
		
		/*
		if (!valuesTb.isEmpty()) {
			count = 0;
			for (Imunization p : valuesTb){
				count++;
				database.insert(p);
				publishProgress(count);
			}
		}
		*/
		
		database.setTransactionSuccessful();
		database.endTransaction();
		database.close();
	}		
		
	private void deleteAllDssDatabase(){
		org.openhds.mobile.dss.database.Database database = new org.openhds.mobile.dss.database.Database(mContext);
        database.open();        
        database.delete(Imunization.class, null, null);        
        database.close();
	}

}
