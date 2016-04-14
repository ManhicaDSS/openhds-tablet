package org.openhds.mobile.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.List;

import org.openhds.mobile.FormsProviderAPI;
import org.openhds.mobile.InstanceProviderAPI;
import org.openhds.mobile.OpenHDS;
import org.openhds.mobile.R;
import org.openhds.mobile.database.BaselineUpdate;
import org.openhds.mobile.database.DeathOfHoHUpdate;
import org.openhds.mobile.database.DeathUpdate;
import org.openhds.mobile.database.ExternalInMigrationUpdate;
import org.openhds.mobile.database.HouseholdUpdate;
import org.openhds.mobile.database.IndividualDetailsUpdate;
import org.openhds.mobile.database.IndividualVisitedUpdate;
import org.openhds.mobile.database.InternalInMigrationUpdate;
import org.openhds.mobile.database.LocationUpdate;
import org.openhds.mobile.database.MembershipUpdate;
import org.openhds.mobile.database.OutMigrationUpdate;
import org.openhds.mobile.database.PregnancyObservationUpdate;
import org.openhds.mobile.database.PregnancyOutcomeUpdate;
import org.openhds.mobile.database.RelationshipUpdate;
import org.openhds.mobile.database.Updatable;
import org.openhds.mobile.database.VisitUpdate;
import org.openhds.mobile.database.queries.Converter;
import org.openhds.mobile.database.queries.Queries;
import org.openhds.mobile.dss.database.Database.ImunizationTable;
import org.openhds.mobile.dss.model.Imunization;
import org.openhds.mobile.fragment.EventFragment;
import org.openhds.mobile.fragment.ProgressFragment;
import org.openhds.mobile.fragment.SelectionFragment;
import org.openhds.mobile.fragment.ValueFragment;
import org.openhds.mobile.fragment.ValueFragment.Displayed;
import org.openhds.mobile.listener.OdkFormLoadListener;
import org.openhds.mobile.model.FieldWorker;
import org.openhds.mobile.model.FilledForm;
import org.openhds.mobile.model.Form;
import org.openhds.mobile.model.FormFiller;
import org.openhds.mobile.model.FormXmlReader;
import org.openhds.mobile.model.Individual;
import org.openhds.mobile.model.Location;
import org.openhds.mobile.model.LocationHierarchy;
import org.openhds.mobile.model.LocationHierarchyLevel;
import org.openhds.mobile.model.LocationVisit;
import org.openhds.mobile.model.PregnancyOutcome;
import org.openhds.mobile.model.Round;
import org.openhds.mobile.model.Settings;
import org.openhds.mobile.model.SocialGroup;
import org.openhds.mobile.model.StateMachine;
import org.openhds.mobile.task.OdkGeneratedFormLoadTask;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * UpdateActivity mediates the interaction between the 3 column fragments. The
 * buttons in the left most column drive a state machine while the user
 * interacts with the application.
 */
public class UpdateActivity extends Activity implements ValueFragment.ValueListener, LoaderCallbacks<Cursor>,
        EventFragment.Listener, SelectionFragment.Listener, ValueFragment.OnlyOneEntryListener {

    private SelectionFragment sf;
    private ValueFragment vf;
    private EventFragment ef;
    private ProgressFragment progressFragment;
    private MenuItem  menuItemForm;

    // loader ids
    private static final int SOCIAL_GROUP_AT_LOCATION = 5;
    private static final int SOCIAL_GROUP_FOR_INDIVIDUAL = 10;
    private static final int SOCIAL_GROUP_FOR_EXT_INMIGRATION = 20;
    private static final int INDIVIDUALS_IN_SOCIAL_GROUP = 30;
    private static final int INDIVIDUALS_IN_SOCIAL_GROUP_ACTIVE = 40;
    
    // activity request codes for onActivityResult
    private static final int SELECTED_XFORM = 1;
    private static final int CREATE_LOCATION = 10;
    private static final int FILTER_RELATIONSHIP = 20;
    private static final int FILTER_LOCATION = 30;
    private static final int FILTER_FORM = 35;
    private static final int FILTER_INMIGRATION = 40;
    private static final int FILTER_BIRTH_FATHER = 45;
    private static final int LOCATION_GEOPOINT = 50;
    protected static final int FILTER_INMIGRATION_MOTHER = 60;
    protected static final int FILTER_INMIGRATION_FATHER = 70;
    protected static final int FILTER_INDIV_VISIT = 75;
    protected static final int FILTER_SOCIALGROUP = 80;
    
    private static int MINIMUM_HOUSEHOLD_AGE;
    private static final int DEFAULT_MINIMUM_HOUSEHOLD_AGE = 14;
    
    private static int MINIMUM_PARENTHOOD_AGE;
    private static final int DEFAULT_MINIMUM_PARENTHOOD_AGE = 12;

    // the uri of the last viewed xform
    private Uri contentUri;

    // status flags indicating a dialog, used for restoring the activity
    private boolean formUnFinished = false;
    private boolean xFormNotFound = false;

    private AlertDialog householdDialog;

    private final FormFiller formFiller = new FormFiller();
    private StateMachine stateMachine;
    
    private LocationVisit locationVisit = new LocationVisit();
    private FilledForm filledForm;
    private AlertDialog xformUnfinishedDialog;
    private boolean showingProgress;
    private Updatable updatable;
    private boolean extInm;
    private int levelNumbers;
    private String parentExtId;
    private boolean hhCreation;
    private boolean deathCreation;
    private String jrFormId;
    
	private ProgressDialog progress;
    
    //State machine stuff
	public static final String SELECT_HIERARCHY_1 = "Select Hierarchy 1";
	public static final String SELECT_HIERARCHY_2 = "Select Hierarchy 2";
	public static final String SELECT_HIERARCHY_3 = "Select Hierarchy 3";
	public static final String SELECT_HIERARCHY_4 = "Select Hierarchy 4";
	public static final String SELECT_HIERARCHY_5 = "Select Hierarchy 5";
	public static final String SELECT_HIERARCHY_6 = "Select Hierarchy 6";
	public static final String SELECT_HIERARCHY_7 = "Select Hierarchy 7";
	public static final String SELECT_HIERARCHY_8 = "Select Hierarchy 8";
	public static final String SELECT_ROUND = "Select Round";
	public static final String SELECT_LOCATION = "Select Location";
	public static final String CREATE_VISIT = "Create Visit";
	public static final String SELECT_INDIVIDUAL = "Select Individual";
	public static final String SELECT_EVENT = "Select Event";
	public static final String FINISH_VISIT = "Finish Visit";
	public static final String INMIGRATION = "Inmigration";
	private int CREATING_NEW_LOCATION = 0;
	private int RETURNING_TO_DSS = 0;
	private int createHouseDetails = 0;
	private int createIndivDetails = 0;
	private int createImunizationDetails = 0; //0-NA, 1-opened using preregistered, 2-opened a new imunization
	private int changingHouseholdHead = 0;
	
	private static final List<String> stateSequence = new ArrayList<String>();
//	private static final Map<String, Integer> stateLabels = new HashMap<String, Integer>();
	static {
		stateSequence.add(SELECT_HIERARCHY_1);
		stateSequence.add(SELECT_HIERARCHY_2);
		stateSequence.add(SELECT_HIERARCHY_3);
		stateSequence.add(SELECT_HIERARCHY_4);
		stateSequence.add(SELECT_HIERARCHY_5);
		stateSequence.add(SELECT_HIERARCHY_6);
		stateSequence.add(SELECT_HIERARCHY_7);
		stateSequence.add(SELECT_HIERARCHY_8);
		stateSequence.add(SELECT_ROUND);
		stateSequence.add(SELECT_LOCATION);
		stateSequence.add(CREATE_VISIT);
		stateSequence.add(SELECT_INDIVIDUAL);
		stateSequence.add(SELECT_EVENT);
		stateSequence.add(FINISH_VISIT);
		stateSequence.add(INMIGRATION);
	}    
	
	String unfinishedFormDialogMsg = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cursor curs = Queries.getAllHierarchyLevels(getContentResolver());
        List<LocationHierarchyLevel> lhll = Converter.toLocationHierarchyLevelList(curs); 
        curs.close();
        
        levelNumbers = lhll.size()-1;
        
        setContentView(R.layout.main);
        
        FieldWorker fw = (FieldWorker) getIntent().getExtras().getSerializable("fieldWorker");
        locationVisit.setFieldWorker(fw);

        vf = new ValueFragment();
        vf.addOnlyOneEntryListener(this);
        
        Cursor startCursor = Queries.getStartHierarchyLevel(getContentResolver(), "2");
        if (startCursor.moveToNext()) {
        	vf.setSTART_HIERARCHY_LEVEL_NAME(startCursor.getString(startCursor.getColumnIndex(OpenHDS.HierarchyLevels.COLUMN_LEVEL_NAME)));
        }
        startCursor.close();

        FragmentTransaction txn = getFragmentManager().beginTransaction();
        txn.add(R.id.middle_col, vf).commit();

        sf = (SelectionFragment) getFragmentManager().findFragmentById(R.id.selectionFragment);
        ef = (EventFragment) getFragmentManager().findFragmentById(R.id.eventFragment);
        
        ActionBar actionBar = getActionBar();
        actionBar.show();        
        
        if(savedInstanceState == null){
            //Create state machine
            stateMachine = new StateMachine(new LinkedHashSet<String>(stateSequence), stateSequence.get(0));
            
            registerTransitions();
	        sf.setLocationVisit(locationVisit);
	        ef.setLocationVisit(locationVisit);   
	        
	        String state = "Select Hierarchy 1";
	        stateMachine.transitionInSequence(state);
        }
        else{
        	String state = (String)savedInstanceState.getSerializable("currentState");
        	stateMachine = new StateMachine(new LinkedHashSet<String>(stateSequence), state);
        	
            locationVisit = (LocationVisit) savedInstanceState.getSerializable("locationvisit");

            String uri = savedInstanceState.getString("uri");
            if (uri != null)
                contentUri = Uri.parse(uri);

            if (savedInstanceState.getBoolean("xFormNotFound"))
                createXFormNotFoundDialog();
            if (savedInstanceState.getBoolean("unfinishedFormDialog"))
                createUnfinishedFormDialog();

            registerTransitions();
            sf.setLocationVisit(locationVisit);
            ef.setLocationVisit(locationVisit);
            
            //Restore last state
            stateMachine.transitionInSequence(state);        	
        }
        
        // SET MINIMUM HOH AGE
		android.database.Cursor c = Queries.getAllSettings(getContentResolver());
		Settings settings = Converter.convertToSettings(c); 
		c.close();
		MINIMUM_HOUSEHOLD_AGE = settings.getMinimumAgeOfHouseholdHead() == 0 ? DEFAULT_MINIMUM_HOUSEHOLD_AGE : settings.getMinimumAgeOfHouseholdHead();
		MINIMUM_PARENTHOOD_AGE = settings.getMinimumAgeOfParents() == 0 ? DEFAULT_MINIMUM_PARENTHOOD_AGE : settings.getMinimumAgeOfParents();
    }
    
    /**
     * At any given point in time, the screen can be rotated. This method is
     * responsible for saving the screen state.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("locationvisit", locationVisit);
        outState.putString("currentState", stateMachine.getState().toString());
        outState.putBoolean("unfinishedFormDialog", formUnFinished);
        outState.putBoolean("xFormNotFound", xFormNotFound);

        if (contentUri != null)
            outState.putString("uri", contentUri.toString());
    }
    
    private void restoreState(){
	    if(stateMachine != null && stateMachine.getState() != ""){
	    	String currentState = stateMachine.getState();
	    	stateMachine.transitionTo(currentState);
	    }
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    }

    /**
     * The main menu, showing multiple options
     */
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.formmenu, menu);
        this.menuItemForm = menu.getItem(0);
        menu.getItem(0).setVisible(false);
        super.onCreateOptionsMenu(menu);
        return true;
	}
    
    @Override
	public void onResume()
	{
	    super.onResume();
	    hideProgressFragment();
	    dismissLoadingDialog();
	    restoreState();
	}    

    /**
     * Defining what happens when a main menu item is selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
		if (itemId == R.id.extra_forms) {
			createFormMenu();
			return true;
		} else if (itemId == R.id.sync_database) {
			createSyncDatabaseMenu();
			return true;
		}
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Display dialog when user clicks on back button
     */    
	@Override
	public void onBackPressed() {
	    new AlertDialog.Builder(this)
	           .setMessage(getString(R.string.exiting_lbl))
	           .setCancelable(false)
	           .setPositiveButton(getString(R.string.yes_lbl), new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	            	   try{
	                    UpdateActivity.this.finish();
	            	   }
	            	   catch(Exception e){}
	               }
	           })
	           .setNegativeButton(getString(R.string.no_lbl), null)
	           .show();
	}    

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	ContentResolver resolver = getContentResolver();
    	Cursor cursor = null;
        switch (requestCode) {
        case SELECTED_XFORM:
        	handleXformResult(resultCode, data);
            break;
        case FILTER_FORM:
        	 if (resultCode != RESULT_OK) {
                 return;
             }
        	Form form =(Form) data.getExtras().getSerializable("form");
        	SocialGroup sg = null;
        	cursor = Queries.getSocialGroupsByIndividualExtId(resolver,locationVisit.getSelectedIndividual().getExtId());
        	if (cursor.moveToFirst()) {
        		sg = Converter.convertToSocialGroup(cursor);
        		locationVisit.getLocation().setHead(sg.getGroupHead());
        	}
        	filledForm = formFiller.fillExtraForm(locationVisit, form.getName(), sg);
        	formFiller.addGroupHead(filledForm, resolver, sg);
        	formFiller.addParents(filledForm, resolver, locationVisit.getSelectedIndividual().getExtId());
        	cursor.close();
        	loadForm(SELECTED_XFORM);
        	break;
        case FILTER_BIRTH_FATHER:
            handleFatherBirthResult(resultCode, data);
            break;
        case CREATE_LOCATION:
            handleLocationCreateResult(resultCode, data);
            break;
        case FILTER_RELATIONSHIP:
            handleFilterRelationshipResult(resultCode, data);
            break;
        case FILTER_LOCATION:
            if (resultCode != RESULT_OK) {
                return;
            }
        	Location location1 = (Location) data.getExtras().getSerializable("location");
        	locationVisit.setLocation(location1);
        	vf.onLoaderReset(null);
            stateMachine.transitionTo("Create Visit");
            break;
        case FILTER_SOCIALGROUP:
            if (resultCode != RESULT_OK) {
                return;
            }
        	SocialGroup socialGroup = (SocialGroup) data.getExtras().getSerializable("socialGroup");
        	vf.onLoaderReset(null);
            filledForm = formFiller.appendSocialGroup(socialGroup, filledForm);
            loadForm(SELECTED_XFORM);
            break;            
        case FILTER_INMIGRATION:
            handleFilterInMigrationResult(resultCode, data);
            vf.onLoaderReset(null);
            //stateMachine.transitionTo(INMIGRATION);
            stateMachine.transitionTo("Select Individual");
            break;
        case FILTER_INMIGRATION_MOTHER:
            handleFilterMother(resultCode, data);
            break;
        case FILTER_INDIV_VISIT:
            handleFilterIndivVisit(resultCode, data);
            break;
        case FILTER_INMIGRATION_FATHER:
            handleFilterFather(resultCode, data);
            break;
        case LOCATION_GEOPOINT:
            if (resultCode == RESULT_OK) {
                String extId = data.getExtras().getString("extId");
                // a few things need to happen here:
                // * get the location by extId
                cursor = Queries.getLocationByExtId(resolver, extId);
                Location location = Converter.toLocation(cursor);
               
                // * figure out the parent location hierarchy
                cursor = Queries.getHierarchyByExtId(resolver, location.getHierarchy());
                LocationHierarchy subvVllage = Converter.toHierarhcy(cursor, true);
                

                cursor = Queries.getHierarchyByExtId(resolver, subvVllage.getParent());
                LocationHierarchy village = Converter.toHierarhcy(cursor, true);
                
                cursor = Queries.getHierarchyByExtId(resolver, village.getParent());
                LocationHierarchy district = Converter.toHierarhcy(cursor, true);
                
                cursor = Queries.getHierarchyByExtId(resolver, district.getParent());
                LocationHierarchy region = Converter.toHierarhcy(cursor, true);
                
                cursor = Queries.allRounds(resolver);
                Round round = Converter.convertToRound(cursor);
                
                locationVisit.setHierarchy1(region);
                locationVisit.setHierarchy2(district);
                locationVisit.setHierarchy3(village);
                locationVisit.setHierarchy4(subvVllage);
                locationVisit.setRound(round);
                locationVisit.setLocation(location);
                
                
                sf.setLocationVisit(locationVisit);
                sf.setAll();
            	vf.onLoaderReset(null);
                stateMachine.transitionTo("Create Visit");
                cursor.close();
            }
        }
    }
    
	public void showLoadingDialog() {

	    if (progress == null) {
	        progress = new ProgressDialog(this);
	        progress.setTitle(getString(R.string.loading_lbl));
	        progress.setMessage(getString(R.string.please_wait_lbl));
	    }
	    progress.show();
	}

	public void dismissLoadingDialog() {

	    if (progress != null && progress.isShowing()) {
	        progress.dismiss();
	    }
	}    

    private void handleFilterIndivVisit(int resultCode, Intent data) { 
    	SocialGroup sg;
    	if (resultCode != RESULT_OK) {
            return;
        }

        Individual individual = (Individual) data.getExtras().getSerializable("individual");
        
        if (individual!=null){
          	filledForm.setIntervieweeId(individual.getExtId());
        }else{
           	filledForm.setIntervieweeId("UNK");
        }
        
        loadForm(SELECTED_XFORM);
	}

	private void handleFatherBirthResult(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        Individual individual = (Individual) data.getExtras().getSerializable("individual");
        new CreatePregnancyOutcomeTask(individual).execute();
    }

    private void handleLocationCreateResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            showProgressFragment();
            new CheckLocationFormStatus(getContentResolver(), contentUri).execute();
        } else {
            Toast.makeText(this, getString(R.string.odk_problem_lbl), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This differs from {@link UpdateActivity.CheckFormStatus} in that, upon
     * creating a new location, the user is automatically forwarded to creating
     * a visit. This happens because the user could in theory create a location,
     * and then skip the visit.
     */
    class CheckLocationFormStatus extends AsyncTask<Void, Void, Boolean> {

        private ContentResolver resolver;
        private Uri contentUri;

        public CheckLocationFormStatus(ContentResolver resolver, Uri contentUri) {
            this.resolver = resolver;
            this.contentUri = contentUri;
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            Cursor cursor = resolver.query(contentUri, new String[] { InstanceProviderAPI.InstanceColumns.STATUS,
                    InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH },
                    InstanceProviderAPI.InstanceColumns.STATUS + "=?",
                    new String[] { InstanceProviderAPI.STATUS_COMPLETE }, null);
            if (cursor.moveToNext()) {
                String filepath = cursor.getString(cursor
                        .getColumnIndex(InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH));
                
                FormXmlReader xmlReader = new FormXmlReader();
            	
            	//check if is BaselineUpdate            	
				try {
					Location location = xmlReader.readLocation(new FileInputStream(new File(filepath)), jrFormId);

					if (location == null) {
						return false;
					}

					// check if perm id exists
					if (Queries.hasLocationByName(resolver, location.getName())) {
						//Toast.makeText(BaselineActivity.this, "Perm ID da casa já existe,  não será possivel adiciona-lo ao sistema", 5000);
						unfinishedFormDialogMsg = "Perm ID da casa já existe,  não será possivel adiciona-lo ao sistema";
						return false;
					}

				} catch (FileNotFoundException e) {
					Log.e(BaselineUpdate.class.getName(),"Could not read In Migration XML file");
				}

            	xmlReader = null;
                
                LocationUpdate update = new LocationUpdate();
                update.updateDatabase(resolver, filepath, jrFormId);
                cursor.close();
                return true;
            } else {
                cursor.close();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            hideProgressFragment();
            if (result) {
            	//Handle new Location, load list and select first entry
            	String locationExtId = locationVisit.getLocation().getExtId();
            	if(locationExtId.length() > 0){
            		vf.loadFilteredLocationById(locationExtId);
            		vf.selectItemNoInList(0);
            		CREATING_NEW_LOCATION = 1;
            		//onCreateVisit();
            	}
            } else {
                createUnfinishedFormDialog();
            }            
        }
    }

    private void handleFilterInMigrationResult(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        showProgressFragment();
        Individual individual = (Individual) data.getExtras().getSerializable("individual");
    


        new CreateInternalInMigrationTask(individual).execute();
        locationVisit.setSelectedIndividual(individual);

        stateMachine.transitionTo("Inmigration");
        
    }

    private class CreateInternalInMigrationTask extends AsyncTask<Void, Void, Void> {

        private Individual individual;
        
        public CreateInternalInMigrationTask(Individual individual) {
            this.individual = individual;
        }

        @Override
        protected Void doInBackground(Void... params) {
            filledForm = formFiller.fillInternalInMigrationForm(locationVisit, individual);
            formFiller.addOriginHouseNumber(filledForm, getContentResolver());
            updatable = new InternalInMigrationUpdate();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            hideProgressFragment();
            loadForm(SELECTED_XFORM);
        }
    }

    
    private void handleFilterRelationshipResult(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        Individual individualB = (Individual) data.getExtras().getSerializable("individual");
        filledForm.setIndividualB(individualB.getExtId());

        if (individualB.getExtId().equalsIgnoreCase(filledForm.getIndividualA())){
        	//Cant create an relationship between an individual and it self
        	Toast.makeText(UpdateActivity.this, getString(R.string.cant_create_relationship_lbl) , Toast.LENGTH_LONG).show();
        	return;
        }
        
        if(individualB.getGender().equalsIgnoreCase(locationVisit.getSelectedIndividual().getGender())){
        	Toast.makeText(UpdateActivity.this, "O relacionamento conjugal deve ser entre um homem e uma mulher" , Toast.LENGTH_LONG).show();
        	return;        	
        }
        
        loadForm(SELECTED_XFORM);
    }    

    private void handleXformResult(int resultCode, Intent data) {		
        if (resultCode == RESULT_OK) {        	
            showProgressFragment();
            new CheckFormStatus(getContentResolver(), contentUri).execute();
        } else {
            Toast.makeText(this, getString(R.string.odk_problem_lbl), Toast.LENGTH_LONG).show();
    		deathCreation = false;
    		extInm= false;
    		updatable = null;
    		createIndivDetails = 0;
    		changingHouseholdHead = 0;
    		createImunizationDetails = 0;
        }
    }

    private void handleFilterFather(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        Individual individual = (Individual) data.getExtras().getSerializable("individual");
        filledForm.setFatherExtId(individual.getExtId());
        filledForm.setFatherPermId(individual.getLastName());
        filledForm.setFatherName(individual.getFirstName());
        //if (createIndivDetails != 1){
        	//filledForm.setIndividualLastName(individual.getLastName());
        	//filledForm.setIndividualFirstName(individual.getFirstName());
        //}
        loadForm(SELECTED_XFORM);
    }

    private void handleFilterMother(int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        Individual individual = (Individual) data.getExtras().getSerializable("individual");
        filledForm.setMotherExtId(individual.getExtId());
        filledForm.setMotherPermId(individual.getLastName());
        filledForm.setMotherName(individual.getFirstName());

        buildFatherDialog();
    }

    private void showProgressFragment() {
        if (showingProgress) {
        	Log.d("showing in progress", ""+ new Date());
            return;
        }

        if (progressFragment == null) {
            progressFragment = ProgressFragment.newInstance();
        }

        showingProgress = true;
        FragmentTransaction txn = getFragmentManager().beginTransaction();
        txn.remove(progressFragment);
        txn.add(R.id.middle_col, progressFragment, "progressFragment").commit();
    }

    void hideProgressFragment() {
        if (!showingProgress) {
            return;
        }

        showingProgress = false;
        FragmentManager fm = getFragmentManager();
        Fragment frag = fm.findFragmentByTag("progressFragment");
        
        if (frag!=null && frag instanceof ProgressFragment)
        {
        	FragmentTransaction tr = fm.beginTransaction();
        	tr.remove(frag).commitAllowingStateLoss();
        }
        
        FragmentTransaction txn = getFragmentManager().beginTransaction();
        if (!vf.isAdded()) {
        	txn.add(R.id.middle_col, vf).commitAllowingStateLoss();
        } else {
        	txn.show(vf);
        }
        
    }

    /**
     * AsyncTask that attempts to get the status of the form that the user just
     * filled out. In ODK, when a form is saved and marked as complete, its
     * status is set to {@link InstanceProviderAPI.STATUS_COMPLETE}. If the user
     * leaves the form in ODK before saving it, the status will not be set to
     * complete. Alternatively, the user could save the form, but not mark it as
     * complete. Since there is no way to tell the difference between the user
     * leaving the form without completing, or saving without marking as
     * complete, we enforce that the form be marked as complete before the user
     * can continue with update events. They have 2 options: go back to the form
     * and save it as complete, or delete the previously filled form.
     */
    class CheckFormStatus extends AsyncTask<Void, Void, Boolean> {

        private ContentResolver resolver;
        private Uri contentUri;

        public CheckFormStatus(ContentResolver resolver, Uri contentUri) {
            this.resolver = resolver;
            this.contentUri = contentUri;
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            Cursor cursor = resolver.query(contentUri, new String[] { InstanceProviderAPI.InstanceColumns.STATUS,
                    InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH },
                    InstanceProviderAPI.InstanceColumns.STATUS + "=?",
                    new String[] { InstanceProviderAPI.STATUS_COMPLETE }, null);
            if (cursor.moveToNext()) {
                String filepath = cursor.getString(cursor
                        .getColumnIndex(InstanceProviderAPI.InstanceColumns.INSTANCE_FILE_PATH));
                try{                	
	                if(updatable != null){
	                	
	                	FormXmlReader xmlReader = new FormXmlReader();
	                	Log.d("start-chf", ""+new Date());
	                	//check if is BaselineUpdate	                	
	                	if (updatable instanceof ExternalInMigrationUpdate){
	                		try {
	                			Individual individual = xmlReader.readInMigration(new FileInputStream(new File(filepath)), jrFormId);
	                				                			
	                			if (individual == null) {
	                            	return false;
	                        	}
	                			
	                			//check if perm id exists
	                			if (Queries.hasIndividualByPermId(resolver, individual.getLastName())){	                				
	                				unfinishedFormDialogMsg = "Perm ID do individuo já existe ("+individual.getLastName()+"),  não será possivel adiciona-lo ao sistema";
	                				return false;
	                			}	                					
	                        	
	                		} catch (FileNotFoundException e) {
	                            Log.e(BaselineUpdate.class.getName(), "Could not read In Migration XML file");
	                        }
	                	}
	                	
	                	if (updatable instanceof PregnancyOutcomeUpdate){
	                		try {
	                			PregnancyOutcome pregOut = xmlReader.readPregnancyOutcome(new FileInputStream(new File(filepath)), jrFormId);

	                            if (pregOut == null) {
	                                return false;
	                            }
	                			
	                            for(Individual child : pregOut.getChildren()) {
	                            	//check if perm id exists
		                			if (Queries.hasIndividualByPermId(resolver, child.getLastName())){
		                				//Toast.makeText(BaselineActivity.this, "Perm ID do individuo já existe,  não será possivel adiciona-lo ao sistema", 5000);
		                				unfinishedFormDialogMsg = "Perm ID do individuo já existe ("+child.getLastName()+"),  não será possivel adiciona-lo ao sistema";
		                				return false;
		                			}
	                            }
	                				                					
	                        	
	                		} catch (FileNotFoundException e) {
	                            Log.e(BaselineUpdate.class.getName(), "Could not read In Migration XML file");
	                        }
	                	}
	                	
	                	xmlReader = null;
	                	
	                	updatable.updateDatabase(getContentResolver(), filepath, jrFormId);
	                	updatable = null;
	                	
	                	Log.d("end-chf", ""+new Date());
	                }
                }finally{
                	try{
                		cursor.close();
                	}catch(Exception e){
                		System.err.println("Exception while trying to close cursor !");
                		e.printStackTrace();
                	}
                }
                return true;
            } else {
            	try{
            		cursor.close();
            	}catch(Exception e){
            		System.err.println("Exception while trying to close cursor !");
            		e.printStackTrace();
            	}            	
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            hideProgressFragment();
            Log.d("start-post", "hidden"+new Date()+ ", result="+result);
            if (result) {
            	if (createHouseDetails == 1){
            		onClearIndividual();
            		createHouseDetails = 0;	
            		return;
            	}
            	
            	if (changingHouseholdHead == 1){
            		//save new head of household
            		Log.d("new head of household", filledForm.getHouseholdId()+" to "+filledForm.getIndividualA()+": "+filledForm.getIndividualFirstName());
            		//changing head of household
            	    ContentValues cv = new ContentValues();
                    cv.put(OpenHDS.SocialGroups.COLUMN_SOCIALGROUP_GROUPHEAD, filledForm.getIndividualA());                    
                    resolver.update(OpenHDS.SocialGroups.CONTENT_ID_URI_BASE, cv, OpenHDS.SocialGroups.COLUMN_SOCIALGROUP_EXTID+"=?", new String[]{ filledForm.getHouseholdId()} );
            		sf.setHouseholdHead(filledForm.getIndividualFirstName());
                    
            		onClearIndividual();
            		changingHouseholdHead = 0;	
            		return;
            	}
            	
            	if (createImunizationDetails > 0){
            		//save contentUri to form
            		Imunization im = Imunization.emptyImunization();
            		org.openhds.mobile.dss.database.Database db = new org.openhds.mobile.dss.database.Database(UpdateActivity.this);
    	    		db.open();
    	    		
    	    		Cursor cursor = db.query(Imunization.class, ImunizationTable.COLUMN_INDIVIDUAL_ID + " = ?", new String[] { filledForm.getIndividualExtId() }, null, null, null);
    	    		
    	    		if (cursor != null && cursor.moveToFirst()){ //If already exists a imunization	    			
    	    			im = org.openhds.mobile.dss.database.Converter.cursorToImunization(cursor);  
    	    			//Log.d("update imunization", "");
    	    			ContentValues cv = new ContentValues();    	    			
    	    			cv.put(org.openhds.mobile.dss.database.Database.ImunizationTable.COLUMN_CONTENT_URI, this.contentUri.toString());
    	    			    	    					
    	    			db.update(Imunization.class, cv, org.openhds.mobile.dss.database.Database.ImunizationTable.COLUMN_INDIVIDUAL_ID+" = ?", new String[] { im.getIndividualId() });
    	    			
    	    		}else{ //new imunization
    	    			//Log.d("saving new imunization", "");
    	    			im.setHouseNumber(filledForm.getHouseName());
    	    			im.setIndividualId(filledForm.getIndividualExtId());
    	    			im.setPermId(filledForm.getIndividualLastName());
    	    			im.setName(filledForm.getIndividualFirstName());
    	    			im.setGender(filledForm.getIndividualGender());
    	    			im.setDob(filledForm.getIndividualDob());
    	    			im.setLastContentUri(this.contentUri.toString());
    	    			db.insert(im);
    	    		}   
    	    		
    	    		if (cursor != null) cursor.close();
    	    		db.close();    	    		
            	}
            	
            	if (createIndivDetails == 1){
            		createIndivDetails = 0;            		
            	}
            	
            	if (stateMachine.getState()=="Inmigration") {
            		stateMachine.transitionTo("Select Event");
            		if (extInm)
                		onFinishExternalInmigration();
            	} else if (stateMachine.getState()=="Select Individual") {
            		if (extInm)
                		onFinishExternalInmigration();
            		selectIndividual();
            	}else if (stateMachine.getState()=="Select Event") {
            		if (hhCreation)
            			onFinishedHouseHoldCreation();
            		else if(deathCreation){            			
            			onClearIndividual();
            		}
            	}else {
            		stateMachine.transitionTo("Select Individual");
            	}
            } else {
            	Log.d("start-unfd", ""+new Date());
                createUnfinishedFormDialog();
                Log.d("end-unfd", ""+new Date());
            }
            
    		deathCreation = false;
    		extInm = false;
    		createHouseDetails = 0;
    		createIndivDetails = 0;
    		createImunizationDetails = 0;
        }
    }
    
	private void onFinishedHouseHoldCreation() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.householdBtn_lbl));
        alertDialogBuilder.setMessage(getString(R.string.finish_household_creation_msg));
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton("Ok", null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();		
                
        hhCreation = false;
	}       

    /**
     * Creates the 'Configure Server' option in the action menu.
     */
    private void createFormMenu() {
        Intent i = new Intent(this, FilterFormActivity.class);
        i.putExtra("location", locationVisit);
        startActivityForResult(i, FILTER_FORM);
    }

    /**
     * Creates the 'Sync Database' option in the action menu.
     */
    private void createSyncDatabaseMenu() {
        //Intent i = new Intent(this, SyncDatabaseActivity.class);
        //startActivity(i);
    }

    /**
     * Method used for starting the activity for filtering for individuals
     */
    private void startFilterActivity(int requestCode) {
    	Intent i =null;
    	if (requestCode==FILTER_INDIV_VISIT) {
            i = new Intent(this, FilterVisitActivity.class);
    	} 
    	else if(requestCode == FILTER_SOCIALGROUP){
    		i = new Intent(this, FilterSocialGroupActivity.class);
    	}
    	else {
    		i = new Intent(this, FilterActivity.class);
    	}
    	

        i.putExtra("hierarchy1", locationVisit.getHierarchy1());
        i.putExtra("hierarchy2", locationVisit.getHierarchy2());
        i.putExtra("hierarchy3", locationVisit.getHierarchy3());
        i.putExtra("hierarchy4", locationVisit.getHierarchy4());
        i.putExtra("hierarchy5", locationVisit.getHierarchy5());
        i.putExtra("hierarchy6", locationVisit.getHierarchy6());
        i.putExtra("hierarchy7", locationVisit.getHierarchy7());
        i.putExtra("hierarchy8", locationVisit.getHierarchy8());

        Location loc = locationVisit.getLocation();
        if (loc == null) {
            loc = Location.emptyLocation();
        }
        i.putExtra("location", loc);

        switch (requestCode) {
        case FILTER_INMIGRATION_MOTHER:
            i.putExtra("requireGender", "F");
            break;
        case FILTER_INMIGRATION_FATHER:
            i.putExtra("requireGender", "M");
            break;
        case FILTER_BIRTH_FATHER:
            i.putExtra("requireGender", "M");
            i.putExtra("minimumAge", MINIMUM_PARENTHOOD_AGE);
        case FILTER_INMIGRATION:
            i.putExtra("img", "ENT");
        }

         if (CREATING_NEW_LOCATION == 1) {
        	handleFilterIndivVisit(RESULT_OK, i);
        	return;
         }
         
         if (RETURNING_TO_DSS == 1){
        	 i.putExtra("img", "IMG_RETURN");
         }
         
        startActivityForResult(i, requestCode);
    }
    
    
    /**
     * Method used for starting the activity for filtering for Locations
     */
    private void startFilterLocActivity(int requestCode) {
        Intent i = new Intent(this, FilterLocationActivity.class);
        i.putExtra("hierarchy1", locationVisit.getHierarchy1());
        i.putExtra("hierarchy2", locationVisit.getHierarchy2());
        i.putExtra("hierarchy3", locationVisit.getHierarchy3());
        i.putExtra("hierarchy4", locationVisit.getHierarchy4());

        Location loc = locationVisit.getLocation();
        if (loc == null) {
            loc = Location.emptyLocation();
        }
        i.putExtra("location", loc);


        startActivityForResult(i, requestCode);
    }    
    
    private void loadHierarchy1ValueData() {
        vf.loadLocationHierarchy();
    }

    private void loadHierarchy2ValueData() {
        vf.loadHierarchy2(locationVisit.getHierarchy1().getExtId());
    }
    
    private void loadHierarchy3ValueData() {
        vf.loadHierarchy3(locationVisit.getHierarchy2().getExtId());
    }

    private void loadHierarchy4ValueData() {
        vf.loadHierarchy4(locationVisit.getHierarchy3().getExtId());
    }

    private void loadHierarchy5ValueData() {
        vf.loadHierarchy5(locationVisit.getHierarchy4().getExtId());
    }
    
    private void loadHierarchy6ValueData() {
        vf.loadHierarchy6(locationVisit.getHierarchy5().getExtId());
    }
    
    private void loadHierarchy7ValueData() {
        vf.loadHierarchy7(locationVisit.getHierarchy6().getExtId());
    }
    
    private void loadHierarchy8ValueData() {
        vf.loadHierarchy8(locationVisit.getHierarchy7().getExtId());
    }
    private void loadLocationValueData() {
        vf.loadLocations(parentExtId);
    }

  /*  private void loadRoundValueData() {
        vf.loadRounds();
    }*/

    private void loadIndividualValueData() {
        vf.loadIndividuals(locationVisit.getLocation().getExtId());
    }

    private void createUnfinishedFormDialog() {
        formUnFinished = true;
        if (xformUnfinishedDialog == null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.warning_lbl));
            
            if (unfinishedFormDialogMsg.isEmpty()){
            	alertDialogBuilder.setMessage(getString(R.string.update_unfinish_msg1));
            }else {
            	alertDialogBuilder.setMessage(unfinishedFormDialogMsg + "\n\n" + getString(R.string.update_unfinish_msg1));
            }
            
            alertDialogBuilder.setCancelable(true);
            alertDialogBuilder.setPositiveButton(getString(R.string.update_unfinish_pos_button), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    formUnFinished = false;
                    xformUnfinishedDialog.hide();
                    getContentResolver().delete(contentUri, InstanceProviderAPI.InstanceColumns.STATUS + "=?",
                            new String[] { InstanceProviderAPI.STATUS_INCOMPLETE });
                }
            });
            alertDialogBuilder.setNegativeButton(getString(R.string.update_unfinish_neg_button), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    formUnFinished = false;
                    xformUnfinishedDialog.hide();
                    startActivityForResult(new Intent(Intent.ACTION_EDIT, contentUri), SELECTED_XFORM);
                }
            });
            xformUnfinishedDialog = alertDialogBuilder.create();
        }
               
        xformUnfinishedDialog.show();
        
        unfinishedFormDialogMsg = "";
    }

    private void createXFormNotFoundDialog() {
        xFormNotFound = true;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
          alertDialogBuilder.setTitle(getString(R.string.warning_lbl));
          alertDialogBuilder.setMessage(getString(R.string.update_xform_not_found_msg));
          alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                xFormNotFound = false;
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void onLocationGeoPoint() {
        Intent intent = new Intent(getApplicationContext(), ShowMapActivity.class);
        startActivityForResult(intent, LOCATION_GEOPOINT);
    }

    public void onCreateLocation() {
        showProgressFragment();
        new GenerateLocationTask().execute();
    }

    private class GenerateLocationTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            locationVisit.createLocation(getContentResolver());            
            filledForm = formFiller.fillLocationForm(locationVisit);
            updatable = new LocationUpdate();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            hideProgressFragment();
            loadForm(CREATE_LOCATION);
        }

    }

    public void onCreateVisit() {
        new CreateVisitTask().execute();
    }

    private class CreateVisitTask extends AsyncTask<Void, Void, Void> {

    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		showProgressFragment();
    	}
    	
        @Override
        protected Void doInBackground(Void... params) {
            locationVisit.createVisit(getContentResolver());
            filledForm = formFiller.fillVisitForm(locationVisit);
            updatable = new VisitUpdate();
        	startFilterActivity(FILTER_INDIV_VISIT);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        	super.onPostExecute(result);
            hideProgressFragment();
            CREATING_NEW_LOCATION = 0;
        }
    }

    public void onFinishVisit() {
        validateVisit();
    }
    
    private void validateVisit(){
    	Cursor curs = null;
    	    	
    	curs = Queries.getActiveIndividualsByResidency(getContentResolver(), locationVisit.getLocation().getExtId());    		
    	    	    	
    	final List<Individual> individualList = Converter.toIndividualList(curs);
    	int individualCount = individualList.size();
    	
    	int visitedIndividualsCount = 0;
    	    	
    	for(int i = 0 ; i < individualCount; i++){
    		Individual individual = individualList.get(i);
    		if(individual.getVisited().equalsIgnoreCase("Yes")){
    			visitedIndividualsCount++;
    		}
    	}
    	    	
    	final boolean notAllIndividualsVisited = visitedIndividualsCount < individualCount;
    	String message;
    	if(notAllIndividualsVisited){
    		message = getString(R.string.update_finish_not_all_visited_msg);
    	}
    	else{
    		message = getString(R.string.update_finish_visit_msg);
    	}
    	   		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(getString(R.string.visit_lbl));
		alertDialogBuilder.setMessage(message);
		alertDialogBuilder.setCancelable(true);
		alertDialogBuilder.setPositiveButton(getString(R.string.yes_lbl),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (notAllIndividualsVisited) {
							/*//If not all individuals have visit - just left as it is
							 for (Individual ind : individualList) {
								if (!ind.getVisited().equalsIgnoreCase("Yes"))
									setIndividualVisitedFlag(ind);
							}
							*/
						}
						finishVisit();
					}
				});
		alertDialogBuilder.setNegativeButton(getString(R.string.cancel_lbl),
				null);
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
    }
    
    private void finishVisit(){
    	if(menuItemForm != null) {
          	menuItemForm.setVisible(false);
    	}    
    	locationVisit = locationVisit.completeVisit();
        sf.setLocationVisit(locationVisit);
        ef.setLocationVisit(locationVisit);
        stateMachine.transitionTo("Finish Visit");
        stateMachine.transitionTo("Select Location");
        vf.onLoaderReset(null);
        //vf.setListCurrentlyDisplayed(Displayed.LOCATION);
    }
    
    private void setIndividualVisitedFlag(Individual individiual){
    	IndividualVisitedUpdate update = new IndividualVisitedUpdate();
    	update.updateDatabase(getContentResolver(), individiual);
    }

    public void onHousehold() {
        showProgressFragment();
        new CreateSocialGroupTask().execute();
    }

    private class CreateSocialGroupTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            SocialGroup sg = locationVisit.createSocialGroup(getContentResolver());
            if (sg==null){
            	//this.cancel(true);
            	//hideProgressFragment();
            	//onSGexists();
            } else {
            	filledForm = formFiller.fillSocialGroupForm(locationVisit, sg);
            	updatable = new HouseholdUpdate();
            }
            
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        	 SocialGroup sg = locationVisit.createSocialGroup(getContentResolver());
             if (sg==null){
            	onSGexists();
            	this.cancel(true);
             	hideProgressFragment();
             	
             } else {
            	hideProgressFragment();
            	hhCreation = true;
            	loadForm(SELECTED_XFORM);
             }
        }
    }
    
    public void onSGexists() {
    	 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
         alertDialogBuilder.setTitle(getString(R.string.socialgroup_lbl));
         alertDialogBuilder.setMessage(getString(R.string.update_on_sgexists_msg));
         alertDialogBuilder.setCancelable(true);
         alertDialogBuilder.setPositiveButton("Ok", null);
         AlertDialog alertDialog = alertDialogBuilder.create();
         alertDialog.show();         
    }

    public void onMembership() {
        filledForm = formFiller.fillMembershipForm(locationVisit);
        updatable = new MembershipUpdate();
//        showProgressFragment();
        getLoaderManager().restartLoader(SOCIAL_GROUP_AT_LOCATION, null, this);
    }

    public void onRelationship() {
        filledForm = formFiller.fillRelationships(locationVisit);
        updatable = new RelationshipUpdate();
        startFilterActivity(FILTER_RELATIONSHIP);
    }
    
    public void onBaseline(){
    	//We call onMigration
    	onInMigration();
    }

    public void onInMigration() {
    	RETURNING_TO_DSS =0;
        createInMigrationFormDialog();
    }

    private void createInMigrationFormDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.in_migration_lbl));
        alertDialogBuilder.setMessage(getString(R.string.update_create_inmigration_msg));
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton(getString(R.string.update_create_inmigration_pos_button), new DialogInterface.OnClickListener() {
        	 public void onClick(DialogInterface dialog, int which) {
            	extInm= true;
            	startFilterActivity(FILTER_INMIGRATION);
     
            }
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.update_create_inmigration_neg_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                showProgressFragment();
                extInm= true;
                new CreateExternalInmigrationTask().execute();

            }
        });
        alertDialogBuilder.setNeutralButton(getString(R.string.update_create_inmigration_neutral_button), new DialogInterface.OnClickListener() {
       	    public void onClick(DialogInterface dialog, int which) {
       	    	extInm= true;
       	    	RETURNING_TO_DSS = 1;
       	    	startFilterActivity(FILTER_INMIGRATION);    
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private class CreateExternalInmigrationTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
        	String id = locationVisit.generateIndividualId(getContentResolver());
            String permId = locationVisit.generateIndividualPermID(getContentResolver());
            
            filledForm = formFiller.fillExternalInmigration(locationVisit, id);
            filledForm.setIndividualLastName(permId);
            filledForm.setLocationName(locationVisit.getLocation().getName());
            
            updatable = new ExternalInMigrationUpdate();            
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            hideProgressFragment();
            buildMotherDialog();
         }
    }

    private void selectIndividual(){
        String indExtId = filledForm.getIndividualExtId();
        if(indExtId.length() > 0){
        	vf.onLoaderReset(null);
        	vf.loadFilteredIndividualById(indExtId);
        	vf.selectItemNoInList(0);
        }    	
    }
    
	private void onFinishExternalInmigration() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.in_migration_lbl));
        alertDialogBuilder.setMessage(getString(R.string.update_finish_ext_inmigration_msg));
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton("Ok", null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();		
        extInm= false;
	}

    private void buildMotherDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getString(R.string.mother_lbl));
        alertDialogBuilder.setMessage(getString(R.string.update_build_mother_msg));
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton(getString(R.string.yes_lbl), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startFilterActivity(FILTER_INMIGRATION_MOTHER);
            }
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.no_lbl), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                filledForm.setMotherExtId("UNK");
                filledForm.setMotherPermId("UNK");
                filledForm.setMotherName("UNK");
                buildFatherDialog();
            }
        });
        
        if (createIndivDetails == 1) {
        	alertDialogBuilder.setNeutralButton("Não Alterar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {                    
                    buildFatherDialog();
                }
            });
        }
        
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void buildFatherDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
       alertDialogBuilder.setTitle(getString(R.string.father_lbl));
        alertDialogBuilder.setMessage(getString(R.string.update_build_father_msg));
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton(getString(R.string.yes_lbl), new DialogInterface.OnClickListener() {
        	  public void onClick(DialogInterface dialog, int which) {
                startFilterActivity(FILTER_INMIGRATION_FATHER);
            }
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.no_lbl), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                filledForm.setFatherExtId("UNK");
                filledForm.setFatherPermId("UNK");
                filledForm.setFatherName("UNK");
                loadForm(SELECTED_XFORM);
            }
        });
        
        if (createIndivDetails == 1) {
        	alertDialogBuilder.setNeutralButton("Não Alterar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {                    
                	loadForm(SELECTED_XFORM);                    
                }
            });
        }
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void onOutMigration() {
        showProgressFragment();
        new CreateOutMigrationTask().execute();
    }

    private class CreateOutMigrationTask extends AsyncTask<Void, Void, Void> {

    	private SocialGroup sg;
        @Override
        protected Void doInBackground(Void... params) {
            filledForm = formFiller.fillOutMigrationForm(locationVisit);
            updatable = new OutMigrationUpdate();
            
            Individual individual = locationVisit.getSelectedIndividual();
            if(individual != null){
            	ContentResolver resolver = getContentResolver();
            	Cursor cursor = Queries.getSocialGroupsByIndividualExtId(resolver,individual.getExtId());
            	if (cursor.moveToFirst()) {
            		SocialGroup socialGroup = Converter.convertToSocialGroup(cursor);
            		this.sg = socialGroup;
            		locationVisit.getLocation().setHead(sg.getGroupHead());
            	}
            	cursor.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            hideProgressFragment();
            
            if(this.sg != null){
            	if(locationVisit.getSelectedIndividual().getExtId().equalsIgnoreCase(sg.getGroupHead())){
            		Toast.makeText(UpdateActivity.this, getString(R.string.is_migrating_hoh_warning_lbl) , Toast.LENGTH_LONG).show();
            	}
            	else{
            		Toast.makeText(UpdateActivity.this, getString(R.string.is_not_migrating_hoh_warning_lbl), Toast.LENGTH_LONG).show();
            	}
            }
            loadForm(SELECTED_XFORM);
        }
    }

    public void onPregnancyRegistration() {
        showProgressFragment();
        new CreatePregnancyObservationTask().execute();
    }

    private class CreatePregnancyObservationTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            filledForm = formFiller.fillPregnancyRegistrationForm(locationVisit);
            updatable = new PregnancyObservationUpdate();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            hideProgressFragment();
            loadForm(SELECTED_XFORM);
        }
    }

    /**
     * The pregnancy outcome flow is as follows: <br />
     * 1. Prompt user for the number of live births. This indicates how many
     * child ids will be generated. <br />
     * 2. Prompt user for the father. We attempt to determine the father by
     * looking at any relationships the mother has. The user also has the option
     * of searching for the father as well. <br />
     * 3. Prompt for the social group to use. In this scenario, a search is made
     * for all memberships present at a location.
     */
    public void onPregnancyOutcome() {
        buildPregnancyLiveBirthCountDialog();
    }

    private void buildPregnancyLiveBirthCountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.update_build_pregnancy_lbr_count_msg)).setCancelable(true)
                .setItems(new String[] {"1", "2", "3", "4" }, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        showProgressFragment();
                        new PregnancyOutcomeFatherSelectionTask(which+1).execute();
                    }
                });
        builder.show();
    }

    private class PregnancyOutcomeFatherSelectionTask extends AsyncTask<Void, Void, Individual> {

        private int liveBirthCount;

        public PregnancyOutcomeFatherSelectionTask(int liveBirthCount) {
            this.liveBirthCount = liveBirthCount;
        }

        @Override
        protected Individual doInBackground(Void... params) {
            PregnancyOutcome pregOut = locationVisit.createPregnancyOutcome(getContentResolver(), liveBirthCount);
            filledForm = formFiller.fillPregnancyOutcome(locationVisit, pregOut);
            updatable = new PregnancyOutcomeUpdate();
            final Individual father = locationVisit.determinePregnancyOutcomeFather(getContentResolver());
            return father;
        }

        @Override
        protected void onPostExecute(final Individual father) {
            hideProgressFragment();

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UpdateActivity.this);
            alertDialogBuilder.setTitle(getString(R.string.update_pregoutcome_choose_father));
            alertDialogBuilder.setCancelable(true);

            if (father != null) {
                String fatherName = father.getFullName() + " (" + father.getExtId() + ")";
                String items[] = { fatherName, getString(R.string.update_pregoutcome_search_hdss), getString(R.string.update_pregoutcome_father_not_found) };
                alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int choice) {
                        if (choice == 0) {
                            new CreatePregnancyOutcomeTask(father).execute();
                        } else if (choice == 1) {
                            // choose father
                            startFilterActivity(FILTER_BIRTH_FATHER);
                        } else if (choice == 2) {
                            new CreatePregnancyOutcomeTask(null).execute();
                        }
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.fatherNotFound), Toast.LENGTH_LONG).show();
                String items[] = { getString(R.string.update_pregoutcome_search_hdss), getString(R.string.update_pregoutcome_not_within_hdss) };
                alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int choice) {
                        if (choice == 0) {
                            startFilterActivity(FILTER_BIRTH_FATHER);
                        } else if (choice == 1) {
                            new CreatePregnancyOutcomeTask(null).execute();
                        }
                    }
                });
            }

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private class CreatePregnancyOutcomeTask extends AsyncTask<Void, Void, Void> {

        private Individual father;

        public CreatePregnancyOutcomeTask(Individual father) {
            this.father = father;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String fatherId = father == null ? "UNK" : father.getExtId();
            formFiller.appendFatherId(filledForm, fatherId);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            hideProgressFragment();
            loadSocialGroupsForIndividual();
        }
    }

    public void onDeath() {
        showProgressFragment();
        new CreateDeathTask().execute();
    }

    private class CreateDeathTask extends AsyncTask<Void, Void, Void> {

    	private SocialGroup sg;
    	
        @Override
        protected Void doInBackground(Void... params) {
        	ContentResolver resolver = getContentResolver();
        	Cursor cursor = Queries.getSocialGroupsByIndividualExtId(resolver,locationVisit.getSelectedIndividual().getExtId());
        	if (cursor.moveToFirst()) {
        		SocialGroup socialGroup = Converter.convertToSocialGroup(cursor);
        		this.sg = socialGroup;
        		locationVisit.getLocation().setHead(sg.getGroupHead());
        	}
            filledForm = formFiller.fillDeathForm(locationVisit, sg);
            
            updatable = new DeathUpdate();
            cursor.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        	hideProgressFragment();
        	if(this.sg != null){
        		deathCreation = true;
        		if(locationVisit.getSelectedIndividual().getExtId().equalsIgnoreCase(locationVisit.getLocation().getHead())){
	    			updatable = new DeathOfHoHUpdate();
	    	        Bundle bundle = new Bundle();
	    	        bundle.putString("sg", sg.getExtId());
	    	        bundle.putString("hohExtId", sg.getGroupHead());
	    	        UpdateActivity.this.getLoaderManager().restartLoader(INDIVIDUALS_IN_SOCIAL_GROUP_ACTIVE, bundle, UpdateActivity.this);
	    	        filledForm = formFiller.fillDeathOfHouseholdForm(locationVisit, sg);
	    		}
	    		else{
	    			loadForm(SELECTED_XFORM);
	    		}
        	}
    		else{
    			Toast.makeText(UpdateActivity.this, getString(R.string.create_membership), Toast.LENGTH_LONG).show();
    		}       
        }
    }

    private void loadSocialGroupsForIndividual() {
        showProgressFragment();
        getLoaderManager().restartLoader(SOCIAL_GROUP_FOR_INDIVIDUAL, null, this);
    }

    public void onClearIndividual() {
        locationVisit.setSelectedIndividual(null);
        stateMachine.transitionTo("Select Individual");
        
        if(this.menuItemForm != null) {
        	this.menuItemForm.setVisible(false);
        }
    }

    public void loadForm(final int requestCode) {
        new OdkGeneratedFormLoadTask(getBaseContext(), filledForm, new OdkFormLoadListener() {
            public void onOdkFormLoadSuccess(Uri contentUri) {
            	Cursor cursor = getCursorForFormsProvider(filledForm.getFormName());
                if (cursor.moveToFirst()) {
                    jrFormId = cursor.getString(0);
                }
                cursor.close();
                UpdateActivity.this.contentUri = contentUri;
                startActivityForResult(new Intent(Intent.ACTION_EDIT, contentUri), requestCode);
            }

            public void onOdkFormLoadFailure() {
                createXFormNotFoundDialog();
            }
        }).execute();
    }

    public void onHierarchy1() {
        locationVisit.clearLevelsBelow(0);
        stateMachine.transitionTo("Select Hierarchy 1");
        
    	ContentResolver resolver = getContentResolver();
    	Cursor cursor = null;
    	cursor = Queries.getHierarchysByLevel(resolver, vf.getSTART_HIERARCHY_LEVEL_NAME());
    	
    	if(cursor.getCount() == 1){
    		cursor.moveToNext();
    		LocationHierarchy currentLocationHierarchy = Converter.convertToHierarchy(cursor);
    		cursor.close();   
    		onHierarchy1Selected(currentLocationHierarchy);
    	}
    	else{
    		cursor.close();   
    		loadHierarchy1ValueData();
    		vf.onLoaderReset(null); 
    	}        
    }

    public void onHierarchy2() {
        locationVisit.clearLevelsBelow(1);
        stateMachine.transitionTo("Select Hierarchy 2");
        
    	ContentResolver resolver = getContentResolver();
    	Cursor cursor = null;
    	String parentExtId = locationVisit.getHierarchy1().getExtId();
    	cursor = Queries.getHierarchysByParent(resolver, parentExtId);
    	
    	if(cursor.getCount() == 1){
    		cursor.moveToNext();
    		LocationHierarchy currentLocationHierarchy = Converter.convertToHierarchy(cursor);
    		cursor.close();   
    		onHierarchy2Selected(currentLocationHierarchy);
    	}
    	else{
    		cursor.close();   
    		loadHierarchy2ValueData();
    		vf.onLoaderReset(null);
    	}       
    }
    
    public void onHierarchy3() {
        locationVisit.clearLevelsBelow(2);
        stateMachine.transitionTo("Select Hierarchy 3");
        
    	ContentResolver resolver = getContentResolver();
    	Cursor cursor = null;
    	String parentExtId = locationVisit.getHierarchy2().getExtId();
    	cursor = Queries.getHierarchysByParent(resolver, parentExtId);
    	
    	if(cursor.getCount() == 1){
    		cursor.moveToNext();
    		LocationHierarchy currentLocationHierarchy = Converter.convertToHierarchy(cursor);
    		cursor.close();   
    		onHierarchy3Selected(currentLocationHierarchy);
    	}
    	else{
    		cursor.close();   
    		loadHierarchy3ValueData();
    		vf.onLoaderReset(null);
    	}            
    }

    public void onHierarchy4() {
        locationVisit.clearLevelsBelow(3);
        stateMachine.transitionTo("Select Hierarchy 4");

    	ContentResolver resolver = getContentResolver();
    	Cursor cursor = null;
    	String parentExtId = locationVisit.getHierarchy3().getExtId();
    	cursor = Queries.getHierarchysByParent(resolver, parentExtId);
    	
    	if(cursor.getCount() == 1){
    		cursor.moveToNext();
    		LocationHierarchy currentLocationHierarchy = Converter.convertToHierarchy(cursor);
    		cursor.close();   
    		onHierarchy4Selected(currentLocationHierarchy);
    	}
    	else{
    		cursor.close();
    		loadHierarchy4ValueData();
    		vf.onLoaderReset(null);
    	}           
    }
    
    
	public void onHierarchy5() {
		 locationVisit.clearLevelsBelow(4);
	        stateMachine.transitionTo("Select Hierarchy 5");

	    	ContentResolver resolver = getContentResolver();
	    	Cursor cursor = null;
	    	String parentExtId = locationVisit.getHierarchy4().getExtId();
	    	cursor = Queries.getHierarchysByParent(resolver, parentExtId);
	    	
	    	if(cursor.getCount() == 1){
	    		cursor.moveToNext();
	    		LocationHierarchy currentLocationHierarchy = Converter.convertToHierarchy(cursor);
	    		cursor.close();   
	    		onHierarchy5Selected(currentLocationHierarchy);
	    	}
	    	else{
	    		cursor.close();
	    		loadHierarchy5ValueData();
	    		vf.onLoaderReset(null);
	    	}          
		
	}
    
	
	public void onHierarchy6() {
		 locationVisit.clearLevelsBelow(5);
	        stateMachine.transitionTo("Select Hierarchy 6");

	    	ContentResolver resolver = getContentResolver();
	    	Cursor cursor = null;
	    	String parentExtId = locationVisit.getHierarchy5().getExtId();
	    	cursor = Queries.getHierarchysByParent(resolver, parentExtId);
	    	
	    	if(cursor.getCount() == 1){
	    		cursor.moveToNext();
	    		LocationHierarchy currentLocationHierarchy = Converter.convertToHierarchy(cursor);
	    		cursor.close();   
	    		onHierarchy6Selected(currentLocationHierarchy);
	    	}
	    	else{
	    		cursor.close();
	    		loadHierarchy6ValueData();
	    		vf.onLoaderReset(null);
	    	}          
				
	}

	
	public void onHierarchy7() {
		 locationVisit.clearLevelsBelow(6);
	        stateMachine.transitionTo("Select Hierarchy 7");

	    	ContentResolver resolver = getContentResolver();
	    	Cursor cursor = null;
	    	String parentExtId = locationVisit.getHierarchy6().getExtId();
	    	cursor = Queries.getHierarchysByParent(resolver, parentExtId);
	    	
	    	if(cursor.getCount() == 1){
	    		cursor.moveToNext();
	    		LocationHierarchy currentLocationHierarchy = Converter.convertToHierarchy(cursor);
	    		cursor.close();   
	    		onHierarchy7Selected(currentLocationHierarchy);
	    	}
	    	else{
	    		cursor.close();
	    		loadHierarchy7ValueData();
	    		vf.onLoaderReset(null);
	    	}          
	}

	
	public void onHierarchy8() {
		 locationVisit.clearLevelsBelow(7);
	        stateMachine.transitionTo("Select Hierarchy 8");

	    	ContentResolver resolver = getContentResolver();
	    	Cursor cursor = null;
	    	String parentExtId = locationVisit.getHierarchy7().getExtId();
	    	cursor = Queries.getHierarchysByParent(resolver, parentExtId);
	    	
	    	if(cursor.getCount() == 1){
	    		cursor.moveToNext();
	    		LocationHierarchy currentLocationHierarchy = Converter.convertToHierarchy(cursor);
	    		cursor.close();   
	    		onHierarchy8Selected(currentLocationHierarchy);
	    	}
	    	else{
	    		cursor.close();
	    		loadHierarchy8ValueData();
	    		vf.onLoaderReset(null);
	    	}          
		
	}


    public void onLocation() {
        locationVisit.clearLevelsBelow(9);
        stateMachine.transitionTo("Select Location");
                
    	ContentResolver resolver = getContentResolver();
    	Cursor cursor = null;
    	if (levelNumbers==2) {
    		parentExtId = locationVisit.getHierarchy2().getExtId();
    	} else if (levelNumbers==3) {
        	parentExtId = locationVisit.getHierarchy3().getExtId(); 
    	} else if (levelNumbers==4) {
    		parentExtId = locationVisit.getHierarchy4().getExtId();
    	} else if (levelNumbers==5) {
    		parentExtId = locationVisit.getHierarchy5().getExtId();
    	} else if (levelNumbers==6) {
    		parentExtId = locationVisit.getHierarchy6().getExtId();
    	} else if (levelNumbers==7) {
    		parentExtId = locationVisit.getHierarchy7().getExtId();
    	} else if (levelNumbers==8) {
    		parentExtId = locationVisit.getHierarchy8().getExtId();
    	}
    	cursor = Queries.getLocationsByHierachy(resolver, parentExtId);
    	
    	if(cursor.getCount() == 1){
    		cursor.moveToNext();
    		Location location = Converter.convertToLocation(cursor);
    		onLocationSelected(location);
    	}
    	else{
    		loadLocationValueData();
    	}
    	cursor.close();
    }

    public void onRound() {
        locationVisit.clearLevelsBelow(8);
        stateMachine.transitionTo("Select Round");
//        loadRoundValueData();
        
    	ContentResolver resolver = getContentResolver();
    	Cursor cursor = null;
        cursor = Queries.allRounds(resolver);
        int rows = cursor.getCount();       
        if(rows > 0){        
        	int highestRoundNumber = -1;
        	Round latestRound = null;
        	while(cursor.moveToNext()){
        		Round currentRound = Converter.convertToRound(cursor);
        		String roundNumberString = currentRound.getRoundNumber();
        		try{
        			int currentRoundNumber = Integer.parseInt(roundNumberString);
        			if(currentRoundNumber > highestRoundNumber){
        				latestRound = currentRound;
        				highestRoundNumber = currentRoundNumber;
        			}
        		}
        		catch(NumberFormatException nfe){}
        	}   
        	
    		if(highestRoundNumber == 0){
    			Toast.makeText(this, getString(R.string.round_number_found_lbl), Toast.LENGTH_LONG).show();
    		}
    		else if(highestRoundNumber > 0){
    			onRoundSelected(latestRound);
    		}
    		else{
    			Toast.makeText(this, getString(R.string.couldnt_parse_roundnr_lbl), Toast.LENGTH_LONG).show();
    		}
        }
        else{
        	Toast.makeText(this, getString(R.string.no_round_info_found_lbl), Toast.LENGTH_LONG).show();
        }
                
        vf.onLoaderReset(null);
        cursor.close();          
    }

    public void onIndividual() {
        locationVisit.clearLevelsBelow(10);
        loadIndividualValueData();
    }

    public void onHierarchy1Selected(LocationHierarchy hierarchy) {
        locationVisit.setHierarchy1(hierarchy);
        stateMachine.transitionTo("Select Hierarchy 2");
        updateButtons(0);
        onHierarchy2();
    }

    private void registerTransitions() {
        sf.registerTransitions(stateMachine);
        ef.registerTransitions(stateMachine);
    }

    public void onHierarchy2Selected(LocationHierarchy subregion) {
        locationVisit.setHierarchy2(subregion);
        stateMachine.transitionTo("Select Hierarchy 3");
        updateButtons(1);
        if (levelNumbers==2) {
        	stateMachine.transitionTo("Select Round");
        	onRound();	
        } else {
            stateMachine.transitionTo("Select Hierarchy 3");
        	onHierarchy3();
        }   
    }
    

    public void onHierarchy3Selected(LocationHierarchy hierarchy) {
        locationVisit.setHierarchy3(hierarchy);
        stateMachine.transitionTo("Select Hierarchy 4");
        updateButtons(2);
        if (levelNumbers==3) {
        	stateMachine.transitionTo("Select Round");
        	onRound();	
        } else {
            stateMachine.transitionTo("Select Hierarchy 4");
        	onHierarchy4();
        }   
    }


    
    
    
    public void onHierarchy4Selected(LocationHierarchy village) {
        locationVisit.setHierarchy4(village);
        updateButtons(3);
        if (levelNumbers==4) {
        	stateMachine.transitionTo("Select Round");
        	onRound();	
        } else {
            stateMachine.transitionTo("Select Hierarchy 5");
        	onHierarchy5();
        }
    }
    
    
	public void onHierarchy5Selected(LocationHierarchy hierarchy5) {
        locationVisit.setHierarchy5(hierarchy5);
        updateButtons(4);
        if (levelNumbers==5) {
        	stateMachine.transitionTo("Select Round");   	
        	onRound();	
        } else {
        	stateMachine.transitionTo("Select Hierarchy 6");
        	onHierarchy6();
        }
	}

	
	public void onHierarchy6Selected(LocationHierarchy hierarchy6) {
        locationVisit.setHierarchy6(hierarchy6);
        updateButtons(5);
        if (levelNumbers==6) {
        	stateMachine.transitionTo("Select Round"); 
        	onRound();	
        } else {
        	stateMachine.transitionTo("Select Hierarchy 7");
        	onHierarchy7();
        }		
	}

	
	public void onHierarchy7Selected(LocationHierarchy hierarchy7) {
        locationVisit.setHierarchy7(hierarchy7);
        updateButtons(6);
        if (levelNumbers==7) {
        	stateMachine.transitionTo("Select Round"); 
        	onRound();	
        } else {
        	stateMachine.transitionTo("Select Hierarchy 8");
        	onHierarchy8();
        }		
	}
	

	
	public void onHierarchy8Selected(LocationHierarchy hierarchy8) {
	     locationVisit.setHierarchy8(hierarchy8);
	//        updateButtons(7);
        	onRound();	
	}
    
    
    private void updateButtons(int level){
    }

    public void onRoundSelected(Round round) {
        locationVisit.setRound(round);
        stateMachine.transitionTo("Select Location");
    }

    public void onLocationSelected(Location location) {
        locationVisit.setLocation(location);
        stateMachine.transitionTo("Create Visit");
    }

    public void onIndividualSelected(Individual individual) {
        locationVisit.setSelectedIndividual(individual);
        stateMachine.transitionTo("Select Event");
        
        if(this.menuItemForm != null) {
        	this.menuItemForm.setVisible(true);
        }
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {	
    	
    	showLoadingDialog();
    	
        Uri uri = null;
        switch (id) {
	        case SOCIAL_GROUP_AT_LOCATION:
	        case SOCIAL_GROUP_FOR_EXT_INMIGRATION:
	            uri = OpenHDS.SocialGroups.CONTENT_LOCATION_ID_URI_BASE.buildUpon()
	                    .appendPath(locationVisit.getLocation().getExtId()).build();
	            break;
	        case SOCIAL_GROUP_FOR_INDIVIDUAL:
	            uri = OpenHDS.SocialGroups.CONTENT_INDIVIDUAL_ID_URI_BASE.buildUpon()
	                    .appendPath(locationVisit.getSelectedIndividual().getExtId()).build();
	            break;
	        case INDIVIDUALS_IN_SOCIAL_GROUP:
	        {
	        	String sg = args.getString("sg");
	        	String hohExtId = args.getString("hohExtId");
	            uri = OpenHDS.IndividualGroups.CONTENT_ID_URI_BASE;
	            String where = "((" + OpenHDS.IndividualGroups.COLUMN_SOCIALGROUPUUID + " = ? ) AND (" + OpenHDS.IndividualGroups.COLUMN_INDIVIDUALUUID + " != ? ))";
	            String[] criteria = new String[] { sg, hohExtId };
	            return new CursorLoader(this, uri, null, where, criteria, null);
	        }
	        case INDIVIDUALS_IN_SOCIAL_GROUP_ACTIVE:
	        {	        	
	        	Cursor cursor = Queries.getSocialGroupsByIndividualExtId(getContentResolver(), locationVisit.getSelectedIndividual().getExtId());	        	
	        	if(cursor.moveToNext()){	        	
	        		int columnIndex = cursor.getColumnIndex("_id");
	        		int extIdIndex = cursor.getColumnIndex("extId");
	        		if(columnIndex > -1  && extIdIndex > -1) {
	        			String extId = cursor.getString(extIdIndex);
	        			cursor.close();
	        			
	        			uri = OpenHDS.Individuals.CONTENT_SG_ACTIVE_URI_BASE.buildUpon().appendPath(extId).build();
	        			String where = "s." + OpenHDS.Individuals.COLUMN_INDIVIDUAL_EXTID + " != ?";
	    	            String[] criteria = new String[] { locationVisit.getSelectedIndividual().getExtId() };	    	            
	    	            return new CursorLoader(this, uri, null, where, criteria, null);
	        		}
	        	}       	
	        }
        }

        return new CursorLoader(this, uri, null, null, null, null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {      
    	
    	hideProgressFragment();
    	
    	restoreState();
    	
    	dismissLoadingDialog();
    	    	
        if(loader.getId() == -1 ) return;
        
        if (cursor.getCount() == 1 && loader.getId() == SOCIAL_GROUP_FOR_INDIVIDUAL) {
            cursor.moveToFirst();
            appendSocialGroupFromCursor(cursor);
            return;
        } else if(loader.getId() == SOCIAL_GROUP_AT_LOCATION){
        	handleSocialGroup(loader, cursor);
        }
        
        else if(loader.getId() == INDIVIDUALS_IN_SOCIAL_GROUP_ACTIVE){       
	        if(cursor.moveToNext()){
		        List<String> uniqueExtIds = new ArrayList<String>();     
		        List<Individual> uniqueIndividuals = new ArrayList<Individual>();
		        
	        	while(!cursor.isAfterLast()){
	        		String individualExtId = cursor.getString(cursor.getColumnIndex(OpenHDS.IndividualGroups.COLUMN_INDIVIDUALUUID));
	        		
					if(!uniqueExtIds.contains(individualExtId)){
						uniqueExtIds.add(individualExtId);
		        		Cursor individualCursor = Queries.getIndividualByExtId(this.getContentResolver(), individualExtId);
		        		if(individualCursor.moveToNext()){
	        				Individual individual = Converter.convertToIndividual(individualCursor);
	        				if(individual != null && individualMeetsMinimumAge(individual)){
	        					uniqueIndividuals.add(individual);
	        				}
		        		}
		        		individualCursor.close();		        		
					}
	        		cursor.moveToNext();
	        	}
	        	   	
	        	final List<Individual> list = uniqueIndividuals; 
        		@SuppressWarnings({ "unchecked", "rawtypes" })
				ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, list) {
      			  @Override
      			  public View getView(int position, View convertView, android.view.ViewGroup parent) {
      			    View view = super.getView(position, convertView, parent);
      			    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
      			    TextView text2 = (TextView) view.findViewById(android.R.id.text2);

      			    text1.setTextColor(Color.BLACK);
      			    text1.setText(list.get(position).getFirstName() + " " +list.get(position).getLastName());
      			    text2.setText("(" + list.get(position).getExtId() + ")");
      			    return view;
      			  }
      			};
    	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	        builder.setTitle(getString(R.string.pls_select_new_hoh_lbl));
    	        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
    	
    	            public void onClick(DialogInterface dialog, int which) {
    	            	ListView lw = ((AlertDialog)dialog).getListView();    	            	
    	            	Object checkedItem = lw.getItemAtPosition(which);
    	            	
    	            	Individual newHoh = null;
    	            	List<Individual> members = new ArrayList<Individual>();
    	            	
    	            	if(checkedItem instanceof Individual){
    	            		newHoh = (Individual)checkedItem;
    	            	}
    	            	
    	            	ListAdapter listAdapter = lw.getAdapter();
    	            	for(int i = 0; i < listAdapter.getCount();i++){
    	            		Object obj = listAdapter.getItem(i);
    	            		if(obj instanceof Individual){
    	            			Individual member = (Individual)obj;
    	            			if(newHoh != null && !newHoh.getExtId().equalsIgnoreCase(member.getExtId())){
    	            				members.add(member);
    	            			}
    	            		}
    	            	}   	            	
	            		selectedNewHoh(newHoh, members);
    	            }
    	        });
    	        builder.setNegativeButton(getString(R.string.cancel_lbl), new DialogInterface.OnClickListener() {
    	        	public void onClick(DialogInterface dialog, int which) {
    	        		deathCreation = false;
    	        	}
    	        });
    	        AlertDialog dlg = builder.create();
    	        dlg.show();      
	        }
	        else{    	        
            	ContentResolver resolver = getContentResolver();
            	Cursor c_cursor = Queries.getSocialGroupsByIndividualExtId(resolver,locationVisit.getSelectedIndividual().getExtId());
            	SocialGroup socialGroup = null;
            	if (c_cursor.moveToFirst()) {
            		socialGroup = Converter.convertToSocialGroup(c_cursor);
            		locationVisit.getLocation().setHead(socialGroup.getGroupHead());
            	}
            	
                filledForm = formFiller.fillDeathForm(locationVisit, socialGroup);    
                updatable = new DeathUpdate();
                c_cursor.close();
                
                loadForm(SELECTED_XFORM);
	        }
	        
	        getLoaderManager().destroyLoader(loader.getId());
        }
        else{
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle(getString(R.string.update_load_finished_select_hh_msg));
	        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor,
	                new String[] { OpenHDS.SocialGroups.COLUMN_SOCIALGROUP_GROUPNAME,
	                        OpenHDS.SocialGroups.COLUMN_SOCIALGROUP_EXTID }, new int[] { android.R.id.text1,
	                        android.R.id.text2 }, 0);
	        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
	
	            public void onClick(DialogInterface dialog, int which) {
	                Cursor cursor = (Cursor) householdDialog.getListView().getItemAtPosition(which);
	                appendSocialGroupFromCursor(cursor);
	            }
	        });
	        builder.setNegativeButton(getString(R.string.cancel_lbl), null);
	        householdDialog = builder.create();
	        householdDialog.show();
        }
    }
    
    private boolean individualMeetsMinimumAge(Individual indiv) {
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date dob = formatter.parse(indiv.getDob());
            Calendar cal = Calendar.getInstance();
            cal.setTime(dob);
            if ((new GregorianCalendar().get(Calendar.YEAR) - cal.get(Calendar.YEAR)) > MINIMUM_HOUSEHOLD_AGE) {
                return true;
            }
        } catch (Exception e) {
            // no dob or malformed
            return true;
        }

        return false;
    }    
    
    private void selectedNewHoh(final Individual newHoh, final List<Individual> members){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.update_load_finished_select_hh_msg));
        if(newHoh != null)
        	builder.setMessage(getString(R.string.selected_new_hoh_lbl)  + " " + newHoh.getFirstName() + " " + newHoh.getLastName() +  " " + getString(R.string.with_extid_lbl)  + " " + newHoh.getExtId());
        builder.setNegativeButton(getString(R.string.cancel_lbl), new DialogInterface.OnClickListener() {
        	public void onClick(DialogInterface dialog, int id) {
        		deathCreation = false;
        	}
        });
        builder.setPositiveButton(R.string.continue_lbl, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            	filledForm.setIndividualA(newHoh.getExtId());
            	filledForm.setHouseHoldMembers(members);
            	loadForm(SELECTED_XFORM);
            }
        });
        householdDialog = builder.create();
        householdDialog.show();  
    }

    private void appendSocialGroupFromCursor(Cursor cursor) {
        SocialGroup sg = Converter.convertToSocialGroup(cursor);
        filledForm = formFiller.appendSocialGroup(sg, filledForm);
        loadForm(SELECTED_XFORM);
    }

    public void onLoaderReset(Loader<Cursor> arg0) {
        if (householdDialog != null)
    	householdDialog.dismiss();
        householdDialog = null;
    }

	public void onFilterLocation() {
		startFilterLocActivity(FILTER_LOCATION);	
		
	}
    private Cursor getCursorForFormsProvider(String name) {
    	ContentResolver resolver = getContentResolver();
        return resolver.query(FormsProviderAPI.FormsColumns.CONTENT_URI, new String[] {
                FormsProviderAPI.FormsColumns.JR_FORM_ID, FormsProviderAPI.FormsColumns.FORM_FILE_PATH },
                FormsProviderAPI.FormsColumns.JR_FORM_ID + " like ?", new String[] { name + "%" }, null);
    }

	public void handleResult(Entity entity) {
		if(entity == Entity.INDIVIDUAL){
			vf.selectItemNoInList(0);
		}
	}

	 private void handleSocialGroup(Loader<Cursor> loader, Cursor cursor){	
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	
	    	if(cursor.getCount() > 0){
	    		builder.setTitle(getString(R.string.select_household_lbl));
	        	SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor,
	        			new String[] { OpenHDS.SocialGroups.COLUMN_SOCIALGROUP_GROUPNAME,
	        			OpenHDS.SocialGroups.COLUMN_SOCIALGROUP_EXTID }, new int[] { android.R.id.text1,
	        			android.R.id.text2 }, 0 )
	        			{
	        			    @Override
	        			    public View getView(int position, View convertView, ViewGroup parent)
	        			    {
	        			        final View row = super.getView(position, convertView, parent);
	        			        TextView text1 = (TextView) row.findViewById(android.R.id.text1);
	              			    TextView text2 = (TextView) row.findViewById(android.R.id.text2);
	              			  
	              			    text1.setTextColor(Color.BLACK);
	              			    text2.setTextColor(Color.BLACK);
	        			        if (position % 2 == 0)
	        			            row.setBackgroundResource(android.R.color.darker_gray);
	        			        else
	        			            row.setBackgroundResource(android.R.color.background_light);
	        			        return row;
	        			    }
	        			};
	        	
	      
	        	
	        	builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
	        			public void onClick(DialogInterface dialog, int which) {
	        				Cursor cursor = (Cursor) householdDialog.getListView().getItemAtPosition(which);
	        				appendSocialGroupFromCursor(cursor);
	        			}
	        	});     		
	    	}
	    	else{
	    		builder.setTitle(getString(R.string.select_household_lbl));
	    		builder.setMessage(getString(R.string.search_for_household_lbl));
	    	}
	    	
	    	builder.setNegativeButton(getString(R.string.cancel_lbl),new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing
					dialog.cancel();
				}
			});   	
	    	builder.setPositiveButton(getString(R.string.create_lbl), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					onHousehold();
				}
			});           	
			builder.setNeutralButton(getString(R.string.search_lbl), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					searchSocialGroup();
				}
			});           	
	        householdDialog = builder.create();
	        householdDialog.show();    	
	    }
	 
	    private void searchSocialGroup(){ 	
	    	startFilterActivity(FILTER_SOCIALGROUP);
	    }

		@Override
		public void onHouseDetails() {
			showProgressFragment();
			filledForm = new FilledForm("location_details");
			buildSubstituteHeadDialog();
	        //new CreateHouseDetailsTask().execute(); 		
		}

		@Override
		public void onIndividualDetails() {
			showProgressFragment();
			updatable = new IndividualDetailsUpdate();
			createIndivDetails = 1;
			filledForm = formFiller.fillExtraForm(locationVisit, "individual_details", null);
			formFiller.addParents(filledForm, getContentResolver(), locationVisit.getSelectedIndividual().getExtId());
			formFiller.addSpouse(filledForm, getContentResolver(), locationVisit.getSelectedIndividual().getExtId());
	        //new CreateIndividualDetailsTask().execute();
			buildUpdateParentsDialog();
		}
		
		private class CreateHouseDetailsTask extends AsyncTask<Void, Void, Boolean> {
			private String errorMessage = "";
			
	        @Override
	        protected Boolean doInBackground(Void... params) {
	                    	        	
	        	ContentResolver resolver = UpdateActivity.this.getContentResolver();	              	
	        		        	
	        	SocialGroup sg = null;
	        	Cursor cursor = Queries.getSocialGroupByName(resolver,locationVisit.getLocation().getName());
	        	
	        	if (cursor.moveToFirst()) {
	        		sg = Converter.convertToSocialGroup(cursor);
	        		locationVisit.getLocation().setHead(sg.getGroupHead());	        		
	        	}
	        	
	        	if (sg == null){
	        		errorMessage = "NO_SOCIAL_GROUP";
	        		return false;
	        	}	        	        
	        	
	        	cursor.close();
	        	
	        	formFiller.addGroupHead(filledForm, resolver, sg);
	        	 
	        	filledForm.setFieldWorkerId(locationVisit.getFieldWorker().getExtId());
	        	
	        	filledForm.setVisitDate(locationVisit.getVisit().getDate());
	            filledForm.setVisitExtId(locationVisit.getVisit().getExtId());
	            filledForm.setLocationName(locationVisit.getLocation().getName());	            
	            filledForm.setLocationId(locationVisit.getLocation().getExtId());
	            
	            filledForm.setRoundNumber(locationVisit.getRound().getRoundNumber());	            
	            filledForm.setHierarchyId(locationVisit.getLatestLevelExtId());	                        
	            
	            return true;
	        }
	        
	        @Override
	        protected void onPostExecute(Boolean result) {
	        	if (result){
	        		hideProgressFragment();
	        		createHouseDetails = 1;
	        		loadForm(SELECTED_XFORM);
	        	}else{
	        		//There's no head of household
	        		createCantCreateHouseDetails();
	        	}
	        }
	    }
		
		private class CreateIndividualDetailsTask extends AsyncTask<Void, Void, Boolean> {
			private String errorMessage = "";
			
	        @Override
	        protected Boolean doInBackground(Void... params) {
	                    	        	
	        	//ContentResolver resolver = UpdateActivity.this.getContentResolver();	                	
	        	        	
	        	//formFiller.addParents(filledForm, resolver, locationVisit.getSelectedIndividual().getExtId());
	        	//formFiller.addSpouse(filledForm, resolver, locationVisit.getSelectedIndividual().getExtId());
	        	
	            return true;
	        }
	        
	        @Override
	        protected void onPostExecute(Boolean result) {
	        	if (result){
	        		hideProgressFragment();
	        		loadForm(SELECTED_XFORM);
	        	}	        	
	        }
	    }
		
		private void buildUpdateParentsDialog() {
	        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	        alertDialogBuilder.setTitle("Atualização dos Pais");
	        alertDialogBuilder.setMessage("Deseja atualizar os Pais, deste individuo?");
	        alertDialogBuilder.setCancelable(true);
	        alertDialogBuilder.setPositiveButton(getString(R.string.yes_lbl), new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	                //Select the mother
	            	buildMotherDialog();
	            }
	        });
	        alertDialogBuilder.setNegativeButton(getString(R.string.no_lbl), new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	new CreateIndividualDetailsTask().execute();
	            }
	        });
	        AlertDialog alertDialog = alertDialogBuilder.create();
	        alertDialog.show();
	    }	
		
	    private void createCantCreateHouseDetails() {	        
	        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	          alertDialogBuilder.setTitle("Detalhes da casa");
	          alertDialogBuilder.setMessage("Não será possivel abrir criar os detalhes da casa, porque o chefe do agregado ainda não foi adicionado a esta casa!");
	          alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {  
	            	//selectIndividual();
	            	//stateMachine.transitionTo("Select Individual");
	            	restoreState();
	            }
	        });
	        AlertDialog alertDialog = alertDialogBuilder.create();
	        alertDialog.show(); 
	    }
	    
	    private void buildSubstituteHeadDialog() {
	        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	        alertDialogBuilder.setTitle(getString(R.string.substitute_head_lbl));
	        alertDialogBuilder.setMessage(getString(R.string.substitute_head_exists_lbl));
	        alertDialogBuilder.setCancelable(true);
	        alertDialogBuilder.setPositiveButton(getString(R.string.yes_lbl), new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	filledForm.setHasSubsHead("1");	            	
	            	//Select Substitute Head
	            	selectSubstituteHeadDialog();
	            }
	        });
	        alertDialogBuilder.setNegativeButton(getString(R.string.no_lbl), new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	                filledForm.setHasSubsHead("2");
	            	//load House Details
	                new CreateHouseDetailsTask().execute();
	            }
	        });
	        AlertDialog alertDialog = alertDialogBuilder.create();
	        alertDialog.show();
	    }
	    
	    private void selectSubstituteHeadDialog(){
	    	List<String> uniquePermIds = new ArrayList<String>();     
	        List<Individual> uniqueIndividuals = new ArrayList<Individual>();
	        
	        
	        
	        SocialGroup sg = null;
        	Cursor cursorSg = Queries.getSocialGroupByName(getContentResolver(), locationVisit.getLocation().getName());
        	
        	if (cursorSg.moveToFirst()) {
        		sg = Converter.convertToSocialGroup(cursorSg);        		 		
        	}        	
        	cursorSg.close();
	        
        	
        	Cursor cursor = Queries.getIndividualsByResidency(getContentResolver(), locationVisit.getLocation().getExtId());
        	
        	List<Individual> individuals = Converter.toIndividualList(cursor);
        	
        	for (Individual individual : individuals){
        		
        		Log.d("indiv", individual+"");
        		
        		if (individual != null){
                    Log.d("indiv", individual.getFirstName());        			
        			//Dont add the current head of household
        			if (individual.getExtId().equalsIgnoreCase(sg.getGroupHead())){
        				continue;
        			}
        			
        			if (!individual.getEndType().equals("DTH") && !individual.getEndType().equals("EXT") && individualMeetsMinimumAge(individual)){
        				uniquePermIds.add(individual.getLastName());        				
        				uniqueIndividuals.add(individual);
        			}
        			
        		}
        		
        		cursor.moveToNext();
        	}
        	   	
        	cursor.close();
        	cursorSg.close();
        	
        	final List<Individual> list = uniqueIndividuals; 
    		@SuppressWarnings({ "unchecked", "rawtypes" })
			ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, list) {
  			  @Override
  			  public View getView(int position, View convertView, android.view.ViewGroup parent) {
  			    View view = super.getView(position, convertView, parent);
  			    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
  			    TextView text2 = (TextView) view.findViewById(android.R.id.text2);

  			    text1.setTextColor(Color.BLACK);
  			    text1.setText(list.get(position).getFirstName());
  			    text2.setTextColor(Color.DKGRAY);
  			    text2.setText("(" + list.get(position).getLastName() + ")");
  			    return view;
  			  }
  			};
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle(getString(R.string.substitute_head_select_lbl));
	        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
	
	            public void onClick(DialogInterface dialog, int which) {
	            	ListView lw = ((AlertDialog)dialog).getListView();    	            	
	            	Object checkedItem = lw.getItemAtPosition(which);
	            	
	            	Individual subsHead = null;
	            		            	
	            	if(checkedItem instanceof Individual){
	            		subsHead = (Individual)checkedItem;
	            	}
	            		            	   	            	
            		selectedSubstituteHead(subsHead);
	            }
	        });
	        builder.setNegativeButton(getString(R.string.cancel_lbl), new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int which) {
	        		createHouseDetails = 0;
	        	}
	        });
	        AlertDialog dlg = builder.create();
	        dlg.show();  
	    }
	    
	    private void selectedSubstituteHead(final Individual subsHead){
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle(getString(R.string.substitute_head_selected_lbl));
	        
	        if(subsHead != null){
	        	builder.setMessage(subsHead.getFirstName() + " <" + subsHead.getLastName() +  "> ");
	        }
	        
	        builder.setNegativeButton(getString(R.string.cancel_lbl), new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int id) {
	        		createHouseDetails = 0;
	        	}
	        });
	        builder.setPositiveButton(R.string.continue_lbl, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	            	filledForm.setSubsHeadName(subsHead.getFirstName());
	            	filledForm.setSubsHeadPermId(subsHead.getLastName());	            	
	            	//load House Details
	                new CreateHouseDetailsTask().execute();
	            }
	        });
	        householdDialog = builder.create();
	        householdDialog.show();  
	    }

		@Override
		public void onChangeHouseholdHead() {
			showProgressFragment();
			filledForm = new FilledForm("change_household_head");
			selectNewHouseholdHeadDialog();
	        //new CreateHouseDetailsTask().execute();
		}
		
		private void selectNewHouseholdHeadDialog(){
	    	List<String> uniquePermIds = new ArrayList<String>();     
	        List<Individual> uniqueIndividuals = new ArrayList<Individual>();
	        
	        
	        
	        SocialGroup sg = null;
        	Cursor cursorSg = Queries.getSocialGroupByName(getContentResolver(), locationVisit.getLocation().getName());
        	
        	if (cursorSg.moveToFirst()) {
        		sg = Converter.convertToSocialGroup(cursorSg);        		 		
        	}        	
        	cursorSg.close();
	        
        	
        	Cursor cursor = Queries.getIndividualsByResidency(getContentResolver(), locationVisit.getLocation().getExtId());
        	
        	List<Individual> individuals = Converter.toIndividualList(cursor);
        	
        	for (Individual individual : individuals){
        		
        		Log.d("indiv", individual+"");
        		
        		if (individual != null){
                    Log.d("indiv", individual.getFirstName());        			
        			//Dont add the current head of household
        			if (individual.getExtId().equalsIgnoreCase(sg.getGroupHead())){
        				continue;
        			}
        			
        			if (!individual.getEndType().equals("DTH") && !individual.getEndType().equals("EXT") && individualMeetsMinimumAge(individual)){
        				uniquePermIds.add(individual.getLastName());        				
        				uniqueIndividuals.add(individual);
        			}
        			
        		}
        		
        		cursor.moveToNext();
        	}
        	   	
        	cursor.close();
        	cursorSg.close();
        	
        	final List<Individual> list = uniqueIndividuals; 
    		@SuppressWarnings({ "unchecked", "rawtypes" })
			ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, list) {
  			  @Override
  			  public View getView(int position, View convertView, android.view.ViewGroup parent) {
  			    View view = super.getView(position, convertView, parent);
  			    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
  			    TextView text2 = (TextView) view.findViewById(android.R.id.text2);

  			    text1.setTextColor(Color.BLACK);
  			    text1.setText(list.get(position).getFirstName());
  			    text2.setTextColor(Color.DKGRAY);
  			    text2.setText("(" + list.get(position).getLastName() + ")");
  			    return view;
  			  }
  			};
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle(getString(R.string.change_household_head_select_lbl));
	        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
	
	            public void onClick(DialogInterface dialog, int which) {
	            	ListView lw = ((AlertDialog)dialog).getListView();    	            	
	            	Object checkedItem = lw.getItemAtPosition(which);
	            	
	            	Individual newHead = null;
	            	List<Individual> members = new ArrayList<Individual>();
	            		            	
	            	if(checkedItem instanceof Individual){
	            		newHead = (Individual)checkedItem;
	            	}
	            	
	            	ListAdapter listAdapter = lw.getAdapter();
	            	for(int i = 0; i < listAdapter.getCount();i++){
	            		Object obj = listAdapter.getItem(i);
	            		if(obj instanceof Individual){
	            			Individual member = (Individual)obj;
	            			if(newHead != null && !newHead.getExtId().equalsIgnoreCase(member.getExtId())){
	            				members.add(member);
	            			}
	            		}
	            	}   	    
	            		            	   	            	
            		selectedNewHouseholdHead(newHead, members);
	            }
	        });
	        builder.setNegativeButton(getString(R.string.cancel_lbl), new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int which) {
	        		changingHouseholdHead = 0;
	        	}
	        });
	        AlertDialog dlg = builder.create();
	        dlg.show();  
	    }
		
		private void selectedNewHouseholdHead(final Individual newHead, final List<Individual> members){
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle(getString(R.string.change_household_head_selected_lbl));
	        
	        if(newHead != null){
	        	builder.setMessage(newHead.getFirstName() + " <" + newHead.getLastName() +  "> ");
	        }
	        
	        builder.setNegativeButton(getString(R.string.cancel_lbl), new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int id) {
	        		changingHouseholdHead = 0;
	        	}
	        });
	        builder.setPositiveButton(R.string.continue_lbl, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	            	//filledForm.setSubsHeadName(newHead.getFirstName());
	            	//filledForm.setSubsHeadPermId(newHead.getLastName());
	            	//we are imittating the death of hoh, in that case the new household head is individualA
	            	filledForm.setIndividualA(newHead.getExtId());
	            	filledForm.setIndividualFirstName(newHead.getFirstName());
	            	filledForm.setHouseHoldMembers(members);
	            	
	            	//load House Details
	                new CreateChangeHouseholdHeadTask().execute();
	            }
	        });
	        householdDialog = builder.create();
	        householdDialog.show();  
	    }
		
		private class CreateChangeHouseholdHeadTask extends AsyncTask<Void, Void, Boolean> {
			private String errorMessage = "";
			
	        @Override
	        protected Boolean doInBackground(Void... params) {
	                    	        	
	        	ContentResolver resolver = UpdateActivity.this.getContentResolver();	              	
	        		        	
	        	SocialGroup sg = null;
	        	Cursor cursor = Queries.getSocialGroupByName(resolver,locationVisit.getLocation().getName());
	        	
	        	if (cursor.moveToFirst()) {
	        		sg = Converter.convertToSocialGroup(cursor);
	        		locationVisit.getLocation().setHead(sg.getGroupHead());	        		
	        	}
	        	
	        	if (sg == null){
	        		errorMessage = "NO_SOCIAL_GROUP";
	        		return false;
	        	}	        	        
	        	
	        	cursor.close();
	        	
	        	filledForm.setHouseholdId(sg.getExtId());
	        	filledForm.setHouseholdName(sg.getGroupName());
	        	filledForm.setGroupHeadId(sg.getGroupHead());
	        	 //addHousehold(sg, form);
	        	filledForm.setFieldWorkerId(locationVisit.getFieldWorker().getExtId());
	        	
	        	filledForm.setVisitDate(locationVisit.getVisit().getDate());
	            filledForm.setVisitExtId(locationVisit.getVisit().getExtId());
	            filledForm.setLocationName(locationVisit.getLocation().getName());	            
	            filledForm.setLocationId(locationVisit.getLocation().getExtId());
	            
	            filledForm.setRoundNumber(locationVisit.getRound().getRoundNumber());	            
	            filledForm.setHierarchyId(locationVisit.getLatestLevelExtId());	                        
	            
	            return true;
	        }
	        
	        @Override
	        protected void onPostExecute(Boolean result) {
	        	if (result){
	        		hideProgressFragment();
	        		changingHouseholdHead = 1;
	        		loadForm(SELECTED_XFORM);
	        	}else{
	        		//There's no head of household
	        		createCantChangeHouseholdHead();
	        	}
	        }
	    }
		
		private void createCantChangeHouseholdHead() {	        
	        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	          alertDialogBuilder.setTitle("Mudança de chefe de agregado");
	          alertDialogBuilder.setMessage("Não será possivel abrir mudar de chefe do agregado!");
	          alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {  
	            	//selectIndividual();
	            	//stateMachine.transitionTo("Select Individual");
	            	restoreState();
	            }
	        });
	        AlertDialog alertDialog = alertDialogBuilder.create();
	        alertDialog.show(); 
	    }

		@Override
		public void onImunization() {
			showProgressFragment();
			new CreateImunizationTask().execute();
		}
	    
		private class CreateImunizationTask extends AsyncTask<Void, Void, Boolean> {
			private String errorMessage = "";			
			private String lastContentUri = "";
			
	        @Override
	        protected Boolean doInBackground(Void... params) {
	                    	        	
	        	ContentResolver resolver = UpdateActivity.this.getContentResolver();
	        	
	        	filledForm = formFiller.fillExtraForm(locationVisit, "imunization", null);	
	        	
	        	Imunization im = null;	        	
	        	
	        	org.openhds.mobile.dss.database.Database db = new org.openhds.mobile.dss.database.Database(UpdateActivity.this);
	    		db.open();
	    		
	    		Cursor cursor = db.query(Imunization.class, ImunizationTable.COLUMN_INDIVIDUAL_ID + " = ?", new String[] { filledForm.getIndividualExtId() }, null, null, null);
	    		
	    		if (cursor != null && cursor.moveToFirst()){ //If already exists a pregnancy_id	    			
	    			im = org.openhds.mobile.dss.database.Converter.cursorToImunization(cursor);	  
	    			createImunizationDetails = 1; //creating imunization using pre-saved data
	    		}else{     			
	    			im = Imunization.emptyImunization();
	    			createImunizationDetails = 2; //creating imunization using new form	    			    			
	    		}    			    		
	    		if (cursor != null) cursor.close();
	    		db.close();
	        	
	        	
	        	        	
	        	formFiller.addParents(filledForm, resolver, locationVisit.getSelectedIndividual().getExtId());
	        	
	        	Log.d("last_content_uri", ""+im.getLastContentUri()+", imu_details="+createImunizationDetails);
	        	//Try to load last opened form
	        	if (im.getLastContentUri() != null && !im.getLastContentUri().isEmpty()){
	        		lastContentUri = im.getLastContentUri();
	        		Log.d("is going to load", "last created form");
	        		return true;
	        	}
	        	
	        	
	        	
	        	filledForm.addExtraParam("hasAllVaccines", im.getVacsOnDatabase().isEmpty() ? "2" : im.getVacsOnDatabase());
	        	filledForm.addExtraParam("vaccinesOnDatabase", im.getVacsOnDatabase().isEmpty() ? "2" : im.getVacsOnDatabase());
	        	
	        	filledForm.addExtraParam("hasRegisteredBcg", im.getVacBcg().isEmpty() ? "2":"1" );
	        	filledForm.addExtraParam("receivedBcg", im.getVacBcg().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedBcgDate", im.getVacBcg());
	        	
	        	filledForm.addExtraParam("hasRegisteredPd0", im.getVacPolioDose0().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedPd0", im.getVacPolioDose0().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedPd0Date", im.getVacPolioDose0());
	        	
	        	
	        	filledForm.addExtraParam("hasRegisteredPd1", im.getVacPolioDose1().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedPd1", im.getVacPolioDose1().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedPd1Date", im.getVacPolioDose1());
	        	
	        	
	        	filledForm.addExtraParam("hasRegisteredPd2", im.getVacPolioDose2().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedPd2", im.getVacPolioDose2().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedPd2Date", im.getVacPolioDose2());
	        	
	        	
	        	filledForm.addExtraParam("hasRegisteredPd3", im.getVacPolioDose3().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedPd3", im.getVacPolioDose3().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedPd3Date", im.getVacPolioDose3());
	        	
	        	
	        	filledForm.addExtraParam("hasRegisteredDpt1", im.getVacDptDose1().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedDpt1", im.getVacDptDose1().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedDpt1Date", im.getVacDptDose1());
	        	
	        	filledForm.addExtraParam("hasRegisteredDpt2", im.getVacDptDose2().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedDpt2", im.getVacDptDose2().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedDpt2Date", im.getVacDptDose2());
	        	
	        	filledForm.addExtraParam("hasRegisteredDpt3", im.getVacDptDose3().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedDpt3", im.getVacDptDose3().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedDpt3Date", im.getVacDptDose3());
	        	
	        	filledForm.addExtraParam("hasRegisteredPcv1", im.getVacPcv10Dose1().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedPcv1", im.getVacPcv10Dose1().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedPcv1Date", im.getVacPcv10Dose1());
	        	
	        	
	        	filledForm.addExtraParam("hasRegisteredPcv2", im.getVacPcv10Dose2().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedPcv2", im.getVacPcv10Dose2().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedPcv2Date", im.getVacPcv10Dose2());
	        	
	        	filledForm.addExtraParam("hasRegisteredPcv3", im.getVacPcv10Dose3().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedPcv3", im.getVacPcv10Dose3().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedPcv3Date", im.getVacPcv10Dose3());
	        	
	        	filledForm.addExtraParam("hasRegisteredSmp", im.getVacSarampo().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedSmp", im.getVacSarampo().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedSmpDate", im.getVacSarampo());
	        	
	        	filledForm.addExtraParam("hasRegisteredRv1", im.getVacRotavirusDose1().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedRv1", im.getVacRotavirusDose1().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedRv1Date", im.getVacRotavirusDose1());
	        	
	        	filledForm.addExtraParam("hasRegisteredRv2", im.getVacRotavirusDose2().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedRv2", im.getVacRotavirusDose2().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedRv2Date", im.getVacRotavirusDose2());
	        	
	        	filledForm.addExtraParam("hasRegisteredRv3", im.getVacRotavirusDose3().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedRv3", im.getVacRotavirusDose3().isEmpty() ? "2":"1");
	        	filledForm.addExtraParam("receivedRv3Date", im.getVacRotavirusDose3());
	        	
	        	filledForm.addExtraParam("vitaminaATotal", im.getVacVitaminaATotal());
	        	filledForm.addExtraParam("mebendazolTotal", im.getVacMebendazolTotal());
	        	filledForm.addExtraParam("othersTotal", im.getVacOthersTotal());
	        	
	            return true;
	        }
	        
	        @Override
	        protected void onPostExecute(Boolean result) {
	        	if (result){
	        		hideProgressFragment();
	        		Log.d("lastContentURi", ""+lastContentUri);
	        		if (lastContentUri == null || lastContentUri.isEmpty()){
	        			loadForm(SELECTED_XFORM);
	        		}else {
	        			loadLastCreatedForm();
	        		}
	        	}	        	
	        }
	        
	        private void loadLastCreatedForm() {
	    		String strUri = lastContentUri;
	    		contentUri = Uri.parse(strUri);
	    		
	    		Log.d("load_last_odk", ""+lastContentUri);
	    		
	    		startActivityForResult(new Intent(Intent.ACTION_EDIT, contentUri), 2);
	    	}
	    }
}
