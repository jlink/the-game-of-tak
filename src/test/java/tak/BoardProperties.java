package tak;

import java.util.*;

import tak.testingSupport.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.constraints.*;

import static tak.TakBoard.*;
import static tak.TakStone.Colour.*;
import static tak.testingSupport.TakAssertions.*;

@TakDomain
class BoardProperties {

	@Property
	void legalBoardCoordinatesCanBeCreated(
			@ForAll @CharRange(from = 'a', to = 'h') char file,
			@ForAll @IntRange(min = 1, max = 8) int rank
	) {
		Spot position = spot(file, rank);
		assertThat(position.toString()).isEqualTo(String.valueOf(file) + String.valueOf(rank));
	}

	@Property
	void illegalBoardCoordinatesAreRejected(
			@ForAll char file,
			@ForAll int rank
	) {
		Assume.that(file < 'a' || file > 'h' || rank < 1 || rank > 8);
		assertThatThrownBy(() -> spot(file, rank)).isInstanceOf(IllegalArgumentException.class);
	}

	@Property
	void newBoard(@ForAll @Board(empty = true) TakBoard board) {
		assertThat(board.size()).isBetween(3, 8);

		assertThat(board.squares()).hasSize(board.size() * board.size());
		assertThat(board.squares().values()).allMatch(TakSquare::isEmpty);

		char highestFile = Spot.files().get(board.size() - 1);
		int highestRank = Spot.ranks().get(board.size() - 1);

		Spot upperRight = spot(highestFile, highestRank);
		assertThat(board.at(upperRight).isEmpty()).isTrue();

		if (board.size() < 8) {
			assertThatThrownBy(() -> board.at(spot((char) (highestFile + 1), highestRank))).isInstanceOf(TakException.class);
			assertThatThrownBy(() -> board.at(spot(highestFile, highestRank + 1))).isInstanceOf(TakException.class);
		}
	}

	@Property
	void canSetStackOnAnySquare(@ForAll Tuple2<@Board(empty = true) TakBoard, Spot> boardAndSpot) {
		TakBoard emptyBoard = boardAndSpot.get1();
		Spot spot = boardAndSpot.get2();

		TakStone stone = TakStone.capstone(WHITE);
		Deque<TakStone> stack = new ArrayDeque<>(List.of(stone));
		Map<Spot, Deque<TakStone>> changes = Map.of(spot, stack);
		TakBoard changedBoard = emptyBoard.change(changes);

		assertThat(changedBoard.at(spot)).isNotEmpty();
		assertThat(changedBoard.at(spot).stack()).hasSize(1);
		assertThat(changedBoard.at(spot).top()).isEqualTo(Optional.of(stone));

		assertThat(emptyBoard).isNotSameAs(changedBoard);
		assertThat(emptyBoard.at(spot)).isEmpty();
	}

	@Example
	void toStringRepresentation(@ForAll @Board(empty = true) TakBoard board) {
		TakBoard boardWithA1andC3occupied = board.change(Map.of(
				spot('a', 1), stack(TakStone.capstone(WHITE)),
				spot('c', 3), stack(TakStone.capstone(BLACK), TakStone.flat(WHITE))
		));

		assertThat(boardWithA1andC3occupied.toString())
				.containsSubsequence("a1", "WHITE:C", "c3", "BLACK:C", "WHITE:F");
	}
}
