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

#### Aim

We want to see how much does Jatyc take into account the state of internal collaborators of an object following a protocol. In particular, jatyc seems to interpret a typestate as a set of enabled methods and their destination typestates. But could the typestate of an object be dictated also by the one of an internal collaborator? That is, could the typestate of the parent object be a compound of both typestates, where calls to the same method from the same parent typestate yield different results (such as an exception for example)?

#### Implementation

Create a Robot and RobotController class. The map that the robot will traverse is a 2x2 map and, starting out in the top left corner (coordinates (1,2)), it will have methods to move a single unit at a time in any cardinal direction. It will have a typestate for each position in the map, dictating which methods it can call so that it does not fall off the map.

The controller simply takes user input and then moves the robot. Its methods will be two simple ones with a boolean returning method `canMoveRobot()`, which "checks its connection to the robot", and then `move(direction: string)` which moves the robot on the corresponding direction. The protocol will consist of only two typestates, one for each method. 

There will be client code with instructions that will make the robot fail its protocol, but not the controller.

#### Expectations

For this example in particular, Jatyc should be able to tell from the client code which will explicitly show the movements the robot will make that it will break its protocol.

#### Results

#### Conclusion

#### Extra notes

- Is an object able to read the current typestate of an internal collaborator and make different decisions based off of it? Perhaps land in different typestates?

### normal_stack

#### Aim

Within the Four Dark Horses of Object Protocols paper, there is an example in section 2.1 mentioning the difficulty of protocols to properly analyze a stack state. The paper claims that to 100% do so, one needs a sort of stack automata to count the amount of push and pops.

This example aims to display the philosophy behind the Jatyc tool, which gives a twist on the paper's interpretation of object protocols. A fully functional stack protocol and obeying implementation can be achieved by being stricter on how a programmer can use an instance of the stack.

#### Implementation

Create a simple stack implementation with methods `push()`, `pop()`, `isEmpty()`, `isFull()`.

The protocol will be a simple one, where one can only push if the stack is not full, and pop if it's not empty.

There will be client code attempting to push or pop without first checking if it can.

#### Expectations

Jatyc will be able to tell that the client code is wrongly calling the stack's methods in all scenarios.

#### Results

#### Conclusion

#### Extra notes

- Within this example we can see that Jatyc **DOES NOT** control the actual implementation of the stack, but whether if the client code properly traverses its typestates.

### faulty_stack

#### Aim

Following the previous example, as far as we know Jatyc is unable to tell if an implementation actually does what it is supposed to. That is, it verifies that the usage of an object follows its protocol, but not that an object's methods follow some sort of **specification** (pre-post condition). 

While it registers that client code could be using an object in a way that it breaks its protocol, it does not control whether if a boolean method simply returns a random value instead of what it actually should.

#### Implementation

Create a simple stack implementation with methods `push()`, `pop()`, `isEmpty()`, `isFull()`. Method `isFull()` will always return false.

The protocol will be the same as the previous example.

Finally, the client code will create a new instance of the faulty stack and then push more times than its capacity.

#### Expectations

Jatyc will not be able to tell that the implicit stack's specification is not being followed, and an exception will be thrown when pushing the final element.

#### Results

#### Conclusion

#### Extra notes

- If this test follows our expectations, it means that indeed jatyc, and perhaps protocols in general, are not able to assert that specifications are being followed within an object's methods. In that sense, pre-post conditions are perhaps more expressive and powerful in this area.

### restrictive_line_reader

#### Aim

With this example we aim to show how a protocol could be too restrictive for a particular implementation. This is similar to how a pre-post condition could overspecify a method.

#### Implementation

Have a FDLineReader class that accepts in its constructor a file name or a file descriptor. It will have methods `open()`, `hasNext()`, `next()` and `close()`. The protocol will be a very basic one, following the lines of the other line reader examples. 

If a file descriptor is passed at construction, then the `open()` method can be avoided. 

#### Expectations

Jatyc will not let the client use the `hasNext()`, `next()` and `close()` methods until `open()` is called, even if a FD is properly provided.  

#### Results

#### Conclusion

#### Extra notes

- There should be another example of underspecifying.

### restrictive_iterator

#### Aim

Perhaps a more clear or feasible example of a protocol that is more restrictive than necessary.

#### Implementation

Have an Abstract Class for an Iterator with the usual protocol that is provided by the Jatyc tool. In particular, this protocol only allows you to call the `next()` method if `hasNext()` returns true.

Then create a subclass that implements the methods of the interface, but called the `LoopIterator`. Calling the `next()` method on an instance of this class when the end of the collection has been reached, the iterator will loop back to the beginning of the collection and start again. The `hasNext()` method will work as usual, where it returns true at all stages except when the end of the collection is reached.

We can try this example both with a subprotocol, where `next()` can be called even if `hasNext()` returns false, and without one (`LoopIterator` would follow the abstract class' protocol).

#### Expectations

Jatyc will not let the client use the `next()` method if `hasNext()` returns false. In the case of the `LoopIterator` following a subprotocol, Jatyc might not even allow the protocol to inherit from the parent one.

#### Results

#### Conclusion

#### Extra notes

### lax_iterator

#### Aim

Following the previous examples where the protocol managed to be more restrictive than needed, and sometimes not allowing the objects to perform all of their features, this example will showcase how a protocol badly written might prove to be too lax. That is, having a class that, despite its method calls following a protocol, the execution raises an exception.

#### Implementation

Have an Abstract Class for an Iterator with the usual protocol that is provided by the Jatyc tool. In particular, this protocol only allows you to call the `next()` method if `hasNext()` returns true.

Have a subclass `EvenIntIterator`, which iterates over arrays of integers. In the case that an odd number is encountered within the collection, it will raise an exception in the `next()` method. It will follow the same protocol as the parent class.

#### Expectations

Jatyc will not let the client use the `next()` method if `hasNext()` returns false. In the case of the `LoopIterator` following a subprotocol, Jatyc might not even allow the protocol to inherit from the parent one.

#### Results

#### Conclusion

#### Extra notes

- Obviously this implementation would be anything but production ready. Despite this, I find it interesting how, since Jatyc has absolutely no way of telling what the contents of the collection could be before the `next()` method is called (to disable it), the programmer will have to modify the way he implements in order to fully utilize the Jatyc protocols. That is, perhaps adding a new `nextIsOdd()` method, which can only be called between `hasNext()` and `next()`, to make sure an instance of this class does not raise exceptions.
- Do these examples make any sense? I'm not sure these are the kind of exceptions we want to examplify with (deliberately throwing an exception instead of something actually breaking).

### state_equals_typestate

#### Aim

In section 2.2 of the Four Dark Corners Of Object Protocols, it mentions an example of an implementation of the `Iterator` interface that adds its own reset method, which sends its index back to the beginning of the collection. If following the protocol of the interface, since this method is not mentioned Jatyc will consider it an *anytime* method. Then, calling it should not allow it to modify the state of an instance (the values of its internal collaborators).

But does modifying the state of an instance mean modifying the typestate? Remember what the documentation says about the typestates:

```
A typestate is associated with a Java class with the @Typestate annotation and defines: the object's states, the methods that can be safely called in each state, and the states resulting from the calls
```

I believe that not always modifying the state of an instance makes it necessarily change its typestate. That is pretty easy to see: having internal collaborators that represent a log context for example, or a timestamp for the last operation performed. Changing these would change the state of an object, but not necessarily its typestate. Therefore, were Jatyc to not allow an anytime method to modify any collaborator, we could say it is overly restrictive.

In this example, the collaborator modified will technically be of importance to the protocol, which will be the index of the array being iterater over.

#### Implementation

Have an Iterator interface, with its very simple protocol including typestates for the methods `hasNext()` and `next()`.

Have an implementation of the Iterator interface, `ResettableIterator`, which adds an *anytime* method `reset()` which resets the index of the iterator to the beginning of the collection. It will have as an internal collaborators an array/list and an integer index.

Have the client code utilize this `reset()` method after a few calls of the `hasNext()` and `next()` loop.

#### Expectations

Jatyc will not compile the `reset()` method since it modifies internal collaborators despite being an *anytime* method.

#### Results

#### Conclusion

#### Extra notes

### state_equals_typestate_2

#### Aim

Same as the previous example, but this time instead of modifying an important variable to the protocol, reset one that has no importance whatsoever to it. This will show if Jatyc makes any sort of distinction

#### Implementation

Have an Iterator interface, with its very simple protocol including typestates for the methods `hasNext()` and `next()`.

Have an implementation of the Iterator interface, `CountingIterator`. It will have as an internal collaborators an array/list, an integer index and an integer counter of operations, which goes up for every operation performed. The class also adds an *anytime* method `resetOperationCounter()` which resets the counter.

Have the client code utilize this `resetOperationCounter()` method after a few calls of the `hasNext()` and `next()` loop.

#### Expectations

Jatyc will behave the same as the previous example.

#### Results

#### Conclusion

#### Extra notes

### public_stack

- Hacer un ejemplo de un stack donde su pila es un colaborador interno publico, lo puedo modificar desde afuera y el mismo sabra su estado?

#### Aim

With the goal of testing the flexibility of the tool, this test aims to create a class with a public internal collaborator with importance to the typestate of an instance. For example, the index of an interator. Then, we want to see what would happen were the client code to create an instance of this class and then read/modify its internal collaborator.

Would Jatyc allow any of these operations? In the case that it doesn't, then it would represent another limitation for the programmer using this tool. If it does, what would happen to the typestate of the class? Would Jatyc be able to recognize it the change?

#### Implementation

Have a `Stack` class.

#### Expectations

Jatyc will behave the same as the previous example.

#### Results

#### Conclusion

#### Extra notes