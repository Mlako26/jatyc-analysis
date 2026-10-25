## Tests

- [x] Hacer test donde tenes un obj protocolado dentro de otro, y el de afuera termina su protocolo. El de adentro lo puedo seguir utilizando? Que pasa con esa referencia?
- [x] Escribir descripcion de immutable list
- [x] Test donde supertipo tiene estados droppable, que subtype lo tenga tambien. 
- [x] Corregir esto, el problema son los argumentos mal tipados, https://github.com/Mlako26/jatyc-analysis/blob/main/tests/examples_by_tutor/ProtocolOutputStream.java#L20
- [x] Revisar en mi documentación qué otros tests mencioné que estaría bueno hacer.
  - [x] Test de metodo constructor incluirlo al protocolo
  - [x] Test con linearidad de parametros
  - [x] Test con typestate de parametros (@ensures en parametros anda?)

## Research

- [x] Checkear cómo usa el checker la tool.
  - [x] Revisar cómo funciona el checker
  - [x] Qué consultas concretas usa
  - [x] Qué rutinas agregaron al compilador para checkear lo que tienen que checkear

## Writing

- [x] Escribir introduccion
- [x] Agregar referencia de mungo https://www.dcs.gla.ac.uk/research/mungo/publication/pub3/ que no me carga ahora mismo
- [x] Agregar referencia del capitulo de la teoria, hay un comentario ahi mismo
- [x] Arreglar los listings pasandolos a Figure
- [ ] Arreglar indentación de código Java en el archivo JaTyC, ademas de registrar que el listing es de tipo language java
- [ ] Experimentacion
  - [x] 24: Introduccion
  - [ ] 24: Compound Protocols
  - [ ] 24: Method Sequences Vs Correctness
  - [ ] 25: Human Intervention
  - [ ] 26: Subtyping
  - [ ] 27: State Versus Typestates
  - [ ] 28: Modeling Capabilities
  - [ ] 29: Aliasing
  - [ ] 30: Exceptions
- [ ] How the tool works
  - [ ] 1,2: Escribir cómo funciona en el paper que hicieron
  - [ ] 3,4: Escribir cómo interactúa con el Checker Framework
- [ ] 5: Conclusion
- [ ] 5: Escribir los abstracts
- [ ] 5: Escribir agradecimientos
- [ ] 5: Escribir dedicatoria