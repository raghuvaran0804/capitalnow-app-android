package com.capitalnowapp.mobile.models.faq

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class TopicQuestions : Serializable{

    @SerializedName("title")
    @Expose
    private val title: String? = null

    @SerializedName("qns")
    @Expose
    private val qns: List<QuestionAnswers>? = null

}
