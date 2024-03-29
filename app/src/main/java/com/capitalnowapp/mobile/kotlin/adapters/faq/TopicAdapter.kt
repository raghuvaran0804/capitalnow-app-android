package com.capitalnowapp.mobile.kotlin.adapters.faq

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.databinding.ItemFaqTopicBinding
import com.capitalnowapp.mobile.kotlin.fragments.FAQFragment

class TopicAdapter(private val topics: List<String>, private val faqFragment: FAQFragment, private var selectedTopic: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    private var binding: ItemFaqTopicBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemFaqTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopicVH(binding)
    }

    override fun getItemCount(): Int {
        return topics.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TopicVH) {
            holder.tvTopic?.text = topics[position]
            holder.tvTopic?.setOnClickListener {
                faqFragment.selectedTopic(position)
            }

            if (position == selectedTopic) {
                holder.tvTopic?.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                holder.tvTopic?.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.green)
            } else {
                holder.tvTopic?.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.intro_page_body_color))
                holder.tvTopic?.backgroundTintList = null
            }
        }
    }

    fun setSelectedTopic(topic: Int) {
        selectedTopic = topic
    }
}

class TopicVH(binding: ItemFaqTopicBinding?) : RecyclerView.ViewHolder(binding!!.root) {
    val tvTopic = binding?.tvTopic
}
