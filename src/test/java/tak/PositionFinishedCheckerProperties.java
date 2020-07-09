package tak;

import java.util.*;

import tak.testingSupport.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

@TakDomain
class PositionFinishedCheckerProperties {

	PositionFinishedChecker checker = new PositionFinishedChecker();

	@Property
	void emptyBoardIsNeverFinished(@ForAll @EmptyBoard Board board) {
		Position position = new Position(board, Player.NONE, new HashMap<>());
		assertThat(checker.check(position)).isEmpty();
	}

	@Property
	void straightLeftToRightRoad(
			@ForAll @EmptyBoard Board emptyBoard,
			@ForAll @IntRange(max = 7) int maxRank,
			@ForAll Player player
	) {
		Stone.Colour stoneColour = player.stoneColour();
		int rank = maxRank % emptyBoard.size() + 1;
		Board board = emptyBoard.change(straightLeftToRightRoad(stoneColour, emptyBoard.size(), rank));
		Position position = new Position(board, Player.NONE, new HashMap<>());
		GameOfTak.Status expectedStatus = switch (player) {
			case BLACK -> GameOfTak.Status.ROAD_WIN_BLACK;
			case WHITE -> GameOfTak.Status.ROAD_WIN_WHITE;
			default -> throw new IllegalArgumentException("Not possible");
		};

		assertThat(checker.check(position)).isEqualTo(Optional.of(expectedStatus));
	}

	@Example
	void bendedLeftToRightRoad() {

		Map<Spot, Deque<Stone>> changes = new HashMap<>();
		changes.put(Spot.of('a', 5), Stone.stack(Stone.flat(Stone.Colour.BLACK)));
		changes.put(Spot.of('b', 5), Stone.stack(Stone.capstone(Stone.Colour.BLACK)));
		changes.put(Spot.of('c', 5), Stone.stack(Stone.flat(Stone.Colour.BLACK)));
		changes.put(Spot.of('c', 4), Stone.stack(Stone.flat(Stone.Colour.BLACK)));
		changes.put(Spot.of('c', 3), Stone.stack(Stone.flat(Stone.Colour.BLACK)));
		changes.put(Spot.of('b', 3), Stone.stack(Stone.flat(Stone.Colour.BLACK)));
		changes.put(Spot.of('b', 2), Stone.stack(Stone.flat(Stone.Colour.BLACK)));
		changes.put(Spot.of('b', 1), Stone.stack(Stone.flat(Stone.Colour.BLACK)));
		changes.put(Spot.of('c', 1), Stone.stack(Stone.flat(Stone.Colour.BLACK)));
		changes.put(Spot.of('d', 1), Stone.stack(Stone.flat(Stone.Colour.BLACK)));
		changes.put(Spot.of('e', 1), Stone.stack(Stone.flat(Stone.Colour.BLACK)));
		// To distract algorithm
		changes.put(Spot.of('e', 5), Stone.stack(Stone.flat(Stone.Colour.BLACK)));

		Board board = Board.ofSize(5).change(changes);
		Position position = new Position(board, Player.NONE, new HashMap<>());
		assertThat(checker.check(position)).isEqualTo(Optional.of(GameOfTak.Status.ROAD_WIN_BLACK));
	}

	@Example
	void standingStoneBreaksRoad() {
		Map<Spot, Deque<Stone>> changes = new HashMap<>();
		changes.put(Spot.of('a', 5), Stone.stack(Stone.flat(Stone.Colour.BLACK)));
		changes.put(Spot.of('b', 5), Stone.stack(Stone.capstone(Stone.Colour.BLACK)));
		changes.put(Spot.of('c', 5), Stone.stack(Stone.flat(Stone.Colour.BLACK)));
		changes.put(Spot.of('c', 4), Stone.stack(Stone.flat(Stone.Colour.BLACK)));
		changes.put(Spot.of('c', 3), Stone.stack(Stone.flat(Stone.Colour.BLACK)));
		changes.put(Spot.of('b', 3), Stone.stack(Stone.flat(Stone.Colour.BLACK).standUp()));
		changes.put(Spot.of('b', 2), Stone.stack(Stone.flat(Stone.Colour.BLACK)));
		changes.put(Spot.of('b', 1), Stone.stack(Stone.flat(Stone.Colour.BLACK)));
		changes.put(Spot.of('c', 1), Stone.stack(Stone.flat(Stone.Colour.BLACK)));
		changes.put(Spot.of('d', 1), Stone.stack(Stone.flat(Stone.Colour.BLACK)));
		changes.put(Spot.of('e', 1), Stone.stack(Stone.flat(Stone.Colour.BLACK)));
		Board board = Board.ofSize(5).change(changes);

		Position position = new Position(board, Player.NONE, new HashMap<>());
		assertThat(checker.check(position)).isEmpty();
	}

	@Property
	void almostStraightLeftToRightRoad(
			@ForAll @EmptyBoard Board emptyBoard,
			@ForAll @IntRange(max = 7) int maxRank,
			@ForAll Player player
	) {
		Stone.Colour stoneColour = player.stoneColour();
		int rank = maxRank % emptyBoard.size() + 1;
		int nextRank = (maxRank + 1) % emptyBoard.size() + 1;
		Board board =
				emptyBoard
						.change(straightLeftToRightRoad(stoneColour, emptyBoard.size(), rank))
						.change(Map.of(
								Spot.of('c', rank), Stone.stack(),
								Spot.of('c', nextRank), Stone.stack(Stone.flat(stoneColour))
						));
		Position position = new Position(board, Player.NONE, new HashMap<>());

		assertThat(checker.check(position)).isEmpty();
	}

	private Map<Spot, Deque<Stone>> straightLeftToRightRoad(final Stone.Colour stoneColour, final int boardSize, final int rank) {
		Map<Spot, Deque<Stone>> changes = new HashMap<>();
		for (char c = 'a'; c < 'a' + boardSize; c++) {
			changes.put(Spot.of(c, rank), Stone.stack(Stone.flat(stoneColour)));
		}
		return changes;
	}
}
