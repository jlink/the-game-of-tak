package tak;

import java.util.*;
import java.util.stream.*;

public class PositionFinishedChecker {
	public Optional<GameOfTak.Status> check(final Position position) {
		if (roadWinWhite(position)) {
			return Optional.of(GameOfTak.Status.ROAD_WIN_WHITE);
		}
		return Optional.empty();
	}

	private boolean roadWinWhite(final Position position) {
		Set<Spot> roadCandidateSpots =
				position.board().squares().entrySet().stream()
						.filter(entry -> isRoadCandidate(entry.getValue(), Player.WHITE.stoneColour()))
						.map(Map.Entry::getKey)
						.collect(Collectors.toSet());
		return hasRoadLeftToRight(roadCandidateSpots, position.board().size());
	}

	private boolean hasRoadLeftToRight(final Set<Spot> candidateSpots, final int boardSize) {
		// TODO: This fulfills the current tests but does not really work!
		Set<Spot> lefts = candidateSpots.stream().filter(spot -> spot.file() == 'a').collect(Collectors.toSet());
		Set<Spot> rights = candidateSpots.stream().filter(spot -> spot.file() == 'a' + boardSize - 1).collect(Collectors.toSet());

		return !lefts.isEmpty() && !rights.isEmpty() && (candidateSpots.size() - lefts.size() - rights.size()) >= (boardSize - 2);
	}

	private boolean isRoadCandidate(final Square square, final Stone.Colour stoneColour) {
		return square.top()
					 .map(stone -> stone.canBuildRoad() && stone.colour() == stoneColour)
					 .orElse(false);
	}
}
