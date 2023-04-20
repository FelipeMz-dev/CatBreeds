package com.example.catbreeds.services;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Authentication result : success (user details) or error message.
 */
public class ApiResult {
    @Nullable
    private JSONObject objResult;
    @Nullable
    private JSONArray arrayResult;
    @Nullable
    private String stringResult;
    @Nullable
    private Integer error;

    ApiResult(@Nullable Integer error) {
        this.error = error;
    }

    ApiResult(@Nullable JSONObject result) {
        this.objResult = result;
    }

    ApiResult(@Nullable JSONArray result) {
        this.arrayResult = result;
    }

    ApiResult(@Nullable String result) {
        this.stringResult = result;
    }

    @Nullable
    public JSONObject getObjResult() {
        return objResult;
    }

    @Nullable
    public JSONArray getArrayResult() {
        return arrayResult;
    }

    @Nullable
    public String getStringResult() {
        return stringResult;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}