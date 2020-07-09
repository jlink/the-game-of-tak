package tak;

import java.util.*;

import tak.GameOfTak.*;

class PlacingMove extends Move {

	private final Stone stone;
	private final Spot spot;

	public PlacingMove(
			final Player player,
			final Stone stone,
			final Spot spot
	) {
		super(player);
		this.stone = stone;
		this.spot = spot;
	}

	@Override
	public Result execute(final Position beforePosition, final Status beforeStatus) {
		if (beforePosition.board().at(spot).isOccupied()) {
			String message = String.format("Stone cannot be placed on occupied spot %s", spot);
			throw new TakException(message);
		}
		Position newPosition = changePosition(beforePosition, player.opponent(), beforeStatus);
		Status nextStatus = nextStatus(newPosition, beforeStatus);
		return new Result(newPosition, nextStatus);
	}

	private Status nextStatus(final Position newPosition, final Status beforeStatus) {
		return checkGameFinished(newPosition).orElse(switch (beforeStatus) {
			case PRELUDE_WHITE -> Status.PRELUDE_BLACK;
			case PRELUDE_BLACK -> Status.ONGOING;
			case ONGOING -> Status.ONGOING;
			default -> {
				String message = "Game already finished";
				throw new TakException(message);
			}
		});
	}

	private Position changePosition(final Position beforePosition, final Player nextToMove, Status currentStatus) {
		if (currentStatus.isPrelude() && stone.isStanding()) {
			String message = String.format("Only flat stones can be set in prelude. Stone %s is not flat.", stone);
			throw new TakException(message);
		}
		Map<Player, List<Stone>> newInventory = Map.copyOf(beforePosition.inventory());
		removeStone(newInventory, currentStatus);
		Board newBoard = beforePosition.board().change(Map.of(spot, Stone.stack(stone)));
		return new Position(newBoard, nextToMove, newInventory);
	}

	private void removeStone(final Map<Player, List<Stone>> newInventory, final Status currentStatus) {
		Player effectedPlayer = currentStatus.isPrelude() ? this.player.opponent() : this.player;
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
