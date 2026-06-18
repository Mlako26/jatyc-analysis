# Jatyc and Mungo

Since Jatyc is deeply intertwined with another Typestate checker for Java, **Mungo**, in this document we will cover their relationship and main differences.

## Mungo

[Mungo](https://www.dcs.gla.ac.uk/research/mungo/tools/) is a predecesor tool for Java which, like Jatyc, aims to control the order of method calls ([read more here](https://www.dcs.gla.ac.uk/research/mungo/publication/pub3/)). It does so under the same theoretical framework of *Typestates*, and reports errors when class instances don't follow their protocol.

The tool is implemented in java using the [JastAdd framework](https://jastadd.cs.lth.se/web/). It is a meta-compilation system which is easily extensible, allowing users to create custom static analysis tools. Despite  being used for many different [applications](https://jastadd.cs.lth.se/web/applications.php), it is not very actively maintained, racking up just 3 releases in the past 6 years as of the time of writing this.

On the other hand. Jatyc is instead implemented in the [Checker Framework](https://checkerframework.org), which extends Java's type system. It allows developers to include several plug-ins, or checkers, which perform different types of assertions and verifications when compiling code. One of the most popular plug-ins for the framework is the **Nullness Checker**, which asserts that no null pointer exceptions will be thrown if the program is run, but there are many others such as the **SQL Quotes Checker**, to prevent SQL injection vulnerabilities, or the **Regex Checker**, to prevent wrong usage of regular expressions. It is a heavily tested and trusted by many high-tier companies such as Amazon with their [KMS Compliance Checker](https://github.com/awslabs/aws-kms-compliance-checker) and their [Crypto Policy Compliance Checker](https://github.com/awslabs/aws-crypto-policy-compliance-checker). It is also much more actively mantained compared to JastAdd.

Authors of Jatyc mention [reference](https://github.com/jdmota/java-typestate-checker/blob/master/docs/msc-thesis.pdf) that despite initially wanting to simply extend Mungo with new features, taking all into consideration, in the end they decided to re-write the tool in the Checker Framework.

Another difference between Mungo and Jatyc is that while the former was written in Java, the latter was written in Kotlin. Authors mention [reference](https://github.com/jdmota/java-typestate-checker/blob/master/docs/msc-thesis.pdf) that the code between both languages being interoperable, and the increased consiceness and safety features it provided, meant that development of the tool would go smoother.

Mungo works very similar to how Jatyc does, which makes sense since the latter is derived from it. A protocol is written in a separate file and then referenced by a class using the annotation `@Typestate("<protocol-name>")`. When compiling the Java source code, it is first statically checked by the regular java type system, and then by Mungo's typestate system. If a class is associated with a protocol, clients of the class must respect the proper calling order of its method. Otherwise, the tool will raise an error.

Protocols are even written with a very similar syntax as Jatyc, if not the same in most cases. The following is an official example of how a stack protocol could look like, for a stack that allows for infinite pushes and pops only if the stack is not empty:

```
typestate StackProtocol {
    Empty = {
        void push(int): NonEmpty,
        void deallocate(): end
    }
    NonEmpty = {
        void push(int): NonEmpty,
        int pop(): Unknown
    }
    Unknown = {
        void push(int): NonEmpty,
        Check isEmpty(): <EMPTY: Empty, NONEMPTY: NonEmpty>
    }
}
``` 

Similarly to Jatyc, the first state mentioned in the protocol is the inital one, which accounts for an empty stack. Then, one can deallocate the memory for the stack, ending the protocol with the transition to `end`, or push an element into it. After pushing, one can push many more times or pop once, and must check if the stack is empty or not if we want to pop again.

Talking about the differences between both tools, since Jatyc was initially planned as an extension for Mungo, the developers wrote down a [comparison table](https://github.com/jdmota/java-typestate-checker/wiki/Mungo-comparison) to show off the new features. In particular, Mungo is able to perform what they call *basic checking* and *Decisions on enumeration values* (which are the conditional typestate transitions), but it lacks support for many additional features Jatyc comes with, such as:

- Argument/return values typestate enforcement (with `@requires` and `@ensures`).
- Droppable states.
- Subtyping (which is not mentioned in the table but is not supported by Mungo).

There are also features where Mungo fails at handling certain corner cases while Jatyc does not, such as Nullness, Linearity and protocol completion checking.

Finally, looking at Mungo's repo, it seems to have already been discontinued, as their latest commit was from 2020 (6 years ago as for the time of writing this), while Jatyc continued getting updates until 2025 (a year ago). If anyone wanted to extend one of these tools, or simply use them, it is natural that Jatyc should be picked over Mungo. 
