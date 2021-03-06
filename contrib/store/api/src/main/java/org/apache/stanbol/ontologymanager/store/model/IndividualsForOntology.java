/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
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
 *         &lt;element ref="{model.rest.persistence.iks.srdc.com.tr}OntologyMetaInformation"/>
 *         &lt;element ref="{model.rest.persistence.iks.srdc.com.tr}IndividualMetaInformation" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"ontologyMetaInformation", "individualMetaInformation"})
@XmlRootElement(name = "IndividualsForOntology")
public class IndividualsForOntology {

    @XmlElement(name = "OntologyMetaInformation", required = true)
    protected OntologyMetaInformation ontologyMetaInformation;
    @XmlElement(name = "IndividualMetaInformation")
    protected List<IndividualMetaInformation> individualMetaInformation;

    /**
     * Gets the value of the ontologyMetaInformation property.
     * 
     * @return possible object is {@link OntologyMetaInformation }
     * 
     */
    public OntologyMetaInformation getOntologyMetaInformation() {
        return ontologyMetaInformation;
    }

    /**
     * Sets the value of the ontologyMetaInformation property.
     * 
     * @param value
     *            allowed object is {@link OntologyMetaInformation }
     * 
     */
    public void setOntologyMetaInformation(OntologyMetaInformation value) {
        this.ontologyMetaInformation = value;
    }

    /**
     * Gets the value of the individualMetaInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification
     * you make to the returned list will be present inside the JAXB object. This is why there is not a
     * <CODE>set</CODE> method for the individualMetaInformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getIndividualMetaInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link IndividualMetaInformation }
     * 
     * 
     */
    public List<IndividualMetaInformation> getIndividualMetaInformation() {
        if (individualMetaInformation == null) {
            individualMetaInformation = new ArrayList<IndividualMetaInformation>();
        }
        return this.individualMetaInformation;
    }

}
