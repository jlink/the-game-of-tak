package tak;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.Tuple2;
import net.jqwik.api.constraints.*;
import tak.testingSupport.*;

import static org.assertj.core.api.Assertions.*;
import static tak.TakPrinter.*;
import static tak.TakStone.Colour.*;

@TakDomain
class TakPrintingProperties {

	@Property
	@FromData("stonesWithPrintString")
	void stones(@ForAll TakStone stone, @ForAll String representation) {
		String printedStone = TakPrinter.print(stone);
		assertThat(printedStone).isEqualTo(representation);
	}

	@Data
	List<Tuple2<TakStone, String>> stonesWithPrintString() {
		return List.of(
				Tuple.of(TakStone.capstone(WHITE), TakPrinter.codePoint(0x1F132)), //🄲
				Tuple.of(TakStone.capstone(BLACK), TakPrinter.codePoint(0x1F172)), //🅲
				Tuple.of(TakStone.flat(WHITE), TakPrinter.codePoint(0x1F135)), //🄵
				Tuple.of(TakStone.flat(BLACK), TakPrinter.codePoint(0x1F175)), //🅵
				Tuple.of(TakStone.flat(WHITE).standUp(), TakPrinter.codePoint(0x1F142)), //🅂
				Tuple.of(TakStone.flat(BLACK).standUp(), TakPrinter.codePoint(0x1F182)) //🆂
		);
	}

	@Property
	void stacks(@ForAll Deque<TakStone> stack) {
		List<String> printedStack = TakPrinter.print(stack);
		if (stack.isEmpty()) {
			assertThat(printedStack).hasSize(1);
			assertThat(printedStack.get(0)).isEqualTo(EMPTY_SQUARE);
		} else {
			assertThat(printedStack).hasSize(stack.size());
		}
	}

	@Property
	void squares(@ForAll Deque<TakStone> stack) {
		TakSquare square = new TakSquare(stack);
		List<String> printedSquare = TakPrinter.print(square);
		if (stack.isEmpty()) {
			assertThat(printedSquare).hasSize(1);
			assertThat(printedSquare.get(0)).isEqualTo(VERTICAL_BAR + EMPTY_SQUARE);
		} else {
			assertThat(printedSquare).hasSize(stack.size());
			assertThat(printedSquare).allMatch(line -> line.startsWith(VERTICAL_BAR));
		}
	}

	@Property
	void emtpyRank(@ForAll @IntRange(min = 3, max = 8) int boardSize) {
		List<TakSquare> rank = new ArrayList<>();
		for (int i = 0; i < boardSize; i++) {
			rank.add(new TakSquare());
		}

		List<String> rankLines = TakPrinter.printRank("1", rank);
		assertThat(rankLines).hasSize(1);

		String rankString = rankLines.get(0);
		int expectedLineLength = 1 + boardSize * (1 + EMPTY_SQUARE.length()) + 2;
		assertThat(rankString).hasSize(expectedLineLength);
		assertThat(rankString).startsWith("1" + VERTICAL_BAR);
		assertThat(rankString).endsWith(VERTICAL_BAR + "1");
	}

	@Property
	void ranks(@ForAll @Size(min = 3, max = 8) List<TakSquare> rank) {

		List<String> rankLines = TakPrinter.printRank(TakPrinter.printRank(8), rank);
		// printLines(rankLines);

		int expectedLines = Math.max(1, maxStones(rank));
		assertThat(rankLines).hasSize(expectedLines);

		String lastLine = rankLines.get(rankLines.size() - 1);
		assertThat(lastLine).startsWith(TakPrinter.printRank(8) + VERTICAL_BAR);
		assertThat(lastLine).endsWith(VERTICAL_BAR + TakPrinter.printRank(8));

		rankLines.remove(rankLines.size() - 1);
		for (String rankLine : rankLines) {
			assertThat(rankLine).startsWith(EMPTY_SQUARE + VERTICAL_BAR);
			assertThat(rankLine).endsWith(VERTICAL_BAR + EMPTY_SQUARE);
		}
	}

	@Property
	void emptyBoard(@ForAll @Board(empty = true) TakBoard board) {

		List<String> boardLines = TakPrinter.print(board);
		// printLines(boardLines);

		assertThat(boardLines).hasSize(1 + board.size() * 2 + 2);

		assertFirstBoardLine(boardLines);

		assertLastBoardLine(boardLines);

		boardLines.remove(0);
		boardLines.remove(boardLines.size() - 1);

		for (int i = 0; i < boardLines.size(); i++) {
			String line = boardLines.get(i);
			int expectedLineLength = board.size() * 2 + 3;
			if (i % 2 == 0) {
				assertThat(line).hasSize(expectedLineLength);
			} else {
				assertThat(line).hasSize(expectedLineLength);
				int rank = board.size() - Math.floorDiv(i , 2);
				assertThat(line).startsWith(TakPrinter.printRank(rank) + VERTICAL_BAR);
				assertThat(line).endsWith(VERTICAL_BAR + TakPrinter.printRank(rank));
			}
		}
	}

	private void assertLastBoardLine(List<String> boardLines) {
		String lastLine = boardLines.get(boardLines.size() - 1);
		assertThat(lastLine).startsWith(EMPTY_SQUARE + VERTICAL_BAR);
		assertThat(lastLine).endsWith(VERTICAL_BAR + EMPTY_SQUARE);
	}

	private void assertFirstBoardLine(List<String> boardLines) {
		String firstLine = boardLines.get(0);
		assertThat(firstLine).startsWith(EMPTY_SQUARE + VERTICAL_BAR);
		assertThat(firstLine).endsWith(VERTICAL_BAR + EMPTY_SQUARE);
	}

	private int maxStones(final List<TakSquare> rank) {
		return rank.stream().mapToInt(square -> square.stack().size()).max().orElse(0);
	}

	private void printLines(final List<String> lines) {
		for (String rankLine : lines) {
			System.out.println(rankLine);
		}
	}

}
