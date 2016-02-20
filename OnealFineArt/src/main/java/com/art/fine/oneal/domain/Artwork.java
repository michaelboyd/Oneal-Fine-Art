package com.art.fine.oneal.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity
public class Artwork {

    @NotNull
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "artist_id")
    private Artist artist;
	
    @NotNull
    private String title;
    
    @Transient
    public String size;    

    @NotNull
    @Size(max = 5000)
    private String description;

    @NotNull
    private int height;
    
    @NotNull
    private int width;

    @NotNull
    private double price;

    @NotNull
    @Value("0")
    private Boolean negotiable;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private Date createDate;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Image> images = new HashSet<Image>();

    @ManyToMany(cascade = CascadeType.MERGE, mappedBy = "artworks")
    private Set<SpecialGroup> specialGroups = new HashSet<SpecialGroup>();

    @Transient
    public Long thumbImageId;

    @Transient
    public Long bigImageId;
    
    @Transient
    public Long fullSizeImageId;    

    @NotNull
    @Value("1")
    private boolean framed;

    public String toString() 
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getTitle());
        return sb.toString();
    }

	public String getSize() {
		//format as hh.hh" x www.ww"
		StringBuffer size = new StringBuffer();
		size.append(getHeight());
		size.append("\"");
		size.append(" x ");
		size.append(getWidth());
		size.append("\"");
		return size.toString();
    }
}
