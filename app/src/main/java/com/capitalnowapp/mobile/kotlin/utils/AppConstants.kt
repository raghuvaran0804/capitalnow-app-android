package com.capitalnowapp.mobile.kotlin.utils

class AppConstants {

    interface FrequentApps {
        companion object {
            const val FACEBOOK = "Facebook"
            const val INSTAGRAM = "Instagram"
            const val SNAPCHAT = "Snapchat"
            const val SWIGGY = "Swiggy"
            const val ZOMATO = "Zomato"
            const val AMAZON = "Amazon"
            const val FLIPKART = "Flipkart"
            const val MYNTRA = "Myntra"
            const val LINKED_IN = "LinkedIn"
            const val INDEED = "Indeed"
            const val NAUKRI = "Naukri"
            const val UBER = "Uber"
            const val OLA = "Ola"
            const val QUICKRIDE = "Quickride"
            const val QUORA = "Quora"
            const val TOI = "TOI"
            const val INSHORTS = "Inshorts"
            const val FUNDS_INDIA = "Funds India"
            const val MY_CAMS = "My Cams"
            const val INVES_TAP = "InvesTap"
        }
    }

    interface AjaxKeys {
        companion object {
            // values configured on backend
            const val DOB = "3"
            const val AltMail = "6"
            const val AltMob = "2"
            const val City = "5"
            const val PromotionType = "23"
            const val EmpSalType = "25"
            const val Department = "17"
            const val Designation = "18"
            const val PanNum = "4"
            const val Company = "7"
            const val NetSal = "13"
            const val LastName = "36"
            const val MiddleName = "35"
            const val FirstName = "31"
            const val Gender = "32"
            const val NativeCity = "33"
            const val ModeOfPay = "34"

            // for future use
            /*const val FullName = "frequently_used_Apps"
            const val Gender = "frequently_used_Apps"
            const val College = "frequently_used_Apps"
            const val CurrentCity = "frequently_used_Apps"
            const val Experience = "frequently_used_Apps"
            const val Marital = "frequently_used_Apps"
            const val YOG = "frequently_used_Apps"
            const val ResidenceType = "frequently_used_Apps"
            const val CardType = "frequently_used_Apps"*/
        }
    }

    interface ContactUsPackages {
        companion object {
            const val FB = "com.facebook.katana"
            const val Youtube = "com.google.android.youtube"
            const val Insta = "com.instagram.android"
            const val Twitter = "com.twitter.android"
            const val LinkedIN = "com.linkedin.android"
            const val Outlook = "com.microsoft.office.outlook"
        }
    }

    interface ContactUsPages {
        companion object {

        }
    }

    interface LoanTypes {
        companion object {
            const val BankTransfer = "1"
            const val APayTransfer = "2"
            const val BankApay = "3"
        }
    }

    interface LoanEMITypes {
        companion object {
            const val Days = "days"
            const val EMI = "emi"
        }
    }
    interface  ChatBot {
        companion object {
            const val SEND_ID = "SEND_ID"
            const val RECEIVE_ID = "RECEIVE_ID"
            const val QUESTION_TITLE = "QuestionTitle"
            const val QUESTION = "Question"
        }
    }
}