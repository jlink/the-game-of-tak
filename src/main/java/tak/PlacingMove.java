package tak;

import java.util.*;

import tak.GameOfTak.*;

class PlacingMove extends TakMove {

	private final TakStone stone;
	private final TakBoard.Spot spot;

	public PlacingMove(
			final TakPlayer player,
			final TakStone stone,
			final TakBoard.Spot spot
	) {
		super(player);
		this.stone = stone;
		this.spot = spot;
	}

	@Override
	public Result execute(final TakPosition beforePosition, final Status beforeStatus) {
		if (beforePosition.board().at(spot).isOccupied()) {
			String message = String.format("Stone cannot be placed on occupied spot %s", spot);
			throw new TakException(message);
		}
		TakPosition newPosition = changePosition(beforePosition, player.opponent(), beforeStatus);
		Status nextStatus = nextStatus(beforeStatus);
		return new Result(newPosition, nextStatus);
	}

	private Status nextStatus(final Status beforeStatus) {
		return switch (beforeStatus) {
			case PRELUDE_WHITE -> Status.PRELUDE_BLACK;
			case PRELUDE_BLACK -> Status.ONGOING;
			case ONGOING -> Status.ONGOING;
			case FINISHED -> {
				String message = "Game already finished";
				throw new TakException(message);
			}
		};
	}

	private TakPosition changePosition(final TakPosition beforePosition, final TakPlayer nextToMove, Status currentStatus) {
		if (currentStatus.isPrelude() && stone.isStanding()) {
			String message = String.format("Only flat stones can be set in prelude. Stone %s is not flat.", stone);
			throw new TakException(message);
		}
		Map<TakPlayer, List<TakStone>> newInventory = Map.copyOf(beforePosition.inventory());
		removeStone(newInventory, currentStatus);
		TakBoard newBoard = beforePosition.board().change(Map.of(spot, TakBoard.stack(stone)));
		return new TakPosition(newBoard, nextToMove, newInventory);
	}

	private void removeStone(final Map<TakPlayer, List<TakStone>> newInventory, final Status currentStatus) {
		TakPlayer effectedPlayer = currentStatus.isPrelude() ? this.player.opponent() : this.player;
		if (!newInventory.get(effectedPlayer).remove(stone)) {
			String message = String.format("Player %s does not have stone %s available", effectedPlayer, stone);
			throw new TakException(message);
		}
	}

	@Override
	public String toPTN() {
		String stoneString = stone.isStanding() ? stone.toPTN() : "";
		return String.format("%s%s", stoneString, spot.toPTN());
	}
}
