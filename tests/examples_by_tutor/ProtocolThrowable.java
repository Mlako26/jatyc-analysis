package op.samples;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.Test;

public class ProtocolOutputStream {

	@Test
	void closeMustNotHaveBeenCalled() throws IOException {
		FileOutputStream fos = new FileOutputStream(new File("temp.txt"));
		fos.close();
		assertThrows(IOException.class, () -> fos.write(0));
	}	

	// This is very simple by adding a basic FileOutputStream protocol, where after calling close() the object
	// moves into the end state, and thus no methods from the protocol can be called (such as write).
	
	@Test
	void closeMustNotHaveBeenCalledNotChecked() throws IOException {
		ByteArrayOutputStream fos = new ByteArrayOutputStream(100);
		fos.close();
		assertTrue(true); // No exception was thrown
	}	
	
	// Tests nothing relevant to protocols afaik. From https://docs.oracle.com/javase/8/docs/api/java/io/ByteArrayOutputStream.html,
	// "Closing a ByteArrayOutputStream has no effect. The methods in this class can be called after the stream has been closed without generating an IOException."

	@Test
	void closeMustNotHaveBeenCalledWithIndirection() throws IOException {
		FileOutputStream fos = new FileOutputStream(new File("temp.txt"));
		FilterOutputStream filter = new FilterOutputStream(fos);
		filter.close();
		assertThrows(IOException.class, () -> filter.write(0));
	}	
	
	// Here, clearly in the protocol of the FilterOutputStream, after calling close() the object finishes its protocol,
	// and thus no other methods should be allowed to be called.
	// One interesting thing is that calling close() on filter moves the underlying stream to the end state as well. 
	// We should have a test to check if Jatyc is able to recognize that writing to the underlying fos is 
	// allowed after closing the filter itself. Perhaps it simply will not be allowed since now the 
	// reference in the client code will be alliased (shared) after using it as an argument for the constructor of the filter.
	// There are maybe a coupleof interesting tests here to be done.

	@Test
	void closeMustNotHaveBeenCalledWithIndirectionNotChecked() throws IOException {
		ByteArrayOutputStream fos = new ByteArrayOutputStream(100);
		FilterOutputStream filter = new FilterOutputStream(fos);
		filter.close();
		assertTrue(true); // No exception was thrown
	}	

	// Nothing to test here really.
}
