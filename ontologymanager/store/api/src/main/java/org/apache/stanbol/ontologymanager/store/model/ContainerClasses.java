//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.4-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.05.13 at 09:50:16 AM EEST 
//

package org.apache.stanbol.ontologymanager.store.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{model.rest.persistence.iks.srdc.com.tr}ClassMetaInformation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"classMetaInformation"})
@XmlRootElement(name = "ContainerClasses")
public class ContainerClasses {

    @XmlElement(name = "ClassMetaInformation")
    protected List<ClassMetaInformation> classMetaInformation;

    /**
     * Gets the value of the classMetaInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification
     * you make to the returned list will be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the classMetaInformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getClassMetaInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link ClassMetaInformation }
     * 
     * 
     */
    public List<ClassMetaInformation> getClassMetaInformation() {
        if (classMetaInformation == null) {
            classMetaInformation = new ArrayList<ClassMetaInformation>();
        }
        return this.classMetaInformation;
    }

}