// Build script for Minecraft 26.x. These versions ship without obfuscation, so they use the non-remapping
// Loom directly instead of Modstitch. Everything else mirrors build.gradle.kts.
plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("net.fabricmc.fabric-loom") version "1.17.11"
    id("me.modmuss50.mod-publish-plugin")
    id("me.fallenbreath.yamlang") version "1.5.0"
}

fun prop(name: String) : String {
    return findProperty(name)?.toString() ?: throw IllegalArgumentException("Missing property: $name")
}

// Variables
class ModData {
    val id = prop("mod.id")
    val name = prop("mod.name")
    val version = prop("mod.version")
    val group = prop("mod.group")
}
val mod = ModData()
val minecraft = stonecutter.current.project.substringBeforeLast('-')
val minecraftVersionRange = prop("mod.mc_version_range")

// See https://stonecutter.kikugie.dev/stonecutter/guide/comments#condition-constants
val loader: String = name.split("-")[1]
stonecutter {
    constants {
        match(loader, "fabric", "neoforge", "forge")
        put("do-a-barrel-roll", hasProperty("deps.dabr"))
    }
}

base { archivesName.set("${mod.id}-$loader") }
group = mod.group
version = "${mod.version}+mc$minecraft"

tasks.withType<Jar> {
    from(rootDir.resolve("LICENSE"))
}

java {
    withSourcesJar()
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 25
}

kotlin {
    jvmToolchain(25)
}

loom {
    runConfigs.all {
        ideConfigGenerated(environment == "client")
        runDir("../../run")
    }
}

// Modstitch fills these templates for the other versions
val templateProperties = mapOf(
    "mod_id" to mod.id,
    "mod_name" to mod.name,
    "mod_version" to mod.version,
    "mc" to minecraftVersionRange,
    "refmap" to "",
)
val generateTemplates = tasks.register<Copy>("generateTemplates") {
    inputs.properties(templateProperties)
    from("../../src/main/templates") {
        exclude("META-INF/**")
    }
    into(layout.buildDirectory.dir("generated/templates"))
    expand(templateProperties)
    filesMatching("fabric.mod.json") {
        filter { line ->
            line.replace("\"entrypoints\": {", "\"mixins\": [\"${mod.id}.mixins.json\"],\n    \"entrypoints\": {")
        }
    }
}
sourceSets.main {
    resources.srcDir(generateTemplates)
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    implementation("net.fabricmc:fabric-loader:${prop("deps.fabric_loader")}")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    implementation("net.fabricmc.fabric-api:fabric-api:${prop("deps.fapi")}")
    implementation("net.fabricmc:fabric-language-kotlin:${prop("deps.flk")}")
    implementation("com.terraformersmc:modmenu:${prop("deps.modmenu")}")

    implementation("dev.architectury:architectury-${loader}:${prop("deps.arch_api")}")
    implementation("dev.isxander:yet-another-config-lib:${prop("deps.yacl")}")

    findProperty("deps.dabr")?.let {
        implementation("nl.enjarai:do-a-barrel-roll:$it")
    }
}

yamlang {
    targetSourceSets.set(mutableListOf(sourceSets["main"]))
    inputDir.set("assets/${mod.id}/lang")
}

// Publishing
publishMods {
    val modrinthToken = findProperty("modrinthToken")
    dryRun = modrinthToken == null

    file = tasks.jar.flatMap { it.archiveFile }
    additionalFiles.from(tasks.named<Jar>("sourcesJar").flatMap { it.archiveFile })

    displayName = "${mod.name} ${mod.version} for ${loader.replaceFirstChar { it.uppercase() }} ${prop("mod.mc_title")}"
    version = "${mod.version}+mc$minecraft-$loader"
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = STABLE
    modLoaders.add(loader)

    val targets = prop("mod.mc_targets").split(' ')
    modrinth {
        projectId = prop("publish.modrinth")
        accessToken = modrinthToken.toString()
        targets.forEach(minecraftVersions::add)
        requires("fabric-api")
        requires("fabric-language-kotlin")
        optional("modmenu")
        requires("architectury-api")
        requires("yacl")
    }
}

val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.jar.flatMap { it.archiveFile })
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
    dependsOn("build")
}

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"
        dependsOn(buildAndCollect)
    }

    rootProject.tasks.register("runActive") {
        group = "project"
        dependsOn(tasks.named("runClient"))
    }
}
