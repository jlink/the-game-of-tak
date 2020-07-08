package tak;

import java.util.*;

public class Position {

	private final Board board;
	private final Player nextToMove;
	private final Map<Player, List<Stone>> inventory;

	Position(
			final Board board,
			final Player nextToMove,
			final Map<Player, List<Stone>> inventory
	) {
		this.board = board;
		this.nextToMove = nextToMove;
		this.inventory = inventory;
	}

	public Board board() {
		return board;
	}

	public List<Stone> playerInventory(final Player player) {
		return Collections.unmodifiableList(inventory.get(player));
	}

	public Map<Player, List<Stone>> inventory() {
		return Collections.unmodifiableMap(inventory);
	}

	public Player nextToMove() {
		return nextToMove;
	}
}
