package tak;

import org.assertj.core.api.*;

public class TakAssertions extends Assertions {

	public static TakSquareAssert assertThat(TakSquare actual) {
		return new TakSquareAssert(actual);
	}
}
