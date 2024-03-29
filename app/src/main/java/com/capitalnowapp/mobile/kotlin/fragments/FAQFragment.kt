package com.capitalnowapp.mobile.kotlin.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.databinding.FragmentFAQBinding
import com.capitalnowapp.mobile.kotlin.adapters.faq.QuestionAdapter
import com.capitalnowapp.mobile.kotlin.adapters.faq.TopicAdapter
import java.util.Locale.getDefault


class FAQFragment : Fragment() {

    private var topicAdapter: TopicAdapter? = null
    private var questionAdapter: QuestionAdapter? = null
    private var binding: FragmentFAQBinding? = null
    private var rvTopics: RecyclerView? = null
    private var rvQuestions: RecyclerView? = null
    private var selectedTopic: Int = 0
    private var selectedQuestion: Int = 0

    @SuppressLint("NotConstructor")
    fun FAQFragment() {
        // Required empty public constructor
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentFAQBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvTopics = binding?.rvTopics
        rvQuestions = binding?.rvQuestions

        rvTopics?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        rvQuestions?.layoutManager = LinearLayoutManager(activity)

        binding?.etSearch?.onRightDrawableClicked {
            if (binding?.etSearch?.text.toString().trim().isEmpty()) {
                binding?.etSearch?.visibility = GONE
                loadData()
            } else {
                binding?.etSearch?.setText("")
                loadData()
            }
        }
        binding?.ivSearch?.setOnClickListener {
            binding?.etSearch?.visibility = VISIBLE
            binding?.rvTopics?.visibility = GONE
        }

        binding?.etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length > 2) {
                    filterQuestions(s)
                } else if (s.isEmpty()) {
                    loadData()
                }
            }
        })
        loadData()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
        this.setOnTouchListener { v, event ->
            var hasConsumed = false
            if (v is EditText) {
                if (event.x >= v.width - v.totalPaddingRight) {
                    if (event.action == MotionEvent.ACTION_UP) {
                        onClicked(this)
                    }
                    hasConsumed = true
                }
            }
            hasConsumed
        }
    }

    private fun loadData() {
        val topics = resources.getStringArray(R.array.faq_topics)
        topicAdapter = TopicAdapter(topics.toList(), this, selectedTopic)
        rvTopics?.adapter = topicAdapter
        binding?.rvTopics?.visibility = VISIBLE
        binding?.rvQuestions?.visibility = VISIBLE
        binding?.llNotFound?.visibility = GONE
        loadQuestions()
    }

    fun selectedTopic(topic: Int) {
        selectedTopic = topic
        topicAdapter?.setSelectedTopic(topic)
        topicAdapter?.notifyDataSetChanged()
        rvTopics?.smoothScrollToPosition(topic)

        loadQuestions()
    }

    private fun loadQuestions() {
        var questions: List<String> = ArrayList()
        var answers: List<String> = ArrayList()
        when (selectedTopic) {
            0 -> {
                questions = resources.getStringArray(R.array.faq_about_cn).toList()
                answers = resources.getStringArray(R.array.faq_about_cn_answers).toList()
            }
            1 -> {
                questions = resources.getStringArray(R.array.faq_application_process).toList()
                answers = resources.getStringArray(R.array.faq_application_process_answers).toList()
            }
            2 -> {
                questions = resources.getStringArray(R.array.faq_fees).toList()
                answers = resources.getStringArray(R.array.faq_fees_answers).toList()
            }
            3 -> {
                questions = resources.getStringArray(R.array.faq_security).toList()
                answers = resources.getStringArray(R.array.faq_security_answers).toList()
            }
        }
        questionAdapter = QuestionAdapter(questions, answers, selectedQuestion)
        rvQuestions?.adapter = questionAdapter
    }

    private fun filterQuestions(s: Editable) {

        val questionsUpdated: ArrayList<String> = ArrayList()
        val answersUpdated: ArrayList<String> = ArrayList()

        val questions: List<String> = resources.getStringArray(R.array.faq_about_cn).toList() + resources.getStringArray(R.array.faq_application_process).toList() + resources.getStringArray(R.array.faq_fees).toList() + resources.getStringArray(R.array.faq_security).toList()
        val answers: List<String> = resources.getStringArray(R.array.faq_about_cn_answers).toList() + resources.getStringArray(R.array.faq_application_process_answers).toList() + resources.getStringArray(R.array.faq_fees_answers).toList() + resources.getStringArray(R.array.faq_security_answers).toList()

        for ((index, qu) in questions.withIndex()) {
            if (qu.toLowerCase(getDefault()).contains(s.toString().toLowerCase(getDefault()))) {
                questionsUpdated.add(qu)
                answersUpdated.add(answers[index])
            }
        }
        if (questionsUpdated.size > 0) {
            binding?.rvQuestions?.visibility = VISIBLE
            binding?.llNotFound?.visibility = GONE
            binding?.rvTopics?.visibility = VISIBLE
            questionAdapter?.updateData(questionsUpdated, answersUpdated)
            questionAdapter?.notifyDataSetChanged()
        } else {
            binding?.rvQuestions?.visibility = GONE
            binding?.llNotFound?.visibility = VISIBLE
            binding?.rvTopics?.visibility = GONE
        }
    }
}
