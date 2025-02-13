package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.operations;

import static com.globalpayments.android.sdk.utils.ViewUtils.handleViewsVisibility;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.globalpayments.android.sdk.model.PaymentCardModel;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseDialogFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomSpinner;
import com.globalpayments.android.sdk.sample.gpapi.paymentmethod.operations.model.PaymentMethodOperationModel;
import com.globalpayments.android.sdk.sample.gpapi.paymentmethod.operations.model.PaymentMethodOperationType;

public class PaymentMethodOperationDialog extends BaseDialogFragment {

    private CustomSpinner paymentMethodOperationSpinner;

    // Payment Method Id views
    private TextView tvLabelPaymentMethodId;
    private EditText etPaymentMethodId;

    //Payment Card
    private CustomSpinner cardModelsSpinner;
    private TextView tvLabelCardModels;
    private TextView tvLabelCardNumber;
    private TextView tvLabelExpiryMonth;
    private TextView tvLabelExpiryYear;
    private TextView tvLabelCvnCvv;
    private EditText etCardNumber;
    private EditText etExpiryMonth;
    private EditText etExpiryYear;
    private EditText etCvnCvv;

    public static PaymentMethodOperationDialog newInstance(Fragment targetFragment) {
        PaymentMethodOperationDialog paymentMethodOperationDialog = new PaymentMethodOperationDialog();
        paymentMethodOperationDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        paymentMethodOperationDialog.setTargetFragment(targetFragment, 0);
        return paymentMethodOperationDialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_fragment_payment_method_operation;
    }

    @Override
    protected void initViews() {
        paymentMethodOperationSpinner = findViewById(R.id.paymentMethodOperationSpinner);
        tvLabelPaymentMethodId = findViewById(R.id.tvLabelPaymentMethodId);
        etPaymentMethodId = findViewById(R.id.etPaymentMethodId);
        cardModelsSpinner = findViewById(R.id.cardModelsSpinner);
        tvLabelCardModels = findViewById(R.id.tvLabelCardModels);
        tvLabelCardNumber = findViewById(R.id.tvLabelCardNumber);
        tvLabelExpiryMonth = findViewById(R.id.tvLabelExpiryMonth);
        tvLabelExpiryYear = findViewById(R.id.tvLabelExpiryYear);
        tvLabelCvnCvv = findViewById(R.id.tvLabelCvnCvv);
        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiryMonth = findViewById(R.id.etExpiryMonth);
        etExpiryYear = findViewById(R.id.etExpiryYear);
        etCvnCvv = findViewById(R.id.etCvnCvv);

        initSpinners();

        Button btSubmit = findViewById(R.id.btSubmit);
        btSubmit.setOnClickListener(v -> submitPaymentMethodOperationModel());
    }

    private void initSpinners() {
        paymentMethodOperationSpinner.init(PaymentMethodOperationType.values(), false, this::handleOperationViewsVisibility);
        cardModelsSpinner.init(PaymentCardModel.values(), false, this::fillPaymentCardFields);
    }

    private void handleOperationViewsVisibility(PaymentMethodOperationType paymentMethodOperationType) {
        switch (paymentMethodOperationType) {
            case TOKENIZE:
                handlePaymentMethodIdViewsVisibility(false);
                handlePaymentCardViewsVisibility(true);
                break;

            case EDIT:
                handlePaymentMethodIdViewsVisibility(true);
                handlePaymentCardViewsVisibility(true);
                break;

            case DELETE:
                handlePaymentMethodIdViewsVisibility(true);
                handlePaymentCardViewsVisibility(false);
                break;
        }
    }

    private void handlePaymentMethodIdViewsVisibility(boolean show) {
        handleViewsVisibility(show,
                tvLabelPaymentMethodId,
                etPaymentMethodId
        );
    }

    private void handlePaymentCardViewsVisibility(boolean show) {
        handleViewsVisibility(show,
                cardModelsSpinner,
                tvLabelCardModels,
                tvLabelCardNumber,
                tvLabelExpiryMonth,
                tvLabelExpiryYear,
                tvLabelCvnCvv,
                etCardNumber,
                etExpiryMonth,
                etExpiryYear,
                etCvnCvv
        );
    }

    private void fillPaymentCardFields(PaymentCardModel paymentCardModel) {
        etCardNumber.setText(paymentCardModel.getCardNumber());
        etExpiryMonth.setText(paymentCardModel.getExpiryMonth());
        etExpiryYear.setText(paymentCardModel.getExpiryYear());
        etCvnCvv.setText(paymentCardModel.getCvnCvv());
    }

    private PaymentMethodOperationModel buildPaymentMethodOperationModel() {
        PaymentMethodOperationModel paymentMethodOperationModel = new PaymentMethodOperationModel();

        paymentMethodOperationModel.setCardNumber(etCardNumber.getText().toString());
        paymentMethodOperationModel.setExpiryMonth(Integer.parseInt(etExpiryMonth.getText().toString()));
        paymentMethodOperationModel.setExpiryYear(Integer.parseInt(etExpiryYear.getText().toString()));
        paymentMethodOperationModel.setCvnCvv(etCvnCvv.getText().toString());

        paymentMethodOperationModel.setPaymentMethodId(etPaymentMethodId.getText().toString());
        paymentMethodOperationModel.setPaymentMethodOperationType(paymentMethodOperationSpinner.getSelectedOption());

        return paymentMethodOperationModel;
    }

    private void submitPaymentMethodOperationModel() {
        PaymentMethodOperationModel paymentMethodOperationModel = buildPaymentMethodOperationModel();

        Fragment targetFragment = getTargetFragment();
        if (targetFragment instanceof Callback) {
            Callback callback = (Callback) targetFragment;
            callback.onSubmitPaymentMethodOperationModel(paymentMethodOperationModel);
        }
        dismiss();
    }

    public interface Callback {
        void onSubmitPaymentMethodOperationModel(PaymentMethodOperationModel paymentMethodOperationModel);
    }

}
