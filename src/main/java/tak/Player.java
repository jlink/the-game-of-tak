package tak;

public enum Player {
	WHITE {
		@Override
		public Stone.Colour stoneColour() {
			return Stone.Colour.WHITE;
		}

		@Override
		public Player opponent() {
			return Player.BLACK;
		}
	},
	BLACK {
		@Override
		public Stone.Colour stoneColour() {
			return Stone.Colour.BLACK;
		}

		@Override
		public Player opponent() {
			return Player.WHITE;
		}
	},
	NONE;

	public Stone.Colour stoneColour() {
		throw new UnsupportedOperationException();
	}

	public Player opponent() {
		return NONE;
	}
}
