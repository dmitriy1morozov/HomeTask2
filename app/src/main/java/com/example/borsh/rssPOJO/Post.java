package com.example.borsh.rssPOJO;

import android.net.Uri;

public class Post {

		private String mTitle;
		private long mDate;
		private Uri mImage;
		private String mDescription;
		private Uri mHyperlink;

		public Post(String title, long date, Uri image, String description, Uri hyperlink) {
				this.mTitle = title;
				this.mDate = date;
				this.mImage = image;
				this.mDescription = description;
				this.mHyperlink = hyperlink;
		}

		public String getTitle() {
				return mTitle;
		}

		public long getDate() {
				return mDate;
		}

		public Uri getImage() {
				return mImage;
		}

		public String getDescription() {
				return mDescription;
		}

		public Uri getHyperlink() {
				return mHyperlink;
		}
}
