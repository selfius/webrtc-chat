import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    java
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "dev.sviri"
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
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.data:spring-data-redis")
    implementation("io.lettuce:lettuce-core")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register<Copy>("processFrontendResources") {
    // Directory containing the artifacts produced by the frontend project
    val frontendProjectBuildDir = project(":frontend").layout.buildDirectory
    val frontendBuildDir = frontendProjectBuildDir.dir("www")
    // Directory where the frontend artifacts must be copied to be packaged alltogether with the backend by the 'war'
    // plugin.
    val frontendResourcesDir = project.layout.buildDirectory.dir("resources/main/static")

    group = "Frontend"
    description = "Process frontend resources"
    dependsOn(":frontend:assembleFrontend")

    from(frontendBuildDir)
    into(frontendResourcesDir)
}

tasks.named<Task>("processResources") {
    dependsOn("processFrontendResources")
}

tasks.named<BootBuildImage>("bootBuildImage") {
    builder.set("gcr.io/buildpacks/builder")
    imageName.set("sviri.dev/${rootProject.name}:0.0.1-SNAPSHOT");
}
