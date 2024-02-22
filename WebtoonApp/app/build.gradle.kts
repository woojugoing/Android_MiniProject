plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.woojugoing.webtoonapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.woojugoing.webtoonapp"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding {
        enable = true
    }
}

val ktlint by configurations.creating
val detekt by configurations.creating

dependencies {
    ktlint("com.pinterest:ktlint:0.49.1") { attributes { attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL)) } }
    detekt("io.gitlab.arturbosch.detekt:detekt-cli:1.23.3")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

val ktlintCheck by tasks.registering(JavaExec::class) {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Check Kotlin code style"
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args("**/src/**/*.kt", "**.kts", "!**/build/**")
}

tasks.register<JavaExec>("ktlintFormat") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Check Kotlin code style and format"
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
    args("-F", "**/src/**/*.kt", "**.kts", "!**/build/**")
}

val detektTask = tasks.register<JavaExec>("detekt") {
    mainClass.set("io.gitlab.arturbosch.detekt.cli.Main")
    classpath = detekt
    args(listOf("-i", projectDir, "-c", "$projectDir/detekt.yml", "-ex", ".*/build/.*,.*/resources/.*"))
}

tasks.check {
    dependsOn(ktlintCheck)
    dependsOn(detektTask)
}