package com.sqsmv.sqsscanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.socketmobile.apiintegration.ScanAPIApplication;
import com.sqsmv.sqsscanner.DB.ProductDataSource;
import com.sqsmv.sqsscanner.DB.ScanDataSource;
import com.sqsmv.sqsscanner.DB.UPCDataSource;

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
	public static boolean ERROR_SCANS;
	private static final String TAG = "ScanHomeActivity";

	private ScanRecord currentRecord;

	// Config info
	public boolean isAutoScan;
	public int autoVal;
	public int msgTime;
	public int boxQty;
	public boolean isBoxQty;
	private boolean isSkidScanMode;
	private String lensId;

	public Pattern sqsRegEx;
	public Pattern upcRegEx;

	private boolean ERR_SCAN;

	private String thisMasNum;

	private DroidConfigManager appConfig;

	private boolean isMasNum = false;
	public boolean isManQty;
	public boolean isNewProduct;

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
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		String message = String.format("in onCreate");
		Log.d(TAG, message);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_scan_home);

		productDataSource = new ProductDataSource(this);
		upcDataSource = new UPCDataSource(this);
		scanDataSource = new ScanDataSource(this);

		msgTime = 3;
		upcRegEx = Pattern.compile(getString(R.string.upcRegEx));
		sqsRegEx = Pattern.compile(getString(R.string.sqsRegEx));

		// find all the views
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

		appConfig = new DroidConfigManager(this);


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
					else if(isSkidScanMode)
					{
						if(mPullNum.getText().toString().isEmpty())
						{
							mPullNum.requestFocus();
							quantity.clearFocus();
						}
						else
						{
							insertSkidScan();
						}
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
		boolean scannerLock = appConfig.accessBoolean(DroidConfigManager.SCANNER_LOCK, null, false);
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
	protected void onPause()
	{
		String message = String.format("in onPause");
		Log.d(TAG, message);

		productDataSource.close();
		upcDataSource.close();
		scanDataSource.close();
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

		productDataSource.read();
		upcDataSource.read();
		scanDataSource.open();

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

		if(appConfig.accessInt(DroidConfigManager.EXPORT_MODE_CHOICE, null, 1) == 6)
		{
			isSkidScanMode = true;
			hideProduct();
		}
		else
		{
			if(isSkidScanMode)
			{
				isSkidScanMode = false;
				showProduct();
			}
		}

		String buildDate = new SimpleDateFormat("yyMMdd", Locale.US).format(new Date());
		if(!(buildDate.equals(appConfig.accessString(DroidConfigManager.BUILD_DATE, null, ""))))
		{
			finish();
		}
	}

	/**
	 * Checks data sent read from scanner using regex. Ensures data is a MAS
	 * number or a valid UPC. Sets the text ScanId view to a valid scan. The UPC
	 * is not necessarily one that exists in the database.
	 *
	 * @param data
	 * @return
	 */
	public boolean checkScan(String data)
	{
		if (sqsRegEx.matcher(data).matches())
		{
			isMasNum = true;
			mScanId.setText(data);
			if(!isManQty && !isAutoScan && !data.substring(data.length() - 3).matches("000"))
			{
				commitRecord();
			}
			else if ((isManQty || !isAutoScan) && (quantity.getText().toString().isEmpty()))
			{
				enterQuantity();
			}
			else if(!isAutoScan && data.substring(data.length() - 3).matches("000") && quantity.getText().toString().isEmpty())
			{
				enterQuantity();
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
			return true;
		}
		else
		{
			Toast.makeText(getBaseContext(), "Invalid Scan", Toast.LENGTH_LONG).show();
			return false;
		}

	}

	/**
	 * @return
	 */
	public boolean commitRecord()
	{
		String tempId = getProductId();
		processRecord();
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

		do
		{
			setThisMasNum(tempId);
			setScanTitle();
			createRecord();
			scanDataSource.insertScan(currentRecord);
			if (ERR_SCAN)
			{
				Toast.makeText(this, "Bring Copy of Title to Dave Kinn", Toast.LENGTH_LONG).show();
			}
			i++;
		} while(i<boxQty && isBoxQty);
		recordCount.setText(Integer.toString(scanDataSource.getAllScans().getCount()));
		setPullNumbers();
	}

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

	public String createMessage(String title)
	{
		String currmPullNum = mPullNum.getText().toString();
		String currScanId = mScanId.getText().toString();

		if (isMasNum)
		{
			currScanId = currScanId.substring(0, currScanId.length() - 3);
		}

		int[] counts = scanDataSource.getScanTotalCounts(currmPullNum, currScanId);

		return String.format(Locale.US, "Pull#: %s \nScans: %d  Qty: %d \n %s",
				currmPullNum, counts[0], counts[1], title);
	}

	public boolean createRecord()
	{
		String tempPull = mPullNum.getText().toString();
		String tempmScanId = mScanId.getText().toString();
		String tempQuantity = quantity.getText().toString();
		String tempTitle = mTitle.getText().toString();
		String tempPriceList = mPriceList.getText().toString();
		String tempPriceFilters = mPriceFilters.getText().toString();
		String tempRating = mRating.getText().toString();

		if (isMasNum)
		{
			if (!isAutoScan && !isManQty && !tempmScanId.substring(tempmScanId.length() - 3).matches("000"))
			{
				tempQuantity = tempmScanId.substring(tempmScanId.length() - 3);
			}
			tempmScanId = tempmScanId.substring(0, tempmScanId.length() - 3);
		}

		String location = "";
		String defGateway = Utilities.getDefaultGateway(this);
		if(defGateway.matches("3.150.168.192"))
		{
			location = "r";
		}
		else if(defGateway.matches("1.150.168.192"))
		{
			location = "p";
		}
		else
		{
			location = "o";
		}
		currentRecord = new ScanRecord(tempmScanId, tempQuantity, tempPull,
				"", tempTitle, tempPriceList, thisMasNum, tempPriceFilters, tempRating, location);

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
	public String[] getProdInfo()
	{
		Product prod;
		String title;
		String priceList;
		String rating;
		String priceFilters;

		prod = productDataSource.getJoinProduct(lensId, thisMasNum);
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
		isAutoScan = appConfig.accessBoolean(DroidConfigManager.IS_AUTO_COUNT, null, false);
		autoVal = appConfig.accessInt(DroidConfigManager.AUTO_COUNT, null, 0);

		isBoxQty = appConfig.accessBoolean(DroidConfigManager.IS_BOX_QTY, null, false);
		String tempBox = appConfig.accessString(DroidConfigManager.BOX_QTY, null, "0");
		lensId = appConfig.accessString(DroidConfigManager.LENS_SELECTION_ID, null, "1");
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
		recordCount.setText(Integer.toString(scanDataSource.getAllScans().getCount()));
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
		pullLines.setText(Integer.toString(scanDataSource.getScansByPullId(mPullNum.getText().toString()).getCount()));
		pullPieceCount.setText(Integer.toString(scanDataSource.getTotalByPull(mPullNum.getText().toString())));
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
		{
			rating = "";
		}
		String priceFilters = prodInfo[3];
		if(priceFilters != null && priceFilters.matches("0"))
		{
			priceFilters = "";
		}

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
	 * @param id
	 */
	private void setThisMasNum(String id)
	{
		// SQSmasnumQTY
		if (id.contains("SQS"))
		{
			thisMasNum = (id.substring(3, id.length() - 3));
		}
		// UPC
		else if (id.length() >= 11)
		{
			thisMasNum = upcDataSource.getMasNumFromUPC(id);
			if (thisMasNum.isEmpty())
			{
				// UPC not found
				thisMasNum = id;
				ERR_SCAN = true;
			}
		}
		// MASNUM
		else
		{
			thisMasNum = id;
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
		int itemCount = scanDataSource.getScanTotalCounts(currmPullNum, scanEntry)[1];
		titleCount.setText(Integer.toString(itemCount));
	}

	public boolean showTitle()
	{
		String title = mTitle.getText().toString();
		titleMessage.setDuration(msgTime);

		if (title.isEmpty())
		{
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
		{
			location += "Reading";
		}
		else if(defGateway.matches("1.150.168.192"))
		{
			location += "PTown";
		}
		else
		{
			location += defGateway;
		}
		deviceLocation.setText(location);
	}

	public void setNewProdMode(View v)
	{
		if(((ToggleButton) v).isChecked())
		{
			isNewProduct = true;
		}
		else
		{
			isNewProduct = false;
		}
	}

	public void setManQtyMode(View v)
	{
		if(((ToggleButton) v).isChecked())
		{
			isManQty = true;
			quantity.setEnabled(true);
			quantity.setText("");
		}
		else
		{
			isManQty = false;
			if(isAutoScan)
			{
				quantity.setEnabled(false);
				quantity.setText(Integer.toString(autoVal));
			}
		}
	}

	public void hideProduct()
	{
		View scanRow = findViewById(R.id.scanRow);
		View tableRow3 = findViewById(R.id.tableRow3);
		View titleScrollView = findViewById(R.id.titleScrollView);
		View priceListRow = findViewById(R.id.priceListRow);
		View priceFilterScrollView = findViewById(R.id.titleScrollView);
		scanRow.setVisibility(View.GONE);
		tableRow3.setVisibility(View.GONE);
		titleScrollView.setVisibility(View.GONE);
		priceListRow.setVisibility(View.GONE);
		priceFilterScrollView.setVisibility(View.GONE);

	}

	public void showProduct()
	{
		View scanRow = findViewById(R.id.scanRow);
		View tableRow3 = findViewById(R.id.tableRow3);
		View titleScrollView = findViewById(R.id.titleScrollView);
		View priceListRow = findViewById(R.id.priceListRow);
		View priceFilterScrollView = findViewById(R.id.titleScrollView);
		scanRow.setVisibility(View.VISIBLE);
		tableRow3.setVisibility(View.VISIBLE);
		titleScrollView.setVisibility(View.VISIBLE);
		priceListRow.setVisibility(View.VISIBLE);
		priceFilterScrollView.setVisibility(View.VISIBLE);
	}

	public void insertSkidScan()
	{
		scanDataSource.insertScan(new ScanRecord(mPullNum.getText().toString(), quantity.getText().toString()));
		recordCount.setText(Integer.toString(scanDataSource.getAllScans().getCount()));
		setPullNumbers();
		mPullNum.setText("");
		if(!isAutoScan)
		{
			quantity.setText("");
		}
	}

	private final BroadcastReceiver receiver = new BroadcastReceiver()
	{
		private static final String TAG = "BroadcastReceiver";

		@Override
		public void onReceive(Context c, Intent intent)
		{
			String message = String.format("in onReceive");
			Log.d(TAG, message);

			if(intent.getAction().equalsIgnoreCase(ScanAPIApplication.NOTIFY_DECODED_DATA))
			{
				String data = new String(intent.getCharArrayExtra(ScanAPIApplication.EXTRA_DECODEDDATA));

				if(mPullNum.getText().toString().equals("1"))
				{
					goToAdmin();
				}
				else if(isSkidScanMode)
				{
					if (data.contains("P"))
					{
						mPullNum.setText(data.substring(1));
						if(quantity.getText().toString().isEmpty())
						{
							enterQuantity();
						}
						else
						{
							insertSkidScan();
						}
					}
				}
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
		registerReceiver(receiver, filter);

		filter = new IntentFilter(ScanAPIApplication.NOTIFY_SCANNER_ARRIVAL);
		registerReceiver(receiver, filter);

		filter = new IntentFilter(ScanAPIApplication.NOTIFY_SCANNER_REMOVAL);
		registerReceiver(receiver, filter);

		filter = new IntentFilter(ScanAPIApplication.NOTIFY_DECODED_DATA);
		registerReceiver(receiver, filter);

		filter = new IntentFilter(ScanAPIApplication.NOTIFY_ERROR_MESSAGE);
		registerReceiver(receiver, filter);

		filter = new IntentFilter(ScanAPIApplication.NOTIFY_CLOSE_ACTIVITY);
		registerReceiver(receiver, filter);

		// increasing the Application View count from 0 to 1 will
		// cause the application to open and initialize ScanAPI
		ScanAPIApplication.getApplicationInstance().increaseViewCount();
	}
}