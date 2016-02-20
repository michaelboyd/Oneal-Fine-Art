package com.art.fine.oneal.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.art.fine.oneal.domain.Image;
import com.art.fine.oneal.domain.SiteInfo;
import com.art.fine.oneal.utils.ImageType;
import com.art.fine.oneal.utils.InfoType;

@RooWebScaffold(path = "admin/manage/siteinfo", formBackingObject = SiteInfo.class)
@RequestMapping("/admin/manage/siteinfo")
@Controller
public class SiteInfoController {

	@RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model uiModel) {
		SiteInfo siteInfo = new SiteInfo();
		siteInfo.setCreateDate(new Date());
        uiModel.addAttribute("siteInfo", siteInfo);
        filterInfoTypes(uiModel);
        addDateTimeFormatPatterns(uiModel);
        return "admin/manage/siteinfo/create";
    }
	
	private void filterInfoTypes(Model uiModel)
	{
        Map <String, Object> modelMap = uiModel.asMap();
        //get possible existing SiteInfo
		List <SiteInfo> siteInfoes = SiteInfo.findAllSiteInfoes();
		
		if(siteInfoes != null && !siteInfoes.isEmpty())
		{
			//all imageTypes
			List <InfoType> infoTypes = (List)modelMap.get("infotypes");
			Set <InfoType> usedInfoTypes = new HashSet <InfoType>();
			for(SiteInfo siteInfo : siteInfoes){
				usedInfoTypes.add(siteInfo.getInfoType());
			}
			
			List <InfoType> filteredInfoTypes = new ArrayList<InfoType>();
			for(InfoType infoType : infoTypes){
				boolean inUse = false;
				for(InfoType usedInfoType : usedInfoTypes){
					if(infoType.equals(usedInfoType)){
						inUse = true;
						break;
					}					
				}
				if(!inUse){
					filteredInfoTypes.add(infoType);
				}
			}
			modelMap.put("infotypes", filteredInfoTypes);			
		}
		
	}
	

	@RequestMapping(method = RequestMethod.POST)
    public String create(@Valid SiteInfo siteInfo, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("siteInfo", siteInfo);
            filterInfoTypes(uiModel);
            addDateTimeFormatPatterns(uiModel);
            return "admin/manage/siteinfo/create";
        }
        uiModel.asMap().clear();
        siteInfo.persist();
        return "redirect:/admin/manage/siteinfo/" + encodeUrlPathSegment(siteInfo.getId().toString(), httpServletRequest);
    }
}
