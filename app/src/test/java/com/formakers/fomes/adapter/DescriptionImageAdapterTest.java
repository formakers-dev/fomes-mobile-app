package com.formakers.fomes.adapter;

import android.view.LayoutInflater;
import android.widget.ImageView;

import com.formakers.fomes.BuildConfig;
import com.formakers.fomes.R;
import com.formakers.fomes.TestAppBeeApplication;
import com.formakers.fomes.helper.ImageLoader;
import com.formakers.fomes.model.Project;
import com.bumptech.glide.request.RequestOptions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class DescriptionImageAdapterTest {

    @Inject
    ImageLoader mockImageLoader;

    private DescriptionImageAdapter subject;

    @Before
    public void setUp() throws Exception {
        ((TestAppBeeApplication) RuntimeEnvironment.application).getComponent().inject(this);

        List<Project.ImageObject> mockImageObjectList = new ArrayList<>();
        mockImageObjectList.add(new Project.ImageObject("www.naver.com", "네이버이미지"));
        mockImageObjectList.add(new Project.ImageObject("www.google.com", "구글이미지"));
        mockImageObjectList.add(new Project.ImageObject("www.appbee.info", "앱비이미지"));

        doNothing().when(mockImageLoader).loadImage(any(ImageView.class), anyString(), any(RequestOptions.class));

        subject = new DescriptionImageAdapter(mockImageObjectList, mockImageLoader);
    }

    @Test
    public void onBindViewHolder시_ImageView에_입력받은_이미지를_세팅한다() throws Exception {
        DescriptionImageAdapter.DescriptionImageHolder holder = subject.new DescriptionImageHolder(LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.item_description_image, null, false));
        subject.onBindViewHolder(holder, 0);

        assertThat(holder.imageView.getTag(R.string.tag_key_image_url)).isEqualTo("www.naver.com");
    }

    @Test
    public void getItemCountTest() throws Exception {
        assertThat(subject.getItemCount()).isEqualTo(3);
    }
}