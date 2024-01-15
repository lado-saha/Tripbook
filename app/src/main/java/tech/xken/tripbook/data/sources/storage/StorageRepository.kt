package tech.xken.tripbook.data.sources.storage

import android.net.Uri
import tech.xken.tripbook.data.models.Results
import tech.xken.tripbook.domain.di.IoDispatcher


interface StorageRepository {
    // Booker profile
    suspend fun uploadAccountPhoto(bookerId: String, uri: Uri): Results<String>
    suspend fun deleteAccountPhoto(bookerId: String): Results<Unit>
    suspend fun accountPhotoUrl(bookerId: String): Results<String>

    // Agency Graphics
    suspend fun uploadAgencyLargeLogoUrl(agencyId: String, uri: Uri): Results<Any>
    suspend fun uploadAgencySmallLogoUrl(agencyId: String, uri: Uri): Results<Any>
    suspend fun uploadHeadQuarterPhotoUrl(agencyId: String, uri: Uri): Results<Any>

    suspend fun agencyLargeLogoUrl(agencyId: String): Results<String>
    suspend fun agencySmallLogoUrl(agencyId: String): Results<String>
    suspend fun headQuarterPhotoUrl(agencyId: String): Results<String>

    suspend fun deleteSmallLogoUrl(agencyId: String): Results<Unit>
    suspend fun deleteAgencyLargeLogoUrl(agencyId: String): Results<Unit>
    suspend fun deleteHeadQuarterPhotoUrl(agencyId: String): Results<Unit>

    // The BusinessEntity Document
    suspend fun agencyBusinessEntityDocUrl(agencyId: String): Results<String>
    suspend fun uploadAgencyBusinessEntityDoc(agencyId: String, docUri: Uri): Results<Any>
    suspend fun deleteAgencyBusinessEntityDoc(agencyId: String): Results<Unit>

    // The Licence Document
    suspend fun agencyLicenceDocUrl(agencyId: String): Results<String>
    suspend fun uploadAgencyLicenceDoc(agencyId: String, docUri: Uri): Results<Any>
    suspend fun deleteAgencyLicenceDoc(agencyId: String): Results<Unit>

    // The PrivacyPolicy Document
    suspend fun agencyPrivacyPolicyDocUrl(agencyId: String): Results<String>
    suspend fun uploadAgencyPrivacyPolicyDoc(agencyId: String, docUri: Uri): Results<Any>
    suspend fun deleteAgencyPrivacyPolicyDoc(agencyId: String): Results<Unit>

    // The ScopeOfAuthority Document
    suspend fun agencyScopeOfAuthorityDocUrl(agencyId: String): Results<String>
    suspend fun uploadAgencyScopeOfAuthorityDoc(agencyId: String, docUri: Uri): Results<Any>
    suspend fun deleteAgencyScopeOfAuthorityDoc(agencyId: String): Results<Unit>

    // The DutiesOfAgency Document
    suspend fun agencyDutiesOfAgencyDocUrl(agencyId: String): Results<String>
    suspend fun uploadAgencyDutiesOfAgencyDoc(agencyId: String, docUri: Uri): Results<Any>
    suspend fun deleteAgencyDutiesOfAgencyDoc(agencyId: String): Results<Unit>

    // The RightsOfClient Document
    suspend fun agencyRightsOfClientDocUrl(agencyId: String): Results<String>
    suspend fun uploadAgencyRightsOfClientDoc(agencyId: String, docUri: Uri): Results<Any>
    suspend fun deleteAgencyRightsOfClientDoc(agencyId: String): Results<Unit>

    // The LiabilityOfAgency Document
    suspend fun agencyLiabilityOfAgencyDocUrl(agencyId: String): Results<String>
    suspend fun uploadAgencyLiabilityOfAgencyDoc(agencyId: String, docUri: Uri): Results<Any>
    suspend fun deleteAgencyLiabilityOfAgencyDoc(agencyId: String): Results<Unit>

    // The DisputeResolutionProcess Document
    suspend fun agencyDisputeResolutionProcessDocUrl(agencyId: String): Results<String>
    suspend fun uploadAgencyDisputeResolutionProcessDoc(agencyId: String, docUri: Uri): Results<Any>
    suspend fun deleteAgencyDisputeResolutionProcessDoc(agencyId: String): Results<Unit>
}