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

Jatyc's own checker classes can be found [here](https://github.com/jdmota/java-typestate-checker/blob/master/src/main/kotlin/jatyc/JavaTypestateChecker.kt#) and [here](https://github.com/jdmota/java-typestate-checker/blob/master/src/main/kotlin/jatyc/utils/TypeFactory.kt). 

### JatycTypestateChecker

Extends the [SourceChecker](https://checkerframework.org/api/org/checkerframework/framework/source/SourceChecker.html) directly.

It overrides the `createSourceVisitor()` method, making it return the custom class [CFVisitor](https://github.com/jdmota/java-typestate-checker/blob/master/src/main/kotlin/jatyc/core/adapters/CFVisitor.kt).

It also overrides the `initChecker()` method, making it initialize some utilities classes.

Finally, it provides utility to print out warning and error messages, overwriting default methods from the `SourceChecker` class.

### FakeBasicTypeChecker 

Extends the [BaseTypeChecker](https://checkerframework.org/api/org/checkerframework/common/basetype/BaseTypeChecker.html), which itself extends the [SourceChecker](https://checkerframework.org/api/org/checkerframework/framework/source/SourceChecker.html).

One of these overrides is for example the `createSupportedTypeQualifiers()` method, which returns the type qualifier's set of the checker. Jatyc [overwrites it](https://github.com/jdmota/java-typestate-checker/blob/master/src/main/kotlin/jatyc/utils/TypeFactory.kt#L63) to include its types we described in the [previous section](#type-qualifiers).

## SourceVisitor

[Checker Framework Documentation](https://checkerframework.org/manual/#creating-extending-visitor)

The [SourceVisitor](https://checkerframework.org/api/org/checkerframework/framework/source/SourceVisitor.html) class is a visitor for ASTs. These visitors traverse the AST of each source file and, for each node, raise warnings when the type-system was violated. It is worth mentioning that these ASTs are provided by java's jdk compiler API.

The SourceVisitor class implements Javac's interface [TreeVisitor](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.compiler/com/sun/source/tree/TreeVisitor.html), which has the basic methods needed to be considered an AST visitor, and extends Javac's [TreePathScanner](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.compiler/com/sun/source/util/TreePathScanner.html) class, which also implements the TreeVisitor interface.

### CFVisitor

SourceVisitors are an internal collaborator of SourceCheckers, and Jatyc's JavaTypestateChecker uses its own [CFVisitor](https://github.com/jdmota/java-typestate-checker/blob/master/src/main/kotlin/jatyc/core/adapters/CFVisitor.kt) class, which extends the aforementioned SourceVisitor.

This visitor has a couple of particularities and differences with its superclass. The main one is that it performs a two-phase analysis, going through all of the code twice instead of once; it does not run any class analysis nor typestate related work on the first one, rather it simply sets up variables and mappings for the second phase to do so.

The first phase tags along to the Checker Framework's file/code discovery, overwriting the superclass' methods such as `visitClass()` in order to make a conceptual map of the code. For example, the Checker Framework analyses each file and set's the SourceVisitor's [CompilationUnitTree](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.compiler/com/sun/source/tree/CompilationUnitTree.html) `root` variable, which is the root node of the AST for an entire **.java** file (containing information such as package/import statements, top-level class declarations, code to file-lines mapping, etc). This root is later on used, for example, for warning/error reporting, marking which file and line has a potential mistake. The CFVisitor class [uses this functionality](https://github.com/jdmota/java-typestate-checker/blob/master/src/main/kotlin/jatyc/core/adapters/CFVisitor.kt#L32-L40) to store a map between classes and their CompilationUnitTree, to later on be used in the second phase where otherwise it would've been lost.

Another layer of checks the visitor performs on the first phase are the validation of annotations within the code, such as `@Requires`, `@Ensures` and `@Typestate`. By looking at the AST provided by the Checker Framework and javac, it checks for example that they are situated in the correct place, or that the states that they mention appear on the protocol of the object's class they refer to.

Finally, during this first phase the CFVisitor makes extensive use of the [CFAdapter](https://github.com/jdmota/java-typestate-checker/blob/master/src/main/kotlin/jatyc/core/adapters/CFAdapter.kt#L274) class. This class has the job to adapt many of the Checker Framework's classes and models into something understandable by the class analyzers. For example, one of the structures it translates is the [CFGraph](https://checkerframework.org/api/org/checkerframework/dataflow/cfg/builder/CFGBuilder.html) of all code blocks provided by the framework into Jatyc's [SimpleGraph](https://github.com/jdmota/java-typestate-checker/blob/master/src/main/kotlin/jatyc/core/cfg/SimpleCFG.kt#L42), which is a simplified version. Another structure it translates is the framework's CFG [Node](https://github.com/typetools/checker-framework/tree/master/dataflow/src/main/java/org/checkerframework/dataflow/cfg/node) structure into [CodeExpr](https://github.com/jdmota/java-typestate-checker/blob/master/src/main/kotlin/jatyc/core/cfg/CodeExprs.kt#L103), which also carry information regarding Jatyc's own internal representation of java code and type system.

The second phase is started after the Checker Framework finishes visiting the code when [typeProcessingOver()](https://github.com/jdmota/java-typestate-checker/blob/master/src/main/kotlin/jatyc/JavaTypestateChecker.kt#L83-L87) is called in the `JavaTypestateChecker`. This in turn calls the visitor's [finishAnalysis()](https://github.com/jdmota/java-typestate-checker/blob/master/src/main/kotlin/jatyc/core/adapters/CFVisitor.kt#L43-L49) method, which performs all typestate related analysis. For this analysis the Checker Framework is not involved, since all structures have already been translated to Jatyc's  entities.

## Utils Classes

Jatyc makes extensive use of the utils classes that the Checker Framework provides.

[`ElementUtils`](https://checkerframework.org/api/org/checkerframework/javacutil/ElementUtils.html) is a class made to analyze [`Element`](https://docs.oracle.com/en/java/javase/21/docs/api/java.compiler/javax/lang/model/element/Element.html)s. These are instances of an interface provided by javac that represent a compile-time language-level construct. Quoting from the checker framework's documentation, "There is an `Element` interface to represent each construct, e.g., TypeElement for classes/interfaces, ExecutableElement for methods/constructors, and VariableElement for local variables and method parameters.". Jatyc uses these utilities to ponder if for example to check if a class member is static, like [here](https://github.com/jdmota/java-typestate-checker/blob/master/src/main/kotlin/jatyc/core/JavaTypes.kt#L57-L59) or to reason about the hierarchy of the code [here](https://github.com/jdmota/java-typestate-checker/blob/master/src/main/kotlin/jatyc/core/adapters/CFAdapter.kt#L44).

[`TypesUtils`](https://checkerframework.org/api/org/checkerframework/javacutil/TypesUtils.html) is another utils class that helps to analyze [`TypeMirror`](https://docs.oracle.com/en/java/javase/21/docs/api/java.compiler/javax/lang/model/type/TypeMirror.html)s. This is an interface also provided by the javac which represents types in Java, such as primitive types, classes and interfaces, arrays, etc. Jatyc uses this class to check which kind of type a `TypeMirror` is, such as in [here](https://github.com/jdmota/java-typestate-checker/blob/master/src/main/kotlin/jatyc/core/JavaTypes.kt#L23-L25), or to analyze the hierarchical relationship between different types, like [here](https://github.com/jdmota/java-typestate-checker/blob/master/src/main/kotlin/jatyc/utils/JTCUtils.kt#L148-L183).

[`AnnotationUtils`](https://checkerframework.org/api/org/checkerframework/javacutil/AnnotationUtils.html) are used to work with [`AnnotationMirrors`](https://docs.oracle.com/en/java/javase/21/docs/api/java.compiler/javax/lang/model/element/AnnotationMirror.html)s, which are simple representations of an annotation in Java. It mainly uses it to get the name of the annotations, such as [here](https://github.com/jdmota/java-typestate-checker/blob/master/src/main/kotlin/jatyc/core/TypeIntroducer.kt#L31-L35).

[`TreeUtils`](https://checkerframework.org/api/org/checkerframework/javacutil/TreeUtils.html) are used to analyze javac's AST [`Tree`](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.compiler/com/sun/source/tree/Tree.html) nodes. In Jatyc it is mainly used to extract `Elements` from `Tree` nodes, like [here](https://github.com/jdmota/java-typestate-checker/blob/master/src/main/kotlin/jatyc/core/adapters/CFVisitor.kt#L140).

## AnnotatedTypeMirror

[Checker Framework Documentation](https://checkerframework.org/manual/#creating-procedurally-specifying-implicit-annotations)

The [`AnnotatedTypeMirror`](https://checkerframework.org/api/org/checkerframework/framework/type/AnnotatedTypeMirror.html) class, similar to Oracle's [`TypeMirror`](https://docs.oracle.com/en/java/javase/21/docs/api/java.compiler/javax/lang/model/type/TypeMirror.html) class, is a representation of an annotated type within the framework.

TODO: learn more about Jatycs usage of it

## AnnotatedTypeFactory

The [AnnotatedTypeFactory](https://checkerframework.org/api/org/checkerframework/framework/type/AnnotatedTypeFactory.html) class is the one that returns the AnnotatedTypeMirrors given an [Element](https://docs.oracle.com/en/java/javase/21/docs/api/java.compiler/javax/lang/model/element/Element.html), which is a class representing program elements such as a module, class, method, etc, or a [Tree](https://docs.oracle.com/en/java/javase/21/docs/api/jdk.compiler/com/sun/source/tree/Tree.html), which is the Java interface for all nodes in an AST.

Basically what this factory does is that when given a program expression, it returns the expression's type.

For a more precise explanation related to the use that the checker framework gives them, [Elements](https://checkerframework.org/manual/#creating-javac-elements) help represent a compile-time language-level construct. Quoting from the checker framework's documentation, "There is an Element interface to represent each construct, e.g., TypeElement for classes/interfaces, ExecutableElement for methods/constructors, and VariableElement for local variables and method parameters.".

On the other hand, [Trees](https://checkerframework.org/manual/#creating-javac-trees) are used to represent nodes in the AST, and particularly are good to traverse code and reach the Types and Elements we want to process. Quoting from the checker framework's documentation, "A Tree represents a syntactic unit in the source code, such as a method declaration, statement, block, for loop, etc".

TODO: mirar el CFadapter