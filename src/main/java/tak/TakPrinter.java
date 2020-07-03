package tak;

import java.util.*;
import java.util.stream.*;

public class TakPrinter {

	public static final String EMPTY_SQUARE = "\u25a2";
	public static final String VERTICAL_BAR = " "; //"\uff5c";
	public static final String HORIZONTAL_BAR = " "; //"\u2015";

	public static String codePoint(int codePoint) {
		char[] charPair = Character.toChars(codePoint);
		return new String(charPair);
	}

	public static String print(final TakStone stone) {
		switch (stone.colour()) {
			case WHITE:
				if (stone.isCapstone()) {
					return codePoint(0x1F132);
				} else if (stone.isStanding()) {
					return codePoint(0x1F142);
				} else {
					return codePoint(0x1F135);
				}
			case BLACK:
				if (stone.isCapstone()) {
					return codePoint(0x1F172);
				} else if (stone.isStanding()) {
					return codePoint(0x1F182);
				} else {
					return codePoint(0x1F175);
				}
			default:
				throw new IllegalArgumentException();
		}
	}

	public static List<String> print(final Deque<TakStone> stack) {
		if (stack.isEmpty()) {
			return List.of(EMPTY_SQUARE);
		}
		return stack.stream().map(TakPrinter::print).collect(Collectors.toList());
	}

	public static List<String> print(final TakSquare square) {
		return print(square.stack()).stream().map(s -> VERTICAL_BAR + s).collect(Collectors.toList());
	}

	public static List<String> printRank(final String rankName, final List<TakSquare> rankSquares) {
		List<String> lines = new ArrayList<>();
		int maxStack = Math.max(
				1,
				rankSquares.stream().mapToInt(s -> s.stack().size()).max().orElse(0)
		);
		for (int i = 0; i < maxStack; i++) {
			String mark = i == 0 ? rankName : " ";
			String line = rankLine(mark, rankSquares, i);
			lines.add(0, line);
		}
		return lines;
	}

	private static String rankLine(final String rankName, final List<TakSquare> rankSquares, final int index) {
		String squares = rankSquares.stream()
									.map(square -> {
										List<String> squareLines = print(square);
										Collections.reverse(squareLines);
										return index < squareLines.size() ? squareLines.get(index) : VERTICAL_BAR + EMPTY_SQUARE;
									})
									.collect(Collectors.joining(""));
		return String.format("%s%s%s%s", rankName, squares, VERTICAL_BAR, rankName);
	}

	public static List<String> print(final TakBoard board) {
		ArrayList<String> lines = new ArrayList<>();
		String letterLine = letterLine(board.size());
		String dividerLine = dividerLine(board.size());
		lines.add(letterLine);
		for (int rank = board.size(); rank > 0; rank--) {
			lines.add(dividerLine);
			List<TakSquare> rankSquares = board.rank(rank);
			List<String> rankLines = rankLines(printRank(rank), rankSquares);
			lines.addAll(rankLines);
		}
		lines.add(dividerLine);
		lines.add(letterLine);
		return lines;
	}

	private static List<String> rankLines(final String rankName, final List<TakSquare> rank) {
		return printRank(rankName, rank);
	}

	private static String dividerLine(final int boardSize) {
		int dividerSize = boardSize * 2 + 3;
		return IntStream.range(0, dividerSize).mapToObj(i -> HORIZONTAL_BAR).collect(Collectors.joining());
	}

	private static String letterLine(final int boardSize) {
		String numbers = IntStream.range(0, boardSize).mapToObj(i -> {
			char codePoint = (char) (i + 'a');
			return printFile(codePoint) + VERTICAL_BAR;
		}).collect(Collectors.joining());
		return String.format(EMPTY_SQUARE + VERTICAL_BAR + "%s" + EMPTY_SQUARE, numbers);
	}

	public static String printFile(final char file) {
		return codePoint(0x1f130 + (file - 'a'));
	}

	public static String printRank(final int rank) {
		return codePoint(0x245f + rank);
	}


}
