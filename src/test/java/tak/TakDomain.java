package tak;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.providers.*;

public class TakDomain extends AbstractDomainContextBase {

	public TakDomain() {
		registerArbitrary(TakBoard.class, newBoards());
		registerArbitrary(tupleOfBoardAndSpotType(), boardsAndSpots());
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
