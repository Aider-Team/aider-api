plugins {
    java
    id("org.springframework.boot") version "2.7.16"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.aider"
version = "0.0.1-SNAPSHOT"

val queryDslVersion by extra("5.0.0")

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    implementation("com.fasterxml.jackson.core:jackson-databind")
//	implementation("io.springfox:springfox-boot-starter:3.0.0")
    implementation("com.querydsl:querydsl-jpa:$queryDslVersion")
    annotationProcessor("com.querydsl:querydsl-apt:$queryDslVersion:jpa")
    runtimeOnly("com.h2database:h2")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val querydslDir = layout.buildDirectory.dir("generated/querydsl") // gradlew 최신

sourceSets {
    getByName("main").java.srcDir(querydslDir)
}

configurations {
    create("querydsl") {
        extendsFrom(configurations.compileClasspath.get())
    }
}

tasks.register("compileQuerydsl", JavaCompile::class) {
    options.annotationProcessorPath = configurations["querydsl"]
    source(querydslDir)
}

// 컴파일 후 QueryDSL 코드 삭제
tasks.clean {
    delete(querydslDir)
}
