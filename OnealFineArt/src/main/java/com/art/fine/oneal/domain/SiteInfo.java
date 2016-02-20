package com.art.fine.oneal.domain;

import java.util.Date;

import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import com.art.fine.oneal.utils.InfoType;

@RooJavaBean
@RooToString
@RooEntity(finders = { "findSiteInfoesByInfoType" })
public class SiteInfo {

    @NotNull
    @Enumerated
    private InfoType infoType;

    @Size(max = 10000)
    private String info;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private Date createDate;
}
