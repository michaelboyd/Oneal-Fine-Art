/**
 * 
 */
package com.art.fine.oneal.bean;

import java.io.Serializable;
import java.util.Date;

import com.art.fine.oneal.domain.Artwork;
import com.art.fine.oneal.domain.ImageFile;
import com.art.fine.oneal.utils.ImageType;

/**
 * @author BigDaddy
 *
 */
public class ImageBean implements Serializable{
	
	private Artwork artwork;
	private String caption;
	private ImageFile imageFile;
	private ImageType imageType;
	private Date createDate;
	
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public ImageType getImageType() {
		return imageType;
	}
	public void setImageType(ImageType imageType) {
		this.imageType = imageType;
	}
	public Artwork getArtwork() {
		return artwork;
	}
	public void setArtwork(Artwork artwork) {
		this.artwork = artwork;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public ImageFile getImageFile() {
		return imageFile;
	}
	public void setImageFile(ImageFile imageFile) {
		this.imageFile = imageFile;
	}
	
	

}
