package com.capitalnowapp.mobile.kotlin.activities

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.SearchView
import com.capitalnowapp.mobile.beans.MasterData
import com.capitalnowapp.mobile.databinding.ActivityCampanyListBinding
import java.util.Timer
import java.util.TimerTask


class CompanyListActivity : RegistrationHomeActivity() {
    private var companies: ArrayList<MasterData>? = null
    private var binding: ActivityCampanyListBinding? = null
    private var firstTimeLoad = true
    var fromSelection: Boolean = false
    private var selectedCompany: MasterData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCampanyListBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.companySearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query == "Others") {
                    selectedCompany?.name = binding?.companySearchView?.query.toString().trim()
                    selectedCompany?.id = "-1"
                    finishThis()
                    return true
                } else if (companies != null && companies!!.size > 0) {
                    for (c in companies!!) {
                        if (c.name.equals(query)) {
                            selectedCompany = c
                            finishThis()
                            return true
                        }
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                var timer = Timer()
                val DELAY: Long = 1000 // Milliseconds
                if (newText.length >= 3 && !fromSelection) {
                    timer.cancel()
                    timer = Timer()
                    timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                getCompanies(newText)
                            }
                        },
                        DELAY
                    )
                    return true
                }
                return false
            }
        })
    }


    fun updateList(companies: ArrayList<MasterData>?) {
        this.companies = companies
        if (companies!!.size <= 0) {
            val data = MasterData()
            data.name = "Add New"
            data.id = "-1"
            companies.add(data)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, companies)
        binding!!.lv1.adapter = adapter
        //Toast.makeText(this,"selectedcompany", Toast.LENGTH_LONG).show()

        binding!!.lv1.onItemClickListener = OnItemClickListener { parent, view, position, id ->

            val query = adapter.getItem(position)
            if (query?.name == "Add New") {
                selectedCompany = MasterData()
                selectedCompany?.name = binding?.companySearchView?.query.toString().trim()
                selectedCompany?.id = "-1"
                finishThis()
            } else {
                selectedCompany = query
                finishThis()
            }

        }

    }

    private fun finishThis() {
        val intent = Intent()
        intent.putExtra("selectedCompany", selectedCompany)
        setResult(RESULT_OK, intent)
        finish()
    }
}






