# Official Examples Notes

I will include some analysis on how the tool can be used based off of the examples that I find the most interesting.

## car-example

### car-example2

Este ejemplo es interesante en el hecho de que falla en seguir correctamente el protocolo de la subclase. Es decir, no checkea que el modo sea automatico antes de ejecutar el método autoPark().

De todos modos, no tengo del todo claro cómo hacer que el subprotocolo me deje hacer el autoPark(). Un codigo simple como:

```java
public static void main(String[] args) {
    AutoDrivingCar car = new AutoDrivingCar();
    if (!car.turnOn()) {
        System.out.println("Damn thing won't start!");
        return;
    };

    Mode mode = car.switchMode();
    while (mode != Mode.AUTO_DRIVE) {
        car.switchMode();
    }
    car.autoPark();
    car.turnOff();  
    System.out.println("Done!");
}
```

Resulta en el error:

```
ClientCode.java:30: error: Cannot call [autoPark] on State{AutoDrivingCar, MANUAL_ON}
    car.autoPark();
```

Donde cláramente parecería que el analizador de typestates no registra (o conceptualmente el test está mal) que el auto no puede salir del while sin estar en modo automático.

Otro test donde forzamos la condición en un if pareceria funcionar:

```java
public static void main(String[] args) {
    AutoDrivingCar car = new AutoDrivingCar();
    if (!car.turnOn()) {
        System.out.println("Damn thing won't start!");
        return;
    };

    if (car.switchMode() == Mode.AUTO_DRIVE) {
        car.autoPark();
    }
    // setSpeedAndPark(car);
    car.turnOff();
    System.out.println("Done!");
}
```

De todos modos, en caso de recibir el `AutoDrivingCar` como parámetro y no saber su modo actual, deberíamos de ser capaces de distinguir entre ambos modos en forma cómoda (es decir, este ejemplo funciona solamente porque ya sabía que el auto comienza encendido en modo manual).

De intentar sacar la variable que devuelve el método switchMode(), resulta en un error. Parecería que de no estar el método explícitamente mencionado en el protocolo en la guarda del if el parser no reacciona bien:

```java
Mode mode = car.switchMode();
if (mode == Mode.AUTO_DRIVE) {
    car.autoPark();
}
```

```
ClientCode.java:27: error: Cannot call [autoPark] on State{AutoDrivingCar, MANUAL_ON}
      car.autoPark();
```

Utilizando este nuevo hallazgo, proporcionandole al while directamente el método del objeto parece haber funcionado:

```java
public static void main(String[] args) {
    AutoDrivingCar car = new AutoDrivingCar();
    while (!car.turnOn()) { System.out.println("turning on..."); }
    while (car.switchMode() != Mode.AUTO_DRIVE) { System.out.println("Switching mode..."); }
    car.autoPark();
    car.turnOff();
    System.out.println("Done!");
}
```

### car-example

En este ejemplo, sucede algo similar donde a pesar de que el protocolo se cumple, tenemos el siguiente método que se llama desde main:

```java
private static void setSpeed(@jatyc.lib.Requires("ON") Car car) {
    if (car instanceof SUV && ((SUV) car).switchMode() == Mode.SPORT)
        ((SUV) car).setFourWheels(true);
    car.setSpeed(50);
    car.turnOff();
}
```

Observemos que el método `setFourWheels()` solo puede accederse en el modo SPORT. De todos modos, en la entrada de este método asumimos que tenemos una instancia de la clase Car de input en el estado `"ON"`, y desconocemos si su subestado en caso de ser SUV es el modo sport o comfort. De querer redefinir este método para que siempre active el fourwheel drive, y en su caso pasar el auto al estado sport, tanto este como el ejemplo anterior de `car-example2` no nos servirían.

Voy a seguir observando los ejemplos en caso de que alguno sea útil para resolver esta clase de problemas de diseño. Quizás podría resolverse con alguna secuencia específica de condicionales o agregando algún método nuevo a las clases/protocolos para cambiar a un modo en específico o checkear modo.

## drone-example

### drone-example

En este ejemplo, se intenta modelar una flota de drones que realizan tareas. La clase `Drone` sigue un protocolo muy simple donde puede pasar de IDLE a HOVERING y al estar flotando puede cambiar su ubicación o tomar fotos. Los drones se organizan cada uno dentro de un `DroneNode`, los cuales sirven como especie de lista enlazada. Por último, un `DroneGroup` tiene de colaborador interno la lista enlazada a la que se le pueden agregar drones y usarlos uno a la vez.

Sigue el protocolo sin problemas.

### drone-example2

Al ejemplo anterior se le suma una clase nueva `PendingDrone`, actuando como un wrapper de `Drone` que está ya flotando. El mismo comienza en el estado `NO_TASK_HOVERING`, y se le puede asignar tareas.

En particular, se le puede preguntar si el drone esta en estado `HOVERING`, y por ende no tiene ninguna tarea asignada, o `FLYING`, y está cambiando relizando una tarea.

```
typestate PendingDroneProtocol {

  NO_TASK_HOVERING = {
    Drone takeHoveringDrone(): NO_TASK,
    boolean completed(): <true: NO_TASK_HOVERING, false: WITH_TASK_FLYING>
  }

  NO_TASK = {
    void setTask(Drone, DroneTask): WITH_TASK_FLYING,
    void finishTask(Drone): NO_TASK_HOVERING,
    drop: end
  }

  WITH_TASK_FLYING = {
    Drone takeFlyingDrone(): WITH_TASK,
    boolean completed(): <true: NO_TASK_HOVERING, false: WITH_TASK_FLYING>
  }

  WITH_TASK = {
    DroneTask getTask(): WITH_TASK,
    void continueTask(Drone): WITH_TASK_FLYING,
    void finishTask(Drone): NO_TASK_HOVERING,
    drop: end
  }

}
```

Como se puede observar, de preguntar en el estado inicial el estado del drone a través del método `completed()`, se puede pedir el hoveringDrone (`takeHoveringDrone()`) o el flyingDrone (`takeFlyingDrone()`) según el booleano que retorne.

```java
public @Ensures("HOVERING") Drone takeHoveringDrone() {
    return this.drone;
}

public @Ensures("FLYING") Drone takeFlyingDrone() {
    return this.drone;
}
```

El código del módulo cumple todos los protocolos.

### drone-example3

Este ejemplo es el que falla el protocolo. En particular, lo único relevante que cambia es la implementación del método `completed()` dentro de `PendingDrone`, donde devuelve los resultados al revés:

```java
public boolean completed() {
    return this.task != null; // Wrong test
}
```

```
PendingDrone.java:41: error: Incompatible return value: cannot cast from State{Drone, FLYING} | State{Drone, HOVERING} to State{Drone, FLYING}
    return this.drone;
    ^
PendingDrone.java:37: error: Incompatible return value: cannot cast from State{Drone, FLYING} | State{Drone, HOVERING} to State{Drone, HOVERING}
    return this.drone;
    ^
2 errors
```

Es decir, reconoce que no puede asegurar que el drone que se esté devolviendo por los métodos take estén en los estados correctos.

## living-being-example

En este ejemplo, se intenta modelar a los seres vivos a través de una superclase `LivingBeing`, la cual NO cuenta con protocolo. De ella desprende la subclase `Animal`, la cual cuenta con protocolo, y de esta última la subclase `Dog`, también contando con su propio protocolo. Ya vimos en ejemplos anteriores herencia de protocolos, pero en este ejemplo tenemos una clase con protocolo que extiende a otra que no tiene uno.De intentar compilar main, nos salta el siguiente error:

```java
public class Main {
    public static void main(String[] args) {
        Animal x = new Dog();
        x.move();
        m1(x);
        LivingBeing x1 = x;
        ((Dog) x1).wag();
        x1.sound();
    }

    public static void m1(LivingBeing x) {
        if (x instanceof Dog) ((Dog) x).roll();
        else x.sound();
    }
}
```

```
Main.java:5: error: Incompatible parameter: cannot cast from State{Animal, Moved} to Shared{LivingBeing}
    m1(x);
       ^
1 error
```

Mirando la documentación de la herramienta, podemos notar lo siguiente:

[Subtyping](https://github.com/jdmota/java-typestate-checker/wiki/Documentation#subtyping)

```
One can also create a class with protocol that extends a class without protocol. In the class without protocol, all the methods are available to be called and need to remain so in the subclass, which means that they cannot be mentioned in the protocol of the subclass. In other words, "anytime" methods should remain "anytime" in subclasses.
```

Es decir, desde la clase `Dog` se deben de poder llamar todos los métodos de `LivingBeing` sin importar el estado de la instancia.

[Casts](https://github.com/jdmota/java-typestate-checker/wiki/Documentation#casts)

```
If the method has no @Requires annotation, it expects either an aliased reference or a reference to an object that is in the end state (notice that a reference to an object that has completed its protocol can be seen as an aliased reference). You can turn a linear reference into a shared (aliased) reference by assigning it to a different variable which will get the linear reference, while the old one becomes shared. The reason why one cannot just pass a linear reference to a parameter without @Requires annotation is that one would not be able to finish the protocol. In this example one must, at first, put the object b into another variable a and then pass b to the method. Below you can find an example:
```

```java
public static void main() {
  B b = new B();
  A a = b; // b becomes shared
  a.m1();
  playA(b);
}

public static void playA(A a) {...}
```

Es decir, la razón por la que falla este ejemplo es porque le estamos pasando un objeto con estado a un método que espera una referencia con alias (es decir, una referencia al objeto el cual no puede cambiar de estado y con métodos limitados). Arreglándolo igual que en la documentación nos da otro error:

```java
public static void main(String[] args) {
    Animal x = new Dog();
    x.move();
    LivingBeing dog = x; // Make x an alias before calling the method
    m1(x);
    LivingBeing x1 = dog;
    ((Dog) x1).wag();
    x1.sound();
}
```

```
Main.java:5: error: Cannot assign: cannot cast from State{Animal, Moved} to Shared{LivingBeing} | Null
    LivingBeing dog = x;
                ^
1 error
```

Parecería que hacer el casting explícito tampoco le gusta. Mirando más detenidamente la documentación, `You can turn a linear reference into a shared (aliased) reference by assigning it to a different variable which will get the linear reference, while the old one becomes shared`. Es decir, no puedo castear la referencia lineal `x` a una instancia de `LivingBeing`, ya que al no tener protocolo solamente puede ser shared. Por lo tanto, ni las variables `x` ni `dog` tendrían la referencia lineal. Vamos a probar lo mismo pero casteando `x` tal que la nueva variable sea una referencia lineal:

```java
public static void main(String[] args) {
    Animal x = new Dog();
    x.move();
    Animal dog = x; // Casting x to another Animal variable, such that a linear reference is maintained
    m1(x);
    LivingBeing x1 = x; // Using x (and not dog) because otherwise we would have the same problem
    ((Dog) x1).wag();
    x1.sound();
}
```

Eso nuevamente nos da dos errores:

- No se puede llamar `wag()` desde una variable que no sea referencia lineal
- La variable `dog`, que es una referencia lineal, no terminó su protocolo.

```
C:\Users\mlako\OneDrive\Desktop\Stuff\Facultad\tesis\jatyc-analysis\official_examples\living-being-example>java -jar ../../dist/checker/dist/checker.jar -classpath ../../dist/jatyc.jar -processor jatyc.JavaTypestateChecker *.java
Main.java:8: error: Cannot call [wag] on Shared{Dog}
    ((Dog) x1).wag();
                  ^
Main.java:2: error: [dog] did not complete its protocol (found: State{Animal, Moved})
  public static void main(String[] args) {
                     ^
2 errors
```

Versión final:

```java
public static void main(String[] args) {
    Animal x = new Dog();
    x.move();
    Dog dog = (Dog) x; // Casting x to a variable such that a linear reference is maintained
    m1(x);
    LivingBeing x1 = x;
    dog.wag();
    x1.sound();
}
```

## removable-iterator-with-init

Este ejemplo es uno que no entiendo. Se trata de unos iteradores que, luego de ser construidos con sus colaboradores internos en null, debe de llamarse a un método de inicialización:

```java
public class RemovableIterator extends BaseIterator {
    protected @Nullable List<Object> items;

public RemovableIterator() {
    super();
    this.items = null;
}

public void init(String[] items) {
    this.items = Util.toList(items);
}

(...)
```

El protocolo mismo enuncia que el estado inicial es el de init, donde solo el método init puede llamarse, y a partir de ahi la funcionalidad normal del iterador puede accederse:

```
typestate RemovableIterator {
  Init = {
    void init(String[]): HasNext
  }
  HasNext = {
    boolean hasNext(): <true: Next, false: end>
  }
  Next = {
    Object next(): Remove
  }
  Remove = {
    boolean hasNext(): <true: Next, false: end>,
    void remove(): HasNext
  }
}
```

En la misma documentación de la herramienta dice lo siguiente:

[Nullness Checking](https://github.com/jdmota/java-typestate-checker/wiki/Documentation#nullness-checking)

```
Furthermore, if a class has a protocol, we use that information to know which methods are called before other ones. This allows us to avoid reporting nullness errors if we know that an initializing method was necessarily called before another one that reads from a given field.
```

De todos modos, al intenar compilar el código tenemos lo siguiente:

```
RemovableIterator.java:23: error: Cannot call [get] on null (found: Shared{java.util.List} | Null)
    return this.items.get(this.index++);
                     ^
RemovableIterator.java:27: error: Cannot call [remove] on null (found: Shared{java.util.List} | Null)
    this.items.remove(--this.index);
              ^
RemovableIterator.java:19: error: Cannot call [size] on null (found: Shared{java.util.List} | Null)
    return this.index < this.items.size();
```

Parecería que no se está correctamente viendo que el primer paso del protocolo debería de quitarle el nullness a los colaboradores internos, pero quizás hay que reforzarlo en el lado del código.

Agregando condicionales checkeando nullnes en cada método parece funcionar, pero no es super lindo. Estaría bueno agregarle al protocolo alguna especie de herramienta para poder asegurar el estado de tu propio colaborador interno.

```java
public class RemovableIterator extends BaseIterator {
  protected @Nullable List<Object> items;

  public RemovableIterator() {
    super();
    this.items = null;
  }

  public void init(String[] items) {
    this.items = Util.toList(items);
  }

  public boolean hasNext() {
    if (this.items != null) {
      return this.index < this.items.size();
    }
    throw new RuntimeException();
  }

  public @Nullable Object next() {
    if (this.items != null) {
      return this.items.get(this.index++);
    }
    throw new RuntimeException();
  }

  public void remove() {
    if (this.items != null) {
      this.items.remove(--this.index);
    }
    throw new RuntimeException();
  }
}
```

### IDEA DE EJEMPLO

Tener un Objeto con un Objeto con protocolo dentro. Verifica correctamente el protocolo? Hay que checkear si esto ya está ejemplificado en algún lugar.

