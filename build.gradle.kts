plugins {
    kotlin("jvm") version "2.3.21"
    kotlin("plugin.spring") version "2.3.21"
    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "xyz.thewind"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")
    // 第三方音乐平台接口的 JSON 解析
    implementation("com.google.code.gson:gson")

    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

// 前端（frontend/，Vue + Vite）构建后打进 jar 的 static/ 目录，需要本机安装 Node.js 22+
val frontendDir = layout.projectDirectory.dir("frontend")
val npm = if (System.getProperty("os.name").startsWith("Windows")) "npm.cmd" else "npm"

val npmInstall = tasks.register<Exec>("npmInstall") {
    workingDir(frontendDir)
    commandLine(npm, "ci", "--no-audit", "--no-fund")
    inputs.files(frontendDir.file("package.json"), frontendDir.file("package-lock.json"))
    outputs.dir(frontendDir.dir("node_modules"))
}

val frontendBuild = tasks.register<Exec>("frontendBuild") {
    dependsOn(npmInstall)
    workingDir(frontendDir)
    commandLine(npm, "run", "build")
    inputs.dir(frontendDir.dir("src"))
    inputs.dir(frontendDir.dir("public"))
    inputs.files(
        frontendDir.file("index.html"),
        frontendDir.file("vite.config.ts"),
        frontendDir.file("tsconfig.json"),
        frontendDir.file("package-lock.json"),
    )
    outputs.dir(frontendDir.dir("dist"))
}

val frontendTest = tasks.register<Exec>("frontendTest") {
    dependsOn(npmInstall)
    workingDir(frontendDir)
    commandLine(npm, "test")
}

tasks.processResources {
    from(frontendBuild) { into("static") }
}

tasks.check {
    dependsOn(frontendTest)
}

tasks.withType<Test> {
    useJUnitPlatform {
        // 访问真实音乐平台的集成测试默认不跑：./gradlew test -Dlive=true
        if (System.getProperty("live") != "true") excludeTags("live")
    }
    systemProperty("live", System.getProperty("live") ?: "false")
}
