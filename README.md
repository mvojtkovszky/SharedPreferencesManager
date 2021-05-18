# SharedPreferencesManager
Simplifies use of Android's SharedPreferences and allows storing of objects and list of objects.

## How does it work?
1. Create an instance of SharedPreferencesManager
``` kotlin
preferencesManager = SharedPreferencesManager(applicationContext)
```

or if you require more options, you could do something like
``` kotlin
preferencesManager = SharedPreferencesManager(
                context = applicationContext,
                fileKey = "myCustomFileName",
                operatingMode = Context.MODE_PRIVATE,
                json = Json { isLenient = true },
                errorListener = { it.printStackTrace() }
        )
```

2. Use any of the public methods to retrieve or save data. Any put call automatically applies changes.
``` kotlin
fun getBoolean(key: String, defaultValue: Boolean): Boolean
fun setBoolean(key: String, value: Boolean)
fun getFloat(key: String, defaultValue: Float = 0f): Float
fun setFloat(key: String, value: Float)
fun getInt(key: String, defaultValue: Int = 0): Int
fun setInt(key: String, value: Int)
fun getList(key: String, defaultValue: List<T>? = null): List<T>?
fun setList(key: String, list: List<T>?)
fun getLong(key: String, defaultValue: Long = 0L): Long
fun setLong(key: String, value: Long)
fun getObject(key: String, defaultValue: T? = null) : T?
fun setObject(key: String, obj: T?)
fun getString(key: String, defaultValue: String?): String?
fun setString(key: String, value: String?)
fun getStringSet(key: String, defaultValue: MutableSet<String>?): MutableSet<String>?
fun setStringSet(key: String, value: MutableSet<String>?)
fun clearData()
fun getAll(): MutableMap<String, *>?
fun remove(key: String)
```

Note that [getObject], [putObject], [getList] and [putList] use Kotlin serialization, so [T] has to be
annotated as [Serializable], otherwise [SerializationException] will be thrown.


## Nice! How do I get started?
Make sure root build.gradle repositories include JitPack
``` gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

And SharedPreferencesManager dependency is added to app build.gradle
``` gradle
dependencies {
    implementation 'com.github.mvojtkovszky:SharedPreferencesManager:$latest_version'
}
```
