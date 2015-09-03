/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.spatentaten.spatentatenserver.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rainer
 */
@Entity
@XmlRootElement
public class Teilnehmer implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 60)
    @Column(length=60,nullable = false)
    private String name;
    private String strasse;
    private String ort;
    @Column(length=512)
    private String beschreibung;
    private String url;
    @Column(precision = 17, scale = 14)
    private BigDecimal lat;
    @Column(precision = 17, scale = 14)
    private BigDecimal lng;
    private Integer plz;

    private Landkreis landkreis;
    
    boolean freigegeben;
    
    @OneToOne
    private Veranstaltung veranstaltung;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLng() {
        return lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }

    public Integer getPlz() {
        return plz;
    }

    public void setPlz(Integer plz) {
        this.plz = plz;
    }

    public Landkreis getLandkreis() {
        return landkreis;
    }

    public void setLandkreis(Landkreis landkreis) {
        this.landkreis = landkreis;
    }

    public boolean isFreigegeben()
    {
        return freigegeben;
    }

    public void setFreigegeben(boolean freigegeben)
    {
        this.freigegeben = freigegeben;
    }

    public Veranstaltung getVeranstaltung()
    {
        return veranstaltung;
    }

    public void setVeranstaltung(Veranstaltung veranstaltung)
    {
        this.veranstaltung = veranstaltung;
    }

    
    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Teilnehmer)) {
            return false;
        }
        Teilnehmer other = (Teilnehmer) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "de.spatentaten.jettytest.Teilnehmer[ id=" + id + " ]";
    }
    
}
