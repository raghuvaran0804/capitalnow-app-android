package com.capitalnowapp.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class MasterJsonResponse implements Serializable {

    @SerializedName("employment_types")
    @Expose
    private List<MasterJsonKeyValue> employmentTypes = null;
    @SerializedName("purpose_of_loan")
    @Expose
    private List<MasterJsonIdValue> purposeOfLoan = null;
    @SerializedName("industries")
    @Expose
    private List<String> industries = null;
    @SerializedName("company_names")
    @Expose
    private List<String> companyNames = null;

    public List<MasterJsonKeyValue> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<MasterJsonKeyValue> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }

    public List<MasterJsonIdValue> getPurposeOfLoan() {
        return purposeOfLoan;
    }

    public void setPurposeOfLoan(List<MasterJsonIdValue> purposeOfLoan) {
        this.purposeOfLoan = purposeOfLoan;
    }

    public List<String> getIndustries() {
        return industries;
    }

    public void setIndustries(List<String> industries) {
        this.industries = industries;
    }

    public List<String> getCompanyNames() {
        return companyNames;
    }

    public void setCompanyNames(List<String> companyNames) {
        this.companyNames = companyNames;
    }
}
