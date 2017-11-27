package com.appbee.appbeemobile.custom;


import android.app.AlertDialog;
import android.content.DialogInterface;

import com.appbee.appbeemobile.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AppBeeAlertDialogTest {
    private AppBeeAlertDialog subject;
    private DialogInterface.OnClickListener mockPositiveOnClickListener = mock(DialogInterface.OnClickListener.class);
    private DialogInterface.OnClickListener mockNegativeOnClickListener = mock(DialogInterface.OnClickListener.class);


    private void setUpWithPositiveButtonOnly() throws Exception {
        subject = new AppBeeAlertDialog(RuntimeEnvironment.application, "팝업타이틀", "팝업메시지", mockPositiveOnClickListener);
    }

    private void setUpWithPositiveAndNegativeButtons() throws Exception {
        subject = new AppBeeAlertDialog(RuntimeEnvironment.application, "팝업타이틀", "팝업메시지", mockPositiveOnClickListener, mockNegativeOnClickListener);
    }

    @Test
    public void 확인버튼만있는다얼로그생성시_showTest() throws Exception {
        setUpWithPositiveButtonOnly();

        subject.show();

        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
        assertThat(dialog).isNotNull();

        ShadowAlertDialog shadowAlertDialog = shadowOf(dialog);
        assertThat(shadowAlertDialog.getTitle()).isEqualTo("팝업타이틀");
        assertThat(shadowAlertDialog.getMessage()).isEqualTo("팝업메시지");

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
        assertThat(shadowAlertDialog.getTitle()).isEqualTo("팝업타이틀");
        assertThat(shadowAlertDialog.getMessage()).isEqualTo("팝업메시지");

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
        verify(mockPositiveOnClickListener).onClick(any(DialogInterface.class), anyInt());

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).performClick();
        verify(mockNegativeOnClickListener).onClick(any(DialogInterface.class), anyInt());
    }
}