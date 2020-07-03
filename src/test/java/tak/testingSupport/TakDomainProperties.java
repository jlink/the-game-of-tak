package tak.testingSupport;

import java.util.*;

import tak.*;
import tak.TakBoard.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

import static org.assertj.core.api.Assertions.*;

@TakDomain
class TakDomainProperties {

	@Property
	void generatedStonesAreValid(@ForAll TakStone stone) {
		assertThat(isValid(stone)).isTrue();
	}

	private boolean isValid(final TakStone stone) {
		if (stone.isCapstone()) {
			return stone.isStanding();
		}
		return true;
	}

	@Property
	void generatedStacksAreValid(@ForAll Deque<TakStone> stack) {
		assertThat(isValid(stack)).isTrue();
	}
	private boolean isValid(final Deque<TakStone> stack) {
		if (TakDomainContext.count(stack, TakStone::isStanding) > 1) {
			return false;
		}
		if (TakDomainContext.count(stack, TakStone::isStanding) == 1) {
			return stack.peekFirst().isStanding();
		}
		return true;
	}

	@Property
	void boardAndSpot(@ForAll Tuple2<TakBoard, Spot> boardAndSpot) {
		TakBoard board = boardAndSpot.get1();
		Spot spot = boardAndSpot.get2();
		assertThat(board.at(spot)).isInstanceOf(TakSquare.class);
	}


}