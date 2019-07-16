package me.qidongs.rootwebsite.util;

public interface CommunityConstant {
    //activation success
    int ACTIVATION_SUCCESS = 0;

    //repetitive activation
    int ACTIVATION_REPEAT =1;

    //failure
    int ACTIVATION_FAILURE=2;

    //default expire second
    int DEFAULT_EXPIRED_SECONDS =3600*12;

    //rememberme EXPIRED SECONDS
    int REMEMBER_EXPIRED_SECONDS = 3600* 24 * 10;

    int ENTITY_TYPE_POST=1;

    int ENTITY_TYPE_COMMENT=2;
}
