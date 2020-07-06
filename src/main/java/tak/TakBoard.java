package tak;

import java.util.*;
import java.util.stream.*;

public class TakBoard {

	public static final int MIN_SIZE = 3;
	public static final int MAX_SIZE = 8;

	public static class Spot {

		public static List<Character> files() {
			return IntStream.range(0, MAX_SIZE).mapToObj(Spot::file).collect(Collectors.toList());
		}

		public static List<Integer> ranks() {
			return IntStream.range(0, MAX_SIZE).mapToObj(Spot::rank).collect(Collectors.toList());
		}

		private static char file(final int index) {
			return (char) ('a' + index);
		}

		private static int rank(final int index) {
			return index + 1;
		}

		private final char file;
		private final int rank;

		private Spot(final int fileIndex, final int rankIndex) {
			this.file = (char) (fileIndex + 'a');
			this.rank = rankIndex + 1;
		}

		private Spot(final char file, final int rank) {
			this.file = file;
			this.rank = rank;
		}

		@Override
		public String toString() {
			return String.format("%s%s", file, rank);
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Spot position = (Spot) o;
			return file == position.file &&
						   rank == position.rank;
		}

		@Override
		public int hashCode() {
			return Objects.hash(file, rank);
		}

		private int fileIndex() {
			return file - 'a';
		}

		private int rankIndex() {
			return rank - 1;
		}

		public String toPTN() {
			return toString();
		}
	}

	public static Spot spot(final char file, final int rank) {
		Spot position = new Spot(file, rank);
		if (!Spot.files().contains(file)) {
			String message = String.format("file coordinate %s is illegal", position.toString());
			throw new IllegalArgumentException(message);
		}
		if (!Spot.ranks().contains(rank)) {
			String message = String.format("file coordinate %s is illegal", position.toString());
			throw new IllegalArgumentException(message);
		}
		return position;
	}

	public static Deque<TakStone> stack(TakStone ... stones) {
		return new ArrayDeque<>(Arrays.asList(stones));
	}

	public static TakBoard ofSize(final int size) {
		if (size < MIN_SIZE || size > MAX_SIZE) {
			throw new IllegalArgumentException(String.format("Size <%s> of Tak board should be between 3 and 8", size));
		}
		TakSquare[] squares = initSquares(size);
		return new TakBoard(size, squares);
	}

	private static TakSquare[] initSquares(final int size) {
		return IntStream.range(0, size * size)
						.mapToObj(ignore -> new TakSquare())
						.toArray(TakSquare[]::new);
	}

	private final int size;
	private final TakSquare[] squares;

	private TakBoard(final int size, final TakSquare[] squares) {
		this.size = size;
		this.squares = squares;
	}

	public int size() {
		return size;
	}

	public TakSquare at(final Spot spot) {
		checkPositionAllowed(spot);
		return squares[spot2Index(spot)];
	}

	public Map<Spot, TakSquare> squares() {
		Map<Spot, TakSquare> squaresMap = new LinkedHashMap<>();
		for (int i = 0; i < squares.length; i++) {
			squaresMap.put(index2Spot(i), squares[i]);
		}
		return squaresMap;
	}

	public List<TakSquare> rank(final int rank) {
		List<TakSquare> rankSquares = new ArrayList<>();
		int rankIndex = rank - 1;
		for (int fileIndex = 0; fileIndex < size; fileIndex++) {
			rankSquares.add(at(new Spot(fileIndex, rankIndex)));
		}
		return rankSquares;
	}

	public TakBoard change(final Map<Spot, Deque<TakStone>> changes) {
		TakBoard clone = cloneBoard();
		for (Map.Entry<Spot, Deque<TakStone>> entry : changes.entrySet()) {
			int index = clone.spot2Index(entry.getKey());
			clone.squares[index] = clone.squares[index].set(entry.getValue());
		}
		return clone;
	}

	private TakBoard cloneBoard() {
		TakSquare[] clonedSquares = new TakSquare[squares.length];
		for (int i = 0; i < squares.length; i++) {
			TakSquare clonedSquare = new TakSquare(cloneStack(squares[i].stack()));
			clonedSquares[i] = clonedSquare;
		}
		return new TakBoard(size, clonedSquares);
	}

	private Deque<TakStone> cloneStack(final Deque<TakStone> stack) {
		Deque<TakStone> clonedStack = new ArrayDeque<>();
		for (TakStone stone : stack) {
			clonedStack.add(TakStone.copy(stone));
		}
		return new ArrayDeque<>(clonedStack);
	}

	private void checkPositionAllowed(final Spot spot) {
		if (spot.fileIndex() >= size || spot.rankIndex() >= size) {
			String message = String.format("Position <%s> is outside %s", spot, this);
			throw new TakException(message);
		}
	}

	private int spot2Index(final Spot spot) {
		return spot.rankIndex() * size + spot.fileIndex();
	}

	private Spot index2Spot(final int index) {
		int fileIndex = index % size;
		int rankIndex = Math.floorDiv(index, size);
		return new Spot(fileIndex, rankIndex);
	}

	@Override
	public String toString() {
		Map<Spot, TakSquare> occupiedSquares =
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
		TakBoard takBoard = (TakBoard) o;
		return Arrays.equals(squares, takBoard.squares);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(squares);
	}
}
