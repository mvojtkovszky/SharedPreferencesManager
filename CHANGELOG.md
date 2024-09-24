# CHANGELOG

## 1.5.8 (TBD)
*

## 1.5.7 (2024-09-24)
* Bump Gradle plugin to 8.6.1, Kotlin to 2.0.20, Serialization to 1.7.3

## 1.5.6 (2024-01-26)
* Bump Gradle plugin to 8.2.2, Kotlin to 1.9.22, Serialization to 1.6.2

## 1.5.5 (2023-08-26)
* Fix publish script

## 1.5.4 (2023-08-25)
* Bump Gradle plugin to 8.1.1, Serialization to 1.6.0
* Bump buildToolsVersion 34.0.0, targetSdkVersion, compileSdkVersion to 34

## 1.5.2 (2023-03-21)
* Bump kotlin to 1.8.0, Gradle plugin to 7.4.2, Serialization to 1.5.0
* BuildToolsVersion to 33.0.2

## 1.5.2 (2022-09-15)
* Remove threading in `setList` and `setObject` and  add it to convenience functions `setListAsync` and `setObjectAsync`
* Bump Kotlin to 1.7.10, Serialization to 1.4.0, Gradle plugin to 7.2.2
* Bump buildToolsVersion to 33.0.0, compileSdkVersion and targetSdkVersion to 33

## 1.5.1 (2022-04-29)
* Fix issue where memory state won't update if null is passed to `setObject` or `setList`

## 1.5.0 (2022-04-27)
* Add `getObjectAsync` and `getListAsync` serialized objects and lists are now cached in memory to avoid redundant deserialization
* Bump Kotlin to 1.6.21, Serialization to 1.3.2, Gradle plugin to 7.1.3
* Bump buildToolsVersion to 32.0.0, compileSdkVersion and targetSdkVersion to 32

## 1.4.0 (2021-10-12)
* Bump serialization-json to 1.3.0
* Bump build tools to 31.0.0, compile sdk and target sdk to 31
* Bump Kotlin and Dokka to 1.5.31

## 1.3.3 (2021-08-04)
* Annotate maps in `InMemorySharedPreferences as volatile
* Use Gradle 7 and update publish scripts

## 1.2.0 (2021-07-27)
* `SharedPreferences` are now required in constructor of `SharedPreferencesManager` 
provide `InMemorySharedPreferences` implementation which bypasses file management and operates in memory only 
parameter `defaultValue` in `getString` and `getStringSet` methods now default to null, so defining null explicitly is no longer necessary

## 1.1.1 (2021-07-21)
* Bump Kotlin Serialize to 1.2.2
* Bump Kotlin to 1.5.21, Gradle plugin to 4.2.2

## 1.1.0 (2021-04-19)
* Rename method names put to set
* Bump Kotlin to 1.5.0, Gradle plugin to 4.2.1, Serialization to 1.2.1

## 1.0.1 (2021-04-19)
* Use error listener instead of logErrors flag
* Use default Json config instead of custom
* Add documentation creation instructions

## 1.0.0 (2021-04-19)
* Initial public release
