plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(gradleApi())
}

gradlePlugin{
    plugins {
        create("untranslated_plugin") {
            id = "com.yandex.practicum.middle_homework_5.gradle_plugins.find-untranslated-plugin"
            implementationClass = "FindUntranslatedStringsPlugin"
        }
    }
}
