package com.art.fine.oneal.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import com.art.fine.oneal.domain.Artwork;
import com.art.fine.oneal.domain.Image;
import com.art.fine.oneal.domain.ImageFile;
import com.art.fine.oneal.service.ImageService;
import com.art.fine.oneal.utils.ImageSize;
import com.art.fine.oneal.utils.ImageType;
import com.art.fine.oneal.utils.ImageUtil;

@RooWebScaffold(path = "admin/manage/image", formBackingObject = Image.class)
@RequestMapping("/admin/manage/image")
@Controller
public class ImageController implements MessageSourceAware {
	
	private MessageSource messageSource;

    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
    }
    
	@RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
		Image image = Image.findImage(id);
		uiModel.addAttribute("artworkTitle", image.getArtwork().getTitle());
		uiModel.addAttribute("artistName", image.getArtwork().getArtist().getName());
		uiModel.addAttribute("image", image);
		uiModel.addAttribute("imageId", ImageUtil.getImageFileIdByImageAndImageSize(image, ImageSize.big));
        uiModel.addAttribute("createDate", image.getCreateDate());
        //filterImageTypes(image, uiModel);
        addDateTimeFormatPatterns(uiModel);
        return "admin/manage/image/update";
    }
    
	@RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid Image image, BindingResult bindingResult, Model uiModel, 
    		MultipartHttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("image", image);
            addDateTimeFormatPatterns(uiModel);
            return "admin/manage/image/update";
        }
        
        uiModel.asMap().clear();
        image.merge();
        
        return "redirect:/admin/manage/image/" + encodeUrlPathSegment(image.getId().toString(), httpServletRequest);
    }

	@RequestMapping(method = RequestMethod.POST)
    public String create(@Valid Image image, BindingResult bindingResult, Model uiModel, 
    		MultipartHttpServletRequest httpServletRequest) {

		if (bindingResult.hasErrors()) {
            uiModel.addAttribute("image", image);
            filterImageTypes(image, uiModel);
            addDateTimeFormatPatterns(uiModel);
            return "admin/manage/image/create";
        }

        uiModel.asMap().clear();

        if(image.getId() == null)
        {
            //image.setFile(null);
            image.persist();
        	if(image.getFile() != null && image.getFile().length > 0){
                	ImageService.saveImage(image, messageSource);
            }

            Artwork artwork = image.getArtwork();
            artwork.getImages().add(image);
            image.setFile(null);
            image.merge();        	

        }
        else
        {
            if(image.getFile() != null && image.getFile().length > 0){
            	ImageService.updateImage(image, messageSource);
            }
        	image.setFile(null);
            image.merge();
        }
        
        
        //return "redirect:/admin/manage/image/" + encodeUrlPathSegment(image.getId().toString(), httpServletRequest);
        
        return "redirect:/admin/manage/artwork/" + encodeUrlPathSegment(image.getArtwork().getId().toString(), httpServletRequest);
    }
	
	@RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(@RequestParam(value = "artworkId", required = true) Long artworkId, Model uiModel) {
		
		Artwork artwork = Artwork.findArtwork(artworkId);
		Map <String, Object> modelMap = uiModel.asMap();
		List <Artwork> artworks = (List)modelMap.get("artworks");
		artworks.clear();
		artworks.add(artwork);
		filterImageTypes(artwork, uiModel);
		uiModel.addAttribute("artworkTitle", artwork.getTitle());
		uiModel.addAttribute("artistName", artwork.getArtist().getName());
		
		Image image = new Image();
		image.setCreateDate(new Date());
        uiModel.addAttribute("image", image);
        addDateTimeFormatPatterns(uiModel);
        List dependencies = new ArrayList();
        if (Artwork.countArtworks() == 0) {
            dependencies.add(new String[]{"artwork", "admin/manage/artwork"});
        }
        uiModel.addAttribute("dependencies", dependencies);
        return "admin/manage/image/create";
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Image image = Image.findImage(id);
        Artwork artwork = image.getArtwork();
        artwork.getImages().remove(image);		
		image.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        
        return "redirect:/admin/manage/artwork/" + image.getArtwork().getId().toString();
        //return "redirect:/admin/manage/image";
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String show(@PathVariable("id") Long id, Model uiModel) {
        addDateTimeFormatPatterns(uiModel);
        Image image = Image.findImage(id);
        List <ImageFile> imageFiles = ImageFile.findImageFilesByImageAndImageSize(image, ImageSize.big).getResultList();
        if(!imageFiles.isEmpty())
        {
        	ImageFile imageFile = imageFiles.get(0);        	
        	uiModel.addAttribute("imageId", imageFile.getId());
        }
        uiModel.addAttribute("image", image);
        uiModel.addAttribute("itemId", id);
        return "admin/manage/image/show";
    }

	@RequestMapping(method = RequestMethod.GET)
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            List <Image> images = Image.findImageEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo);
            ImageUtil.setThumbImageIdFromImage(images);
            uiModel.addAttribute("images", images);
            float nrOfPages = (float) Image.countImages() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
        	List <Image> images = Image.findAllImages();
        	ImageUtil.setThumbImageIdFromImage(images);
            uiModel.addAttribute("images", images);
        }
        addDateTimeFormatPatterns(uiModel);
        return "admin/manage/image/list";
    }

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	private void filterImageTypes(Image image, Model uiModel)
	{
        Artwork artwork = image.getArtwork();
        Map <String, Object> modelMap = uiModel.asMap();
		//get possible existing images
		List <Image> images = Image.findImagesByArtwork(artwork).getResultList();
		if(images != null && !images.isEmpty())
		{
			//all imageTypes
			List <ImageType> imageTypes = (List)modelMap.get("imagetypes");
			Set <ImageType> usedImageTypes = new HashSet <ImageType>();
			for(Image artworkImage : images){
				usedImageTypes.add(artworkImage.getImageType());
			}
			
			List <ImageType> filteredImageTypes = new ArrayList<ImageType>();
			for(ImageType imageType : imageTypes){
				boolean inUse = false;
				for(ImageType usedImageType : usedImageTypes){
					if(imageType.equals(usedImageType)){
						inUse = true;
						break;
					}					
				}
				if(!inUse){
					filteredImageTypes.add(imageType);
				}
			}
			modelMap.put("imagetypes", filteredImageTypes);			
		}
		
	}

	private void filterImageTypes(Artwork artwork, Model uiModel)
	{
        Map <String, Object> modelMap = uiModel.asMap();
		//get possible existing images
		List <Image> images = Image.findImagesByArtwork(artwork).getResultList();
		if(images != null && !images.isEmpty())
		{
			//all imageTypes
			List <ImageType> imageTypes = (List)modelMap.get("imagetypes");
			Set <ImageType> usedImageTypes = new HashSet <ImageType>();
			for(Image artworkImage : images){
				usedImageTypes.add(artworkImage.getImageType());
			}
			
			List <ImageType> filteredImageTypes = new ArrayList<ImageType>();
			for(ImageType imageType : imageTypes){
				boolean inUse = false;
				for(ImageType usedImageType : usedImageTypes){
					if(imageType.equals(usedImageType)){
						inUse = true;
						break;
					}					
				}
				if(!inUse){
					filteredImageTypes.add(imageType);
				}
			}
			modelMap.put("imagetypes", filteredImageTypes);			
		}
		
	}
	
}