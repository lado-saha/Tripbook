package tech.xken.tripbook.domain.di

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.xken.tripbook.data.models.Booker
import tech.xken.tripbook.data.models.CurrentBooker
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.sources.booking.BookingDataSource
import tech.xken.tripbook.data.sources.booking.BookingRepository
import tech.xken.tripbook.data.sources.booking.BookingRepositoryImpl
import tech.xken.tripbook.data.sources.booking.local.BookingLocalDatabase
import tech.xken.tripbook.data.sources.booking.local.BookingLocalDataSourceImpl
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
 * Annotation for the [BookingLocalDataSourceImpl]
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalBookingDataSource

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

    @Singleton//Only one instance per app session
    @Provides // Helps hilt to know that it must use this function to provide an instance of [
    fun provideBookingRepository(
        @LocalBookingDataSource localDataSource: BookingDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): BookingRepository = BookingRepositoryImpl(
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

    @Singleton
    @Provides
    @LocalBookingDataSource
    fun provideLocalBookingDataSource(
        database: BookingLocalDatabase,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): BookingDataSource = BookingLocalDataSourceImpl(
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
    fun provideUniverseDatabase(@ApplicationContext context: Context): UniverseLocalDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            UniverseLocalDatabase::class.java,
            "Universe.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides//One instance only for the database
    fun provideBookingDatabase(@ApplicationContext context: Context): BookingLocalDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            BookingLocalDatabase::class.java,
            "Booking.db"
        ).fallbackToDestructiveMigration().build()
    }
}