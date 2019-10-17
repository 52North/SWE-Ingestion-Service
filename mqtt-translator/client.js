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
const mqtt = require("mqtt");
// next is only required when NOT running in docker as docker adds timestamps to logs by default
// require('log-timestamp');
const mqttUrl = process.env.MQTT_URL || "mqtt://mosca";
const mqttTopic = process.env.MQTT_TOPYIC || "airmar-rinville-1";
const stationName = process.env.STATION_NAME || "AIRMAR-RINVILLE-1";
const frequency = process.env.FREQUENCY || 5000;
const rampUpTime = process.env.RAMP_UP_TIME || 1000;

const values = initValues({
  temperature: {
    min: -10,
    max: 30,
    maxChange: 0.001,
    trend: 1,
    trendChangeProbability: 0.0
  },
  humidity: {
    min: 0,
    max: 100,
    maxChange: 0.01,
    trendChangeProbability: 0.1
  },
  dewPoint: {
    min: 10,
    max: 30,
    maxChange: 0.001,
    trend: 1,
    trendChangeProbability: 0.0
  },
  windDirection: {
    min: 0,
    max: 359,
    maxChange: 0.05,
    trendChangeProbability: 0.1
  },
  windSpeed: {
    min: 0,
    max: 35,
    maxChange: 0.01,
    trendChangeProbability: 0.1
  }
});

setTimeout(() => {
  const client = mqtt.connect(mqttUrl);
  client.on("connect", () => {
    console.log("Message generation activated.");
    sendMessage(client);
  });
}, rampUpTime);

function sendMessage(client) {
  var msg = createMessage(updateValues(values));

  client.publish(mqttTopic, msg, null, () => {
    console.log(`message published in ${mqttTopic}: ${msg}`);
  });

  setTimeout(() => sendMessage(client), frequency);
}

function updateValues(values) {
  for (const [key, v] of Object.entries(values)) {
    const prev = v.value;
    let min, max;

    if (v.trendChangeProbability !== null || v.trendChangeProbalitiy !== undefined) {
      if (
        prev === v.min ||
        prev === v.max ||
        Math.random() < v.trendChangeProbability
      ) {
        v.trend *= -1;
      }
      if (v.trend > 0) {
        min = prev;
        max = Math.min(v.max, prev + (v.max - v.min) * v.maxChange);
      } else {
        min = Math.max(v.min, prev - (v.max - v.min) * v.maxChange);
        max = prev;
      }
    } else {
      min = Math.max(v.min, prev - (v.max - v.min) * v.maxChange);
      max = Math.min(v.max, prev + (v.max - v.min) * v.maxChange);
    }

    v.value = randomize(min, max);
  }
  return values;
}

function initValues(values) {
  for (const [key, v] of Object.entries(values)) {
    const v = values[key];
    if (v.max < v.min) {
      let tmp = v.max;
      v.max = v.min;
      v.min = tmp;
    }
    if (v.value === undefined) {
      v.value = randomize(v.min, v.max);
    }
    if (v.trend === undefined) {
      v.trend = v.value <= randomize(v.min, v.max) ? -1 : 1;
    }
  }
  return values;
}

function createMessage(values) {
  var msg = `${new Date().toISOString()};${stationName};`;
  msg += `${values.temperature.value};`;
  msg += `${values.humidity.value};`;
  msg += `${values.dewPoint.value};`;
  msg += `${values.windDirection.value};`;
  msg += values.windSpeed.value;
  return msg;
}

function randomize(min, max) {
  return Math.round(100 * (Math.random() * (max - min) + min)) / 100;
}
