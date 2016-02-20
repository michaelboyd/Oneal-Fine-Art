/**
 * 
 */
package com.art.fine.oneal.utils;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.art.fine.oneal.domain.Artwork;
import com.art.fine.oneal.domain.Image;
import com.art.fine.oneal.domain.ImageFile;

/**
 * @author BigDaddy
 *
 */
public class ImageUtil {

	public static Long getImageFileIdByImageAndImageSize(Image image, ImageSize imageSize)
	{
	    List <ImageFile> imageFiles = ImageFile.findImageFilesByImageAndImageSize(image, imageSize).getResultList();
	    if(!imageFiles.isEmpty())
	    {
	    	ImageFile imageFile = imageFiles.get(0);        	
	    	return imageFile.getId();
	    }
		return null;
	}

	
	public static Long getImageFileIdBySize(Set <ImageFile> imageFiles, ImageSize imageSize)
	{
		for(ImageFile imageFile : imageFiles){
        	if(imageFile.getImageSize().equals(imageSize)){
        		return imageFile.getId();
        	}
        }
        return null;
	}
	
	public static Long getImageIdByType(Collection <Image> images, ImageType imageType)
	{
		for(Image image : images){
        	if(image.getImageType().equals(imageType)){
        		return image.getId();
        	}
        }
        return null;
	}
	
	
	public static void setImageIdFromArtwork(Collection <Artwork> artworks, ImageSize imageSize, ImageType imageType)
	{
		for(Artwork artwork : artworks){
			Set <Image> images = artwork.getImages(); 
			for(Image image : images){
				if(image.getImageType().equals(imageType)){
					artwork.thumbImageId = ImageUtil.getImageFileIdByImageAndImageSize(image, imageSize);
				}
			}
		}
	}
	
	public static void setMainThumbImageIdFromArtwork(Collection <Artwork> artworks)
	{
		for(Artwork artwork : artworks){
			List <Image> images = Image.findImagesByArtwork(artwork).getResultList();
			for(Image image : images){
				if(image.getImageType().equals(ImageType.MainPicture)){
					artwork.thumbImageId = ImageUtil.getImageFileIdByImageAndImageSize(image, ImageSize.thumb);
				}
			}
		}
	}
	
	public static void setMainThumbImageId(Collection <Artwork> artworks, ImageSize imageSize)
	{
		for(Artwork artwork : artworks){
			List <Image> images = Image.findImagesByArtwork(artwork).getResultList();
			for(Image image : images){
				if(image.getImageType().equals(ImageType.MainPicture)){
					artwork.thumbImageId = ImageUtil.getImageFileIdByImageAndImageSize(image, imageSize);
				}
			}
		}
	}
	
	
	public static void setThumbImageIdFromImage(Collection<Image> images)
	{
		for(Image image : images){
			image.thumbImageId = ImageUtil.getImageFileIdByImageAndImageSize(image, ImageSize.thumb);
		}
	}
	
}
