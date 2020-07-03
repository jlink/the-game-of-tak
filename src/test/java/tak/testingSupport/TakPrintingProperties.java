package tak.testingSupport;

import java.util.*;

import tak.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

import static org.assertj.core.api.Assertions.*;
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
			assertThat(printedStack.get(0)).isEqualTo(" ");
		} else {
			assertThat(printedStack).hasSize(stack.size());
		}
		// System.out.println(printedStack);
	}

}
