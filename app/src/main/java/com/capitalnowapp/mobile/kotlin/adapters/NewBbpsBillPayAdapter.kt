package com.capitalnowapp.mobile.kotlin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.databinding.ItemNewBbpsListBinding
import com.capitalnowapp.mobile.fragments.HomeFragment
import com.capitalnowapp.mobile.models.BbpsBillPayResponse

class NewBbpsBillPayAdapter(
    private var context: HomeFragment,
    private var bbpsBillPayResponse: BbpsBillPayResponse
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    private var binding: ItemNewBbpsListBinding? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding =
            ItemNewBbpsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewBbpsBillPayListVH(binding!!)
    }

    class NewBbpsBillPayListVH(binding: ItemNewBbpsListBinding) :RecyclerView.ViewHolder(binding.root){

    }

    override fun getItemCount(): Int {
        return bbpsBillPayResponse.data?.size!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}