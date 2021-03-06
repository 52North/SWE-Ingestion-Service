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
package org.n52.stream.seadatacloud.processors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CsvTimestampFilter.class })
@ActiveProfiles("dwd-global-utc")
public class CsvDwdTimestampFilterWithGlobalTimestampUtcTest extends AbstractDwdCsvTimestampFilterTest{
    
    private int ignoredData = 13;
    
    @Test
    public void testDwdData() {
        int counter = 0;
        for (String s : getValues()) {
           counter = checkMessage(s, getCsvtf().process(getMessage(s, getHeaders()))) ? counter + 1 : counter;
        }
        assertThat(counter, is(getValues().size()-2-ignoredData));
    }
    
    @Override
    protected boolean checkMessage(String s, Message<String> message) {
        if (message != null || s.equalsIgnoreCase("START") || s.equalsIgnoreCase("END")) {
            return super.checkMessage(s, message);
        }
        return false;
    }

}
