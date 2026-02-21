public class Main {
  public static void main(String[] args) {
    Animal x = new Dog();
    x.move();
    Dog dog = (Dog) x; // Casting x to a variable such that a linear reference is maintained
    m1(x);
    LivingBeing x1 = x;
    dog.wag();
    x1.sound();
  }

  public static void m1(LivingBeing x) {
    if (x instanceof Dog) ((Dog) x).roll();
    else x.sound();
  }
}
