/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.spatentaten.spatentatenserver.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author rainer
 */
@Entity
@Table(name = "USERS")
@XmlRootElement
public class SpatentatenUser implements Serializable {
    
    private static final long serialVersionUID = 1L;
    @Id
    @Column(unique=true, nullable=false, length=128)
    private String email;
    
    private Anrede anrede;

    @Size(max=50)
    @Column(length=50)
    private String vorname;

    @Size(max=50)
    private String nachname;

    
    private String registrationId;
    @Column(length=128) //sha-512 + hex
    private String password;
    private String salt;
    private String passwordResetId;
    private boolean registrationConfirmed;
            
    @ElementCollection(targetClass = Group.class)
    @CollectionTable(name = "USERS_GROUPS",
                    joinColumns       = @JoinColumn(name = "email", nullable=false),
                    uniqueConstraints = { @UniqueConstraint(columnNames={"email","groupname"}) } )
    @Enumerated(EnumType.STRING)
    @Column(name="groupname", length=64, nullable=false)
    private List<Group> groups;

    @ElementCollection(targetClass = Landkreis.class)
    @CollectionTable(name = "TEILNEHMER_LANDKREIS",
                    joinColumns       = @JoinColumn(name = "email", nullable=false),
                    uniqueConstraints = { @UniqueConstraint(columnNames={"email","landkreis"}) } )
    @Enumerated(EnumType.STRING)
    @Column(name="landkreis", length=64, nullable=false)
    private List<Landkreis> landkreise;

    
    public String getEmail() {
        return email;
    }

    public void setEmail(String eMail) {
        this.email = eMail;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public boolean isRegistrationConfirmed() {
        return registrationConfirmed;
    }

    public void setRegistrationConfirmed(boolean registrationConfirmed) {
        this.registrationConfirmed = registrationConfirmed;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getPasswordResetId() {
        return passwordResetId;
    }

    public void setPasswordResetId(String passwordResetId) {
        this.passwordResetId = passwordResetId;
    }

    @XmlElement(name="groups")
    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
    
    public void setGroups(Group... groups) {
        this.groups = Arrays.asList(groups);
    }

    @XmlElement(name="landkreise")
    public List<Landkreis> getLandkreise() {
        return landkreise;
    }

    public void setLandkreise(List<Landkreis> landkreise) {
        this.landkreise = landkreise;
    }

    public void setLandkreise(Landkreis... allLandkreise) {
        this.landkreise = Arrays.asList(allLandkreise);
    }

    public Anrede getAnrede() {
        return anrede;
    }

    public void setAnrede(Anrede anrede) {
        this.anrede = anrede;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (email != null ? email.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SpatentatenUser)) {
            return false;
        }
        SpatentatenUser other = (SpatentatenUser) object;
        if ((this.email == null && other.email != null) || (this.email != null && !this.email.equals(other.email))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "de.spatentaten.spatentatenserver.entity.SpatentatenUser[ id=" + email + " ]";
    }
    
}
