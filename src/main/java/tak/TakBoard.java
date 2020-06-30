package tak;

import java.util.*;
import java.util.stream.*;

public class TakBoard {
	public static class Position {
		private final char file;
		private final int rank;

		private Position(final char file, final int rank) {
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
			Position position = (Position) o;
			return file == position.file &&
						   rank == position.rank;
		}

		@Override
		public int hashCode() {
			return Objects.hash(file, rank);
		}

		public int index() {
			return rank;
		}

		private int fileIndex() {
			return file - 'a';
		}

		private int rankIndex() {
			return rank - 1;
		}
	}

	public static Position position(final char file, final int rank) {
		Position position = new Position(file, rank);
		if (file < 'a' || file > 'h') {
			String message = String.format("file coordinate %s is illegal", position.toString());
			throw new IllegalArgumentException(message);
		}
		if (rank < 1 || rank > 8) {
			String message = String.format("file coordinate %s is illegal", position.toString());
			throw new IllegalArgumentException(message);
		}
		return position;
	}

	public static TakBoard ofSize(final int size) {
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

	public TakSquare at(final Position position) {
		checkPositionAllowed(position);
		return squares[squareIndex(position)];
	}

	private void checkPositionAllowed(final Position position) {
		if (position.fileIndex() >= size || position.rankIndex() >= size) {
			String message = String.format("Position <%s> is outside %s", position, this);
			throw new TakException(message);
		}
	}

	private int squareIndex(final Position position) {
		return 0;
	}
}
