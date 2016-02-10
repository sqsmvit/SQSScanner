package com.sqsmv.sqsscanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
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

        pullSpinner = (Spinner)findViewById(R.id.spinPullNumbers);
        quantityEdit = (EditText)findViewById(R.id.editQtyNum);

        pullListSpinnerAdapter = Utilities.createSpinnerAdapter(this, pullList);
        pullSpinner.setAdapter(pullListSpinnerAdapter);
        resetRecord();
        ((TextView)findViewById(R.id.editMasnum)).setText(currentScanRecord.getMasNum());
        ((TextView)findViewById(R.id.editScanTitle)).setText(currentScanRecord.getTitle());
        ((TextView)findViewById(R.id.editDate)).setText(currentScanRecord.getScanDate());
        ((TextView)findViewById(R.id.editScannerInitials)).setText(currentScanRecord.getInitials());

        setListeners();
    }

    private void resetRecord()
    {
        quantityEdit.setText(currentScanRecord.getQuantity());
        resetPullSpinner();
    }

    private void resetPullSpinner()
    {
        pullSpinner.setSelection(pullList.indexOf(currentScanRecord.getFkPullId()));
    }

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

    private void displayNewPullDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        alertDialogBuilder
                .setTitle("Pull Number")
                .setMessage("Enter PullNumber")
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        pullList.add(pullList.size() - 1, input.getText().toString());
                        pullListSpinnerAdapter.notifyDataSetChanged();
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

    private void onClickDone()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setTitle("Confirm Edit")
                .setMessage("Confirm Edit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        if(pullSpinner.getSelectedItem().toString().equals("..."))
                        {
                            resetPullSpinner();
                        }
                        currentScanRecord.setFkPullId(pullSpinner.getSelectedItem().toString());
                        currentScanRecord.setQuantity(quantityEdit.getText().toString());

                        scanAccess.open();
                        scanAccess.insertRecord(currentScanRecord);
                        //.updateRecordByID(recordID, editRecord);
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
                        dialog.cancel();
                        resetRecord();
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

        if(quantityEdit.getText().toString().equals(currentScanRecord.getQuantity()) && pullSpinner.getSelectedItem().toString().equals(currentScanRecord.getFkPullId()))
        {
            onBackPressed();
        }
        else
        {
            alertDialog.show();
        }
    }
        /*


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
    */
}
