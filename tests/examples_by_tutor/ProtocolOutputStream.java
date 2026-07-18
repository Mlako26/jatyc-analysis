	package op.samples;

	import static org.junit.Assert.assertThrows;

	import java.util.ArrayList;
	import java.util.List;
	import java.util.NoSuchElementException;

	import org.junit.jupiter.api.Test;

	public class ProtocolList {
		
		@Test
		void toArrayHasIncompatibleType() {
			List<String> list = new ArrayList<>();
			list.add("foo");
			assertThrows(ArrayStoreException.class, () -> list.toArray(new Integer[3]));
		}

		// Jatyc currently does not have support for generics sadly (its in the documentation).
		// Anyways, this is still an argument problem, not a protocol one. As an example of this, let's suppose we have a
		// protocoled class IntList that implements List<E>, and let's suppose Jatyc has support for generics. Then, the
		// IntList class would have to implement method toArray(T[] a) with a generic type T. The compiler might still
		// not know in compilation time what the type of the array would be, and thus it cannot be checked in the protocol.
		// The array could be provided via user input, randomly generated, etc.
		
		@Test
		void addUnsupported() {
			assertThrows(UnsupportedOperationException.class, () -> List.of("foo").add("bar"));
		}

		// I would first create a local example, and then see if you can stub List, but it might be hard

		@Test
		void passingNullToContainsAll() {
			List<String> list = new ArrayList<>();
			assertThrows(NullPointerException.class, () -> list.containsAll(null));
		}

		// Does not require a protocol since this is basic type checking, not related to method call restrictions
		// Java should recognize that we are passing null and not allow statically to effect that call.
		
		@Test 
		void invalidIndex() {
			List<String> list = new ArrayList<>();
			assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
		}

		// Same here, could potentally statically check that index should be non-negative
		// Unrelated to method orders or restrictions, more so in arguments
		
		@Test 
		void getFirstOnEmpty() {
			List<String> list = new ArrayList<>();
			assertThrows(NoSuchElementException.class, () -> list.getFirst());
		}

		// We could also require the programmer to first use the conditional method isEmpty() 
		// to check if the list is empty before calling getFirst, which would be a valid protocol.
}
