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

- [ ] Jatyc segunda versión
  - [ ] Explicar que existe una segunda versión de Jatyc en una branch que intenta taclear el problema de la linearidad
  - [ ] Explicar un poco cómo funciona
  - [ ] Explicar que Jatyc actualmente no se banca el alliasing si no es de forma lineal (solo una referencia puede modificar el estado de un objeto a la vez)
- [ ] Escribir los abstracts
- [ ] Escribir introduccion
