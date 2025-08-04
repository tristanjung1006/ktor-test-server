val kotlin_version: String by project
val logback_version: String by project
val koinVersion: String by project

plugins {
    kotlin("jvm") version "2.1.21"
    id("io.ktor.plugin") version "3.2.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.21"
    id("jacoco")
}

jacoco {
    toolVersion = "0.8.10"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)  // Codecov는 XML 리포트 필요
        html.required.set(true) // 로컬 확인용
        csv.required.set(false)
    }

    // 제외할 파일들
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/Application*",
                    "**/ApplicationKt*",
                    "**/plugins/**",
                    "**/models/**", // 데이터 클래스 제외
                    "**/*\$WhenMappings.*" // Kotlin when 매핑 제외
                )
            }
        })
    )

    executionData.setFrom(fileTree(layout.buildDirectory.dir("jacoco")).include("**/*.exec"))
}

tasks.test {
    useJUnitPlatform()
    systemProperty("file.encoding", "UTF-8")
    jvmArgs = listOf("-Dfile.encoding=UTF-8")
    finalizedBy(tasks.jacocoTestReport)
}

// 명시적으로 JUnit 의존성 추가 (Gradle 9.0 호환)
dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-netty")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-default-headers:3.2.2")
    implementation("io.ktor:ktor-server-host-common:3.2.2")
    implementation("io.ktor:ktor-server-status-pages:3.2.2")

    // 테스트 의존성 - JUnit 버전 통일
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlin_version")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("io.insert-koin:koin-ktor:${koinVersion}")
    implementation("io.insert-koin:koin-logger-slf4j:${koinVersion}")
}
