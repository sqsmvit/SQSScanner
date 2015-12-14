  package com.sqsmv.sqsscanner.database.scan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.sqsmv.sqsscanner.DB.ScanContract.ScanTable;
import com.sqsmv.sqsscanner.ScanRecord;
import com.sqsmv.sqsscanner.database.DBAccess;

  public class ScanAccess extends DBAccess
  {
      private static String SCAN_FROM_PULL_QUERY = "SELECT * FROM " + ScanTable.TABLE_NAME + " WHERE " + ScanTable.COLUMN_NAME_FK_PULL_ID + " = ?";

      public ScanAccess(Context context)
      {
          super(context, new ScanContract());
      }

      public Cursor getAllScans()
      {
          return getDB().query(ScanTable.TABLE_NAME, null, null, null, null, null, null);
      }

      public Cursor getScansForPrint(int exportMode)
      {
          if(exportMode == 1)
          {
              //Normal Mode
              String[] cols = new String[]{ScanTable.COLUMN_NAME_MASNUM, ScanTable.COLUMN_NAME_QUANTITY, ScanTable.COLUMN_NAME_FK_PULL_ID, ScanTable.COLUMN_NAME_DATE, ScanTable.COLUMN_NAME_LOCATION, ScanTable.COLUMN_NAME_TITLE};
              return getDB().query(ScanTable.TABLE_NAME, cols, null, null, null, null, null);
          }
          else if(exportMode == 2)
          {
              //Consolidated Mode
              String[] cols = new String[]{ScanTable.COLUMN_NAME_MASNUM, "SUM(" + ScanTable.COLUMN_NAME_QUANTITY + ") As Total", ScanTable.COLUMN_NAME_FK_PULL_ID, ScanTable.COLUMN_NAME_DATE, ScanTable.COLUMN_NAME_LOCATION, ScanTable.COLUMN_NAME_TITLE};
              return getDB().query(ScanTable.TABLE_NAME, cols, null, null, ScanTable.COLUMN_NAME_MASNUM +  ", " + ScanTable.COLUMN_NAME_FK_PULL_ID, null, ScanTable.COLUMN_NAME_FK_PULL_ID + ", " + ScanTable.COLUMN_NAME_MASNUM);
          }
          else if(exportMode == 3)
          {
              //BillB Mode
              String[] cols = new String[]{ScanTable.COLUMN_NAME_MASNUM, "SUM(" + ScanTable.COLUMN_NAME_QUANTITY + ") As Total", ScanTable.COLUMN_NAME_FK_PULL_ID, ScanTable.COLUMN_NAME_DATE, ScanTable.COLUMN_NAME_TITLE, ScanTable.COLUMN_NAME_PRICE_LIST, ScanTable.COLUMN_NAME_PRICEFILTERS, ScanTable.COLUMN_NAME_RATING};
              return getDB().query(ScanTable.TABLE_NAME, cols, null, null, ScanTable.COLUMN_NAME_MASNUM +  ", " + ScanTable.COLUMN_NAME_FK_PULL_ID, null, ScanTable.COLUMN_NAME_FK_PULL_ID + ", " + ScanTable.COLUMN_NAME_TITLE);
          }
          else if(exportMode == 4)
          {
              //Drew mode
              String[] cols = new String[]{ScanTable.COLUMN_NAME_TITLE, ScanTable.COLUMN_NAME_MASNUM, "SUM(" + ScanTable.COLUMN_NAME_QUANTITY + ") As Total"};
              return getDB().query(ScanTable.TABLE_NAME, cols, null, null, ScanTable.COLUMN_NAME_MASNUM, null, ScanTable.COLUMN_NAME_TITLE);
          }
          else if(exportMode == 5)
          {
              //RI Mode
              String[] cols = new String[]{ScanTable.COLUMN_NAME_MASNUM, ScanTable.COLUMN_NAME_QUANTITY, ScanTable.COLUMN_NAME_TITLE};
              return getDB().query(ScanTable.TABLE_NAME, cols, null, null, null, null, null);
          }
          else if(exportMode == 6)
          {
              //Skid Mode
              String[] cols = new String[]{ScanTable.COLUMN_NAME_FK_PULL_ID, ScanTable.COLUMN_NAME_QUANTITY, ScanTable.COLUMN_NAME_DATE};
              return getDB().query(ScanTable.TABLE_NAME, cols, null, null, null, null, null);
          }
          else
          {
              //Fail for some reason
              return null;
          }
      }

      public void deleteAllScans()
      {
          getDB().delete(ScanTable.TABLE_NAME, null, null);
      }

      //get all scans under a pull id
      public Cursor getScansByPullId(String pullId)
      {
          String[] args = {pullId};
          Cursor dbCur = getDB().rawQuery(SCAN_FROM_PULL_QUERY, args);
          return dbCur;
      }

      //count of a scan of in a specific pull
      public int[] getScanTotalCounts(String pullNum, String scanEntry)
      {
          String query = "SELECT COUNT(*) AS Lines, SUM("+ ScanTable.COLUMN_NAME_QUANTITY + ") AS Total FROM " +
                  ScanTable.TABLE_NAME + " WHERE "+ ScanTable.COLUMN_NAME_MASNUM + "=? AND " + ScanTable.COLUMN_NAME_FK_PULL_ID +"=? ";
          Cursor cur = getDB().rawQuery(query, new String[]{scanEntry, pullNum});
          int[] counts = new int[2];
          if(cur.moveToFirst())
          {
              counts[0] = cur.getInt(cur.getColumnIndex("Lines"));
              counts[1] = cur.getInt(cur.getColumnIndex("Total"));
          }

          return counts;
      }

      public int getTotalByPull(String pullId)
      {
          Cursor cur = getDB().query(ScanTable.TABLE_NAME, new String[]{"SUM ("+ScanTable.COLUMN_NAME_QUANTITY +") As Total"} ,ScanTable.COLUMN_NAME_FK_PULL_ID + "=?", new String[]{pullId}, null, null, null, null );
          int total = 0;
          if(cur.moveToNext())
          {
              total = cur.getInt(cur.getColumnIndex("Total"));
          }
          return total;
      }

      public String[] getPullNums()
      {
          int i = 0;
          Cursor cur = getDB().query(true, ScanTable.TABLE_NAME, new String[]{ScanTable.COLUMN_NAME_FK_PULL_ID}, null, null, null, null, null, null);
          String[] pulls = new String[cur.getCount()];
          if(cur.moveToFirst())
          {
              do
              {
                  pulls[i] = cur.getString(0);
                  i++;
              }while(cur.moveToNext());
          }
          return pulls;
      }

      public int updateRecordByID(int id, ScanRecord editRecord)
      {
          ContentValues values = new ContentValues();
          values.put(ScanTable.COLUMN_NAME_FK_PULL_ID, editRecord.getPullNumber());
          values.put(ScanTable.COLUMN_NAME_QUANTITY, editRecord.getQuantity());

          return getDB().update(ScanTable.TABLE_NAME, values, ScanTable._ID + " = ?", new String[]{String.valueOf(id)});
      }
  }
