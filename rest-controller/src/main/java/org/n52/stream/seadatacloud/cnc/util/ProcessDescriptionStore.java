/*
 * Copyright (C) 2018-2019 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.stream.seadatacloud.cnc.util;

import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import org.springframework.stereotype.Component;

/**
 * @author Maurin Radtke <m.radtke@52north.org>
 */
@Component
public class ProcessDescriptionStore implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private HashMap<String, SimpleEntry<String, String>> streamIdProcessDescriptionMap;

    public ProcessDescriptionStore() {
        streamIdProcessDescriptionMap = new HashMap<>();
    }

    public boolean hasProcessDescriptionForStream(String streamName) {
        return streamIdProcessDescriptionMap.containsKey(streamName);
    }

    public String getProcessDescriptionForStream(String streamName) {
        return streamIdProcessDescriptionMap.get(streamName).getKey();
    }

    public void addProcessDescription(String streamName, String processDescription, String streamDefinition) {
        streamIdProcessDescriptionMap.put(streamName, new SimpleEntry<String,String>(processDescription, streamDefinition));
    }
    
    public HashMap<String,SimpleEntry<String,String>> getDescriptions() {
        return this.streamIdProcessDescriptionMap;
    }
    
    public void deleteProcessDescription(String streamName){
        SimpleEntry<String,String> processDescription = streamIdProcessDescriptionMap.get(streamName);
        if (processDescription != null) {
            streamIdProcessDescriptionMap.remove(streamName);
        }
    }

}