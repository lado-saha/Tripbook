package tech.xken.tripbook.domain.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.PropertyConversionMethod
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.CoroutineDispatcher
import tech.xken.tripbook.data.sources.LocalDatabase
import tech.xken.tripbook.data.sources.SUPABASE_KEY
import tech.xken.tripbook.data.sources.SUPABASE_URL
import tech.xken.tripbook.data.sources.agency.AgencyDataSource
import tech.xken.tripbook.data.sources.agency.AgencyRepository
import tech.xken.tripbook.data.sources.agency.AgencyRepositoryImpl
import tech.xken.tripbook.data.sources.agency.local.AgencyLocalDataSourceImpl
import tech.xken.tripbook.data.sources.agency.remote.AgencyRemoteDataSource
import tech.xken.tripbook.data.sources.booker.BookerDataSource
import tech.xken.tripbook.data.sources.booker.BookerRepository
import tech.xken.tripbook.data.sources.booker.BookerRepositoryImpl
import tech.xken.tripbook.data.sources.booker.local.BookerLocalDataSourceImpl
import tech.xken.tripbook.data.sources.booker.remote.BookerRemoteDataSource
import tech.xken.tripbook.data.sources.caches.CachesDataSource
import tech.xken.tripbook.data.sources.caches.CachesRepository
import tech.xken.tripbook.data.sources.caches.CachesRepositoryImpl
import tech.xken.tripbook.data.sources.caches.local.CachesLocalDataSourceImpl
import tech.xken.tripbook.data.sources.caches.remote.CachesRemoteDataSource
import tech.xken.tripbook.data.sources.init.InitDataSource
import tech.xken.tripbook.data.sources.init.InitRepository
import tech.xken.tripbook.data.sources.init.InitRepositoryImpl
import tech.xken.tripbook.data.sources.init.local.InitLocalDataSourceImpl
import tech.xken.tripbook.data.sources.init.remote.InitRemoteDataSource
import tech.xken.tripbook.data.sources.universe.UniverseDataSource
import tech.xken.tripbook.data.sources.universe.UniverseRepository
import tech.xken.tripbook.data.sources.universe.UniverseRepositoryImpl
import tech.xken.tripbook.data.sources.universe.local.UniverseLocalDataSourceImpl
import tech.xken.tripbook.data.sources.universe.remote.UniverseRemoteDataSource
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalUniverseDataSource

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RemoteUniverseDataSource

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalInitDataSource

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RemoteInitDataSource

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalBookingDataSource

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RemoteBookingDataSource

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalAgencyDataSource

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RemoteAgencyDataSource

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalCachesDataSource

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RemoteCachesDataSource


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
        @LocalUniverseDataSource local: UniverseDataSource,
        @RemoteUniverseDataSource remote: UniverseDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): UniverseRepository = UniverseRepositoryImpl(
        local,
        ioDispatcher
    )

    @Singleton//Only one instance per app session
    @Provides // Helps hilt to know that it must use this function to provide an instance of
    fun provideCachesRepository(
        @LocalCachesDataSource local: CachesDataSource,
        @RemoteCachesDataSource remote: CachesDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): CachesRepository = CachesRepositoryImpl(
        local,
        ioDispatcher
    )

    @Singleton
    @Provides
    fun provideBookingRepository(
        @LocalBookingDataSource local: BookerDataSource,
        @RemoteBookingDataSource remote: BookerDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): BookerRepository = BookerRepositoryImpl(
        local,
        ioDispatcher
    )

    @Singleton
    @Provides
    fun provideAgencyRepository(
        @LocalAgencyDataSource local: AgencyDataSource,
        @RemoteAgencyDataSource remote: AgencyDataSource,
        @LocalBookingDataSource localBooker: BookerDataSource,
        @RemoteBookingDataSource remoteBooker: BookerDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): AgencyRepository = AgencyRepositoryImpl(
        local,
        BookerRepositoryImpl(
            localBooker,
            ioDispatcher
        ),
        ioDispatcher
    )

    @Singleton
    @Provides
    fun provideInitRepository(
        @LocalInitDataSource local: InitDataSource,
        @RemoteInitDataSource remote: InitDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): InitRepository = InitRepositoryImpl(
        local,
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
        database: LocalDatabase,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): CachesDataSource = CachesLocalDataSourceImpl(
        database.cachesDao,
        ioDispatcher
    )

    @Singleton
    @Provides
    @RemoteCachesDataSource
    fun provideRemoteCachesDataSource(): CachesDataSource = CachesRemoteDataSource

    @Singleton
    @Provides
    @LocalInitDataSource
    fun provideLocalInitDataSource(
        database: LocalDatabase,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): InitDataSource = InitLocalDataSourceImpl(
        database.initDao,
        ioDispatcher
    )

    @Singleton
    @Provides
    @RemoteInitDataSource
    fun provideRemoteInitDataSource(): InitDataSource = InitRemoteDataSource

    @Singleton
    @Provides
    @LocalUniverseDataSource
    fun provideLocalUniverseDataSource(
        database: LocalDatabase,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): UniverseDataSource = UniverseLocalDataSourceImpl(
        database.universeDao,
        ioDispatcher
    )

    @Singleton
    @Provides
    @RemoteUniverseDataSource
    fun provideRemoteUniverseDataSource(): UniverseDataSource = UniverseRemoteDataSource

    @Singleton
    @Provides
    @LocalBookingDataSource
    fun provideLocalBookingDataSource(
        database: LocalDatabase,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): BookerDataSource = BookerLocalDataSourceImpl(
        database.bookerDao,
        ioDispatcher
    )

    @Singleton
    @Provides
    @RemoteBookingDataSource
    fun provideRemoteBookingDataSource(): BookerDataSource = BookerRemoteDataSource

    @Singleton
    @Provides
    @LocalAgencyDataSource
    fun provideLocalAgencyDataSource(
        agencyDatabase: LocalDatabase,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): AgencyDataSource = AgencyLocalDataSourceImpl(
        agencyDatabase.agencyDao,
        ioDispatcher
    )

    @Singleton
    @Provides
    @RemoteAgencyDataSource
    fun provideRemoteAgencyDataSource(): AgencyDataSource = AgencyRemoteDataSource
}

/**
 * Helps hilt instantiate our local database
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    /*
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
    */
    @Singleton
    @Provides
    fun provideLocalDatabase(@ApplicationContext context: Context): LocalDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            LocalDatabase::class.java,
            "tripbook_local_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideSupabaseClient() = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(GoTrue)
        install(Postgrest) {
            propertyConversionMethod = PropertyConversionMethod.CAMEL_CASE_TO_SNAKE_CASE
        }
        install(Storage)
        install(Realtime)
    }
}
