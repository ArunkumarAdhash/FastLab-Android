package com.lifehopehealthapp.retrofitService.crypto;

public class DecryptionImpl implements CryptoStrategy {
    @Override
    public String encrypt(String body) {
        return body;
    }

    @Override
    public String decrypt(String data) throws Exception {
        //return CryptoUtil.decrypt(data);
        return CryptoHelper.decrypt(data);
    }


}
