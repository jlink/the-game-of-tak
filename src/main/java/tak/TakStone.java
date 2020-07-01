package tak;

import java.util.*;

public class TakStone {

	public enum Colour {WHITE, BLACK}

	enum Shape {NORMAL, CAPSTONE}

	enum Position {STANDING, FLAT}

	public static TakStone capstone(Colour colour) {
		return new TakStone(colour, Shape.CAPSTONE, Position.STANDING);
	}

	public static TakStone flat(Colour colour) {
		return new TakStone(colour, Shape.NORMAL, Position.FLAT);
	}

	static TakStone copy(final TakStone stone) {
		return new TakStone(stone.colour, stone.shape, stone.position);
	}

	private final Colour colour;
	private final Shape shape;
	private final Position position;

	private TakStone(Colour colour, Shape shape, Position position) {
		this.colour = colour;
		this.shape = shape;
		this.position = position;
	}

	public String toPTN() {
		if (shape == Shape.CAPSTONE) {
			return PTN.CAPSTONE;
		} else if (position == Position.FLAT) {
			return PTN.FLAT_STONE;
		} else {
			return PTN.STANDING_STONE;
		}
	}

	public Colour colour() {
		return colour;
	}

	public boolean isCapstone() {
		return shape == Shape.CAPSTONE;
	}

	public boolean isStanding() {
		return position == Position.STANDING;
	}

	public TakStone flatten() {
		if (!isStanding() || isCapstone()) {
			throw new TakException(String.format("%s cannot be flattened", toString()));
		}
		return new TakStone(colour, shape, Position.FLAT);
	}

	public TakStone standUp() {
		if (isStanding()) {
			throw new TakException(String.format("%s cannot stand up", toString()));
		}
		return new TakStone(colour, shape, Position.STANDING);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TakStone takStone = (TakStone) o;
		return colour == takStone.colour &&
					   shape == takStone.shape &&
					   position == takStone.position;
	}

	@Override
	public int hashCode() {
		return Objects.hash(colour, shape, position);
	}

	@Override
	public String toString() {
		return String.format("%s:%s", colour, toPTN());
	}

}
