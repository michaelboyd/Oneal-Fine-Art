package com.art.fine.oneal.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.art.fine.oneal.domain.Artist;
import com.art.fine.oneal.domain.Artwork;
import com.art.fine.oneal.domain.Image;
import com.art.fine.oneal.domain.ImageFile;
import com.art.fine.oneal.utils.ImageSize;
import com.art.fine.oneal.utils.ImageType;
import com.art.fine.oneal.utils.ImageUtil;

@RequestMapping("/gallery/**")
@Controller
public class GalleryController {

    @RequestMapping
    public void get(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
    	
    	System.out.println("stop");
    }

    @RequestMapping(method = RequestMethod.POST, value = "{id}")
    public void post(@PathVariable Long id, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
    }

    @RequestMapping
    public String index(Model uiModel, HttpServletRequest request, HttpServletResponse response) {
    	
    	List <Artist> artists = Artist.findAllArtistsWithArt();
    	if(request.getSession().getAttribute("artists") == null){
    		request.getSession().setAttribute("artists", artists);
    	}
    	uiModel.addAttribute("artists", artists);
    	List <ImageFile> imageFiles = ImageFile.findImageFilesByImageSize(ImageSize.big).getResultList();
    	
    	ImageFile activeImage = null;
    	
    	//set the active image but not the Hunters picture
    	for(ImageFile imageFile : imageFiles)
    	{
    		if(!imageFile.getImage().getArtwork().getArtist().getName().equals("Arturo Mercado")
    				&& !imageFile.getImage().getArtwork().getTitle().equals("Hunters"))
    		{
    			activeImage = imageFile;
    			uiModel.addAttribute("activeImage", activeImage);
    			break;
    		}
    	}
    	
    	List <ImageFile> otherImages = new ArrayList <ImageFile>();
   		for(ImageFile imageFile : imageFiles)
   		{
   			if(!imageFile.getId().equals(activeImage.getId()))
   			{
   	    		if(!imageFile.getImage().getArtwork().getArtist().getName().equals("Arturo Mercado")
   	    				&& !imageFile.getImage().getArtwork().getTitle().equals("Hunters"))
   	    		{
   	    			otherImages.add(imageFile);
   	    		}   				
   			}        	
    	}    
   		uiModel.addAttribute("imageFiles", otherImages);
    	return "gallery/index";
    }
    
	@RequestMapping(value = "/artworks", method = RequestMethod.GET)
    public String artworks(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            List <Artwork> artworks = Artwork.findArtworkEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo);
            ImageUtil.setMainThumbImageIdFromArtwork(artworks);
            uiModel.addAttribute("artworks", artworks);
            float nrOfPages = (float) Artwork.countArtworks() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
        	List <Artwork> artworks = Artwork.findAllArtworks();
        	ImageUtil.setMainThumbImageIdFromArtwork(artworks);
            uiModel.addAttribute("artworks", artworks);
        }
        uiModel.addAttribute("artists", Artist.findAllArtistsWithArt());
        addDateTimeFormatPatterns(uiModel);
        return "gallery/artwork/list";
    }
    
	@RequestMapping(value = "/artist/{id}", method = RequestMethod.GET)
    public String artist(@PathVariable("id") Long id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        Artist artist = Artist.findArtist(id);
        Set <Artwork> artworks = artist.getArtworks();
        ImageUtil.setMainThumbImageIdFromArtwork(artworks);
        uiModel.addAttribute("artist", artist);        
        uiModel.addAttribute("itemId", id);
        uiModel.addAttribute("artists", Artist.findAllArtistsWithArt());
        return "gallery/artist/show";
    }
	
	@RequestMapping(value = "/artwork/{id}", method = RequestMethod.GET)
    public String artwork(@PathVariable("id") Long id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        Artwork artwork = Artwork.findArtwork(id);
        List <Image> images = Image.findImagesByArtwork(artwork).getResultList();
        if(images != null && !images.isEmpty())
        {
            ImageUtil.setThumbImageIdFromImage(images);
            Image bigImage = Image.findImage(ImageUtil.getImageIdByType(images, ImageType.MainPicture));
            if(bigImage != null){
            	//the result sets should have only one image file
                List <ImageFile> imageFiles = ImageFile.findImageFilesByImageAndImageSize(bigImage, ImageSize.big).getResultList();
                List <ImageFile> fullSizeImageFiles = ImageFile.findImageFilesByImageAndImageSize(bigImage, ImageSize.fullSize).getResultList();
                if(imageFiles != null && !imageFiles.isEmpty()){
                	ImageFile imageFile = imageFiles.get(0);
                	artwork.bigImageId = imageFile.getId();
                }
                if(fullSizeImageFiles != null && !fullSizeImageFiles.isEmpty()){
                	ImageFile fullSizeImageFile = fullSizeImageFiles.get(0);
                	artwork.fullSizeImageId = fullSizeImageFile.getId();
                }
            }
        }
        uiModel.addAttribute("artwork", artwork);
        uiModel.addAttribute("itemId", id);
        uiModel.addAttribute("artists", Artist.findAllArtistsWithArt());
        return "gallery/artwork/show";
    }
	
    
    void addDateTimeFormatPatterns(Model uiModel) {
        uiModel.addAttribute("artist_createdate_date_format", "MM-dd-yyyy");
    }
	
}
