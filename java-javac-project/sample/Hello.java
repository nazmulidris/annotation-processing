package sample;

import sample.annotation.MySampleAnnotation;

@MySampleAnnotation
public class Hello {

public static void main(String[] args) {
  Hello helloObject = new Hello();
  helloObject.saySomething();
}

public String getGreeting() {
  return "Hello";
}

public void saySomething() {
  System.out.println(getGreeting());
}

}