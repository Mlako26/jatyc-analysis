# History of Typestates and Object Protocols

In this document, I will attempt to give some background on the Typestates theoretical framework and the notion of Object Protocols. This could be of us as a "previous works" section, simply a short "object protocols" section, or something similar.

I will give a short explaination of a couple papers I've read for this, so that they can be of use later on.

## Typestate: A programming Language Concept for Enhancing Software Reliability

- By Strom and Yemini

This paper, written all the way back in 1986, introduces to the world the notion of `Typestates`. The authors found a gap to fill when looking at type systems in programming languages. Specifically, when one would like to constrain developers from doing actions that should be forbidden in certain contexts, such as derefercing a pointer after the memory it points to has been freed, or using a variable before it has been initialized.

While types restrict developers on which operations are allowed for a particular data object in any context, typestates attempt to restric developers on the order in which certain operations should happen given a particular context. A set of typestates is then associated with a certain type, and for each typestate a set of allowed methods is defined. An object from that type will be in one of these typestates at any point in the program, and by calling the allowed methods it will (or not) transition to other typestates.

In the paper, the notion of protocol completion also makes an initial appearance, where objects are supposed to return to the initial typestate before the program terminates for it to have a *typestate-correct* execution.

The formal theory for typestates and an initial algorithm to determine statically if a program is *typestate-consistent* is also presented in this paper.

## Lightweight Object Specification with Typestates

- By Bierhoff and Aldrich

In this paper, more contributions to the typestates theory are brought to the table, such as hierarchical state refinement, method refinement and state dimensions.

On one hand, state refinement talks about subtyping a protocoled tyoe with another type that wants to extend the protocol. If the subtype wants to add new typestates to its protocol, it must make sure to still follow the supertype's. Similarly to Jatyc, State Refinement talks about how a set of states in the subtype are subcases of a single state in the supertype, meaning that eventhough they are adding even more valid method call sequences, the existing ones from the supertype are still maintained. In a certain sense, state refinement talks about how the supertype's finite automata must be contained within the subtype's.

On another hand, state dimensions talk about data objects potentially following multiple typestates at once. Suppose we have an object whose state depends on two conceptual variables which are unrelated, or **orthogonal**. For example, looking at a class like this:

```java
@Typestate("Iterator")
public interface Iterator {
  public boolean hasNext();
  public int next();
  public boolean canPrint();
  public void print();
}
```

One can observe that the class' behavior depends on both if it can iterate to the next object in the collection and if it can currently print or not. Supposing that both of these conditions are independent from each other, attempting to build a protocol based off of them as lax as possible could turn out to be cumbersome, as it might have to create a cross product of the possible method calls. One can see that in a big enough type, where there are potentally a higher number of orthogonal variables which the state depends on, this cross product of states could skyrocket in size. 

Instead, in this paper a notion of **state dimensions** is introduced, where a data object can be in multiple orthogonal states at once, allowing for different sequences of method calls. This makes it easier to specify protocols in these cases. 

Finally, the paper also touches on **dynamic state analysis**, which is different from Jatyc's static analysis. It is based off of state invariants, which are checked at runtime to determine the current state of a data object.

TODO: **DE ESTE PAPER LEER TAMBIEN LAS REFRENCIAS QUE MARCASTE**

## An Empirical Study of Object Protocols in the Wild

- By Beckman, Kim and Aldrich

This is an interesting article from 2011 talking about the use of object protocols in open source Java software. In particular, how much are protocols defined, how often they are used/followed by client code, and which types of protocols are the most common. It did this by performing a static analysis and manual examination on open source projects, finding candidates classes for protocol definition and/or use. It discovered some interesting results, such as protocols being defined in Java types being more common than the use of generic type parameters.

## Foundations of Typestate-Oriented Programming

- By Garcia, Tanter, Wolff and Aldrich

This is another article published in 2014 talking about **Typestate Oriented Programming** (TSOP), the paradigm where code is not only modeled around classes but their typestates and protocols.

In particular, it defines a new TSOP language which supports direct protocol specification, instead of using the usual plugins and frameworks on top of another language. This attempts to bring programmers closer to typestates by increasing their integration to the programming language they use.

It defines many similar features and concepts to Jatyc's, such as state transitions, permissions in the presence of alliasing, etc.

TODO: Leer los papers que menciona esto también

## Plaid

Another similar TSOP language is [plaid](https://www.cs.cmu.edu/~aldrich/plaid/). It is also mentioned as inspiration for Jatyc, and is one of the first TSOP languages. In it, objects are always in a particular state, which are defined very similarly to classes (with methods, variables, substates, etc). Objects can then transition in runtime between one state and another, modifying their behavior and their interface.

It also touches on the matter of alliasing, where to mitigate the issue it defines different types of access permissions, granting variables read and/or write permissions to the objects they refer to.
