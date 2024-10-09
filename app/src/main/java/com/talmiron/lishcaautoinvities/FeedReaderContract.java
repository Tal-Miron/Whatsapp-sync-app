package com.talmiron.lishcaautoinvities;

import android.provider.BaseColumns;

public final class FeedReaderContract {
    private FeedReaderContract() {}

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "History";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_MEETING_NAME = "mettingname";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_HOUR = "hour";
        public static final String COLUMN_RAW_DATA = "rawdata";
    }
}
