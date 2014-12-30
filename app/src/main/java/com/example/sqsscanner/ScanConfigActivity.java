package com.example.sqsscanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.sqsscanner.DB.LensDataSource;

import java.util.ArrayList;

public class ScanConfigActivity  extends Activity
{
	private Spinner autoCount, lensSelect;
	private EditText boxQty;
	private SharedPreferences scanConfig;
	private String customValue = "0";
	private Context context;
	private String mark = "";
    private EditText billBAccess;
    private LinearLayout billBArea;
    private ToggleButton billBToggle, invAdjustToggle;
	private RadioGroup invModeRGroup;
    Button lockBillBAccess, pairScanner;
    private boolean hasBillBAccess;
	private int invModeChoice;
    private LensDataSource lensDataSource;

	protected void onCreate(Bundle savedInstanceState)
    {
		setContentView(R.layout.activity_scan_config);
		super.onCreate(savedInstanceState);
		
		context = this;
        lensDataSource = new LensDataSource(this);

        scanConfig = getSharedPreferences("scanConfig", 0);
        autoCount = (Spinner) findViewById(R.id.autoCountSelect);
        boxQty = (EditText) findViewById(R.id.boxQty);
        ((ToggleButton) findViewById(R.id.togAutoQuan)).setChecked(scanConfig.getBoolean("isAutoCount", false));
        ((ToggleButton) findViewById(R.id.togAutoBox)).setChecked(scanConfig.getBoolean("isBoxQty", false));
        lensSelect = (Spinner)findViewById(R.id.LensSelect);
        billBAccess = (EditText)findViewById(R.id.BillBAccess);
        billBArea = (LinearLayout)findViewById(R.id.BillBArea);
        billBToggle = (ToggleButton)findViewById(R.id.billBMode);
        invAdjustToggle = (ToggleButton)findViewById(R.id.invAdjustMode);
        invModeRGroup = (RadioGroup)findViewById(R.id.invModeGroup);
        lockBillBAccess = (Button)findViewById(R.id.LockBillBAccess);
        pairScanner =  (Button)findViewById(R.id.PairScanner);

		setSpinner(autoCount, R.array.autoCounts);
		autoCount.setSelection(scanConfig.getInt("autoCountIdx", 0));
		boxQty.setText(scanConfig.getString("boxQty", "0"));
        setLensSpinner(lensSelect);
        lensSelect.setSelection(scanConfig.getInt("lensSelectIdx", 0));
        //setSpinner(lensSelect, R.array.lensList);
        lensSelect.setSelection(scanConfig.getInt("lensSelectIdx", 0));
        hasBillBAccess = scanConfig.getBoolean("hasBillBAccess", false);
        billBToggle.setChecked(scanConfig.getBoolean("isBillB", false));
		invAdjustToggle.setChecked(scanConfig.getBoolean("isInvAdj", false));
		if(invAdjustToggle.isChecked())
			invModeRGroup.setVisibility(View.VISIBLE);
		else
			invModeRGroup.setVisibility(View.GONE);
		invModeChoice = scanConfig.getInt("InventoryModeChoice", 1);
		if(invModeChoice == 1)
			((RadioButton)findViewById(R.id.plus)).setChecked(true);
		else if(invModeChoice == 2)
			((RadioButton)findViewById(R.id.minus)).setChecked(true);
		else if(invModeChoice == 3)
			((RadioButton)findViewById(R.id.set)).setChecked(true);

        if(!hasBillBAccess)
            toggleBillBVisibility();
		//setBluToothImg();
		
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
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					
				}	
		});
			
		findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

        billBAccess.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    if(billBAccess.getText().toString().matches("0"))
                    {
                        hasBillBAccess = true;
                        toggleBillBVisibility();
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(billBAccess.getWindowToken(), 0);
                    }
                    billBAccess.setText("");
                }
                return true;
            }
        });
		
		billBToggle.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(!billBToggle.isChecked())
				{
                    ((RadioButton)findViewById(R.id.plus)).setChecked(true);
				}
			}
		});
		 
		invAdjustToggle.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				toggleInvAdjustSettings();
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
                case R.id.set:
                	invModeChoice = 3;
                    billBToggle.setChecked(true);
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

        lockBillBAccess.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                toggleBillBVisibility();
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
	
	/*
	private void setBluToothImg()
	{
		int[] images = {R.drawable.d1_blutooth_addr, 
					    R.drawable.d2_blutooth_addr,
					    R.drawable.d3_blutooth_addr,
					    R.drawable.d5_blutooth_addr,
					    R.drawable.d6_blutooth_addr};
		
		String deviceName = BluetoothAdapter.getDefaultAdapter().getName();
		ImageView imBt = (ImageView) findViewById(R.id.bt_address);
		try
		{
			int deviceId = Integer.parseInt(deviceName.substring(1));		
			imBt.setImageResource(images[(deviceId - 1)]);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/

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
	
						
	/**
	 * 
	 */
	public void writePref()
	{
		SharedPreferences.Editor scanState = scanConfig.edit();
        String lensSelectString = "";
        if(lensSelect.getSelectedItem() != null)
        {
            scanState.putString("lensSelectionId", lensDataSource.getLensId(lensSelect.getSelectedItem().toString()));
            scanState.putInt("lensSelectIdx", lensSelect.getSelectedItemPosition());
            scanState.putString("lensSelection", lensSelect.getSelectedItem().toString());
        }
        else
        {
            scanState.putString("lensSelectionId", "1");
            scanState.putInt("lensSelectIdx", 0);
            scanState.putString("lensSelection", "Default");
        }

        scanState.putInt("autoCountIdx", autoCount.getSelectedItemPosition());
		scanState.putString("boxQty", boxQty.getText().toString());
		scanState.putBoolean("isAutoCount", ((ToggleButton) findViewById(R.id.togAutoQuan)).isChecked());
		scanState.putBoolean("isBoxQty", ((ToggleButton) findViewById(R.id.togAutoBox)).isChecked());
        scanState.putBoolean("hasBillBAccess", hasBillBAccess);
		scanState.putBoolean("isBillB", ((ToggleButton) findViewById(R.id.billBMode)).isChecked());
		scanState.putBoolean("isInvAdj", ((ToggleButton) findViewById(R.id.invAdjustMode)).isChecked());
		scanState.putInt("InventoryModeChoice", invModeChoice);
		//scanState.putBoolean("isManQty", ((ToggleButton) findViewById(R.id.manualQty)).isChecked());
		//scanState.putBoolean("isNewProduct", ((ToggleButton) findViewById(R.id.newProductMode)).isChecked());
			
		//if(this.customValue.equals("0"))
			//scanState.putInt("autoCount", Integer.parseInt(autoCount.getSelectedItem().toString()));
		//else
		scanState.putInt("autoCount", Integer.parseInt(customValue));
		
		scanState.commit();

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
	
	private void toggleInvAdjustSettings()
	{
		if(invAdjustToggle.isChecked())
		{	
			//billBToggle.setChecked(true);
			invModeRGroup.setVisibility(View.VISIBLE);
		}
		else
		{
			//billBToggle.setChecked(false);
			invModeRGroup.setVisibility(View.GONE);
		}
	}

    private void toggleBillBVisibility()
    {
        if(billBArea.getVisibility() == View.VISIBLE)
        {
            billBAccess.setVisibility(View.VISIBLE);
            billBArea.setVisibility(View.GONE);
            lockBillBAccess.setVisibility(View.GONE);
            billBToggle.setChecked(false);
            invAdjustToggle.setChecked(false);
            hasBillBAccess = false;
        }
        else
        {
            billBAccess.setVisibility(View.GONE);
            billBArea.setVisibility(View.VISIBLE);
            lockBillBAccess.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume()
    {
        lensDataSource.read();
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        lensDataSource.close();
        super.onPause();
    }

    private void goToPairActivity()
    {
        Intent intent = new Intent(this, SocketMobilePairActivity.class);
        startActivity(intent);
    }
	
	/*
	public void setSkidMode(View v){
		
		if(((ToggleButton) v).isChecked()){
			//create skid id
			Calendar c = Calendar.getInstance();
			mark = Integer.toString(c.get(Calendar.HOUR)) + Integer.toString(c.get(Calendar.MILLISECOND));
	    	((TextView)this.findViewById(R.id.skid_id)).setText(this.mark);
		}
		else{
			mark = "";
			((TextView)this.findViewById(R.id.skid_id)).setText("");
			//updateMarks();
		}	
	}*/
}
