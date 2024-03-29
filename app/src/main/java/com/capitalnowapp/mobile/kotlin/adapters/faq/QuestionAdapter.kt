package com.capitalnowapp.mobile.kotlin.adapters.faq

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.databinding.ItemFaqQuestionBinding

class QuestionAdapter(private var questions: List<String>, private var answers: List<String>, private val selectedQuestion: Any?) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    private var binding: ItemFaqQuestionBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemFaqQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestionVH(binding)
    }

    override fun getItemCount(): Int {
        return questions.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is QuestionVH) {
            holder.tvQuestion?.text = questions[position]
            holder.ivArrow?.setImageDrawable(holder.itemView.context.getDrawable(R.drawable.ic_keyboard_arrow_right_black_24dp))
            if (position==questions.size-1){
                holder.viewDivider?.visibility= GONE
            }
            holder.llQuestion?.setOnClickListener {
                if (holder.tvAnswer?.visibility == VISIBLE) {
                    holder.tvAnswer.visibility = GONE
                    holder.ivArrow?.setImageDrawable(holder.itemView.context.getDrawable(R.drawable.ic_keyboard_arrow_right_black_24dp))
                } else {
                    holder.tvAnswer?.visibility = VISIBLE
                    holder.tvAnswer?.text = answers[position]
                    holder.ivArrow?.setImageDrawable(holder.itemView.context.getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp))
                }
            }
        }
    }

    fun updateData(questionsUpdated: ArrayList<String>, answersUpdated: ArrayList<String>) {
        questions = questionsUpdated
        answers = answersUpdated
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}

class QuestionVH(binding: ItemFaqQuestionBinding?) : RecyclerView.ViewHolder(binding!!.root) {
    val tvQuestion = binding?.tvQuestion
    val tvAnswer = binding?.tvAnswer
    val ivArrow = binding?.ivArrow
    val llQuestion = binding?.llQuestion
    val viewDivider=binding?.viewDivider
}
