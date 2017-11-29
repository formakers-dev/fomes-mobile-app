package com.appbee.appbeemobile.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.appbee.appbeemobile.AppBeeApplication;
import com.appbee.appbeemobile.R;
import com.appbee.appbeemobile.custom.AppBeeAlertDialog;
import com.appbee.appbeemobile.network.ProjectService;
import com.appbee.appbeemobile.util.FormatUtil;

import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.HttpException;
import rx.android.schedulers.AndroidSchedulers;

import static com.appbee.appbeemobile.util.AppBeeConstants.EXTRA;

public class CancelInterviewActivity extends BaseActivity {
    @Inject
    ProjectService projectService;

    @BindView(R.id.interview_name_status)
    TextView interviewNameStatusTextView;

    @BindView(R.id.interview_date_location)
    TextView interviewDateLocationTextView;

    private String projectId;
    private long seq;
    private String timeSlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_interview);
        ((AppBeeApplication) getApplication()).getComponent().inject(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        projectId = getIntent().getStringExtra(EXTRA.PROJECT_ID);
        seq = getIntent().getLongExtra(EXTRA.INTERVIEW_SEQ, 0L);
        timeSlot = getIntent().getStringExtra(EXTRA.TIME_SLOT);

        interviewNameStatusTextView.setText(String.format(getString(R.string.cancel_interview_name_status), getIntent().getStringExtra(EXTRA.PROJECT_NAME), getIntent().getStringExtra(EXTRA.INTERVIEW_STATUS)));
        interviewDateLocationTextView.setText(String.format(getString(R.string.cancel_interview_date_location), FormatUtil.toShortDateFormat((Date) getIntent().getSerializableExtra(EXTRA.INTERVIEW_DATE)),
                getIntent().getStringExtra(EXTRA.LOCATION),
                Integer.parseInt(timeSlot.substring(4))));
    }

    @OnClick(R.id.back_button)
    void onBackButtonClick() {
        super.onBackPressed();
    }

    @OnClick(R.id.cancel_yes)
    void onClickCancel(View view) {
        projectService.postCancelParticipate(projectId, seq, timeSlot)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result) {
                        DialogInterface.OnClickListener onClickListener = (dialog, which) -> {
                            dialog.dismiss();
                            Intent intent = new Intent(CancelInterviewActivity.this, MyInterviewActivity.class);
                            startActivity(intent);
                            finish();
                        };
                        AppBeeAlertDialog alertDialog = new AppBeeAlertDialog(this, R.drawable.dialog_cancel_image, getString(R.string.dialog_cancel_title), getString(R.string.dialog_cancel_message), onClickListener);
                        alertDialog.show();
                    }
                }, err -> {
                    if (err instanceof HttpException) {
                        Toast.makeText(this, String.valueOf(((HttpException) err).code()), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, String.valueOf(err.getCause()), Toast.LENGTH_LONG).show();
                    }
                });
    }
}