package com.art.fine.oneal.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.art.fine.oneal.domain.Artist;
import com.art.fine.oneal.domain.Artwork;
import com.art.fine.oneal.domain.Image;
import com.art.fine.oneal.domain.ImageFile;
import com.art.fine.oneal.domain.SpecialGroup;
import com.art.fine.oneal.utils.ImageSize;
import com.art.fine.oneal.utils.ImageType;
import com.art.fine.oneal.utils.ImageUtil;

@RooWebScaffold(path = "admin/manage/artwork", formBackingObject = Artwork.class)
@RequestMapping("/admin/manage/artwork")
@Controller
public class ArtworkController {

	@RequestMapping(method = RequestMethod.POST)
    public String create(@Valid Artwork artwork, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("artwork", artwork);
            addDateTimeFormatPatterns(uiModel);
            return "admin/manage/artwork/create";
        }
        uiModel.asMap().clear(); 
        artwork.persist();
        Artist artist = artwork.getArtist();
        artist.getArtworks().add(artwork);
        artist.merge();
        return "redirect:/admin/manage/artwork/" + encodeUrlPathSegment(artwork.getId().toString(), httpServletRequest);
    }

	@RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(@RequestParam(value = "artistId", required = false) Long artistId, Model uiModel) {

		if(artistId != null)
		{
			Artist artist = Artist.findArtist(artistId);
			Map <String, Object> modelMap = uiModel.asMap();
			List <Artist> artists = (List)modelMap.get("artists");
			artists.clear();
			artists.add(artist);
			uiModel.addAttribute("artistName", artist.getName());
		}
		
		Artwork artwork = new Artwork();
		artwork.setCreateDate(new Date());
        uiModel.addAttribute("artwork", artwork);
        addDateTimeFormatPatterns(uiModel);
        List dependencies = new ArrayList();
        if (Artist.countArtists() == 0) {
            dependencies.add(new String[]{"artist", "admin/manage/artist"});
        }
        uiModel.addAttribute("dependencies", dependencies);
        return "admin/manage/artwork/create";
    }
	
	@RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
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
        addDateTimeFormatPatterns(uiModel);
        return "admin/manage/artwork/list";
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {

		Artwork artwork = Artwork.findArtwork(id);
        
		//remove the artwork from any SpecialGroups
        Set <SpecialGroup> specialGroups = artwork.getSpecialGroups();
        if(specialGroups != null)
        {
            for(SpecialGroup specialGroup : specialGroups){
                specialGroup.getArtworks().remove(artwork);
            }
        }
        
        Artist artist = artwork.getArtist();
        artist.getArtworks().remove(artwork);
        artist.merge();
        
        Set <Image> images = artwork.getImages();
        if(images != null)
        {
            for(Image image : images)
            {
            	//image.getImageFiles().clear();
            	Set <ImageFile> imageFiles = image.getImageFiles();
            	for(ImageFile imageFile : imageFiles)
            	{
            		imageFile.clear();
            	}
            }
        }
        
        //remove the artwork
        artwork.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        
        
        if(page == null && size == null){
        	//deleted from artist page
        	return "redirect:/admin/manage/artist/" + artist.getId();
        }
        else
        {
        	return "redirect:/admin/manage/artwork";
        }       
        
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") Long id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        Artwork artwork = Artwork.findArtwork(id);
        List <Image> images = Image.findImagesByArtwork(artwork).getResultList();
        if(images != null && !images.isEmpty())
        {
            ImageUtil.setThumbImageIdFromImage(images);
            Image bigImage = Image.findImage(ImageUtil.getImageIdByType(images, ImageType.MainPicture));
            if(bigImage != null){
	            List <ImageFile> imageFiles = ImageFile.findImageFilesByImageAndImageSize(bigImage, ImageSize.big).getResultList();
	            if(imageFiles != null && !imageFiles.isEmpty()){
	            	ImageFile imageFile = imageFiles.get(0);
	            	artwork.bigImageId = imageFile.getId();
	            }
            }
        }
        uiModel.addAttribute("artwork", artwork);
        uiModel.addAttribute("itemId", id);
        return "admin/manage/artwork/show";
    }

	@RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid Artwork artwork, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("artwork", artwork);
            addDateTimeFormatPatterns(uiModel);
            return "admin/manage/artwork/update";
        }
        uiModel.asMap().clear();
        artwork.merge();
        return "redirect:/admin/manage/artwork/" + encodeUrlPathSegment(artwork.getId().toString(), httpServletRequest);
    }


	@RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        Artwork artwork = Artwork.findArtwork(id);
        List <Image> images = Image.findImagesByArtwork(artwork).getResultList();
        if(images != null && !images.isEmpty())
        {
            ImageUtil.setThumbImageIdFromImage(images);
            Image bigImage = Image.findImage(ImageUtil.getImageIdByType(images, ImageType.MainPicture));
            if(bigImage != null){
            	uiModel.addAttribute("bigImageId", ImageUtil.getImageFileIdBySize(bigImage.getImageFiles(), ImageSize.big));
            }
        }
        uiModel.addAttribute("artwork", artwork);
        addDateTimeFormatPatterns(uiModel);
        return "admin/manage/artwork/update";
    }
}
