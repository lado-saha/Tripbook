package tech.xken.tripbook.data.sources.storage

import android.net.Uri
import kotlinx.coroutines.CoroutineDispatcher
import tech.xken.tripbook.data.AuthRepo
import tech.xken.tripbook.domain.NetworkState

class StorageRepositoryImpl(
//    private val lStorage: StorageSource,
    private val rStorage: StorageSource,
    private val authRepo: AuthRepo,
    private val ioDispatcher: CoroutineDispatcher,
    private val networkState: NetworkState
) : StorageRepository {
    override suspend fun uploadAccountPhoto(bookerId: String, uri: Uri) =
        rStorage.uploadProfilePhoto(bookerId, uri)

    override suspend fun deleteAccountPhoto(bookerId: String) =
        rStorage.deleteProfilePhoto(bookerId)

    override suspend fun accountPhotoUrl(bookerId: String) = rStorage.accountPhotoUrl(bookerId)

    // The BusinessEntity Document
    override suspend fun agencyBusinessEntityDocUrl(agencyId: String) =
        rStorage.agencyBusinessEntityDocUrl(agencyId)

    override suspend fun uploadAgencyBusinessEntityDoc(agencyId: String, docUri: Uri) =
        rStorage.uploadAgencyBusinessEntityDoc(agencyId, docUri)

    override suspend fun deleteAgencyBusinessEntityDoc(agencyId: String) =
        rStorage.deleteAgencyBusinessEntityDoc(agencyId)

    // The Licence Document
    override suspend fun agencyLicenceDocUrl(agencyId: String) =
        rStorage.agencyLicenceDocUrl(agencyId)

    override suspend fun uploadAgencyLicenceDoc(agencyId: String, docUri: Uri) =
        rStorage.uploadAgencyLicenceDoc(agencyId, docUri)

    override suspend fun deleteAgencyLicenceDoc(agencyId: String) =
        rStorage.deleteAgencyLicenceDoc(agencyId)

    // The PrivacyPolicy Document
    override suspend fun agencyPrivacyPolicyDocUrl(agencyId: String) =
        rStorage.agencyPrivacyPolicyDocUrl(agencyId)

    override suspend fun uploadAgencyPrivacyPolicyDoc(agencyId: String, docUri: Uri) =
        rStorage.uploadAgencyPrivacyPolicyDoc(agencyId, docUri)

    override suspend fun deleteAgencyPrivacyPolicyDoc(agencyId: String) =
        rStorage.deleteAgencyPrivacyPolicyDoc(agencyId)

    // The ScopeOfAuthority Document
    override suspend fun agencyScopeOfAuthorityDocUrl(agencyId: String) =
        rStorage.agencyScopeOfAuthorityDocUrl(agencyId)

    override suspend fun uploadAgencyScopeOfAuthorityDoc(agencyId: String, docUri: Uri) =
        rStorage.uploadAgencyScopeOfAuthorityDoc(agencyId, docUri)

    override suspend fun deleteAgencyScopeOfAuthorityDoc(agencyId: String) =
        rStorage.deleteAgencyScopeOfAuthorityDoc(agencyId)

    // The DutiesOfAgency Document
    override suspend fun agencyDutiesOfAgencyDocUrl(agencyId: String) =
        rStorage.agencyDutiesOfAgencyDocUrl(agencyId)

    override suspend fun uploadAgencyDutiesOfAgencyDoc(agencyId: String, docUri: Uri) =
        rStorage.uploadAgencyDutiesOfAgencyDoc(agencyId, docUri)

    override suspend fun deleteAgencyDutiesOfAgencyDoc(agencyId: String) =
        rStorage.deleteAgencyDutiesOfAgencyDoc(agencyId)

    // The RightsOfClient Document
    override suspend fun agencyRightsOfClientDocUrl(agencyId: String) =
        rStorage.agencyRightsOfClientDocUrl(agencyId)

    override suspend fun uploadAgencyRightsOfClientDoc(agencyId: String, docUri: Uri) =
        rStorage.uploadAgencyRightsOfClientDoc(agencyId, docUri)

    override suspend fun deleteAgencyRightsOfClientDoc(agencyId: String) =
        rStorage.deleteAgencyRightsOfClientDoc(agencyId)

    // The LiabilityOfAgency Document
    override suspend fun agencyLiabilityOfAgencyDocUrl(agencyId: String) =
        rStorage.agencyLiabilityOfAgencyDocUrl(agencyId)

    override suspend fun uploadAgencyLiabilityOfAgencyDoc(agencyId: String, docUri: Uri) =
        rStorage.uploadAgencyLiabilityOfAgencyDoc(agencyId, docUri)

    override suspend fun deleteAgencyLiabilityOfAgencyDoc(agencyId: String) =
        rStorage.deleteAgencyLiabilityOfAgencyDoc(agencyId)

    // The DisputeResolutionProcess Document
    override suspend fun agencyDisputeResolutionProcessDocUrl(agencyId: String) =
        rStorage.agencyDisputeResolutionProcessDocUrl(agencyId)

    override suspend fun uploadAgencyDisputeResolutionProcessDoc(agencyId: String, docUri: Uri) =
        rStorage.uploadAgencyDisputeResolutionProcessDoc(agencyId, docUri)

    override suspend fun deleteAgencyDisputeResolutionProcessDoc(agencyId: String) =
        rStorage.deleteAgencyDisputeResolutionProcessDoc(agencyId)

    // Agency grahics
    override suspend fun uploadAgencyLargeLogoUrl(agencyId: String, uri: Uri) =
        rStorage.uploadAgencyLargeLogoUrl(agencyId, uri)

    override suspend fun uploadAgencySmallLogoUrl(agencyId: String, uri: Uri) =
        rStorage.uploadAgencySmallLogoUrl(agencyId, uri)

    override suspend fun uploadHeadQuarterPhotoUrl(agencyId: String, uri: Uri) =
        rStorage.uploadHeadQuarterPhotoUrl(agencyId, uri)

    override suspend fun agencyLargeLogoUrl(agencyId: String) =
        rStorage.agencyLargeLogoUrl(agencyId)

    override suspend fun agencySmallLogoUrl(agencyId: String) =
        rStorage.agencySmallLogoUrl(agencyId)

    override suspend fun headQuarterPhotoUrl(agencyId: String) =
        rStorage.headQuarterPhotoUrl(agencyId)

    override suspend fun deleteSmallLogoUrl(agencyId: String) =
        rStorage.deleteSmallLogoUrl(agencyId)

    override suspend fun deleteAgencyLargeLogoUrl(agencyId: String) =
        rStorage.deleteLargeLogoUrl(agencyId)

    override suspend fun deleteHeadQuarterPhotoUrl(agencyId: String) =
        rStorage.deleteHeadQuarterPhotoUrl(agencyId)

}