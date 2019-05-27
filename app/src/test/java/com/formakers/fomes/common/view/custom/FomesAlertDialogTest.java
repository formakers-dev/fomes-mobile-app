package com.formakers.fomes.common.view.custom;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.formakers.fomes.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowAlertDialog;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class FomesAlertDialogTest {
    private FomesAlertDialog subject;
    private DialogInterface.OnClickListener mockPositiveOnClickListener = mock(DialogInterface.OnClickListener.class);
    private DialogInterface.OnClickListener mockNegativeOnClickListener = mock(DialogInterface.OnClickListener.class);


    private void setUpWithPositiveButtonOnly() throws Exception {
        subject = new FomesAlertDialog(ApplicationProvider.getApplicationContext(), "팝업타이틀", "팝업메시지", mockPositiveOnClickListener);
    }

    private void setUpWithPositiveAndNegativeButtons() throws Exception {
        subject = new FomesAlertDialog(ApplicationProvider.getApplicationContext(), "팝업타이틀", "팝업메시지", mockPositiveOnClickListener, mockNegativeOnClickListener);
    }

    private void setUpWithImageAndPositiveButton() throws Exception {
        subject = new FomesAlertDialog(ApplicationProvider.getApplicationContext(), R.drawable.dialog_success_image, "팝업타이틀", "팝업메시지", mockPositiveOnClickListener);
    }

    @Test
    public void 확인버튼만있는다얼로그생성시_showTest() throws Exception {
        setUpWithPositiveButtonOnly();

        subject.show();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(dialog).isNotNull();

        ShadowAlertDialog shadowAlertDialog = shadowOf(dialog);
        View rootView = shadowAlertDialog.getView();

        assertThat(((TextView) rootView.findViewById(R.id.dialog_title)).getText()).isEqualTo("팝업타이틀");
        assertThat(((TextView) rootView.findViewById(R.id.dialog_message)).getText()).isEqualTo("팝업메시지");

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();

        verify(mockPositiveOnClickListener).onClick(any(DialogInterface.class), anyInt());
    }

    @Test
    public void 확인및취소버튼이있는다얼로그생성시_showTest() throws Exception {
        setUpWithPositiveAndNegativeButtons();

        subject.show();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(dialog).isNotNull();

        ShadowAlertDialog shadowAlertDialog = shadowOf(dialog);
        View rootView = shadowAlertDialog.getView();

        assertThat(((TextView) rootView.findViewById(R.id.dialog_title)).getText()).isEqualTo("팝업타이틀");
        assertThat(((TextView) rootView.findViewById(R.id.dialog_message)).getText()).isEqualTo("팝업메시지");

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
        verify(mockPositiveOnClickListener).onClick(any(DialogInterface.class), anyInt());

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).performClick();
        verify(mockNegativeOnClickListener).onClick(any(DialogInterface.class), anyInt());
    }

    @Test
    public void 확인버튼과이미지가있는다얼로그생성시_showTest() throws Exception {
        setUpWithImageAndPositiveButton();

        subject.show();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(dialog).isNotNull();

        ShadowAlertDialog shadowAlertDialog = shadowOf(dialog);
        View rootView = shadowAlertDialog.getView();

        assertThat(((TextView) rootView.findViewById(R.id.dialog_title)).getText()).isEqualTo("팝업타이틀");
        assertThat(((TextView) rootView.findViewById(R.id.dialog_message)).getText()).isEqualTo("팝업메시지");
        assertThat(shadowOf(((ImageView) rootView.findViewById(R.id.dialog_image)).getDrawable()).getCreatedFromResId()).isEqualTo(R.drawable.dialog_success_image);

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();

        verify(mockPositiveOnClickListener).onClick(any(DialogInterface.class), anyInt());
    }
}