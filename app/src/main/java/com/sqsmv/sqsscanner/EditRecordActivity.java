package com.sqsmv.sqsscanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.sqsmv.sqsscanner.DB.ScanDataSource;

import java.util.ArrayList;

public class EditRecordActivity extends Activity
{
    private ScanDataSource scanDataSource;
    private String editScanId;
    private String editOldQty;
    private String title;
    private String pullNum;
    private String customValue;
    private Spinner spPullNumbers;
    private int recordID;

    private ScanRecord editRecord;
    private Intent intent;
    private EditText scanMasnum;
    private EditText scanTitle;
    private EditText qty;
    private ArrayAdapter<String> spinAdp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_record);

        final Context context = this;

        scanDataSource = new ScanDataSource(this);
        scanDataSource.open();

        String[] pullNums = scanDataSource.getPullNums();
        ArrayList<String> pullNumbers = new ArrayList<String>();
        for(int i = 0; i < pullNums.length; i++)
        {
            pullNumbers.add(pullNums[i]);
        }
        pullNumbers.add("...");

        spinAdp = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, pullNumbers);

        spinAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        intent = getIntent();

        recordID = (intent.getExtras().getInt("EDIT_SCAN"));
        editRecord = new ScanRecord(scanDataSource.getValueByID(recordID));
        scanDataSource.close();

        pullNum = editRecord.getPullNumber();
        editScanId = editRecord.getPullNumber();
        editOldQty = editRecord.getQuantity();
        title = editRecord.getTitle();

        spPullNumbers = (Spinner) findViewById(R.id.spinPullNumbers);
        scanMasnum = (EditText) findViewById(R.id.editMasnum);
        scanTitle = (EditText) findViewById(R.id.editScanId);
        qty = (EditText) findViewById(R.id.editQtyNum);

        spPullNumbers.setAdapter(spinAdp);
        setSpinner(pullNum);
        customValue = pullNum;

        scanMasnum.setText(editRecord.getMasNum());
        scanTitle.setText(title);
        qty.setText(editOldQty);

        scanMasnum.setFocusable(false);
        scanMasnum.setEnabled(false);
        scanTitle.setFocusable(false);
        scanTitle.setEnabled(false);

        spPullNumbers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                //last item in list
                if(spPullNumbers.getSelectedItem().toString().matches("..."))
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);

                    alert.setTitle("Pull Number");
                    alert.setMessage("Enter PullNumber");

                    // Set an EditText view to get user input
                    final EditText input = new EditText(context);
                    alert.setView(input);
                    input.setText(customValue);

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int whichButton)
                        {
                            if(input.getText().toString().matches(""))
                            {
                                input.setText(customValue);
                            }
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
                    customValue = spPullNumbers.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {}
        });
    }

    public void onClickDone(View v)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Confirm Edit");

        // set dialog message
        alertDialogBuilder
                .setMessage("Confirm Edit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        String newQty = qty.getText().toString();
                        editRecord.setPullNumber(customValue);
                        editRecord.setQuantity(newQty);

                        scanDataSource.open();
                        scanDataSource.updateRecordByID(recordID, editRecord);
                        scanDataSource.close();
                        commitChange();
                    }
                })
                .setNeutralButton("No", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                        setSpinner(pullNum);
                        qty.setText(editOldQty);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        cancelEdit();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        if((qty.getText().toString().equals(editOldQty)) && (pullNum.equals(spPullNumbers.getSelectedItem().toString())))
        {
            cancelEdit();
        }
        else
        {
            alertDialog.show();
        }
    }

    /**
     * @param key
     */
    public void setSpinner(String key)
    {
        int i = 0;

        while(i < spinAdp.getCount())
        {
            if (key.equals(spPullNumbers.getItemAtPosition(i).toString()))
            {
                spPullNumbers.setSelection(i);
            }

            i++;
        }
    }

    public void cancelEdit()
    {
        Intent returnData = new Intent();
        setResult(RESULT_CANCELED, returnData);

        finish();
    }

    public void commitChange()
    {
        //Bundle bundle = new Bundle();
        Intent returnData = new Intent();
        setResult(RESULT_OK, returnData);

        finish();
    }
}
