package tak;

import java.util.*;
import java.util.stream.*;

public class TakPrinter {

	public static final String EMPTY_SQUARE = "\u25a2";

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
		return print(square.stack()).stream().map(s -> "|" + s).collect(Collectors.toList());
	}

	public static List<String> printRank(final char rankName, final List<TakSquare> rankSquares) {
		List<String> lines = new ArrayList<>();
		int maxStack = Math.max(
				1,
				rankSquares.stream().mapToInt(s -> s.stack().size()).max().orElse(0)
		);
		for (int i = 0; i < maxStack; i++) {
			String mark = i == 0 ? Character.toString(rankName) : " ";
			String line = rankLine(mark, rankSquares, i);
			lines.add(line);
		}
		return lines;
	}

	private static String rankLine(final String rankName, final List<TakSquare> rankSquares, final int index) {
		String squares = rankSquares.stream()
									.map(square -> {
										List<String> squareLines = print(square);
										return index < squareLines.size() ? squareLines.get(index) : "|" + EMPTY_SQUARE;
									})
									.collect(Collectors.joining(""));
		return String.format("%s%s|%s", rankName, squares, rankName);
	}
}
