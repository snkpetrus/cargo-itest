# Introduction #

To enable you to quickly get started with the cargo-itest utility there are some default JUnit tests available which you could extend from. In this tutorial the AbstractDefaultDeploymentTest will be used to show you how easy it is to start creating integration tests with a Tomcat server.

# Details #

When using maven2 you can simply start by adding the following dependency to your POM file:
```
<dependency>
	<groupId>nl.tranquilizedquality</groupId>
	<artifactId>cargo-itest</artifactId>
	<version>1.4.0</version>
</dependency>
```

After that you need to do the following things:
  1. Extend from [AbstractTomcatContainerUtil](http://cargo-itest.googlecode.com/svn/trunk/src/main/java/nl/tranquilizedquality/itest/cargo/AbstractTomcatContainerUtil.java) class. Check the sample [here](http://cargo-itest.googlecode.com/svn/trunk/src/test/java/nl/tranquilizedquality/itest/cargo/AppTomcatContainerUtil.java).
  1. Configure your container utility like the example [here](http://cargo-itest.googlecode.com/svn/trunk/src/test/resources/tomcat-itest-context.xml). Make sure you name your context file itest-context.xml since this is the default name which is used.
  1. As you can see you need to specify a remote location where Tomcat can be found. You need to place your tomcat ZIP file somewhere on a server so the container utility can download it. You need to make some modifications on the server.xml if you want to run Tomcat on a different port that 8080. Replace the default port 8080 with the following placeholder ${cargo.server.port} so it will pick up your custom configured port in the context file. ZIP your tomcat distribution and place it on a server.
  1. Now you need to create a JUnit test that extends from [AbstractDefaultDeploymentTest](.md). A sample can be found [here](http://cargo-itest.googlecode.com/svn/trunk/src/test/java/nl/tranquilizedquality/itest/tomcat/TestAppTomcatDeploymentTest.java). This sample is a Tomcat sample that doesn't extend from the AbstractDefaultDeploymentTest since it uses a custom tomcat-itest-context.xml file. For JBoss you can find one that does extend from it [here](http://cargo-itest.googlecode.com/svn/trunk/src/test/java/nl/tranquilizedquality/itest/jboss/TestAppJBossDeploymentTest.java). Your Tomcat one can look exactly the same.
  1. Now you can actually execute your integration test :-).