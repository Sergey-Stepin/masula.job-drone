## Drones

The service was designed and implemented according to the requirements provided in file drone/TASK.md

### Description

Drones are registered and stored in the service database along with the medications lists which they load and deliver.

Each list of medications for a delivery is packed in a load which is passed to the service, where it is validated and stored.

The load is checked against the following rules:
- whether the total weight of the load is bearable for the drone
- whether the drone is in available state (free to be loaded)
- whether the drone has already a created load to carry 
- whether the drone's battery level is high enough (25% minimum)
- 
Each medication is validated according to the requirements in TASK.md

When the load is validated it is persisted in database together with the list of the medications.
A persisted load has the following attributes (apart from the list of the medications):
- loadId (unique key)
- createdAt
- loadedAt
- unloadedAt
- droneId

After that, the drone is supposed to physically load and deliver the medicines, exchanging the information with the service, 
pertaining its state, time of physical loading/unloading, etc.
(the communication with drones is out of the scope of this project).
Clients can obtain the respective information via the service. 

Periodically, in a separate thread, the service requests battery levels of the registered drones.
The period of the requesting task is set as a parameter <drone.battery-level.minimum-for-load> 
(see file src/main/resources/application.yml)
The received battery values are logged in a rotating log-file logs/drone-monitor.log

The communication with drones is mocked with a class MockDroneCommunicator.java.
It imitates the communication with a drone, acquiring its battery level.
The mock just returns the battery level saved in database at the time of the registration

### Instruction

The service was built and tested in the following environment:
- OpenJDK 17.0.4
- Gradle 7.2

Before building and using the service, JDK and Gradle must be installed and configured properly 
(the configuration of java and gradle is out of the scope of this instruction)

#### Build and test
1. Chose directory: 
drone/

2. run command:

a) For Linux:
./gradlew build

b) For Windows:
gradlew.bat build

3. Unit-tests are done automatically together with the building task
If necessary, the tests can be run separately with the next command

a) For Linux:
./gradlew test

b) For Windows:
gradlew.bat test

#### Start
After finishing the building, start the service using gradle command:

- For Linux:
./gradlew bootRun

- For Windows:
gradlew.bat bootRun

Alternatively, if successfully built, the service can be started without gradle, using pure java command:
java -jar build/libs/drone-0.0.1-SNAPSHOT.jar

There must start a Spring application; If started successfully, the application logs the next messages: 
... Tomcat started on port(s): 8080 (http) with context path ...
... Started DroneApplication in ...

#### Demonstration
After a successful start the main features of the service could be presented by the following commands (run from the direactory /drone):

Register a drone:  
curl http://localhost:8080/register -X POST -H "Content-Type: application/json" -d @src/test/resources/json/drone_d4.json  
(response: the drone with created droneId)

Check all registered drones:  
curl http://localhost:8080/list  
(response: a list of drones, including the new one)  

Try to make a load for drone#1 (droneId=1):  
curl http://localhost:8080/load?droneId=1 -X POST -H "Content-Type: application/json" -d @src/test/resources/json/load_1.json  
(response: 400-BadRequest,   
with message: (droneId: 1, status: DELIVERING) The drone is not available!)  

Check all drones available for loading:  
curl http://localhost:8080/available  
(response: a list of drones, which state is either IDLE or RETURNING)  

Try to make the same load for drone#2 (droneId=2):  
curl http://localhost:8080/load?droneId=2 -X POST -H "Content-Type: application/json" -d @src/test/resources/json/load_1.json  
(response: 400-BadRequest,   
with message: The load is to heavy for the drone: droneId: 2, weight_limit: 50, total load weight: 160)  

Try to make the same load for drone#3 (droneId=3):  
curl http://localhost:8080/load?droneId=3 -X POST -H "Content-Type: application/json" -d @src/test/resources/json/load_2.json  
(response: the load with created loadId) 

Check drone#3 (droneId=3):  
curl http://localhost:8080/drone_with_load?droneId=3  
(response: drone info with the load)  

Try to make another load for drone#3 (droneId=3):  
curl http://localhost:8080/load?droneId=3 -X POST -H "Content-Type: application/json" -d @src/test/resources/json/load_2.json  
(response: 400-BadRequest,  
with message: (droneId: 3, status: RETURNING) The drone Has already a load !)  

Check drone#4 (droneId=4), created earlier:  
curl http://localhost:8080/drone_with_load?droneId=4  
(response: drone info with load = null, since the drone has no load)  

Check the battery of drone#4 (droneId=4):  
curl http://localhost:8080/battery_level?droneId=4  
(response: integer value, the was value set when the drone was registered)  

enjoy yourself :)
## End
Have you got any questions, feel free to ask 


