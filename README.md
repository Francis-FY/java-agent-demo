[![Build Status](https://travis-ci.org/Francis-FY/java-agent-demo.svg?branch=master)](https://travis-ci.org/Francis-FY/java-agent-demo)
# java-agent-demo
A simple java agent demo project

# run demo app
1. Packaging module agent(`mvn clean package -pl agent -am -Dmaven.test.skip=true`) and you'll get a jar file(eg. agent.jar)
2. Compiling mudule application, run the main method with VM option `-javaagent:xxx(path to agent jar file)\agent.jar`, you'll see some log of method invocation with annotation `AutoLogMethod` present.
