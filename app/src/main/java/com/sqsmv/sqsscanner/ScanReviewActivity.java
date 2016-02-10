package com.sqsmv.sqsscanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.sqsmv.sqsscanner.database.DBAdapter;
import com.sqsmv.sqsscanner.database.scan.ScanAccess;
import com.sqsmv.sqsscanner.database.scan.ScanRecord;

import java.util.ArrayList;

public class ScanReviewActivity extends Activity
{
    private String pullKey;

    private DBAdapter dbAdapter;
    private ScanAccess scanAccess;

    private ScanRecordExpandableListAdapter scanAdapter;

    private ArrayList<ScanRecord> scanRecordList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_review);

        pullKey = getIntent().getStringExtra("PULL_NUM");

        dbAdapter = new DBAdapter(this);
        scanAccess = new ScanAccess(dbAdapter);

        ExpandableListView scanReviewList = ((ExpandableListView)findViewById(R.id.scanReviewList));

        scanRecordList = new ArrayList<ScanRecord>();
        scanAdapter = new ScanRecordExpandableListAdapter(this, scanRecordList);

        scanReviewList.setAdapter(scanAdapter);
        scanReviewList.setGroupIndicator(null);

        setListeners();
        ((TextView)findViewById(R.id.ScanReviewPullNum)).setText(pullKey);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        scanAccess.open();
        createAdapterDataset();
        scanAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause()
    {
        dbAdapter.close();
        super.onPause();
    }

    private void setListeners()
    {
        findViewById(R.id.scanHeadBack).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });
    }

    private void createAdapterDataset()
    {
        scanRecordList.clear();
        Cursor scanCursor = scanAccess.selectScansByPullId(pullKey);
        while(scanCursor.moveToNext())
        {
            scanRecordList.add(new ScanRecord(scanCursor));
        }
    }

    private void deleteRow(final ScanRecord scanRecord)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setTitle("Delete Scan")
                .setMessage("Delete Scan?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        scanAccess.deleteByPk(scanRecord.getId());
                        scanRecordList.remove(scanRecord);
                        scanAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void editRow(ScanRecord scanRecord)
    {
        Intent intent = new Intent(this, EditRecordActivity.class);
        intent.putExtra("EDIT_SCAN", scanRecord.getId());
        startActivity(intent);
    }

    private class ScanRecordExpandableListAdapter extends BaseExpandableListAdapter
    {
        private Context context;
        private ArrayList<ScanRecord> scanRecords;

        private ScanRecordExpandableListAdapter(Context context, ArrayList<ScanRecord> scanRecords)
        {
            this.context = context;
            this.scanRecords = scanRecords;
        }

        @Override
        public int getGroupCount()
        {
            return scanRecords.size();
        }

        @Override
        public int getChildrenCount(int groupPosition)
        {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition)
        {
            return scanRecords.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition)
        {
            return scanRecords.get(groupPosition);
        }

        @Override
        public long getGroupId(int groupPosition)
        {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition)
        {
            return childPosition;
        }

        @Override
        public boolean hasStableIds()
        {
            return true;
        }

        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
        {
            ScanRecord scanRecord = (ScanRecord)getGroup(groupPosition);
            if (convertView == null)
            {
                LayoutInflater inf = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                convertView = inf.inflate(R.layout.scan_row, null);
            }
            TextView titleView = (TextView)convertView.findViewById(R.id.scanTitle);
            TextView quantityView = (TextView)convertView.findViewById(R.id.Scan_qty);
            titleView.setText(scanRecord.getTitle());
            quantityView.setText(scanRecord.getQuantity());

            convertView.findViewById(R.id.editBtn).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    editRow(scanRecords.get(groupPosition));
                }
            });
            convertView.findViewById(R.id.delBtn).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    deleteRow(scanRecords.get(groupPosition));
                }
            });
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
        {
            ScanRecord scanRecord = (ScanRecord)getChild(groupPosition, childPosition);
            if (convertView == null)
            {
                LayoutInflater infalInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.scan_row_child, null);
            }
            TextView scanIDView = (TextView)convertView.findViewById(R.id.scanRowID);
            TextView priceListView = (TextView)convertView.findViewById(R.id.pList);
            scanIDView.setText(scanRecord.getMasNum());
            priceListView.setText(scanRecord.getPriceList());

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition)
        {
            return false;
        }
    }
}
