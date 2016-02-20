/**
 * 
 */
package com.art.fine.oneal.service;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.media.jai.JAI;
import javax.media.jai.OpImage;
import javax.media.jai.RenderedOp;

import org.springframework.context.MessageSource;

import com.art.fine.oneal.domain.Image;
import com.art.fine.oneal.domain.ImageFile;
import com.art.fine.oneal.utils.ImageSize;
import com.sun.media.jai.codec.SeekableStream;

/**
 * @author BigDaddy
 *
 */

public class ImageService {
	
	/**
	 * the following is from
	 * http://www.digitalsanctuary.com/tech-blog/java/how-to-resize-uploaded-images-using-java-better-way.html 
	 */
    private static final String JAI_STREAM_ACTION = "stream";
    private static final String JAI_SUBSAMPLE_AVERAGE_ACTION = "SubsampleAverage";
    private static final String JAI_ENCODE_FORMAT_JPEG = "JPEG";
    private static final String JAI_ENCODE_ACTION = "encode";
    private static final String JPEG_CONTENT_TYPE = "image/jpeg";
    
    /**
    * this gets rid of exception for not using native acceleration
    */
    static
    {
    	System.setProperty("com.sun.media.jai.disableMediaLib", "true");
    }    

    
	public static void saveImage(Image image, MessageSource messageSource) {
		
		int maxWidthFull = new Integer(messageSource.getMessage("image_full_max_width", null, Locale.US));
        int maxWidthBig = new Integer(messageSource.getMessage("image_big_max_width", null, Locale.US));
        int maxWidthThumb = new Integer(messageSource.getMessage("image_thumb_max_width", null, Locale.US));
		
		try
		{
			//full size file
			ImageFile fullSizeFile = new ImageFile();
			fullSizeFile.setCreateDate(new Date());
			fullSizeFile.setFile(resizeImageAsJPG(image.getFile(), maxWidthFull));
			fullSizeFile.setImageSize(ImageSize.fullSize);
			fullSizeFile.setImage(image);
			fullSizeFile.persist();
			//big file
			ImageFile bigFile = new ImageFile();
			bigFile.setCreateDate(new Date());
			bigFile.setFile(resizeImageAsJPG(image.getFile(), maxWidthBig));
			bigFile.setImageSize(ImageSize.big);
			bigFile.setImage(image);
			bigFile.persist();
			//thumb file
			ImageFile thumbFile = new ImageFile();
			thumbFile.setCreateDate(new Date());
			thumbFile.setFile(resizeImageAsJPG(image.getFile(), maxWidthThumb));
			thumbFile.setImageSize(ImageSize.thumb);
			thumbFile.setImage(image);
			thumbFile.persist();
			//add the imageFile to the image
			image.getImageFiles().add(fullSizeFile);
			image.getImageFiles().add(bigFile);
			image.getImageFiles().add(thumbFile);			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	public static void updateImage(Image image, MessageSource messageSource) {

		int maxWidthFull = new Integer(messageSource.getMessage("image_full_max_width", null, Locale.US));
		int maxWidthBig = new Integer(messageSource.getMessage("image_big_max_width", null, Locale.US));
        int maxWidthThumb = new Integer(messageSource.getMessage("image_thumb_max_width", null, Locale.US));
        
        List <ImageFile> imageFiles = ImageFile.findImageFilesByImage(image).getResultList();

        for(ImageFile imageFile : imageFiles)
        {
        	if(imageFile.getImageSize().equals(ImageSize.fullSize)){
        		imageFile.setFile(resizeImageAsJPG(image.getFile(), maxWidthFull));
        	}
        	else if(imageFile.getImageSize().equals(ImageSize.big)){
        		imageFile.setFile(resizeImageAsJPG(image.getFile(), maxWidthBig));
        	}
        	else if(imageFile.getImageSize().equals(ImageSize.thumb)){
        		imageFile.setFile(resizeImageAsJPG(image.getFile(), maxWidthThumb));
        	}
        	imageFile.merge();
        }
        
	}
	
    
    
    /**
     * This method takes in an image as a byte array (currently supports GIF, JPG, PNG and
     * possibly other formats) and
     * resizes it to have a width no greater than the pMaxWidth parameter in pixels.
     * It converts the image to a standard
     * quality JPG and returns the byte array of that JPG image.
     *
     * @param pImageData
     *                the image data.
     * @param pMaxWidth
     *                the max width in pixels, 0 means do not scale.
     * @return the resized JPG image.
     * @throws IOException
     *                 if the image could not be manipulated correctly.
     */
	public static byte[] resizeImageAsJPG(byte[] pImageData, int pMaxWidth) {
		InputStream imageInputStream = new ByteArrayInputStream(pImageData);
		
		// read in the original image from an input stream
		SeekableStream seekableImageStream = SeekableStream.wrapInputStream(imageInputStream, true);
		RenderedOp originalImage = JAI.create(JAI_STREAM_ACTION, seekableImageStream);
		((OpImage) originalImage.getRendering()).setTileCache(null);
		int origImageWidth = originalImage.getWidth();
		// now resize the image
		double scale = 1.0;
		if (pMaxWidth > 0 && origImageWidth > pMaxWidth) {
			scale = (double) pMaxWidth / originalImage.getWidth();
		}
		ParameterBlock paramBlock = new ParameterBlock();
		paramBlock.addSource(originalImage); // The source image
		paramBlock.add(scale); // The xScale
		paramBlock.add(scale); // The yScale
		paramBlock.add(0.0); // The x translation
		paramBlock.add(0.0); // The y translation

		RenderingHints qualityHints = new RenderingHints(
				RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		RenderedOp resizedImage = JAI.create(JAI_SUBSAMPLE_AVERAGE_ACTION,
				paramBlock, qualityHints);

		// lastly, write the newly-resized image to an output stream, in a
		// specific encoding
		ByteArrayOutputStream encoderOutputStream = new ByteArrayOutputStream();
		JAI.create(JAI_ENCODE_ACTION, resizedImage, encoderOutputStream,
				JAI_ENCODE_FORMAT_JPEG, null);
		// Export to Byte Array
		byte[] resizedImageByteArray = encoderOutputStream.toByteArray();
		return resizedImageByteArray;
	}

	
	public void compressImage(BufferedImage input, float quality, String imageName, String directoryPath) throws IOException
	{
		Iterator iter = ImageIO.getImageWritersByFormatName("JPG");
		if (iter.hasNext()) 
		{
			ImageWriter writer = (ImageWriter) iter.next();
			ImageWriteParam iwp = writer.getDefaultWriteParam();			
			iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);			
			iwp.setCompressionQuality(quality);			
			File outFile = new File(directoryPath + imageName);
			try{				
				FileImageOutputStream output = new FileImageOutputStream(outFile);				
				writer.setOutput(output);
				IIOImage image = new IIOImage(input, null, null);			
				writer.write(null, image, iwp);
			}
			catch(Exception e){
				e.printStackTrace();
				throw new IOException();
			}			
		}		
	}

}
