package fr.xebia.findbugs;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.BytecodeScanningDetector;

public class XebiaDoNotUseDetector extends BytecodeScanningDetector {

	private BugReporter bugReporter;

	public XebiaDoNotUseDetector(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}

	public void sawOpcode(int seen) {
		if (seen == INVOKESTATIC) {
			if (getNameConstantOperand().equals("doNotUse")) {
				bugReporter.reportBug(new BugInstance("XEBIA_DO_NOT_USE", NORMAL_PRIORITY).addClassAndMethod(this).addSourceLine(this));
			}
		}
	}


}
