plugins {
    id("org.springframework.boot") version "3.4.4"
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.spring") version "1.9.24" // The Kotlin Spring plugin
}

group = "uno.moni.onex"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

springBoot {
    mainClass = "uno.moni.onex.ApplicationKt"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "uno.moni.onex.ApplicationKt"
    }
    // 下方的依赖打包可能会有重复文件，设置排除掉重复文件
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // 将依赖一起打包进jar
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
}
dependencies {
    /* Kotlin */
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.20")
    /* SpringBoot */
    implementation("org.springframework.boot:spring-boot-starter-web:3.4.4")
    /* Security */
    implementation("cn.dev33:sa-token-spring-boot3-starter:1.42.0")
    /* sql */
    implementation("com.mysql:mysql-connector-j:9.2.0")
    implementation("com.baomidou:mybatis-plus-spring-boot3-starter:3.5.11")
    /* Api Doc */
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.30")
    /* Validation */
    implementation("jakarta.validation:jakarta.validation-api:3.1.1")
    /* Tools */
    implementation("cn.hutool:hutool-core:5.8.37")
    implementation("cn.hutool:hutool-crypto:5.8.37")
    implementation("cn.hutool:hutool-extra:5.8.37")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}