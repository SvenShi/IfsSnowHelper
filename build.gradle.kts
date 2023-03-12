plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.13.2"
}


group = "com.ruimin"
version = "2.30"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.3")
    type.set("IU") // Target IDE Platform

    plugins.set(
        listOf(
            "org.intellij.intelliLang", "com.intellij.jsp", "JavaScript"
        )
    )

}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
        options.encoding = "UTF-8"
    }

    patchPluginXml {
        sinceBuild.set("212.2")
        untilBuild.set("231.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("ORG_GRADLE_PROJECT_intellijPublishToken"))
    }
}
