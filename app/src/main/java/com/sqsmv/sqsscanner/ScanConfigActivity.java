package com.sqsmv.sqsscanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sqsmv.sqsscanner.DB.LensDataSource;
import com.sqsmv.sqsscanner.DB.ScanDataSource;

import java.util.ArrayList;

public class ScanConfigActivity  extends Activity
{
	private Spinner autoCount, lensSelect;
	private EditText boxQty;
    private DroidConfigManager appConfig;
	private String customValue = "0";
	private Context context;
	private String mark = "";
    private RadioGroup exportModeRGroup;
    private RadioGroup invModeRGroup;
    Button pairScanner;
    private int exportModeChoice;
	private int invModeChoice;
    private LensDataSource lensDataSource;
    private ScanDataSource scanDataSource;

	protected void onCreate(Bundle savedInstanceState)
    {
		setContentView(R.layout.activity_scan_config);
		super.onCreate(savedInstanceState);
		
		context = this;
        lensDataSource = new LensDataSource(this);
        scanDataSource = new ScanDataSource(this);

        appConfig = new DroidConfigManager(this);
        autoCount = (Spinner) findViewById(R.id.autoCountSelect);
        boxQty = (EditText) findViewById(R.id.boxQty);
        ((ToggleButton) findViewById(R.id.togAutoQuan)).setChecked(appConfig.accessBoolean(DroidConfigManager.IS_AUTO_COUNT, null, false));
        ((ToggleButton) findViewById(R.id.togAutoBox)).setChecked(appConfig.accessBoolean(DroidConfigManager.IS_BOX_QTY, null, false));
        lensSelect = (Spinner)findViewById(R.id.LensSelect);
        exportModeRGroup = (RadioGroup)findViewById(R.id.exportModeGroup);
        invModeRGroup = (RadioGroup)findViewById(R.id.invModeGroup);
        pairScanner =  (Button)findViewById(R.id.PairScanner);

		setSpinner(autoCount, R.array.autoCounts);
		autoCount.setSelection(appConfig.accessInt(DroidConfigManager.AUTO_COUNT_IDX, null, 0));
		boxQty.setText(appConfig.accessString(DroidConfigManager.BOX_QTY, null, "0"));
        setLensSpinner(lensSelect);
        lensSelect.setSelection(appConfig.accessInt(DroidConfigManager.LENS_SELECT_IDX, null, 0));
        exportModeChoice = appConfig.accessInt(DroidConfigManager.EXPORT_MODE_CHOICE, null, 1);
        setExportRadio();

        if(((RadioButton)findViewById(R.id.invAdjustMode)).isChecked())
			invModeRGroup.setVisibility(View.VISIBLE);
		else
			invModeRGroup.setVisibility(View.GONE);

        invModeChoice = appConfig.accessInt(DroidConfigManager.INVENTORY_MODE_CHOICE, null, 1);
		if(invModeChoice == 1)
			((RadioButton)findViewById(R.id.plus)).setChecked(true);
		else if(invModeChoice == 2)
			((RadioButton)findViewById(R.id.minus)).setChecked(true);

		autoCount.setOnItemSelectedListener(new OnItemSelectedListener()
		{
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
				{
					//last item in list
					if(parent.getCount()-1 == pos)
					{
						AlertDialog.Builder alert = new AlertDialog.Builder(context);

						alert.setTitle("Quantity");
						alert.setMessage("Enter Quantity");

						// Set an EditText view to get user input 
						final EditText input = new EditText(context);
						alert.setView(input);
						input.setText(customValue);

						alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int whichButton)
							{
								if(input.getText().toString().matches(""))
									input.setText("0");
								customValue = input.getText().toString();
							}
						});
						alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int whichButton)
							{
						    // Canceled.
							}
						});
						alert.show();
					}
					else
					{
						customValue = autoCount.getSelectedItem().toString();
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent)
                {}
		});
			
		findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onBackPressed();
			}
		});

        exportModeRGroup.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId)
            {
                switch(checkedId)
                {
                    case R.id.normalMode:
                        setExportMode(1);
                        break;
                    case R.id.consolidateMode:
                        setExportMode(2);
                        break;
                    case R.id.billBMode:
                        setExportMode(3);
                        break;
                    case R.id.drewMode:
                        setExportMode(4);
                        break;
                    case R.id.skidMode:
                        setExportMode(6);
                        break;
                    case R.id.invAdjustMode:
                        setExportMode(5);
                        break;
                }
            }
        });

		invModeRGroup.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch(checkedId)
                {
                case R.id.plus:
                	invModeChoice = 1;
                    break;
                case R.id.minus:
                	invModeChoice = 2;
                    break;
                }
            }
        });

        pairScanner.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                goToPairActivity();
            }
        });
	}//end onCreate
	
	private boolean checkValues()
    {
		if(boxQty.getText().toString().matches(""))
			boxQty.setText("0");
		
		if(Integer.parseInt(boxQty.getText().toString()) > 100){
			
			Toast.makeText(this, "Box Quantity Value must be between 0-100", Toast.LENGTH_LONG).show();
			return false;
		}
		else if(Integer.parseInt(customValue) > 410 ){
			
			Toast.makeText(this,"Auto Quantity must be between 0-410", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed()
	{
		if(checkValues())
		{
			writePref();	
			startActivity(getSkidIdIntent());
			super.onBackPressed();
		}
	}
	
	
	private Intent getSkidIdIntent()
	{
		Intent intent = new Intent(this, ScanHomeActivity.class);
		
		//sends the selected skid to the Scan Activity
		Bundle returnData = new Bundle();
		returnData.putString("MARK_ID", mark);
		intent.putExtras(returnData);
		return intent;		
	}

    private void setExportRadio()
    {
        switch(exportModeChoice)
        {
            case 1:
                ((RadioButton)findViewById(R.id.normalMode)).setChecked(true);
                break;
            case 2:
                ((RadioButton)findViewById(R.id.consolidateMode)).setChecked(true);
                break;
            case 3:
                ((RadioButton)findViewById(R.id.billBMode)).setChecked(true);
                break;
            case 4:
                ((RadioButton)findViewById(R.id.drewMode)).setChecked(true);
                break;
            case 5:
                ((RadioButton)findViewById(R.id.invAdjustMode)).setChecked(true);
                break;
            case 6:
                ((RadioButton)findViewById(R.id.skidMode)).setChecked(true);
                break;
        }
    }

    private void setExportMode(int modeChoice)
    {
        switch(modeChoice)
        {
            case 1:
            case 2:
            case 3:
            case 4:
                if(exportModeChoice == 6 && scanDataSource.getAllScans().getCount() > 0)
                {
                    Utilities.makeToast(this, "Please commit scans first.");
                    setExportRadio();
                }
                else
                {
                    exportModeChoice = modeChoice;
                    hideInvAdjust();
                }
                break;
            case 5:
                if(exportModeChoice == 6 && scanDataSource.getAllScans().getCount() > 0)
                {
                    Utilities.makeToast(this, "Please commit scans first.");
                    setExportRadio();
                }
                else
                {
                    exportModeChoice = modeChoice;
                    showInvAdjust();
                }
                break;
            case 6:
                if(exportModeChoice != 6 && scanDataSource.getAllScans().getCount() > 0)
                {
                    Utilities.makeToast(this, "Please commit scans first.");
                    setExportRadio();
                }
                else
                {
                    exportModeChoice = modeChoice;
                }
                break;
        }
    }
	
						
	/**
	 * 
	 */
	public void writePref()
	{
		if(lensSelect.getSelectedItem() != null)
        {
            appConfig.accessString(DroidConfigManager.LENS_SELECTION_ID, lensDataSource.getLensId(lensSelect.getSelectedItem().toString()), "");
            appConfig.accessInt(DroidConfigManager.LENS_SELECT_IDX, lensSelect.getSelectedItemPosition(), 0);
            appConfig.accessString(DroidConfigManager.LENS_SELECTION, lensSelect.getSelectedItem().toString(), "");
        }
        else
        {
            appConfig.accessString(DroidConfigManager.LENS_SELECTION_ID, "1", "");
            appConfig.accessInt(DroidConfigManager.LENS_SELECT_IDX, 0, 0);
            appConfig.accessString(DroidConfigManager.LENS_SELECTION, "Default", "");
        }

        appConfig.accessInt(DroidConfigManager.AUTO_COUNT_IDX, autoCount.getSelectedItemPosition(), 0);
		appConfig.accessString(DroidConfigManager.BOX_QTY, boxQty.getText().toString(), "0");
		appConfig.accessBoolean(DroidConfigManager.IS_AUTO_COUNT, ((ToggleButton) findViewById(R.id.togAutoQuan)).isChecked(), false);
		appConfig.accessBoolean(DroidConfigManager.IS_BOX_QTY, ((ToggleButton) findViewById(R.id.togAutoBox)).isChecked(), false);
        appConfig.accessInt(DroidConfigManager.EXPORT_MODE_CHOICE, exportModeChoice, 1);
		appConfig.accessInt(DroidConfigManager.INVENTORY_MODE_CHOICE, invModeChoice, 1);

		appConfig.accessInt(DroidConfigManager.AUTO_COUNT, Integer.parseInt(customValue), 0);
	}
		
	/**
	 * @param spinner
	 * @param resource
	 */
	private void setSpinner(Spinner spinner, int resource ){
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, resource, R.layout.spinner_list);
		adapter.setDropDownViewResource(R.layout.spinner_item);
		spinner.setAdapter(adapter);
	}

    private void setLensSpinner(Spinner spinner)
    {
        lensDataSource.read();
        ArrayList<String> lensList = lensDataSource.getAllLensNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_list, lensDataSource.getAllLensNames());
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        lensDataSource.close();
    }
	
	private void showInvAdjust()
	{
        invModeRGroup.setVisibility(View.VISIBLE);
    }

    private void hideInvAdjust()
    {
        invModeRGroup.setVisibility(View.GONE);
    }

    @Override
    protected void onResume()
    {
        lensDataSource.read();
        scanDataSource.read();
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        lensDataSource.close();
        scanDataSource.close();
        super.onPause();
    }

    private void goToPairActivity()
    {
        Intent intent = new Intent(this, SocketMobilePairActivity.class);
        startActivity(intent);
    }
}
