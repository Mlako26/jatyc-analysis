# Thesis Structure

In this document I will propose a structure for the thesis, given the investigation carried out and the different topics we can touch on.

## Contents

The following is a list of the research we've already done, which potentially will become the contents of the thesis:

- Short background on theoretical framework, such as other related works, typestate theory, etc.
- Jatyc explaination, an introduction to it and also how it works.
- Some notes on the documentation of jatyc and also its official examples
- Comparison with Mungo
- Comparison with the four dark corners examples
  - Explaination of how Jatyc solves them already
  - Some limitations of Jatyc (and potentially object protocols with typestates in general)
- Many test examples showing the power of Jatyc and its limitations.

## Thesis' Aim

To know which structure should the thesis follow, we would first need to know what exactly we want to portray, or its aim.

Given the research performed, I would say the focus of the thesis should be around Jatyc itself as an object of study, and the thesis should teach readers about the tool, its theoretical and practical background, comparison with its father tool Mungo, power/limitations, etc.

## Structure

1. Introduction
   1. Motivation
     - Why are object protocols important, explain about how we've tried to analyze code both statically and dynamically for a long time to prevent errors from it, and how there's been many tools/programming languages that take them into account.
     - We can mention here the paper `An Empirical Study of Object Protocols in the Wild`, mentioned [here](../docs/theoretical_framework.md#an-empirical-study-of-object-protocols-in-the-wild), which talks about how much used do object protocols get in the industry.
     - A summary of what is Jatyc, why it is of interest in the field of object protocols, and explain that in the thesis we will put it under the microscope to understand one of the cutting-edge tools for object protocol analysis.
     - Explain that by analyzing Jatyc we will learn more about the power/guarantees that Object Protocol Analysis has, the gaps/limitations that it has, and possible future contributions that could be made for the field.
   2. Thesis Structure
      - Explain a bit about the thesis structure, how the following chapters will look like, what they will contain, etc.
2. Related Work
   1. Theoretical Background
      - Talk about the introduction of the notion of typestates to the field.
      - Talk bout the paper `Lightweight Object Specification with Typestates` which uses typestates to define object protocols even further.
      - Perhaps we could add more papers here if necessary.
   2. Practical Typestate Related Works
      - Talk about Featherweight Typestate (a Typestate Oriented Programming language), mentioned [here](../docs/theoretical_framework.md#foundations-of-typestate-oriented-programming)
      - Talk about Plaid (a Typestate Oriented Programming language), mentioned [here](../docs/theoretical_framework.md#plaid)
      - Talk about Mungo, how it relates to Jatyc, and similarities/differences.
3. Jatyc
   - Talk about the origin of the tool, what it does, etc.
   - Advantages of Jatyc over other checkers (such as subtyping, droppable states, etc).  
   1. Practical examples
      - Give simple examples of Jatyc, show off the notation
   2. How it works
      - Talk about how it works internally, how it analyses code, etc.
      - How it uses the checker framework
      - Talk about experimental non-linear version of it
4. Experiments
   - Give a quick rundown of wanting to experiment with the tool to find its powers and limitations. Explain that by testing we validated the tool's advertized features against border/interesting use cases, and also attempted to find functionality that isn't yet implemented (or is a limitation to typestate-oriented object protocols in general).
   - Explain that the tests were carried out by using the tool to analyze small snippets of code, which includes different classes with/without protocols interacting with each other.
   - Each chapter here should tackle a different aspect of the tool we are testing for, these can be seen in the [Testing Categories](#testing-categories) section below. It should include one or two interesting experiments with class diagrams, the results we found from the tool, what we found out it was capable of and which limitations we found in the area.
5. Conclusions and Future Work
   - Give a final analysis and rundown of our findings while analyzing the tool.
   - Some thoughts on the usability of the tool, its impact and benefits. The power of the tool and its clear limitations.
   - Future improvements that could be done to the tool and open problems (if any).

### Testing Categories

Acá voy a compilar diferentes tópicos generales que venimos testeando con la tool, y todos los tests que considero que podrían incluirse en las categorias.

- **Compound Protocols**
  - En esta sección hablar sobre qué pasa cuando se tienen protocolos compuestos, donde varias clases dependen del protocolo de la otra. Cómo se comporta Jatyc ante estas situaciones.
  - collaborator_compound_typestate
    - Primera experimentación entre el RobotController y el Robot, donde vimos que Jatyc evalúa por separado cómo el Client Code usa el RobotController, y cómo el RobotController usa el Robot.
  - collaborator_compound_typestate_2
    - Similar al anterior. Este test simplemente introduce por primera vez el método `run()` del RobotController, que hace dar una vuelta al robot. No parece tan interesante para mencionar.
  - collaborator_compound_typestate_3
    - Este test muestra en forma muy interesante cómo funciona el algoritmo de class analysis de Jatyc. En él, considera todas las posibles secuencias de llamadas a los métodos en el protocolo del RobotController, y de ahi se da cuenta de qué formas se puede romper el protocolo del Robot que tiene adentro.
  - collaborator_compound_state_4
    - En este ejemplo se muestra qué sucede cuando el RobotController termina su protocolo antes del Robot. Es decir, el objeto padre termina su protocolo antes que su colaborador interno. En particular, Jatyc correctamente lo detecta y levanta un error.
  - collaborator_compound_state_5
    - Similar al anterior, pero qué sucede si terminamos el protocolo del colaborador interno antes que el objeto padre? En particular, Jatyc reconoce que ya casi que no le vamos a poder dar uso a los métodos que antes modificaban el estado del colaborador interno? Jatyc si se da cuenta de todo esto, y alerta que potencialmente vamos a intentar modificar el estado del robot a pesar de ya haber terminado su protocolo.
- **Method Sequences Vs Correctness**
  - En esta sección hablar sobre como hacer análisis de protocolos no tiene que ver con correctitud del código de los métodos. Es decir, el código de los métodos puede estar mal pero el uso de su protocolo bien.
  - faulty_stack 
    - Un stack que su implementación tiene fallas y larga excepciones, pero su protocolo lo cumple bien.
  - normal_stack
    - Esto es un stack normal, donde para hacer que cumpla un protocolo hay que agregarle métodos booleanos de control. Sirve para explicar que la tool no puede trackear cantidad de llamadas a métodos o secuencias de métodos, a no ser que hagamos un estado por llamada. Es decir, en escencia lo que se testea es querer determinar cuándo estoy por ejemplo haciendo un push cuando la pila ya está llena. En ese sentido, la herramienta al no tener una pila interna es incapaz de asegurarse que el código sea seguro, y te obliga a usar métodos booleanos de control.
    - Explicar que si no puede probar que tu código es seguro, te fuerza a hacer checkeos (por medio de métodos de control) para que lo sea. En ese sentido, son falsos positivos, donde potencialmente el codigo esta bien pero te obliga a ser mas defensivo.
- **Human Intervention**
  - En esta sección se puede hablar sobre cómo a la hora de hacer protocolos puede haber errores humanos, al menos a la hora de sub-sobre especificar, o tener un protocolo mal hecho en general. Es decir, es un checker que depende de input humano para checkear.
  - restrictive_iterator
    - Un iterador sobreespecificado
  - lax_iterator
    - Un iterador subespecificado
    - En este ejemplo intentamos "subespecificar" un protocolo. Es decir, hacer que el protocolo permita más de lo que debería. En realidad, a priori este test no parece ser muy bueno, símplemente muestra que el protocolo está bien, y el `EvenIntIterator` no debería de permitir construirse con una collección que no pueda recorrer. La llamada de métodos parecería estar bien, que es lo que Jatyc terminó diciendo. 
  - underspecified_iterator
    - Un mejor ejemplo como "subespecificación" de protocolo, simplemente muestra un iterador que tiene un protocolo que permite `hasNext()` y `next()` todo el tiempo. Esto no depende del input, pero la cadena de llamadas estaría mal y lanzaría errores potencialmente de pasarse de index bounds.
- **Subtyping**
  - En esta sección hablar sobre cómo funciona el subtyping en la herramienta. Principalmente que protocolos del subtipo deben de incluir todas las secuencias de métodos que permite el protocolo del supertipo, y como mucho más, pero nunca menos.
  - subtyping_iterators
    - En este test mostramos diferentes protocolos y sus grafos, y mostramos como diferentes jerarquías de protocolos se comportan entre sí.
  - restrictive_iterator
    - Otro ejemplo práctico de un subprotocolo que permite más que su protocolo padre, y Jatyc por ende lo permite.
  - subtyping_with_droppables
    - En este test se demuestra que Jatyc toma a `end` (estado de protocolo terminado) como un estado más al que hay que llegar del autómata del protocolo. Hace falta incluir todas las transiciones a end del supertipo en un subtipo.
- **State Versus Typestates**
  - In this section talk about the differences between the state of an object and its typestate. That is, do internal collaborators determine the typestate it should be in, and how does Jatyc relate both.
  - state_equals_typestate
    - Talks about how anytime methods can overwrite internal collaborators, despite the documentation of Jatyc stating that they shouldn't be able to. That is, there is a change of the state of the object but not of the typestate.
  - state_equals_typestate_2
    - En este ejemplo aprovechamos lo encontrado en el anterior para romper un objeto que seguia su protocolo. Es decir, conseguimos que un objeto que llamaba a sus métodos dado el orden de su protocolo tire una excepción inesperada.
  - public_stack
    - En este ejemplo creo un stack con un colaborador interno público. Por alguna razón esto sí es bloqueado por jatyc, lo cual tiene sentido porque sino no se sabe si se rompe el protocolo o no.
- **Border Cases** o **Modeling Capabilities**
  - Esta categoría no habla tanto de cosas conceptuales de object protocols o typestates, sino quizás como casos bordes puntuales de jatyc (quizás estaría bueno pensar un nombre mejor para la categoría).
  - overloaded_stack_1
  - overloaded_stack_2
    - Ambos ejemplos hablan sobre cómo tener métodos sobrecargados igualmente funciona, Jatyc puede distinguirlos en el protocolo. Podemos tener dos métodos de igual nombre pero con diferente signatura, quizás disponibles en diferentes estados del protocolo, y Jatyc no tiene problema en procesarlos correctamente.
  - parameter_ensures_1
  - parameter_ensures_2
    - Estos tests intentan ver qué sucede si usamos la notación `@Ensures` (que predica sobre el estado de un objeto al terminar el método) en los parámetros de entrada de un método. Esto era de interés ya que en la documentación solo mencionaba que se podían aplicar a la variable de salida. Los tests demuestran que, de usar la palabra clave `final` de java, se puede utilizar la notación en parametros de entrada también.
  - immutable_list
    - En este ejemplo se muestra un poco las limitaciones de Jatyc a la hora de querer implementar algo como una lista immutable.
  - constructor_protocol
    - En este test se muestra como Jatyc no permite incluir el método constructor en el protocolo (y probablemente cualquier "class method" y no de "instance"). Esto serviría para, por ejemplo, según que constructor se usa cambiar el protocolo.
- **Alliasing**
  - En esta sección hablar sobre casos interesantes donde se pone a prueba el manejo de alliasing por parte de la herramienta, las limitaciones y poderes que tiene.
  - parameter_ensures_1
    - Este test muestra simplemente cómo al pasar un colaborador interno como parámetro a un método hace que se pierda la referencia lineal al mismo desde afuera. Esto es, siempre y cuando se siga la documentación de la herramienta (y el programador no haga algo similar a parameter_ensures_2).
  - parameter_ensures_3
    - En este ejemplo se muestra cómo maneja Jatyc se da cuenta que, al perderse la referencia lineal del objeto, se tiene que primero terminar el protocolo. De no ser asi, lanza un error. En este sentido, maneja bien el alliasing.
  - collaborator_compound_state_6
    - En este ejemplo, se devuelve con un getter el colaborador interno de un objeto padre, y Jatyc correctamente reconoce que el padre ya no puede llamar a métodos del colaborador interno que modifiquen su estado. En particular, dado que este ejemplo es entre un RobotController y un Robot, cuando el padre devuelve `this.robot` en un método getter, `this.robot` pasa a ser una shared reference.
- **Exceptions**
  - Probablemente pueda haber un capítulo sobre algún test con excepciones. Jatyc ya avisa que no tiene soporte a incluir transiciones de excepciones en sus protocoloes en su documentación, pero igual estaría interesante mencionarlo. También acá se puede mencionar el paper que leí al principio de la investigación de enabledness-based testing https://www.researchgate.net/publication/348188473_Enabledness-based_Testing_of_Object_Protocols. 