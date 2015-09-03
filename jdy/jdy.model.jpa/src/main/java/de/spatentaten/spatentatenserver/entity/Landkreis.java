/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.spatentaten.spatentatenserver.entity;

/**
 *
 * @author rainer
 */
public enum Landkreis {
    
    NU("Neu-Ulm"),
    GZ("Günzburg"),
    MN("Unterallgäu"),
    A("Augsburg"),
    AIC("Aichach-Friedberg"),
    LI("Lindau"),
    OAL("Ostallgäu"),
    OA("Oberallgäu"),
    DON("Donauwörth");
    
    private String bezeichnung;
    
    private Landkreis(String aBezeichnung) {
        this.bezeichnung = aBezeichnung;
    }
}
