package com.art.fine.oneal.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import com.art.fine.oneal.utils.ImageType;

@RooJavaBean
@RooToString
@RooEntity(finders = { "findImagesByArtwork" })
public class Image {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "artwork_id")
    private Artwork artwork;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] file;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private Date createDate;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<ImageFile> imageFiles = new HashSet<ImageFile>();

    @NotNull
    @Enumerated(EnumType.STRING)
    private ImageType imageType;

    @Transient
    public Long thumbImageId;

    @Transient
    public Long bigImageId;

    @Transient
    public Long fullSizeImageId;

    public Long getThumbImageId() {
        return thumbImageId;
    }
}
