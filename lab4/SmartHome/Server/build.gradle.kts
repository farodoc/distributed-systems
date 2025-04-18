plugins {
    id("java")
    id("application")
}

application {
    mainClass.set("smart_home.Server")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.zeroc:ice:3.7.10")
    implementation("org.apache.commons:commons-lang3:3.6")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}