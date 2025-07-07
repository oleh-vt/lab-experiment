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

object Versions {
	const val SPRINGDOC = "2.8.9";
	const val TESTCONTAINERS = "1.21.3";
	const val LIQUIBASE = "4.32.0";
	const val JUNIT = "5.13.3";
	const val LOMBOK = "1.18.38";
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")

	implementation(platform("org.springdoc:springdoc-openapi-bom:${Versions.SPRINGDOC}"))
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")

	implementation("org.liquibase:liquibase-core:${Versions.LIQUIBASE}")
	runtimeOnly("org.postgresql:postgresql")

	compileOnly("org.projectlombok:lombok:${Versions.LOMBOK}")
	annotationProcessor("org.projectlombok:lombok:${Versions.LOMBOK}")

	testImplementation(platform("org.junit:junit-bom:${Versions.JUNIT}"))
	testImplementation(platform("org.testcontainers:testcontainers-bom:${Versions.TESTCONTAINERS}"))

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	testCompileOnly("org.projectlombok:lombok:${Versions.LOMBOK}")
	testAnnotationProcessor("org.projectlombok:lombok:${Versions.LOMBOK}")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.jar {
	enabled = false
}

