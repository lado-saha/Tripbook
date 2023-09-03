package tech.xken.tripbook.domain.di


//import tech.xken.tripbook.data.sources.agency.AgencyDataSource
//import tech.xken.tripbook.data.sources.agency.AgencyRepository
//import tech.xken.tripbook.data.sources.agency.AgencyRepositoryImpl
//import tech.xken.tripbook.data.sources.agency.local.AgencyLocalDataSourceImpl
//import tech.xken.tripbook.data.sources.agency.remote.AgencyRemoteDataSource
//import tech.xken.tripbook.data.sources.univ.UniverseDataSource
//import tech.xken.tripbook.data.sources.univ.UniverseRepository
//import tech.xken.tripbook.data.sources.univ.UniverseRepositoryImpl
//import tech.xken.tripbook.data.sources.univ.local.UniverseLocalDataSourceImpl
//import tech.xken.tripbook.data.sources.univ.remote.UniverseRemoteDataSource
//import tech.xken.tripbook.data.sources.init.InitDataSource
//import tech.xken.tripbook.data.sources.init.InitRepository
//import tech.xken.tripbook.data.sources.init.InitRepositoryImpl
//import tech.xken.tripbook.data.sources.init.local.InitLocalDataSourceImpl
//import tech.xken.tripbook.data.sources.init.remote.InitRemoteDataSource
import UniverseRepositoryImpl
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.createChannel
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.CoroutineDispatcher
import tech.xken.tripbook.data.AuthRepo
import tech.xken.tripbook.data.models.BookingKeys
import tech.xken.tripbook.data.sources.LocalDatabase
import tech.xken.tripbook.data.sources.SUPABASE_KEY
import tech.xken.tripbook.data.sources.SUPABASE_URL
import tech.xken.tripbook.data.sources.booker.BookerDataSource
import tech.xken.tripbook.data.sources.booker.BookerRepository
import tech.xken.tripbook.data.sources.booker.BookerRepositoryImpl
import tech.xken.tripbook.data.sources.booker.local.BookerLocalDataSource
import tech.xken.tripbook.data.sources.booker.remote.BookerRemoteDataSource
import tech.xken.tripbook.data.sources.caches.CachesDataSource
import tech.xken.tripbook.data.sources.caches.local.CachesLocalDataSourceImpl
import tech.xken.tripbook.data.sources.caches.remote.CachesRemoteDataSource
import tech.xken.tripbook.data.sources.storage.StorageRepository
import tech.xken.tripbook.data.sources.storage.StorageRepositoryImpl
import tech.xken.tripbook.data.sources.storage.StorageSource
import tech.xken.tripbook.data.sources.storage.local.LocalStorageSource
import tech.xken.tripbook.data.sources.storage.remote.RemoteStorageSource
import tech.xken.tripbook.data.sources.univ.UniverseDataSource
import tech.xken.tripbook.data.sources.univ.UniverseRepository
import tech.xken.tripbook.data.sources.univ.remote.UniverseRemoteDataSource
import tech.xken.tripbook.domain.NetworkState
import javax.inject.Qualifier
import javax.inject.Singleton


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalUniverseDataSource

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RemoteUniverseDataSourceAnnot

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalInitDataSourceAnnot

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RemoteInitDataSourceAnnot

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalBookingDataSourceAnnot

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RemoteBookingDataSourceAnnot


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalAgencyDataSourceAnnot

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RemoteAgencyDataSourceAnnot

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalCachesDataSourceAnnot

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RemoteCachesDataSourceAnnot

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class NetworkStateFlowAnnot

// Storage---
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalStorageSourceAnnot

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RemoteStorageSourceAnnot

/*--------------------------------------------------------*/
/**
 * A Module to help hilt instantiate all our repositories`
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideStorageRepository(
        @LocalStorageSourceAnnot local: StorageSource,
        @RemoteStorageSourceAnnot remote: StorageSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        authRepo: AuthRepo,
        @NetworkStateFlowAnnot networkState: NetworkState
    ): StorageRepository = StorageRepositoryImpl(
        lStorage = local,
        rStorage = remote,
        authRepo = authRepo,
        ioDispatcher = ioDispatcher,
        networkState = networkState
    )

    @Singleton//Only one instance per app session
    @Provides // Helps hilt to know that it must use this function to provide an instance of
    fun provideUniverseRepository(
//        @LocalUniverseDataSource local: UniverseDataSource,
        @RemoteUniverseDataSourceAnnot remote: UniverseDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): UniverseRepository = UniverseRepositoryImpl(
        remote,
        ioDispatcher
    )

//    @Singleton//Only one instance per app session
//    @Provides // Helps hilt to know that it must use this function to provide an instance of
//    fun provideCachesRepository(
//        @LocalCachesDataSource local: CachesDataSource,
//        @RemoteCachesDataSource remote: CachesDataSource,
//        @IoDispatcher ioDispatcher: CoroutineDispatcher,
//    ): CachesRepository = CachesRepositoryImpl(
//        local,
//        ioDispatcher
//    )

    @Singleton
    @Provides
    fun provideBookingRepository(
        @LocalBookingDataSourceAnnot local: BookerDataSource,
        @RemoteBookingDataSourceAnnot remote: BookerDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @NetworkStateFlowAnnot networkState: NetworkState,
        authRepo: AuthRepo,
        client: SupabaseClient,
    ): BookerRepository = BookerRepositoryImpl(
        local,
        remote,
        authRepo,
        ioDispatcher,
        networkState,
    )

//    @Singleton
//    @Provides
//    fun provideAgencyRepository(
//        @LocalAgencyDataSource local: AgencyDataSource,
//        @RemoteAgencyDataSource remote: AgencyDataSource,
//        @LocalBookingDataSource localBooker: BookerDataSource,
//        @RemoteBookingDataSource remoteBooker: BookerDataSource,
//        @IoDispatcher ioDispatcher: CoroutineDispatcher,
//        @NetworkStateFlowAnot networkState: NetworkState
//    ): AgencyRepository = AgencyRepositoryImpl(
//        local,
//        BookerRepositoryImpl(
//            localBooker,
//            remoteBooker,
//            ioDispatcher,
//            networkState
//        ),
//        ioDispatcher
//    )

//    @Singleton
//    @Provides
//    fun provideInitRepository(
//        @LocalInitDataSourceAnnot local: InitDataSource,
//        @RemoteInitDataSourceAnnot remote: InitDataSource,
//        @IoDispatcher ioDispatcher: CoroutineDispatcher,
//    ): InitRepository = InitRepositoryImpl(
//        local,
//        ioDispatcher
//    )
}

/**
 * Help hilt instantiate all our data sources online and local
 */
@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Singleton
    @Provides
    @LocalStorageSourceAnnot
    fun provideLocalStorageSource(
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @ApplicationContext context: Context
    ): StorageSource = LocalStorageSource(ioDispatcher, context)

    @Singleton
    @Provides
    @RemoteStorageSourceAnnot
    fun provideRemoteStorageSource(
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        client: SupabaseClient
    ): StorageSource = RemoteStorageSource(ioDispatcher, client)


    @Singleton
    @Provides
    @LocalCachesDataSourceAnnot
    fun provideLocalCachesDataSource(
        database: LocalDatabase,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): CachesDataSource = CachesLocalDataSourceImpl(
        database.cachesDao,
        ioDispatcher
    )

    @Singleton
    @Provides
    @RemoteCachesDataSourceAnnot
    fun provideRemoteCachesDataSource(): CachesDataSource = CachesRemoteDataSource

//    @Singleton
//    @Provides
//    @LocalInitDataSourceAnnot
//    fun provideLocalInitDataSource(
//        database: LocalDatabase,
//        @IoDispatcher ioDispatcher: CoroutineDispatcher,
//    ): InitDataSource = InitLocalDataSourceImpl(
//        database.initDao,
//        ioDispatcher
//    )

//    @Singleton
//    @Provides
//    @RemoteInitDataSourceAnnot
//    fun provideRemoteInitDataSource(): InitDataSource = InitRemoteDataSource

    //
//    @Singleton
//    @Provides
//    @LocalUniverseDataSource
//    fun provideLocalUniverseDataSource(
//        database: LocalDatabase,
//        @IoDispatcher ioDispatcher: CoroutineDispatcher,
//    ): UniverseDataSource = UniverseLocalDataSourceImpl(
//        database.universeDao,
//        ioDispatcher
//    )
//
    @Singleton
    @Provides
    @RemoteUniverseDataSourceAnnot
    fun provideRemoteUniverseDataSource(
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        supabaseClient: SupabaseClient
    ): UniverseDataSource = UniverseRemoteDataSource(ioDispatcher, supabaseClient)


    @Singleton
    @Provides
    @LocalBookingDataSourceAnnot
    fun provideLocalBookingDataSource(
        database: LocalDatabase,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): BookerDataSource = BookerLocalDataSource(
        database.bookerDao,
        ioDispatcher
    )


    @Singleton
    @Provides
    @RemoteBookingDataSourceAnnot
    fun provideRemoteBookingDataSource(
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        supabaseClient: SupabaseClient
    ): BookerDataSource = BookerRemoteDataSource(
        ioDispatcher,
        supabaseClient
    )


//    @Singleton
//    @Provides
//    @LocalAgencyDataSource
//    fun provideLocalAgencyDataSource(
//        agencyDatabase: LocalDatabase,
//        @IoDispatcher ioDispatcher: CoroutineDispatcher,
//    ): AgencyDataSource = AgencyLocalDataSourceImpl(
//        agencyDatabase.agencyDao,
//        ioDispatcher
//    )
//
//    @Singleton
//    @Provides
//    @RemoteAgencyDataSource
//    fun provideRemoteAgencyDataSource(): AgencyDataSource = AgencyRemoteDataSource
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
        val db = Room.databaseBuilder(
            context.applicationContext,
            LocalDatabase::class.java,
            "tripbook_local_db"
        ).fallbackToDestructiveMigration()
            .build()

        db.openHelper.writableDatabase.execSQL("PRAGMA case_sensitive_like=OFF;")
        return db
    }

    @Singleton
    @Provides
    fun provideSupabaseClient() = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(GoTrue)
        install(Postgrest)
        install(Storage)
        install(Realtime)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object ServiceModules {
    @Singleton
    @Provides
    @NetworkStateFlowAnnot
    fun provideNetworkState(@ApplicationContext context: Context) = NetworkState(context)

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create { context.preferencesDataStoreFile(BookingKeys.name) }
}
