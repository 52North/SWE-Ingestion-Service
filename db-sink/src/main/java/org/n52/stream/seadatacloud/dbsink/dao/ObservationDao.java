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
package org.n52.stream.seadatacloud.dbsink.dao;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.n52.series.db.beans.BooleanDataEntity;
import org.n52.series.db.beans.CategoryDataEntity;
import org.n52.series.db.beans.CountDataEntity;
import org.n52.series.db.beans.DatasetEntity;
import org.n52.series.db.beans.GeometryEntity;
import org.n52.series.db.beans.QuantityDataEntity;
import org.n52.series.db.beans.TextDataEntity;
import org.n52.series.db.beans.data.Data;
import org.n52.series.db.beans.data.Data.BooleanData;
import org.n52.series.db.beans.data.Data.CategoryData;
import org.n52.series.db.beans.data.Data.CountData;
import org.n52.series.db.beans.data.Data.QuantityData;
import org.n52.series.db.beans.data.Data.TextData;
import org.n52.shetland.ogc.sensorML.elements.SmlIo;
import org.n52.stream.core.Measurement;

/**
 * DAO implementation for {@link Data} persisting
 *
 * @author <a href="mailto:c.hollmann@52north.org">Carsten Hollmann</a>
 * @since 1.0.0
 *
 */
public class ObservationDao
        extends
        AbstractDao {

    /**
     * constructor
     *
     * @param daoFactory the {@link DaoFactory}
     */
    public ObservationDao(DaoFactory daoFactory) {
        super(daoFactory);
    }

    /**
     * Persist the {@link Measurement} into the database
     *
     * @param m the {@link Measurement} to persist
     * @param dataset the related {@link DatasetEntity}
     * @param outputs the outputs
     * @return the persisted {@link Data}
     */
    public Data<?> persist(Measurement<?> m, DatasetEntity dataset, List<SmlIo> outputs) {
        if (m.getValue() instanceof BigDecimal) {
            return persistQuantity(m.getValue(), m, dataset);
        } else if (m.getValue() instanceof String) {
            if (dataset.hasUnit()) {
                return persistCategory(m.getValue(), m, dataset);
            }
            return persistText(m.getValue(), m, dataset);
        } else if (m.getValue() instanceof Integer) {
            if (dataset.hasUnit() && checkForQuantity(dataset.getPhenomenon().getIdentifier(), outputs)) {
                return persistQuantity(m.getValue(), m, dataset);
            }
            return persistCount(m.getValue(), m, dataset);
        } else if (m.getValue() instanceof Boolean) {
            return persistBoolean(m.getValue(), m, dataset);
        }
        // LOG.info("Not supported value type");
        return null;
    }

    /**
     * Persist a {@link QuantityData} observation
     *
     * @param value the observed value
     * @param m the related {@link Measurement}
     * @param dataset the related {@link DatasetEntity}
     * @return persisted {@link Data}
     */
    protected Data<?> persistQuantity(Object value, Measurement<?> m, DatasetEntity dataset) {
        QuantityData data = new QuantityDataEntity();
        data.setValue(new BigDecimal(value.toString()));
        return persist(data, m, dataset);
    }

    /**
     * Persist a {@link TextData} observation
     *
     * @param value the observed value
     * @param m the related {@link Measurement}
     * @param dataset the related {@link DatasetEntity}
     * @return persisted {@link Data}
     */
    protected Data<?> persistText(Object value, Measurement<?> m, DatasetEntity dataset) {
        TextData data = new TextDataEntity();
        data.setValue(value.toString());
        return persist(data, m, dataset);
    }

    /**
     * Persist a {@link CategoryData} observation
     *
     * @param value the observed value
     * @param m the related {@link Measurement}
     * @param dataset the related {@link DatasetEntity}
     * @return persisted {@link Data}
     */
    protected Data<?> persistCategory(Object value, Measurement<?> m, DatasetEntity dataset) {
        CategoryData data = new CategoryDataEntity();
        data.setValue(value.toString());
        return persist(data, m, dataset);
    }

    /**
     * Persist a {@link CountData} observation
     *
     * @param value the observed value
     * @param m the related {@link Measurement}
     * @param dataset the related {@link DatasetEntity}
     * @return persisted {@link Data}
     */
    protected Data<?> persistCount(Object value, Measurement<?> m, DatasetEntity dataset) {
        CountData data = new CountDataEntity();
        data.setValue(Integer.parseInt(value.toString()));
        return persist(data, m, dataset);
    }

    /**
     * Persist a {@link BooleanData} observation
     *
     * @param value the observed value
     * @param m the related {@link Measurement}
     * @param dataset the related {@link DatasetEntity}
     * @return persisted {@link Data}
     */
    protected Data<?> persistBoolean(Object value, Measurement<?> m, DatasetEntity dataset) {
        BooleanData data = new BooleanDataEntity();
        data.setValue(Boolean.valueOf(value.toString()));
        return persist(data, m, dataset);
    }

    /**
     * Persist a {@link Data} observation
     *
     * @param value the observed value
     * @param m the related {@link Measurement}
     * @param dataset the related {@link DatasetEntity}
     * @return persisted {@link Data}
     */
    private Data<?> persist(Data<?> data, Measurement<?> m, DatasetEntity dataset) {
        data.setDataset(dataset);
        java.util.Date samplingDate = Date.from(m.getPhenomenonTime().toInstant());
        data.setSamplingTimeStart(samplingDate);
        data.setSamplingTimeEnd(samplingDate);
        if (m.hasResultTime()) {
            data.setResultTime(Date.from(m.getResultTime().toInstant()));
        } else {
            data.setResultTime(samplingDate);
        }
        if (m.hasResultGeometry()) {
            GeometryFactory gf = new GeometryFactory();
            GeometryEntity ge = new GeometryEntity();
            Coordinate cd = null;
            if (m.getResultGeometry().hasHeight()) {
                cd = new Coordinate(
                        m.getResultGeometry().getLongitude(),
                        m.getResultGeometry().getLatitude(),
                        m.getResultGeometry().getHeight()
                );
                ge.setSrid(4979);
            } else {
                cd = new Coordinate(
                        m.getResultGeometry().getLongitude(),
                        m.getResultGeometry().getLatitude()
                );
                ge.setSrid(4326);
            }
            Point geom = gf.createPoint(cd);
            ge.setGeometry(geom);
            ge.setGeometryFactory(gf);
            data.setGeometryEntity(ge);
        }
        getSession().saveOrUpdate(data);
        getSession().flush();
        getSession().refresh(data);
        return data;
    }
}
