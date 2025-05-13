
<!-- https://www.busyqa.com/post/maven-cucumber-reporting-in-2022 -->
<plugin>
	<groupId>net.masterthought</groupId>
	<artifactId>maven-cucumber-reporting</artifactId>
	...
</plugin>
=====> target/cucumber/json/cucumber-html-reports/overview-features.html
----------------------------------------------------------------------------------------------------------------------
Testcontainers nécessite que Docker soit installé et exécuté sur votre machine,
	car il lance des conteneurs de test (MongoDB dans mon cas).
Il faut s'assurer que le démon Docker est en cours d'exécution :
----------------
docker --version
docker ps
----------------------------------------------------------------------------------------------------------------------
package com.chj.gr.integration.controller.flapdoodle;

import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

<!--
<dependency>
    <groupId>de.flapdoodle.embed</groupId>
    <artifactId>de.flapdoodle.embed.mongo.spring30x</artifactId>
    <version>4.11.0</version>
    <scope>test</scope>
</dependency>
<dependency>
	<groupId>org.testcontainers</groupId>
	<artifactId>testcontainers</artifactId>
	<version>${testcontainers.version}</version>
	<scope>test</scope>
</dependency>
<dependency>
	<groupId>org.testcontainers</groupId>
	<artifactId>junit-jupiter</artifactId>
	<version>${testcontainers.version}</version>
	<scope>test</scope>
</dependency>
<dependency>
	<groupId>org.testcontainers</groupId>
	<artifactId>mongodb</artifactId>
	<version>${testcontainers.version}</version>
	<scope>test</scope>
</dependency>
-->
----------------------------------------------------------------------------------------------------------------------