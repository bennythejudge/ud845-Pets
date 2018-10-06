package com.example.android.pets.data;

import android.provider.BaseColumns;

public final class PetContract {
    private static final String TAG = "PetContract";

    // empty constructor
    private PetContract() {}

    // inner class to define the constants
    public static abstract class PetEntry implements BaseColumns {
        public static final String TABLE_NAME = "pets";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PET_NAME = "name";
        public static final String COLUMN_PET_BREED = "breed";
        public static final String COLUMN_PET_GENDER = "gender";
        public static final String COLUMN_PET_WEIGHT = "weight";

        public static final Integer GENDER_UNKNOWN = 0;
        public static final Integer GENDER_MALE = 1;
        public static final Integer GENDER_FEMALE = 2;
    }

}
