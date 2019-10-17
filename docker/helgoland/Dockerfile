# Copyright (C) 2018-2019 52°North Initiative for Geospatial Open Source
# Software GmbH
#
# This program is free software; you can redistribute it and/or modify it
# under the terms of the GNU General Public License version 2 as published
# by the Free Software Foundation.
#
# If the program is linked with libraries which are licensed under one of
# the following licenses, the combination of the program with the linked
# library is not considered a "derivative work" of the program:
#
#     - Apache License, version 2.0
#     - Apache Software License, version 1.0
#     - GNU Lesser General Public License, version 3
#     - Mozilla Public License, versions 1.0, 1.1 and 2.0
#     - Common Development and Distribution License (CDDL), version 1.0
#
# Therefore the distribution of the program linked with libraries licensed
# under the aforementioned licenses, is permitted by the copyright holders
# if the distribution is compliant with both the GNU General Public
# License version 2 and the aforementioned licenses.
#
# This program is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
# Public License for more details.
FROM node AS BUILD

RUN apt-get update && \
    apt-get install --assume-yes --no-install-recommends git && \
    cd /tmp/ && \
    git clone https://github.com/52North/helgoland.git && \
    cd helgoland && \
    git checkout feature/sea-data-cloud && \
    npm install && \
    npm run build && \
    sed -i 's@base href="/"@base href="/helgoland/"@g' /tmp/helgoland/dist/complete/index.html

FROM nginx:alpine
# COPY ${PWD}/helgoland/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=BUILD /tmp/helgoland/dist/complete /usr/share/nginx/html
