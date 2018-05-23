/*
 * Copyright (C) 2018-2018 52°North Initiative for Geospatial Open Source
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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * @author <a href="mailto:e.h.juerrens@52north.org">J&uuml;rrens, Eike Hinderk</a>
 *
 */
@Validated
@ConfigurationProperties("org.n52.stream")
public class AppConfiguration {

    /**
     * Number of header lines to skip.
     */
    private int numberOfHeaderLines = 0;

    /**
     * Number of footer lines to skip
     */
    private int numberOfFooterLines = 0;

    /**
     * Comment Line start char
     */
    private String commentLineStartChar = "#";

    public int getNumberOfHeaderLines() {
        return numberOfHeaderLines;
    }

    public void setNumberOfHeaderLines(int numberOfHeaderLines) {
        this.numberOfHeaderLines = numberOfHeaderLines;
    }

    public int getNumberOfFooterLines() {
        return numberOfFooterLines;
    }

    public void setNumberOfFooterLines(int numberOfFooterLines) {
        this.numberOfFooterLines = numberOfFooterLines;
    }

    public String getCommentLineStartChar() {
        return commentLineStartChar;
    }

    public void setCommentLineStartChar(String commentLineStartChar) {
        this.commentLineStartChar = commentLineStartChar;
    }


}
