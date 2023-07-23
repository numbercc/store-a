import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
//    waiting till hibernate 6.3 or higher is avalable in spring boot 3
  id("org.springframework.boot") version "3.0.9"
  id("io.spring.dependency-management") version "1.1.2"
  kotlin("jvm") version "1.8.22"
  kotlin("plugin.spring") version "1.8.22"
  kotlin("plugin.jpa") version "1.8.22"
}

group = "nl.intergamma.store-a"
version = "0.0.1-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15+")
  implementation("org.jetbrains.kotlin:kotlin-reflect")

  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

  developmentOnly("org.springframework.boot:spring-boot-devtools")
  runtimeOnly("com.h2database:h2")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("io.mockk:mockk:1.12.0")

  compileOnly("org.projectlombok:lombok:1.18.20")
  annotationProcessor("org.projectlombok:lombok:1.18.20")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs += "-Xjsr305=strict"
    jvmTarget = "17"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}