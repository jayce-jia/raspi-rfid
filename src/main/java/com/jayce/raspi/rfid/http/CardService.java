package com.jayce.raspi.rfid.http;

import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Jaycejia on 2017/3/27.
 */
public interface CardService {
    @POST("newCard")
    Observable<String> postNewCard(@Header("Fridge-ID") String fridgeId, @Query("cardId") String cardId);
}
