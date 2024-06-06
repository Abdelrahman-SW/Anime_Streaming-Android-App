package com.anyone.smardy.motaj.badtrew.Constants;

public interface Constants {

    String SKIP_INSTALL_APPS = "skip";
    String POLICY_LINK = "https://apps-anime.com/apps/policy.html";
    String TERMS_OF_USE_LINK = "https://apps-anime.com/apps/terms.html";

    String privacyTxt = "من خلال المتابعه فانك توافق على شروط الاستخدام و سياسة الخصوصية الخاصه بنا";

    int USER_CREATED = 200;
    int USER_NOT_CREATED = 201;
    int USER_ALREADY_EXISTS = 202;
    int USER_NOT_FOUND = 203;
    int INCORRECT_PASSWORD = 205;
    String CARTOON_ID = "cartoon_id";
    String EPISODE_ID = "episode_id";
    String FEEDBACK_ID = "feedback_id";
    String COMMENT_ID = "comment_id";
    int IS_SERIES = 1;
    int IS_FILM = 2;
    int IS_DUBBED = 1;  // مدبلج
    int IS_TRANSLATED = 2;
    int DUBBED_ANIME = 100;
    int TRANSLATED_ANIME = 101;
    int TRANSLATED_FILMS = 102;
    int DUBBED_FILMS = 103;
    int NEW_ANIME = 104;
    int LATEST_EPISODES = 0;
    int FAVOURITE = 10;
    int WATCHED = 11;
    int WATCH_LATER = 12;
    int MOST_VIEWED = 13;
}
