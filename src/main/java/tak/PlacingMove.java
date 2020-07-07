package tak;

import java.util.*;

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
	public Result execute(final TakPosition beforePosition, final GameOfTak.Status beforeStatus) {
		TakPosition newPosition = changePosition(beforePosition, player.opponent(), beforeStatus);
		GameOfTak.Status nextStatus = nextStatus(beforeStatus);
		return new Result(newPosition, nextStatus);
	}

	private GameOfTak.Status nextStatus(final GameOfTak.Status beforeStatus) {
		return switch (beforeStatus) {
			case PRELUDE_WHITE -> GameOfTak.Status.PRELUDE_BLACK;
			case PRELUDE_BLACK -> GameOfTak.Status.WHITE_TO_MOVE;
			default -> GameOfTak.Status.WHITE_TO_MOVE;
		};
	}

	private TakPosition changePosition(final TakPosition beforePosition, final TakPlayer nextToMove, GameOfTak.Status currentStatus) {
		if (currentStatus.isPrelude() && stone.isStanding()) {
			String message = String.format("Only flat stones can be set in prelude. Stone %s is not flat.", stone);
			throw new TakException(message);
		}
		Map<TakPlayer, List<TakStone>> newInventory = Map.copyOf(beforePosition.inventory());
		removeStone(newInventory, currentStatus);
		TakBoard newBoard = beforePosition.board().change(Map.of(spot, TakBoard.stack(stone)));
		return new TakPosition(newBoard, nextToMove, newInventory);
	}

	private void removeStone(final Map<TakPlayer, List<TakStone>> newInventory, final GameOfTak.Status currentStatus) {
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
