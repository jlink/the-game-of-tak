package tak.testingSupport;

import java.util.*;

import tak.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

import static org.assertj.core.api.Assertions.*;
import static tak.TakStone.Colour.*;

class TakPrintingTests {

	@Property
	@FromData("stonesWithPrintString")
	void stones(@ForAll TakStone stone, @ForAll String representation) {
		String printedStone = TakPrinter.print(stone);
		System.out.println(printedStone);
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

}
