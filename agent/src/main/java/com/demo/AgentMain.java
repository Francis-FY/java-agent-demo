package com.demo;

import java.io.ByteArrayInputStream;
import java.lang.instrument.Instrumentation;

import com.demo.common.annotation.AutoLogMethod;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * @author feng.yang
 * @date 2019-01-24
 */
@Slf4j
public class AgentMain {
  public static void premain(String agentArgs, Instrumentation instrumentation) {
    log.info("Agent Args:{}", agentArgs);

    instrumentation.addTransformer(
        (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
          //          log.info("premain load class:{}", className);
          ClassPool pool = ClassPool.getDefault();
          try {
            if (className.startsWith("com/demo")) {
              CtClass ctClass = pool.makeClass(new ByteArrayInputStream(classfileBuffer));
              log.info("transforming class {}", ctClass.getName());
              CtField ctField =
                  CtField.make(
                      String.format(
                          "private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(%s.class);",
                          ctClass.getName()),
                      ctClass);
              ctClass.addField(ctField);

              CtMethod[] methods = ctClass.getDeclaredMethods();
              for (CtMethod method : methods) {
                if (method.hasAnnotation(AutoLogMethod.class)) {
                  MethodInfo methodInfo = method.getMethodInfo();
                  CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
                  LocalVariableAttribute attribute =
                      (LocalVariableAttribute)
                          codeAttribute.getAttribute(LocalVariableAttribute.tag);
                  CtClass[] types = method.getParameterTypes();
                  StringBuilder code = new StringBuilder("logger.info(\"executing method ");
                  code.append(method.getName()).append("(");

                  int paramInitPos = Modifier.isStatic(method.getModifiers()) ? 0 : 1;
                  for (int i = 0; i < types.length; i++) {
                    CtClass type = types[i];
                    if (!type.isPrimitive()) {}

                    String name = attribute.variableName(i + paramInitPos);
                    code.append(String.format("%s: \"+$%d", name, i + 1));
                    if (i != types.length - 1) {
                      code.append("+\",");
                    }
                  }
                  code.append("+\")\");");
                  method.insertAt(-1, code.toString());

                  if (method.getReturnType() != CtClass.voidType) {
                    method.insertAfter(
                        "logger.info(\"method " + method.getName() + " return:{}\",$_);");
                  }
                }
              }
              return ctClass.toBytecode();
            }
          } catch (Exception e) {
            log.error("JavaAssist Exception", e);
          }
          return classfileBuffer;
        });
  }
}
