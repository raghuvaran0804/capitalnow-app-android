package com.capitalnowapp.mobile.interfaces;

import com.capitalnowapp.mobile.models.loan.AmtPayable;
import com.capitalnowapp.mobile.models.loan.LoansToPay;

public interface SelectedToPayCallback {
    void selectedObj(AmtPayable amtPayable, LoansToPay loansToPay);
}
