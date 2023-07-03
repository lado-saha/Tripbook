package tech.xken.tripbook.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.OtpType
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Phone
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.xken.tripbook.data.models.BookerCredentials
import tech.xken.tripbook.data.models.Results
import javax.inject.Inject


class AuthRepo @Inject constructor(
    private val client: SupabaseClient,

    ) {
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    val authClient = client.gotrue


    init {
        CoroutineScope(ioDispatcher).launch {
            if(authClient.currentSessionOrNull() != null)
                authClient.startAutoRefreshForCurrentSession()
        }
    }

    /**
     * Creates a new user in the auth schema and return the generated id
     */
//    suspend fun phoneSignUp(
//        credentials: BookerCredentials
//    ) = withContext(ioDispatcher) {
//        return@withContext try {
//            Results.Success(
//                authClient.signUpWith(Phone) {
//                    password = credentials.password
//                    phoneNumber = credentials.formattedPhone
//                }
//            )
//        } catch (e: Exception) {
//            Results.Failure(e)
//        }
//    }

    /**
     * Tells us if a booker has already created a profile or not
     */
    val hasProfile get() = authClient.currentSessionOrNull()?.user?.userMetadata?.get("has_profile") != null


    /**
     * Sign in a user to database
     */
    suspend fun bookerPhoneAuth(credentials: BookerCredentials) = withContext(ioDispatcher) {
        return@withContext try {
            Results.Success(
                authClient.sendOtpTo(Phone, createUser = true) {
                    phoneNumber = credentials.formattedPhone
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
                Results.Success(
                    authClient.verifyPhoneOtp(
                        type = OtpType.Phone.SMS,
                        phoneNumber = credentials.formattedPhone,
                        token = credentials.token!!,
                    )
                )
            } catch (e: Exception) {
                Results.Failure(e)
            }
        }

    suspend fun resendPhoneToken(credentials: BookerCredentials) = withContext(ioDispatcher) {
        return@withContext try {
            Results.Success(
                authClient.sendOtpTo(Phone, createUser = false) {
                    phoneNumber = credentials.formattedPhone
                }
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