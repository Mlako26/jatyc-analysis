## Tests

- [x] Hacer test donde tenes un obj protocolado dentro de otro, y el de afuera termina su protocolo. El de adentro lo puedo seguir utilizando? Que pasa con esa referencia?
- [x] Escribir descripcion de immutable list
- [x] Test donde supertipo tiene estados droppable, que subtype lo tenga tambien. 
- [x] Corregir esto, el problema son los argumentos mal tipados, https://github.com/Mlako26/jatyc-analysis/blob/main/tests/examples_by_tutor/ProtocolOutputStream.java#L20
- [ ] Revisar en mi documentación qué otros tests mencioné que estaría bueno hacer.
  - [ ] Test de metodo constructor incluirlo al protocolo
  - [ ] Test con linearidad de parametros
  - [ ] Test con typestate de parametros (@ensures en parametros anda?)
  - [ ] Test con ensures/requires con colaboradores internos

## Research

- [ ] Checkear cómo usa el checker la tool.
  - [ ] Revisar cómo funciona el checker
  - [ ] Qué consultas concretas usa
  - [ ] Qué rutinas agregaron al compilador para checkear lo que tienen que checkear

## Writing

- [ ] Jatyc segunda versión
  - [ ] Explicar que existe una segunda versión de Jatyc en una branch que intenta taclear el problema de la linearidad
  - [ ] Explicar un poco cómo funciona
  - [ ] Explicar que Jatyc actualmente no se banca el alliasing si no es de forma lineal (solo una referencia puede modificar el estado de un objeto a la vez)
- [ ] Jatyc funcionalidad
  - [ ] Explicar que si no puede probar que sos seguro, te fuerza a hacer checkeos para que estes seguro. En ese sentido, son falsos positivos, donde potencialmente el codigo esta bien pero te obliga a ser mas defensivo. 
  - [ ] Explicar que la escencia de lo que se esta testeando (tests de profe), es que no se puede hacer un next si ya llegaste al final. Esto no se puede hacer sin pila
- [ ] En la sección de theoretical framework, hablar de cómo se usa el framework de checker jatyc, y cómo usa para checkear los typestates.
