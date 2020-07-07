package tak;

import java.util.*;

public class TakPosition {

	private final TakBoard board;
	private final TakPlayer nextToMove;
	private final Map<TakPlayer, List<TakStone>> inventory;

	public TakPosition(
			final TakBoard board,
			final TakPlayer nextToMove,
			final Map<TakPlayer, List<TakStone>> inventory
	) {
		this.board = board;
		this.nextToMove = nextToMove;
		this.inventory = inventory;
	}

	public TakBoard board() {
		return board;
	}

	public List<TakStone> playerInventory(final TakPlayer player) {
		return Collections.unmodifiableList(inventory.get(player));
	}

	public Map<TakPlayer, List<TakStone>> inventory() {
		return Collections.unmodifiableMap(inventory);
	}

	public TakPlayer nextToMove() {
		return nextToMove;
	}
}
