package com.example.borsh.rssPOJO;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class Enclosure{

	@SerializedName("link")
	private String link;

	@SerializedName("length")
	private int length;

	@SerializedName("type")
	private String type;

	public void setLink(String link){
		this.link = link;
	}

	public String getLink(){
		return link;
	}

	public void setLength(int length){
		this.length = length;
	}

	public int getLength(){
		return length;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	@Override
 	public String toString(){
		return 
			"Enclosure{" + 
			"link = '" + link + '\'' + 
			",length = '" + length + '\'' + 
			",type = '" + type + '\'' + 
			"}";
		}
}