package tak.testingSupport;

import java.util.*;
import java.util.function.*;

import tak.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.providers.*;

import static tak.TakStone.Colour.*;
import static tak.TakStone.*;

public class TakDomainContext extends AbstractDomainContextBase {

	public TakDomainContext() {
		registerProvider(gamesProvider());
		registerProvider(emptyBoardProvider());
		registerArbitrary(tupleOfBoardAndSpotType(), boardsAndSpots());
		registerArbitrary(TakStone.class, stones());
		registerArbitrary(takStack(), stacks());
		registerArbitrary(TakSquare.class, squares());
	}

	private ArbitraryProvider gamesProvider() {
		return new ArbitraryProvider() {
			@Override
			public boolean canProvideFor(final TypeUsage targetType) {
				return targetType.isOfType(GameOfTak.class);
			}

			@Override
			public Set<Arbitrary<?>> provideFor(final TypeUsage targetType, final SubtypeProvider subtypeProvider) {
				Optional<Game> gameAnnotation = targetType.findAnnotation(Game.class);
				if (gameAnnotation.isPresent() && gameAnnotation.get().isNew()) {
					return Set.of(gameSize(targetType).map(GameOfTak::new));
				}

				return Set.of();
			}
		};
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
					return Set.of(gameSize(targetType).map(TakBoard::ofSize));
				}
				return Set.of(gameSize(targetType).flatMap(gameSize -> boards(gameSize)));
			}
		};
	}

	private Arbitrary<Integer> gameSize(final TypeUsage targetType) {
		Optional<GameSize> sizeAnnotation = targetType.findAnnotation(GameSize.class);
		int gameSize = sizeAnnotation.map(GameSize::value).orElse(0);
		int actualMinSize = gameSize == 0 ? GameOfTak.MIN_SIZE : gameSize;
		int actualMaxSize = gameSize == 0 ? GameOfTak.MAX_SIZE : gameSize;
		return Arbitraries.integers().between(actualMinSize, actualMaxSize);
	}

	private Arbitrary<TakBoard> boards(int size) {
		Arbitrary<TakBoard> emptyBoards = newBoards(size);
		return emptyBoards.flatMap(
				board -> {
					SetArbitrary<Tuple2<TakBoard.Spot, Deque<TakStone>>> spotsToFill =
							Arbitraries.of(board.squares().keySet())
									   .unique()
									   .flatMap(spot -> {
										   Arbitrary<Deque<TakStone>> stacks = stacks().filter(stack -> stack.size() > 1);
										   return stacks.map(stack -> Tuple.of(spot, stack));
									   })
									   .set()
									   .ofMinSize(1)
									   .ofMaxSize(board.size() * board.size() - 3);
					return spotsToFill
								   .map(spotAndStacks -> {
									   Map<TakBoard.Spot, Deque<TakStone>> changes = new HashMap<>();
									   for (Tuple2<TakBoard.Spot, Deque<TakStone>> spotAndStack : spotAndStacks) {
										   changes.put(spotAndStack.get1(), spotAndStack.get2());
									   }
									   return board.change(changes);
								   });
				});
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
		int actualMinSize = size == 0 ? GameOfTak.MIN_SIZE : size;
		int actualMaxSize = size == 0 ? GameOfTak.MAX_SIZE : size;
		return Arbitraries
					   .integers().between(actualMinSize, actualMaxSize)
					   .map(TakBoard::ofSize);
	}
}
