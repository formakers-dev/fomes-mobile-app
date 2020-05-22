package com.formakers.fomes.provisioning.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.formakers.fomes.FomesApplication;
import com.formakers.fomes.R;
import com.formakers.fomes.common.util.Log;
import com.formakers.fomes.common.view.BaseActivity;
import com.formakers.fomes.common.view.custom.decorator.ContentDividerItemDecoration;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

public class LoginActivity extends BaseActivity implements LoginContract.View {

    public static final String TAG = "LoginActivity";

    private static final int REQUEST_CODE_SIGN_IN = 9001;

    @BindView(R.id.fomes_logo_layout) ViewGroup logoLayout;
    @BindView(R.id.login_description_view_pager) ViewPager2 loginViewPager;
    @BindView(R.id.login_tnc) TextView loginTncTextView;
    @BindView(R.id.login_google_button) Button loginButton;

    @Inject LoginContract.Presenter presenter;

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        if (this.presenter == null) {
            this.presenter = presenter;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerLoginDagger_Component.builder()
                .applicationComponent(FomesApplication.get(this).getComponent())
                .module(new LoginDagger.Module(this))
                .build()
                .inject(this);

        this.setContentView(R.layout.activity_login);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        bindLoginView();
        presenter.init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "requestCode=" + requestCode + " resultCode=" + resultCode + " data=" + data);

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            if (resultCode != Activity.RESULT_OK) {
                showToast("구글 로그인이 취소되었습니다.");
                return;
            }

            GoogleSignInResult googleSignInResult = this.presenter.convertGoogleSignInResult(data);
            if (googleSignInResult == null) {
                showToast("구글 로그인에 실패하였습니다.");
                return;
            }

            this.presenter.signUpOrSignIn(googleSignInResult);
        }
    }

    @Override
    protected void onDestroy() {
        presenter.unsubscribe();
        super.onDestroy();
    }

    private void bindLoginView() {
        loginTncTextView.setText(Html.fromHtml(getString(R.string.login_tnc)));
        loginTncTextView.setMovementMethod(LinkMovementMethod.getInstance());

        List<Description> descriptions = new ArrayList<>();
        descriptions.add(new Description("사전예약 없는!\n신작 게임 플레이", "바로 플레이 하세요.", R.drawable.fomes_description_1));
        descriptions.add(new Description("탈락 없는!\n베타테스트 참여", "자유롭게 참여 가능해요.", R.drawable.fomes_description_2));
        descriptions.add(new Description("짭짤해!\n참여 리워드", "게임 피드백주고 보상을 받으세요.", R.drawable.fomes_description_3));

        this.loginViewPager.setAdapter(new LoginViewPagerAdapter(descriptions));

        ContentDividerItemDecoration dividerItemDecoration = new ContentDividerItemDecoration(this, ContentDividerItemDecoration.HORIZONTAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider,
                new ContextThemeWrapper(this, R.style.LoginTheme_CardDivider).getTheme()));
        this.loginViewPager.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void showToast(String toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void startActivityAndFinish(Class<?> destActivity) {
        Intent intent = new Intent(this, destActivity);
        this.startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }

    public void showLoginView() {
        Animation fadeInAnimation = getFadeInAnimation(1000);

        loginViewPager.startAnimation(fadeInAnimation);
        loginViewPager.setVisibility(View.VISIBLE);

        loginButton.startAnimation(fadeInAnimation);
        loginButton.setVisibility(View.VISIBLE);

        loginTncTextView.startAnimation(fadeInAnimation);
        loginTncTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideFomesLogoAndShowLoginView() {
        logoLayout.setVisibility(View.GONE);
        showLoginView();
    }

    @Override
    public void addToCompositeSubscription(Subscription subscription) {
        super.addToCompositeSubscription(subscription);
    }

    @OnClick(R.id.login_google_button)
    public void onLoginButtonClick(View view) {
        Intent signInIntent = this.presenter.getGoogleSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
    }

    /*** 애니메이션 관련 로직 => 공통화 고려 필요 ***/

    private Animation getFadeOutAnimation(long durationMills) {
        Animation out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(durationMills);
        return out;
    }

    private Animation getFadeInAnimation(long durationMills) {
        Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(durationMills);
        return in;
    }

    /*** 로그인 설명 뷰페이저 관련 뷰 로직 ***/

    class Description {
        String title;
        String subTitle;
        @DrawableRes int imageResId;

        public Description(String title, String subTitle, int imageResId) {
            this.title = title;
            this.subTitle = subTitle;
            this.imageResId = imageResId;
        }
    }

    class LoginViewPagerAdapter extends RecyclerView.Adapter<LoginViewPagerAdapter.ViewHolder> {
        private List<Description> descriptions;

        public LoginViewPagerAdapter(List<Description> descriptions) {
            this.descriptions = descriptions;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_login_description, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Description description = descriptions.get(position);
            holder.titleTextView.setText(description.title);
            holder.subTitleTextView.setText(description.subTitle);
            holder.imageView.setImageResource(description.imageResId);
        }

        @Override
        public int getItemCount() {
            return descriptions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView;
            TextView subTitleTextView;
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                this.titleTextView = itemView.findViewById(R.id.login_description_title);
                this.subTitleTextView = itemView.findViewById(R.id.login_description_subtitle);
                this.imageView = itemView.findViewById(R.id.login_description_image);
            }
        }
    }
}
