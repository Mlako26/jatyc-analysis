## Adapting Paper's Examples to Jatyc - Jatyc to Examples

### Section 2.1 - Stack Example

We need to be able to know **when** we can call methods `pop()` and `push()`. To do this, the paper expresses the need for an expressive specification language such as a pushdown automata.

In particular, the problem lies in the fact that only accounting for the order of method calls one cannot know the inner state of a stack. Thus, how many times can a stack pop before being empty and push before being full is something a simple regular language can't process.

This is one of the many implementations possible for a stack protocol in jatyc.

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
    void push(int): Init,
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

**Example to Jatyc**:

In order to adapt the example to Jatyc, one can introduce new control methods to know if a call to `pop()` or `push()` would be out of bounds, such as `isEmpty()` and `isFull()`.  A valid protocol that would make any following object not result in a runtime exception would be to only allow pushes and pulls after calling their respective control methods. There is already an example of for this within the repo.

This makes it so clients to instances of this stack would have to check with conditionals before each push and pull, thus adapting their coding style to match the tool.

**Jatyc to Example**: Currently Jatyc keeps no track of an object's state. For example, it has absolutely no way of having a notion of a stack's maximum size or the value of its index variable.

One way to do this would be to add typestates with arguments, which would be sort of equivalent to dynamic typestates. That is, whenever we push the first element, we enter a typestate where the stack's current load is 1, pushing another calls another one with load 2, etc.

We would also need to know the stack's capacity value, so an initial instance-boundaries values must be set within the protocol that could be configured in the constructor of the stack to know which is the maximum number of pushes allowed.

For example, we could modify the previous protocol in the following way with pattern-matching:

```
typestate StackProtocol {
  int size;
  Empty = {
    void push(int): NotEmpty(1),
    drop: end
  }
  NotEmpty(int index) = {
    index == size - 1 = {
        void push(int): Full,
        int pop(): NotEmpty(index - 1),
        drop: end
    }
    index == 1 = {
        void push(int): NotEmpty(index + 1),
        int pop(): Empty,
        drop: end
        }
    default = {
        void push(int): NotEmpty(index + 1),
        int pop(): NotEmpty(index - 1),
        drop: end
    }
  }
  Full = {
    int pop(): NotEmpty(size - 1),
    drop: end
  }
}
```

Capacity can be set within the constructor:

```java
public Stack(@Loads('size') int size) {
this.size = size;
}
```

This does have a problem, which is that it makes static verification more complex. In fact, this practically moves the problem of attempting to check that a stack does not run out of space statically in a normal way to the protocol checker, which will try to do this in the same way that a "regular static analyzer would". In some sense, doing it this way would make Jatyc lose its *essence* of being a typestate checker and philosophy.

**question**: More specifically, how does one even check statically that a dynamic protocol is followed when its use is dependent on an initial user input?

**question**: Should we investigate other protocol tools to see if they implement a similar feature?

### Section 2.1 - Iterator example

I believe this is the same as the stack. That is, before using the method `next()` one must call `hasNext()`, and the only way (that I can think of) of knowing the amount of calls to  `next()` the object has left would be similarly having the length of the collection and then dinamically setting the typestates.

### Section 2.1 - Conclusion

On the topic of `Tension between Sequences and States`, I believe that modeling states takes away from the philosophy of protocols, or at least of Jatyc. Without the need of tracking the inner state of an object, programs using protocols might need to adapt to working with them and change the code style, but it would potentially make for safer and more reliable code.

### Section 2.2 - Iterator and ListIterator

Notice how in this example, it talks about an abstract subclass doing more than its superclass. That is, following the subclass' protocol does not mean that the superclass' protocol is being violated.  For example, for the ListIterator it is also the case that before using `next()` one must receive approval from the `hasNext()` method first. In that sense, the subclass' protocol is an **extension** of the superclass.

For example, if a concrete `Iterator` declared a method `reset()` to return the iterator to its initial abstract state, it would still be doing **more** than its supertype protocol. That is, you still cannot call `next()` until `hasNext()` has first returned a True value. Also, if one were to implement a circular iterator whose `hasNext()` method never returns **false**, then it would still not be allowing less within its protocol compared to its supertype (one must still call `hasNext()` before calling `next()`).

### Section 2.2 - Remove Iterators

On another example the paper brings up the method `remove()` for the iterators. "In the same vein, the non-implementation of optional methods by subtypes converts one protocol (“only call remove after next”) to a different one (“don’t call remove”)". Notice how *don't call remove* is an even stricter restriction than *call it after next*. If this were to be implemented in Jatyc as is, it would probably fail, since the subclass' protocol would not be a proper subprotocol of the superclass' one. Do notice though that this particular example is related to **optional methods**, and therefore their behavior might vary for each implementation. In that regard, there could be some adaptation here between Jatyc and the example.

**Example to Jatyc**: Accept that `remove()` is an **optional method** and don't define its behavior within the protocol of the abstract superclass. In general, methods of a subclass should follow the proper calling order from the superclass (such as for `hastNext()` and `next()`), so they should be included in the protocol, but not for optional methods.

The other option is to make it **non-optional** and not allow abstract subtypes to ignore the method.

**Jatyc to Example**: This probably would be as simple as adding optional protocol typestates. That is, if it is common that optional methods would be declared in interfaces, and that abstract subtypes break its behavior, then add a feature to the tool to allow certain transitions and typestates to be breakable by subtypes.

We could interpret this similarly to how subtyping a protocol should always allow you to do **more** transitions and not less than the ones stated in the supertype.

For example, if a normal Iterator interface without the `remove()` method could be declared as follows:

```
typestate RemovableIterator {
  HasNext = {
    boolean hasNext(): <true: Next, false: end>
  }
  Next = {
    Object next(): HasNext
  }
}
```

then we could make the entire method optional with the following syntaxis:

```
typestate RemovableIterator {
  HasNext = {
    boolean hasNext(): <true: Next, false: end>
  }
  Next = {
    Object next(): Remove | HasNext
  }
  Remove = {
    boolean hasNext(): <true: Next, false: end>,
    void remove(): HasNext
  }
}
```

Notice how now there is an `|`, which implies that a subprotocol might opt out of allowing the transition to typestate Remove. I don't fully like this syntax but it's one of the possibilities. What if the initial state is the one that is optional? (such as adding thing in the constructor or not).

### Section 2.2 - Public Stack

This example has already been implemented in our tests of the tool. Basically, the paper puts in question how a protocol can properly function when a class has public internal collaborators which could be modified by client code. If the protocol is supposed to be an abstract concept and not care for the internal state of an object, then how will it behave if the typestate should change because of a change in its state?

In this case, if we were to empty a stack manually by accessing its internal array of elements, the typestate **should** go back to being the initial one. The test showed how Jatyc simply does not let client code modify internal collaborators of objects that follow a protocol.

**Example to Jatyc**: Simply accept that modifying the state of an object that follows a protocol should be done through methods and not direct access to its internal collaborators.

**Jatyc to Example**: If we were to allow client code to modify internal collaborators, then Jatyc should have to know about the object's internal state and from there reason in which typestate the object should be in. This again goes against object protocols' and Jatyc's philosophy on what their role and responsibilities are.

### Section 2.2 - Conclusion

I feel like the conclusion I've reached is that subtyping a protocol must be done taking into careful consideration not to break it. That is, if different classes implementing an interface with a protocol wish to implement a variation of the protocol themselves, the new protocols must allow all operations and more, but never less, of the original one. There is one caveat with optional methods, which are currently not supported by Jatyc.

### Section 2.3 - Command Desing Pattern

In this section, a problematic example is mentioned where there is two objects: a `Context` object and a `Command` object which has a reference to the first. If one wanted for example to disable the command based off of the context, and include this within the protocol, how can one do this without observing the **external state** of the object?

**Example to Jatyc**: Same as with other examples, have a new method `canExecute()` within the `Command` class which when returning True it will move the object to the typestate allowed to execute the command. Within this method, the very command object can decide by itself, inspecting its internal and external state, whether it can be ran or not. This would force client code to first check for enabledness of the execution method.

**Jatyc to Example**: Same as with previous examples, I don't see a way to inspect the variables of an object without making the tool a lot more complex and diverging from its philosophy.

### Section 2.3 - Decorator Pattern

In the paper, there is an example mentioning the decorator pattern. In particular, it examplifies with the abstract class `java.io.OutputStream`, which declares that a method `write()` should be implemented when extending it. Since there are inconsistencies in the spec, different subclasse implement different protocols. For example, `ByteArrayOutputStream` allows client code to write after closing, but `FileOutputStream` does not.

In my opinion, since there are inconsistencies in the protocol of the superclass, there **should not** be a parent protocol mentioning the `write()` method. That is, were `OutputStream` to have a protocol that all subclasses must follow, why would it even include `write()` in it when it's not even sure how to handle it?

Now, overloaded variants is an interesting topic, because I don't think it is talked about in Jatyc. For example, is this allowed?

```
typestate LineWriterProtocol {
  Init = {
    Status open(String): <OK: Open, ERROR: end>
  }
  Open = {
    void write(String): Open,
    void write(char[]*): Open,
    bool write(String): Open, // returns if there were errors or not
    void close(): end
  }
}
```

### Section 2.3 - Conclusions

Perhaps I am missing some more analysis on this, but for now on the topic of intrinsic and extrinsic states, I believe that it can still be handled by object protocols. I also can't seem to find any opportunity for improvement for Jatyc.

### Section 2.4 - Constructors

In this section a point is being made about how attaching protocols to specific instances is favorable when talking about constructors. In particular, it talks about how the cause of a `Throwable` can only be set once, in its constructor or `ìnitCause()` method. Were a subclass of `Throwable` decide to overload its constructor and always initialize the cause, then it could potentially make a difference of the protocol.

**Example to Jatyc**: One could simply not allow to initialize the object with the cause and only accept doing so through the special method, or viceversa. This would limit the expressability of the client code but it would get the job done.

**Jatyc to Example**: I'm not sure how it works right now with Jatyc, but I believe we would need two features:

- Reason about the constructor method as a possible method to call from the inital state. I believe this is currently not being done but we need a test for it.
- Overloading of methods support. That is, if we were to provide the cause argument, then it would take us to a different typestate.

Then the Jatyc tool would be able to let the client code know in compile time that it is setting the cause twice incorrectly.

### Section 2.4 - Immutable objects

It also mentions in this section the problem with *immutable objects*. Examplifying with the class `java.math.BigInteger`, it speaks about how these objects can have different protocols, or available methods, depending on the constant values that they store. For the given example, if provided a negative number then one cannot call methods `sqrt()` or `nextProbablePrime()`.

This presents a problem where, again, the protocol in some sense has a need of knowing about the internal state of the object in order to determine which methods and typestates it can access to. For usual objects, I would say to create different subclasses of the same superclass, but for this case with immutable objects it is clearly a better design choice to keep both positive, zero and negative numbers represented under the same class.

**Example to Jatyc**: Add boolean control methods to the immutable object to solve the issue. Have a protocol where you need to first call these before calling any conflicting operation. For example, before calling `sqrt()` one must first call `canCalculateSqrt()`. One could think that this would add complexity to the usage of the objects, but it might not necessarily be worse. For example, it would turn the following client code:

```java
try {
    float sqrt = myNumber.sqrt();
} catch (Throwable e) {
    System.out.println("Can't get square root of negative number!");
}
```

to the following protocol compliant code:

```java
if (myNumber.canCalculateSqrt()) {
    float sqrt = myNumber.sqrt();
} else {
    System.out.println("Can't get square root of negative number!");
}
```

In my opinion both are declarative from the client code that we are accepting that the operation could fail and we are reacting to that eventuality.

**Jatyc to Example**: To fix this issue from Jatyc's side, and not have boolean control methods, one must necessarily peek into the object's state. In the case of *immutable objects*, there could be many ways of solving this with new features:

- Have multiple entire protocols that can be chosen from at construction time. It could be seen as having two automatas for each protocol, and then having an initial typestate which can transition to the initial state of one of them.
- Have enabled/disabled transitions/states which can be done at construction time. This can be seen as having a graph with colored edges, and one can only travel the edges with the allowed colors.

Me faltan diagramas para todo esto.

Despite the paper only mentioning *immutable objects*, I wonder what would happen in the case that we are modeling a mutable `BigInteger`, where one can modify it in place. For this scenario, where the protocol is constantly changing, I suppose the solution should be similar to the ones mentioned in section 2.1, where state vs sequences is discussed.

### Section 2.4 - Factory methods

This is the same as many other examples we've covered before. The paper states as a potential issue the usage of the factory pattern, where a factory returns an implementor of an interface which is unknown statically, usually set via config.

For Jatyc, if a method is to return an instance of the interface, then the tool only checks whether it follows the protocol of the interface and nothing else. (Actually I'm not 100% it works like this but I think it does based off of official tests, I need to re-check with a test of my own though). Even if the subclass returned actually has its own protocol, the client code should only be concerned at an abstract level and with the protocol of the more abstract interface.

Whether the subclasses have a protocol **more restrictive** than the interface's and therefore is not a subprotocol is another topic already covered in previous examples.

### Section 2.4 - Conclusions

I believe that the concept behind a couple of the features proposed for immutable objects and constructors in this section are interesting, but I'm not too sure of how useful they might be. This is because most of these problems can still be solved by the tool by changing the way we model objects and client code a little bit, plus I'm not too sure how common these issues are.

Finally, the paper reads in its synthesis of this section that "*In principle, each sequence in the protocol should be achievable by some adequate sequence of method invocations. Yet, upon instantiation, the allowable sequence of methods may be a subset of those described by the protocol for the type.*".

For constructors, this is an interesting thought, since we've seen in the examples that different constructors for the same interface can definitely lead to different sequences of allowed methods. Defining a protocol that encapsulates all of these variations, or adapting the variations and design to work under a stricter protocol, I'm not sure what is better and what the responsibility and reach of object protocols should be.

For immutable objects, this could easily be solved by adding boolean control methods or by adding conditional protocol sections.

For factory objects, this is technically not an issue for Jatyc to handle, and if a factory method returns an instance of an interface, then the client code should only expect it to behave as the interface's protocol. 
