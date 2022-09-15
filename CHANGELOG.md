# CHANGELOG

## 1.5.2 (2022-09-15)
* remove threading in `setList` and `setObject` and 
  add it to convenience functions `setListAsync` and `setObjectAsync`
* bump Kotlin to 1.7.10, Serialization to 1.4.0, Gradle plugin to 7.2.2
* bump buildToolsVersion to 33.0.0, compileSdkVersion and targetSdkVersion to 33

## 1.5.1 (2022-04-29)
* fix issue where memory state won't update if null is passed to `setObject` or `setList`

## 1.5.0 (2022-04-27)
* add `getObjectAsync` and `getListAsync`
* serialized objects and lists are now cached in memory to avoid redundant deserialization
* bump Kotlin to 1.6.21, Serialization to 1.3.2, Gradle plugin to 7.1.3
* bump buildToolsVersion to 32.0.0, compileSdkVersion and targetSdkVersion to 32

## 1.4.0 (2021-10-12)
* bump serialization-json to 1.3.0
* bump build tools to 31.0.0, compile sdk and target sdk to 31
* bump Kotlin and Dokka to 1.5.31

## 1.3.3 (2021-08-04)
 Annotate maps in `InMemorySharedPreferences as volatile
* Use Gradle 7 and update publish scripts

## 1.2.0 (2021-07-27)
* `SharedPreferences` are now required in constructor of `SharedPreferencesManager`
* provide `InMemorySharedPreferences` implementation which bypasses file management and operates 
  in memory only
* parameter `defaultValue` in `getString` and `getStringSet` methods now default to null, so 
  defining null explicitly is no longer necessary

## 1.1.1 (2021-07-21)
Bump Kotlin Serialize to 1.2.2
Bump Kotlin to 1.5.21, Gradle plugin to 4.2.2

## 1.1.0 (2021-04-19)
* Rename method names put to set
* Bump Kotlin to 1.5.0, Gradle plugin to 4.2.1, Serialization to 1.2.1

## 1.0.1 (2021-04-19)
* Use error listener instead of logErrors flag
* Use default Json config instead of custom
* Add documentation creation instructions

## 1.0.0 (2021-04-19)
* Initial public release
