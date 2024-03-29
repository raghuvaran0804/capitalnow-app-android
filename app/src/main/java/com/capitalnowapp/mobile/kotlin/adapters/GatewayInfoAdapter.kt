package com.capitalnowapp.mobile.kotlin.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.capitalnowapp.mobile.R
import com.capitalnowapp.mobile.models.loan.UpiArray

class GatewayInfoAdapter(private val valuesArrays: List<UpiArray>, private val type: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = if (type == 0) {
            LayoutInflater.from(parent.context).inflate(R.layout.item_gateway_upi, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.item_gateway_bank, parent, false)
        }
        return ValueVH(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ValueVH) {
            val myClipboard = holder.itemView.context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            holder.tvTitle?.text = valuesArrays[position].title
            holder.tvValue?.text = valuesArrays[position].value
            if (!valuesArrays[position].canCopy!!) {
                holder.tvValue?.setCompoundDrawables(null, null, null, null)
                holder.tvValue?.isEnabled = false
            }
            holder.tvValue?.setOnTouchListener(View.OnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= holder.tvValue.right - holder.tvValue.totalPaddingRight) {
                        val myClip = ClipData.newPlainText("text", holder.tvValue.text.toString())
                        myClipboard?.setPrimaryClip(myClip)
                        Toast.makeText(holder.itemView.context, valuesArrays[position].title + " Copied", Toast.LENGTH_SHORT).show()
                    }
                    return@OnTouchListener true
                }
                true
            })

            if (type == 0) {
                holder.tvTitle?.setTypeface(holder.tvTitle.typeface, Typeface.BOLD)
            } else {
                holder.tvTitle?.text = valuesArrays[position].title + ": "
                holder.tvTitle?.setTypeface(holder.tvTitle.typeface, Typeface.NORMAL)
            }
        }
    }

    override fun getItemCount(): Int {
        return valuesArrays.size
    }

    private inner class ValueVH(view: View?) : RecyclerView.ViewHolder(view!!) {
        val tvTitle = view?.findViewById<TextView>(R.id.tvTitle)
        val tvValue = view?.findViewById<TextView>(R.id.tvValue)
    }
}