plugins {
	java
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.epam"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")

	implementation(platform("org.springdoc:springdoc-openapi-bom:2.8.9"))
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")

	implementation("org.liquibase:liquibase-core:4.32.0")
	runtimeOnly("org.postgresql:postgresql")

	testImplementation(platform("org.junit:junit-bom:5.13.3"))
	testImplementation(platform("org.testcontainers:testcontainers-bom:1.21.3"))

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
