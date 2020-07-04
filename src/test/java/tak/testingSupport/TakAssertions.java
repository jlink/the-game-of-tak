package tak.testingSupport;

import org.assertj.core.api.*;
import tak.*;

public class TakAssertions extends Assertions {

	public static TakSquareAssert assertThat(TakSquare actual) {
		return new TakSquareAssert(actual);
	}

	public static TakBoardAssert assertThat(TakBoard actual) {
		return new TakBoardAssert(actual);
	}
}
