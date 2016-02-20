// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.art.fine.oneal.domain;

import com.art.fine.oneal.domain.SiteInfo;
import java.lang.Integer;
import java.lang.Long;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Version;
import org.springframework.transaction.annotation.Transactional;

privileged aspect SiteInfo_Roo_Entity {
    
    declare @type: SiteInfo: @Entity;
    
    @PersistenceContext
    transient EntityManager SiteInfo.entityManager;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long SiteInfo.id;
    
    @Version
    @Column(name = "version")
    private Integer SiteInfo.version;
    
    public Long SiteInfo.getId() {
        return this.id;
    }
    
    public void SiteInfo.setId(Long id) {
        this.id = id;
    }
    
    public Integer SiteInfo.getVersion() {
        return this.version;
    }
    
    public void SiteInfo.setVersion(Integer version) {
        this.version = version;
    }
    
    @Transactional
    public void SiteInfo.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void SiteInfo.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            SiteInfo attached = SiteInfo.findSiteInfo(this.id);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void SiteInfo.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void SiteInfo.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public SiteInfo SiteInfo.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        SiteInfo merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    public static final EntityManager SiteInfo.entityManager() {
        EntityManager em = new SiteInfo().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long SiteInfo.countSiteInfoes() {
        return entityManager().createQuery("SELECT COUNT(o) FROM SiteInfo o", Long.class).getSingleResult();
    }
    
    public static List<SiteInfo> SiteInfo.findAllSiteInfoes() {
        return entityManager().createQuery("SELECT o FROM SiteInfo o", SiteInfo.class).getResultList();
    }
    
    public static SiteInfo SiteInfo.findSiteInfo(Long id) {
        if (id == null) return null;
        return entityManager().find(SiteInfo.class, id);
    }
    
    public static List<SiteInfo> SiteInfo.findSiteInfoEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM SiteInfo o", SiteInfo.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}