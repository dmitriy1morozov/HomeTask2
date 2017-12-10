package com.example.borsh.rss;

import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class ItemsItem{

	@SerializedName("thumbnail")
	private String thumbnail;

	@SerializedName("enclosure")
	private Enclosure enclosure;

	@SerializedName("author")
	private String author;

	@SerializedName("link")
	private String link;

	@SerializedName("guid")
	private String guid;

	@SerializedName("description")
	private String description;

	@SerializedName("categories")
	private List<Object> categories;

	@SerializedName("title")
	private String title;

	@SerializedName("pubDate")
	private String pubDate;

	@SerializedName("content")
	private String content;

	public void setThumbnail(String thumbnail){
		this.thumbnail = thumbnail;
	}

	public String getThumbnail(){
		return thumbnail;
	}

	public void setEnclosure(Enclosure enclosure){
		this.enclosure = enclosure;
	}

	public Enclosure getEnclosure(){
		return enclosure;
	}

	public void setAuthor(String author){
		this.author = author;
	}

	public String getAuthor(){
		return author;
	}

	public void setLink(String link){
		this.link = link;
	}

	public String getLink(){
		return link;
	}

	public void setGuid(String guid){
		this.guid = guid;
	}

	public String getGuid(){
		return guid;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		return description;
	}

	public void setCategories(List<Object> categories){
		this.categories = categories;
	}

	public List<Object> getCategories(){
		return categories;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setPubDate(String pubDate){
		this.pubDate = pubDate;
	}

	public String getPubDate(){
		return pubDate;
	}

	public void setContent(String content){
		this.content = content;
	}

	public String getContent(){
		return content;
	}

	@Override
 	public String toString(){
		return 
			"ItemsItem{" + 
			"thumbnail = '" + thumbnail + '\'' + 
			",enclosure = '" + enclosure + '\'' + 
			",author = '" + author + '\'' + 
			",link = '" + link + '\'' + 
			",guid = '" + guid + '\'' + 
			",description = '" + description + '\'' + 
			",categories = '" + categories + '\'' + 
			",title = '" + title + '\'' + 
			",pubDate = '" + pubDate + '\'' + 
			",content = '" + content + '\'' + 
			"}";
		}
}