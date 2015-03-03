package com.sqsmv.sqsscanner.Unimplemented;

import com.sqsmv.sqsscanner.ScanRecord;

public interface PullInterface {
	
	boolean addScanRecord(ScanRecord rec);
	boolean removeScanRecord(ScanRecord rec);
	
}
