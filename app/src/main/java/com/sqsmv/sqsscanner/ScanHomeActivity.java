package com.sqsmv.sqsscanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sqsmv.sqsscanner.DB.ProductDataSource;
import com.sqsmv.sqsscanner.DB.ScanDataSource;
import com.sqsmv.sqsscanner.DB.UPCDataSource;
import com.socketmobile.apiintegration.ScanAPIApplication;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * @author ChrisS
 *
 */
@SuppressLint("ShowToast")
public class ScanHomeActivity extends Activity
{
	public static final String DEBUG = "SQS";
	public static boolean ERROR_SCANS;
	private static final String TAG = "ScanHomeActivity";

	private ScanRecord currentRecord;

	// Config info
	public static boolean isAutoScan;
	public static int autoVal;
	public static int msgTime;
	public int boxQty;
	public boolean isBoxQty;
    private String lensId;

	public static Pattern sqsRegEx;
	public static Pattern upcRegEx;

	private boolean ERR_SCAN;

	private String thisMasNum;

	private SharedPreferences config;

	private boolean isMasNum = false;
	public boolean isManQty;
	public boolean isNewProduct;
	
	
	private String mark = "";
	private Toast titleMessage;
	private EditText mScanId;
	private EditText mPullNum;
	private TextView mTitle;
	private TextView mPriceList;
	private TextView mPriceFilters;
	private EditText quantity;
	private TextView recordCount;
	private TextView pullLines;
	private TextView pullPieceCount;
	private TextView mRating;
	//private LinearLayout mSkidMode;

	private TextView titleCount;

	private InputMethodManager imm;

	private ProductDataSource productDataSource;
	private UPCDataSource upcDataSource;
	private ScanDataSource scanDataSource;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String message = String.format("in onCreate");
		Log.d(TAG, message);
		
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_scan_home);

		this.productDataSource = new ProductDataSource(this);
		this.upcDataSource = new UPCDataSource(this);
		this.scanDataSource = new ScanDataSource(this);

		upcRegEx = Pattern.compile(getString(R.string.upcRegEx));
		sqsRegEx = Pattern.compile(getString(R.string.sqsRegEx));

		// find all the views
		//mSkidMode = (LinearLayout) findViewById(R.id.skidmode);
		mScanId = (EditText) findViewById(R.id.scanId);
		mPullNum = (EditText) findViewById(R.id.pullNum);
		mRating = (TextView) findViewById(R.id.rating);
		quantity = (EditText) findViewById(R.id.qtyNum);
		mTitle = (TextView) findViewById(R.id.title);
		mPriceList = (TextView) findViewById(R.id.priceList);
		mPriceFilters = (TextView) findViewById(R.id.priceFilters);
		recordCount = (TextView) findViewById(R.id.count);
		pullLines = (TextView) findViewById(R.id.runPullCount);
		pullPieceCount = (TextView) findViewById(R.id.totalCount);
		titleCount = (TextView) findViewById(R.id.totalTitleCount);

		config = getSharedPreferences("scanConfig", 0);


		titleMessage = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		titleMessage.setGravity(Gravity.BOTTOM, 5, 5);

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		//handler for entering data into pull number field
		mPullNum.setOnEditorActionListener(new OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				if(actionId == EditorInfo.IME_ACTION_DONE && v.getText().length() > 0)
				{
					imm.hideSoftInputFromWindow(mPullNum.getWindowToken(), 0);
					if (v.getText().toString().equals("1"))
					{
						goToAdmin();
					}
					setPullNumbers();
					return true;
				}
				return false;
			}
		});

		//handler for entering data into quantity field
		quantity.setOnEditorActionListener(new OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				if (actionId == EditorInfo.IME_ACTION_DONE)
				{
					if (mPullNum.getText().toString().equals("1"))
					{
						goToAdmin();
					}
					else if(mScanId.getText().length() == 0)
					{
						mScanId.requestFocus();
						quantity.clearFocus();
					}
					else if(quantity.getText().length() > 0)
					{
						if(mPullNum.getText().length() == 0)
						{
							mPullNum.requestFocus();
							quantity.clearFocus();
						}
						else
						{
							imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

							commitRecord();
							return true;
						}
					}
				}
				return false;
			}
		});

		//handler for entering data into scan id field
		mScanId.setOnEditorActionListener(new OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				if (actionId == EditorInfo.IME_ACTION_DONE)
				{
					if (mPullNum.getText().toString().equals("1"))
					{
						goToAdmin();
					}
					else if (quantity.getText().length() == 0)
					{
						quantity.requestFocus();
						mScanId.clearFocus();
					}
					else
					{
						if(mPullNum.getText().length() == 0)
						{
							mPullNum.requestFocus();
							quantity.clearFocus();
						}
						else
						{
							commitRecord();
							return true;
						}
					}
				}
				return true;
			}
		});
	}

    @Override
    protected void onStart()
    {
        super.onStart();

        regBroadCastReceivers();
    }

    @Override
    protected void onStop()
    {
        boolean scannerLock = config.getBoolean("scannerLock", false);
        // unregister the scanner
        unregisterReceiver(receiver);

        if(!scannerLock)
        {

            // indicate this view has been destroyed
            // if the reference count becomes 0 ScanAPI can
            // be closed if this is not a screen rotation scenario
            ScanAPIApplication.getApplicationInstance().forceRelease();
        }

        super.onStop();
    }


    /*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {

		String message = String.format("in onPause");
		Log.d(TAG, message);
		
		this.productDataSource.close();
		this.upcDataSource.close();
		this.scanDataSource.close();
        ScanAPIApplication.getApplicationInstance().currPullNum = mPullNum.getText().toString();
		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume()
	{
        super.onResume();
		String message = String.format("in onResume");
		Log.d(TAG, message);
		
		setConfig();
		setLocation();

		this.productDataSource.read();
		this.upcDataSource.read();
		this.scanDataSource.open();
        //this.mSkidMode.setVisibility(View.INVISIBLE);
		//checkMarkIntent(getIntent());

		mPullNum.setText(ScanAPIApplication.getApplicationInstance().currPullNum);

		isMasNum = false;

		try
		{
			setData();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		if (!isAutoScan || isManQty)
		{
			quantity.setEnabled(true);
			quantity.setText("");
		}
		else
		{
			quantity.clearFocus();
			quantity.setEnabled(false);
		}

		if (PullReviewActivity.FILE_EXPORTED == true)
		{
			resetViews();
			PullReviewActivity.FILE_EXPORTED = false;
		}

        String buildDate = new SimpleDateFormat("yyMMdd", Locale.US).format(new Date());
        if(!(buildDate.equals(getSharedPreferences("scanConfig", 0).getString("buildDate", ""))))
        {
            finish();
        }
	}
	/**
	 * @param data
	 */
	/*
	private void checkMarkIntent(Intent data) {

		if (data != null) {  
			if (data.hasExtra("MARK_ID")) {
				this.mSkidMode.setVisibility(View.VISIBLE);
				this.mark = data.getStringExtra("MARK_ID");
				//((TextView) this.findViewById(R.id.skid_id)).setText(this.mark);
				//((ToggleButton) this.findViewById(R.id.tog_set_mark)).setChecked(true);
				updateMarks();

			}
		}

	}

	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
		     if (resultCode == 0) 
		     {
		    	 if(requestCode == 3)
		    	 {
		    		 mark = data.getStringExtra("MARK_ID");
		    	 }
		     }
	}*/
	
	
	/*
	 * Checks data sent read from scanner using regex. Ensures data is a MAS
	 * number or a valid UPC. Sets the text ScanId view to a valid scan. The UPC
	 * is not necessarily one that exists in the database.
	 * 
	 * @param String data
	 */
	/**
	 * @param data
	 * @return
	 */
	public boolean checkScan(String data) {

		if (sqsRegEx.matcher(data).matches())
		{
			isMasNum = true;
			mScanId.setText(data);
			if(!isManQty && !isAutoScan && !data.substring(data.length() - 3).matches("000"))
				commitRecord();
			else if ((isManQty || !isAutoScan) && (quantity.getText().toString().isEmpty()))
			{
				enterQuantity();
				//mScanId.setText("");
			}
			else if(!isAutoScan && data.substring(data.length() - 3).matches("000") && quantity.getText().toString().isEmpty())
			{
				enterQuantity();
				//mScanId.setText("");
			}
			else
				commitRecord();
			
			return true;
		}
		else if (upcRegEx.matcher(data).matches())
		{
			isMasNum = false;
			mScanId.setText(data);

			if(quantity.getText().toString().isEmpty())
			{
				enterQuantity();
			}
			else
				commitRecord();
			/*
			if (isAutoScan && !isManQty)
			{
				quantity.setText(Integer.toString(autoVal));
				commitRecord();
				return true;
			}
			else if()
			else
			{
				enterQuantity();
				return true;
			}*/
			return true;
		}

		else {

			Toast.makeText(getBaseContext(), "Invalid Scan", Toast.LENGTH_LONG)
					.show();
			return false;
		}

	}

	/**
	 * @return
	 */
	public boolean commitRecord() {

		String tempId = getProductId();

		processRecord();
		/*
		if(isBoxQty)
		{
			confirmBoxQty(this.boxQty);
			processRecord();
		}
		else
		{
			processRecord();
		}
		 */
		/*
		if (!mark.isEmpty()) {
			updateMarks();
		}
		*/
		showTitle();
		setTitleCount(currentRecord.getScanEntry());

		setQuantity(tempId);

		mScanId.setText("");
		return true;
	}

	private void processRecord()
	{
		String tempId = getProductId();
		int i = 0;
		
		do {
			setThisMasNum(tempId);
			setScanTitle();
			createRecord();
			scanDataSource.insertScan(currentRecord);
			recordCount.setText(Integer.toString(this.scanDataSource.getAllScans().getCount()));
			setPullNumbers();
			if (ERR_SCAN)
			{
				Toast.makeText(this, "Bring Copy of Title to Dave Kinn", Toast.LENGTH_LONG).show();
			}
			i++;
		} while(i<this.boxQty && this.isBoxQty);
	}
	/* Creates error with Box Quantity mode
	private void confirmBoxQty(int boxQty)
	{
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		
		// set title
		alertDialogBuilder.setTitle("Confirm Box Quantity");
		
		// set dialog message
		alertDialogBuilder
			.setMessage("Add " + boxQty+ "boxes?")
			.setCancelable(false)
			.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					
					processRecord();
												
				}
			  })
			.setNegativeButton("No",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing
					dialog.cancel();
				}
			});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
	}
	 */
	public String getProductId()
	{
		String tempId = mScanId.getText().toString();

		if (isNewProduct && !(isMasNum))
		{
			if(mScanId.getText().length() == 12)
				return (tempId + "-N");
		}

		return tempId;
	}

	/**
	 * @param title
	 * @return
	 */
	public String createMessage(String title)
	{
		String currmPullNum = mPullNum.getText().toString();
		String currScanId = mScanId.getText().toString();

		if (isMasNum)
		{
			currScanId = currScanId.substring(0, currScanId.length() - 3);
		}

		int[] counts = this.scanDataSource.getScanTotalCounts(currmPullNum, currScanId);

		return String.format(Locale.US, "Pull#: %s \nScans: %d  Qty: %d \n %s",
				currmPullNum, counts[0], counts[1], title);
	}

	/**
	 * @return
	 */
	public boolean createRecord() {

		String tempPull = mPullNum.getText().toString();
		String tempmScanId = mScanId.getText().toString();
		String tempQuantity = quantity.getText().toString();
		String tempTitle = mTitle.getText().toString();
		String tempPriceList = mPriceList.getText().toString();
		String tempPriceFilters = mPriceFilters.getText().toString();
		String tempRating = mRating.getText().toString();

		if (isMasNum) {
			if (!isAutoScan && !isManQty && !tempmScanId.substring(tempmScanId.length() - 3).matches("000"))
			{
				tempQuantity = tempmScanId.substring(tempmScanId.length() - 3);
			}

			tempmScanId = tempmScanId.substring(0, tempmScanId.length() - 3);
		}

        String location = "";
        String defGateway = Utilities.getDefaultGateway(this);
        if(defGateway.matches("3.150.168.192"))
            location = "r";
        else if(defGateway.matches("1.150.168.192"))
            location = "p";
        else
            location = "o";
		currentRecord = new ScanRecord(tempmScanId, tempQuantity, tempPull,
				mark, tempTitle, tempPriceList, thisMasNum, tempPriceFilters, tempRating, location);

		return true;
	}

	private void enterQuantity()
	{
		quantity.setEnabled(true);
		quantity.requestFocus();
		imm.showSoftInput(quantity, 0);
		titleMessage.setText("Enter Quantity.");
		titleMessage.show();
	}

	/**
	 * @return
	 */
	public String getDeviceName() {

		return BluetoothAdapter.getDefaultAdapter().getName();
	}

	/**
	 * @return
	 */
	public String[] getProdInfo()
	{
		Product prod;
		String title;
		String priceList;
		String rating;
		String priceFilters;
		
		prod = productDataSource.getJoinProduct(lensId, this.thisMasNum);
		title = prod.getTitle();
		priceList = prod.getPriceList();
		rating = prod.getRating();
		priceFilters = prod.getPriceFilters();
		
		return new String[] {title, priceList, rating, priceFilters};
	}

	/*
	 * Starts the Admin Activity.
	 * 
	 * @param View v
	 */
	public void goToAdmin()
	{
		Intent intent = new Intent(this, AdminActivity.class);
		startActivity(intent);
	}

	/*
	 * View OnClick event that starts the Scan Config Activity.
	 * 
	 * @param View v
	 */
	public void goToConfig(View v)
	{
		Intent intent = new Intent(this, ScanConfigActivity.class);
		//startActivityForResult(intent, 3);
		startActivity(intent);
		finish();
	}

	/**
	 * @param v
	 */
	public void goToSkidReview(View v)
	{
		Intent intent = new Intent(this, SkidScanActivity.class);
		startActivity(intent);
	}

	/**
	 * @param v
	 */
	public void goToReview(View v)
	{
		Intent intent = new Intent(this, PullReviewActivity.class);
		startActivity(intent);
	}

	/*
	 * Reset all data that is displayed on the screen. Called only after a file
	 * is exported.
	 * 
	 * @param none
	 * 
	 * @see onResume()
	 */
	private void resetViews()
	{
		mPullNum.setText("");
		pullPieceCount.setText("0");
		pullLines.setText("0");
		mScanId.setText("");
		mTitle.setText("");
		titleCount.setText("");
		mRating.setText("");
		recordCount.setText("0");
		mPriceList.setText("");
		mPriceFilters.setText("");
	}

	/*
	 * Reads a config file and sets all configs for this activity
	 * 
	 * @param none
	 */
	public void setConfig()
	{
		isAutoScan = config.getBoolean("isAutoCount", false);
		autoVal = config.getInt("autoCount", 0);
		msgTime = config.getInt("msgTime", 3);
		isBoxQty = config.getBoolean("isBoxQty", false);
		//isManQty = config.getBoolean("isManQty", false);
		//isNewProduct = config.getBoolean("isNewProduct", false);
		String tempBox = config.getString("boxQty", "0");
        lensId = config.getString("lensSelectionId", "1");
		if (tempBox == "")
		{
			tempBox = "1";
		}
		boxQty = Integer.parseInt(tempBox);
	}

	/**
	 * @throws IOException
	 */
	public void setData() throws IOException
	{
		setQuantity(mScanId.getText().toString());
		recordCount.setText(Integer.toString(this.scanDataSource.getAllScans()
				.getCount()));
		setPullNumbers();

		if (!(mScanId.getText().toString().isEmpty()))
		{
			setTitleCount(mScanId.getText().toString());
		}
	}

	/*
	 * Displays toast dialog with the title and count information.
	 */
	private void setPullNumbers()
	{
		this.pullLines.setText(Integer.toString(this.scanDataSource
				.getScansByPullId(mPullNum.getText().toString()).getCount()));
		this.pullPieceCount.setText(Integer.toString(this.scanDataSource
				.getTotalByPull(mPullNum.getText().toString())));
	}

	/*
	 * Sets the quantity view to display the correct scan quantity
	 * 
	 * @param id
	 */
	public void setQuantity(String id)
	{
		if (isAutoScan && !isManQty)
		{
			quantity.setText(Integer.toString(autoVal));
		}
		/*
		else if (isMasNum && !isManQty)
		{
			if (id.substring(id.length() - 3).startsWith("0"))
			{
				quantity.setText(id.substring(id.length() - 2));
			}
			else
			{
				quantity.setText(id.substring(id.length() - 3));
			}
		}
		*/
		else
		{
			quantity.setText("");
		}

	}

	/*
	 * Updates the count for a specific scan/title.
	 * @param id
	 */
	public boolean setScanTitle()
	{
		String[] prodInfo = getProdInfo();
		String title = prodInfo[0];
		String priceList = prodInfo[1];
		String rating = prodInfo[2];
		if(rating != null && rating.matches("0"))
			rating = "";
		String priceFilters = prodInfo[3];
		if(priceFilters != null && priceFilters.matches("0"))
			priceFilters = "";
		
		if (title == null || title.isEmpty())
		{
			title = (getString(R.string.err_scan));
			ERR_SCAN = true;
			ERROR_SCANS = true;
		}
		else
		{
			ERR_SCAN = false;
		}

		mTitle.setText(title);
		mTitle.setSelected(true);
		mPriceList.setText(priceList);
		mRating.setText(rating);
		mPriceFilters.setText(priceFilters);

		return true;
	}

	/**
	 * @param v
	 */
	/*
	 * public void setSkidMode(View v){
	 * 
	 * if(((ToggleButton) v).isChecked()){ //create skid id Calendar c =
	 * Calendar.getInstance(); mark = Integer.toString(c.get(Calendar.HOUR)) +
	 * Integer.toString(c.get(Calendar.MILLISECOND));
	 * ((TextView)this.findViewById(R.id.skid_id)).setText(this.mark); } else{
	 * 
	 * mark = ""; ((TextView)this.findViewById(R.id.skid_id)).setText("");
	 * updateMarks();
	 * 
	 * }
	 * 
	 * }
	 */

	/*
	 * Sets all data displayed on the screen and underlying variables.
	 * 
	 * @param none
	 */
	/**
	 * @param id
	 */
	private void setThisMasNum(String id)
	{
		// SQSmasnumQTY
		if (id.contains("SQS"))
		{
			this.thisMasNum = (id.substring(3, id.length() - 3));
		}
		// UPC
		else if (id.length() >= 11)
		{
			this.thisMasNum = upcDataSource.getMasNumFromUPC(id);
			if (thisMasNum.isEmpty())
			{
				// UPC not found
				this.thisMasNum = id;
				this.ERR_SCAN = true;
			}
		}
		// MASNUM
		else
		{
			this.thisMasNum = id;
		}
		return;
	}

	/**
	 * @param scanEntry
	 */
	private void setTitleCount(String scanEntry)
	{
		String currmPullNum = mPullNum.getText().toString();
		// [1] == total number of pcs.
		int itemCount = this.scanDataSource.getScanTotalCounts(currmPullNum,
				scanEntry)[1];
		titleCount.setText(Integer.toString(itemCount));
	}

	public boolean showTitle()
	{
		String title = mTitle.getText().toString();
		titleMessage.setDuration(msgTime);

		if (title.isEmpty()) {
			titleMessage.setText("Invalid Scan");
			titleMessage.show();
			mScanId.setText("");

			return false;
		}// end if

		String message = createMessage(title);

		titleMessage.setText(message);
		titleMessage.show();

		return true;
	}
	
	public void setLocation()
	{
		String location = "Location: ";
		String defGateway = Utilities.getDefaultGateway(this);
		TextView deviceLocation = (TextView)findViewById(R.id.device_location); 
		if(defGateway.matches("3.150.168.192"))
			location += "Reading";
		else if(defGateway.matches("1.150.168.192"))
			location += "PTown";
		else
			location += defGateway;
		deviceLocation.setText(location);
	}

	/**
	 * Updates the count of each skid if in SkidMode
	 */
	/*
	public void updateMarks() {

		if (this.mark != "") {

			String[] vals = scanDataSource.getMarkValuesById(this.mark);

			((TextView) this.findViewById(R.id.skid_scans)).setText(vals[0]);
			((TextView) this.findViewById(R.id.skid_total)).setText(vals[1]);
		}

		else {

			((TextView) this.findViewById(R.id.skid_scans)).setText("");
			((TextView) this.findViewById(R.id.skid_total)).setText("");
		}
	}*/
	
	public void setNewProdMode(View v)
	{
		if(((ToggleButton) v).isChecked())
		{
			this.isNewProduct = true;
		}
		else
		{
			this.isNewProduct = false;
		}
	}
	
	public void setManQtyMode(View v)
	{
		if(((ToggleButton) v).isChecked())
		{
			this.isManQty = true;
			quantity.setEnabled(true);
			quantity.setText("");
		}
		else
		{
			this.isManQty = false;
			if(isAutoScan)
			{
				quantity.setEnabled(false);
				quantity.setText(Integer.toString(autoVal));
			}
		}
	}
	
	public void checkProduct()
	{
		Product prod = productDataSource.getProduct(mScanId.getText().toString());
		int masNum = prod.getMasNum();
		String title = prod.getTitle();
		String category = prod.getCategory();
		String rating = prod.getRating();
		String streetDate = prod.getStreetDate();
		int titleFilm = prod.getTitleFilm();
		int noCover = prod.getNoCover();
		String priceList = prod.getPriceList();
		int isNew = prod.getIsNew();
		int isBoxSet = prod.getIsBoxSet();
		int multipack = prod.getMultipack();
		String mediaFormat = prod.getMediaFormat();
		String priceFilters = prod.getPriceFilters();
		String specialFields = prod.getSpecialFields();
		String studio = prod.getStudio();
		String season = prod.getSeason();
		int numberOfDiscs = prod.getNumberOfDiscs();
		String theaterDate = prod.getTheaterDate();
		String studioName = prod.getStudioName();
		String sha = prod.getSha();

		String displayString = 
				Integer.toString(masNum) + '\n' +
				title + '\n' +
				category + '\n' +
				rating + '\n' +
				streetDate + '\n' +
				Integer.toString(titleFilm) + '\n' +
				Integer.toString(noCover) + '\n' + 
				priceList + '\n' + 
				isNew + '\n' +
				isBoxSet + '\n' +
				multipack + '\n' +
				mediaFormat + '\n' +
				priceFilters + '\n' +
				specialFields + '\n' +
				studio + '\n' +
				season + '\n' +
				numberOfDiscs + '\n' +
				theaterDate + '\n' +
				studioName + '\n' +
				sha;
		Toast.makeText(this, displayString, Toast.LENGTH_SHORT).show();
	}

	private final BroadcastReceiver receiver = new BroadcastReceiver()
    {
		private static final String TAG = "BroadcastReceiver";

		@Override
		public void onReceive(Context c, Intent intent)
        {
			String message = String.format("in onReceive");
			Log.d(TAG, message);

            if (intent.getAction().equalsIgnoreCase(ScanAPIApplication.NOTIFY_DECODED_DATA))
            {
                String data = new String(intent.getCharArrayExtra(ScanAPIApplication.EXTRA_DECODEDDATA));

                if(mPullNum.getText().toString().equals("1"))
                {
                    goToAdmin();
                }/*
                else if (data.equals(getString(R.string.adminCode)))
                {
                    goToAdmin();
                    mScanId.setText("");
                    titleCount.setText("");
                }// end if*/
                else if (data.contains("P"))
                {
                    mPullNum.setText(data.substring(1));
                    setPullNumbers();
                }// end if
                else if (mPullNum.getText().length() <= 0)
                {
                    Toast.makeText(getBaseContext(),
                            "Enter Pull Number before Scanning",
                            Toast.LENGTH_LONG).show();
                }// end else if
                else
                {
                    mScanId.setText("");
                    titleCount.setText("");
                    checkScan(data);
                }// end else
            }// end else if
			else if (intent.getAction().equalsIgnoreCase(ScanAPIApplication.NOTIFY_SCANNER_ARRIVAL))
            {
                Utilities.makeLongToast(c, intent.getStringExtra(ScanAPIApplication.EXTRA_DEVICENAME) + " Connected");
			}
            else if (intent.getAction().equalsIgnoreCase(ScanAPIApplication.NOTIFY_SCANPI_INITIALIZED))
            {
                Utilities.makeLongToast(c, "Ready to pair with scanner");
			}
            else if (intent.getAction().equalsIgnoreCase(ScanAPIApplication.NOTIFY_CLOSE_ACTIVITY))
            {
			}
            else if (intent.getAction().equalsIgnoreCase(ScanAPIApplication.NOTIFY_ERROR_MESSAGE))
            {
                Utilities.makeLongToast(c, intent.getStringExtra(ScanAPIApplication.EXTRA_ERROR_MESSAGE));
            }
        }// end on Recieve
	};

	private void regBroadCastReceivers()
    {
        IntentFilter filter;
        filter = new IntentFilter(ScanAPIApplication.NOTIFY_SCANPI_INITIALIZED);
        registerReceiver(this.receiver, filter);

        filter = new IntentFilter(ScanAPIApplication.NOTIFY_SCANNER_ARRIVAL);
        registerReceiver(this.receiver, filter);

        filter = new IntentFilter(ScanAPIApplication.NOTIFY_SCANNER_REMOVAL);
        registerReceiver(this.receiver, filter);

        filter = new IntentFilter(ScanAPIApplication.NOTIFY_DECODED_DATA);
        registerReceiver(this.receiver, filter);

        filter = new IntentFilter(ScanAPIApplication.NOTIFY_ERROR_MESSAGE);
        registerReceiver(this.receiver, filter);

        filter = new IntentFilter(ScanAPIApplication.NOTIFY_CLOSE_ACTIVITY);
        registerReceiver(this.receiver, filter);

        filter = new IntentFilter(ScanAPIApplication.SET_SOUND_CONFIG_COMPLETE);
        registerReceiver(this.receiver, filter);

        filter = new IntentFilter(ScanAPIApplication.GET_SOUND_CONFIG_COMPLETE);
        registerReceiver(this.receiver, filter);

        // increasing the Application View count from 0 to 1 will
        // cause the application to open and initialize ScanAPI
        ScanAPIApplication.getApplicationInstance().increaseViewCount();
    }
}