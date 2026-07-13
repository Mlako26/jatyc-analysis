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

		// I don't believe this can be modeled in Jatyc or by protocols in general. After all, protocols are usually
		// used to declare the order in which methods are executed, or which methods are available for an object
		// in a specific point of a program. This is more of an invalid argument error, not method call.
		
		@Test
		void addUnsupported() {
			assertThrows(UnsupportedOperationException.class, () -> List.of("foo").add("bar"));
		}

		// Probably can be done by sending the element to end already in the protocol definition
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

		// I dont remember if constructors were a thing or not in protocols.
		// If they are, then using this empty constructor, which generates an empty list, should leave the object
		// in a state where getFirst is not allowed.
		// Otherwise, we could also require the programmer to first use the conditional method isEmpty() 
		// to check if the list is empty before calling getFirst, which would be a valid protocol.
	}
