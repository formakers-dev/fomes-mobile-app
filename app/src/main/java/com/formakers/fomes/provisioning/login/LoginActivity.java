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
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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
    @BindView(R.id.login_description_layout) ViewGroup loginDescriptionLayout;
    @BindView(R.id.login_description_view_pager) ViewPager2 loginViewPager;
    @BindView(R.id.login_description_view_pager_indicator) TabLayout loginViewPagerIndicator;
    @BindView(R.id.login_button_layout) ViewGroup loginButtonLayout;
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
                showToast("?????? ???????????? ?????????????????????.");
                return;
            }

            GoogleSignInResult googleSignInResult = this.presenter.convertGoogleSignInResult(data);
            if (googleSignInResult == null) {
                showToast("?????? ???????????? ?????????????????????.");
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
        descriptions.add(new Description("???????????? ??????!\n?????? ?????? ?????????", "?????? ????????? ?????????.", R.drawable.fomes_description_1));
        descriptions.add(new Description("?????? ??????!\n??????????????? ??????", "???????????? ?????? ????????????.", R.drawable.fomes_description_2));
        descriptions.add(new Description("?????????!\n?????? ?????????", "?????? ??????????????? ????????? ????????????.", R.drawable.fomes_description_3));

        this.loginViewPager.setAdapter(new LoginViewPagerAdapter(descriptions));

        ContentDividerItemDecoration dividerItemDecoration = new ContentDividerItemDecoration(this, ContentDividerItemDecoration.HORIZONTAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider,
                new ContextThemeWrapper(this, R.style.LoginTheme_CardDivider).getTheme()));
        this.loginViewPager.addItemDecoration(dividerItemDecoration);

        new TabLayoutMediator(loginViewPagerIndicator, loginViewPager, (tab, position) -> {
        }).attach();
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
        loginDescriptionLayout.setVisibility(View.VISIBLE);

        loginButton.startAnimation(fadeInAnimation);
        loginTncTextView.startAnimation(fadeInAnimation);
        loginButtonLayout.setVisibility(View.VISIBLE);
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

    /*** ????????? ?????? ???????????? ?????? ??? ?????? ***/

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
