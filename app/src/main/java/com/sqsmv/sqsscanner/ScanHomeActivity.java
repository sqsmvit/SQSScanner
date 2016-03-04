package com.sqsmv.sqsscanner;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ToggleButton;

import com.socketmobile.apiintegration.ScanAPIApplication;
import com.sqsmv.sqsscanner.database.DBAdapter;
import com.sqsmv.sqsscanner.database.pricelist.PriceListAccess;
import com.sqsmv.sqsscanner.database.pricelist.PriceListRecord;
import com.sqsmv.sqsscanner.database.product.ProductAccess;
import com.sqsmv.sqsscanner.database.product.ProductRecord;
import com.sqsmv.sqsscanner.database.productlens.ProductLensAccess;
import com.sqsmv.sqsscanner.database.productlens.ProductLensRecord;
import com.sqsmv.sqsscanner.database.scan.ScanAccess;
import com.sqsmv.sqsscanner.database.scan.ScanRecord;
import com.sqsmv.sqsscanner.database.upc.UPCAccess;
import com.sqsmv.sqsscanner.database.upc.UPCRecord;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The main Activity for the app where all the data input is done. Input can be done either manually
 * or through the Socket Mobile scanner.
 */
public class ScanHomeActivity extends Activity
{
    private static final String TAG = "ScanHomeActivity";
    private final static String DEFAULT_UPC_COUNT = "30";

    private DroidConfigManager appConfig;

    private DBAdapter dbAdapter;
    private ProductAccess productAccess;
    private UPCAccess upcAccess;
    private ProductLensAccess productLensAccess;
    private PriceListAccess priceListAccess;
    private ScanAccess scanAccess;

    private Pattern pullScanRegEx, sqsRegEx, upcRegEx;

    private TextView recordCountView, numPullLinesView, pullPieceCountView, titleCountView, titleView, priceListView, ratingView;
    private EditText pullNumberInput, scanIdInput, quantityInput, scannerInitialsInput;
    private ToggleButton manualQuantityModeToggle, newProductModeToggle;

    private boolean isAutoCountMode, isBoxQtyMode, isManualCountMode, isNewProductMode;
    private int boxQtyVal, exportMode;
    private String autoCountVal, selectedLensId;

    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "in onCreate");
        setContentView(R.layout.activity_scan_home);

        appConfig = new DroidConfigManager(this);

        dbAdapter = new DBAdapter(this);
        productAccess = new ProductAccess(dbAdapter);
        upcAccess = new UPCAccess(dbAdapter);
        productLensAccess = new ProductLensAccess(dbAdapter);
        priceListAccess = new PriceListAccess(dbAdapter);
        scanAccess = new ScanAccess(dbAdapter);


        pullScanRegEx = Pattern.compile("^[pP](\\d+)$");
        upcRegEx = Pattern.compile("^\\d{12,13}(-N)?$");
        sqsRegEx = Pattern.compile("^SQS(\\d+)(\\d{3})$");

        // find all the views
        recordCountView = (TextView)findViewById(R.id.count);
        pullNumberInput = (EditText)findViewById(R.id.pullNum);
        numPullLinesView = (TextView)findViewById(R.id.runPullCount);
        pullPieceCountView = (TextView)findViewById(R.id.totalCount);
        scanIdInput = (EditText)findViewById(R.id.scanId);
        titleCountView = (TextView)findViewById(R.id.totalTitleCount);
        titleView = (TextView)findViewById(R.id.title);
        priceListView = (TextView)findViewById(R.id.priceList);
        ratingView = (TextView)findViewById(R.id.rating);
        quantityInput = (EditText)findViewById(R.id.qtyNum);
        scannerInitialsInput = (EditText)findViewById(R.id.scannerInitials);
        manualQuantityModeToggle = (ToggleButton)findViewById(R.id.manualQty);
        newProductModeToggle = (ToggleButton)findViewById(R.id.newProductMode);

        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        setListeners();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        regBroadCastReceivers();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "in onResume");

        setConfig();
        displayLocation();

        productAccess.open();
        upcAccess.open();
        productLensAccess.open();
        priceListAccess.open();
        scanAccess.open();

        updateExportModeViews();
        if(!isAutoCountMode || isManualCountMode)
        {
            enableQuantityInput();
        }
        else
        {
            quantityInput.setText(autoCountVal);
            disableQuantityInput();
        }

        displayScannedRecordCount();

        if(scannerInitialsInput.getText().toString().isEmpty())
        {
            scannerInitialsInput.requestFocus();
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }

        String buildDate = Utilities.formatYYMMDDDate(new Date());
        if(!(buildDate.equals(appConfig.accessString(DroidConfigManager.BUILD_DATE, null, ""))))
        {
            finish();
        }
    }

    @Override
    protected void onStop()
    {
        boolean scannerLock = appConfig.accessBoolean(DroidConfigManager.SCANNER_LOCK, null, false);
        unregisterReceiver(receiver);

        if(!scannerLock)
        {
            ScanAPIApplication.getApplicationInstance().forceRelease();
        }

        super.onStop();
    }


    @Override
    protected void onPause()
    {
        Log.d(TAG, "in onPause");

        dbAdapter.close();

        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK)
        {
            if(requestCode == 1)
            {
                if(data != null)
                {
                    if(data.getBooleanExtra("FILE_EXPORTED", false))
                    {
                        pullNumberInput.setText("");
                        scanIdInput.setText("");
                        scannerInitialsInput.setText("");
                        titleCountView.setText("");
                        titleView.setText("");
                        priceListView.setText("");
                        ratingView.setText("");
                        manualQuantityModeToggle.setChecked(false);
                        newProductModeToggle.setChecked(false);
                    }
                }
            }
        }
    }

    /**
     * Sets the listeners used for the current Activity's GUI elements.
     */
    private void setListeners()
    {
        findViewById(R.id.reviewBtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goToReview();
            }
        });
        findViewById(R.id.configBtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goToConfig();
            }
        });
        manualQuantityModeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                setManualQuantityMode(isChecked);
            }
        });

        newProductModeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                setNewProductMode(isChecked);
            }
        });

        pullNumberInput.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(!hasFocus)
                {
                    displayScannedRecordCount();
                }
            }
        });

        scannerInitialsInput.setOnEditorActionListener(scanHomeFieldListener);
        pullNumberInput.setOnEditorActionListener(scanHomeFieldListener);
        scanIdInput.setOnEditorActionListener(scanHomeFieldListener);
        quantityInput.setOnEditorActionListener(scanHomeFieldListener);
    }

    /**
     * Initializes the value of config related variables based off of the config file.
     */
    private void setConfig()
    {
        exportMode = appConfig.accessInt(DroidConfigManager.EXPORT_MODE_CHOICE, null, 1);
        selectedLensId = appConfig.accessString(DroidConfigManager.LENS_SELECTION_ID, null, "1");
        isAutoCountMode = appConfig.accessBoolean(DroidConfigManager.IS_AUTO_COUNT, null, false);
        autoCountVal = appConfig.accessString(DroidConfigManager.AUTO_COUNT, null, "0");
        isBoxQtyMode = appConfig.accessBoolean(DroidConfigManager.IS_BOX_QTY, null, false);
        boxQtyVal = appConfig.accessInt(DroidConfigManager.BOX_QTY, null, 1);
    }

    /**
     * Sets isManualCountMode and adjusts GUI elements based off the new value.
     * @param isManualCountMode    The boolean value to set manualCountMode to.
     */
    private void setManualQuantityMode(boolean isManualCountMode)
    {
        this.isManualCountMode = isManualCountMode;
        if(isManualCountMode)
        {
            enableQuantityInput();
        }
        else
        {
            if(isAutoCountMode)
            {
                disableQuantityInput();
                quantityInput.setText(autoCountVal);
            }
        }
    }

    /**
     * Sets isNewProductMode.
     * @param isNewProductMode    The boolean value to set isNewProductMode to.
     */
    private void setNewProductMode(boolean isNewProductMode)
    {
        this.isNewProductMode = isNewProductMode;
    }

    /**
     * Registers the intents that can be received by the Socket Mobile scanner, then alerts the
     * application to initialize ScanAPI.
     */
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

    /**
     * Updates Views to display the current location of the Scanner based off of the
     * default gateway.
     */
    private void displayLocation()
    {
        String location;
        String defGateway = Utilities.getDefaultGateway(this);
        TextView deviceLocation = (TextView)findViewById(R.id.device_location);
        if(defGateway.equals("3.150.168.192"))
        {
            location = "Reading";
        }
        else if(defGateway.equals("1.150.168.192"))
        {
            location = "PTown";
        }
        else
        {
            location = defGateway;
        }
        deviceLocation.setText(location);
    }

    /**
     * Updates Views to display count information from the Scan table. Information displayed include
     * the total record count, the number of scans for the currently selected pull, and the total
     * number of pieces scanned for the currently selected pull.
     */
    private void displayScannedRecordCount()
    {
        recordCountView.setText(Integer.toString(scanAccess.getTotalScans()));
        numPullLinesView.setText(Integer.toString(scanAccess.getTotalScansByPull(pullNumberInput.getText().toString())));
        pullPieceCountView.setText(Integer.toString(scanAccess.getTotalByPull(pullNumberInput.getText().toString())));
    }

    /**
     * Updates Views to display information on the most recently scanned product. Information displayed includes the
     * total number of pieces that have been scanned, the title, the pricelist, and the rating,
     * @param scanRecord    The ScanRecord containing information on most recently scanned product.
     */
    private void displayProductInfo(ScanRecord scanRecord)
    {
        titleCountView.setText(Integer.toString(scanAccess.getProductCountForPull(scanRecord.getFkPullId(), scanRecord.getMasNum())));
        titleView.setText(scanRecord.getTitle());
        priceListView.setText(scanRecord.getPriceList());
        ratingView.setText(scanRecord.getRating());
    }

    private void updateExportModeViews()
    {
        ((TextView)findViewById(R.id.homeExportDisplay)).setText(ExportModeHandler.getExportMode(exportMode));
        updateProductModeFieldVisibility(exportMode != 6);
        updateBoxQuantityFieldVisibility(exportMode == 7);
    }

    private void updateProductModeFieldVisibility(boolean productVisibility)
    {
        int visibilityMode = View.VISIBLE;
        if(!productVisibility)
        {
            visibilityMode = View.GONE;
        }

        findViewById(R.id.scanRow).setVisibility(visibilityMode);
        findViewById(R.id.tableRow3).setVisibility(visibilityMode);
        findViewById(R.id.titleScrollView).setVisibility(visibilityMode);
        findViewById(R.id.priceListRow).setVisibility(visibilityMode);
        findViewById(R.id.titleScrollView).setVisibility(visibilityMode);
    }

    private void updateBoxQuantityFieldVisibility(boolean boxQuantityVisibility)
    {
        int visibilityMode = View.VISIBLE;
        if(!boxQuantityVisibility)
        {
            visibilityMode = View.GONE;
        }

        findViewById(R.id.boxQtyRow).setVisibility(visibilityMode);
    }

    private void toastProductTotals(ScanRecord scanRecord)
    {
        int[] counts = scanAccess.getScanTotalCounts(scanRecord.getFkPullId(), scanRecord.getMasNum());
        String productTotals = "Pull#: " + scanRecord.getFkPullId() + "\nScans: " + counts[0] + "  Quantity: " + counts[1] + "\n" + scanRecord.getTitle();
        Utilities.makeToast(this, productTotals);
    }

    private void enableQuantityInput()
    {
        quantityInput.setEnabled(true);
        quantityInput.setText("");
    }

    private void disableQuantityInput()
    {
        quantityInput.clearFocus();
        quantityInput.setEnabled(false);
    }

    private void resetQuantityInput()
    {
        if(!isAutoCountMode || isManualCountMode)
        {
            quantityInput.setText("");
        }
    }

    private void alertSoundVibrate()
    {
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        int minVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
        if(minVolume > currentVolume)
        {
            audioManager.setStreamVolume(AudioManager.STREAM_RING, minVolume, AudioManager.FLAG_ALLOW_RINGER_MODES);
        }

        //Ring and vibrate
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringTone = RingtoneManager.getRingtone(this, notification);
        ringTone.play();
        Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        long[] vibratePattern = {0, 500, 250, 500};
        vibrator.vibrate(vibratePattern, -1);
    }

    private void handleInputs(String scannerInitialsValue, String pullInputValue, String scanInputValue, String quantityInputValue)
    {
        if(pullInputValue.equals("1"))
        {
            goToAdmin();
        }
        else if(scannerInitialsValue.isEmpty())
        {
            scannerInitialsInput.requestFocus();
            imm.showSoftInput(scannerInitialsInput, 0);
            Utilities.makeToast(this, "Enter Scanner Initials.");
        }
        else if(pullInputValue.isEmpty())
        {
            pullNumberInput.requestFocus();
            imm.showSoftInput(pullNumberInput, 0);
            Utilities.makeToast(this, "Enter Pull Number.");
        }
        else if(scanInputValue.isEmpty() && exportMode != 6) //Not Skid Scan Mode
        {
            scanIdInput.requestFocus();
            imm.showSoftInput(scanIdInput, 0);
            Utilities.makeToast(this, "Enter Masnum or UPC.");
        }
        else if(quantityInputValue.isEmpty() || Integer.parseInt(quantityInputValue) <= 0)
        {
            quantityInput.requestFocus();
            imm.showSoftInput(quantityInput, 0);
            Utilities.makeToast(this, "Enter Quantity.");
        }
        else
        {
            if(exportMode == 6) //Skid Scan Mode
            {
                commitSkidScan(scannerInitialsValue, pullInputValue, quantityInputValue);
            }
            else if(exportMode == 7) //Reset Scan Mode
            {
                commitResetScan(scannerInitialsValue, pullInputValue, scanInputValue, quantityInputValue);
            }
            else
            {
                commitProductScan(scannerInitialsValue, pullInputValue, scanInputValue, quantityInputValue);
            }
        }
    }

    private void handleScanInput(String scanInput)
    {
        Log.d(TAG, "in handleScanInput");
        Matcher pullNumberMatcher = pullScanRegEx.matcher(scanInput);
        Matcher sqsMatcher = sqsRegEx.matcher(scanInput);
        Matcher upcMatcher = upcRegEx.matcher(scanInput);
        if(pullNumberMatcher.find())
        {
            pullNumberInput.setText(pullNumberMatcher.group(1));
        }
        else if(sqsMatcher.find() && exportMode != 6)
        {
            scanIdInput.setText(sqsMatcher.group(1));
            if(!(isManualCountMode || isAutoCountMode || sqsMatcher.group(2).equals("000")))
            {
                quantityInput.setText(sqsMatcher.group(2));
            }
        }
        else if(upcMatcher.find() && exportMode != 6)
        {
            scanIdInput.setText(scanInput);
            if(!isManualCountMode && quantityInput.getText().toString().isEmpty())
            {
                quantityInput.setText(DEFAULT_UPC_COUNT);
            }
        }
        else
        {
            Utilities.makeToast(this, "Invalid Scan");
            alertSoundVibrate();
        }

        handleInputs(scannerInitialsInput.getText().toString(), pullNumberInput.getText().toString(), scanIdInput.getText().toString(), quantityInput.getText().toString());
    }

    private void commitProductScan(String scannerInitialsValue, String pullInputValue, String scanInputValue, String quantityInputValue)
    {
        ScanRecord currentScanRecord = buildScanRecord(scannerInitialsValue, pullInputValue, scanInputValue, quantityInputValue, "1");
        int count = 0;

        do
        {
            scanAccess.insertRecord(currentScanRecord);
            count++;
        } while(isBoxQtyMode && count < boxQtyVal);
        displayScannedRecordCount();
        displayProductInfo(currentScanRecord);
        toastProductTotals(currentScanRecord);

        scanIdInput.setText("");
        resetQuantityInput();
    }

    private void commitSkidScan(String scannerInitialsValue, String pullInputValue, String quantityInputValue)
    {
        Utilities.makeToast(this, "Skid Scan Inserted");
        scanAccess.insertRecord(new ScanRecord(pullInputValue, quantityInputValue, scannerInitialsValue));
        displayScannedRecordCount();
        pullNumberInput.setText("");
        resetQuantityInput();
    }

    private void commitResetScan(String scannerInitialsValue, String pullInputValue, String scanInputValue, String quantityInputValue)
    {
        EditText boxQuantityInput = (EditText)findViewById(R.id.boxQtyNum);
        String boxQuantityInputValue = boxQuantityInput.getText().toString();
        int boxQuantity = 1;
        if(!boxQuantityInputValue.isEmpty() && Integer.parseInt(boxQuantityInputValue) > 1)
        {
            boxQuantity = Integer.parseInt(boxQuantityInputValue);
        }
        int quantity = Integer.parseInt(quantityInputValue) * boxQuantity;

        ScanRecord currentScanRecord = buildScanRecord(scannerInitialsValue, pullInputValue, scanInputValue, Integer.toString(quantity), Integer.toString(boxQuantity));
        scanAccess.insertRecord(currentScanRecord);
        displayScannedRecordCount();
        displayProductInfo(currentScanRecord);
        toastProductTotals(currentScanRecord);

        scanIdInput.setText("");
        boxQuantityInput.setText("");
        resetQuantityInput();
    }

    private ScanRecord buildScanRecord(String scannerInitialsValue, String pullInputValue, String scanInputValue, String quantityInputValue, String boxQuantityInputValue)
    {
        ProductRecord scannedProduct = new ProductRecord();
        PriceListRecord scannedProductPriceList = new PriceListRecord();
        if(upcRegEx.matcher(scanInputValue).matches())
        {
            if(isNewProductMode)
            {
                scanInputValue += "-N";
            }
            UPCRecord upcRecord = UPCRecord.buildNewUPCRecordFromCursor(upcAccess.selectByPk(scanInputValue));
            if(!upcRecord.getMasNum().isEmpty())
            {
                scannedProduct = ProductRecord.buildNewProductRecordFromCursor(productAccess.selectByPk(upcRecord.getMasNum()));
            }
        }
        else
        {
            scannedProduct = ProductRecord.buildNewProductRecordFromCursor(productAccess.selectByPk(scanInputValue));
        }

        if(!scannedProduct.getMasNum().isEmpty())
        {
            ProductLensRecord productPriceListJoin = ProductLensRecord.buildNewProductLensRecordFromCursor(productLensAccess.selectByMasNumLensId(scannedProduct.getMasNum(), selectedLensId));
            if(!productPriceListJoin.getProductLensId().isEmpty())
            {
                scannedProductPriceList = PriceListRecord.buildNewPriceListRecordFromCursor(priceListAccess.selectByPk(productPriceListJoin.getPriceListId()));
            }
        }
        else
        {
            scannedProduct.setMasNum(scanInputValue);
            Utilities.makeToast(this, "Bring Copy of Title to Dave Kinn");
        }

        String location = createScanLocationString();

        return new ScanRecord(scannedProduct.getMasNum(), quantityInputValue, pullInputValue, scannedProduct.getName(), scannedProductPriceList.getPriceList(),
                scannedProduct.getRating(), location, boxQuantityInputValue, scannerInitialsValue);
    }

    private String createScanLocationString()
    {
        String location = "o";
        String defGateway = Utilities.getDefaultGateway(this);
        if(defGateway.equals("3.150.168.192"))
        {
            location = "r";
        }
        else if(defGateway.equals("1.150.168.192"))
        {
            location = "p";
        }

        return location;
    }

    private void goToReview()
    {
        Intent intent = new Intent(this, PullReviewActivity.class);
        startActivityForResult(intent, 1);
    }

    private void goToConfig()
    {
        Intent intent = new Intent(this, ScanConfigActivity.class);
        startActivity(intent);
    }

    private void goToAdmin()
    {
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver()
    {
        private static final String TAG = "BroadcastReceiver";

        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.d(TAG, "in onReceive");

            if(intent.getAction().equalsIgnoreCase(ScanAPIApplication.NOTIFY_DECODED_DATA))
            {
                String data = new String(intent.getCharArrayExtra(ScanAPIApplication.EXTRA_DECODEDDATA));
                handleScanInput(data);
            }
            else if (intent.getAction().equalsIgnoreCase(ScanAPIApplication.NOTIFY_SCANNER_ARRIVAL))
            {
                Utilities.makeToast(context, intent.getStringExtra(ScanAPIApplication.EXTRA_DEVICENAME) + " Connected");
            }
            else if (intent.getAction().equalsIgnoreCase(ScanAPIApplication.NOTIFY_SCANPI_INITIALIZED))
            {
                Utilities.makeToast(context, "Ready to pair with scanner");
            }
            else if (intent.getAction().equalsIgnoreCase(ScanAPIApplication.NOTIFY_CLOSE_ACTIVITY))
            {
            }
            else if (intent.getAction().equalsIgnoreCase(ScanAPIApplication.NOTIFY_ERROR_MESSAGE))
            {
                Utilities.makeToast(context, intent.getStringExtra(ScanAPIApplication.EXTRA_ERROR_MESSAGE));
            }
        }
    };

    private final OnEditorActionListener scanHomeFieldListener = new OnEditorActionListener()
    {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
        {
            if(actionId == EditorInfo.IME_ACTION_DONE && !v.getText().toString().isEmpty())
            {
                handleInputs(scannerInitialsInput.getText().toString(), pullNumberInput.getText().toString(), scanIdInput.getText().toString(),
                        quantityInput.getText().toString());
            }
            return true;
        }
    };
}