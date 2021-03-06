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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CsvTimestampFilter.class })
@ActiveProfiles("dwd-multi")
public class CsvDwdTimestampFilterMultiFileTest extends AbstractCsvTimestampFilterTest {
    
    private Map<MessageHeaders, List<String>> files = new HashMap<>();

    @Before
    public void setUp() {
        initFirstFile();
        initSecondFile();
        initThirdFile();
    }

    @Test
    public void testDwdData() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<Future<Integer>> results = new LinkedList<>();
        for (Entry<MessageHeaders, List<String>> e : files.entrySet()) {
            results.add(executor.submit(new TimestampFilter(e.getKey(), e.getValue(), this)));
        }

        for (Future<Integer> future : results) {
            assertThat(future, notNullValue());
            assertThat(future.get(), is(25));
        }
    }

    public static class TimestampFilter implements Callable<Integer> {
        
        private MessageHeaders header;
        private List<String> values;
        private AbstractCsvTimestampFilterTest test;

        public TimestampFilter(MessageHeaders header, List<String> values, AbstractCsvTimestampFilterTest test) {
            this.header = header;
            this.values = values;
            this.test = test;
        }

        @Override
        public Integer call()
                throws Exception {
            int counter = 0;
            for (String s : values) {
               counter = test.checkMessage(s, test.getCsvtf().process(test.getMessage(s, header))) ? logAndIncrease(s, counter) : counter;
            }
            return counter;
        }

        private int logAndIncrease(String s, int counter) {
//            System.out.println(s);
            return counter + 1;
        }
        
    }

    private void initFirstFile() {
        Map<String, Object> map = new HashMap<>();
        map.put("file_name", "dwd1");
        MessageHeaders headers = new MessageHeaders(map);
        
        List<String> values = new LinkedList<>();
        values.add("START");
        values.add(
                "22.05.18;11:00;38;---;---;4,5;---;---;---;23,4;---;---;---;1500;50;---;---;---;---;---;---;---;100;22;---;---;---;---;---;---;---;---;---;---;---;2;1023,4;29;---;---;---;---;---");
        values.add(
                "22.05.18;10:00;25;---;---;5,8;---;---;---;22,3;---;---;---;2100;35;---;---;---;---;---;---;---;90;18;---;---;---;---;---;---;---;---;---;---;---;2;1023,2;34;---;---;---;---;---");
        values.add(
                "22.05.18;09:00;38;---;---;6,2;---;---;---;20,9;---;---;---;2100;35;---;---;---;---;---;---;---;90;22;---;---;---;---;10;10;---;---;---;---;---;2;1023,1;38;---;---;---;---;---");
        values.add(
                "22.05.18;08:00;50;---;---;6,4;---;---;---;19,4;---;---;---;2100;30;---;---;---;---;---;---;---;90;18;---;---;---;---;---;---;---;---;---;---;---;3;1022,8;43;---;---;---;---;---");
        values.add(
                "22.05.18;07:00;38;---;---;7,1;---;---;---;17,6;---;---;---;5100;30;---;---;---;---;---;---;---;80;18;---;---;---;---;---;---;---;---;---;---;---;2;1022,3;50;---;---;---;---;---");
        values.add(
                "22.05.18;06:00;75;---;---;7,8;---;---;---;16;---;---;---;5100;30;---;---;21,7;---;---;---;---;80;18;---;---;12,4;11;10;10;0;---;---;---;0;3;1021,6;58;---;---;0;---;---");
        values.add(
                "22.05.18;05:00;88;---;---;8,6;---;---;---;14,5;---;---;---;4800;30;---;---;---;---;---;---;---;70;14;---;---;---;---;---;---;---;---;---;---;---;4;1021;68;---;---;---;---;---");
        values.add(
                "22.05.18;04:00;88;---;---;7,4;---;---;---;13,2;---;---;---;2400;30;---;---;---;---;---;---;---;70;14;---;---;---;---;---;---;---;---;---;---;---;4;1020,4;68;---;---;---;---;---");
        values.add(
                "22.05.18;03:00;88;---;---;7,3;---;---;---;12,6;---;---;---;2400;30;---;---;---;---;---;---;---;70;14;---;---;---;---;10;10;---;---;---;---;---;4;1019,8;70;---;---;---;---;---");
        values.add(
                "22.05.18;02:00;88;---;---;7,6;---;---;---;13,9;---;---;---;2400;25;---;---;---;---;---;---;---;80;11;---;---;---;---;---;---;---;---;---;---;---;4;1019,5;66;---;---;---;---;---");
        values.add(
                "22.05.18;01:00;88;---;---;7,4;---;---;---;14;---;---;---;2400;25;---;---;---;---;---;---;---;70;11;---;---;---;---;---;---;---;---;---;---;---;4;1019,2;64;---;---;---;---;---");
        values.add(
                "22.05.18;00:00;75;---;---;6,8;---;---;---;14,7;---;---;---;2400;25;---;---;---;---;---;---;---;80;11;---;---;---;---;10;10;---;---;---;---;---;3;1018,9;59;---;---;---;---;---");
        values.add(
                "21.05.18;23:00;88;---;---;7,1;---;---;---;15,8;---;---;---;2400;25;---;---;---;---;---;---;---;70;14;---;---;---;---;---;---;---;---;---;---;---;4;1018,4;56;---;---;---;---;---");
        values.add(
                "21.05.18;22:00;75;---;---;7,6;---;---;---;15,3;---;---;---;2400;25;---;---;---;---;---;---;---;80;11;---;---;---;---;---;---;---;---;---;---;---;3;1018,1;60;---;---;---;---;---");
        values.add(
                "21.05.18;21:00;63;---;---;8,8;---;---;---;16,4;---;---;---;2100;25;---;---;---;---;---;---;---;80;11;---;---;---;---;10;10;---;---;---;---;---;3;1017,7;61;---;---;---;---;---");
        values.add(
                "21.05.18;20:00;75;---;---;7,5;---;---;---;17,7;---;---;---;2400;30;---;---;---;---;---;---;---;80;14;---;---;---;---;---;---;---;---;---;---;---;3;1017,1;51;---;---;---;---;---");
        values.add(
                "21.05.18;19:00;88;---;---;7,2;---;---;---;20,6;---;---;---;2100;30;---;---;---;---;---;---;---;100;11;---;---;---;---;---;---;---;---;---;---;---;4;1016,2;42;---;---;---;---;---");
        values.add(
                "21.05.18;18:00;75;---;---;7,8;---;---;---;21,7;---;---;---;2100;30;---;---;24,8;---;---;---;---;80;14;---;---;14,1;16;10;10;---;---;---;---;0;3;1015,6;41;---;---;0;---;---");
        values.add(
                "21.05.18;17:00;75;---;---;6,6;---;---;---;23,1;---;---;---;1800;30;---;---;---;---;---;---;---;60;22;---;---;---;---;---;---;---;---;---;---;---;3;1015,2;35;---;---;---;---;---");
        values.add(
                "21.05.18;16:00;88;---;---;5,6;---;---;---;24,1;---;---;---;1800;30;---;---;---;---;---;---;---;60;25;---;---;---;---;---;---;---;---;---;---;---;4;1015;30;---;---;---;---;---");
        values.add(
                "21.05.18;15:00;75;---;---;5,9;---;---;---;24,3;---;---;---;1800;30;---;---;---;---;---;---;---;70;25;---;---;---;---;10;10;---;---;---;---;---;3;1014,7;31;---;---;---;---;---");
        values.add(
                "21.05.18;14:00;88;---;---;5,4;---;---;---;24,2;---;---;---;1800;30;---;---;---;---;---;---;---;80;22;---;---;---;---;---;---;---;---;---;---;---;4;1014,8;30;---;---;---;---;---");
        values.add(
                "21.05.18;13:00;88;---;---;5,6;---;---;---;23,6;---;---;---;1800;35;---;---;---;---;---;---;---;100;18;---;---;---;---;---;---;---;---;---;---;---;4;1015;31;---;---;---;---;---");
        values.add(
                "21.05.18;12:00;88;---;---;5,7;---;---;---;22,9;---;---;---;1800;35;---;---;---;---;---;---;---;90;18;---;---;---;---;10;10;---;---;---;---;---;4;1015,2;33;---;---;---;---;---");
        values.add(
                "21.05.18;11:00;75;---;---;5,6;---;---;---;21,7;---;---;---;1500;35;---;---;---;---;---;---;---;110;18;---;---;---;---;---;---;---;---;---;---;---;3;1015,3;35;---;---;---;---;---");
        values.add("END");
        files.put(headers, values);
    }

    private void initSecondFile() {
        Map<String, Object> map = new HashMap<>();
        map.put("file_name", "dwd2");
        MessageHeaders headers = new MessageHeaders(map);
        
        List<String> values = new LinkedList<>();
        values.add("START");
        values.add(
                "23.05.18;11:00;38;---;---;4,5;---;---;---;23,4;---;---;---;1500;50;---;---;---;---;---;---;---;100;22;---;---;---;---;---;---;---;---;---;---;---;2;1023,4;29;---;---;---;---;---");
        values.add(
                "23.05.18;10:00;25;---;---;5,8;---;---;---;22,3;---;---;---;2100;35;---;---;---;---;---;---;---;90;18;---;---;---;---;---;---;---;---;---;---;---;2;1023,2;34;---;---;---;---;---");
        values.add(
                "23.05.18;09:00;38;---;---;6,2;---;---;---;20,9;---;---;---;2100;35;---;---;---;---;---;---;---;90;22;---;---;---;---;10;10;---;---;---;---;---;2;1023,1;38;---;---;---;---;---");
        values.add(
                "23.05.18;08:00;50;---;---;6,4;---;---;---;19,4;---;---;---;2100;30;---;---;---;---;---;---;---;90;18;---;---;---;---;---;---;---;---;---;---;---;3;1022,8;43;---;---;---;---;---");
        values.add(
                "23.05.18;07:00;38;---;---;7,1;---;---;---;17,6;---;---;---;5100;30;---;---;---;---;---;---;---;80;18;---;---;---;---;---;---;---;---;---;---;---;2;1022,3;50;---;---;---;---;---");
        values.add(
                "23.05.18;06:00;75;---;---;7,8;---;---;---;16;---;---;---;5100;30;---;---;21,7;---;---;---;---;80;18;---;---;12,4;11;10;10;0;---;---;---;0;3;1021,6;58;---;---;0;---;---");
        values.add(
                "23.05.18;05:00;88;---;---;8,6;---;---;---;14,5;---;---;---;4800;30;---;---;---;---;---;---;---;70;14;---;---;---;---;---;---;---;---;---;---;---;4;1021;68;---;---;---;---;---");
        values.add(
                "23.05.18;04:00;88;---;---;7,4;---;---;---;13,2;---;---;---;2400;30;---;---;---;---;---;---;---;70;14;---;---;---;---;---;---;---;---;---;---;---;4;1020,4;68;---;---;---;---;---");
        values.add(
                "23.05.18;03:00;88;---;---;7,3;---;---;---;12,6;---;---;---;2400;30;---;---;---;---;---;---;---;70;14;---;---;---;---;10;10;---;---;---;---;---;4;1019,8;70;---;---;---;---;---");
        values.add(
                "23.05.18;02:00;88;---;---;7,6;---;---;---;13,9;---;---;---;2400;25;---;---;---;---;---;---;---;80;11;---;---;---;---;---;---;---;---;---;---;---;4;1019,5;66;---;---;---;---;---");
        values.add(
                "23.05.18;01:00;88;---;---;7,4;---;---;---;14;---;---;---;2400;25;---;---;---;---;---;---;---;70;11;---;---;---;---;---;---;---;---;---;---;---;4;1019,2;64;---;---;---;---;---");
        values.add(
                "23.05.18;00:00;75;---;---;6,8;---;---;---;14,7;---;---;---;2400;25;---;---;---;---;---;---;---;80;11;---;---;---;---;10;10;---;---;---;---;---;3;1018,9;59;---;---;---;---;---");
        values.add(
                "22.05.18;23:00;88;---;---;7,1;---;---;---;15,8;---;---;---;2400;25;---;---;---;---;---;---;---;70;14;---;---;---;---;---;---;---;---;---;---;---;4;1018,4;56;---;---;---;---;---");
        values.add(
                "22.05.18;22:00;75;---;---;7,6;---;---;---;15,3;---;---;---;2400;25;---;---;---;---;---;---;---;80;11;---;---;---;---;---;---;---;---;---;---;---;3;1018,1;60;---;---;---;---;---");
        values.add(
                "22.05.18;21:00;63;---;---;8,8;---;---;---;16,4;---;---;---;2100;25;---;---;---;---;---;---;---;80;11;---;---;---;---;10;10;---;---;---;---;---;3;1017,7;61;---;---;---;---;---");
        values.add(
                "22.05.18;20:00;75;---;---;7,5;---;---;---;17,7;---;---;---;2400;30;---;---;---;---;---;---;---;80;14;---;---;---;---;---;---;---;---;---;---;---;3;1017,1;51;---;---;---;---;---");
        values.add(
                "22.05.18;19:00;88;---;---;7,2;---;---;---;20,6;---;---;---;2100;30;---;---;---;---;---;---;---;100;11;---;---;---;---;---;---;---;---;---;---;---;4;1016,2;42;---;---;---;---;---");
        values.add(
                "22.05.18;18:00;75;---;---;7,8;---;---;---;21,7;---;---;---;2100;30;---;---;24,8;---;---;---;---;80;14;---;---;14,1;16;10;10;---;---;---;---;0;3;1015,6;41;---;---;0;---;---");
        values.add(
                "22.05.18;17:00;75;---;---;6,6;---;---;---;23,1;---;---;---;1800;30;---;---;---;---;---;---;---;60;22;---;---;---;---;---;---;---;---;---;---;---;3;1015,2;35;---;---;---;---;---");
        values.add(
                "22.05.18;16:00;88;---;---;5,6;---;---;---;24,1;---;---;---;1800;30;---;---;---;---;---;---;---;60;25;---;---;---;---;---;---;---;---;---;---;---;4;1015;30;---;---;---;---;---");
        values.add(
                "22.05.18;15:00;75;---;---;5,9;---;---;---;24,3;---;---;---;1800;30;---;---;---;---;---;---;---;70;25;---;---;---;---;10;10;---;---;---;---;---;3;1014,7;31;---;---;---;---;---");
        values.add(
                "22.05.18;14:00;88;---;---;5,4;---;---;---;24,2;---;---;---;1800;30;---;---;---;---;---;---;---;80;22;---;---;---;---;---;---;---;---;---;---;---;4;1014,8;30;---;---;---;---;---");
        values.add(
                "22.05.18;13:00;88;---;---;5,6;---;---;---;23,6;---;---;---;1800;35;---;---;---;---;---;---;---;100;18;---;---;---;---;---;---;---;---;---;---;---;4;1015;31;---;---;---;---;---");
        values.add(
                "22.05.18;12:00;88;---;---;5,7;---;---;---;22,9;---;---;---;1800;35;---;---;---;---;---;---;---;90;18;---;---;---;---;10;10;---;---;---;---;---;4;1015,2;33;---;---;---;---;---");
        values.add(
                "22.05.18;11:00;75;---;---;5,6;---;---;---;21,7;---;---;---;1500;35;---;---;---;---;---;---;---;110;18;---;---;---;---;---;---;---;---;---;---;---;3;1015,3;35;---;---;---;---;---");
        values.add("END");
        files.put(headers, values);
    }

    private void initThirdFile() {
        Map<String, Object> map = new HashMap<>();
        map.put("file_name", "dwd3");
        MessageHeaders headers = new MessageHeaders(map);
        
        List<String> values = new LinkedList<>();
        values.add("START");
        values.add(
                "24.05.18;11:00;38;---;---;4,5;---;---;---;23,4;---;---;---;1500;50;---;---;---;---;---;---;---;100;22;---;---;---;---;---;---;---;---;---;---;---;2;1023,4;29;---;---;---;---;---");
        values.add(
                "24.05.18;10:00;25;---;---;5,8;---;---;---;22,3;---;---;---;2100;35;---;---;---;---;---;---;---;90;18;---;---;---;---;---;---;---;---;---;---;---;2;1023,2;34;---;---;---;---;---");
        values.add(
                "24.05.18;09:00;38;---;---;6,2;---;---;---;20,9;---;---;---;2100;35;---;---;---;---;---;---;---;90;22;---;---;---;---;10;10;---;---;---;---;---;2;1023,1;38;---;---;---;---;---");
        values.add(
                "24.05.18;08:00;50;---;---;6,4;---;---;---;19,4;---;---;---;2100;30;---;---;---;---;---;---;---;90;18;---;---;---;---;---;---;---;---;---;---;---;3;1022,8;43;---;---;---;---;---");
        values.add(
                "24.05.18;07:00;38;---;---;7,1;---;---;---;17,6;---;---;---;5100;30;---;---;---;---;---;---;---;80;18;---;---;---;---;---;---;---;---;---;---;---;2;1022,3;50;---;---;---;---;---");
        values.add(
                "24.05.18;06:00;75;---;---;7,8;---;---;---;16;---;---;---;5100;30;---;---;21,7;---;---;---;---;80;18;---;---;12,4;11;10;10;0;---;---;---;0;3;1021,6;58;---;---;0;---;---");
        values.add(
                "24.05.18;05:00;88;---;---;8,6;---;---;---;14,5;---;---;---;4800;30;---;---;---;---;---;---;---;70;14;---;---;---;---;---;---;---;---;---;---;---;4;1021;68;---;---;---;---;---");
        values.add(
                "24.05.18;04:00;88;---;---;7,4;---;---;---;13,2;---;---;---;2400;30;---;---;---;---;---;---;---;70;14;---;---;---;---;---;---;---;---;---;---;---;4;1020,4;68;---;---;---;---;---");
        values.add(
                "24.05.18;03:00;88;---;---;7,3;---;---;---;12,6;---;---;---;2400;30;---;---;---;---;---;---;---;70;14;---;---;---;---;10;10;---;---;---;---;---;4;1019,8;70;---;---;---;---;---");
        values.add(
                "24.05.18;02:00;88;---;---;7,6;---;---;---;13,9;---;---;---;2400;25;---;---;---;---;---;---;---;80;11;---;---;---;---;---;---;---;---;---;---;---;4;1019,5;66;---;---;---;---;---");
        values.add(
                "24.05.18;01:00;88;---;---;7,4;---;---;---;14;---;---;---;2400;25;---;---;---;---;---;---;---;70;11;---;---;---;---;---;---;---;---;---;---;---;4;1019,2;64;---;---;---;---;---");
        values.add(
                "24.05.18;00:00;75;---;---;6,8;---;---;---;14,7;---;---;---;2400;25;---;---;---;---;---;---;---;80;11;---;---;---;---;10;10;---;---;---;---;---;3;1018,9;59;---;---;---;---;---");
        values.add(
                "23.05.18;23:00;88;---;---;7,1;---;---;---;15,8;---;---;---;2400;25;---;---;---;---;---;---;---;70;14;---;---;---;---;---;---;---;---;---;---;---;4;1018,4;56;---;---;---;---;---");
        values.add(
                "23.05.18;22:00;75;---;---;7,6;---;---;---;15,3;---;---;---;2400;25;---;---;---;---;---;---;---;80;11;---;---;---;---;---;---;---;---;---;---;---;3;1018,1;60;---;---;---;---;---");
        values.add(
                "23.05.18;21:00;63;---;---;8,8;---;---;---;16,4;---;---;---;2100;25;---;---;---;---;---;---;---;80;11;---;---;---;---;10;10;---;---;---;---;---;3;1017,7;61;---;---;---;---;---");
        values.add(
                "23.05.18;20:00;75;---;---;7,5;---;---;---;17,7;---;---;---;2400;30;---;---;---;---;---;---;---;80;14;---;---;---;---;---;---;---;---;---;---;---;3;1017,1;51;---;---;---;---;---");
        values.add(
                "23.05.18;19:00;88;---;---;7,2;---;---;---;20,6;---;---;---;2100;30;---;---;---;---;---;---;---;100;11;---;---;---;---;---;---;---;---;---;---;---;4;1016,2;42;---;---;---;---;---");
        values.add(
                "23.05.18;18:00;75;---;---;7,8;---;---;---;21,7;---;---;---;2100;30;---;---;24,8;---;---;---;---;80;14;---;---;14,1;16;10;10;---;---;---;---;0;3;1015,6;41;---;---;0;---;---");
        values.add(
                "23.05.18;17:00;75;---;---;6,6;---;---;---;23,1;---;---;---;1800;30;---;---;---;---;---;---;---;60;22;---;---;---;---;---;---;---;---;---;---;---;3;1015,2;35;---;---;---;---;---");
        values.add(
                "23.05.18;16:00;88;---;---;5,6;---;---;---;24,1;---;---;---;1800;30;---;---;---;---;---;---;---;60;25;---;---;---;---;---;---;---;---;---;---;---;4;1015;30;---;---;---;---;---");
        values.add(
                "23.05.18;15:00;75;---;---;5,9;---;---;---;24,3;---;---;---;1800;30;---;---;---;---;---;---;---;70;25;---;---;---;---;10;10;---;---;---;---;---;3;1014,7;31;---;---;---;---;---");
        values.add(
                "23.05.18;14:00;88;---;---;5,4;---;---;---;24,2;---;---;---;1800;30;---;---;---;---;---;---;---;80;22;---;---;---;---;---;---;---;---;---;---;---;4;1014,8;30;---;---;---;---;---");
        values.add(
                "23.05.18;13:00;88;---;---;5,6;---;---;---;23,6;---;---;---;1800;35;---;---;---;---;---;---;---;100;18;---;---;---;---;---;---;---;---;---;---;---;4;1015;31;---;---;---;---;---");
        values.add(
                "23.05.18;12:00;88;---;---;5,7;---;---;---;22,9;---;---;---;1800;35;---;---;---;---;---;---;---;90;18;---;---;---;---;10;10;---;---;---;---;---;4;1015,2;33;---;---;---;---;---");
        values.add(
                "23.05.18;11:00;75;---;---;5,6;---;---;---;21,7;---;---;---;1500;35;---;---;---;---;---;---;---;110;18;---;---;---;---;---;---;---;---;---;---;---;3;1015,3;35;---;---;---;---;---");
        values.add("END");
        files.put(headers, values);
    }

}
