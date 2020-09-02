package com.fdev.cleanarchitecture.business.domain.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DateUtil  @Inject constructor(
    private val dateFormat : SimpleDateFormat
) {

    // date format "YYYY-MM-DD HH:MM"

    /*
        This function is working to take the substring from inputed time(in string)
        wiht date format "YYYY-MM-DD HH:MM"
         example :
            input : "2020-01-19 04:04"
            output : "2020-01-19"
     */
    fun getDateOnlyFromDateString(stringDate : String) : String{
        return stringDate.substring(0,stringDate.indexOf(" "))
    }

    /*
        This function return a string format of firebase timestamp
        (input is com.google.firebase.Timestamp")
        return : "YYYY-MM-DD HH:MM"
     */
    fun convertFirebaseTimestampToStringDate(timeStamp : Timestamp) : String{
        return dateFormat.format(timeStamp.toDate())
    }

    /*
        This function is working to manipulate string of date to firebase timestamp
        input : "YYYY-MM-DD HH:MM"
        return : com.google.firebase.TimeStamp
     */
    fun convertStringDateToFirebaseTimestamp(date : String) : Timestamp {
        return Timestamp(dateFormat.parse(date))
    }


    /*
        Return current timestamp in string format
     */
    fun getCurrentTimeStampString() : String{
        return dateFormat.format(Date())
    }

}