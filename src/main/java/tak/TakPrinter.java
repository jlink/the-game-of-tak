package tak;

import java.util.*;
import java.util.stream.*;

public class TakPrinter {

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
			return List.of(" ");
		}
		return stack.stream().map(TakPrinter::print).collect(Collectors.toList());
	}
}
