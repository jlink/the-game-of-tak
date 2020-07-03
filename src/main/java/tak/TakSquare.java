package tak;

import java.util.*;

public class TakSquare {

	private final Deque<TakStone> stack;

	public TakSquare() {
		this(new ArrayDeque<>());
	}

	public TakSquare(Deque<TakStone> stack) {
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

	public TakSquare set(final Deque<TakStone> stack) {
		return new TakSquare(stack);
	}

	public Deque<TakStone> stack() {
		// Copy to make it unmodifiable
		return new ArrayDeque<>(stack);
	}

	public Optional<TakStone> top() {
		return Optional.ofNullable(stack.peekFirst());
	}
}
