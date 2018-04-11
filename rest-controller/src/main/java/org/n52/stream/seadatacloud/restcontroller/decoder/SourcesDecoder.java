/*
 * Copyright (C) 2018-2018 52Â°North Initiative for Geospatial Open Source
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.n52.stream.seadatacloud.restcontroller.decoder;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import static org.n52.stream.seadatacloud.restcontroller.service.CloudService.BASE_URL;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.n52.stream.seadatacloud.restcontroller.model.AppOptions;
import org.n52.stream.seadatacloud.restcontroller.model.Source;
import org.n52.stream.seadatacloud.restcontroller.model.Sources;

/**
 *
 * @author Maurin Radtke <m.radtke@52north.org>
 */
public class SourcesDecoder extends BaseDeserializer<Sources> {

    ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public Sources deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
        Sources results = new Sources();

        JsonNode node = jp.readValueAsTree();
        JsonNode embedded = node.get("_embedded");
        ArrayNode appRegistrationResourceList = (ArrayNode) embedded.get("appRegistrationResourceList");

        List<Source> sourceList = new ArrayList();
        appRegistrationResourceList.forEach((source) -> {
            Source current = new Source();
            current.setName(source.get("name").asText());

            try {
                URL url = new URL(BASE_URL + "/apps/" + "source" + "/" + current.getName());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                StringBuffer res = new StringBuffer();
                while ((line = in.readLine()) != null) {
                    res.append(line);
                    res.append("\n");
                }
                in.close();
                conn.disconnect();
                String response = res.toString();
                
                AppOptions aos = objectMapper.readValue(response, AppOptions.class);

                current.setOptions(aos.getAppOptions());
            } catch (Exception e) {
                e.printStackTrace();
            }

            sourceList.add(current);
        });
        results.setSources(sourceList);
        return results;
    }

}