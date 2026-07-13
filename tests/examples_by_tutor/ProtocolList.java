
package op.samples;

import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

public class ProtocolListIterator {
	
	@Test
	void nextMustHaveElement() {
		ListIterator<Integer> list = List.of(1).listIterator();
		list.next();
		assertThrows(NoSuchElementException.class, () -> list.next());
	}

	// This is a simple hasNext() protocol we've already tested
	
	@Test
	void previousMustHaveElement() {
		ListIterator<Integer> iter = List.of(1).listIterator();
		assertThrows(NoSuchElementException.class, () -> iter.previous());
	}

	// Same but with hasPrevious()
	
	@Test
	void removeMustHappenAfterNext() {
		List<Integer> list = new ArrayList<>();
		list.add(1);
		ListIterator<Integer> iter = list.listIterator();
		assertThrows(IllegalStateException.class, () -> iter.remove());
	}

	// Same here with the remove, can only be called after next() and should be included in the protocol
	
	@Test
	void removeMustHappenOnlyOncePerCallToNext() {
		List<Integer> list = new ArrayList<>();
		list.add(1);
		ListIterator<Integer> iter = list.listIterator();
		iter.next();
		iter.remove();
		assertThrows(IllegalStateException.class, () -> iter.remove());
	}

	// After calling remove, it moves into a different typestate where remove is not allowed anymore.
	
	@Test
	void removeMustNotHappenAfterAdd() {
		List<Integer> list = new ArrayList<>();
		list.add(1);
		ListIterator<Integer> iter = list.listIterator();
		iter.next();
		iter.add(2);
		assertThrows(IllegalStateException.class, () -> iter.remove());
	}

	// Same here with add, calling it leaves the object in a state where remove is not allowed to be called.

}
