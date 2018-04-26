package com.sqsmv.sqsscanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.sqsmv.sqsscanner.database.DBAdapter;
import com.sqsmv.sqsscanner.database.scan.ScanAccess;
import com.sqsmv.sqsscanner.database.scan.ScanRecord;

import java.util.ArrayList;

/**
 * The Activity that allows uers to edit the product quantity and pull number of a specific scan.
 */
public class EditRecordActivity extends Activity
{
    private DBAdapter dbAdapter;
    private ScanAccess scanAccess;
    private ScanRecord currentScanRecord;

    private Spinner pullSpinner;
    private EditText quantityEdit;

    private ArrayAdapter<String> pullListSpinnerAdapter;

    private ArrayList<String> pullList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_record);

        dbAdapter = new DBAdapter(this);
        scanAccess = new ScanAccess(dbAdapter);
        scanAccess.open();

        currentScanRecord = ScanRecord.buildNewScanRecordFromCursor(scanAccess.selectByPk(getIntent().getStringExtra("EDIT_SCAN")));
        pullList = scanAccess.getPullNums();
        pullList.add("...");
        dbAdapter.close();

        pullSpinner = findViewById(R.id.spinPullNumbers);
        quantityEdit = findViewById(R.id.editQtyNum);

        pullListSpinnerAdapter = Utilities.createSpinnerAdapter(this, pullList);
        pullSpinner.setAdapter(pullListSpinnerAdapter);
        resetRecord();
        ((TextView)findViewById(R.id.editMasnum)).setText(currentScanRecord.getMasNum());
        ((TextView)findViewById(R.id.editScanTitle)).setText(currentScanRecord.getTitle());
        ((TextView)findViewById(R.id.editDate)).setText(currentScanRecord.getScanDate());
        ((TextView)findViewById(R.id.editBoxQuantity)).setText(currentScanRecord.getNumBoxes());
        ((TextView)findViewById(R.id.editScannerInitials)).setText(currentScanRecord.getInitials());

        setListeners();
    }

    /**
     * Resets the screen to the original scan.
     */
    private void resetRecord()
    {
        quantityEdit.setText(currentScanRecord.getQuantity());
        resetPullSpinner();
    }

    /**
     * Resets the pull spinner to point at the original pull number.
     */
    private void resetPullSpinner()
    {
        pullSpinner.setSelection(pullList.indexOf(currentScanRecord.getFkPullId()));
    }

    /**
     * Sets the listeners used for the current Activity's GUI elements.
     */
    private void setListeners()
    {
        findViewById(R.id.editDoneBtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onClickDone();
            }
        });

        pullSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                if(pos == parent.getCount() - 1)
                {
                    displayNewPullDialog();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });
    }

    /**
     * Displays a dialog to prompt user for a new pull number value that hasn't been used for the current set of scans.
     */
    private void displayNewPullDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setGravity(Gravity.CENTER);

        alertDialogBuilder
                .setTitle("Pull Number")
                .setMessage("Enter PullNumber")
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        String inputValue = input.getText().toString();
                        if(inputValue.isEmpty())
                        {
                            resetPullSpinner();
                        }
                        else if(pullList.indexOf(inputValue) == -1)
                        {
                            pullList.add(pullList.size() - 1, inputValue);
                            pullListSpinnerAdapter.notifyDataSetChanged();
                        }
                        else
                        {
                            pullSpinner.setSelection(pullList.indexOf(inputValue));
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        resetPullSpinner();
                    }
                })
                .show();
    }

    /**
     * Prompts user to commit edit if any changes have been made. Otherwise it acts as if the back button was pressed.
     */
    private void onClickDone()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final String pullSpinnerValue = pullSpinner.getSelectedItem().toString();
        final String quantityInputValue = quantityEdit.getText().toString();

        alertDialogBuilder
                .setTitle("Confirm Edit")
                .setMessage("Confirm Edit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        if(pullSpinnerValue.equals("..."))
                        {
                            resetPullSpinner();
                        }

                        scanAccess.open();

                        if(!quantityInputValue.isEmpty() && Integer.parseInt(quantityInputValue) > 0)
                        {
                            currentScanRecord.setFkPullId(pullSpinnerValue);
                            currentScanRecord.setQuantity(quantityInputValue);
                            scanAccess.insertRecord(currentScanRecord);
                        }
                        else
                        {
                            scanAccess.deleteByPk(currentScanRecord.getId());
                        }

                        dbAdapter.close();
                        onBackPressed();
                    }
                })
                .setNeutralButton("No", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        resetRecord();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        onBackPressed();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        if(quantityInputValue.equals(currentScanRecord.getQuantity()) && pullSpinnerValue.equals(currentScanRecord.getFkPullId()))
        {
            onBackPressed();
        }
        else
        {
            alertDialog.show();
        }
    }
}
