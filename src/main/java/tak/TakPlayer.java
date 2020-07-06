package tak;

public enum TakPlayer {
	WHITE {
		@Override
		public TakStone.Colour stoneColour() {
			return TakStone.Colour.WHITE;
		}
	},
	BLACK {
		@Override
		public TakStone.Colour stoneColour() {
			return TakStone.Colour.BLACK;
		}
	},
	NONE;

	public TakStone.Colour stoneColour() {
		throw new UnsupportedOperationException();
	}
}
