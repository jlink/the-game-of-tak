package tak;

import java.util.*;

public class GameOfTak {
	public static final int MIN_SIZE = 3;
	public static final int MAX_SIZE = 8;

	public enum Status {
		PRELUDE_WHITE {
			@Override
			public boolean isPrelude() {
				return true;
			}
		},
		PRELUDE_BLACK {
			@Override
			public boolean isPrelude() {
				return true;
			}
		},
		ONGOING,
		FINISHED;

		public boolean isPrelude() {
			return false;
		}
	}

	private final int size;
	private final List<TakMove> moves = new ArrayList<>();
	private TakPosition position;
	private Status status;

	public GameOfTak(final int size) {
		this.size = size;
		this.position = createStartingPosition(size);
		this.status = Status.PRELUDE_WHITE;
	}

	private TakPosition createStartingPosition(final int size) {
		TakBoard board = TakBoard.ofSize(size);
		TakPlayer firstToMove = TakPlayer.WHITE;
		Map<TakPlayer, List<TakStone>> inventory = createInventory(size);
		return new TakPosition(board, firstToMove, inventory);
	}

	private Map<TakPlayer, List<TakStone>> createInventory(final int size) {
		return Map.of(
				TakPlayer.WHITE, inventoryStones(size, TakPlayer.WHITE),
				TakPlayer.BLACK, inventoryStones(size, TakPlayer.BLACK)
		);
	}

	private List<TakStone> inventoryStones(final int size, final TakPlayer player) {
		List<TakStone> stones = new ArrayList<>();
		addFlats(stones, player, size);
		addCaps(stones, player, size);
		return stones;
	}

	private void addFlats(final List<TakStone> stones, final TakPlayer player, final int size) {
		int flats = switch (size) {
			case 3 -> 10;
			case 4 -> 15;
			case 5 -> 21;
			case 6 -> 30;
			case 7 -> 40;
			case 8 -> 50;
			default -> 0;
		};
		for (int i = 0; i < flats; i++) {
			stones.add(TakStone.flat(player.stoneColour()));
		}
	}

	private void addCaps(final List<TakStone> stones, final TakPlayer player, final int size) {
		int caps = switch (size) {
			case 3 -> 0;
			case 4 -> 0;
			case 5 -> 1;
			case 6 -> 1;
			case 7 -> 2;
			case 8 -> 2;
			default -> 0;
		};
		for (int i = 0; i < caps; i++) {
			stones.add(TakStone.capstone(player.stoneColour()));
		}
	}

	public int size() {
		return size;
	}

	public TakPosition position() {
		return position;
	}

	public List<TakMove> moves() {
		return moves;
	}

	public Status status() {
		return status;
	}

	public void makeMove(final TakMove move) {
		TakMove.Result result = move.execute(position, status);
		status = result.status();
		position = result.position();
		moves.add(move);
	}

	@Override
	public String toString() {
		return String.format("GameOfTak(%s):%s", size, moves);
	}
}
