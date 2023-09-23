package tech.xken.tripbook.data.sources


//import kotlinx.serialization.internal.NoOpEncoder.encodeDoubleElement


const val SUPABASE_URL = "https://ualtxjmmfokiqkcfpoem.supabase.co"
const val SUPABASE_KEY =
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVhbHR4am1tZm9raXFrY2Zwb2VtIiwicm9sZSI6ImFub24iLCJpYXQiOjE2ODQxODc3MzcsImV4cCI6MTk5OTc2MzczN30.XBg5vl3QurYBd1BoDnVbYjDCfZMfDn2Q5KIIfdoV39g"


/**
 * I noticed that all the functions must be stored in the public schema
 */
object RPC {
    const val AGENCY_LATEST_ACCOUNT_LOG = "get_latest_agency_account_log"
    const val AGENCY_LATEST_PHONE_SUPPORT_LOG = "get_latest_agency_phone_support_log"
    const val AGENCY_LATEST_EMAIL_SUPPORT_LOG = "get_latest_agency_email_support_log"
    const val AGENCY_LATEST_SOCIAL_SUPPORT_LOG = "get_latest_agency_social_support_log"
    const val AGENCY_LATEST_MOMO_SUPPORT_LOG = "get_latest_agency_momo_account_log"
    const val AGENCY_LATEST_OM_SUPPORT_LOG = "get_latest_agency_om_account_log"
    const val AGENCY_LATEST_REFUND_POLICY_LOG = "get_latest_agency_refund_policy_log"
}


