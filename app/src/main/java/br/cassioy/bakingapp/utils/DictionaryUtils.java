package br.cassioy.bakingapp.utils;

import android.content.Context;

import java.util.HashMap;

public class DictionaryUtils {

    public static HashMap<String, String> parseStringArray(Context context, int stringArrayResourceId){

        String[] stringArray = context.getResources().getStringArray(stringArrayResourceId);
        HashMap<String, String> hashMap = new HashMap<>(stringArray.length);
        for (String values: stringArray) {
            String[] splitResult = values.split("\\|", 2);
            hashMap.put(splitResult[0], splitResult[1]);
        }

        return hashMap;
    }



}
