package tak;

import java.util.*;

public class Square {

	private final Deque<Stone> stack;

	public Square() {
		this(new ArrayDeque<>());
	}

	public Square(Deque<Stone> stack) {
		this.stack = stack;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return true;
	}

	@Override
	public int hashCode() {
		return 42;
	}

	@Override
	public String toString() {
		return stack.toString();
	}

	public boolean isEmpty() {
		return stack.isEmpty();
	}

	public boolean isOccupied() {
		return !isEmpty();
	}

	public Square set(final Deque<Stone> stack) {
		return new Square(stack);
	}

	public Deque<Stone> stack() {
		// Copy to make it unmodifiable
		return new ArrayDeque<>(stack);
	}

	public Optional<Stone> top() {
		return Optional.ofNullable(stack.peekFirst());
	}

}
