package com.capitalnowapp.mobile.kotlin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.activities.BaseActivity
import com.capitalnowapp.mobile.beans.MasterData
import com.capitalnowapp.mobile.interfaces.SelectedIdCallback

class ListFilterAdapter(private val activity: BaseActivity, masterDataList: List<MasterData>, selectedIdCallback: SelectedIdCallback) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var masterDataList: List<MasterData>
    private val selectedIdCallback: SelectedIdCallback
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_filter_data, parent, false)
        return FilterVH(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FilterVH) {
            holder.tvCode.text = masterDataList[position].toString()
            holder.llCode.setOnClickListener {
                if(masterDataList[position].id!=null) {
                    selectedIdCallback.onIdSelected(masterDataList[position].id)
                }else{
                    selectedIdCallback.onIdSelected(masterDataList[position].name)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return masterDataList.size
    }

    fun updateList(filterList: ArrayList<MasterData>) {
        masterDataList = filterList
        notifyDataSetChanged()
    }

    private inner class FilterVH internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        var llCode: LinearLayout = view.findViewById(R.id.llCode)
        var tvCode: TextView = view.findViewById(R.id.tvCode)

    }
    init {
        this.masterDataList = masterDataList
        this.selectedIdCallback = selectedIdCallback
    }
}