package tak;

public class GameOfTak {
	private final int size;
	private final TakBoard board;

	public GameOfTak(final int size) {
		this.size = size;
		this.board = TakBoard.ofSize(size);
	}

	public int size() {
		return size;
	}

	public TakBoard board() {
		return board;
	}
}
