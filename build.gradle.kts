plugins {
	java
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.vstu"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	/**
	 * Spring boot starters
	 */
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign:4.2.0")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	implementation("org.springframework.retry:spring-retry:2.0.2")

	/**
	 * Swagger
	 */
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")

	/**
	 * JWT
	 * */
	implementation("org.bitbucket.b_c:jose4j:0.9.6")


	/**
	 * Database
	 */
	implementation("org.liquibase:liquibase-core")
	implementation("redis.clients:jedis:4.3.2")
	runtimeOnly("org.postgresql:postgresql")

	/**
	 * Utils & Logging
	 */
	implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
	implementation("org.slf4j:slf4j-api:2.0.5")
	implementation("ch.qos.logback:logback-classic:1.5.18")
	implementation("ch.qos.logback:logback-core:1.5.18")
	implementation("org.projectlombok:lombok:1.18.32")
	annotationProcessor("org.projectlombok:lombok:1.18.32")
	implementation("org.mapstruct:mapstruct:1.5.3.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.3.Final")
	implementation("com.github.sviperll:result4j:1.2.0")

	/**
	 * Test containers
	 */
	implementation(platform("org.testcontainers:testcontainers-bom:1.19.1"))
	testImplementation("org.testcontainers:junit-jupiter:1.19.1")
	testImplementation("org.testcontainers:postgresql:1.19.1")
	testImplementation("com.redis.testcontainers:testcontainers-redis-junit-jupiter:1.4.6")

	/**
	 * Tests
	 */
	testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
	testImplementation("org.assertj:assertj-core:3.24.2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.mockito:mockito-core:5.15.2")
	testRuntimeOnly("org.mockito:mockito-inline:5.2.0")
	testImplementation("net.bytebuddy:byte-buddy:1.14.11")
	testImplementation("net.bytebuddy:byte-buddy-agent:1.14.11")

	implementation("org.json:json:20231013")
	implementation("org.apache.commons:commons-compress:1.26.0")
}

configurations.all {
	exclude(group = "com.vaadin.external.google", module = "android-json")
}

tasks.withType<Test> {
	useJUnitPlatform()
	jvmArgs = listOf("-Djdk.instrument.traceUsage", "-XX:+EnableDynamicAgentLoading")
}

val test by tasks.getting(Test::class) { testLogging.showStandardStreams = true }

tasks.bootJar {
	archiveFileName.set("service.jar")
}