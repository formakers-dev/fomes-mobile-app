package com.formakers.fomes.wishList.presenter;

import com.formakers.fomes.common.network.UserService;
import com.formakers.fomes.wishList.contract.WishListContract;

import org.mockito.Mock;

public class WishListPresenterTest {

    //TODO : 테스트코드 현행화 필요
    @Mock UserService mockUserService;
    @Mock WishListContract.View mockView;

    private WishListPresenter subject;

//    @Before
//    public void setUp() throws Exception {
//        MockitoAnnotations.initMocks(this);
//
//        subject = new WishListPresenter(mockUserService);
//    }

//    @Test
//    public void emitRemoveFromWishList_호출시__특정앱을_위시리스트에서_삭제해줄것을_서버에_요청한다() {
//        subject.requestRemoveFromWishList("com.test");
//        verify(mockUserService).requestRemoveAppFromWishList("com.test");
//    }
//
//    @Test
//    public void emitRemoveFromWishList_호출시_서버로부터_성공응답을_받으면_뷰의_hideApp을_호출한다() {
//        //Given
//        when(mockUserService.requestRemoveAppFromWishList(anyString())).thenReturn(Completable.complete());
//
//        //When
//        subject.requestRemoveFromWishList("com.test");
//
//        //Then
//        verify()
//    }
}