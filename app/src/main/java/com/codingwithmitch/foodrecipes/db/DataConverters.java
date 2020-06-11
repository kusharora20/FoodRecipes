package com.codingwithmitch.foodrecipes.db;

import androidx.room.TypeConverter;

import java.io.Serializable;

public class DataConverters implements Serializable {

    @TypeConverter
    public String getStringFromArray(String[] array){

        StringBuilder stringBuilder = new StringBuilder();

        for (String s : array) stringBuilder.append(s).append("::");

        return stringBuilder.toString();
    }

    @TypeConverter
    public String[] getArrayFromString(String string){
        return string.split("::");
    }

}
