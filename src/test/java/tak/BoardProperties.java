package tak;

import java.util.*;

import tak.testingSupport.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.constraints.*;

import static tak.Board.*;
import static tak.Stone.Colour.*;
import static tak.testingSupport.TakAssertions.*;

@TakDomain
class BoardProperties {

	@Property
	void legalBoardCoordinatesCanBeCreated(
			@ForAll @CharRange(from = 'a', to = 'h') char file,
			@ForAll @IntRange(min = 1, max = 8) int rank
	) {
		Spot position = Spot.of(file, rank);
		assertThat(position.toString()).isEqualTo(String.valueOf(file) + String.valueOf(rank));
	}

	@Property
	void illegalBoardCoordinatesAreRejected(
			@ForAll char file,
			@ForAll int rank
	) {
		Assume.that(file < 'a' || file > 'h' || rank < 1 || rank > 8);
		assertThatThrownBy(() -> Spot.of(file, rank)).isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void newBoard(@ForAll @tak.testingSupport.Board(empty = true) Board board) {
		assertThat(board.size()).isBetween(3, 8);

		assertThat(board.squares()).hasSize(board.size() * board.size());
		assertThat(board.squares().values()).allMatch(Square::isEmpty);

		char highestFile = Spot.files().get(board.size() - 1);
		int highestRank = Spot.ranks().get(board.size() - 1);

		Spot upperRight = Spot.of(highestFile, highestRank);
		assertThat(board.at(upperRight).isEmpty()).isTrue();

		if (board.size() < 8) {
			assertThatThrownBy(() -> board.at(Spot.of((char) (highestFile + 1), highestRank))).isInstanceOf(TakException.class);
			assertThatThrownBy(() -> board.at(Spot.of(highestFile, highestRank + 1))).isInstanceOf(TakException.class);
		}
	}

	@Property
	void canSetStackOnAnySquare(@ForAll Tuple2<@tak.testingSupport.Board(empty = true) Board, Spot> boardAndSpot) {
		Board emptyBoard = boardAndSpot.get1();
		Spot spot = boardAndSpot.get2();

		Stone stone = Stone.capstone(WHITE);
		Deque<Stone> stack = new ArrayDeque<>(List.of(stone));
		Map<Spot, Deque<Stone>> changes = Map.of(spot, stack);
		Board changedBoard = emptyBoard.change(changes);

		assertThat(changedBoard.at(spot)).isNotEmpty();
		assertThat(changedBoard.at(spot).stack()).hasSize(1);
		assertThat(changedBoard.at(spot).top()).isEqualTo(Optional.of(stone));

		assertThat(emptyBoard).isNotSameAs(changedBoard);
		assertThat(emptyBoard.at(spot)).isEmpty();
	}

	@Example
	void toStringRepresentation(@ForAll @tak.testingSupport.Board(empty = true) Board board) {
		Board boardWithA1andC3occupied = board.change(Map.of(
				Spot.of('a', 1), Stone.stack(Stone.capstone(WHITE)),
				Spot.of('c', 3), Stone.stack(Stone.capstone(BLACK), Stone.flat(WHITE))
		));

		assertThat(boardWithA1andC3occupied.toString())
				.containsSubsequence("a1", "WHITE:C", "c3", "BLACK:C", "WHITE:F");
	}
}
