# JATYC Language

El objeto de estudio principal de este análisis es la herramienta **Java Typestate Checker**. La misma permite verificar código Java en forma estática utilizando typestates. A continuación se detallarán las diferentes funcionalidades que permite el lenguaje provisto por la herramienta, junto con sus asumpciones y comportamientos implícitos:

- Declarar diferentes estados y los métodos que se pueden llamar desde el mismo.
  - Incluye la declaración del tipo de retorno, de los parámetros, y el/los estado/s a los que puede pasar el objeto.
- Dado un estado y un método llamable desde el mismo, definir a qué otro estado (o estados posibles) va a pasar. Este estado resultante puede declararse usando el nombre de otro estado del protocolo, un estado anónimo, o una lista de pares clave-valor, donde las claves son los posibles resultados de un método y los valores el estado al que se pasaría. Vi ejemplos con booleanos y con enumeraciones. (Esto último creo que los xEPA no checkeaban, es decir se abstraen de los valores de los typestates de los argumentos y los resultados. Ahi quizás puedo hacer un poco de análisis de compatibilidad entre los xEPAs y las cosas que permite esta herramienta.)

A continuación hay un ejemplo de definición de un protocolo para un iterador, el cual cambia de estado según el resultado booleano de sus métodos:
```
typestate Iterator {
  HasNext = {
    boolean hasNext(): <true: Next, false: end>
  }
  Next = {
    boolean hasNext(): <true: Next, false: end>,
    Object next(): HasNext
  }
}
```

A su vez, en este siguiente ejemplo, se puede ver que además de booleanos funciona con enumeraciones (en este caso, `AUTO_DRIVE` y `MANUAL_DRIVE`):

```
typestate AutoDrivingCar {
  OFF = {
    boolean turnOn(): <true: MANUAL_ON, false: OFF>,
    drop: end
  }

  MANUAL_ON = {
    void turnOff(): OFF,
    void setSpeed(int): MANUAL_ON,
    Mode switchMode(): <AUTO_DRIVE: AUTO_ON, MANUAL_DRIVE: MANUAL_ON>
  }

  AUTO_ON = {
    void turnOff(): OFF,
    void setSpeed(int): AUTO_ON,
    Mode switchMode(): <AUTO_DRIVE: AUTO_ON, MANUAL_DRIVE: MANUAL_ON>,
    void autoPark(): AUTO_ON
  }
}
```

- (TESTING REQUIRED) Permite suscribirse a varios protocolos? (declarar `@Typestate("...")` para una misma clase)
  - Mirando la [documentación](https://github.com/jdmota/java-typestate-checker/wiki/Documentation#introduction) a priori parecería que la múltiple herencia termina en comportamiento indefinido: `No support for interface multiple inheritance (using it is undefined behavior);`. 
- Si hay métodos no especificados por el protocolo, o si hay una clase que no está suscrita a ningún protocolo, estos pueden llamarse en cualquier estado del mismo pero no pueden cambiar el estado interno del objeto (e.g. colaboradores internos). Estos son referenciados como **anytime methods**.
- Trata los objetos que siguen protocolos como **lineales**, y los que no como **compartidos**. 
  - El concepto de linealidad viene de que hay una sola referencia a un objeto (es decir una variable) que permite realizarle modificaciones a su estado.
  - Una misma instancia de una clase puede tener varias referencias, pero solo una sola lineal y todas las demás compartidas.
  - Al crear una copia de una referencia, ya sea a una variable nueva o como parámetro de una llamada, la variable nueva será la lineal y la vieja pasa a ser compartida.
    - (TESTING REQUIRED) Comprobar que hacer una llamada a un método con una referencia lineal hace que la misma pase a ser compartida.
  - En una referencia compartida, solo los **anytime methods** pueden ser llamados.


- Permite especificar el o los estados de los objetos que son parámetros para un método con el `@requires`.
  - De no utilizarse, la herramienta asume que el objeto es 

```java
void readFile(@Requires("Open") File file) {
  file.read();
  file.close();
}
```

- Permite especificar el o los estados del objeto retornado por el método con un `@ensures`.

```java
@Ensures("Open") File newFile() {
  File file = new File();
  file.open();
  return file;
}
```

- El lenguaje asume por defecto que ningún objeto es nullable. En caso de decidir lo contrario, se puede declarar lo mismo con la anotación `@Nullable`. La misma puede ser utilizada en valores de retorno, parámetros, y cualquier tipo en una declaración de variable.

```java
@Nullable @Ensures("Open") File tryOpening() {
  File file = new File();
  return file.open() ? file : null;
}
```

```java
private @Nullable AutoDriveAI driver;
```

- Asume que los valores de retorno de métodos de librerías externas son `@Nullable`, ya que es incapaz de predicar sobre ellas.
- (TESTING REQUIRED) El lenguaje solo permite predicar sobre el valor de retorno de los métodos con el `@Ensures`, pero no sobre los colaboradores internos del suscriptor al protocolo ni los parámetros de entrada.
- Los objetos siempre tienen un estado final, y la herramienta asegura que los objetos lleguen al mismo. Este estado tiene nombre reservado `end`, y se debe de especificar explícitamente en el protocolo cómo llegar a él:

Por ejemplo, a través de una llamada a método:
```
typestate Iterator {
  HasNext = {
    boolean hasNext(): <true: Next, false: end>
  }
  Next = {
    boolean hasNext(): <true: Next, false: end>,
    Object next(): HasNext
  }
}
```

También puede hacerse a través de la transición especial `drop`, la cual declara que el objeto pasa automáticamente al estado `end` en caso de dejar de ser usado. En:

```
typestate Iterator {
  HasNext = {
    boolean hasNext(): <true: Next, false: end>,
    drop: end
  }
  Next = {
    boolean hasNext(): <true: Next, false: end>,
    Object next(): HasNext,
    drop: end
  }
}
```

- (TESTING REQUIRED) Permite herencia en protocolos y subtyping. En particular, la herramienta se asegura de que el protocolo hijo sea un subtipo del protocolo padre. Más explicación sobre esto puede encontrarse en [sección de subtyping de la documentación](https://github.com/jdmota/java-typestate-checker/wiki/Documentation#subtyping).

```
typestate Animal {
  Init = {
    void move(): Moved,
    drop: end
  }

  Moved = {
    void eat(): Init
  }
}
```

```
typestate Dog {
  Init = {
    void move(): Moved,
    drop: end
  }

  Moved = {
    void wag(): end,
    void eat(): Init
  }
}
```

- De ser subclase de un padre sin protocolo, todos los métodos del padre se consideran **anytime methods**.
- Tiene soporte para casteos de variables.
- Permite ignorar errores o warnings generados por la herramienta con `@SuppressWarnings("all")`.

```java
public class Tasks {
  @SuppressWarnings("all")
  private Object makeDescription() {
    return null;
  }
}
```

- El lenguaje no predica sobre Excepciones. Es decir, no hay sintaxis dentro de la herramienta que permita especificar si llamar un método en cierto estado debe de resultar en una excepción.
- El lenguaje no predica sobre generics (se los asume nullables y compartidos por defecto)
- 