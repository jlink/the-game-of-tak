package tak.testingSupport;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.arbitraries.SetArbitrary;
import tak.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.providers.*;

import static tak.TakStone.Colour.*;
import static tak.TakStone.*;

public class TakDomainContext extends AbstractDomainContextBase {

	public TakDomainContext() {
		registerProvider(emptyBoardProvider());
		registerArbitrary(tupleOfBoardAndSpotType(), boardsAndSpots());
		registerArbitrary(TakStone.class, stones());
		registerArbitrary(takStack(), stacks());
		registerArbitrary(TakSquare.class, squares());
	}

	private ArbitraryProvider emptyBoardProvider() {
		return new ArbitraryProvider() {
			@Override
			public boolean canProvideFor(TypeUsage targetType) {
				return targetType.isOfType(TakBoard.class);
			}

			@Override
			public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
				Optional<Board> boardAnnotation = targetType.findAnnotation(Board.class);
				if (boardAnnotation.isPresent() && boardAnnotation.get().empty()) {
					return Set.of(newBoards(boardAnnotation.get().size()));
				}
				return Set.of(boards(boardAnnotation.map(Board::size).orElse(0)));
			}
		};
	}

	private Arbitrary<TakBoard> boards(int size) {
		Arbitrary<TakBoard> emptyBoards = newBoards(size);
		return emptyBoards.flatMap(
				board -> {
					SetArbitrary<TakBoard.Spot> spotsToFill =
							Arbitraries.of(board.squares().keySet())
									.unique()
									.set()
									.ofMinSize(1)
									.ofMaxSize(board.size() * board.size() - 3);
					return spotsToFill.flatMap(spots -> {
						return stacks()
								.filter(stack -> stack.size() > 1)
								.map(stack -> {
									Map<TakBoard.Spot, Deque<TakStone>> changes = new HashMap<>();
									for (TakBoard.Spot spot : spots) {
										changes.put(spot, stack);
									}
									return board.change(changes);
								});
					});
				}
		);
	}

	private Arbitrary<TakSquare> squares() {
		return stacks().map(TakSquare::new);
	}

	private TypeUsage takStack() {
		return TypeUsage.of(
				Deque.class,
				TypeUsage.of(TakStone.class)
		);
	}

	private Arbitrary<Deque<TakStone>> stacks() {
		return stones().list().ofMaxSize(10)
				.filter(list -> count(list, capstone(WHITE)) <= 1)
				.filter(list -> count(list, capstone(BLACK)) <= 1)
				.map(c -> (Deque<TakStone>) new ArrayDeque<>(c))
					   .filter(stack -> !isBelowTop(stack, TakStone::isStanding));
	}

	static boolean isBelowTop(Deque<TakStone> stack, Predicate<TakStone> condition) {
		if (stack.isEmpty()) {
			return false;
		}
		ArrayDeque<TakStone> clone = new ArrayDeque<>(stack);
		clone.removeFirst();
		return count(clone, condition) > 0;
	}

	static int count(final Collection<TakStone> stones, final TakStone stone) {
		return count(stones, s -> s.equals(stone));
	}

	static int count(final Collection<TakStone> stones, final Predicate<TakStone> condition) {
		return Math.toIntExact(stones.stream().filter(condition).count());
	}

	private Arbitrary<TakStone> stones() {
		Arbitrary<TakStone.Colour> colours = Arbitraries.of(TakStone.Colour.class);
		return Arbitraries.frequencyOf(
				Tuple.of(10, colours.map(colour -> TakStone.flat(colour))),
				Tuple.of(5, colours.map(colour -> TakStone.flat(colour).standUp())),
				Tuple.of(5, colours.map(colour -> capstone(colour)))
		);
	}

	private Arbitrary<Tuple2<TakBoard, TakBoard.Spot>> boardsAndSpots() {
		Arbitrary<TakBoard> boards = newBoards(0);
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

	private Arbitrary<TakBoard> newBoards(int size) {
		int actualMinSize = size == 0 ? TakBoard.MIN_SIZE : size;
		int actualMaxSize = size == 0 ? TakBoard.MAX_SIZE : size;
		return Arbitraries
				.integers().between(actualMinSize, actualMaxSize)
				.map(TakBoard::ofSize);
	}
}
