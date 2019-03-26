package com.demo;


import com.demo.common.annotation.AutoLogMethod;
import lombok.Data;

/**
 * @author feng.yang
 * @date 2019-01-11
 */
@Data
public class Person {
  private String name;

  private Integer age;

  @AutoLogMethod
  public void say(String word) {
    System.out.printf("%s is saying %s%n", name, word);
  }
}
