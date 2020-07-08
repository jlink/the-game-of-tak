package tak.testingSupport;

import org.assertj.core.api.*;
import tak.*;
import tak.Board;

public class TakAssertions extends Assertions {

	public static TakSquareAssert assertThat(Square actual) {
		return new TakSquareAssert(actual);
	}

	public static TakBoardAssert assertThat(Board actual) {
		return new TakBoardAssert(actual);
	}
}
