# How Jatyc Works

In this document I will attempt to compile findings on the inner workings of the Jatyc tool. Despite being uploaded in a public github repo, official documentation or papers on this are not so direct to find, and my main lead is a msc thesis pdf explaining how the initial version worked. This had one or two extra features that the current version does not, and lacks support for some functionality that it currently does. Despite this, the explanation is so complete and the tool it describes is so similar that I assume they must've kept most of the inner workings the same.

[Link to the original msc thesis](https://github.com/jdmota/java-typestate-checker/blob/master/docs/msc-thesis.pdf).

## Version Differences

From chapter 4.2, where the thesis explains the features that their initial Jatyc tool has, we can observe some differences between it and the current version. Here are what I consider to be the most important distinctions (there probably are more).

Some extra features include:

- **State Refinement** (chapter 4.2.2): In this version, one can write an `@Ensures` annotation not only for return types but also for method parameters. While I did not test it, the [documentation on the current tool](https://github.com/jdmota/java-typestate-checker/wiki/Documentation#ensures-annotation) is specific on this apparently only working on returning types. In fact, the original tool did not support using the `@Ensures` annotation in the return type at all, but they did have a different one for the same functionality: `@State`. This actually makes me think of a new example that I have not tested before: how does Jatyc currently ensures linearity of objects passed as parameters?
- **Linearity**: While the paper does describe how a second version of the tool might enforce linearity in a more relaxed way, the explanation for the first version seems to not allow to share a reference to the same object. That is, if there are multiple references to the same object, ensuring that the protocol is followed and completed is harder, and the current version does so with strict linearity (no more than one reference can alter the protocol of the object).

## Implementation

According to section 4.3 of the thesis, the tool basically analyzes each class on their own, going method for method in two phases. A first one infers the types of the variables and fields. A second one uses these inferred types to report errors when type incompatibilities are detected or invalid operations are performed. Also it ensures that protocols are completed and objects are used in a linear way.


### First Phase: Type Inferring

Jatyc uses this phase to infer even more types than the ones Java already infers statically. To do this, the tool uses the following lattice (the picture was taken from the thesis):

![](./assets/img/lattice.png).

Their descriptions are:

- Unknown: All possible values.
- Primitive: All primitive values from Java, like integers.
- Object: All objects, excluding null.
- Null: The null value.
- Moved: Object was passed as a parameter to a method or assigned to another value. These variables can NO LONGER BE USED.
- NoProtocol: Objects that don't follow a protocol.
- Ended: Objects which completed the protocol.
- State*: Object in a specific state of their protocol. There is one lattice type for each state of the protocol, for each protocol.
- Bottom: Conceptually the empty set. Allows for all operations. Used for unreachable variables, computations that might generate errors, etc.

There are a couple of rules that the implementation follows:

- The type of a variable/field can be one of these or the union of multiple ones.
- There are no unions within unions.
- A set with the unknown type is just the unknown type.
- A set with only one tye is just that type.
- An empty set is the Bottom type.
- Unions don't include Bottom since it is the empty set.
- If Object is present in an union, NoProtocol/Empty/State* are not since they are already subtypes.

The following are the steps that the tool follows in this phase to infer the types of variables and fields.

1. Visit each class independently.
2. If the class does not have a protocol, assume trivial one where has one state, droppable, all methods available and lead to the same state.
3. Analyze the **non-static** methods in the order of the protocol (this is to avoid analyzing invalid sequences of method calls), starting from the constructor. This process is called *class analysis*. 

#### Class Analysis

In a nutshell, from an initial state, it goes from top to bottom for each expression in the method, and tracks the type for each variable/field similar to other lattice based static checkers. The following is a more detailed description of how it works, but the full algorithm can be seen in section 4.3.2 and 4.3.3 of the thesis.

To analyze a particular method, the following rules are followed:
   - Expressions are analyzed independently.
   - For each expression, call a *transfer* function which accepts a pair of *stores* and returns another pair of *stores*. A *store* is a mapping between all variables/fields to their respective types. For the pair of *stores* that the *transfer* function returns, the first one is the *then store* (if the current expression evaluates to true) and the second the *else store* (if it evaluates to false). If the expression does not evaluate to a boolean, these two should be the same. The input for the *transfer* function is the result of the previous expression.
   - For the first expression (where there is no previous one for input for the *transfer* function), the input is the *entry store*.
   - For the last expressions (could be multiple), their results are merged to produce the *exit store*.
   - Both *entry stores* and *exit stores* work as pre-post conditions of a method.
   - Merging stores is a store that includes all variables/fields in the two stores, and if the same variable/field is present in both of them, their new type is the union of their correspondent types.

The algorithm keeps track of two sets:

- A state (from the protocol) to store.
- A method to store.

In the beginning, all methods and states start with an empty store, which assigns the type *bottom* to all variables/fields. The algorithm then infers the initial types of the fields by analyzing the constructor method, and associates the *exit store* with the initial state from the protocol. It then pushes the initial state into a queue.

It then starts to process all states within the queue doing the following:

1. It loops through all methods reachable from that state.
2. For each of them, it merges the state store and the method store, and sees if it is different from just the method store. 
3. If it isn't, then the method was already analyzed with the same store and thus it is skipped.
4. Otherwise, the method has its store updated in the set and then analyzed with it as *entry store*.
5. The *exit store* from the method analysis is then merged with the destination state store, and sees if it is different from just the destination state store.
6. If it isn't, then nothing happens.
7. If it is, then the destination state has its store updated and then queued for future analysis.

The algorithm stops when there are no more states in the queue to process, thus having checked all possible combinations of call sequences and all possible types for the variables and fields.

### Second Phase: Error Checking

In this phase, all expressions are checked for:

- Type incompatibilities, such as the variable/field being a correct type for a method call.
- Unsafe operations, such as method calls on potential null objects.
- Protocol completion by the end of methods.