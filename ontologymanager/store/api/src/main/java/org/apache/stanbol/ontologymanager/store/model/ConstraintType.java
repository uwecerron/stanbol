//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.4-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.05.13 at 09:50:16 AM EEST 
//

package org.apache.stanbol.ontologymanager.store.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for ConstraintType.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * <p>
 * 
 * <pre>
 * &lt;simpleType name="ConstraintType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *     &lt;enumeration value="complement_of"/>
 *     &lt;enumeration value="enumeration_of"/>
 *     &lt;enumeration value="intersection_of"/>
 *     &lt;enumeration value="union_of"/>
 *     &lt;enumeration value="all_values_from"/>
 *     &lt;enumeration value="some_values_from"/>
 *     &lt;enumeration value="cardinality"/>
 *     &lt;enumeration value="max_cardinality"/>
 *     &lt;enumeration value="min_cardinality"/>
 *     &lt;enumeration value="has_value"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ConstraintType")
@XmlEnum
public enum ConstraintType {

    @XmlEnumValue("complement_of")
    COMPLEMENT_OF("complement_of"),
    @XmlEnumValue("enumeration_of")
    ENUMERATION_OF("enumeration_of"),
    @XmlEnumValue("intersection_of")
    INTERSECTION_OF("intersection_of"),
    @XmlEnumValue("union_of")
    UNION_OF("union_of"),
    @XmlEnumValue("all_values_from")
    ALL_VALUES_FROM("all_values_from"),
    @XmlEnumValue("some_values_from")
    SOME_VALUES_FROM("some_values_from"),
    @XmlEnumValue("cardinality")
    CARDINALITY("cardinality"),
    @XmlEnumValue("max_cardinality")
    MAX_CARDINALITY("max_cardinality"),
    @XmlEnumValue("min_cardinality")
    MIN_CARDINALITY("min_cardinality"),
    @XmlEnumValue("has_value")
    HAS_VALUE("has_value");
    private final String value;

    ConstraintType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ConstraintType fromValue(String v) {
        for (ConstraintType c : ConstraintType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}