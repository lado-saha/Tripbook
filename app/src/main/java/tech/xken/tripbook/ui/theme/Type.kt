@file:OptIn(ExperimentalTextApi::class, ExperimentalTextApi::class)

package tech.xken.tripbook.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import tech.xken.tripbook.R

//val provider = GoogleFont.Provider(
//    providerAuthority = "com.google.android.gms.fonts",
//    providerPackage = "com.google.android.gms",
//    certificates = R.array.com_google_android_gms_fonts_certs
//)

val montserratFamily = FontFamily(
//    Regular
    Font(R.font.montserrat_extralight, FontWeight.ExtraLight),
    Font(R.font.montserrat_light, FontWeight.Light),
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold),
    Font(R.font.montserrat_bold, FontWeight.Bold),
    Font(R.font.montserrat_extrabold, FontWeight.ExtraBold),
// Italic
    Font(R.font.montserrat_extralightitalic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(R.font.montserrat_lightitalic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.montserrat_mediumitalic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.montserrat_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.montserrat_bolditalic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.montserrat_extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic),
)

val Typography = Typography(defaultFontFamily = montserratFamily)


// Set of Material typography styles to start with
//val Typography = Typography(
//    body1 = TextStyle(
////        fontFamily = fontFamily,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp
//    ),
////     Other default text styles to override
//    button = TextStyle(
////        fontFamily = fontFamily,
//        fontWeight = FontWeight.W500,
//        fontSize = 14.sp
//    ),
//    caption = TextStyle(
////        fontFamily = fontFamily,
//        fontWeight = FontWeight.Normal,
//        fontSize = 12.sp
//    ),
//    defaultFontFamily = montserratFamily,
//    body2 = TextStyle(
//        fontWeight = FontWeight.Bold,
//        fontSize = 16.sp
//    ),
//    h4 = TextStyle(
//        fontWeight = FontWeight.SemiBold,
//        fontSize = 18.sp
//    ),


//)