# ═══════════════════════════════════════════════════════════════
# ProGuard — INNOV'ACTION Finance
# ═══════════════════════════════════════════════════════════════

# Room — entités, DAOs, relations
-keep class com.innovaction.finance.data.local.entity.**    { *; }
-keep class com.innovaction.finance.data.local.dao.**       { *; }
-keep class com.innovaction.finance.data.local.relation.**  { *; }
-keep class com.innovaction.finance.data.local.AppDatabase  { *; }

# Modèles domaine et export
-keep class com.innovaction.finance.domain.model.**         { *; }
-keep class com.innovaction.finance.data.export.**          { *; }
-keep class com.innovaction.finance.data.backup.**          { *; }

# Hilt
-keep class dagger.hilt.**                                   { *; }
-keep class javax.inject.**                                  { *; }
-keep @dagger.hilt.android.HiltAndroidApp class *           { *; }

# WorkManager + Hilt Worker
-keep class androidx.work.**                                 { *; }
-keep class * extends androidx.work.Worker                   { *; }
-keep class * extends androidx.work.CoroutineWorker          { *; }

# Biometric
-keep class androidx.biometric.**                            { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# DataStore
-keep class androidx.datastore.**                            { *; }

# JSON (backup/restore)
-keep class org.json.**                                      { *; }

# Kotlin
-keep class kotlin.**                                        { *; }
-keep class kotlin.Metadata                                  { *; }
-dontwarn kotlin.**

# Reflection Compose
-keep class androidx.compose.**                              { *; }
-dontwarn androidx.compose.**
