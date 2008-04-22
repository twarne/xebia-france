package fr.xebia.findbugs;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.BytecodeScanningDetector;

public class XebiaNoStringBufferDetector extends BytecodeScanningDetector {

	private BugReporter bugReporter;
	public XebiaNoStringBufferDetector(BugReporter bugReporter) {
		this.bugReporter = bugReporter;
	}

@Override
public void sawOpcode(int seen) {
	if (seen == NEW) {
		if (getClassConstantOperand().equals("java/lang/StringBuffer")) {
			BugInstance bug = new BugInstance("XEBIA_STRINGBUFFER", NORMAL_PRIORITY);
			bug.addClassAndMethod(this);
			bug.addSourceLine(this);

			bugReporter.reportBug(bug);
		}
	}
}

}
