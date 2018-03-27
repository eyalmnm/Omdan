package com.em_projects.omdan.utils;

/**
 * Created by eyalmuchtar on 3/27/18.
 */

public class ErrorsUtils {

    public static int getError(String response) {
        int errorIdx = response.indexOf("error") + "error".length();
        int sepIdx = response.indexOf(":", errorIdx) + ":".length();
        int lastIdx = response.indexOf(",", sepIdx);
        String errorCodeStr = "";
        if (0 < lastIdx) {
            errorCodeStr = response.substring(sepIdx, lastIdx).trim();
        } else {
            errorCodeStr = response.substring(sepIdx, sepIdx + 4).trim();
        }
        return Integer.parseInt(errorCodeStr);
    }
}
