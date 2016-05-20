package com.example.knowbooks.constants;

/**
 * Created by qq on 2016/4/29.
 */
public class UrlConstant {

    public final static String url="http://115.28.139.214:8080/knowbook";
//    public final static String url="http://192.168.1.103:8080/KnowBook";
    public final static String RegisterUrl=url+"/users/registe";
    public final static String LoginUrl=url+"/users/login";
    public final static String ReloginUrl=url+"/relogin";
    public final static String LoginAddUrl=url+"/users/loginAdd";
    public final static String UpLoadurl=url+"/users/doUpload";


    public final static String CreateShowBookUrl=url+"/showBook/createshowbook";
    public final static String FragmentshowUrl=url+"/showBook/fragmentshow";
    public final static String BookDetailShowUrl=url+"/showBook/detailshowbook";
    public final static String ShowbookCommentUrl=url+"/showBook/showbookComment";
    public final static String WriteshowbookCommentUrl=url+"/showBook/writeshowbookComment";
    public final static String DetailCommentClickOneUrl=url+"/showBook/detailCommentClickOne";
    public final static String WriteshowbookSonCommentUrl=url+"/showBook/writeshowbookSonComment";
    public final static String DetailshowbookCommentUrl=url+"/showBook/detailshowbookComment";
    public final static String DetailCommentSonCommentDelUrl=url+"/showBook/SonCommentDelete";
    //书籍收藏和取消收藏
    public final static String CollectBookUrl=url+"/booklist/addTobookList";
    public final static String NoCollectBookUrl=url+"/booklist/removeFrombookList\n";


    //书单的url
    public final static String AddTobookListUrl=url+"addTobookList";
    public final static String CreatebooklistUrl=url+"/booklist/createbooklist";
    public final static String FragmentbooklistWeekUrl=url+"/booklist/fragmentbooklistAll";
    public final static String FragmentbooklistWorthUrl=url+"/booklist/fragmentbooklistWorth";
    public final static String FragmentbooklistNewUrl=url+"/booklist/fragmentbooklistNew";
    public final static String FragmentbooklistHotUrl=url+"/booklist/fragmentbooklistHot";
    public final static String DetailbooklistUrl=url+"/booklist/detailbooklist";
    //书单收藏和取消收藏
    public final static String CollectBooklistUrl=url+"/booklist/collectBooklist";
    public final static String NoCollectBooklistUrl=url+"/booklist/noCollectBooklist";
    //得到书单列表
    public final static String GetBookListView=url+"/booklist/myCreateBooklist";

    //售卖书籍的url
    public final static String CreateBuyUrl=url+"/sellerMarket/createBuy";
    public final static String FragmentBuyUrl=url+"/sellerMarket/fragmentBuy";
    public final static String FragmentBuySomeUrl=url+"/sellerMarket/fragmentBuySome";
    public final static String DetailBuyUrl=url+"/sellerMarket/detailBuy";

    //心愿的url
    public final static String CreateWantUrl=url+"/wish/createWant";
    public final static String FragmentWantUrl=url+"/wish/fragmentWant";
    public final static String FragmentWantSomeUrl=url+"/wish/fragmentWantSome";
    public final static String DetailWantUrl=url+"/wish/detailWant";

    public final static String MyShowBookUrl=url+"/showBook/myshowBook";
    public final static String MyBookListUrl=url+"/booklist/myBooklist";
    public final static String MyBuyBookUrl=url+"/sellerMarket//myBuyBook";
    public final static String MyWantBookUrl=url+"/wish/myWish";
    public final static String MyCollectBookListUrl=url+"/booklist/collectBooklist";


    public final static String DeleteShowBook=url+"/showBook/deleteBook";
    public final static String DeleteBookList=url+"/booklist/deleteBooklist";
    public final static String DeleteBuyBook=url+"/ sellerMarket/deleteSellBook";
    public final static String DeleteWantBook=url+"/wish/deleteWish";

    public final static String UserAdd=url+"/users/getinfo";

    public final static String CommentDel=url+"/showBook/deleteComment";
    public final static String SonCommentDel=url+"/showBook/deleteSonComment";
    public final static String UpdateLocation=url+"/users/myLocation";
}
