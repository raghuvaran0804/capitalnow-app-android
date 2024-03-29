package com.capitalnowapp.mobile.models.faq

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class QuestionAnswers : Serializable{

    @SerializedName("q")
    @Expose
    private val q: String? = null

    @SerializedName("a")
    @Expose
    private val a: String? = null

}
