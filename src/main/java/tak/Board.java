package tak;

import java.util.*;
import java.util.stream.*;

public class Board {

	public static Board ofSize(final int size) {
		if (size < GameOfTak.MIN_SIZE || size > GameOfTak.MAX_SIZE) {
			throw new IllegalArgumentException(String.format("Size <%s> of Tak board should be between 3 and 8", size));
		}
		Square[] squares = initSquares(size);
		return new Board(size, squares);
	}

	private static Square[] initSquares(final int size) {
		return IntStream.range(0, size * size)
						.mapToObj(ignore -> new Square())
						.toArray(Square[]::new);
	}

	private final int size;
	private final Square[] squares;

	private Board(final int size, final Square[] squares) {
		this.size = size;
		this.squares = squares;
	}

	public int size() {
		return size;
	}

	public Square at(final Spot spot) {
		checkPositionAllowed(spot);
		return squares[spot2Index(spot)];
	}

	public Map<Spot, Square> squares() {
		Map<Spot, Square> squaresMap = new LinkedHashMap<>();
		for (int i = 0; i < squares.length; i++) {
			squaresMap.put(index2Spot(i), squares[i]);
		}
		return squaresMap;
	}

	public List<Square> rank(final int rank) {
		List<Square> rankSquares = new ArrayList<>();
		int rankIndex = rank - 1;
		for (int fileIndex = 0; fileIndex < size; fileIndex++) {
			rankSquares.add(at(new Spot(fileIndex, rankIndex)));
		}
		return rankSquares;
	}

	public Board change(final Map<Spot, Deque<Stone>> changes) {
		Board clone = cloneBoard();
		for (Map.Entry<Spot, Deque<Stone>> entry : changes.entrySet()) {
			int index = clone.spot2Index(entry.getKey());
			clone.squares[index] = clone.squares[index].set(entry.getValue());
		}
		return clone;
	}

	private Board cloneBoard() {
		Square[] clonedSquares = new Square[squares.length];
		for (int i = 0; i < squares.length; i++) {
			Square clonedSquare = new Square(cloneStack(squares[i].stack()));
			clonedSquares[i] = clonedSquare;
		}
		return new Board(size, clonedSquares);
	}

	private Deque<Stone> cloneStack(final Deque<Stone> stack) {
		Deque<Stone> clonedStack = new ArrayDeque<>();
		for (Stone stone : stack) {
			clonedStack.add(Stone.copy(stone));
		}
		return new ArrayDeque<>(clonedStack);
	}

	private void checkPositionAllowed(final Spot spot) {
		if (!spot.isAllowed(this)) {
			String message = String.format("Position <%s> is outside %s", spot, this);
			throw new TakException(message);
		}
	}

	private int spot2Index(final Spot spot) {
		return spot.boardIndex(this);
	}

	private Spot index2Spot(final int index) {
		int fileIndex = index % size;
		int rankIndex = Math.floorDiv(index, size);
		return new Spot(fileIndex, rankIndex);
	}

	@Override
	public String toString() {
		Map<Spot, Square> occupiedSquares =
				squares().entrySet().stream()
						 .filter(e -> !e.getValue().isEmpty())
						 .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		String occupiedSquaresString =
				occupiedSquares.isEmpty() ? "" :
						"{" +
								occupiedSquares.entrySet().stream()
											   .map(e -> e.getKey() + "=" + e.getValue())
											   .collect(Collectors.joining(", "))
								+ "}";
		return String.format("TakBoard(%s)%s", size, occupiedSquaresString);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Board takBoard = (Board) o;
		return Arrays.equals(squares, takBoard.squares);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(squares);
	}
}
