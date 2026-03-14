# Four Dark Corners of Object Protocols papaer

These are some test cases ideas or things to read more on that came up while reading the aforementioned paper:

- Hacer un ejemplo donde el estado de un objeto en realidad es un estado conjunto con un colaborador interno. Entonces, llamadas al mismo método desde el mismo estado generen distintos resultados, de alguna forma donde se genere una excepción donde no debería.
- Hacer el ejemplo del stack que está descrito en la versión 2.1 del paper. Es decir, de querer programar el stack sin problema siguiendo el protocolo entonces si, se necesita un autómata de pila para modelar la cantidad de objetos pusheados, pero de modificar el código haciéndolo más estricto y explícitamente checkeando si el stack está vacío antes de popear, seguiria el protocolo de jatyc.
- Recomiendo leer la referencia 13 que habla sobre typestates
- Ejemplo donde el protocolo es muy restrictivo. Ejemplo, tener un filedescriptor ya abierto y que el objeto que lo usa no requiera usar el metodo open().
- Yo diria que los protocolos, si es que pasa todos los ejemplos como queremos, son casi igual de expresivos que pre y post condiciones (para modelar secuencias de llamados), siempre y cuando permitamos hacer más restrictivo el código que se puede escribir.
- Recomendaria leer referencia 6 sobre alternativa a protocolos con typestates.
- En 2.2 habla sobre los problemas de los abstract subtypes. Hacer un ejemplo con los Iterator y ListIterator, junto a sus protocolos, para mostrar que se puede usar herencia de protocolos con la tool. Leer sobre ListIterator y su protocolo oficial a ver si puede ser un sub-protocolo.
- En 2.2 habla sobre un ejemplo de un implementador de Iterator que suma el metodo reset. Como el mismo un  el protocolo del Iterator, que no incluye al reset, el mismo sería un anytime method. Entonces, de llamarlo no debería de ser capaz de modificar el estado de una instancia de la clase. Estaria bueno hacer un ejemplo de esto. Modificar el estado de una clase == modificar el typestate?
- Que pasa si hago un iterador circular, el cual la finalizar la colleción resetea el iterador y por ende hasNext siempre retorna true?
- Hacer un ejemplo de un stack donde su pila es un colaborador interno publico, lo puedo modificar desde afuera y el mismo sabra su estado?
- Hacer un ejemplo donde haya un proxy y que mi "protocolo" sea completamente dependiente del protocolo de mi colaborador interno. Ej, hacer un wrapper de iterador LoggerIterator, que itera y loggea, pero sin protocolo. Que sucede?
- Decorator pattern example:  buscar ejemplos simples del patron de decorator y ver que onda. La idea es buscar un ejemplo donde el protocolo para usar apropiadamente un decorador dependa del objeto que estamos decorando (su contexto?).
- Hacer algun ejemplo donde el estado final de un objeto al salir del constructor varie. Por ejemplo, una excepcion donde la inicializamos con el cause o no.
- Hacer un ejemplo donde una excepcion siempre inicialize el cause, y una sublclase que sobrecargue el metodo. Pasara que rompe el protocolo? La herramienta maneja bien esto?
- De la seccion 2.4, ver que onda el ejemplo del java.math.BigInteger. Es decir, ver el tema de objetos inmutables.
- Con el tema de las factories, no pasa nada en jatyc. Esto es porque subclasses de clases con PROTOCOLO deben si o si implementar su protocolo. Incluso si implementan un segundo protocolo, el mismo debe de ser un subprotocolo.
- Ver el ejemplo de java util list de la seccion 2.4.
- Ejemplo: Ponele que tengo una factory que devuleve una implementacion particular de una interfaz con protocolo. Estas subclases tienen distrintos subprotocolos. La tool revisa que el subprotocolo se respete? Y que el protocolo grande?
- Pensar algun ejemplo relacionado a assertion checking. Es mas poderoso el protoclo que hacer laguna espcie de assercion al comineo de los metodos? Pareceria que si.
- Se te ocurre algun ejemplo donde el protocolosea mejor que la pre o post condicion? En un principio pareceria que la pre y post condicion es un protocolo en esteroides, donde a pesar de que no define cadena de metodos, define que es necesario para poder utilizar a uno correctamente. Hay algun cas donde necesitar definir los typestates de un objeto? Mirar articulo donde menciona las partes malas de la pre y post condicion
