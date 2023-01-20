package tech.xken.tripbook.domain.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import tech.xken.tripbook.data.sources.universe.UniverseDataSource
import tech.xken.tripbook.data.sources.universe.UniverseRepository
import tech.xken.tripbook.data.sources.universe.UniverseRepositoryImpl
import tech.xken.tripbook.data.sources.universe.local.UniverseLocalDataSourceImpl
import tech.xken.tripbook.data.sources.universe.local.UniverseLocalDatabase
import javax.inject.Qualifier
import javax.inject.Singleton


/**
 * Annotation for the [UniverseLocalDataSourceImpl]
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalUniverseDataSource

/**
 * A Module to help hilt instantiate all our repositories`
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton//Only one instance per app session
    @Provides // Helps hilt to know that it must use this function to provide an instance of [
    fun provideUniverseRepository(
        @LocalUniverseDataSource localDataSource: UniverseDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): UniverseRepository = UniverseRepositoryImpl(
        localDataSource,
        ioDispatcher
    )
}

/**
 * Help hilt instantiate all our data sources online and local
 */
@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Singleton
    @Provides
    @LocalUniverseDataSource
    fun provideLocalUniverseDataSource(
        database: UniverseLocalDatabase,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): UniverseDataSource = UniverseLocalDataSourceImpl(
        database.dao,
        ioDispatcher
    )
}

/**
 * Helps hilt instantiate our local database
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides//One instance only for the database
    fun provideDatabase(@ApplicationContext context: Context): UniverseLocalDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            UniverseLocalDatabase::class.java,
            "Universe.db"
        ).fallbackToDestructiveMigration().build()
    }
}