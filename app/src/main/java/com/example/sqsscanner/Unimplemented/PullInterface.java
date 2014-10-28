package com.example.sqsscanner.Unimplemented;

import com.example.sqsscanner.ScanRecord;

public interface PullInterface {
	
	boolean addScanRecord(ScanRecord rec);
	boolean removeScanRecord(ScanRecord rec);
	
}
