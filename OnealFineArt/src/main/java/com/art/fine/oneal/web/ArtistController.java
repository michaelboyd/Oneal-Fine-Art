package com.art.fine.oneal.web;

import java.util.Date;
import java.util.Set;

import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.art.fine.oneal.domain.Artist;
import com.art.fine.oneal.domain.Artwork;
import com.art.fine.oneal.domain.Image;
import com.art.fine.oneal.domain.ImageFile;
import com.art.fine.oneal.domain.SpecialGroup;
import com.art.fine.oneal.utils.ImageUtil;

@RooWebScaffold(path = "admin/manage/artist", formBackingObject = Artist.class)
@RequestMapping("/admin/manage/artist")
@Controller
public class ArtistController {
	
	@RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model uiModel) {
		Artist artist = new Artist();
		artist.setCreateDate(new Date());
        uiModel.addAttribute("artist", artist);
        addDateTimeFormatPatterns(uiModel);        
        return "admin/manage/artist/create";
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Artist artist = Artist.findArtist(id);
        Set <Artwork> artworks = artist.getArtworks();
        if(artworks != null)
        {
            for(Artwork artwork : artworks)
            {
            	//remove the artwork from the Artist's set table
            	artist.getArtworks().remove(artwork);
            	artist.merge();
            	
            	Set <SpecialGroup> specialGroups = artwork.getSpecialGroups();
            	if(specialGroups != null)
            	{
            		//remove the artwork from the SpecialGroups artwork table
            		for(SpecialGroup specialGroup : specialGroups)
            		{
            			specialGroup.getArtworks().remove(artwork);
            			specialGroup.merge();
            		}
            	}
            	
            	//detach the images (not sure if detach is the right word but this works)
                Set <Image> images = artwork.getImages();
                if(images != null)
                {
                    for(Image image : images)
                    {
                    	Set <ImageFile> imageFiles = image.getImageFiles();
                    	for(ImageFile imageFile : imageFiles)
                    	{
                    		imageFile.clear();
                    	}
                    }
                }
            	//delete the artwork and images (if applicable)
            	artwork.remove();
            }
        }
        
        artist.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/admin/manage/artist";
    }


	@RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
		Artist artist = Artist.findArtist(id);
        Set <Artwork> artworks = artist.getArtworks();        
        ImageUtil.setMainThumbImageIdFromArtwork(artworks);
        uiModel.addAttribute("artist", artist);
        uiModel.addAttribute("artworks", artist.getArtworks());
        addDateTimeFormatPatterns(uiModel);
        return "admin/manage/artist/update";
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") Long id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        Artist artist = Artist.findArtist(id);
        Set <Artwork> artworks = artist.getArtworks();        
        ImageUtil.setMainThumbImageIdFromArtwork(artworks);
        uiModel.addAttribute("artist", artist);        
        uiModel.addAttribute("itemId", id);
        return "admin/manage/artist/show";
    }

	@RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            uiModel.addAttribute("artists", Artist.findArtistEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) Artist.countArtists() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("artists", Artist.findAllArtists());
        }
        addDateTimeFormatPatterns(uiModel);
        return "admin/manage/artist/list";
    }
}
