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

	private Map<Spot, Deque<Stone>> straightLeftToRightRoad(final Stone.Colour stoneColour, final int boardSize, final int rank) {
		Map<Spot, Deque<Stone>> changes = new HashMap<>();
		for (char c = 'a'; c < 'a' + boardSize; c++) {
			changes.put(Spot.of(c, rank), Stone.stack(Stone.flat(stoneColour)));
		}
		return changes;
	}
}
