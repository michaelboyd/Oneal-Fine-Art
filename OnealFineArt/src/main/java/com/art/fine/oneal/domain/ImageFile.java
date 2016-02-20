package com.art.fine.oneal.domain;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import com.art.fine.oneal.utils.ImageSize;
import com.art.fine.oneal.domain.Image;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

@RooJavaBean
@RooToString
@RooEntity(finders = { "findImageFilesByImageAndImageSize", "findImageFilesByImage", "findImageFilesByImageSize" })
public class ImageFile {

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] file;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private Date createDate;

    @NotNull
    @Enumerated
    private ImageSize imageSize;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "image_id")
    private Image image;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("Artwork: ").append(getImage().getArtwork().getTitle());
        return sb.toString();
    }
}
