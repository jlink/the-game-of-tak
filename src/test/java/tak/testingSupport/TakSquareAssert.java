package tak.testingSupport;

import org.assertj.core.api.*;
import tak.*;

public class TakSquareAssert extends AbstractAssert<TakSquareAssert, TakSquare> {
	public TakSquareAssert(final TakSquare actual) {
		super(actual, TakSquareAssert.class);
	}

	public TakSquareAssert isEmpty() {
		if (!actual.isEmpty()) {
			failWithMessage("Expected square to be empty but it wasn't");
		}
		return this;
	}

	public TakSquareAssert isNotEmpty() {
		if (actual.isEmpty()) {
			failWithMessage("Expected square not to be empty but it was");
		}
		return this;
	}

	public TakSquareAssert hasStack(final TakStone ... stones) {
		Assertions.assertThat(actual.stack()).containsExactly(stones);
		return this;
	}
}
