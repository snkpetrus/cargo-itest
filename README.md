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

#5 minute tutorial

To enable you to quickly get started with the cargo-itest utility there are some default JUnit tests available which you could extend from. In this tutorial the AbstractDefaultDeploymentTest will be used to show you how easy it is to start creating integration tests with a Tomcat server.

##Details
When using maven2+ you can simply start by adding the following dependency to your POM file:
```xml
<dependency>
       <groupId>nl.tranquilizedquality</groupId>
       <artifactId>cargo-itest</artifactId>
       <version>1.4.0</version>
</dependency>
```
After that you need to do the following things:

* Extend from AbstractTomcatContainerUtil class. Check the sample here.
Configure your container utility like the example here. Make sure you name your context file itest-context.xml since this is the default name which is used.
* As you can see you need to specify a remote location where Tomcat can be found. You need to place your tomcat ZIP file somewhere on a server so the container utility can download it. You need to make some modifications on the server.xml if you want to run Tomcat on a different port that 8080. Replace the default port 8080 with the following placeholder ${cargo.server.port} so it will pick up your custom configured port in the context file. ZIP your tomcat distribution and place it on a server.
* Now you need to create a JUnit test that extends from AbstractDefaultDeploymentTest. A sample can be found here. This sample is a Tomcat sample that doesn't extend from the AbstractDefaultDeploymentTest since it uses a custom tomcat-itest-context.xml file. For JBoss you can find one that does extend from it here. Your Tomcat one can look exactly the same.
* Now you can actually execute your integration test :-).

If you have any questions or remarks you can contact me on salomo.petrus@gmail.com
