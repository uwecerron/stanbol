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
package org.apache.stanbol.commons.jsonld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Fabian Christ
 * 
 */
public class JsonLdResource {

    private String subject;
    private String profile;
    private List<String> types = new ArrayList<String>();
    
    // maps properties to types
    private Map<String,String> coerceMap = new HashMap<String,String>();
    
    // maps properties to values
    private Map<String,Object> propertyMap = new HashMap<String,Object>();

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void addType(String type) {
        types.add(type);
    }

    public void addAllTypes(List<String> types) {
        this.types.addAll(types);
    }

    public void putPropertyType(String property, String type) {
        this.coerceMap.put(property, type);
    }

    public String getTypeOfProperty(String property) {
        return this.coerceMap.get(property);
    }

    public Map<String,String> getCoerceMap() {
        return this.coerceMap;
    }

    public List<String> getTypes() {
        return types;
    }

    public void putAllProperties(Map<String,Object> propertyMap) {
        this.propertyMap.putAll(propertyMap);
    }

    public void putProperty(String property, Object value) {
        propertyMap.put(property, value);
    }

    public Object getPropertyValueIgnoreCase(String property) {
        for (String p : this.propertyMap.keySet()) {
            if (p.equalsIgnoreCase(property)) {
                return this.propertyMap.get(p);
            }
        }
        return null;
    }
    
    public Set<String> getProperties() {
        return this.propertyMap.keySet();
    }
   
    public Map<String,Object> getPropertyMap() {
        return this.propertyMap;
    }

    public boolean hasPropertyIgnorecase(String property) {
        for (String p : this.propertyMap.keySet()) {
            if (p.equalsIgnoreCase(property)) {
                return true;
            }
        }

        return false;
    }
}