package tak;

public abstract class TakMove {

	public static TakMove place(final TakPlayer player, final TakStone stone, final TakBoard.Spot spot) {
		return new PlacingMove(player, stone, spot);
	}

	private final TakPlayer player;

	protected TakMove(final TakPlayer player) {
		this.player = player;
	}

}

class PlacingMove extends TakMove {
	public PlacingMove(
			final TakPlayer player,
			final TakStone stone,
			final TakBoard.Spot spot
	) {
		super(player);
	}
}
