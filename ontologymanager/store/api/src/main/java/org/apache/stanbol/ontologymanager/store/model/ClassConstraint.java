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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
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
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element ref="{model.rest.persistence.iks.srdc.com.tr}ClassConstraint"/>
 *           &lt;element ref="{model.rest.persistence.iks.srdc.com.tr}ClassMetaInformation"/>
 *           &lt;element ref="{model.rest.persistence.iks.srdc.com.tr}PropertyMetaInformation"/>
 *           &lt;element name="Literal" type="{model.rest.persistence.iks.srdc.com.tr}non_empty_string"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="type" use="required" type="{model.rest.persistence.iks.srdc.com.tr}ConstraintType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"classConstraintOrClassMetaInformationOrPropertyMetaInformation"})
@XmlRootElement(name = "ClassConstraint")
public class ClassConstraint {

    @XmlElements({@XmlElement(name = "ClassConstraint", type = ClassConstraint.class),
                  @XmlElement(name = "ClassMetaInformation", type = ClassMetaInformation.class),
                  @XmlElement(name = "Literal", type = String.class),
                  @XmlElement(name = "PropertyMetaInformation", type = PropertyMetaInformation.class)})
    protected List<Object> classConstraintOrClassMetaInformationOrPropertyMetaInformation;
    @XmlAttribute(namespace = "model.rest.persistence.iks.srdc.com.tr", required = true)
    protected ConstraintType type;

    /**
     * Gets the value of the classConstraintOrClassMetaInformationOrPropertyMetaInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification
     * you make to the returned list will be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the classConstraintOrClassMetaInformationOrPropertyMetaInformation
     * property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getClassConstraintOrClassMetaInformationOrPropertyMetaInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link ClassConstraint }
     * {@link ClassMetaInformation } {@link String } {@link PropertyMetaInformation }
     * 
     * 
     */
    public List<Object> getClassConstraintOrClassMetaInformationOrPropertyMetaInformation() {
        if (classConstraintOrClassMetaInformationOrPropertyMetaInformation == null) {
            classConstraintOrClassMetaInformationOrPropertyMetaInformation = new ArrayList<Object>();
        }
        return this.classConstraintOrClassMetaInformationOrPropertyMetaInformation;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return possible object is {@link ConstraintType }
     * 
     */
    public ConstraintType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *            allowed object is {@link ConstraintType }
     * 
     */
    public void setType(ConstraintType value) {
        this.type = value;
    }

}