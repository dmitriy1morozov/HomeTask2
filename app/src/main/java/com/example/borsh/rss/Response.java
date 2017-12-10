package com.example.borsh.rss;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class Response{

	@SerializedName("feed")
	private Feed feed;

	@SerializedName("items")
	private List<ItemsItem> items;

	@SerializedName("status")
	private String status;

	public void setFeed(Feed feed){
		this.feed = feed;
	}

	public Feed getFeed(){
		return feed;
	}

	public void setItems(List<ItemsItem> items){
		this.items = items;
	}

	public List<ItemsItem> getItems(){
		return items;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"Response{" + 
			"feed = '" + feed + '\'' + 
			",items = '" + items + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}