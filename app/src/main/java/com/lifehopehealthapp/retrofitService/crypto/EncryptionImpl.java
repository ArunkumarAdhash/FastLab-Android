package com.lifehopehealthapp.retrofitService.crypto;


public class EncryptionImpl implements CryptoStrategy {

    @Override
    public String encrypt(String body) throws Exception {
        return CryptoHelper.encrypt(body);
    }

    @Override
    public String decrypt(String data) {
        return data;
    }
}
