package com.lambton.projects.tovisit_chaitanya_c0777253_android.typeconverters;

import androidx.room.TypeConverter;

import java.util.Date;

public class ConvertDatatypes
{

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
