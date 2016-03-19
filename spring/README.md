This blog post is the example application of the following blog posts:

* [Spring Batch Tutorial: Getting the Required Dependencies With Maven](http://www.petrikainulainen.net/programming/spring-framework/spring-batch-tutorial-getting-the-required-dependencies-with-maven/)
* [Spring Batch Tutorial: Getting the Required Dependencies With Gradle](http://www.petrikainulainen.net/programming/spring-framework/spring-batch-tutorial-getting-the-required-dependencies-with-gradle/)
* [Spring Batch Tutorial: Reading Information From a File](http://www.petrikainulainen.net/programming/spring-framework/spring-batch-tutorial-reading-information-from-a-file/)
* [Spring Batch Tutorial: Creating a Custom ItemReader](http://www.petrikainulainen.net/programming/spring-framework/spring-batch-tutorial-creating-a-custom-itemreader/)
* [Spring Batch Tutorial: Reading Information From a REST API](http://www.petrikainulainen.net/programming/spring-framework/spring-batch-tutorial-reading-information-from-a-rest-api/)

Prerequisites
=============

You need to install the following tools if you want to run this application:

* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Maven](http://maven.apache.org/) (the application is tested with Maven 3.3.3) OR [Gradle](http://gradle.org/) (the application is tested with Gradle 2.9)

Running the Application
=======================

You can run the application by using either Maven or Gradle.

Running the Application With Maven
----------------------------------

You can run the application by using the following command:

    mvn clean jetty:run

Running the Application With Gradle
-----------------------------------

You can start the application by using the following command:

	gradle clean appStart
	

You can stop the application by using the following command:

	gradle appStop