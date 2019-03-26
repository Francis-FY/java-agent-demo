package com.demo;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.demo.common.annotation.AutoLogMethod;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;

/**
 * @author feng.yang
 * @date 2019-01-11
 */
public class JavassistDemo {

  /**
   * Add VM Option Before Launch：-javaagent:path to agent jar(xxx\agent.jar)
   *
   * @param args
   */
  public static void main(String[] args) {
    greeting("Francis");
    greeting("Francis");
    Person francis = mockPerson("Francis", 12);
    francis.say("Hello World!");
    testParam("a1", "a2", 3, francis, "1", "2", "3", "4", "5", "6");
  }

  private static void generateClass()
      throws CannotCompileException, IllegalAccessException, InstantiationException,
          NoSuchMethodException, InvocationTargetException, IOException {
    ClassPool cp = ClassPool.getDefault();
    CtClass dogClass = cp.makeClass("com.demo.Dog");

    // Field
    CtField name = CtField.make("private String name;", dogClass);
    dogClass.addField(name);
    CtField age = CtField.make("private Integer age;", dogClass);
    dogClass.addField(age);

    // Constructor
    CtConstructor constructor =
        CtNewConstructor.make("public Dog(String name, Integer age){} ", dogClass);
    constructor.setBody("{this.name = $1;\n this.age = $2;}");
    dogClass.addConstructor(constructor);

    // method
    CtMethod hello = CtNewMethod.make("public void sayHello(String name, String msg){}", dogClass);
    hello.setBody(
        "System.out.println(\"Hello, \"+$1+\", My Name is \" + this.name + \", your message is: \"+$2);");
    dogClass.addMethod(hello);

    Class<?> aClass = dogClass.toClass();
    Constructor dogConstruct = aClass.getDeclaredConstructor(String.class, Integer.class);
    Object teddy = dogConstruct.newInstance("Teddy", 2);
    Method sayHello = aClass.getDeclaredMethod("sayHello", String.class, String.class);
    sayHello.invoke(teddy, "Francis", "You are soooooo cute!");

    dogClass.writeFile("./generated");
  }

  private static void getMethod() throws NotFoundException {
    ClassPool cp = ClassPool.getDefault();
    CtClass ctClass = cp.get("com.demo.Person");

    CtMethod say = ctClass.getDeclaredMethod("say2");
  }

  @AutoLogMethod
  private static String greeting(String name) {
    return String.format("Hello,%s!", name);
  }

  @AutoLogMethod
  public static Person mockPerson(String name, Integer age) {
    Person person = new Person();
    person.setName(name);
    person.setAge(age);
    return person;
  }

  @AutoLogMethod
  public static void testParam(String a1, String a2, Integer a3, Person a4, String... a5) {}
}
