package com.example.sqsscanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ScanConfigActivity  extends Activity  {

	private Spinner autoCount;
	private EditText boxQty;
	private SharedPreferences scanConfig;
	private String customValue = "0";
	private Context context;
	private String mark = "";
	ToggleButton billBToggle, invAdjustToggle;
	RadioGroup invModeRGroup;
	int invModeChoice;
	
	protected void onCreate(Bundle savedInstanceState) {		
		setContentView(R.layout.activity_scan_config);
		super.onCreate(savedInstanceState);
		
		context = this;
		
		scanConfig = getSharedPreferences("scanConfig", 0);
		
		autoCount = (Spinner) findViewById(R.id.autoCountSelect);
		setSpinner(autoCount, R.array.autoCounts);
		autoCount.setSelection(scanConfig.getInt("autoCountIdx", 0));
		boxQty = (EditText) findViewById(R.id.boxQty);
		boxQty.setText(scanConfig.getString("boxQty", "0"));
		((ToggleButton) findViewById(R.id.togAutoQuan)).setChecked(scanConfig.getBoolean("isAutoCount", false));
		((ToggleButton) findViewById(R.id.togAutoBox)).setChecked(scanConfig.getBoolean("isBoxQty", false));
		billBToggle = (ToggleButton)findViewById(R.id.billBMode);
		billBToggle.setChecked(scanConfig.getBoolean("isBillB", false));
		invAdjustToggle = (ToggleButton)findViewById(R.id.invAdjustMode);
		invAdjustToggle.setChecked(scanConfig.getBoolean("isInvAdj", false));
		invModeRGroup = (RadioGroup)findViewById(R.id.invModeGroup);
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
	}//end onCreate
	
	private boolean checkValues(){
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
		
		scanState.putInt("autoCountIdx", autoCount.getSelectedItemPosition());
		scanState.putString("boxQty", boxQty.getText().toString());
		scanState.putBoolean("isAutoCount", ((ToggleButton) findViewById(R.id.togAutoQuan)).isChecked());
		scanState.putBoolean("isBoxQty", ((ToggleButton) findViewById(R.id.togAutoBox)).isChecked());
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
