package com.art.fine.oneal.web;

import java.util.Date;

import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.art.fine.oneal.domain.SpecialGroup;

@RooWebScaffold(path = "admin/manage/specialgroup", formBackingObject = SpecialGroup.class)
@RequestMapping("/admin/manage/specialgroup")
@Controller
public class SpecialGroupController {

	@RequestMapping(params = "form", method = RequestMethod.GET)
    public String createForm(Model uiModel) {
		SpecialGroup specialGroup = new SpecialGroup();
		specialGroup.setCreateDate(new Date());
        uiModel.addAttribute("specialGroup", specialGroup);
        addDateTimeFormatPatterns(uiModel);
        return "admin/manage/specialgroup/create";
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        SpecialGroup specialGroup = SpecialGroup.findSpecialGroup(id);
        specialGroup.getArtworks().clear();
		specialGroup.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/admin/manage/specialgroup";
    }
}
