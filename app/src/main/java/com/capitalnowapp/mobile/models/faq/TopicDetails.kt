package com.capitalnowapp.mobile.models.faq

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class TopicDetails :Serializable {

    @SerializedName("topics")
    @Expose
    private var topics: List<TopicQuestions?>? = null

}