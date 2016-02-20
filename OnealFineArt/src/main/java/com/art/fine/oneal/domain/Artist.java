package com.art.fine.oneal.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
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
public class Artist {

    @NotNull
    private String firstName;

    private String middleName;

    @NotNull
    private String lastName;
    
    @Transient
    private String name;

    @NotNull
    @Size(max = 5000)
    private String biography;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "MM-dd-yyyy")
    private Date createDate;
    
    @NotNull
    @Value("0")
    private boolean hasOtherAvailable;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Artwork> artworks = new HashSet<Artwork>();

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getFirstName()).append(" ").append(getLastName());
        return sb.toString();
    }
    
    @Transient
    public int artCount;
    

	public static List<Artist> findArtistEntries(int firstResult, int maxResults) {
    	List <Artist> artists = entityManager().createQuery("SELECT o FROM Artist o ORDER BY o.firstName, o.lastName", Artist.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList(); 
    	setArtCountAndName(artists);
    	return artists; 
    }

	public static List<Artist> findAllArtists() {
		List <Artist> artists = entityManager().createQuery("SELECT o FROM Artist o ORDER BY o.firstName, o.lastName", Artist.class).getResultList();
		setArtCountAndName(artists);
        return artists;
    }
	
	public static List<Artist> findAllArtistsWithArt() {
		List <Artist> artists = findAllArtists();
		List <Artist> artistsWithArt = new ArrayList<Artist>();
		for(Artist artist : artists){
			if(artist.getArtworks() != null && !artist.getArtworks().isEmpty()){
				artistsWithArt.add(artist);
			}
		}
		setArtCountAndName(artistsWithArt);
        return artistsWithArt;
    }
	
	
	private static void setArtCountAndName(List <Artist> artists)
	{
    	for(Artist artist : artists){
    		artist.setArtCount(artist.getArtworks().size());
    		StringBuffer name = new StringBuffer();
    		name.append(artist.getFirstName());
    		if(artist.getMiddleName() != null && !artist.getMiddleName().trim().equals("")){
    			name.append(" " + artist.getMiddleName());
    		}    		
    		name.append(" " + artist.getLastName());
    		artist.setName(name.toString());
    	}
	}
	
	public void setName(String name) {
        this.name = name;
    }

	public String getName() {
		StringBuffer name = new StringBuffer();
		name.append(getFirstName());
		if(getMiddleName() != null && !getMiddleName().trim().equals("")){
			name.append(" " + getMiddleName());
		}    		
		name.append(" " + getLastName());
		return name.toString();
    }
}
