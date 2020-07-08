package tak.testingSupport;

import org.assertj.core.api.AbstractAssert;
import tak.*;
import tak.Board;

public class TakBoardAssert extends AbstractAssert<TakBoardAssert, Board> {
	public TakBoardAssert(Board actual) {
		super(actual, TakBoardAssert.class);
	}

	public TakBoardAssert isEmpty() {
		if (actual.squares().values().stream().anyMatch(takSquare -> !takSquare.isEmpty())) {
			failWithMessage("Expected board to be empty but it wasn't");
		}
		return this;
	}

	public TakBoardAssert isNotEmpty() {
		if (actual.squares().values().stream().allMatch(Square::isEmpty)) {
			failWithMessage("Expected board not to be empty but it was");
		}
		return this;
	}

}
