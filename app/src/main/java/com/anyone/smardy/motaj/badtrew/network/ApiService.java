package com.anyone.smardy.motaj.badtrew.network;

import com.anyone.smardy.motaj.badtrew.model.Admob;
import com.anyone.smardy.motaj.badtrew.model.Cartoon;
import com.anyone.smardy.motaj.badtrew.model.CartoonWithInfo;
import com.anyone.smardy.motaj.badtrew.model.Episode;
import com.anyone.smardy.motaj.badtrew.model.EpisodeComment;
import com.anyone.smardy.motaj.badtrew.model.EpisodeDate;
import com.anyone.smardy.motaj.badtrew.model.EpisodeWithInfo;
import com.anyone.smardy.motaj.badtrew.model.Feedback;
import com.anyone.smardy.motaj.badtrew.model.Information;
import com.anyone.smardy.motaj.badtrew.model.Playlist;
import com.anyone.smardy.motaj.badtrew.model.Redirect;
import com.anyone.smardy.motaj.badtrew.model.User;
import com.anyone.smardy.motaj.badtrew.model.UserData;
import com.anyone.smardy.motaj.badtrew.model.UserResponse;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @GET("cartoon/readPaging.php")
    Single<List<Cartoon>> getCartoons(
            @Query("page") int pageNumber
    );

    @GET("cartoon_with_info/readPagingContinueAnime.php")
    Single<List<CartoonWithInfo>> readPagingContinueAnime(
            @Query("page") int pageNumber
    );
    @GET("cartoon_with_info/readPagingDUBBEDFilms.php")
    Single<List<CartoonWithInfo>> readPagingDUBBEDFilms(
            @Query("page") int pageNumber
    );
    @GET("cartoon_with_info/readPagingDUBBEDSeriesAnime.php")
    Single<List<CartoonWithInfo>> readPagingDUBBEDSeriesAnime(
            @Query("page") int pageNumber
    );
    @GET("cartoon_with_info/readPagingTranslatedFilms.php")
    Single<List<CartoonWithInfo>> readPagingTranslatedFilms(
            @Query("page") int pageNumber
    );
    @GET("cartoon_with_info/readPagingTranslatedSeriesAnime.php")
    Single<List<CartoonWithInfo>> readPagingTranslatedSeriesAnime(
            @Query("page") int pageNumber
    );

    @GET("cartoon/readPagingByType.php")
    Single<List<Cartoon>> getCartoonsByType(
            @Query("page") int pageNumber,
            @Query("type") int type
    );


    @FormUrlEncoded
    @POST("playlist/read.php")
    Single<List<Playlist>> getPlaylists(
            @Field("cartoon_id") int cartoonId
    );

    @FormUrlEncoded
    @POST("episode/readPaging.php")
    Single<List<Episode>> getEpisodes(
            @Field("playlist_id") int playlistId,
            @Query("page") int pageNumber
    );

    @GET("Accses/addMalfunctions.php")
    Single<UserResponse> makeMalfunctionsReport(
            @Query("description") String description
    );



    @GET("admob/readOneForAll.php")
    Single<Admob> getAdmob(
    );

    @GET("cartoon/search.php")
    Single<List<Cartoon>> searchCartoons(
            @Query("s") String keyword
    );

    @GET("episode/search.php")
    Single<List<Episode>> searchEpisodes(
            @Query("s") String keyword,
            @Query("playlistId") int playlistId
    );


    @GET("redirect/readOneForAll.php")
    Single<Redirect> getRedirect(
    );

    @GET("message/readOneForAll.php")
    Single<Redirect> getMessage(
    );

    @GET("episode/latest.php")
    Single<List<Episode>> latestEpisodes();

    @GET("episodeWithInfo/latest.php")
    Single<List<EpisodeWithInfo>> latestEpisodesWithInfo();

    @FormUrlEncoded
    @POST("information/readOne.php")
    Single<Information> getCartoonInformation(
            @Field("cartoon_id") int cartoonId
    );



    @FormUrlEncoded
    @POST("Accses/LoginWithEmail.php")
    Single<UserResponse> loginWithEmail(
            @Field("email") String email,
            @Field("password") String password
    );


    @FormUrlEncoded
    @POST("Accses/RegisterWithEmail.php")
    Single<UserResponse> createNewUserWithEmail(
            @Field("email") String email,
            @Field("password") String password,
            @Field("name") String name,
            @Field("photo_Uri") String photoUrl
    );


    @FormUrlEncoded
    @POST("Accses/RegisterWithToken.php")
    Single<UserResponse> createNewUserWithToken(
            @Field("token") String token,
            @Field("email") String email,
            @Field("name") String name,
            @Field("photo_Uri") String photo_Uri
    );

    @FormUrlEncoded
    @POST("Accses/RegisterWithGoogle.php")
    Single<UserResponse> createNewUserWithGoogleToken(
            @Field("token") String token,
            @Field("email") String email,
            @Field("name") String name,
            @Field("photo_Uri") String photo_Uri
    );


    @GET("cartoon_with_info/searchCartoon.php")
    Single<List<CartoonWithInfo>> searchCartoon(
            @Query("search") String Search,
            @Query("type") int type ,
            @Query("classification") int classification
    );

    @GET("UserLoggedOptions/getAllFavouriteCartoons.php")
    Single<List<CartoonWithInfo>> getAllFavouriteCartoons(
            @Query("user_id") int userId
    );


    @GET("cartoon_with_info/getMostViewedCartoons.php")
    Single<List<CartoonWithInfo>> getMostViewedCartoons(
    );


    @GET("Accses/getUserImg.php")
    Single<String> getUserImg(
            @Query("id") int userId
    );

    @GET("UserLoggedOptions/getAllWatchedCartonns.php")
    Single<List<CartoonWithInfo>> getAllWatchedCartoons(
            @Query("user_id") int userId
    );

    @GET("UserLoggedOptions/getAllWatchedLaterCartoons.php")
    Single<List<CartoonWithInfo>> getAllWatchedLaterCartoons(
            @Query("user_id") int userId
    );

    @GET("UserLoggedOptions/getAllSeenEpisodes.php")
    Single<List<Integer>> getAllSeenEpisodes(
            @Query("user_id") int userId
    );

    @GET("UserLoggedOptions/loadUserData.php")
    Single<UserData> LoadUserData(
            @Query("user_id") int userId
    );

    @GET("UserLoggedOptions/addFavourite.php")
    Single<UserResponse> addFavourite(
            @Query("user_id") int userId,
            @Query("cartoon_id") int cartoonId
    );

    @GET("UserLoggedOptions/removeFavourite.php")
    Single<UserResponse> deleteFavourite(
            @Query("user_id") int userId,
            @Query("cartoon_id") int cartoonId
    );

    @GET("UserLoggedOptions/addwatchedCartoon.php")
    Single<UserResponse> addWatchedCartoon(
            @Query("user_id") int userId,
            @Query("cartoon_id") int cartoonId
    );

    @GET("UserLoggedOptions/removeWatchedCartoon.php")
    Single<UserResponse> removeWatchedCartoon(
            @Query("user_id") int userId,
            @Query("cartoon_id") int cartoonId
    );

    @GET("UserLoggedOptions/addWatchedLaterCartoon.php")
    Single<UserResponse> addWatchedLaterCartoon(
            @Query("user_id") int userId,
            @Query("cartoon_id") int cartoonId
    );

    @GET("UserLoggedOptions/removeWatchLater.php")
    Single<UserResponse> removeWatchLater(
            @Query("user_id") int userId,
            @Query("cartoon_id") int cartoonId
    );


    @GET("UserLoggedOptions/getCartoonFeedbacksAsc.php")
    Single<List<Feedback>> getFeedbacksAsc(
            @Query("cartoon_id") int cartoonId
    );

    @GET("UserLoggedOptions/getCartoonFeedbacksDesc.php")
    Single<List<Feedback>> getFeedbacksDesc(
            @Query("cartoon_id") int cartoonId
    );

    @GET("UserLoggedOptions/getFeedbackRepliesASC.php")
    Single<List<Feedback>> getFeedbacksRepliesAsc(
            @Query("feedback_id") int feedback_id
    );

    @GET("UserLoggedOptions/getFeedbackRepliesDesc.php")
    Single<List<Feedback>> getFeedbacksRepliesDesc(
            @Query("feedback_id") int feedback_id
    );


    @GET("UserLoggedOptions/getUserLikesOnCartoonFeedback.php")
    Single<List<Integer>> getFeedbacksLikesIds(
            @Query("user_id") int userID,
            @Query("cartoon_id") int cartoonId
    );


    @GET("UserLoggedOptions/getUserDislikesOnCartoonFeedback.php")
    Single<List<Integer>> getFeedbacksDisLikesIds(
            @Query("user_id") int userID,
            @Query("cartoon_id") int cartoonId
    );

    //#--------------------------------------------------------------#//

    @GET("UserLoggedOptions/getEpisodesCommentsDesc.php")
    Single<List<EpisodeComment>> getCommentsDesc(
            @Query("episode_id") int episodeId
    );

    @GET("UserLoggedOptions/getEpisodesCommentsAsc.php")
    Single<List<EpisodeComment>> getCommentsAsc(
            @Query("episode_id") int episodeId
    );

    @GET("UserLoggedOptions/getAllCommentsRepliesDesc.php")
    Single<List<EpisodeComment>> getCommentsRepliesDesc(
            @Query("comment_id") int comment_id
    );

    @GET("UserLoggedOptions/getAllCommentsRepliesAsc.php")
    Single<List<EpisodeComment>> getCommentsRepliesAsc(
            @Query("comment_id") int comment_id
    );

    @GET("UserLoggedOptions/getUserLikesOnEpisodeComments.php")
    Single<List<Integer>> getCommentsLikesIds(
            @Query("user_id") int userID,
            @Query("episode_id") int episodeId
    );






    @GET("UserLoggedOptions/getUserDislikesOnEpisodeComments.php")
    Single<List<Integer>> getCommentsDisLikesIds(
            @Query("user_id") int userID,
            @Query("episode_id") int episodeId
    );

    @GET("UserLoggedOptions/addEpisodeComment.php")
    Single<UserResponse> addEpisodeComment(
            @Query("user_id") int userId,
            @Query("episode_id") int episode_id,
            @Query("comment") String comment,
            @Query("name") String name,
            @Query("photo_Uri") String photo_Uri,
            @Query("time") String time
    );


    @GET("UserLoggedOptions/addEpisodeCommentReply.php")
    Single<UserResponse> addEpisodeCommentReply(
            @Query("comment_id") int comment_id,
            @Query("user_id") int userId,
            @Query("episode_id") int episode_id,
            @Query("comment") String comment,
            @Query("name") String name,
            @Query("photo_Uri") String photo_Uri,
            @Query("time") String time
    );

    @GET("UserLoggedOptions/likeEpisodeComment.php")
    Single<UserResponse> likeEpisodeComment(
            @Query("user_id") int userId,
            @Query("comment_id") int comment_id
    );

    @GET("UserLoggedOptions/dislikeEpisodeComment.php")
    Single<UserResponse> dislikeEpisodeComment(
            @Query("user_id") int userId,
            @Query("comment_id") int comment_id
    );

    @GET("UserLoggedOptions/removeCommentLike.php")
    Single<UserResponse> removeCommentLike(
            @Query("user_id") int userId,
            @Query("comment_id") int comment_id
    );

    @GET("UserLoggedOptions/removeEpisodeComment.php")
    Single<UserResponse> removeEpisodeComment(
            @Query("comment_id") int comment_id
    );

    @GET("UserLoggedOptions/removeCommentDislike.php")
    Single<UserResponse> removeCommentDislike(
            @Query("user_id") int userId,
            @Query("comment_id") int comment_id
    );


    @GET("UserLoggedOptions/makeCommentReport.php")
    Single<UserResponse> makeCommentReport(
            @Query("user_id") int userId,
            @Query("comment_id") int comment_id,
            @Query("description") String description
    );


    //#--------------------------------------------------------------#//

    @GET("UserLoggedOptions/insertSeenEpisode.php")
    Single<UserResponse> insertSeenEpisode(
            @Query("user_id") int userId,
            @Query("episode_id") int episodeId
    );

    @GET("UserLoggedOptions/incrementWatchedEpisodes.php")
    Single<UserResponse> incrementWatchedEpisodes(
            @Query("user_id") int userId
    );

    @GET("UserLoggedOptions/insertDownloadEpisode.php")
    Single<UserResponse> insertDownloadEpisode(
            @Query("user_id") int userId,
            @Query("episode_id") int episodeId ,
            @Query("video_url") String video_url
    );

    @GET("UserLoggedOptions/deleteDownloadEpisode.php")
    Single<UserResponse> deleteDownloadEpisode(
            @Query("user_id") int userId,
            @Query("episode_id") int episodeId,
            @Query("video_url") String video_url);

    @GET("UserLoggedOptions/getEpisodeDownloads.php")
    Single<List<Episode>> getEpisodeDownloads(
            @Query("user_id") int userId
    );


    @GET("UserLoggedOptions/addCartoonFeedback.php")
    Single<UserResponse> addCartoonFeedback(
            @Query("user_id") int userId,
            @Query("cartoon_id") int cartoon_id,
            @Query("feedback") String feedback,
            @Query("name") String name,
            @Query("photo_Uri") String photo_Uri,
            @Query("time") String time
    );

    @GET("UserLoggedOptions/addCartoonFeedbackReply.php")
    Single<UserResponse> addCartoonFeedbackReply(
            @Query("feedback_id") int feedback_id,
            @Query("user_id") int userId,
            @Query("cartoon_id") int cartoon_id,
            @Query("feedback") String feedback,
            @Query("name") String name,
            @Query("photo_Uri") String photo_Uri,
            @Query("time") String time
    );

    @GET("UserLoggedOptions/likeFeedback.php")
    Single<UserResponse> likeFeedback(
            @Query("user_id") int userId,
            @Query("feedback_id") int feedback_id
    );

    @GET("UserLoggedOptions/dislikeFeedback.php")
    Single<UserResponse> dislikeFeedback(
            @Query("user_id") int userId,
            @Query("feedback_id") int feedback_id
    );

    @GET("UserLoggedOptions/removeLikeFeedback.php")
    Single<UserResponse> removeLike(
            @Query("user_id") int userId,
            @Query("feedback_id") int feedback_id
    );

    @GET("UserLoggedOptions/removeFeedback.php")
    Single<UserResponse> removeFeedback(
            @Query("feedback_id") int feedback_id
    );

    @GET("UserLoggedOptions/removeDislikeFeedback.php")
    Single<UserResponse> removeDisLike(
            @Query("user_id") int userId,
            @Query("feedback_id") int feedback_id
    );


    @FormUrlEncoded
    @POST("Uploaded_Images/uploadImg.php")
    Single<String> saveUserImg(
            @Field("image") String base64Img,
            @Field("user_id") int id);


    @FormUrlEncoded
    @POST("Uploaded_Images/changeUserImg.php")
    Single<String> changeUserImg(
            @Field("image") String base64Img,
            @Field("user_id") int id,
            @Field("old_img") String imgName
    );


    @FormUrlEncoded
    @POST("Accses/sendOTP.php")
    Single<UserResponse> sendOtpToEmail(
            @Field("email") String email,
            @Field("code") int code
    );

    @FormUrlEncoded
    @POST("Accses/sendPasswordToEmail.php")
    Single<UserResponse> sendPasswordToEmail(
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("Accses/checkIfEmailExits.php")
    Single<UserResponse> checkIfEmailExits(
            @Field("email") String email
    );

    @GET("UserLoggedOptions/makeReport.php")
    Single<UserResponse> makeFeedbackReport(
            @Query("user_id") int userId,
            @Query("feedback_id") int feedback_id,
            @Query("description") String description
    );


    @GET("UserLoggedOptions/getBlockStatues.php")
    Single<Integer> getUserBlockedStatue(
            @Query("user_id") int userId
    );


    @GET("Leaderboard/getLeaderboard.php")
    Single<List<User>> getLeaderboard();

    @GET("Accses/makeServerReport.php")
    Single<UserResponse> sendServerReport(
            @Query("episode_id") int episode_id,
            @Query("episode_name") String episode_name,
            @Query("playlist_name") String playlist_name,
            @Query("cartoon_name") String cartoon_name);

    @GET("episode_dates_with_info/read.php")
    Single<List<EpisodeDate>> getEpisodeDates();


    @GET("UserLoggedOptions/decrementNumOfCommentReplies.php")
    Single<UserResponse> decrementCommentReplies(
            @Query("comment_id") int comment_id);

    @GET("UserLoggedOptions/decrementNumOfFeedbackReplies.php")
    Single<UserResponse> decrementFeedbackReplies(
            @Query("feedback_id") int feedback_id);

    @GET("Accses/serverMaintance.php")
    Single<Integer> checkIfServerIsUnderMaintains();

    @GET("Accses/getVideoAppPackageName2.php")
    Single<String> getVideoAppPackageName();


    @GET("Accses/getDownloadAppPackageName2.php")
    Single<String> getDownloadAppPackageName();
}
