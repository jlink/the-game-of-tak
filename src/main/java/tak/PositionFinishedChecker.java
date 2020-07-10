package tak;

import java.util.*;
import java.util.stream.*;

public class PositionFinishedChecker {
	public Optional<GameOfTak.Status> check(final Position position) {
		boolean whiteHasRoad = hasRoad(position, Player.WHITE);
		boolean blackHasRoad = hasRoad(position, Player.BLACK);
		if (whiteHasRoad && !blackHasRoad) {
			return Optional.of(GameOfTak.Status.ROAD_WIN_WHITE);
		}
		if (blackHasRoad && !whiteHasRoad) {
			return Optional.of(GameOfTak.Status.ROAD_WIN_BLACK);
		}
		if (blackHasRoad && whiteHasRoad) {
			if (position.nextToMove() == Player.BLACK) {
				return Optional.of(GameOfTak.Status.ROAD_WIN_WHITE);
			} else {
				return Optional.of(GameOfTak.Status.ROAD_WIN_BLACK);
			}
		}
		return Optional.empty();
	}

	private boolean hasRoad(final Position position, final Player player) {
		Set<Spot> roadCandidateSpots = roadCandidateSpots(position, player);
		return hasRoad(roadCandidateSpots, position.board().size());
	}

	private Set<Spot> roadCandidateSpots(final Position position, final Player player) {
		return position.board().squares().entrySet().stream()
					   .filter(entry -> isRoadCandidate(entry.getValue(), player.stoneColour()))
					   .map(Map.Entry::getKey)
					   .collect(Collectors.toSet());
	}

	private boolean hasRoad(final Set<Spot> candidateSpots, final int boardSize) {
		while (!candidateSpots.isEmpty()) {
			Spot seed = candidateSpots.iterator().next();
			Set<Spot> area = new HashSet<>();
			area.add(seed);
			candidateSpots.remove(seed);
			growConnectedArea(area, candidateSpots, boardSize);
			if (hasLeftAndRight(area, boardSize) || hasTopAndBottom(area, boardSize)) {
				return true;
			}
		}
		return false;
	}

	private void growConnectedArea(final Set<Spot> area, final Set<Spot> candidateSpots, final int boardSize) {
		while (true) {
			Set<Spot> neighbours = allNeighboursIn(area, candidateSpots, boardSize);
			if (neighbours.isEmpty()) {
				break;
			}
			area.addAll(neighbours);
			candidateSpots.removeAll(neighbours);
		}
	}

	private Set<Spot> allNeighboursIn(final Set<Spot> area, final Set<Spot> candidateSpots, final int boardSize) {
		return area.stream()
				   .flatMap(s -> s.neighbours(boardSize).stream())
				   .filter(candidateSpots::contains)
				   .collect(Collectors.toSet());
	}

	private boolean hasLeftAndRight(final Set<Spot> area, final int boardSize) {
		boolean hasLeft = area.stream().anyMatch(spot -> spot.file() == 'a');
		int rightFile = 'a' + boardSize - 1;
		boolean hasRight = area.stream().anyMatch(spot -> spot.file() == rightFile);
		return hasLeft && hasRight;
	}

	private boolean hasTopAndBottom(final Set<Spot> area, final int boardSize) {
		boolean hasTop = area.stream().anyMatch(spot -> spot.rank() == 1);
		boolean hasBottom = area.stream().anyMatch(spot -> spot.rank() == boardSize);
		return hasTop && hasBottom;
	}

	private boolean isRoadCandidate(final Square square, final Stone.Colour stoneColour) {
		return square.top()
					 .map(stone -> stone.canBuildRoad() && stone.colour() == stoneColour)
					 .orElse(false);
	}
}
