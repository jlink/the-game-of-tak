package tak.testingSupport;

import org.assertj.core.api.AbstractAssert;
import tak.*;

public class TakBoardAssert extends AbstractAssert<TakBoardAssert, TakBoard> {
	public TakBoardAssert(TakBoard actual) {
		super(actual, TakBoardAssert.class);
	}

	public TakBoardAssert isEmpty() {
		if (actual.squares().values().stream().anyMatch(takSquare -> !takSquare.isEmpty())) {
			failWithMessage("Expected board to be empty but it wasn't");
		}
		return this;
	}

	public TakBoardAssert isNotEmpty() {
		if (actual.squares().values().stream().allMatch(TakSquare::isEmpty)) {
			failWithMessage("Expected board not to be empty but it was");
		}
		return this;
	}

}
