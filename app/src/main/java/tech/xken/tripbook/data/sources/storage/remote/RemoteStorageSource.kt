@file:OptIn(SupabaseExperimental::class)

package tech.xken.tripbook.data.sources.storage.remote

import android.net.Uri
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.withContext
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.data.models.Results.*
import tech.xken.tripbook.data.models.agency.AgencyGraphics
import tech.xken.tripbook.data.models.agency.AgencyLegalDocs.Companion.docBusinessEntityUrlOrPath
import tech.xken.tripbook.data.models.agency.AgencyLegalDocs.Companion.docDisputeResolutionUrlOrPath
import tech.xken.tripbook.data.models.agency.AgencyLegalDocs.Companion.docDutiesOfAgencyUrlOrPath
import tech.xken.tripbook.data.models.agency.AgencyLegalDocs.Companion.docLiabilityOfAgencyUrlOrPath
import tech.xken.tripbook.data.models.agency.AgencyLegalDocs.Companion.docLicenceUrlOrPath
import tech.xken.tripbook.data.models.agency.AgencyLegalDocs.Companion.docPrivacyPolicyUrlOrPath
import tech.xken.tripbook.data.models.agency.AgencyLegalDocs.Companion.docRightsOfClientsUrlOrPath
import tech.xken.tripbook.data.models.agency.AgencyLegalDocs.Companion.docScopeOfAuthorityUrlOrPath
import tech.xken.tripbook.data.sources.storage.StorageSource
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * the supabse cloud storage is a place to store all documents, images and videos
 */
@OptIn(SupabaseExperimental::class)
class RemoteStorageSource @Inject constructor(
    var ioDispatcher: CoroutineContext,
    var client: SupabaseClient
) : StorageSource {
    override suspend fun accountPhotoUrl(bookerId: String) = withContext(ioDispatcher) {
        return@withContext try {
            Success(
                client.storage["bookers"].publicUrl("account_photos/${bookerId}.jpeg")
            )
        } catch (e: Exception) {
            Failure(e)
        }
    }

    @OptIn(SupabaseExperimental::class)
    override suspend fun uploadProfilePhoto(bookerId: String, uri: Uri): Results<String> =
        withContext(ioDispatcher) {
            return@withContext try {
                Success(
                    client.storage["bookers"].upload("account_photos/${bookerId}.jpeg", uri, true)
                )
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun deleteProfilePhoto(bookerId: String) = withContext(ioDispatcher) {
        return@withContext try {
//            We modify the authClient
            Success(client.storage["bookers"].delete("account_photos/${bookerId}.jpeg"))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    // Agency graphics
    override suspend fun uploadAgencyLargeLogoUrl(agencyId: String, uri: Uri) =
        withContext(ioDispatcher) {
            try {
                Success(
                    client.storage["agency"].upload(
                        AgencyGraphics.imgLargeLogoUrlOrPath(
                            agencyId,
                            true
                        ), uri
                    )
                )
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun uploadAgencySmallLogoUrl(agencyId: String, uri: Uri) =
        withContext(ioDispatcher) {
            try {
                Success(
                    client.storage["agency"].upload(
                        AgencyGraphics.imgSmallLogoUrlOrPath(
                            agencyId,
                            true
                        ), uri
                    )
                )
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun uploadHeadQuarterPhotoUrl(agencyId: String, uri: Uri) =
        withContext(ioDispatcher) {
            try {
                Success(
                    client.storage["agency"].upload(
                        AgencyGraphics.imgHeadQuarterPictureUrlOrPath(
                            agencyId,
                            true
                        ), uri
                    )
                )
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun agencyLargeLogoUrl(agencyId: String) = withContext(ioDispatcher) {
        try {
            Success(
                client.storage["agency"].publicUrl(
                    AgencyGraphics.imgLargeLogoUrlOrPath(
                        agencyId,
                        true
                    )
                )
            )
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun agencySmallLogoUrl(agencyId: String) = withContext(ioDispatcher) {
        try {
            Success(
                client.storage["agency"].publicUrl(
                    AgencyGraphics.imgSmallLogoUrlOrPath(
                        agencyId,
                        true
                    )
                )
            )
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun headQuarterPhotoUrl(agencyId: String) = withContext(ioDispatcher) {
        try {
            Success(
                client.storage["agency"].publicUrl(
                    AgencyGraphics.imgHeadQuarterPictureUrlOrPath(
                        agencyId,
                        true
                    )
                )
            )
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun deleteSmallLogoUrl(agencyId: String) = withContext(ioDispatcher) {
        try {
            Success(
                client.storage["agency"].delete(
                    AgencyGraphics.imgSmallLogoUrlOrPath(
                        agencyId,
                        true
                    )
                )
            )
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun deleteLargeLogoUrl(agencyId: String) = withContext(ioDispatcher) {
        try {
            Success(
                client.storage["agency"].delete(
                    AgencyGraphics.imgLargeLogoUrlOrPath(
                        agencyId,
                        true
                    )
                )
            )
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun deleteHeadQuarterPhotoUrl(agencyId: String) = withContext(ioDispatcher) {
        try {
            Success(
                client.storage["agency"].delete(
                    AgencyGraphics.imgHeadQuarterPictureUrlOrPath(
                        agencyId,
                        true
                    )
                )
            )
        } catch (e: Exception) {
            Failure(e)
        }
    }

    // The BusinessEntity Document
    override suspend fun agencyBusinessEntityDocUrl(agencyId: String) = withContext(ioDispatcher) {
        try {
            Success(client.storage["agency"].publicUrl(docBusinessEntityUrlOrPath(agencyId)))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun uploadAgencyBusinessEntityDoc(agencyId: String, docUri: Uri) =
        withContext(ioDispatcher) {
            try {
                Success(
                    client.storage["agency"].upload(
                        docBusinessEntityUrlOrPath(agencyId, true),
                        docUri
                    )
                )
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun deleteAgencyBusinessEntityDoc(agencyId: String) =
        withContext(ioDispatcher) {
            try {
                Success(
                    client.storage["agency"].delete(
                        docBusinessEntityUrlOrPath(
                            agencyId,
                            true
                        )
                    )
                )
            } catch (e: Exception) {
                Failure(e)
            }
        }

    // The Licence Document
    override suspend fun agencyLicenceDocUrl(agencyId: String) = withContext(ioDispatcher) {
        try {
            Success(client.storage["agency"].publicUrl(docLicenceUrlOrPath(agencyId)))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun uploadAgencyLicenceDoc(agencyId: String, docUri: Uri) =
        withContext(ioDispatcher) {
            try {
                Success(
                    client.storage["agency"].upload(
                        docLicenceUrlOrPath(
                            agencyId,
                            true
                        ), docUri
                    )
                )
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun deleteAgencyLicenceDoc(agencyId: String) = withContext(ioDispatcher) {
        try {
            Success(client.storage["agency"].delete(docLicenceUrlOrPath(agencyId, true)))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    // The PrivacyPolicy Document
    override suspend fun agencyPrivacyPolicyDocUrl(agencyId: String) = withContext(ioDispatcher) {
        try {
            Success(client.storage["agency"].publicUrl(docPrivacyPolicyUrlOrPath(agencyId)))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun uploadAgencyPrivacyPolicyDoc(agencyId: String, docUri: Uri) =
        withContext(ioDispatcher) {
            try {
                Success(
                    client.storage["agency"].upload(
                        docPrivacyPolicyUrlOrPath(agencyId, true),
                        docUri
                    )
                )
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun deleteAgencyPrivacyPolicyDoc(agencyId: String) =
        withContext(ioDispatcher) {
            try {
                Success(client.storage["agency"].delete(docPrivacyPolicyUrlOrPath(agencyId)))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    // The ScopeOfAuthority Document
    override suspend fun agencyScopeOfAuthorityDocUrl(agencyId: String) =
        withContext(ioDispatcher) {
            try {
                Success(client.storage["agency"].publicUrl(docScopeOfAuthorityUrlOrPath(agencyId)))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun uploadAgencyScopeOfAuthorityDoc(agencyId: String, docUri: Uri) =
        withContext(ioDispatcher) {
            try {
                Success(
                    client.storage["agency"].upload(
                        docScopeOfAuthorityUrlOrPath(agencyId, true),
                        docUri
                    )
                )
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun deleteAgencyScopeOfAuthorityDoc(agencyId: String) =
        withContext(ioDispatcher) {
            try {
                Success(client.storage["agency"].delete(docScopeOfAuthorityUrlOrPath(agencyId)))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    // The DutiesOfAgency Document
    override suspend fun agencyDutiesOfAgencyDocUrl(agencyId: String) = withContext(ioDispatcher) {
        try {
            Success(client.storage["agency"].publicUrl(docDutiesOfAgencyUrlOrPath(agencyId)))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun uploadAgencyDutiesOfAgencyDoc(agencyId: String, docUri: Uri) =
        withContext(ioDispatcher) {
            try {
                Success(
                    client.storage["agency"].upload(
                        docDutiesOfAgencyUrlOrPath(agencyId, true),
                        docUri
                    )
                )
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun deleteAgencyDutiesOfAgencyDoc(agencyId: String) =
        withContext(ioDispatcher) {
            try {
                Success(client.storage["agency"].delete(docDutiesOfAgencyUrlOrPath(agencyId)))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    // The RightsOfClient Document
    override suspend fun agencyRightsOfClientDocUrl(agencyId: String) = withContext(ioDispatcher) {
        try {
            Success(client.storage["agency"].publicUrl(docRightsOfClientsUrlOrPath(agencyId)))
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun uploadAgencyRightsOfClientDoc(agencyId: String, docUri: Uri) =
        withContext(ioDispatcher) {
            try {
                Success(
                    client.storage["agency"].upload(
                        docRightsOfClientsUrlOrPath(agencyId, true),
                        docUri
                    )
                )
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun deleteAgencyRightsOfClientDoc(agencyId: String) =
        withContext(ioDispatcher) {
            try {
                Success(
                    client.storage["agency"].delete(
                        docRightsOfClientsUrlOrPath(
                            agencyId,
                            true
                        )
                    )
                )
            } catch (e: Exception) {
                Failure(e)
            }
        }

    // The LiabilityOfAgency Document
    override suspend fun agencyLiabilityOfAgencyDocUrl(agencyId: String) =
        withContext(ioDispatcher) {
            try {
                Success(client.storage["agency"].publicUrl(docLiabilityOfAgencyUrlOrPath(agencyId)))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun uploadAgencyLiabilityOfAgencyDoc(agencyId: String, docUri: Uri) =
        withContext(ioDispatcher) {
            try {
                Success(
                    client.storage["agency"].upload(
                        docLiabilityOfAgencyUrlOrPath(agencyId, true),
                        docUri
                    )
                )
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun deleteAgencyLiabilityOfAgencyDoc(agencyId: String) =
        withContext(ioDispatcher) {
            try {
                Success(client.storage["agency"].delete(docLiabilityOfAgencyUrlOrPath(agencyId)))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    // The DisputeResolutionProcess Document
    override suspend fun agencyDisputeResolutionProcessDocUrl(agencyId: String) =
        withContext(ioDispatcher) {
            try {
                Success(client.storage["agency"].publicUrl(docDisputeResolutionUrlOrPath(agencyId)))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun uploadAgencyDisputeResolutionProcessDoc(agencyId: String, docUri: Uri) =
        withContext(ioDispatcher) {
            try {
                Success(
                    client.storage["agency"].upload(
                        docDisputeResolutionUrlOrPath(agencyId, true),
                        docUri
                    )
                )
            } catch (e: Exception) {
                Failure(e)
            }
        }

    override suspend fun deleteAgencyDisputeResolutionProcessDoc(agencyId: String) =
        withContext(ioDispatcher) {
            try {
                Success(client.storage["agency"].delete(docDisputeResolutionUrlOrPath(agencyId)))
            } catch (e: Exception) {
                Failure(e)
            }
        }

    /*override suspend fun profilePhoto(bookerId: String) = withContext(ioDispatcher) {
        return@withContext try {
            val byteArray = client.storage["booker"] .downloadPublic("$bookerId/profile") {
                size(100, 100)
                quality = 100
                fill()
            }
            Results.Success(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size))
        } catch (e: Exception) {
            Results.Failure(e)
        }
    }*/

}