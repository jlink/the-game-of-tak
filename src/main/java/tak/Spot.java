package tak;

import java.util.*;
import java.util.stream.*;

public class Spot {

	public static List<Character> files() {
		return IntStream.range(0, GameOfTak.MAX_SIZE).mapToObj(Spot::file).collect(Collectors.toList());
	}

	public static List<Integer> ranks() {
		return IntStream.range(0, GameOfTak.MAX_SIZE).mapToObj(Spot::rank).collect(Collectors.toList());
	}

	private static char file(final int index) {
		return (char) ('a' + index);
	}

	private static int rank(final int index) {
		return index + 1;
	}

	public static Spot of(final char file, final int rank) {
		Spot position = new Spot(file, rank);
		if (!files().contains(file)) {
			String message = String.format("file coordinate %s is illegal", position.toString());
			throw new IllegalArgumentException(message);
		}
		if (!ranks().contains(rank)) {
			String message = String.format("file coordinate %s is illegal", position.toString());
			throw new IllegalArgumentException(message);
		}
		return position;
	}

	private final char file;
	private final int rank;

	Spot(final int fileIndex, final int rankIndex) {
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

	public char file() {
		return file;
	}

	public int rank() {
		return rank;
	}

	public String toPTN() {
		return toString();
	}

	public boolean isAllowed(final Board board) {
		return fileIndex() < board.size() && rankIndex() < board.size();
	}

	public int boardIndex(final Board board) {
		return rankIndex() * board.size() + fileIndex();
	}

	public Set<Spot> neighbours(final int boardSize) {
		HashSet<Spot> spots = new HashSet<>();
		if (fileIndex() > 0) {
			spots.add(new Spot(fileIndex() - 1, rankIndex()));
		}
		if (fileIndex() < boardSize - 1) {
			spots.add(new Spot(fileIndex() + 1, rankIndex()));
		}
		if (rankIndex() > 0) {
			spots.add(new Spot(fileIndex(), rankIndex() - 1));
		}
		if (rankIndex() < boardSize - 1) {
			spots.add(new Spot(fileIndex(), rankIndex() + 1));
		}
		return spots;
	}
}
