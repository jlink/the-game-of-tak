package tak.testingSupport;

import java.util.*;
import java.util.function.*;

import tak.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.domains.*;
import net.jqwik.api.providers.*;

import static tak.Stone.Colour.*;
import static tak.Stone.*;

public class TakDomainContext extends AbstractDomainContextBase {

	public TakDomainContext() {
		// Have precedence over default providers
		setDefaultPriority(1);
		registerProvider(gamesProvider());
		registerProvider(emptyBoardProvider());
		registerArbitrary(tupleOfBoardAndSpotType(), boardAndSpot());
		registerArbitrary(tupleOfGameAndSpotListType(), gameAndSpots());
		registerArbitrary(Stone.class, stones());
		registerArbitrary(takStack(), stacks());
		registerArbitrary(Square.class, squares());
		registerArbitrary(Player.class, players());
	}

	private Arbitrary<Player> players() {
		return Arbitraries.of(Player.WHITE, Player.BLACK);
	}

	private Arbitrary<Tuple2<GameOfTak, List<Spot>>> gameAndSpots() {
		return gameSize(GameOfTak.MIN_SIZE, GameOfTak.MAX_SIZE)
					   .flatMap(size -> {
						   GameOfTak game = new GameOfTak(size);
						   Arbitrary<Spot> spot = Arbitraries.of(game.position().board().squares().keySet()).unique();
						   return spot.list().ofMinSize(3).ofMaxSize(size * size - 3)
									  .map(spots -> Tuple.of(game, spots));
					   });
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
					return Set.of(newGame(targetType));
				}
				return Set.of();
			}
		};
	}

	private Arbitrary<GameOfTak> newGame(final TypeUsage targetType) {
		return gameSize(targetType).map(GameOfTak::new);
	}

	private ArbitraryProvider emptyBoardProvider() {
		return new ArbitraryProvider() {
			@Override
			public boolean canProvideFor(TypeUsage targetType) {
				return targetType.isOfType(tak.Board.class);
			}

			@Override
			public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
				Optional<EmptyBoard> boardAnnotation = targetType.findAnnotation(EmptyBoard.class);
				if (boardAnnotation.isPresent() && boardAnnotation.get().value()) {
					return Set.of(gameSize(targetType).map(tak.Board::ofSize));
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
		return TakDomainContext.this.gameSize(actualMinSize, actualMaxSize);
	}

	private Arbitrary<Integer> gameSize(final int minSize, final int maxSize) {
		return Arbitraries.integers().between(minSize, maxSize);
	}

	private Arbitrary<tak.Board> boards(int size) {
		Arbitrary<tak.Board> emptyBoards = newBoards(size);
		return emptyBoards.flatMap(
				board -> {
					SetArbitrary<Tuple2<Spot, Deque<Stone>>> spotsToFill =
							Arbitraries.of(board.squares().keySet())
									   .unique()
									   .flatMap(spot -> {
										   Arbitrary<Deque<Stone>> stacks = stacks().filter(stack -> stack.size() > 1);
										   return stacks.map(stack -> Tuple.of(spot, stack));
									   })
									   .set()
									   .ofMinSize(1)
									   .ofMaxSize(board.size() * board.size() - 3);
					return spotsToFill
								   .map(spotAndStacks -> {
									   Map<Spot, Deque<Stone>> changes = new HashMap<>();
									   for (Tuple2<Spot, Deque<Stone>> spotAndStack : spotAndStacks) {
										   changes.put(spotAndStack.get1(), spotAndStack.get2());
									   }
									   return board.change(changes);
								   });
				});
	}

	private Arbitrary<Square> squares() {
		return stacks().map(Square::new);
	}

	private TypeUsage takStack() {
		return TypeUsage.of(
				Deque.class,
				TypeUsage.of(Stone.class)
		);
	}

	private Arbitrary<Deque<Stone>> stacks() {
		return stones().list().ofMaxSize(10)
					   .filter(list -> count(list, capstone(WHITE)) <= 1)
					   .filter(list -> count(list, capstone(BLACK)) <= 1)
					   .map(c -> (Deque<Stone>) new ArrayDeque<>(c))
					   .filter(stack -> !isBelowTop(stack, Stone::isStanding));
	}

	static boolean isBelowTop(Deque<Stone> stack, Predicate<Stone> condition) {
		if (stack.isEmpty()) {
			return false;
		}
		ArrayDeque<Stone> clone = new ArrayDeque<>(stack);
		clone.removeFirst();
		return count(clone, condition) > 0;
	}

	static int count(final Collection<Stone> stones, final Stone stone) {
		return count(stones, s -> s.equals(stone));
	}

	static int count(final Collection<Stone> stones, final Predicate<Stone> condition) {
		return Math.toIntExact(stones.stream().filter(condition).count());
	}

	private Arbitrary<Stone> stones() {
		Arbitrary<Stone.Colour> colours = Arbitraries.of(Stone.Colour.class);
		return Arbitraries.frequencyOf(
				Tuple.of(10, colours.map(colour -> Stone.flat(colour))),
				Tuple.of(5, colours.map(colour -> Stone.flat(colour).standUp())),
				Tuple.of(5, colours.map(colour -> capstone(colour)))
		);
	}

	private Arbitrary<Tuple2<tak.Board, Spot>> boardAndSpot() {
		Arbitrary<tak.Board> boards = newBoards(0);
		return boards.flatMap(board -> {
			Arbitrary<Spot> spots = Arbitraries.of(board.squares().keySet());
			return spots.map(spot -> Tuple.of(board, spot));
		});
	}

	private TypeUsage tupleOfBoardAndSpotType() {
		return TypeUsage.of(
				Tuple2.class,
				TypeUsage.of(tak.Board.class),
				TypeUsage.of(Spot.class)
		);
	}

	private TypeUsage tupleOfGameAndSpotListType() {
		return TypeUsage.of(
				Tuple2.class,
				TypeUsage.of(GameOfTak.class),
				TypeUsage.of(
						List.class,
						TypeUsage.of(Spot.class)
				)
		);
	}

	private Arbitrary<tak.Board> newBoards(int size) {
		int actualMinSize = size == 0 ? GameOfTak.MIN_SIZE : size;
		int actualMaxSize = size == 0 ? GameOfTak.MAX_SIZE : size;
		return gameSize(actualMinSize, actualMaxSize).map(tak.Board::ofSize);
	}
}
