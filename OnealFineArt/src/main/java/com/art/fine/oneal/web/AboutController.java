package com.art.fine.oneal.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.art.fine.oneal.domain.Artist;
import com.art.fine.oneal.domain.SiteInfo;
import com.art.fine.oneal.utils.InfoType;

@RequestMapping("/about/**")
@Controller
public class AboutController {

    @RequestMapping
    public void get(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
    }

    @RequestMapping(method = RequestMethod.POST, value = "{id}")
    public void post(@PathVariable Long id, ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
    }

    @RequestMapping
    public String index(Model uiModel) {
    	
    	List <SiteInfo> info = null;
    	
    	info = SiteInfo.findSiteInfoesByInfoType(InfoType.History).getResultList();
    	if(info != null && !info.isEmpty())
    		uiModel.addAttribute(InfoType.History.toString(), ((SiteInfo)info.get(0)).getInfo());    	
    	
    	info = SiteInfo.findSiteInfoesByInfoType(InfoType.MakeOffer).getResultList();
    	if(info != null && !info.isEmpty())
    		uiModel.addAttribute(InfoType.MakeOffer.toString(), ((SiteInfo)info.get(0)).getInfo());
    	
    	info = SiteInfo.findSiteInfoesByInfoType(InfoType.SpecialGroup).getResultList();
    	if(info != null && !info.isEmpty())
    		uiModel.addAttribute(InfoType.SpecialGroup.toString(), ((SiteInfo)info.get(0)).getInfo());
    	
    	info = SiteInfo.findSiteInfoesByInfoType(InfoType.Negotiable).getResultList();
    	if(info != null && !info.isEmpty())
    		uiModel.addAttribute(InfoType.Negotiable.toString(), ((SiteInfo)info.get(0)).getInfo());
    	
    	info = SiteInfo.findSiteInfoesByInfoType(InfoType.ContactName).getResultList();
    	if(info != null && !info.isEmpty())
    		uiModel.addAttribute(InfoType.ContactName.toString(), ((SiteInfo)info.get(0)).getInfo());

    	info = SiteInfo.findSiteInfoesByInfoType(InfoType.ContactEmail).getResultList();
    	if(info != null && !info.isEmpty())
    		uiModel.addAttribute(InfoType.ContactEmail.toString(), ((SiteInfo)info.get(0)).getInfo());
    	
    	List <Artist> artists = Artist.findAllArtistsWithArt();
    	uiModel.addAttribute("artists", artists); 	
    	
    	return "about/index";
    }
}
