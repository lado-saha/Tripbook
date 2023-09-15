package tech.xken.tripbook.data

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.OtpType
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Phone
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.createChannel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.put
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.booker.Booker
import tech.xken.tripbook.data.models.booker.BookerCredentials
import javax.inject.Inject


class AuthRepo @Inject constructor(
    private val client: SupabaseClient
) {
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    val authClient = client.gotrue
    val dbClient = client.postgrest

    val isSignedIn = MutableStateFlow(authClient.currentUserOrNull() != null)
    val bookerId get() = authClient.currentSessionOrNull()?.user?.id
    val isSignedInFlow = authClient.sessionStatus.map {
        it is SessionStatus.Authenticated
    }.onEach {
        isSignedIn.value = it
    }

    val agencyId: String? = "gp"


    init {
        CoroutineScope(ioDispatcher).launch {
            when (authClient.sessionStatus.value) {
                is SessionStatus.Authenticated -> {
                    Log.e("AuthRepo", "You're online and authenticated")
                    authClient.startAutoRefreshForCurrentSession()
                    authClient.sessionManager.saveSession(authClient.currentSessionOrNull()!!)
                }

                is SessionStatus.LoadingFromStorage -> {
                    Log.e("AuthRepo", "You're offline and loading from storage")
                }

                is SessionStatus.NetworkError -> {
                    Log.e("AuthRepo", "You're offline")
                    authClient.loadFromStorage(true)
                }

                is SessionStatus.NotAuthenticated -> {
                    Log.e("AuthRepo", "You're not authenticated")
                    Log.e("Auth repo", authClient.loadFromStorage(true).toString())
                }
            }
        }
    }

    /**
     * Creates a new user in the auth schema and return the generated id
     */
    suspend fun phoneSignUp(
        credentials: BookerCredentials
    ) = withContext(ioDispatcher) {
        return@withContext try {
            Results.Success(
                authClient.signUpWith(Phone) {
                    password = credentials.password
                    phoneNumber = credentials.fullPhoneNumber
                }
            )
        } catch (e: Exception) {
            Results.Failure(e)
        }
    }

    /**
     * Tells us if a booker has already created a profile or not
     */
    val hasAccount
        get() = authClient.currentSessionOrNull()?.user?.userMetadata?.get(
            "has_account"
        )?.let { Json.decodeFromJsonElement<Boolean>(it) } ?: false

    val hasAccountPhoto
        get() = authClient.currentSessionOrNull()?.user?.userMetadata?.get(
            "has_account_photo"
        )?.let { Json.decodeFromJsonElement<Boolean>(it) } ?: false

    suspend fun forceRefreshUser() = withContext(ioDispatcher) {
        return@withContext try {
            authClient.retrieveUserForCurrentSession(true)
        } catch (_: Exception) {

        }
    }

    suspend fun updateHasAccountFlag(new: Boolean) = withContext(ioDispatcher) {
        return@withContext try {
            authClient.modifyUser {
                data { put("has_account", new) }
            }
            Results.Success(new)
        } catch (e: Exception) {
            Results.Failure(e)
        }
    }

    suspend fun updateHasAccountPhotoFlag(new: Boolean) = withContext(ioDispatcher) {
        return@withContext try {
            authClient.modifyUser {
                data { put("has_account_photo", new) }
            }
            authClient.refreshCurrentSession()
        } catch (e: Exception) {
            Results.Failure(e)
        }
    }

    suspend fun deleteBooker(bookerId: String) = withContext(ioDispatcher) {
        return@withContext try {
            authClient
            authClient.refreshCurrentSession()
        } catch (e: Exception) {
            Results.Failure(e)
        }
    }

    /**
     * Sign in a user to database
     */
    suspend fun signUpBooker(credentials: BookerCredentials) = withContext(ioDispatcher) {
        return@withContext try {
            Results.Success(
                authClient.sendOtpTo(Phone, createUser = true) {
                    phoneNumber = credentials.fullPhoneNumber
                    password = credentials.password
                }
            )
        } catch (e: Exception) {
            Results.Failure(e)
        }
    }

    /**
     * Sign in a user to database
     */
    suspend fun signInBooker(credentials: BookerCredentials) = withContext(ioDispatcher) {
        return@withContext try {
            Results.Success(
                authClient.sendOtpTo(Phone, createUser = false) {
                    phoneNumber = credentials.fullPhoneNumber
                    password = credentials.password
                }
            )
        } catch (e: Exception) {
            Results.Failure(e)
        }
    }

    /**
     * Challenges user to enter the 6 digit code and checks to verify
     */
    suspend fun bookerPhoneVerification(credentials: BookerCredentials) =
        withContext(ioDispatcher) {
            return@withContext try {
                Results.Success(authClient.verifyPhoneOtp(
                    type = OtpType.Phone.SMS,
                    phoneNumber = credentials.fullPhoneNumber,
                    token = credentials.token!!,
                ).also {
                    authClient.refreshCurrentSession()
                })
            } catch (e: Exception) {
                Results.Failure(e)
            }
        }

    suspend fun resendPhoneToken(credentials: BookerCredentials) = withContext(ioDispatcher) {
        return@withContext try {
            Results.Success(
                authClient.resendPhone(OtpType.Phone.SMS, credentials.fullPhoneNumber)
            )
        } catch (e: Exception) {
            Results.Failure(e)
        }
    }

    suspend fun signOut() = withContext(ioDispatcher) {
        return@withContext try {
            Results.Success(
                authClient.logout()
            )
        } catch (e: Exception) {
            Results.Failure(e)
        }
    }


//    begin
//    insert into public.users (id, full_name, avatar_url)
//    values (new.id, new.raw_user_meta_data->>'full_name', new.raw_user_meta_data->>'avatar_url');
//    return new;
//    end;
}