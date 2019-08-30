**Here is my submission**

Billable Hours is a solution that allows a company upload a CSV file containing their employee billable hours in a specified format
and it generates invoices for all the companies the employees have worked with.

**User Guide**

Navigate to the homepage http://localhost:8080/index.html of the application and upload a csv file of the following format


**Technology used**
1. Spring Boot
2. Java 8


**Running this Project Locally**

Dependencies you need:
1. Java 8: You need the latest Java 8 JDK installed on your system. https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

2. Maven: You need maven to run the project. Maven is a java build tool.
Read more about maven here https://www.tutorialspoint.com/maven/index.htm 

Steps to run:
1. Clone this project

2. Run this commands in the root folder of the project

    `mvn clean`

    `mvn package`

    which will generate a target folder

    `cd target`

    `java -jar billablehours-0.0.1-SNAPSHOT.jar`

    Access the website via http://localhost:8080/index.html

**Deploying on different environments**

There are properties file specific to different environments in the resources folder. i.e test, prod, default

To deploy the app with a specific environment file 

for prod

`java -jar -Dspring.profiles.active=prod application.jar`

for test

`java -jar -Dspring.profiles.active=test application.jar`





