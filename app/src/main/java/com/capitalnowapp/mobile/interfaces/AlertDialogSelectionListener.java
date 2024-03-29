package com.capitalnowapp.mobile.interfaces;

import com.capitalnowapp.mobile.constants.Constants;

public interface AlertDialogSelectionListener {
    void alertDialogCallback();

    void alertDialogCallback(Constants.ButtonType buttonType, int requestCode);
}
