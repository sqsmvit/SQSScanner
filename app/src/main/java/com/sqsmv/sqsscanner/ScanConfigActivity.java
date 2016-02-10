package com.sqsmv.sqsscanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.sqsmv.sqsscanner.database.DBAdapter;
import com.sqsmv.sqsscanner.database.lens.LensAccess;
import com.sqsmv.sqsscanner.database.scan.ScanAccess;

import java.util.ArrayList;
import java.util.Arrays;

public class ScanConfigActivity  extends Activity
{
    private DroidConfigManager appConfig;

    private DBAdapter dbAdapter;
    private LensAccess lensAccess;
    private ScanAccess scanAccess;

    private ToggleButton autoCountModeToggle, boxQuantityModeToggle;
    private Spinner autoCountSelect, lensSelect;
    private EditText boxQuantityInput;
    private RadioGroup exportModeRGroup, invModeRGroup;

    private String customAutoValue;
    private int exportModeChoice, invModeChoice;

    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_scan_config);
        super.onCreate(savedInstanceState);

        appConfig = new DroidConfigManager(this);

        dbAdapter = new DBAdapter(this);
        lensAccess = new LensAccess(dbAdapter);
        scanAccess = new ScanAccess(dbAdapter);

        autoCountModeToggle = (ToggleButton)findViewById(R.id.togAutoQuan);
        boxQuantityModeToggle = (ToggleButton)findViewById(R.id.togAutoBox);
        autoCountSelect = (Spinner)findViewById(R.id.autoCountSelect);
        boxQuantityInput = (EditText)findViewById(R.id.boxQty);
        lensSelect = (Spinner)findViewById(R.id.LensSelect);
        exportModeRGroup = (RadioGroup)findViewById(R.id.exportModeGroup);
        invModeRGroup = (RadioGroup)findViewById(R.id.invModeGroup);

        customAutoValue = appConfig.accessString(DroidConfigManager.CUSTOM_AUTO_COUNT, null, "0");
        exportModeChoice = appConfig.accessInt(DroidConfigManager.EXPORT_MODE_CHOICE, null, 1);
        invModeChoice = appConfig.accessInt(DroidConfigManager.INVENTORY_MODE_CHOICE, null, 1);

        initGUIElements();
        setListeners();
    }

    @Override
    protected void onResume()
    {
        lensAccess.open();
        scanAccess.open();
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        dbAdapter.close();
        super.onPause();
    }

    @Override
    public void onBackPressed()
    {
        if(checkValues())
        {
            writePref();
            super.onBackPressed();
        }
    }

    private void setListeners()
    {
        autoCountSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                if(pos == parent.getCount() - 1)
                {
                    displayCustomAutoCountDialog();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });
        autoCountSelect.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                boolean consumeCallback = false;
                if(autoCountSelect.getSelectedItemPosition() == autoCountSelect.getCount() - 1)
                {
                    displayCustomAutoCountDialog();
                    consumeCallback = true;
                }
                return consumeCallback;
            }
        });

        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        exportModeRGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
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

        invModeRGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
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

        findViewById(R.id.PairScanner).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                goToPairActivity();
            }
        });
    }

    private boolean checkValues()
    {
        boolean hasGoodValues = true;

        if(boxQuantityModeToggle.isChecked())
        {
            if(boxQuantityInput.getText().toString().matches(""))
            {
                boxQuantityInput.setText("1");
            }
            if(Integer.parseInt(boxQuantityInput.getText().toString()) < 1 || Integer.parseInt(boxQuantityInput.getText().toString()) > 100)
            {
                Utilities.makeLongToast(this, "Box Quantity Value must be between 1-100");
                hasGoodValues = false;
            }
        }
        else if(autoCountModeToggle.isChecked())
        {
            if(autoCountSelect.getSelectedItem().toString().equals("...") && Integer.parseInt(customAutoValue) > 410)
            {
                Utilities.makeLongToast(this, "Auto Quantity must be between 0-410");
                hasGoodValues = false;
            }
        }
        return hasGoodValues;
    }

    private void initGUIElements()
    {
        autoCountModeToggle.setChecked(appConfig.accessBoolean(DroidConfigManager.IS_AUTO_COUNT, null, false));
        autoCountSelect.setAdapter(Utilities.createSpinnerAdapter(this, new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.autoCounts)))));
        if(!(appConfig.accessInt(DroidConfigManager.AUTO_COUNT_IDX, null, 0) == (autoCountSelect.getCount() - 1)) || autoCountModeToggle.isChecked())
        {
            autoCountSelect.setSelection(appConfig.accessInt(DroidConfigManager.AUTO_COUNT_IDX, null, 0));
        }
        else
        {
            autoCountSelect.setSelection(0);
        }

        boxQuantityModeToggle.setChecked(appConfig.accessBoolean(DroidConfigManager.IS_BOX_QTY, null, false));
        boxQuantityInput.setText(Integer.toString(appConfig.accessInt(DroidConfigManager.BOX_QTY, null, 1)));
        lensAccess.open();
        lensSelect.setAdapter(Utilities.createSpinnerAdapter(this, lensAccess.getAllLensNames()));
        dbAdapter.close();
        lensSelect.setSelection(appConfig.accessInt(DroidConfigManager.LENS_SELECT_IDX, null, 0));
        initExportRadio();

        if(((RadioButton)findViewById(R.id.invAdjustMode)).isChecked())
        {
            invModeRGroup.setVisibility(View.VISIBLE);
        }
        else
        {
            invModeRGroup.setVisibility(View.GONE);
        }

        if(invModeChoice == 1)
        {
            ((RadioButton)findViewById(R.id.plus)).setChecked(true);
        }
        else if(invModeChoice == 2)
        {
            ((RadioButton)findViewById(R.id.minus)).setChecked(true);
        }
    }

    private void initExportRadio()
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
                if(exportModeChoice == 6 && scanAccess.getTotalScans() > 0)
                {
                    Utilities.makeToast(this, "Please commit scans first.");
                    initExportRadio();
                }
                else
                {
                    exportModeChoice = modeChoice;
                    hideInvAdjust();
                }
                break;
            case 5:
                if(exportModeChoice == 6 && scanAccess.getTotalScans() > 0)
                {
                    Utilities.makeToast(this, "Please commit scans first.");
                    initExportRadio();
                }
                else
                {
                    exportModeChoice = modeChoice;
                    showInvAdjust();
                }
                break;
            case 6:
                if(exportModeChoice != 6 && scanAccess.getTotalScans() > 0)
                {
                    Utilities.makeToast(this, "Please commit scans first.");
                    initExportRadio();
                }
                else
                {
                    exportModeChoice = modeChoice;
                    hideInvAdjust();
                }
                break;
        }
    }

    public void writePref()
    {
        if(lensSelect.getSelectedItem() != null)
        {
            appConfig.accessString(DroidConfigManager.LENS_SELECTION_ID, lensAccess.getLensId(lensSelect.getSelectedItem().toString()), "");
            appConfig.accessInt(DroidConfigManager.LENS_SELECT_IDX, lensSelect.getSelectedItemPosition(), 0);
            appConfig.accessString(DroidConfigManager.LENS_SELECTION, lensSelect.getSelectedItem().toString(), "");
        }

        if(appConfig.accessBoolean(DroidConfigManager.IS_AUTO_COUNT, autoCountModeToggle.isChecked(), false))
        {
            appConfig.accessInt(DroidConfigManager.AUTO_COUNT_IDX, autoCountSelect.getSelectedItemPosition(), 0);
            if(appConfig.accessString(DroidConfigManager.AUTO_COUNT, autoCountSelect.getSelectedItem().toString(), "0").equals("..."))
            {
                appConfig.accessString(DroidConfigManager.AUTO_COUNT, customAutoValue, "0");
            }
            appConfig.accessString(DroidConfigManager.CUSTOM_AUTO_COUNT, customAutoValue, "0");
        }

        if(appConfig.accessBoolean(DroidConfigManager.IS_BOX_QTY, boxQuantityModeToggle.isChecked(), false))
        {
            appConfig.accessInt(DroidConfigManager.BOX_QTY, Integer.parseInt(boxQuantityInput.getText().toString()), 1);
        }

        appConfig.accessInt(DroidConfigManager.EXPORT_MODE_CHOICE, exportModeChoice, 1);
        appConfig.accessInt(DroidConfigManager.INVENTORY_MODE_CHOICE, invModeChoice, 1);
    }

    private void displayCustomAutoCountDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        input.setGravity(Gravity.CENTER);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setText(customAutoValue);

        alertDialogBuilder
                .setTitle("Quantity")
                .setMessage("Enter Quantity")
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        if(input.getText().toString().matches(""))
                        {
                            input.setText("0");
                        }
                        customAutoValue = input.getText().toString();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void showInvAdjust()
    {
        invModeRGroup.setVisibility(View.VISIBLE);
    }

    private void hideInvAdjust()
    {
        invModeRGroup.setVisibility(View.GONE);
    }

    private void goToPairActivity()
    {
        Intent intent = new Intent(this, SocketMobilePairActivity.class);
        startActivity(intent);
    }
}
