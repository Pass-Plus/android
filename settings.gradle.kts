import java.io.FileInputStream
import java.util.Properties

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

/**
 * Loads local user-specific build properties that are not checked into source control.
 */
val userProperties = Properties().apply {
    val buildPropertiesFile = File(rootDir, "user.properties")
    if (buildPropertiesFile.exists()) {
        FileInputStream(buildPropertiesFile).use { load(it) }
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // FOR OFFLINE BUILD: Use local Maven repository instead of GitHub Packages
        // To use local SDK:
        // 1. Build the SDK locally from bitwarden/sdk repository
        // 2. Publish it to your local Maven repository (mavenLocal())
        // 3. Set localSdk=true in user.properties file
        // 4. Alternatively, set up a local Maven repository server and update the URL below
        val useLocalSdk = (userProperties["localSdk"] as String?).toBoolean()
        if (useLocalSdk) {
            mavenLocal()
        } else {
            maven {
                name = "GitHubPackages (Bitwarden)"
                url = uri("https://maven.pkg.github.com/bitwarden/sdk")
                credentials {
                    username = ""
                    password = userProperties["gitHubToken"] as String? ?: System.getenv("GITHUB_TOKEN")
                }
            }
        }
        // FOR OFFLINE BUILD: Add your local Maven repository here
        // Example: maven { url = uri("file:///path/to/local/maven/repo") }
    }
}

buildCache {
    local {
        isEnabled = true
        directory = File(rootDir, "build-cache")
    }
}

rootProject.name = "Bitwarden"
include(
    ":annotation",
    ":app",
    ":authenticator",
    ":authenticatorbridge",
    ":core",
    ":cxf",
    ":data",
    ":network",
    ":testharness",
    ":ui",
)
