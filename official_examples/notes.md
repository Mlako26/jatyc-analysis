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

