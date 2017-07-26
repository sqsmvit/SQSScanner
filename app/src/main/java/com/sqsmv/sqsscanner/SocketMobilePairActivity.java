package com.sqsmv.sqsscanner;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ToggleButton;

import com.onbarcode.barcode.android.AndroidColor;
import com.onbarcode.barcode.android.AndroidFont;
import com.onbarcode.barcode.android.Code128;
import com.onbarcode.barcode.android.IBarcode;

/**
 * The Activity that contains barcodes, both the SPP and the barcode with the phone's bluetooth address, for pairing SocketMobile scanners with the
 * phone. From this Activity, the scanner can also be locked to this app.
 */
public class SocketMobilePairActivity extends Activity
{
    private DroidConfigManager appConfig;

    BarcodeView sppBarcodeView, btaddrBarcodeView;
    ToggleButton scannerLockToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_mobile_pair);

        String sppData = "#FNB00F40000#";
        String btAddr = "#FNI" + getBTAddr() + "#";

        appConfig = new DroidConfigManager(this);

        sppBarcodeView = (BarcodeView)findViewById(R.id.SPPBarcodeView);
        btaddrBarcodeView = (BarcodeView)findViewById(R.id.BTAddrBarcodeView);
        scannerLockToggle = (ToggleButton)findViewById(R.id.scannerLockToggle);

        scannerLockToggle.setChecked(appConfig.accessBoolean(DroidConfigManager.SCANNER_LOCK, null, false));

        sppBarcodeView.drawBarcode(makeBarcode(sppData, 0));

        btaddrBarcodeView.drawBarcode(makeBarcode(btAddr, 0));

        findViewById(R.id.pairBackButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        saveScanLock();
        super.onDestroy();
    }

    /**
     * Gets the phone's bluetooth address and cleans out the
     * @return The phone's bluetooth
     */
    private String getBTAddr()
    {
        String deviceAddress = BluetoothAdapter.getDefaultAdapter().getAddress();
        return deviceAddress.replace(":", "");
    }

    /**
     * 
     * @param data
     * @param width
     * @return
     */
    private Code128 makeBarcode(String data, float width)
    {
        Code128 barcode = new Code128();
        /*
           Code 128 Valid data char set:
                all 128 ASCII characters (Char from 0 to 127)
        */
        barcode.setData(data);

        //  Set the processTilde property to true, if you want use the tilde character "~"
        //  to specify special characters in the input data. Default is false.
        //  1) All 128 ISO/IEC 646 characters, i.e. characters 0 to 127 inclusive, in accordance with ISO/IEC 646.
        //       NOTE This version consists of the G0 set of ISO/IEC 646 and the C0 set of ISO/IEC 6429 with values 28 - 31
        //       modified to FS, GS, RS and US respectively.
        //  2) Characters with byte values 128 to 255 may also be encoded.
        //  3) 4 non-data function characters.
        //  4) 4 code set selection characters.
        //  5) 3 Start characters.
        //  6) 1 Stop character.
        barcode.setProcessTilde(false);

        // Unit of Measure, pixel, cm, or inch
        barcode.setUom(IBarcode.UOM_PIXEL);
        // barcode bar module width (X) in pixel
        TypedValue outValue = new TypedValue();
        getResources().getValue(R.dimen.barcodeWidth, outValue, true);
        float barcodeWidth = outValue.getFloat();
        barcode.setX(barcodeWidth);
        // barcode bar module height (Y) in pixel
        barcode.setY(75);
        // set barcode width
        barcode.setBarcodeWidth(width);

        // barcode image margins
        barcode.setLeftMargin(10);
        barcode.setRightMargin(10);
        barcode.setTopMargin(10);
        barcode.setBottomMargin(10);

        // barcode image resolution in dpi
        barcode.setResolution(72);

        // disply barcode encoding data below the barcode
        barcode.setShowText(true);
        // barcode encoding data font style
        barcode.setTextFont(new AndroidFont("Arial", Typeface.NORMAL, 12));
        // space between barcode and barcode encoding data
        barcode.setTextMargin(6);
        barcode.setTextColor(AndroidColor.black);

        // barcode bar color and background color in Android device
        barcode.setForeColor(AndroidColor.black);
        barcode.setBackColor(AndroidColor.white);

        return barcode;
    }

    /**
     * Writes the scanner lock preference to the config file.
     */
    private void saveScanLock()
    {
        appConfig.accessBoolean(DroidConfigManager.SCANNER_LOCK, scannerLockToggle.isChecked(), false);
    }
}
