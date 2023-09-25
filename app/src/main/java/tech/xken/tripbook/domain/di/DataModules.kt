package tech.xken.tripbook.domain.di

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
import io.github.jan.supabase.realtime.RealtimeChannel
import io.github.jan.supabase.realtime.createChannel
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.CoroutineDispatcher
import tech.xken.tripbook.data.AuthRepo
import tech.xken.tripbook.data.models.BookingKeys
import tech.xken.tripbook.data.models.Channel
import tech.xken.tripbook.data.sources.LocalDatabase
import tech.xken.tripbook.data.sources.SUPABASE_KEY
import tech.xken.tripbook.data.sources.SUPABASE_URL
import tech.xken.tripbook.data.sources.agency.AgencyDataSource
import tech.xken.tripbook.data.sources.agency.AgencyRepository
import tech.xken.tripbook.data.sources.agency.AgencyRepositoryImpl
import tech.xken.tripbook.data.sources.agency.local.AgencyLocalDataSource
import tech.xken.tripbook.data.sources.agency.remote.AgencyRemoteDataSource
import tech.xken.tripbook.data.sources.booker.BookerDataSource
import tech.xken.tripbook.data.sources.booker.BookerRepository
import tech.xken.tripbook.data.sources.booker.BookerRepositoryImpl
import tech.xken.tripbook.data.sources.booker.local.BookerLocalDataSource
import tech.xken.tripbook.data.sources.booker.remote.BookerRemoteDataSource
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
annotation class NetworkStateFlowAnnot

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalStorageSourceAnnot

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RemoteStorageSourceAnnot

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RealtimeAgencyChannel
/**
 * A Module to help hilt instantiate all our repositories
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
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): UniverseRepository = UniverseRepositoryImpl(remote, ioDispatcher)

    @Singleton
    @Provides
    fun provideBookingRepository(
        @LocalBookingDataSourceAnnot localDS: BookerDataSource,
        @RemoteBookingDataSourceAnnot remoteDS: BookerDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @NetworkStateFlowAnnot networkState: NetworkState,
        authRepo: AuthRepo,
    ): BookerRepository = BookerRepositoryImpl(
        localDS,
        remoteDS,
        authRepo,
        ioDispatcher,
        networkState,
    )

    @Singleton
    @Provides
    fun provideAgencyRepository(
        @LocalAgencyDataSourceAnnot localDS: AgencyDataSource,
        @RemoteAgencyDataSourceAnnot remoteDS: AgencyDataSource,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @NetworkStateFlowAnnot networkState: NetworkState
    ): AgencyRepository = AgencyRepositoryImpl(
        localDS = localDS,
        remoteDS = remoteDS,
        ioDispatcher = ioDispatcher
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
    @RemoteAgencyDataSourceAnnot
    fun provideRemoteAgencyDataSource(
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        client: SupabaseClient,
        @RealtimeAgencyChannel channel: RealtimeChannel
    ): AgencyDataSource = AgencyRemoteDataSource(
        ioDispatcher,
        client,
        channel
    )

    @Singleton
    @Provides
    @RemoteBookingDataSourceAnnot
    fun provideRemoteBookingDataSource(
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        client: SupabaseClient
    ): BookerDataSource = BookerRemoteDataSource(ioDispatcher, client)

    @Singleton
    @Provides
    @RemoteUniverseDataSourceAnnot
    fun provideRemoteUniverseDataSource(
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        client: SupabaseClient
    ): UniverseDataSource = UniverseRemoteDataSource(ioDispatcher, client)

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
    @LocalAgencyDataSourceAnnot
    fun provideLocalAgencyDataSource(
        database: LocalDatabase,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): AgencyDataSource = AgencyLocalDataSource(
        database.agencyDao,
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
    @RealtimeAgencyChannel
    fun provideAgencyChannel(
        client: SupabaseClient
    ) = client.realtime.createChannel(Channel.AGENCY)

    @Singleton
    @Provides
    @NetworkStateFlowAnnot
    fun provideNetworkState(@ApplicationContext context: Context) = NetworkState(context)

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create { context.preferencesDataStoreFile(BookingKeys.name) }
}
