package tak.testingSupport;

import java.util.*;

import tak.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.constraints.*;

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
			assertThat(printedSquare.get(0)).isEqualTo("|" + EMPTY_SQUARE);
		} else {
			assertThat(printedSquare).hasSize(stack.size());
			assertThat(printedSquare).allMatch(line -> line.startsWith("|"));
		}
	}

	@Property
	void emtpyRank(@ForAll @IntRange(min = 3, max = 8) int boardSize) {
		List<TakSquare> rank = new ArrayList<>();
		for (int i = 0; i < boardSize; i++) {
			rank.add(new TakSquare());
		}

		List<String> rankLines = TakPrinter.printRank('a', rank);
		assertThat(rankLines).hasSize(1);

		String rankString = rankLines.get(0);
		int expectedLineLength = 1 + boardSize * (1 + EMPTY_SQUARE.length()) + 2;
		assertThat(rankString).hasSize(expectedLineLength);
		assertThat(rankString).startsWith("a|");
		assertThat(rankString).endsWith("|a");
	}

	@Property
	// @Report(Reporting.GENERATED)
	void ranks(@ForAll @Size(min = 3, max = 8) List<TakSquare> rank) {

		List<String> rankLines = TakPrinter.printRank('h', rank);
		for (String rankLine : rankLines) {
			System.out.println(rankLine);
		}

		int expectedLines = Math.max(1, maxStones(rank));
		assertThat(rankLines).hasSize(expectedLines);

		String firstLine = rankLines.get(0);
		assertThat(firstLine).startsWith("h|");
		assertThat(firstLine).endsWith("|h");

		rankLines.remove(0);
		for (String rankLine : rankLines) {
			assertThat(rankLine).startsWith(" |");
			assertThat(rankLine).endsWith("| ");
		}
	}

	private int maxStones(final List<TakSquare> rank) {
		return rank.stream().mapToInt(square -> square.stack().size()).max().orElse(0);
	}

}
