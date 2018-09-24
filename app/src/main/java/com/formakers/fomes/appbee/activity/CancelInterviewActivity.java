package com.formakers.fomes.appbee.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.formakers.fomes.AppBeeApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.view.BaseActivity;
import com.formakers.fomes.appbee.custom.AppBeeAlertDialog;
import com.formakers.fomes.common.network.ProjectService;
import com.formakers.fomes.util.FormatUtil;

import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.HttpException;
import rx.android.schedulers.AndroidSchedulers;

import static com.formakers.fomes.util.FomesConstants.EXTRA;

@Deprecated
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
        ((AppBeeApplication) getApplication()).getComponent().inject(this);

        setContentView(R.layout.activity_cancel_interview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setHomeAsUpIndicator(R.drawable.back_button);
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.cancel_no)
    void onClickNo() {
        super.onBackPressed();
    }

    @OnClick(R.id.cancel_yes)
    void onClickYes() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        addToCompositeSubscription(
                projectService.postCancelParticipate(projectId, seq, timeSlot)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            showSuccessAlertDialog();
                        }, err -> {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            if (err instanceof HttpException && ((HttpException) err).code() == 412) {
                                showErrorAlertDialog(R.string.dialog_interview_cancel_fail_message);
                            } else {
                                showErrorAlertDialog(R.string.participate_cancel_http_fail_message);
                            }
                        })
        );
    }

    private void showSuccessAlertDialog() {
        DialogInterface.OnClickListener onClickListener = (dialog, which) -> moveToMyInterviewActivity(dialog);
        final AppBeeAlertDialog dialog = new AppBeeAlertDialog(this, R.drawable.dialog_cancel_image, getString(R.string.dialog_cancel_title), getString(R.string.dialog_cancel_message), onClickListener);
        dialog.setOnCancelListener(this::moveToMyInterviewActivity);
        dialog.show();
    }

    private void showErrorAlertDialog(@StringRes int messageResId) {
        DialogInterface.OnClickListener onClickListener = (dialog, which) -> moveToMyInterviewActivity(dialog);
        AppBeeAlertDialog dialog = new AppBeeAlertDialog(this, getString(R.string.dialog_interview_cancel_fail_title), getString(messageResId), onClickListener);
        dialog.setOnCancelListener(this::moveToMyInterviewActivity);
        dialog.show();
    }

    private void moveToMyInterviewActivity(DialogInterface dialog) {
        dialog.dismiss();
        setResult(Activity.RESULT_OK);
        finish();
    }
}