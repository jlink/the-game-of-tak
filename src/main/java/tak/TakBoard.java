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

	public TakSquare at(final Spot position) {
		checkPositionAllowed(position);
		return squares[squareIndex(position)];
	}

	@Override
	public String toString() {
		return String.format("TakBoard(%s)", size);
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

	private void checkPositionAllowed(final Spot position) {
		if (position.fileIndex() >= size || position.rankIndex() >= size) {
			String message = String.format("Position <%s> is outside %s", position, this);
			throw new TakException(message);
		}
	}

	private int squareIndex(final Spot position) {
		return 0;
	}
}
