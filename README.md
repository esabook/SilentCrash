![](/doc/silentcrash_demo.gif)

Download sample app: [released here](https://github.com/esabook/SilentCrash/releases)

### Usage:

Root/build.gradle or Root/settings.gradle

```groovy
repositories {
    // my latest development snapshot
    maven { url "https://s01.oss.sonatype.org/content/repositories/snapshots" }
}

```

Root/yourApp/build.gradle
```groovy
dependencies {
    // my latest development snapshot
    implementation 'io.github.esabook:silentcrash:1.0.0-SNAPSHOT'
}

```

Init once at Application class or Activity class with

```kotlin
SilentCrash
    .init(application)
    .autoReloadAppON(true)

// optional
// add custom listener to deliver custom action
SilentCrash.getWatcher()?.setOnCrashListener(customAction)
```