# Power and Limitations of Jatyc

The object of study of this project is the Jatyc tool. We will attempt to figure out how powerful it is and what its limitations are. The aim of this document is to compile a new test suite, with each test case targeting different points of interest within the functionality of the tool.

## Test Case Structure

Each test case described in this file will stick to the following structure:

- Name of the test.
- Aim of the test: what we want to test and why is it of interest.
- Implementation the test: which classes are involved and how they will interact, including class diagrams and pseudocode if necessary.
- Expectations of the test: Given the implementation, what we think will jatyc do or why the given implementation makes sense given the aim of the test.
- Results of the test: what happened when the test was ran.
- Conclusions of the test: what we can learn of the tool based on the results of the test.
- Extra notes of the test: Things of note or realizations that appeared during the writing and execution of the test case.

## Test Cases

### collaborator_compound_typestate

This example can be found [here](https://github.com/Mlako26/jatyc-analysis/tree/main/tests/collaborator_compound_typestate).

#### Aim

We want to see how much does Jatyc take into account the state of internal collaborators of an object following a protocol. In particular, jatyc seems to interpret a typestate as a set of enabled methods and their destination typestates. But could the typestate of an object be dictated also by the one of an internal collaborator? That is, could the typestate of the parent object be a compound of both typestates, where calls to the same method from the same parent typestate yield different results (such as an exception for example)?

#### Implementation

Create a Robot and RobotController class. The map that the robot will traverse is a 2x2 map and, starting out in the top left corner (coordinates (0,0)), it will have methods to move a single unit at a time in any cardinal direction. It will have a typestate for each position in the map, dictating which methods it can call so that it does not fall off the map.

The controller simply takes user input and then moves the robot. Its methods will be two simple ones with a boolean returning method `canMoveRobot()`, which "checks its connection to the robot", and then `move(direction: string)` which moves the robot on the corresponding direction. The protocol will consist of only two typestates, one for each method. 

There will be client code with instructions that will make the robot fail its protocol, but not the controller.

#### Expectations

For this example in particular, Jatyc should be able to tell from the client code which will explicitly show the movements the robot will make that it will break its protocol.

#### Results

Our first implementation had the controller receive in its constructor the robot to be controlled.

```java
	public RobotController(Robot robot) {
		this.robot = robot;
	}
```

This made it so Jatyc interpreted the robot as a *shared* reference, thus no method calls would work. For example:

```
RobotController.java:24: error: Cannot call [moveDown] on Shared{Robot}
                                this.robot.moveDown();
```

When instead implementing it so that it created its own robot within the constructor, and using the following client code:


```java
public class Main {
    public static void main(String args[]) throws Exception {
		RobotController controller = new RobotController();

		if (controller.canMoveRobot()) {
			controller.move("right");
		}
    }
}
```

We got the following errors:

```
RobotController.java:27: error: Cannot call [moveLeft] on State{Robot, TopLeft}
                                this.robot.moveLeft();
                                                   ^
RobotController.java:21: error: Cannot call [moveUp] on State{Robot, TopLeft}
                                this.robot.moveUp();
                                                 ^
2 errors
```

What this tells us is interesting. Since the robot starts at position topleft, the tool will not allow us to simply create a switch statement moving the robot around, and it blocks off both invalid methods that would break the protocol. 

We would need to add more methods to the `RobotProtocol`, such as new boolean returning methods that enable certain movements based off of the robot's current position. 

Trying to trick the system by adding private methods also doesn't work:


```java
	public void move(String direction) {
		switch (direction) {
			case "up":
				this.moveUp();
				break;
			case "down":
				this.moveDown();
				break;
			case "left":
				this.moveLeft();
				break;
			case "right":
				this.moveRight();
				break;
			default:
				throw new RuntimeException("Invalid direction of movement");
		}
	}

	private void moveLeft() {
		this.robot.moveLeft();
	}

	private void moveRight() {
		this.robot.moveRight();
	}

	private void moveDown() {
		this.robot.moveDown();
	}
	
	private void moveUp() {
		this.robot.moveUp();
	}
```

But it yields new interesting findings!

```
RobotController.java:42: error: Cannot call [moveRight] on Shared{Robot}
                this.robot.moveRight();
                                    ^
RobotController.java:50: error: Cannot call [moveUp] on Shared{Robot}
                this.robot.moveUp();
                                 ^
RobotController.java:46: error: Cannot call [moveDown] on Shared{Robot}
                this.robot.moveDown();
                                   ^
RobotController.java:7: error: [this.robot] did not complete its protocol (found: Shared{Robot} | State{Robot, ?})
public class RobotController {
       ^
RobotController.java:38: error: Cannot call [moveLeft] on Shared{Robot}
                this.robot.moveLeft();
                                   ^
5 errors
```

It seems that when calling a new method, the internal collaborator becomes a shared variable even if not provided as an argument to the method. Changing its visibility to `Public` did not change this result.

Trying to trick it even further by adding a `@Requires` statement at the method declaration also did not work:

``` java
	private void moveLeft(@Requires({"TopRight", "BotRight"}) Robot robot) {
		robot.moveLeft();
	}

	private void moveRight(@Requires({"TopLeft", "BotLeft"}) Robot robot) {
		robot.moveRight();
	}

	private void moveDown(@Requires({"TopLeft", "TopRight"}) Robot robot) {
		robot.moveDown();
	}
	
	private void moveUp(@Requires({"BotLeft", "BotRight"}) Robot robot) {
		robot.moveUp();
	}
```

Returning errors similar to the first one

```
RobotController.java:18: error: Incompatible parameter: cannot cast from State{Robot, TopLeft} to State{Robot, BotLeft} | State{Robot, BotRight}
                                this.moveUp(this.robot);
                                                ^
RobotController.java:24: error: Incompatible parameter: cannot cast from State{Robot, TopLeft} to State{Robot, BotRight} | State{Robot, TopRight}
                                this.moveLeft(this.robot);
                                                  ^
2 errors
```

Seems like this might just be a restriction that the programmer will have to live with in order to write "safer code" and use the tool freely.

#### Conclusion

Seems like there is a pretty strong limitation with Jatyc where it does not really care about client code if we are talking about the typestate of an internal collaborator. We could be sure that the robot would not fall of the map, and it still would not care.

We need to explicitly delcare with boolean returning methods the current typestate we are in and therefore the methods that are available to the internal collaborator.

#### Extra notes

- Is an object able to read the current typestate of an internal collaborator and make different decisions based off of it? Perhaps land in different typestates? 
  - Does not seem like it. More likely it will use the methods available in the current typestate, which might provide some information into the next typestate of the collaborator and its available methods.
- I could create a test for it, but I'm pretty sure that even with having user inputted robot movements (so that Jatyc can't know which way the robot might go beforehand) would also result in the same error. This makes a lot of sense, since where Jatyc puts emphasis on is that you use objects safely, no matter which methods you call. That is, always verify the typestate you are on before calling a certain methods.

### normal_stack

This example can be found [here](https://github.com/Mlako26/jatyc-analysis/tree/main/tests/normal_stack).

#### Aim

Within the Four Dark Horses of Object Protocols paper, there is an example in section 2.1 mentioning the difficulty of protocols to properly analyze a stack state. The paper claims that to 100% do so, one needs a sort of stack automata to count the amount of push and pops.

This example aims to display the philosophy behind the Jatyc tool, which gives a twist on the paper's interpretation of object protocols. A fully functional stack protocol and obeying implementation can be achieved by being stricter on how a programmer can use an instance of the stack.

#### Implementation

Create a simple stack implementation with methods `push()`, `pop()`, `isEmpty()`, `isFull()`.

The protocol will be a simple one, where one can only push if the stack is not full, and pop if it's not empty.

```
typestate StackProtocol {
  Init = {
    boolean isEmpty(): <true: Init, false: CanPop>,
    boolean isFull(): <true: Init, false: CanPush>,
    drop: end
  }
  CanPush = {
    boolean isEmpty(): <true: Init, false: CanPop>,
    boolean isFull(): <true: Init, false: CanPush>,
    boolean push(T): Init,
    drop: end
  }
  CanPop = {
    boolean isFull(): <true: Init, false: CanPush>,
    boolean isEmpty(): <true: Init, false: CanPop>,
    T pop(): Init,
    drop: end
  }
}
```

There will be client code attempting to push or pop without first checking if it can.

#### Expectations

Jatyc will be able to tell that the client code is wrongly calling the stack's methods in all scenarios.

#### Results

I first tried doing this test with a stack supporting any kind of variable, using generics, but Jatyc started complaining. Then I remembered that they mentioned that limitation in their documentation:

```
Currently, JaTyC has some limitations that we are working on:
- No overall support for generics (parametric types are non-linear and nullable by default);
```

Using integers instead as the values stored within the stack, we get the following expected error using this client code:

```java
public class Main {
    public static void main(String args[]) throws Exception {
		Stack stack = new Stack(5);
		stack.push(5);
    }
}
```

```
Main.java:4: error: Cannot call [push] on State{Stack, Init}
                stack.push(5);
                          ^
1 error
```

#### Conclusion

As expected, using Jatyc definitely ensures that callers to an object that follows a protocol do not break it.

#### Extra notes

- Within this example we can see that Jatyc **DOES NOT** control the actual implementation of the stack, but whether if the client code properly traverses its typestates.

### faulty_stack

This example can be found [here](https://github.com/Mlako26/jatyc-analysis/tree/main/tests/faulty_stack).

#### Aim

Following the previous example, as far as we know Jatyc is unable to tell if an implementation actually does what it is supposed to. That is, it verifies that the usage of an object follows its protocol, but not that an object's methods follow some sort of **specification** (pre-post condition). 

While it registers that client code could be using an object in a way that it breaks its protocol, it does not control whether a boolean method simply returns a random value instead of what it actually should.

#### Implementation

Create a simple stack implementation with methods `push()`, `pop()`, `isEmpty()`, `isFull()`. Method `isFull()` will always return false.

The protocol will be the same as the previous example.

Finally, the client code will create a new instance of the faulty stack and then push more times than its capacity.

#### Expectations

Jatyc will not be able to tell that the implicit stack's specification is not being followed, and an exception will be thrown when pushing the final element.

#### Results

Using the following implementation for the `isFull()` method and client code we got Jatyc to compile.

```java
	public boolean isFull() {
		return false;
	}
```

```java
    public static void main(String args[]) throws Exception {
        Stack stack = new Stack(1);
        while (!stack.isFull()) {
            stack.push(5);
        }
    }
```

#### Conclusion

As expected, the tool's purpose is not to verify a "correct" implementation. Just that its method calls are in the correct order.

#### Extra notes

- If this test follows our expectations, it means that indeed jatyc, and perhaps protocols in general, are not able to assert that specifications are being followed within an object's methods. In that sense, pre-post conditions are perhaps more expressive and powerful in this area.

### restrictive_iterator

This example can be found [here](https://github.com/Mlako26/jatyc-analysis/tree/main/tests/restrictive_iterator).

#### Aim

With this example we aim to show how a protocol could be too restrictive for a particular implementation. This is similar to how a pre-post condition could overspecify a method.

#### Implementation

Have a class `BaseIterator` with the protocol that is provided by the Jatyc tool. In particular, this protocol only allows you to call the `next()` method if `hasNext()` returns true.

Then create a subclass that implements the methods of the interface, but called the `LoopIterator`. Calling the `next()` method on an instance of this class when the end of the collection has been reached, the iterator will loop back to the beginning of the collection and start again. The `hasNext()` method will work as usual, where it returns true at all stages except when the end of the collection is reached.

We can try this example both with a subprotocol, where `next()` can be called even if `hasNext()` returns false, and without one (`LoopIterator` would follow the abstract class' protocol).

Finally, have the client code simply make a loop of `hasNext()` and `next()` calls, and then a final `next()` call when it exits the loop. This technically will not raise an exception and is expected behavior for the object, but not for the protocol.

#### Expectations

Jatyc will not let the client use the `next()` method if `hasNext()` returns false. In the case of the `LoopIterator` following a subprotocol, Jatyc might not even allow the protocol to inherit from the parent one.

#### Results

This is the client code that was used.

```java
    public static void main(String[] args) {
        BaseIterator it = new LoopIterator(args);
        while (it.hasNext()) {
			it.next();
		}
		it.next();
	}
```


I first tried this test without having the `LoopIterator` subclass follow a protocol. As expected, we got the following error message:

```
ClientCode.java:9: error: Cannot call [next] on State{LoopIterator, end}
                it.next();
                       ^
1 error
```

This time, I created a new protocol for the subclass which allow to use the `hasNext()` and `next()` methods all the time.

```
typestate LoopIterator {
  Init = {
    boolean hasNext(): <true: Init, false: Init>,
    Object next(): Init,
    drop: end
  }
}
```

This time it worked. This makes sense since the subprotocol allows **more** method calls than the parent protocol. This means that no matter which class we use to create a `BaseIterator`, it will follow both protocols. Remember how in the documentation of the tool it states that:

```
The tool also has support for subtyping. This means that you may have a class with a protocol that extends another class with another protocol and the tool will ensure that the first protocol is a subtype of the second one.
```

Finally, just to check, I created a new `ClientCode2` example where I left both protocols working, but instead of creating a `LoopIterator` as is I upcasted it to a `BaseIterator`. It failed again.

```java
    BaseIterator it = new LoopIterator(args);
```

```
ClientCode2.java:9: error: Cannot call [next] on State{BaseIterator, end}
                it.next();
                       ^
1 error
```

#### Conclusion

We should probably review how the algorithm for subtyping support works, which is detailed in a paper linked by the developers of the tool. I don't believe it did in this case what it should've been doing.

#### Extra notes

### lax_iterator

This example can be found [here](https://github.com/Mlako26/jatyc-analysis/tree/main/tests/lax_iterator).

#### Aim

Following the previous examples where the protocol managed to be more restrictive than needed, and sometimes not allowing the objects to perform all of their features, this example will showcase how a protocol badly written might prove to be too lax. That is, having a class that, despite its method calls following a protocol, the execution raises an unwanted exception.

#### Implementation

Have a `BaseIterator` class with the usual protocol that is provided by the Jatyc tool. In particular, this protocol only allows you to call the `next()` method if `hasNext()` returns true.

Have a subclass `EvenIntIterator`, which iterates over arrays of integers. In the case that an odd number is encountered within the collection, it will raise an exception in the `next()` method. It will follow the same protocol as the parent class.

Finally, have a client code supply a list of items that include an odd number.

#### Expectations

Jatyc will not let the client use the `next()` method if `hasNext()` returns false. In the case of the `LoopIterator` following a subprotocol, Jatyc might not even allow the protocol to inherit from the parent one.

#### Results

Using the following client code we got the code to compile without errors.

```java
    public static void main(String[] args) {
		int[] array = {2,3};
        EvenIntIterator it = new EvenIntIterator(array);
        while (it.hasNext()) {
			it.next();
		}
	}
```

#### Conclusion

What this tells us is that Jatyc, just like doing pre and post conditions, is vulnerable to underspecification and human error prone.

#### Extra notes

- Obviously this implementation would be anything but production ready. Despite this, I find it interesting how, since Jatyc has absolutely no way of telling what the contents of the collection could be before the `next()` method is called (to disable it), the programmer will have to modify the way he implements in order to fully utilize the Jatyc protocols. That is, perhaps adding a new `nextIsOdd()` method, which can only be called between `hasNext()` and `next()`, to make sure an instance of this class does not raise exceptions.
- Do these examples make any sense? I'm not sure these are the kind of exceptions we want to examplify with (deliberately throwing an exception instead of something actually breaking).

### underspecified_iterator

This example can be found [here](https://github.com/Mlako26/jatyc-analysis/tree/main/tests/underspecified_iterator).

#### Aim

In this example, we simply want to show how a protocol might be underspecified and cause trouble.

#### Implementation

Have a class `UnderspecifiedIterator`, which iterates over arrays. The protocol will be a simple single state protocol, which allows the use of `hasNext()` and `next()` at any point. Then, have the client code reach the end of the collection with the iterator and then request the next object.

#### Expectations

Jatyc will compile and not raise any errors

#### Results

Indeed, using the following protocol and client code we get no errors when compiling, but do get errors when running.

```java
  public static void main(String[] args) {
		int[] array = {2,3};
    UnderspecifiedIterator it = new UnderspecifiedIterator(array);
    while (true) {
			it.next();
		}
	}
```

```
typestate UnderspecifiedIterator {
  HasNext = {
    boolean hasNext(): <true: Next, false: end>,
    int next(): HasNext,
    drop: end
  }
}
```

```
Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: Index 2 out of bounds for length 2
        at UnderspecifiedIterator.next(UnderspecifiedIterator.java:16)
        at ClientCode.main(ClientCode.java:8)
```

#### Conclusion

Indeed, Jatyc can't do anything about human error while defining protocols.

#### Extra notes


### state_equals_typestate

This example can be found [here](https://github.com/Mlako26/jatyc-analysis/tree/main/tests/state_equals_typestate).

#### Aim

In section 2.2 of the Four Dark Corners Of Object Protocols, it mentions an example of an implementation of the `Iterator` interface that adds its own reset method, which sends its index back to the beginning of the collection. Following the protocol of the interface, since this method is not mentioned Jatyc will consider it an *anytime* method. Then, calling it should not allow it to modify the state of an instance (the values of its internal collaborators).

But does modifying the state of an instance mean modifying the typestate? Remember what the documentation says about the typestates:

```
A typestate is associated with a Java class with the @Typestate annotation and defines: the object's states, the methods that can be safely called in each state, and the states resulting from the calls
```

I believe that not always modifying the state of an instance makes it necessarily change its typestate. That is pretty easy to see: having internal collaborators that represent a log context for example, or a timestamp for the last operation performed. Changing these would change the state of an object, but not necessarily its typestate. Therefore, were Jatyc to not allow an anytime method to modify any collaborator, we could say it is overly restrictive.

In this example, the collaborator modified will technically be of importance to the protocol, which will be the index of the array being iterater over.

#### Implementation

Have an Iterator, `ResettableIterator`, with the usual `next()` and `hasNext()` methods which adds an *anytime* method `reset()`. This one resets the index of the iterator to the beginning of the collection. The protocol of the class will include both `next()` and `hasNext()` methods but not `reset()`. It will have as an internal collaborators an array of strings and an integer index.

Have the client code utilize this `reset()` method after a few calls of the `hasNext()` and `next()` loop.

#### Expectations

Jatyc will not compile the `reset()` method since it modifies internal collaborators despite being an *anytime* method.

#### Results

Using the following client code, compilation did not raise any errors and indeed the prints show how the index went back to 0. 

```java
    public static void main(String[] args) {
		String[] array = {"hello", "world"};
        ResseteableIterator it = new ResseteableIterator(array);
        if (it.hasNext()) {
			it.next();
		}
		it.print();
		it.reset();
		it.print();
	}
```

#### Conclusion

I find it interesting that Jatyc would not check for internal collaborators modifications within *anytime* methods. Given its quite restrictive nature, I would've assumed that it simply would not let you modify them.

Looking at the documentation there is something mentioned about this limitation though:

```
Currently, JaTyC has some limitations that we are working on:

Anytime methods cannot write to fields.
```

#### Extra notes

- An interesting next example would be to break the protocol with this.

### state_equals_typestate_2

This example can be found [here](https://github.com/Mlako26/jatyc-analysis/tree/main/tests/state_equals_typestate_2).

#### Aim

Same as previous example but showing how we can abuse this gap to cause a protocol fault.

#### Implementation

Have an Iterator, `MoveToEndIterator`, which has the same protocol as a regular iterator but adds an *anytime* method `moveToEnd()` which sets the index of the iterator to the end of the collection. It will have as an internal collaborators an array and an integer index.

Have the client code utilize this `moveToEnd()` method within an if conditional of the `hasNext()` method. This should tell Jatyc that we are in a state to call the `next()` method. Instead, before doing that, call the `moveToEnd()` method.

#### Expectations

Jatyc will not raise any errors and running the code ends up in an exception.

#### Results

Compiling the test and running it indeed ends up in a `IndexOutOfBoundsException`.

```
C:\Users\mlako\OneDrive\Desktop\Stuff\Facultad\tesis\jatyc-analysis\tests\state_equals_typestate_2>java ClientCode                                                        
Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: Index 2 out of bounds for length 2
        at MoveToEndIterator.next(MoveToEndIterator.java:17)
        at ClientCode.main(ClientCode.java:9)
```

This is the client code ran:

```java
    public static void main(String[] args) {
		String[] array = {"hello", "world"};
        MoveToEndIterator it = new MoveToEndIterator(array);
        if (it.hasNext()) {
			it.moveToEnd();
			it.next();
		}
	}
```

#### Conclusion

This is clearly still not a feature supported by the tool.

#### Extra notes

### public_stack

This example can be found [here](https://github.com/Mlako26/jatyc-analysis/tree/main/tests/public_stack).

#### Aim

With the goal of testing the flexibility of the tool, this test aims to create a class with a public internal collaborator with importance to the typestate of an instance. For example, the index of an interator. Then, we want to see what would happen were the client code to create an instance of this class and then read/modify its internal collaborator.

Would Jatyc allow any of these operations? In the case that it doesn't, then it would represent another limitation for the programmer using this tool. If it does, what would happen to the typestate of the class? Would Jatyc be able to recognize it the change?

#### Implementation

Have a `Stack` class and protocols similar to the other two done before. Then, have its `size` internal collaborator be of public visibility.

Have client code create a new instance of this class. Then, change the `size` variable to a very big number. Finally, push elements until the stack potentially runs out of space.

#### Expectations

Jatyc will not allow the client code to modify the internal state of the Stack.

#### Results

With the following client code Jatyc returned the following error:

```java
	public static void main(String args[]) throws Exception {
		Stack stack = new Stack(1);
		stack.size = 2; 
		while (!stack.isFull()) {
			stack.push(2);
		}
	}
```

```
Main.java:4: error: Cannot access [stack.size]
                stack.size = 2;
                     ^
1 error
```

#### Conclusion

Indeed Jatyc does not let client code make use of public collaborators. This makes certain sense though, helps keep object's use under control and makes code safer.

#### Extra notes

### collaborator_compound_typestate_2

This example can be found [here](https://github.com/Mlako26/jatyc-analysis/tree/main/tests/collaborator_compound_typestate_2).

#### Aim

Following up on the `collaborator_compound_typestate` test we performed earlier, results showed how Jatyc was able to recognize when a move was being performed that the robot was at the top-left corner in its initial state. We know this since the two method calls that threw a compilation error were `moveLeft()` and `moveRight()`.

```
RobotController.java:27: error: Cannot call [moveLeft] on State{Robot, TopLeft}
                                this.robot.moveLeft();
                                                   ^
RobotController.java:21: error: Cannot call [moveUp] on State{Robot, TopLeft}
                                this.robot.moveUp();
                                                 ^
2 errors
```

We wonder what would happen if instead the controller had a method `run()` which would simply move the robot around the map, in a clock-wise direction. Would Jatyc allow it, despite moving the robot without checking its state first with some sort of conditional method? What would happen if we ran it twice?

#### Implementation

Same as for the same example but with a method `run()` which first moves the robot right, then down, then left, and finally up. You can only call method `run()` if `canMoveRobot()` returned `true`.

#### Expectations

Jatyc should allow it, since after each method call the robot's state changes to one where the following method call does not break the protocol.

On the other hand, running it twice should not make a difference to jatyc, since it does not check implementations. That is, jatyc makes sure client code respects the protocol of the objects it is using, and calling the `RobotController` twice following the protocol should not raise any alarms. In other words, if the first call to the method `run()` did not raise any errors, then calling it twice shouldn't either.

#### Results

Using the following `run()` implementation and calling it only once we got **no errors**:

```java
	public void run() {
		this.robot.moveRight();
		this.robot.moveDown();
		this.robot.moveLeft();
		this.robot.moveUp();
	}
```

Calling it twice from the client code also does not raise any errors.

#### Conclusion

Seems like as long as you follow the protocol from typestate to typestate you don't need any conditionals to check the current typestate.

#### Extra notes

### collaborator_compound_typestate_3

This example can be found [here](https://github.com/Mlako26/jatyc-analysis/tree/main/tests/collaborator_compound_typestate_3).

#### Aim

Going even further with the `Robot` and `RobotController` example, we would like to now instead of having a `run()` method, what would happen if we had a `next()` method, which if called 4 times in a row it would replicate the behavior of `run()`. That is, it moves the robot one position clockwise. We also want to have a `reset()` method, which tells the controller to start the sequence from the beginning.

Does Jatyc track the typestate of internal collaborators between method calls of its parent object? Or can we somehow make an implementation of the `RobotController` and client code that will cause the robot to fall of the map?

#### Implementation

Have a new internal collaborator variable called position, which tells the controller which position de robot is in. Have a new `next()` method which moves the robot to the next position in the map clockwise. Finally, have a new `reset()` method which will reset the position variable to the top-left corner of the map (starting position).

The protocol for the robot and implementations should be the same as for the previous tests, and the protocol for the controller should be similar (client code must check that the robot can be moved before moving it).

Client code will call `next()` two times, then `reset()` and finally one more `next()` call. This should cause the controller to send the robot right from the bottom-right corner and fall of the map.

#### Expectations

I don't even think I have to test this. In order to implement the `next()` method, I have use a switch statement or many ifs to know where to move the robot to. Thus, the result of the first test will repeat, where we did a similar implementation for the `move()` method.

#### Implementation 2

Have the method `run()` but **also** a method `moveRight()`, which simply moves the robot to the right. This move should be allowed if the robot is in the starting position.

Then have the client code call `run()`, which will make the robot loop clockwise around the map and go back to the top-left starting position, `moveRight()`, causing the robot to be in the top-right position, and another `run()` call. Since the first movement of `run()` is also to the right, it should move the robot out of the map.

#### Expectations

Jatyc will not recognize this and the robot will throw an exception when it falls of the map. I believe Jatyc always assumes the internal collaborator is in the initial state.

#### Results

With this client code:

```java
public static void main(String args[]) throws Exception {
		RobotController controller = new RobotController();

		if (controller.canMoveRobot()) {
			controller.run();
		}

		if (controller.canMoveRobot()) {
			controller.moveRight();
		}

		if (controller.canMoveRobot()) {
			controller.run();
    }
}
```

and this controller implementations:

```java
public void run() {
		this.robot.moveRight();
		this.robot.moveDown();
		this.robot.moveLeft();
		this.robot.moveUp();
}

public void moveRight() {
		this.robot.moveRight();
}
```

and protocol:

```
typestate RobotControllerProtocol {
  CanMove = {
    boolean canMoveRobot(): <true: Move, false: CanMove>,
    drop: end
  }
  Move = {
    void run(): CanMove,
    void moveRight(): CanMove
  }
}
```

we got the following errors:

```
RobotController.java:23: error: Cannot call [moveRight] on State{Robot, TopLeft} | State{Robot, TopRight}
                this.robot.moveRight();
                                    ^
RobotController.java:16: error: Cannot call [moveRight] on State{Robot, TopLeft} | State{Robot, TopRight}
                this.robot.moveRight();
                                    ^
2 errors
```

Seems like it recognizes, for some reason, that the robot could be in the top-right corner and thus you cannot move right initially without "checking".

Experimenting a bit more with it, I made the client code simply call `run()` once, and thus making the robot not fall off the map. I got the same errors.

I will now create one move method for each direction to see if we get even more errors. Interestingly, when adding a new `moveDown()` method to the controller, we get a new error for that direction:

```java
public void run() {
		this.robot.moveRight();
		this.robot.moveDown();
		this.robot.moveLeft();
		this.robot.moveUp();
}

public void moveRight() {
		this.robot.moveRight();
}

public void moveDown() {
		this.robot.moveDown();
}
```

```
RobotController.java:16: error: Cannot call [moveRight] on State{Robot, TopLeft} | State{Robot, TopRight}
                this.robot.moveRight();
                                    ^
RobotController.java:27: error: Cannot call [moveDown] on Shared{Robot}
                this.robot.moveDown();
                                   ^
RobotController.java:23: error: Cannot call [moveRight] on State{Robot, TopLeft} | State{Robot, TopRight}
                this.robot.moveRight();
                                    ^
3 errors
```

But now the new error states that the robot is a shared variable somehow. Writing all of the methods gives us:

```
RobotController.java:35: error: Cannot call [moveUp] on Shared{Robot}
                this.robot.moveUp();
                                 ^
RobotController.java:31: error: Cannot call [moveLeft] on Shared{Robot}
                this.robot.moveLeft();
                                   ^
RobotController.java:23: error: Cannot call [moveRight] on State{Robot, TopLeft} | State{Robot, TopRight}
                this.robot.moveRight();
                                    ^
RobotController.java:27: error: Cannot call [moveDown] on Shared{Robot}
                this.robot.moveDown();
                                   ^
RobotController.java:16: error: Cannot call [moveRight] on State{Robot, TopLeft} | State{Robot, TopRight}
                this.robot.moveRight();
                                    ^
5 errors
```

In all methods except for the `moveRight()`, we get an error due to calling a protocol method from a shared reference (which can only use anytime methods).

If we now remove the `moveRight()` methods but leave the other three, we get a new error which tells me a lot about what was happening with the shared variable:

```
RobotController.java:4: error: Method [moveRight] is required by the typestate but not implemented
public class RobotController {
       ^
RobotController.java:23: error: Cannot call [moveDown] on Shared{Robot}
                this.robot.moveDown();
                                   ^
RobotController.java:27: error: Cannot call [moveLeft] on Shared{Robot}
                this.robot.moveLeft();
                                   ^
RobotController.java:31: error: Cannot call [moveUp] on Shared{Robot}
                this.robot.moveUp();
                                 ^
4 errors
```

This new `moveRight method was not implemented` reminded me that I didn't include the other movement methods to the protocol of the controller. After adding them to it, and re-implementing the missing method, we get:

```
RobotController.java:17: error: Cannot call [moveDown] on State{Robot, BotRight} | State{Robot, TopRight}
                this.robot.moveDown();
                                   ^
RobotController.java:16: error: Cannot call [moveRight] on State{Robot, BotLeft} | State{Robot, BotRight} | State{Robot, TopLeft} | State{Robot, TopRight}
                this.robot.moveRight();
                                    ^
RobotController.java:27: error: Cannot call [moveDown] on State{Robot, BotLeft} | State{Robot, BotRight} | State{Robot, TopLeft} | State{Robot, TopRight}
                this.robot.moveDown();
                                   ^
RobotController.java:23: error: Cannot call [moveRight] on State{Robot, BotLeft} | State{Robot, BotRight} | State{Robot, TopLeft} | State{Robot, TopRight}
                this.robot.moveRight();
                                    ^
RobotController.java:35: error: Cannot call [moveUp] on State{Robot, BotLeft} | State{Robot, BotRight} | State{Robot, TopLeft} | State{Robot, TopRight}
                this.robot.moveUp();
                                 ^
RobotController.java:31: error: Cannot call [moveLeft] on State{Robot, BotLeft} | State{Robot, BotRight} | State{Robot, TopLeft} | State{Robot, TopRight}
                this.robot.moveLeft();
                                   ^
6 errors
```

Now it seems like the tool recognizes that the robot could be anywhere, and thus we cannot simply call the methods as they are. That is very interesting and would love to investigate more how it does that. It clearly seems like it reads which methods could be called from the controller class, and maybe tests different method calls with them? 

I also am not sure why it only complained about moving the robot right and down within the `run()` method (lines 16 and 17), but not going left and up.

#### Conclusion

I feel like I really want to know the algorithm that this tool follows to ensure an object's protocol is being followed. I assume it checks the final state that the internal collaborator would be left at after each method call and colors the typestate graph but not sure.

#### Extra notes

### subtyping_iterators

This example can be found [here](https://github.com/Mlako26/jatyc-analysis/tree/main/tests/subtyping_iterators).

#### Aim

We saw how a protocol can subtype another as long as it allows **more** method sequences, but never less. We want to see what would happen if we were to implement a protocol that went from typestate A to B to A and so on, and a subprotocol that went from A to B to C to A and so on, both on a loop. Would that make Jatyc raise an error?

```mermaid
graph LR
    start(( )) --> q0((q0))
    q0 -->|a| q1((q1))
    q1 -->|b| q0
```

```mermaid
graph LR
    start(( )) --> q0((q0))
    q0 -->|a| q1((q1))
    q1 -->|c| q2((q2))
    q2 -->|b| q0
```

Would these configurations work too?

```mermaid
graph LR
    start(( )) --> q0((q0))
    q0 -->|a| q1((q1))
    q1 -->|b| q2((q2))
    q2 -->|c| q0
```

```mermaid
graph LR
    start(( )) --> q0((q0))
    q0 -->|a| q1((q1))
    q1 -->|c| q2((q2))
    q1 -->|b| q0
    q2 -->|b| q0
```

Also, what would happen if there was another subprotocol that after calling A, B and C are available, 

#### Implementation

Have a `BaseIterator` class like the previous ones, with a `hasNext()` and `next()` methods. Its protocol will be the usual:

```
typestate BaseIterator {
  HasNext = {
    boolean hasNext(): <true: Next, false: end>
  }
  Next = {
    int next(): HasNext
  }
}
```

Have a `SuperSafeIterator` class that has an additional method, `iAmSure()`, which is to be called after `hasNext()` and before `next()`. One should only be able to call `next()` if first `hasNext()` returned true and then `iAmSure()` does so too. The protocol for this class should be the following:

```
typestate SuperSafeIterator {
  HasNext = {
    boolean hasNext(): <true: Verify, false: end>
  }
  Verify = {
    boolean iAmSure(): <true: Next, false: end>
  }
  Next = {
    int next(): HasNext
  }
}
```

Have a `TiredIterator` class that has an additional method, `rest()`, which is to be called after `next()` and before `hasNext()` can be called again. The protocol for this class should be the following:

```
typestate TiredIterator {
  HasNext = {
    boolean hasNext(): <true: Verify, false: end>
  }
  Next = {
    int next(): HasNext
  }
  Rest = {
    void rest(): HasNext
  }
}
```

Have a `SafeIterator` class that has an additional method, `iAmSure()`, which can only be called after `hasNext()` and before `next()`. It allows users to call `next()` right after `hasNext()` though. The protocol for this class should be the following:

```
typestate SafeIterator {
  HasNext = {
    boolean hasNext(): <true: Verify, false: end>
  }
  Verify = {
    boolean iAmSure(): <true: Next, false: end>,
    int next(): HasNext
  }
  Next = {
    int next(): HasNext
  }
}
```

#### Expectations

I believe it should raise an error for both the `SuperSafeIterator` and `TiredIterator`. Lets suppose we have a factory that returns implementations of the `BaseIterator`. The consumer of this iterator would expect to be able to call `next()` staight away after calling `hasNext()` and viceversa, but if any of both of these were to be called this would not be possible due to its protocol.

#### Results

Indeed it raised an error as expected:

```
SuperSafeIterator.java:6: error: [next] transition(s) in [Next] of BaseIterator.protocol are not included in [Verify] of SuperSafeIterator.protocol
public class SuperSafeIterator extends BaseIterator {
       ^
TiredIterator.java:6: error: [hasNext] transition(s) in [HasNext] of BaseIterator.protocol are not included in [Rest] of TiredIterator.protocol
public class TiredIterator extends BaseIterator {
       ^
2 errors
```

It complains that the method `next()` is not included in the typestate `Verify` for the protocol of the `SuperSafeIterator`. At the same time, the transition for method `hasNext()` does not exist within the `Rest` typestate of the protocol for `TiredIterator`.

Note how it did not raise any errors for the `SafeIterator`, where all original `BaseIterator`'s protocol transitions were present.

#### Conclusion

All available transitions in the supertype must be also available in the subtype's protocol for it to work.

#### Extra notes

### overloaded_stack_1

This example can be found [here](https://github.com/Mlako26/jatyc-analysis/tree/main/tests/overloaded_stack_1).

#### Aim

We would like to know if Jatyc supports declaring overloaded methods within its protocols. That is, what would happen for example if two methods with the same name but different input arguments were to be mentioned in the protocol? Would Jatyc complain?

#### Implementation

Have a `Stack` class and protocols similar to the `normal_stack` implementation, but have two `push()` methods: `push(int e)` and `push(float e)`. Include both of these methods within its protocol like this:

```
typestate StackProtocol {
  Init = {
    boolean isEmpty(): <true: Init, false: CanPop>,
    boolean isFull(): <true: Init, false: CanPush>,
    drop: end
  }
  CanPush = {
    boolean isEmpty(): <true: Init, false: CanPop>,
    boolean isFull(): <true: Init, false: CanPush>,
    void push(int): Init, <----- HERE
    void push(float): Init, <--- HERE
    drop: end
  }
  CanPop = {
    boolean isFull(): <true: Init, false: CanPush>,
    boolean isEmpty(): <true: Init, false: CanPop>,
    int pop(): Init,
    drop: end
  }
}
```

#### Expectations

Jatyc will complain since we are declaring the method twice.

#### Results

Compiling this example gives no errors whatsoever

#### Conclusion

It would seem like Jatyc does indeed support the usage of method overloading.

#### Extra notes

### overloaded_stack_2

This example can be found [here](https://github.com/Mlako26/jatyc-analysis/tree/main/tests/overloaded_stack_2).

#### Aim

Despite the previous example, we would like to know what would happen if the overloaded methods were only usable in different typestates, would Jatyc be able to differenciate between them properly?

#### Implementation

Have a `Stack` class similar to the previous implementation, but add methods `canPushInt()` and `canPushFloat()`, with the following protocol:

```
typestate StackProtocol {
  Init = {
    boolean canPushInt(): <true: PushInt, false: Init>,
    boolean canPushFloat(): <true: PushFloat, false: Init>,
    drop: end
  }
  PushInt = {
    void push(int): Init
  }
  PushFloat = {
    void push(float): Init
  }
}
```

The client code will first use both push operations correctly, following the protocol by using their correspondent boolean control method, and then switch them around:

```java
      if (stack.canPushFloat()) {
        stack.push((float) 3.4);
      }

      if (stack.canPushInt()) {
        stack.push(3);
      }

      if (stack.canPushInt()) {
        stack.push((float) 3.4);
      }

      if (stack.canPushFloat()) {
        stack.push(3);
      }
```

#### Expectations

If Jatyc indeed supports method overloading, it should raise an error with the final two calls to push.

#### Results

When compiling, Jatyc throws these two errors for both the incorrect calls to push.
```
Main.java:13: error: Cannot call [push] on State{Stack, PushInt}
        stack.push((float) 3.4);
                  ^
Main.java:17: error: Cannot call [push] on State{Stack, PushFloat}
        stack.push(3);
                  ^
2 errors
```

#### Conclusion

This example confirms even more that Jatyc does indeed process both of these methods as different ones despite having the same name. There are many more variables that could be tested for, such as overloading return types, or having overloaded methods lead to different protocol typestates, but I don't think it is necessary for now.

#### Extra notes

### parameter_ensures

This example can be found [here](https://github.com/Mlako26/jatyc-analysis/tree/main/tests/parameter_ensures).

#### Aim

Following the analysis of the master thesis which the tool is based on, we noticed some discrepancies between the initial version and the current one. In particular, the initial version allowed method parameters to be noted with the `@Ensures` annotation, making sure that the parameters are then left in a specific state. Since this version does not seem to allow for it according to the documentation, we wanted to see how Jatyc actually reacts to using the annotation on parameters.

Also, we want to know what type does Jatyc currently assign to variables which were passed as arguments after the method call ends.

#### Implementation

Have the same stack implementation and protocol as the `normal_stack` example. Then, have a new method in main that gets a stack in its inital position and pushes an element if it can:

```java
    public static void pushToStack(@Requires("Init") Stack stack, int e) {
      if (!stack.isFull()) {
        stack.push(e);
      }
    }
```

Then, from main, call this method and then attempt to use the stack.

```java
    public static void main(String args[]) throws Exception {
		  Stack stack = new Stack(5);
      pushToStack(stack, 2);
      if (!stack.isFull()) {
        stack.push(3);
      }
    }
```

#### Expectations

Jatyc will turn the stack variable into a shared reference and not allow us to use it.

#### Results

When compiling, Jatyc returns the following error:

```
Main.java:7: error: Cannot call [isFull] on Shared{Stack}
      if (!stack.isFull()) {
                       ^
1 error
```

#### Conclusion

It seems like Jatyc turns the variable into a shared reference after the method call. Checking the [linearity section](https://github.com/jdmota/java-typestate-checker/wiki/Documentation#linearity) of the documentation, it seems like if we pass control to another variable of the object (such as using a variable as a paramter), then it instantaneously becomes shared and cannot be used to modify the state of the object anymore.

#### Extra notes

### parameter_ensures_2

This example can be found [here](https://github.com/Mlako26/jatyc-analysis/tree/main/tests/parameter_ensures_2).

#### Aim

In this example, we are going to try to return the parameter reference to the original variable that held it using the `@Ensures` annotation.

#### Implementation

Use the same implementation as before but change the method in main to use the `@Ensures`:

```java
    public static void pushToStack(@Requires("Init") @Ensures("Init") Stack stack, int e) {
      if (!stack.isFull()) {
        stack.push(e);
      }
    }
```

#### Expectations

Since the documentation does not mention it, and specifically says it is for return type values, it should raise an error.

#### Results

When compiling, Jatyc returns the following error:

```
Main.java:13: error: Parameters with @Ensures should be final
    public static void pushToStack(@Requires("Init") @Ensures("Init") Stack stack, int e) {
                                                     ^
1 error
```

Seems like it requires for the variable to be final (that is, we cannot change the object the parameter stack is pointing to). After adding the final keyword, we get no errors!

#### Conclusion

If setting the parameter as final, and then using the `@Ensures` annotation, then it seems like the control returns to the original variable.

### parameter_ensures_3

This example can be found [here](https://github.com/Mlako26/jatyc-analysis/tree/main/tests/parameter_ensures_3).

#### Aim

In this example, we want to follow-up previous experiments. We realized that one thing we haven't touched on was protocol completion when passing linear references as method arguments. Does Jatyc properly let the user know that the reference will be lost?

Remember how in the first experiment the reference was lost when the method code finished. Since the `@Ensures` annotation is not referenced in the documentation, users would've never known how to overcome this without perhaps doing unintended fixes (such as adding droppable states or finishing the protocol within the method's body).

#### Implementation

Use both implementations as before but change the stack code and protocol. In particular, make the stack only complete its protocol after calling method `close()`.

```
typestate StackProtocol {
  Init = {
    boolean isEmpty(): <true: Init, false: CanPop>,
    boolean isFull(): <true: Init, false: CanPush>,
    void close(): end
  }
  CanPush = {
    boolean isEmpty(): <true: Init, false: CanPop>,
    boolean isFull(): <true: Init, false: CanPush>,
    void push(int): Init,
    void close(): end
  }
  CanPop = {
    boolean isFull(): <true: Init, false: CanPush>,
    boolean isEmpty(): <true: Init, false: CanPop>,
    int pop(): Init,
    void close(): end
  }
}
```

We will test with both client codes from previous tests.

#### Expectations

Jatyc will properly recognize that if the linear reference is not returned with an `@Ensures` notation, the parameter will lose its only linear reference and thus the protocol will never be completed.

#### Results

With the following client code, we get absolutely no errors from Jatyc when compiling:

```java
public static void main(String args[]) throws Exception {
  Stack stack = new Stack(5);
  pushToStack(stack, 2);
  if (!stack.isFull()) {
    stack.push(3);
  }
  stack.close();
}

public static void pushToStack(@Requires("Init") @Ensures("Init") final Stack stack, int e) {
  if (!stack.isFull()) {
    stack.push(e);
  }
}
```

This is because the linear reference is properly being returned to the stack variable after the static method call, and then further on the `close()` method is called to finish its protocol.

When using the other client's code, we get:

```
Main.java:20: error: [stack] did not complete its protocol (found: State{Stack, Init})
    public static void pushToStack(@Requires("Init") Stack stack, int e) {
                       ^
Main.java:8: error: Cannot call [isFull] on Shared{Stack}
      if (!stack.isFull()) {
                       ^
2 errors
```

Realize how the second error is the same as the first experiment, since in main we are trying to continue using the stack variable as a linear reference when it actually is shared. The first error acknowledges how, since the static method is not returning the linear reference back, one must finish the object's protocol before finishing.

#### Conclusion

Jatyc properly checks each method to see if any linear reference is lost when it finishes. If that is the case, then the method must first take that object being referenced to protocol completion.

### collaborator_compound_state_4

This example can be found [here](https://github.com/Mlako26/jatyc-analysis/tree/main/tests/collaborator_compound_state_4).

#### Aim

In this example, we would like to know how Jatyc handles protocol completion when a protocoled object is an internal collaborator another protocoled one. Specifically, we would like to know what would happen in the scenario where the parent object finishes its protocol before its child object does. Would jatyc let us know that we can have not finished the protocol? Also, can we continue using the robot, or will we simply lose the reference?

#### Implementation

Use the same `RobotController` and `Robot` implementation as before. This time though, the only way of finishing the protocol for the robot is to call the method `rest()`. On the other hand, the controller also can only finish its protocol by calling method `turnOff()`. Also, add a `Robot` anytime method (outside of the protocol) called `dance()` which does nothing.

We will have two client codes for this. First, an initial method where we initialize a Robot object, then a controller and pass the robot object as a parameter to the constructor. It will then attempt to turn off the controller before turning off the robot. After that, it will attempt to first call `dance()` and then an alliased method for moving the robot. A second client code will do the same but initialize the robot directly as the argument to the constructor of the controller.

#### Expectations

For both of them, it will simply let us know that we are not finishing the robots protocol by the end of the client code's methods. Also, attempting to use the robot after facto, in the the first client code, will simply let us know that it is a shared reference, and thus we cannot use it anymore (for alliased methods at least).

#### Results

The first client code ended up looking like this:

```java
public static void main(String args[]) throws Exception {
		Robot robot = new Robot();
		RobotController controller = new RobotController(robot);

		if (controller.canMoveRobot()) {
			controller.run();
		}

		controller.turnOff();

		robot.dance();
		robot.moveLeft();
}
```

Running Jatyc on the code returns the following errors:

```
RobotController.java:5: error: [this.robot] did not complete its protocol (found: State{Robot, TopLeft})
public class RobotController {
       ^
Main.java:13: error: Cannot call [moveLeft] on Shared{Robot}
                robot.moveLeft();
                              ^
2 errors
```

This basically says that the robot was not able to complete the protocol, and then that we cannot call method `moveLeft()` from main after passing the robot to the controller's constructor. Notice though how the protocol completion error is mentioned within the `RobotController` class and not on main.

The second client code ended up looking like this, and we got a very similar error:

```java
public static void main(String args[]) throws Exception {
		RobotController controller = new RobotController(new Robot());

		controller.turnOff();
}
```

```
RobotController.java:5: error: [this.robot] did not complete its protocol (found: State{Robot, TopLeft})
public class RobotController {
       ^
1 error
```
#### Conclusion

The tool was able to properly recognize that the internal collaborator's protocol was not completed.

### collaborator_compound_state_5

This example can be found [here](https://github.com/Mlako26/jatyc-analysis/tree/main/tests/collaborator_compound_state_5).

#### Aim

Following from the previous example, we would like to know what happens when we finish the protocol of the internal collaborator from the controller as well. What would happen with the methods that actually move the robot and thus modify its state?

#### Implementation

Use the same `RobotController` and `Robot` implementation as before.Keep the `rest()` method in the robot that finishes its protocol.

Then, have both a new protocoled and anytime method for the robotController, lets say `anytimeEnd()` and `alliasedEnd()` methods, that both perform the same action of calling the robots `rest()` method and thus finishing its protocol.

We will have two client codes here, one which call each end methods.

#### Expectations

Since after both method calls in both client codes the robot's state will be at `end`, that will be used as input for all the other methods again in the class analysis. This will make it raise errors for every method in the controller, since attempting to call any type of alliased method on the robot at state `end` should not be possible.

#### Results

The first client code for the anytimeEnd ended up looking like this. Remember that this method is called anytimeEnd since it is **not** included in the protocol of the controller, and thus any reference to the controller would be able to modify its "internal state" (or, better said, its robot's).

```java
public static void main(String args[]) throws Exception {
		RobotController controller = new RobotController(new Robot());

		controller.anytimeEnd();
}
```
```
RobotController.java:28: error: Cannot call [rest] on State{Robot, TopLeft} | State{Robot, end}
                this.robot.rest();
                               ^
RobotController.java:17: error: Cannot call [moveRight] on State{Robot, TopLeft} | State{Robot, end}
                this.robot.moveRight();
                                    ^
RobotController.java:5: error: [this.robot] did not complete its protocol (found: State{Robot, TopLeft} | State{Robot, end})
public class RobotController {
       ^
RobotController.java:32: error: Cannot call [rest] on State{Robot, TopLeft} | State{Robot, end}
                this.robot.rest();
                               ^
4 errors
```

We get four interesting errros. Both the first and last one, mentioning errors while calling the `rest()` method, mention that we cannot call that method since the robot would potentially already be in the end state. These correspond to both end methods in the controller. The second error is the same, but for the `moveRight()` robot's method being called from the controller. Remember that the controller's implementation looks like this:

```java
	public void run() {
		this.robot.moveRight();
		this.robot.moveDown();
		this.robot.moveLeft();
		this.robot.moveUp();
	}

	public void turnOff() {
		return;
	}

	public void anytimeEnd() {
		this.robot.rest();
	}

	public void alliasingEnd() {
		this.robot.rest();
	}
```

Finally, the third error is very interesting, where it states that the robot might have potentially not finished its protocol.

```
[this.robot] did not complete its protocol (found: State{Robot, TopLeft} | State{Robot, end})
```

This is because, were we not to call any of the end related methods in the controller, the robot would effectively not complete its protocol.

For the second client method, we get the exact same errors.

```java
public static void main(String args[]) throws Exception {
		RobotController controller = new RobotController(new Robot());

		controller.alliasingEnd();
}
```

#### Conclusion

que pasaria si tuviesemos un controller simple, que tenga un metodo qeu solo pueda terminar el protocol del robot una sola vez, o que por ejemplo se termine el protocolo del robot dentro del constructor del objeto.

### collaborator_compound_state_6

This example can be found [here](https://github.com/Mlako26/jatyc-analysis/tree/main/tests/collaborator_compound_state_6).

#### Aim

What would happen if we allowed to share internal collaborators that are protocoled? Would Jatyc allow us to have getters for internal collaborators?

If so, would its internal collaborator reference become shared potentially?

#### Implementation

Use the same `RobotController` and `Robot` implementation as before. Keep the `rest()` method in the robot that finishes its protocol.

Then, have a new method in the controller which returns the robot in its inital state:

```java
public @Ensures("TopLeft") Robot getRobot() {
		return this.robot;
}
```

Finally, have a client two client codes. They will both create the controller with its robot and then ask for the robot. The first client code will only attempt to move the robot to the right. The second one will straight up finish the protocol by calling the `rest()` method.

#### Expectations

After returning the internal collaborator, the reference within the controller will become shared. This will cause the class analysis of all methods fail, since one cant call alliased methods on a shared reference.

#### Results

The following compilation error arises when using the following client code:

```java
public static void main(String args[]) throws Exception {
		RobotController controller = new RobotController(new Robot());

		Robot robot = controller.getRobot();
		robot.moveRight();
}
```
```
RobotController.java:25: error: Incompatible return value: cannot cast from Shared{Robot} to State{Robot, TopLeft}
                return this.robot;
                ^
1 error
```

It basically says that the internal collaborator is already taken for a shared reference, which is weird. This could be related to the fact that this method in the controller is an anytime method, and thus it might convert it into a shared reference. Using the other client code gives the same error. To see if anything changes, I will add the method to the protocol of the controller.

```
typestate RobotControllerProtocol {
  CanMove = {
    boolean canMoveRobot(): <true: Move, false: CanMove>,
    Robot getRobot(): CanMove,
    drop: end
  }
  Move = {
    void run(): CanMove
  }
}
```

Now we get different errors, which are exactly what we expected in the first place:

```
RobotController.java:18: error: Cannot call [moveRight] on Shared{Robot}
                this.robot.moveRight();
                                    ^
RobotController.java:25: error: Incompatible return value: cannot cast from Shared{Robot} to State{Robot, TopLeft}
                return this.robot;
                ^
2 errors
```

Now we can't call any method from the controller that utilizes the robot, since it might possible be a shared reference. Also notice how we did not get an error from using the robot from the client code after getting it from the object:

```java
public static void main(String args[]) throws Exception {
		RobotController controller = new RobotController(new Robot());

		Robot robot = controller.getRobot();
		robot.rest();
}
```

If we change the protocol a little bit, so that after calling the `getRobot()` method, the controller does not call the `run()` or `getRobot()` methods again, then there are no compilation errors:

```
typestate RobotControllerProtocol {
  CanMove = {
    boolean canMoveRobot(): <true: Move, false: CanMove>,
    Robot getRobot(): CantShare,
    drop: end
  }
  Move = {
    void run(): CanMove
  }
  CantShare = {
    boolean canMoveRobot():  <true: CantShare, false: CantShare>,
    drop: end
  }
}
```

#### Conclusion

Jatyc clearly follows the protocol's method sequences to analyze a class, otherwise the latest example wouldn't have worked. Other than that, clearly one can get possesion of the linear reference of a protocoled object from another class, and that is no problem.

### immutable_list

This example can be found [here](https://github.com/Mlako26/jatyc-analysis/tree/main/tests/immutable_list).

#### Aim

Sometimes we have objects that, depending on the construction, have different methods available to them. Take for example the `List.of("foo")` method. This creates an *immutable list* which, as its name states, cannot be modified with methods such as `add()`. Now, if one were to create a protocol for the `List` interface, how could we represent this?

#### Implementation 

Lets have a `DroppedList` class. Then, to get instances of this class, one can call the public constructor (which returns a reference at the initial typestate) or call a `of(String)` method, which creates a list and then returns it without an `@Ensures` annotation. This should make it so the reference returned by the `of(String)` method is a shared one, and thus one cannot call methods included in the protocol. Then, include the `add(String)` method into the protocol, and check that it cannot be called from the client code if the object is created through the `of(String)` method.

#### Expectations 

Jatyc will recognize that objects created via the static method will be shared, or at the `end` state, and thus the `add(String)` method should not be able to be called.

#### Results 

With the following client code, that is indeed what happens:

```java
public static void main(String[] args) {
		DroppedList list = DroppedList.of("foo");
		DroppedList list2 = new DroppedList("foo");
		String s = list.get();
		list.add("bar");
		s = list2.get();
		list2.add("bar");
}
```
```
ClientCode.java:8: error: Cannot call [add] on Shared{DroppedList}
                list.add("bar");
                        ^
1 error
```

#### Implementation 2

Have two protocols in one, one for the mutable and another one for the immutable methods. Then, at construction time with the `of(String)` method, move the object into the immutable part of the protocol. 

```
typestate ImmutableList {
  Mutable = {
    void add(String): Mutable,
    String get(): Mutable,
    void immutable(): Immutable,
    drop: end
  }
  Immutable = {
    String get(): Immutable,
    drop: end
  }
}
```

```java
public @Ensures("Immutable")static ImmutableList of(String s) {
   ImmutableList list= new ImmutableList(s);
    list.immutable();
    return list;
}

private void immutable() {
    return;
}
```
 
#### Results 2

Running this configuration yields the following errors:

```
ImmutableList.java:5: error: Method [immutable] is required by the typestate but not implemented
public class ImmutableList {
       ^
ClientCode.java:16: error: Cannot call [get] on Shared{ImmutableList}
                String s = list.get();
                                   ^
ClientCode.java:18: error: Cannot call [get] on State{ImmutableList, Mutable}
                s = list2.get();
                             ^
3 errors
```

Looking at the first error, one can see that it mentions that the `immutable()` is required by the typestate but it is not implemented. I assumed it was due to it being private, so I changed the method's visibility to public. Now we get the desired outcome:

```
ClientCode.java:17: error: Cannot call [add] on State{ImmutableList, Immutable}
                list.add("bar");
                        ^
1 error
```

#### Conclusion

One way to represent immutable objects is to have all methods that mutate it into the protocol, and then return an object that's already ended the protocol. Another one is to have another method which moves the object into an immutable state, although this isn't as clean as this method must show in the interface of the object and it might be senseless.

### subtyping_with_droppables

This example can be found [here](https://github.com/Mlako26/jatyc-analysis/tree/main/tests/subtyping_with_droppables).

#### Aim

On the aspect of subtyping, Jatyc makes sure that when both parent and child types are suscribed to a protocol, the subtype's protocol includes the supertype's.

We wonder if this assertion it makes includes all aspects of protocols, including droppable states. If the supertype can be droped at a particular state, will it make sure the subtype can do the same?

#### Implementation

Have a parent `Iterator` class, with the usual `hasNext()` and `next()` methods, plus the usual protocol. This class will be able to be dropped from both `HasNext` and `Next` typestates:

```
typestate Iterator {
  HasNext = {
    boolean hasNext(): <true: Next, false: end>,
    drop: end
  }
  Next = {
    int next(): HasNext,
    drop: end
  }
}
```

Have a `NeverDroppableIterator` which extends it, which is the same protocol but without the drop transitions. Finally, have another class `SometimesDroppableIterator`, which includes a new method `doNothing()` and new state to call it. Eventhough its supertype class can be dropped at all times, this new class will not be droppable in this new state.

```
typestate NeverDroppableIterator {
  HasNext = {
    boolean hasNext(): <true: Next, false: end>
  }
  Next = {
    int next(): HasNext
  }
}
```

```
typestate SometimesDroppableIterator {
  HasNext = {
    boolean hasNext(): <true: Next, false: end>,
    drop: end
  }
  Next = {
    int next(): DoNothing,
    drop: end
  }
  DoNothing = {
    boolean hasNext(): <true: Next, false: end>,
    void doNothing(): HasNext
  } 
}
```

#### Expectations

Jatyc will recognize these drop statements as transitions in the typestate graph, and thus it should process them just as well as regular transitions from method calls. It will recognize that both subprotocols do not include these transitions.

#### Results

```
NeverDroppableIterator.java:5: error: [drop: end] transition(s) in [HasNext] of Iterator.protocol are not included in [HasNext] of NeverDroppableIterator.protocol
public class NeverDroppableIterator extends Iterator {
       ^
NeverDroppableIterator.java:5: error: [drop: end] transition(s) in [Next] of Iterator.protocol are not included in [Next] of NeverDroppableIterator.protocol
public class NeverDroppableIterator extends Iterator {
       ^
SometimesDroppableIterator.java:5: error: [drop: end] transition(s) in [HasNext] of Iterator.protocol are not included in [DoNothing] of SometimesDroppableIterator.protocol
public class SometimesDroppableIterator extends Iterator {
```

#### Conclusions

Jatyc does indeed consider drop: end transitions the same as regular method transitions, and thus cannot be ommited in subprotocols.

### constructor_protocol

This example can be found [here](https://github.com/Mlako26/jatyc-analysis/tree/main/tests/constructor_protocol).

#### Aim

Even though we are pretty sure it does not, we want to know if constructor methods can be added to protocols or not.

#### Implementation

Create an `Iterator` class just like the previous ones, but include in the protocol its constructor method.

#### Expectations

Jatyc will not allow it since it is not a public method from an instance of the class, but rather a static method from the class.

#### Results

Trying to create the protocol with the following implementation raises the following error:

```
typestate Iterator {
  Initialize = {
    Iterator Iterator(int[]): HasNext
  }
  HasNext = {
    boolean hasNext(): <true: Next, false: end>,
    drop: end
  }
  Next = {
    int next(): HasNext,
    drop: end
  }
}
```
```
Iterator.java:5: error: Method [Iterator] is required by the typestate but not implemented
public class Iterator {
       ^
1 error
```

So basically, it is claiming that it is expecting an instance method, not the actual constructor method, to be implemented in the class. Trying to write the protocol in a different way just breaks the syntax:

```
typestate Iterator {
  Initialize = {
    Iterator(int[]): HasNext
  }
  HasNext = {
    boolean hasNext(): <true: Next, false: end>,
    drop: end
  }
  Next = {
    int next(): HasNext,
    drop: end
  }
}
```

```
Iterator.protocol:3: error: (mismatched input '(' expecting ID)
    Iterator(int[]): HasNext
            ^
1 error
```

#### Conclusions

Jatyc does not support adding constructor methods to protocols.
