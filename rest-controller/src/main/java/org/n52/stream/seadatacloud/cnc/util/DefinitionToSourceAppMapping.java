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

import java.util.HashMap;

import org.springframework.stereotype.Component;

/**
 * Maintains mappings between SensorML .. <code>interfaceParameters</code> &rarr; <code>DataRecord::definitions</code>
 * and Spring Cloud Data Flow Source apps.
 *
 * @author Maurin Radtke <m.radtke@52north.org>
 * @author <a href="mailto:e.h.juerrens@52north.org">J&uuml;rrens, Eike Hinderk</a>
 */
@Component
public class DefinitionToSourceAppMapping {

    private HashMap<String, String> mappings = new HashMap<>();

    public boolean hasSourceAppForDefinition(String definition) {
        return mappings.containsKey(definition);
    }

    public String getSourceAppName(String definition) {
        return mappings.get(definition);
    }

    public void addMapping(String definition, String appName) {
        mappings.put(definition, appName);
    }

}
