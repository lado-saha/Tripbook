package tech.xken.tripbook.data.sources

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure


//import kotlinx.serialization.internal.NoOpEncoder.encodeDoubleElement


const val SUPABASE_URL = "https://ualtxjmmfokiqkcfpoem.supabase.co"
const val SUPABASE_KEY =
    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVhbHR4am1tZm9raXFrY2Zwb2VtIiwicm9sZSI6ImFub24iLCJpYXQiOjE2ODQxODc3MzcsImV4cCI6MTk5OTc2MzczN30.XBg5vl3QurYBd1BoDnVbYjDCfZMfDn2Q5KIIfdoV39g"





