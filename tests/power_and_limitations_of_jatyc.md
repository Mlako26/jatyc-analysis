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