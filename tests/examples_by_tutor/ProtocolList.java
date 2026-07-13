
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
	
	@Test
	void previousMustHaveElement() {
		ListIterator<Integer> iter = List.of(1).listIterator();
		assertThrows(NoSuchElementException.class, () -> iter.previous());
	}
	
	@Test
	void removeMustHappenAfterNext() {
		List<Integer> list = new ArrayList<>();
		list.add(1);
		ListIterator<Integer> iter = list.listIterator();
		assertThrows(IllegalStateException.class, () -> iter.remove());
	}
	
	@Test
	void removeMustHappenOnlyOncePerCallToNext() {
		List<Integer> list = new ArrayList<>();
		list.add(1);
		ListIterator<Integer> iter = list.listIterator();
		iter.next();
		iter.remove();
		assertThrows(IllegalStateException.class, () -> iter.remove());
	}
	
	@Test
	void removeMustNotHappenAfterAdd() {
		List<Integer> list = new ArrayList<>();
		list.add(1);
		ListIterator<Integer> iter = list.listIterator();
		iter.next();
		iter.add(2);
		assertThrows(IllegalStateException.class, () -> iter.remove());
	}

}
