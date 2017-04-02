package com.jayce.raspi.rfid.nfc;

import com.jayce.raspi.rfid.enu.SysConfig;
import com.jayce.raspi.rfid.http.CardService;
import com.jayce.raspi.rfid.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import rx.Observer;
import rx.schedulers.Schedulers;

import java.io.IOException;

/**
 * Created by Jaycejia on 2017/3/30.
 */
public class CardManager {
    private static final Logger logger = LoggerFactory.getLogger(CardManager.class);
    private final Retrofit retrofit;
    private CardInfo cardInfo;
    private Long invalidTimeout;
    private String fridgeId;

    public CardManager(Retrofit retrofit) throws IOException {
        this.retrofit = retrofit;
        this.invalidTimeout = Long.valueOf(String.valueOf(PropertiesUtil.getProperty(SysConfig.NFC_POLL_INVALID_TIMEOUT)));
        this.fridgeId = (String) PropertiesUtil.getProperty(SysConfig.NFC_READER_ID);
    }

    public synchronized void onCard(String cardId) {
        if (cardInfo == null) {
            cardInfo = new CardInfo(cardId, System.currentTimeMillis());
            postNewCard(cardId);
            return;
        }
        String oldId = cardInfo.getCarId();
        if (oldId.equals(cardId)) {//如果读到之前的卡
            long current = System.currentTimeMillis();
            if ((current - cardInfo.getTime()) > invalidTimeout) {//如果距离上一次超过1s
                postNewCard(cardId);
                cardInfo.setTime(current);
            }
        } else {//若为新卡
            cardInfo.setCarId(cardId);
            cardInfo.setTime(System.currentTimeMillis());
            postNewCard(cardId);
        }

    }

    private void postNewCard(String cardId) {
        logger.info("向服务器发送新的cardId:{}", cardId);
        retrofit.create(CardService.class)
                .postNewCard(fridgeId, cardId)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        logger.error("请求错误", e);
                    }

                    @Override
                    public void onNext(String s) {
                        logger.debug("请求成功，收到服务器消息:{}", s);
                    }
                });
    }

    private class CardInfo {
        private String carId;
        private Long time;

        CardInfo(String carId, Long time) {
            this.carId = carId;
            this.time = time;
        }

        public String getCarId() {
            return carId;
        }

        public void setCarId(String carId) {
            this.carId = carId;
        }

        public Long getTime() {
            return time;
        }

        public void setTime(Long time) {
            this.time = time;
        }
    }
}
