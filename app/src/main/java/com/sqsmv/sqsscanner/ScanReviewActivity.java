/**
 *
 */
package com.sqsmv.sqsscanner;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;

import com.sqsmv.sqsscanner.DB.ProductDataSource;
import com.sqsmv.sqsscanner.DB.ScanContract.ScanTable;
import com.sqsmv.sqsscanner.DB.ScanDataSource;
import com.sqsmv.sqsscanner.DB.UPCDataSource;

//import android.widget.TextView;

/**
 * @author ChrisS
 *
 */
public class ScanReviewActivity extends ExpandableListActivity
{
    private class ScansCursorTreeAdapter extends SimpleCursorTreeAdapter
    {
        public ScansCursorTreeAdapter(Context context, Cursor cursor,
                                      int groupLayout, String[] groupFrom,
                                      int[] groupTo, int childLayout, String[] childFrom,
                                      int[] childTo)
                {

                    super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom, childTo);
                }

        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor)
        {
            int id = groupCursor.getInt(groupCursor.getColumnIndex(ScanTable._ID));
            //Cursor childCursor =  scanDataSource.getValueByID(id, new String[]{ScanTable.COLUMN_NAME_SCAN_ENTRY, ScanTable.COLUMN_NAME_PRICE_LIST});
            Cursor childCursor =  scanDataSource.getValueByID(id);
            childCursor.moveToFirst();
            return childCursor;
        }
    }

    public static final String PULL_KEY = "com.sqsmv.sqsscanner.pullKey";
    public static final String NEW_PULL_LINES = "com.sqsmv.sqsscanner.newPullLines";
    public static final String NEW_PULL_PIECES = "com.sqsmv.sqsscanner.newPullPieces";
    public static final String PULL_POS = "com.sqsmv.sqsscanner.pullPos";

    private String pullKey;

    private ScansCursorTreeAdapter scanAdapter;

    private ProductDataSource productDataSource;
    private UPCDataSource upcDataSource;
    private ScanDataSource scanDataSource;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        pullKey = intent.getStringExtra("PULL_NUM");

        productDataSource = new ProductDataSource(this);
        upcDataSource = new UPCDataSource(this);
        scanDataSource = new ScanDataSource(this);
        productDataSource.read();
        upcDataSource.read();
        scanDataSource.open();

        scanAdapter = new ScansCursorTreeAdapter(this, scanDataSource.getScansByPullId(pullKey),
                        R.layout.scan_row, new String[]{ScanTable.COLUMN_NAME_TITLE, ScanTable.COLUMN_NAME_QUANTITY},
                        new int[] {R.id.scanTitle, R.id.Scan_qty}, R.layout.scan_title,
                        new String[]{ScanTable.COLUMN_NAME_SCAN_ENTRY, ScanTable.COLUMN_NAME_PRICE_LIST, ScanTable._ID},
                        new int[] {R.id.scanID, R.id.pList});

        ListView listView = getExpandableListView();

        View header = getLayoutInflater().inflate(R.layout.scan_header, null);

        listView.addHeaderView(header);


        getExpandableListView().setGroupIndicator(null);

        this.setListAdapter(scanAdapter);

        TextView scansPull = (TextView) findViewById(R.id.headScanPull);
        scansPull.setText(scansPull.getText().toString() + " " +pullKey);

    }


    /* (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
          if (requestCode == 1)
          {
             if(resultCode == RESULT_OK)
             {
                 scanAdapter.changeCursor(scanDataSource.getScansByPullId(pullKey));
                 scanAdapter.notifyDataSetChanged();
             }

             if (resultCode == RESULT_CANCELED)
             {
             }
          }
    }

    public void onClickBack(View v)
    {
        onBackPressed();
    }

    /**
     * @param v
     */
    public void delRow(View v)
    {
        final int position = getRowPosition(v);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle("Delete Scan");

        // set dialog message
        alertDialogBuilder
            .setMessage("Delete Scan?")
            .setCancelable(false)
            .setPositiveButton("Yes",new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog,int id)
                {
                    Cursor tempCursor = scanAdapter.getCursor();
                    tempCursor.moveToPosition(position);
                    scanDataSource.delScansById(Integer.toString(tempCursor.getInt(tempCursor.getColumnIndex(ScanTable._ID))));
                    scanAdapter.changeCursor(scanDataSource.getScansByPullId(pullKey));
                    scanAdapter.notifyDataSetChanged();

                }
              })
            .setNegativeButton("No",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id)
                {
                    // if this button is clicked, just close
                    // the dialog box and do nothing
                    dialog.cancel();
                }
            });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void clickGoToEdit(View v)
    {
        int pos = getRowPosition(v);

        Cursor tempCursor = scanAdapter.getCursor();
        tempCursor.moveToPosition(pos);
        ScanRecord editScan = new ScanRecord(tempCursor);

        int scanPkey = tempCursor.getInt(tempCursor.getColumnIndex(ScanTable._ID));

        Intent intent = new Intent(this, EditRecordActivity.class);
        Bundle b = new Bundle();
        b.putInt("EDIT_SCAN", scanPkey);
        intent.putExtras(b);
        startActivityForResult(intent, 1);
    }


    public int getRowPosition(View v)
    {
        int rawPos = getExpandableListView().getPositionForView((LinearLayout) v.getParent());
        long expPos = getExpandableListView().getExpandableListPosition(rawPos);
        return ExpandableListView.getPackedPositionGroup(expPos);
    }
}
