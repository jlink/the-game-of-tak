package tak;

public abstract class TakMove {

	public static TakMove place(final TakPlayer player, final TakStone stone, final TakBoard.Spot spot) {
		return new PlacingMove(player, stone, spot);
	}

	protected final TakPlayer player;

	protected TakMove(final TakPlayer player) {
		this.player = player;
	}

	public abstract Result execute(final TakPosition position, final GameOfTak.Status beforeStatus);

	public abstract String toPTN();

	@Override
	public String toString() {
		return String.format("Player(%s): %s", player, toPTN());
	}

	public class Result {
		private final TakPosition newPosition;
		private final GameOfTak.Status newStatus;

		Result(final TakPosition newPosition, final GameOfTak.Status newStatus) {

			this.newPosition = newPosition;
			this.newStatus = newStatus;
		}

		public GameOfTak.Status status() {
			return newStatus;
		}

		public TakPosition position() {
			return newPosition;
		}
	}
}

class PlacingMove extends TakMove {

	private final TakStone stone;
	private final TakBoard.Spot spot;

	public PlacingMove(
			final TakPlayer player,
			final TakStone stone,
			final TakBoard.Spot spot
	) {
		super(player);
		this.stone = stone;
		this.spot = spot;
	}

	@Override
	public Result execute(final TakPosition beforePosition, final GameOfTak.Status beforeStatus) {
		TakPosition newPosition = beforePosition;
		GameOfTak.Status newStatus = GameOfTak.Status.PRELUDE_BLACK;
		return new Result(newPosition, newStatus);
	}

	@Override
	public String toPTN() {
		String stoneString = stone.isStanding() ? stone.toPTN() : "";
		return String.format("%s%s", stoneString, spot.toPTN());
	}
}
