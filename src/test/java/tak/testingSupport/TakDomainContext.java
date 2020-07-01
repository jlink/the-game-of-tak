package tak.testingSupport;

import tak.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.providers.*;

public class TakDomainContext extends AbstractDomainContextBase {

	public TakDomainContext() {
		registerArbitrary(TakBoard.class, newBoards());
		registerArbitrary(tupleOfBoardAndSpotType(), boardsAndSpots());
		registerArbitrary(TakStone.class, stones());
	}

	private Arbitrary<TakStone> stones() {
		Arbitrary<TakStone.Colour> colours = Arbitraries.of(TakStone.Colour.class);
		return Arbitraries.frequencyOf(
				Tuple.of(10, colours.map(colour -> TakStone.flat(colour))),
				Tuple.of(5, colours.map(colour -> TakStone.flat(colour).standUp())),
				Tuple.of(5, colours.map(colour -> TakStone.capstone(colour)))
		);
	}

	private Arbitrary<Tuple2<TakBoard, TakBoard.Spot>> boardsAndSpots() {
		Arbitrary<TakBoard> boards = newBoards();
		return boards.flatMap(board -> {
			Arbitrary<TakBoard.Spot> spots = Arbitraries.of(board.squares().keySet());
			return spots.map(spot -> Tuple.of(board, spot));
		});
	}

	private TypeUsage tupleOfBoardAndSpotType() {
		return TypeUsage.of(
				Tuple2.class,
				TypeUsage.of(TakBoard.class),
				TypeUsage.of(TakBoard.Spot.class)
		);
	}

	private Arbitrary<TakBoard> newBoards() {
		return Arbitraries
					   .integers().between(TakBoard.MIN_SIZE, TakBoard.MAX_SIZE)
					   .map(TakBoard::ofSize);
	}
}
