package tech.xken.tripbook.domain.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import tech.xken.tripbook.data.sources.agency.AgencyDataSource
import tech.xken.tripbook.data.sources.agency.AgencyRepository
import tech.xken.tripbook.data.sources.agency.AgencyRepositoryImpl
import tech.xken.tripbook.data.sources.agency.local.AgencyLocalDataSourceImpl
import tech.xken.tripbook.data.sources.agency.local.AgencyLocalDatabase
import tech.xken.tripbook.data.sources.booking.BookingDataSource
import tech.xken.tripbook.data.sources.booking.BookingRepository
import tech.xken.tripbook.data.sources.booking.BookingRepositoryImpl
import tech.xken.tripbook.data.sources.booking.local.BookingLocalDatabase
import tech.xken.tripbook.data.sources.booking.local.BookingLocalDataSourceImpl
import tech.xken.tripbook.data.sources.caches.CachesDataSource
import tech.xken.tripbook.data.sources.caches.CachesRepository
import tech.xken.tripbook.data.sources.caches.CachesRepositoryImpl
import tech.xken.tripbook.data.sources.caches.local.CachesLocalDataSourceImpl
import tech.xken.tripbook.data.sources.caches.local.CachesLocalDatabase
import tech.xken.tripbook.data.sources.init.InitDataSource
import tech.xken.tripbook.data.sources.init.InitRepository
import tech.xken.tripbook.data.sources.init.InitRepositoryImpl
import tech.xken.tripbook.data.sources.init.local.InitLocalDataSourceImpl
import tech.xken.tripbook.data.sources.init.local.InitLocalDatabase
import tech.xken.tripbook.data.sources.universe.UniverseDataSource
import tech.xken.tripbook.data.sources.universe.UniverseRepository
import tech.xken.tripbook.data.sources.universe.UniverseRepositoryImpl
import tech.xken.tripbook.data.sources.universe.local.UniverseLocalDataSourceImpl
import tech.xken.tripbook.data.sources.universe.local.UniverseLocalDatabase
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalUniverseDataSource

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalInitDataSource

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalBookingDataSource

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalAgencyDataSource

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalCachesDataSource

/*--------------------------------------------------------*/
/**
 * A Module to help hilt instantiate all our repositories`
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton//Only one instance per app session
    @Provides // Helps hilt to know that it must use this function to provide an instance of
    fun provideUniverseRepository(
        @LocalUniverseDataSource localDataSource: UniverseDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): UniverseRepository = UniverseRepositoryImpl(
        localDataSource,
        ioDispatcher
    )

    @Singleton//Only one instance per app session
    @Provides // Helps hilt to know that it must use this function to provide an instance of
    fun provideCachesRepository(
        @LocalCachesDataSource localDataSource: CachesDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): CachesRepository = CachesRepositoryImpl(
        localDataSource,
        ioDispatcher
    )

    @Singleton
    @Provides
    fun provideBookingRepository(
        @LocalBookingDataSource localDataSource: BookingDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): BookingRepository = BookingRepositoryImpl(
        localDataSource,
        ioDispatcher
    )

    @Singleton
    @Provides
    fun provideAgencyRepository(
        @LocalBookingDataSource localBookingDataSource: BookingDataSource,
        @LocalAgencyDataSource localAgencyDataSource: AgencyDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): AgencyRepository = AgencyRepositoryImpl(
        localAgencyDataSource,
        BookingRepositoryImpl(
            localBookingDataSource,
            ioDispatcher
        ),
        ioDispatcher
    )

    @Singleton
    @Provides
    fun provideInitRepository(
        @LocalInitDataSource localInitDataSource: InitDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): InitRepository = InitRepositoryImpl(
        localInitDataSource,
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
    @LocalCachesDataSource
    fun provideLocalCachesDataSource(
        database: CachesLocalDatabase,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): CachesDataSource = CachesLocalDataSourceImpl(
        database.dao,
        ioDispatcher
    )

    @Singleton
    @Provides
    @LocalInitDataSource
    fun provideLocalInitDataSource(
        database: InitLocalDatabase,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): InitDataSource = InitLocalDataSourceImpl(
        database.dao,
        ioDispatcher
    )

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

    @Singleton
    @Provides
    @LocalAgencyDataSource
    fun provideLocalAgencyDataSource(
        agencyDatabase: AgencyLocalDatabase,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): AgencyDataSource = AgencyLocalDataSourceImpl(
        agencyDatabase.dao,
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
    fun provideCachesDatabase(@ApplicationContext context: Context): CachesLocalDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            CachesLocalDatabase::class.java,
            "Caches.db"
        ).fallbackToDestructiveMigration().build()
    }

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

    @Singleton
    @Provides
    fun provideAgencyDatabase(@ApplicationContext context: Context): AgencyLocalDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AgencyLocalDatabase::class.java,
            "Agency.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideInitDatabase(@ApplicationContext context: Context): InitLocalDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            InitLocalDatabase::class.java,
            "init.db"
        ).fallbackToDestructiveMigration().build()
    }
}