package com.example.borsh.database;

import java.util.Locale;

public class DBContract {
		static final String DB_NAME = "rss";
		static final String DB_TABLE = "feed";
		static final int DB_VERSION = 1;

		public static final String POST_ID = "_id";
		public static final String POST_TITLE = "title";
		public static final String POST_DATE = "date";
		public static final String POST_IMAGE_URI = "imageuri";
		public static final String POST_DESCRIPTION = "description";
		public static final String POST_HYPERLINK = "hyperlink";

		static final String DB_QUERY_CREATE_TABLE = String.format(Locale.US,
				"create table %s (%s integer primary key autoincrement, %s text, %s integer, %s text, %s text, %s text, UNIQUE(%s));",
				DB_TABLE, POST_ID, POST_TITLE, POST_DATE, POST_IMAGE_URI, POST_DESCRIPTION, POST_HYPERLINK, POST_HYPERLINK);
}
