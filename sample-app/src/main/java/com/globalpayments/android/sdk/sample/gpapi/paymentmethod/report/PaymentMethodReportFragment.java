package com.globalpayments.android.sdk.sample.gpapi.paymentmethod.report;

import static com.globalpayments.android.sdk.sample.gpapi.common.model.TYPE.BY_ID;
import static com.globalpayments.android.sdk.sample.gpapi.common.model.TYPE.LIST;
import static com.globalpayments.android.sdk.utils.ViewUtils.hideView;
import static com.globalpayments.android.sdk.utils.ViewUtils.showView;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.global.api.entities.reporting.StoredPaymentMethodSummary;
import com.globalpayments.android.sdk.sample.R;
import com.globalpayments.android.sdk.sample.common.base.BaseFragment;
import com.globalpayments.android.sdk.sample.common.views.CustomToolbar;
import com.globalpayments.android.sdk.sample.gpapi.common.model.TYPE;
import com.globalpayments.android.sdk.sample.gpapi.transaction.report.model.TransactionReportParameters;
import com.globalpayments.android.sdk.sample.gpapi.verifications.model.VerificationsModel;

import java.util.List;

public class PaymentMethodReportFragment extends BaseFragment implements PaymentMethodReportDialog.Callback {

    private ProgressBar progressBar;
    private TextView errorTextView;
    private RecyclerView recyclerViewById;
    private RecyclerView recyclerViewList;

    private PaymentMethodReportByIdAdapter paymentMethodReportByIdAdapter;
    private PaymentMethodReportListAdapter paymentMethodReportListAdapter;
    private PaymentMethodReportViewModel paymentMethodReportViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_payment_method_report;
    }

    @Override
    protected void initDependencies() {
        paymentMethodReportViewModel = new ViewModelProvider(this).get(PaymentMethodReportViewModel.class);
        paymentMethodReportByIdAdapter = new PaymentMethodReportByIdAdapter();
        paymentMethodReportListAdapter = new PaymentMethodReportListAdapter();
    }

    @Override
    protected void initViews() {
        CustomToolbar customToolbar = findViewById(R.id.toolbar);
        customToolbar.setTitle(R.string.payment_method_report);
        customToolbar.setOnBackButtonListener(v -> close());

        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);

        recyclerViewById = findViewById(R.id.recyclerViewById);
        recyclerViewById.setAdapter(paymentMethodReportByIdAdapter);

        recyclerViewList = findViewById(R.id.recyclerViewList);
        recyclerViewList.setAdapter(paymentMethodReportListAdapter);

        Button btGetPaymentMethodsList = findViewById(R.id.btGetPaymentMethodsList);
        btGetPaymentMethodsList.setOnClickListener(v -> showPaymentMethodReportDialog(LIST));

        Button btGetPaymentMethodById = findViewById(R.id.btGetPaymentMethodById);
        btGetPaymentMethodById.setOnClickListener(v -> showPaymentMethodReportDialog(BY_ID));
    }

    private void showPaymentMethodReportDialog(TYPE type) {
        PaymentMethodReportDialog paymentMethodReportDialog = PaymentMethodReportDialog.newInstance(this, type);
        paymentMethodReportDialog.show(requireFragmentManager(), PaymentMethodReportDialog.class.getSimpleName());
    }

    @Override
    protected void initSubscriptions() {
        paymentMethodReportViewModel.getProgressStatus().observe(this, show -> {
            if (show) {
                hideView(recyclerViewById);
                hideView(recyclerViewList);
                hideView(errorTextView);
                showView(progressBar);
            } else {
                hideView(progressBar);
            }
        });

        paymentMethodReportViewModel.getError().observe(this, errorMessage -> {
            hideView(recyclerViewById);
            hideView(recyclerViewList);
            showView(errorTextView);
            if (errorMessage.contains("Empty") && errorMessage.contains("List")) {
                errorTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorBlack));
                errorTextView.setText(R.string.empty_list);
            } else {
                errorTextView.setText(errorMessage);
            }
        });

        paymentMethodReportViewModel.getPaymentMethodsLiveData().observe(this, paymentMethods -> {
            hideView(errorTextView);
            hideView(recyclerViewList);
            showView(recyclerViewById);
            submitPaymentMethodsById(paymentMethods);
        });

        paymentMethodReportViewModel.getPaymentMethodsListLiveData().observe(this, storedPaymentMethodSummaryList -> {
            hideView(errorTextView);
            hideView(recyclerViewById);
            showView(recyclerViewList);
            submitPaymentMethodsList(storedPaymentMethodSummaryList);
        });
    }

    private void submitPaymentMethodsById(List<StoredPaymentMethodSummary> paymentMethods) {
        paymentMethodReportByIdAdapter.submitList(paymentMethods);
    }

    private void submitPaymentMethodsList(List<StoredPaymentMethodSummary> storedPaymentMethodSummaryList) {
        paymentMethodReportListAdapter.submitList(storedPaymentMethodSummaryList);
    }

    @Override
    public void onSubmitPaymentMethodId(String paymentMethodId) {
        paymentMethodReportViewModel.getPaymentMethodById(paymentMethodId);
    }

    @Override
    public void onSubmitPaymentsReportParameters(TransactionReportParameters transactionReportParameters) {
        paymentMethodReportViewModel.getPaymentMethodList(transactionReportParameters);
    }

}
