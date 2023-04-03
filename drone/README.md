## Drones

### Description



### Instruction

The service was built and tested in the following environment:
- OpenJDK 17.0.4
- Gradle 7.2

Before building and using the service JDK and Gradle must be installed and configured properly 
(the configuration of java dns gradle is out of the scope of this instruction)

### Build and test
1. Chose directory: 
drone/

2. run command:

2.1. For Linux:
./gradlew build

2.1. For Windows:
gradlew.bat build

3. Unit-tests are done automatically withing the building. 
If necessary, the tests can be run separately with the next command

3.1. For Linux:
./gradlew test

3.2. For Windows:
gradlew.bat test

### Start
After the building, start the service using command:

- For Linux:
./gradlew bootRun

- For Windows:
gradlew.bat bootRun 

A Spring application must be started; If started successfully, the application logs the next messages: 
... Tomcat started on port(s): 8080 (http) with context path ''
... Started DroneApplication in 3.334 seconds (process running for 3.887)

### Demonstrate
After a successful start the main feature of the service could be represented by the following commands. 

