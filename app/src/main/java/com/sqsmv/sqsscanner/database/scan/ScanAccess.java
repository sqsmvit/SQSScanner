package com.sqsmv.sqsscanner.database.scan;

import android.database.Cursor;

import com.sqsmv.sqsscanner.database.DBAccess;
import com.sqsmv.sqsscanner.database.DBAdapter;
import com.sqsmv.sqsscanner.database.QueryBuilder;

import java.util.ArrayList;

public class ScanAccess extends DBAccess
{
    public ScanAccess(DBAdapter dbAdapter)
    {
        super(dbAdapter, new ScanContract());
    }

    //get all scans under a pull id
    public Cursor selectScansByPullId(String pullId)
    {
        String[] selectColumns = new String[]{"*"};
        String[] whereColumns = new String[]{ScanContract.COLUMN_NAME_FKPULLID};
        String[] args = {pullId};
        return getDB().rawQuery(QueryBuilder.buildSelectQuery(getTableName(), selectColumns, whereColumns), args);
    }


    public Cursor selectScansForPrint(int exportMode)
    {
        String[] columns = new String[]{"*"};
        String orderBy = null;
        String groupBy = null;

        if(exportMode == 1)
        {
            //Normal Mode
            columns = new String[]{ScanContract.COLUMN_NAME_MASNUM, ScanContract.COLUMN_NAME_QUANTITY, ScanContract.COLUMN_NAME_FKPULLID,
                    ScanContract.COLUMN_NAME_SCANDATE, ScanContract.COLUMN_NAME_LOCATION, ScanContract.COLUMN_NAME_NUMBOXES,
                    ScanContract.COLUMN_NAME_INITIALS, ScanContract.COLUMN_NAME_TITLE};
        }
        else if(exportMode == 2)
        {
            //Consolidated Mode
            columns = new String[]{ScanContract.COLUMN_NAME_MASNUM, "SUM(" + ScanContract.COLUMN_NAME_QUANTITY + ") As Total",
                    ScanContract.COLUMN_NAME_FKPULLID, ScanContract.COLUMN_NAME_SCANDATE, ScanContract.COLUMN_NAME_LOCATION,
                    "SUM(" + ScanContract.COLUMN_NAME_NUMBOXES + ") As Total", ScanContract.COLUMN_NAME_INITIALS, ScanContract.COLUMN_NAME_TITLE};
            groupBy = ScanContract.COLUMN_NAME_MASNUM + ", " + ScanContract.COLUMN_NAME_FKPULLID + ", " + ScanContract.COLUMN_NAME_INITIALS;
            orderBy = ScanContract.COLUMN_NAME_FKPULLID + ", " + ScanContract.COLUMN_NAME_MASNUM;
        }
        else if(exportMode == 3)
        {
            //BillB Mode
            columns = new String[]{ScanContract.COLUMN_NAME_MASNUM, "SUM(" + ScanContract.COLUMN_NAME_QUANTITY + ") As Total",
                    ScanContract.COLUMN_NAME_FKPULLID, ScanContract.COLUMN_NAME_SCANDATE, ScanContract.COLUMN_NAME_TITLE,
                    ScanContract.COLUMN_NAME_PRICELIST, ScanContract.COLUMN_NAME_RATING};
            groupBy = ScanContract.COLUMN_NAME_MASNUM + ", " + ScanContract.COLUMN_NAME_FKPULLID;
            orderBy = ScanContract.COLUMN_NAME_FKPULLID + ", " + ScanContract.COLUMN_NAME_TITLE;
        }
        else if(exportMode == 4)
        {
            //Drew mode
            columns = new String[]{ScanContract.COLUMN_NAME_TITLE, ScanContract.COLUMN_NAME_MASNUM, "SUM(" + ScanContract.COLUMN_NAME_QUANTITY + ") As Total"};
            groupBy = ScanContract.COLUMN_NAME_MASNUM;
            orderBy = ScanContract.COLUMN_NAME_TITLE;
        }
        else if(exportMode == 5)
        {
            //RI Mode
            columns = new String[]{ScanContract.COLUMN_NAME_MASNUM, ScanContract.COLUMN_NAME_QUANTITY, ScanContract.COLUMN_NAME_INITIALS,
                    ScanContract.COLUMN_NAME_TITLE};
        }
        else if(exportMode == 6)
        {
            //Skid Mode
            columns = new String[]{ScanContract.COLUMN_NAME_FKPULLID, ScanContract.COLUMN_NAME_QUANTITY, ScanContract.COLUMN_NAME_SCANDATE,
                    ScanContract.COLUMN_NAME_INITIALS};
        }
        else if(exportMode == 7)
        {
            //Inventory Reset Mode
            columns = new String[]{ScanContract.COLUMN_NAME_MASNUM, ScanContract.COLUMN_NAME_QUANTITY, ScanContract.COLUMN_NAME_FKPULLID,
                                   ScanContract.COLUMN_NAME_SCANDATE, ScanContract.COLUMN_NAME_NUMBOXES, ScanContract.COLUMN_NAME_INITIALS,
                                   ScanContract.COLUMN_NAME_TITLE};
            orderBy = ScanContract._ID;
        }
        return getDB().query(ScanContract.TABLE_NAME, columns, null, null, groupBy, null, orderBy);
    }

    public int getTotalScans()
    {
        String[] selectColumns = new String[]{ScanContract._ID};
        Cursor cursor = getDB().rawQuery(QueryBuilder.buildSelectQuery(getTableName(), selectColumns, null), null);
        int totalScans = cursor.getCount();
        cursor.close();
        return totalScans;
    }

    public int getTotalScansByPull(String pullId)
    {
        String[] selectColumns = new String[]{ScanContract._ID};
        String[] whereColumns = new String[]{ScanContract.COLUMN_NAME_FKPULLID};
        String[] args = {pullId};
        Cursor cursor = getDB().rawQuery(QueryBuilder.buildSelectQuery(getTableName(), selectColumns, whereColumns), args);
        int totalScans = cursor.getCount();
        cursor.close();
        return totalScans;
    }

    public int getTotalByPull(String pullId)
    {
        Cursor cursor = getDB().query(ScanContract.TABLE_NAME, new String[]{"SUM(" + ScanContract.COLUMN_NAME_QUANTITY + ") As Total"},
        ScanContract.COLUMN_NAME_FKPULLID + " = ?", new String[]{pullId}, null, null, null, null);
        int total = 0;
        if(cursor.moveToFirst())
        {
            total = cursor.getInt(cursor.getColumnIndex("Total"));
        }
        cursor.close();
        return total;
    }

    public int getProductCountForPull(String pullNum, String scanEntry)
    {
        String query = "SELECT SUM(" + ScanContract.COLUMN_NAME_QUANTITY + ") AS Total FROM " + ScanContract.TABLE_NAME + " WHERE " +
                ScanContract.COLUMN_NAME_MASNUM + " = ? AND " + ScanContract.COLUMN_NAME_FKPULLID +" = ?";
        Cursor cursor = getDB().rawQuery(query, new String[]{scanEntry, pullNum});
        int productCount = 0;
        if(cursor.moveToFirst())
        {
            productCount = cursor.getInt(cursor.getColumnIndex("Total"));
        }
        cursor.close();
        return productCount;
    }

    //count of a scan of in a specific pull
    public int[] getScanTotalCounts(String pullNum, String scanEntry)
    {
        String query = "SELECT COUNT(*) AS Lines, SUM(" + ScanContract.COLUMN_NAME_QUANTITY + ") AS Total FROM " +
        ScanContract.TABLE_NAME + " WHERE "+ ScanContract.COLUMN_NAME_MASNUM + " = ? AND " + ScanContract.COLUMN_NAME_FKPULLID +" = ?";
        Cursor cursor = getDB().rawQuery(query, new String[]{scanEntry, pullNum});
        int[] counts = new int[2];
        if(cursor.moveToFirst())
        {
            counts[0] = cursor.getInt(cursor.getColumnIndex("Lines"));
            counts[1] = cursor.getInt(cursor.getColumnIndex("Total"));
        }
        return counts;
    }

    public ArrayList<String> getPullNums()
    {
        Cursor cursor = getDB().query(true, ScanContract.TABLE_NAME, new String[]{ScanContract.COLUMN_NAME_FKPULLID}, null, null, null, null, null, null);
        ArrayList<String> pulls = new ArrayList<String>();
        while(cursor.moveToNext())
        {
            pulls.add(cursor.getString(0));
        }
        return pulls;
    }
}
