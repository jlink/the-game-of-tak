package tak;

import java.util.*;

import tak.testingSupport.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;
import static tak.TakPrinter.*;
import static tak.Stone.Colour.*;

@TakDomain
class TakPrintingProperties {

	@Property
	@FromData("stonesWithPrintString")
	void stones(@ForAll Stone stone, @ForAll String representation) {
		String printedStone = TakPrinter.print(stone);
		assertThat(printedStone).isEqualTo(representation);
	}

	@Data
	List<Tuple2<Stone, String>> stonesWithPrintString() {
		return List.of(
				Tuple.of(Stone.capstone(WHITE), TakPrinter.codePoint(0x24b8)), //Ⓒ
				Tuple.of(Stone.capstone(BLACK), TakPrinter.codePoint(0x1F152)), //🅒
				Tuple.of(Stone.flat(WHITE), TakPrinter.codePoint(0x1F135)), //🄵
				Tuple.of(Stone.flat(BLACK), TakPrinter.codePoint(0x1F175)), //🅵
				Tuple.of(Stone.flat(WHITE).standUp(), TakPrinter.codePoint(0x1F142)), //🅂
				Tuple.of(Stone.flat(BLACK).standUp(), TakPrinter.codePoint(0x1F182)) //🆂
		);
	}

	@Property
	void stacks(@ForAll Deque<Stone> stack) {
		List<String> printedStack = TakPrinter.print(stack);
		if (stack.isEmpty()) {
			assertThat(printedStack).hasSize(1);
			assertThat(printedStack.get(0)).isEqualTo(EMPTY_SQUARE);
		} else {
			assertThat(printedStack).hasSize(stack.size());
		}
	}

	@Property
	void squares(@ForAll Deque<Stone> stack) {
		Square square = new Square(stack);
		List<String> printedSquare = TakPrinter.print(square);
		if (stack.isEmpty()) {
			assertThat(printedSquare).hasSize(1);
			assertThat(printedSquare.get(0)).isEqualTo(VERTICAL_DIVIDER + EMPTY_SQUARE);
		} else {
			assertThat(printedSquare).hasSize(stack.size());
			assertThat(printedSquare).allMatch(line -> line.startsWith(VERTICAL_DIVIDER));
		}
	}

	@Property
	void emtpyRank(@ForAll @IntRange(min = 3, max = 8) int boardSize) {
		List<Square> rank = new ArrayList<>();
		for (int i = 0; i < boardSize; i++) {
			rank.add(new Square());
		}

		List<String> rankLines = printRank("1", rank);
		assertThat(rankLines).hasSize(1);

		String rankString = rankLines.get(0);
		int expectedLineLength = boardSize * (1 + VERTICAL_DIVIDER.length()) + 5;
		assertThat(rankString).hasSize(expectedLineLength);
		assertThat(rankString).startsWith("1" + VERTICAL_DIVIDER);
		assertThat(rankString).endsWith(VERTICAL_DIVIDER + "1");
	}

	@Property
	void ranks(@ForAll @Size(min = 3, max = 8) List<Square> rank) {

		List<String> rankLines = printRank(rankName(8), rank);
		// printLines(rankLines);

		int expectedLines = Math.max(1, maxStones(rank));
		assertThat(rankLines).hasSize(expectedLines);

		for (String rankLine : rankLines) {
			assertThat(rankLine).startsWith(rankName(8) + VERTICAL_DIVIDER);
			assertThat(rankLine).endsWith(VERTICAL_DIVIDER + rankName(8));
		}
	}

	@Property
	void emptyBoard(@ForAll @EmptyBoard(value = true) Board board) {

		List<String> boardLines = TakPrinter.print(board);
		// printLines(boardLines);

		assertThat(boardLines).hasSize(1 + board.size() * 2 + 2);

		assertFilesLine(boardLines.get(0));
		boardLines.remove(0);
		assertFilesLine(boardLines.get(boardLines.size() - 1));
		boardLines.remove(boardLines.size() - 1);

		for (int i = 0; i < boardLines.size(); i++) {
			String line = boardLines.get(i);
			if (i % 2 != 0) {
				int expectedLineLength = board.size() * (1 + VERTICAL_DIVIDER.length()) + 5;
				assertThat(line).hasSize(expectedLineLength);
				int rank = board.size() - Math.floorDiv(i, 2);
				assertThat(line).startsWith(rankName(rank) + VERTICAL_DIVIDER);
				assertThat(line).endsWith(VERTICAL_DIVIDER + rankName(rank));
			}
		}
	}

	@Property(tries = 10)
	void nonEmptyBoard(@ForAll @EmptyBoard(value = false) Board board) {

		List<String> boardLines = TakPrinter.print(board);
		// printLines(boardLines);

		assertFilesLine(boardLines.get(0));
		boardLines.remove(0);
		assertFilesLine(boardLines.get(boardLines.size() - 1));
		boardLines.remove(boardLines.size() - 1);

		for (String line : boardLines) {
			if (line.startsWith(HORIZONTAL_DIVIDER)) {
				int expectedLineLength = board.size() * 2 + 3;
				assertThat(line).hasSize(expectedLineLength);
				assertThat(line.toCharArray()).containsOnly(HORIZONTAL_DIVIDER.charAt(0));
			} else {
				String rankName = line.substring(0, 1);
				assertThat(line).startsWith(rankName + VERTICAL_DIVIDER);
				assertThat(line).endsWith(VERTICAL_DIVIDER + rankName);
			}
		}
	}

	private void assertFilesLine(String lastLine) {
		assertThat(lastLine).startsWith(EMPTY_SQUARE + VERTICAL_DIVIDER);
		assertThat(lastLine).endsWith(VERTICAL_DIVIDER + EMPTY_SQUARE);
	}

	private int maxStones(final List<Square> rank) {
		return rank.stream().mapToInt(square -> square.stack().size()).max().orElse(0);
	}

	private void printLines(final List<String> lines) {
		System.out.println();
		for (String rankLine : lines) {
			System.out.println(rankLine);
		}
		System.out.println();
		System.out.println("#######################");
	}

}
