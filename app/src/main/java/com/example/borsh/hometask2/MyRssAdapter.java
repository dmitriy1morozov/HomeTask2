package com.example.borsh.hometask2;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import java.util.List;
import java.util.Map;

public class MyRssAdapter extends SimpleAdapter {

		public MyRssAdapter(Context context, List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
				super(context, data, resource, from, to);
		}

		@Override public void setViewImage(ImageView imageView, String uri) {
				Log.d("MyLogs", "setViewImage: ");

				final ProgressBar progressBar = ((ViewGroup)imageView.getParent()).findViewById(R.id.progress_item_thumb_loading);

				RequestOptions options = new RequestOptions();
				options.centerCrop();
				Glide.with(imageView.getContext())
						.load(uri)
						.apply(options)
						.thumbnail(0.1f)
						.listener(new RequestListener<Drawable>() {
						@Override public boolean onLoadFailed(@Nullable GlideException e, Object model,
								Target<Drawable> target, boolean isFirstResource) {
								progressBar.setVisibility(View.GONE);
								return false;
						}

						@Override
						public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
								DataSource dataSource, boolean isFirstResource) {
								progressBar.setVisibility(View.GONE);
								return false;
						}
				}).into(imageView);
		}
}
