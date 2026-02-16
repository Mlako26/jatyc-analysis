import jatyc.lib.Requires;

public class ClientCode {
  // Original code that does not work
  // public static void main(String[] args) {
  //   AutoDrivingCar car = new AutoDrivingCar();
  //   while (!car.turnOn()) { System.out.println("turning on..."); }
  //   setSpeedAndPark(car);
  //   System.out.println("Done!");
  // }
  // private static void setSpeedAndPark(@Requires("ON") Car c) {
  //   c.setSpeed(50);
  //   if (c instanceof AutoDrivingCar){
  //     ((AutoDrivingCar) c).autoPark(); // Cannot call [autoPark] on State{AutoDrivingCar, MANUAL_ON}
  //   }
  //   c.turnOff();
  // }
  public static void main(String[] args) {
    AutoDrivingCar car = new AutoDrivingCar();
    if (!car.turnOn()) {
      System.out.println("Damn thing won't start!");
      return;
    };
    
    Mode mode = car.switchMode();
    if (mode == Mode.AUTO_DRIVE) {
      car.autoPark();
    }
    car.turnOff();
    System.out.println("Done!");
  }
  // private static void setSpeedAndPark(@Requires("ON") Car c) {
  //   c.setSpeed(50);
  //   if (c instanceof AutoDrivingCar){
  //     ((AutoDrivingCar) c).autoPark(); // Cannot call [autoPark] on State{AutoDrivingCar, MANUAL_ON}
  //   }
  //   c.turnOff();
  // }
}
