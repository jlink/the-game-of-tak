package tak.testingSupport;

import java.util.*;

import tak.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

import static tak.testingSupport.TakAssertions.*;

@TakDomain
class TakDomainProperties {

	@Property
	void generatedStonesAreValid(@ForAll Stone stone) {
		assertThat(isValid(stone)).isTrue();
	}

	private boolean isValid(final Stone stone) {
		if (stone.isCapstone()) {
			return stone.isStanding();
		}
		return true;
	}

	@Property
	void generatedStacksAreValid(@ForAll Deque<Stone> stack) {
		assertThat(isValid(stack)).isTrue();
	}
	private boolean isValid(final Deque<Stone> stack) {
		if (TakDomainContext.count(stack, Stone::isStanding) > 1) {
			return false;
		}
		if (TakDomainContext.count(stack, Stone::isStanding) == 1) {
			return stack.peekFirst().isStanding();
		}
		return true;
	}

	@Property
	void boardAndSpot(@ForAll Tuple2<tak.Board, Spot> boardAndSpot) {
		tak.Board board = boardAndSpot.get1();
		Spot spot = boardAndSpot.get2();
		assertThat(board.at(spot)).isInstanceOf(Square.class);
	}


	@Property(tries =  10)
	void emptyBoards(@ForAll @Board(empty = true) tak.Board board) {
		assertThat(board).isEmpty();
	}

	@Property(tries =  10)
	void boardsWithStones(@ForAll tak.Board board) {
		// List<String> lines = TakPrinter.print(board);
		// lines.forEach(System.out::println);
		// System.out.println();
		// System.out.println("############################");
		// System.out.println();

		assertThat(board).isNotEmpty();
	}
}
