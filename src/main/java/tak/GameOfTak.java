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
		ROAD_WIN_WHITE {
			@Override
			public boolean isFinished() {
				return true;
			}

			@Override
			public String toPTN() {
				return "R-0";
			}
		},
		ROAD_WIN_BLACK {
			@Override
			public boolean isFinished() {
				return true;
			}

			@Override
			public String toPTN() {
				return "0-R";
			}
		};

		public boolean isPrelude() {
			return false;
		}

		public boolean isFinished() {
			return false;
		}

		public String toPTN() {
			return "???";
		}
	}

	private final int size;
	private final List<Move> moves = new ArrayList<>();
	private Position position;
	private Status status;

	public GameOfTak(final int size) {
		this.size = size;
		this.position = createStartingPosition(size);
		this.status = Status.PRELUDE_WHITE;
	}

	private Position createStartingPosition(final int size) {
		Board board = Board.ofSize(size);
		Player firstToMove = Player.WHITE;
		Map<Player, List<Stone>> inventory = createInventory(size);
		return new Position(board, firstToMove, inventory);
	}

	private Map<Player, List<Stone>> createInventory(final int size) {
		return Map.of(
				Player.WHITE, inventoryStones(size, Player.WHITE),
				Player.BLACK, inventoryStones(size, Player.BLACK)
		);
	}

	private List<Stone> inventoryStones(final int size, final Player player) {
		List<Stone> stones = new ArrayList<>();
		addFlats(stones, player, size);
		addCaps(stones, player, size);
		return stones;
	}

	private void addFlats(final List<Stone> stones, final Player player, final int size) {
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
			stones.add(Stone.flat(player.stoneColour()));
		}
	}

	private void addCaps(final List<Stone> stones, final Player player, final int size) {
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
			stones.add(Stone.capstone(player.stoneColour()));
		}
	}

	public int size() {
		return size;
	}

	public Position position() {
		return position;
	}

	public List<Move> moves() {
		return moves;
	}

	public Status status() {
		return status;
	}

	public boolean isFinished() {
		return status.isFinished();
	}

	public void makeMove(final Move move) {
		Move.Result result = move.execute(position, status);
		status = result.status();
		position = result.position();
		moves.add(move);
	}

	@Override
	public String toString() {
		return String.format("GameOfTak(%s):%s", size, moves);
	}

}
