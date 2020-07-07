package tak;

public enum TakPlayer {
	WHITE {
		@Override
		public TakStone.Colour stoneColour() {
			return TakStone.Colour.WHITE;
		}

		@Override
		public TakPlayer opponent() {
			return TakPlayer.BLACK;
		}
	},
	BLACK {
		@Override
		public TakStone.Colour stoneColour() {
			return TakStone.Colour.BLACK;
		}

		@Override
		public TakPlayer opponent() {
			return TakPlayer.WHITE;
		}
	},
	NONE;

	public TakStone.Colour stoneColour() {
		throw new UnsupportedOperationException();
	}

	public TakPlayer opponent() {
		return NONE;
	}
}
