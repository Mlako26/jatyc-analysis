# Jatyc and Checker Framework

In this document I will compile how the checker framework is used by Jatyc, which classes and interfaces it uses from it, and overall how the checker framework helps it achieve its purpose of checking object protocols.

## Type Qualifiers

[Checker Framework Documentation](https://checkerframework.org/manual/#creating-typequals).

As explained in the paper Jatyc is based on, the tool uses a new type system lattice and hierarchy to analyze whether a program follows the protocols of objects or not. These types are:

- Unknown: All possible values.
- Primitive: All primitive values from Java, like integers.
- Object: All objects, excluding null.
- Null: The null value.
- Moved: Object was passed as a parameter to a method or assigned to another value. These variables can NO LONGER BE USED.
- NoProtocol: Objects that don't follow a protocol.
- Ended: Objects which completed the protocol.
- State*: Object in a specific state of their protocol. There is one lattice type for each state of the protocol, for each protocol.
- Bottom: Conceptually the empty set. Allows for all operations. Used for unreachable variables, computations that might generate errors, etc.

To represent them, the checker framework allows defining type qualifiers via the `@interface` notation, and their hierarchy via the use os the `@SubtypeOf` notation. All qualifier hierarchies should have a top and a bottom qualifier. Jatyc makes use of this functionality to represent the overall structure of their qualifiers (can be seen [here](https://github.com/jdmota/java-typestate-checker/tree/master/src/main/java/jatyc/qualifiers)):

- BottomAnno: Simply the Bottom type of the type system.
- UnknownAnno: Simply the Top type of the type system.
- InternalInfoAnno: Everything that is in between.

Reasonably, the hierarchy is the following:

UnknownAnno <- InternalInfoAnno <- BottomAnno

Why they did not create the entire hierarchy as qualifiers from the framework? I gotta read a bit more into it to know.

## SourceChecker

[Checker Framework Documentation](https://checkerframework.org/manual/#creating-compiler-interface)

One of the main classes from the checker framework is the [SourceChecker](https://checkerframework.org/api/org/checkerframework/framework/source/SourceChecker.html) and its subclass [BaseTypeChecker](https://checkerframework.org/api/org/checkerframework/common/basetype/BaseTypeChecker.html). This second subclass is the one that not only allows for type annotation but also subtype checking. 

These checker classes serve as factories for all of the interesting type-system classes, which we will talk about later, and provide an interface to the java compiler.

Both classes already provide with many defauls that are useful for most checkers, but many methods can be overwritten to modify the checker's behavior.

One of these overrides is for example the `createSupportedTypeQualifiers()` method, which returns the type qualifier's set of the checker. Jatyc [overwrites it](https://github.com/jdmota/java-typestate-checker/blob/master/src/main/kotlin/jatyc/utils/TypeFactory.kt#L63) to include its types we described in the [previous section](#type-qualifiers).

### SourceVisitor

[Checker Framework Documentation](https://checkerframework.org/manual/#creating-extending-visitor)

The [SourceVisitor](https://checkerframework.org/api/org/checkerframework/framework/source/SourceVisitor.html) class is a visitor for ASTs. These visitors traverse the AST of each source file and, for each node, raise warnings when the type-system was violated. It is worth mentioning that these ASTs are provided by java's jdk compiler API.

TODO: learn more about how Jatyc uses the visitor.

### AnnotatedTypeMirror

[Checker Framework Documentation](https://checkerframework.org/manual/#creating-procedurally-specifying-implicit-annotations)

The [AnnotatedTypeMirror](https://checkerframework.org/api/org/checkerframework/framework/type/AnnotatedTypeMirror.html) class, similar to Oracle's [TypeMirror](https://docs.oracle.com/en/java/javase/21/docs/api/java.compiler/javax/lang/model/type/TypeMirror.html) class, is a representation of an annotated type.

TODO: learn more about Jatycs usage of it

### AnnotatedTypeFactory

The [AnnotatedTypeFactory](https://checkerframework.org/api/org/checkerframework/framework/type/AnnotatedTypeFactory.html) class is the one that returns the AnnotatedTypeMirrors given an [Element](https://docs.oracle.com/en/java/javase/21/docs/api/java.compiler/javax/lang/model/element/Element.html), which is a class representing program elements such as a module, class, method, etc, or a [Tree](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.compiler/com/sun/source/tree/Tree.html), which is the Java interface for all nodes in an AST.

Basically what this factory does is that when given a program expression, it returns the expression's type.

For a more precise explanation related to the use that the checker framework gives them, [Elements](https://checkerframework.org/manual/#creating-javac-elements) help represent a compile-time language-level construct. Quoting from the checker framework's documentation, "There is an Element interface to represent each construct, e.g., TypeElement for classes/interfaces, ExecutableElement for methods/constructors, and VariableElement for local variables and method parameters.".

On the other hand, [Trees](https://checkerframework.org/manual/#creating-javac-trees) are used to represent nodes in the AST, and particularly are good to traverse code and reach the Types and Elements we want to process. Quoting from the checker framework's documentation, "A Tree represents a syntactic unit in the source code, such as a method declaration, statement, block, for loop, etc".

TODO: mirar el CFadapter