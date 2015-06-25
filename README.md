# Introduction
This utility enables you to easily create an integration test with as less configuration possible. It even provides you with a default test that can be used out of the box:

* AbstractDefaultDeploymentTest
* AbstractDefaultHibernateDeploymentTest
* AbstractDefaultNoDbDeployment

Container support:
* Tomcat
* JBoss
* Jetty
* Jonas
* Glassfish

Check out the source code to see the examples in src/test/java. You can find example container utilities and simple integration tests using the abstract deployment tests mentioned above in the src/test/java folder.

Some main features:

* Integration: Run the same tests with whatever build tool out there like Ant, Maven etc. and even within your IDE.
Configurable container port: You can easily configure on which port the container will run.
* Easy SQL script execution: This can be done by just configuring it in you spring context file.
* Multiple container support: It currently supports Tomcat, Jetty, JOnas & JBoss.
* Easy to extend: You can create your own container utility if the container you are using is not supported yet.
* Configurable context names: For WAR applications by default the context name will be the name of the WAR. This is most of the time not what you want so the utility also provides the flexibility to set the context name.
Maven2
You can add the following to your Maven pom file:
```xml
<dependency>
       <groupId>nl.tranquilizedquality</groupId>
       <artifactId>cargo-itest</artifactId>
       <version>1.4.0</version>
</dependency>
```

If you have any questions or remarks you can contact me on salomo.petrus@gmail.com
