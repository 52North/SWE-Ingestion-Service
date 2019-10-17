#### status: in development process

[![Build Status](https://travis-ci.org/52North/SWE-Ingestion-Service.svg?branch=dev)](https://travis-ci.org/52North/SWE-Ingestion-Service)

# SWE-Ingestion-Service

The SensorWebEnabled-Ingestion-Service is an easy to configure and easy to deploy Data Flow Web application. It utilizes the Spring Cloud DataFlow Server technology and makes use of the Source-Processor-Sink pipelines stream approach.

## Libraries and Licenses.

#### Third party libraries and licenses

|Library|License|Link/Source|
|:----:|:----:|:----:|
|Spring-framework|Apache License Version 2.0|[https://github.com/spring-projects/spring-framework/blob/master/src/docs/dist/license.txt](https://github.com/spring-projects/spring-framework/blob/master/src/docs/dist/license.txt)|

#### SWE-Ingestion-Service

|Library|License|Link/Source|
|:----:|:----:|:-----:|
|SWE-Ingestion-Service|GNU GENERAL PUBLIC LICENSE 2.0|[https://github.com/52North/SWE-Ingestion-Service/blob/dev/LICENSE](https://github.com/52North/SWE-Ingestion-Service/blob/dev/LICENSE)|


## Installation

  1. Clone the repository: `git clone https://github.com/52north/SWE-Ingestion-Service`.
  2. Change into new directoy: `cd SWE-Ingestion-Service`.
  3. Build the project: `mvn clean install`.

## How to Run

  1. Execute `docker-compose --file etc/docker-compose.yml up`.

#### Creating and starting a Stream:

1. Create a Stream: Send an aggregateProcess to the CnCService via a POST request (see [API section](#streams) for detailed description). On success, your response payload contains name, status, and definition of the created stream, such as:

      ```
   {
     "name": "sb68a63d8-cc63-4ce3-9212-09b7a1f47740",
     "status": "undeployed",
     "definition": "mqtt-source-rabbit --url=mqtt://mosca --topics=spiddal-fluorometer | csv-processor | db-sink"
   }
    ```
    The `"name"`-field value contains the `streamId` of the created stream. Use this value in order to start the streaming process of the stream as described in the next step.

2. Deploy/Start (or Undeploy/Pause) the stream: Send a PUT request to the CnCService at the resource of the stream (see [API section](#streams) for detailed description), e.g.:
 /cnc/api/streams/sb68a63d8-cc63-4ce3-9212-09b7a1f47740

     with the status "deployed" (or "undeployed") to start (or pause) the stream process, e.g.:
     ```
      {
        "status": "deployed"
      }
    ```
On success, the response status code is 204 - `no content` and the stream is running.

## API:

#### general:
* GET [http://localhost:8082/cnc/api](http://localhost:8082/cnc/api) with `Accept` header `application/json` -         gets a list of resources, i.e.:
    ```json
    {
        "resources": [
            {
                "name": "sources",
                "decription": "List of registered sources.",
                "href": "http://cnc:8082/cnc/api/sources"
            },
            {
                "name": "processors",
                "decription": "List of registered processors.",
                "href": "http://cnc:8082/cnc/api/processors"
            },
            {
                "name": "sinks",
                "decription": "List of registered sinks.",
                "href": "http://cnc:8082/cnc/api/sinks"
            },
            {
                "name": "streams",
                "decription": "List of registered streams.",
                "href": "http://cnc:8082/cnc/api/streams"
            }
        ]
    }
    ```

#### Sources:

  * GET [http://localhost:8082/cnc/api/sources](http://localhost:8082/cnc/api/sources) with `Accept`header "application/json` - gets all registered sources, e.g.:
    ```json
    {
        "sources": [
            {
                "name": "mqtt-source-rabbit",
                "options": [
                    {
                        "name": "qos",
                        "type": "java.lang.Integer[]",
                        "description": "the qos; a single value for all topics or a comma-delimited list to match the topics",
                        "defaultValue": ""
                    },
                    {
                        "name": "binary",
                        "type": "java.lang.Boolean",
                        "description": "true to leave the payload as bytes",
                        "defaultValue": "false"
                    },
                    {
                        "name": "charset",
                        "type": "java.lang.String",
                        "description": "the charset used to convert bytes to String (when binary is false)",
                        "defaultValue": "UTF-8"
                    },
                    {
                        "name": "clean-session",
                        "type": "java.lang.Boolean",
                        "description": "whether the client and server should remember state across restarts and reconnects",
                        "defaultValue": "true"
                    },
                    {
                        "name": "persistence-directory",
                        "type": "java.lang.String",
                        "description": "Persistence directory",
                        "defaultValue": "/tmp/paho"
                    },
                    {
                        "name": "client-id",
                        "type": "java.lang.String",
                        "description": "identifies the client",
                        "defaultValue": "stream.client.id.source"
                    },
                    {
                        "name": "keep-alive-interval",
                        "type": "java.lang.Integer",
                        "description": "the ping interval in seconds",
                        "defaultValue": "60"
                    },
                    {
                        "name": "url",
                        "type": "java.lang.String[]",
                        "description": "location of the mqtt broker(s) (comma-delimited list)",
                        "defaultValue": ""
                    },
                    {
                        "name": "persistence",
                        "type": "java.lang.String",
                        "description": "'memory' or 'file'",
                        "defaultValue": "memory"
                    },
                    {
                        "name": "username",
                        "type": "java.lang.String",
                        "description": "the username to use when connecting to the broker",
                        "defaultValue": "guest"
                    },
                    {
                        "name": "password",
                        "type": "java.lang.String",
                        "description": "the password to use when connecting to the broker",
                        "defaultValue": "guest"
                    },
                    {
                        "name": "connection-timeout",
                        "type": "java.lang.Integer",
                        "description": "the connection timeout in seconds",
                        "defaultValue": "30"
                    },
                    {
                        "name": "topics",
                        "type": "java.lang.String[]",
                        "description": "the topic(s) (comma-delimited) to which the source will subscribe",
                        "defaultValue": ""
                    }
                ]
            }
        ]
    }
    ```


#### Processors:

  * GET [http://localhost:8082/cnc/api/processors](http://localhost:8082/cnc/api/processors) with `Accept`header `application/json` - gets all registered processors, e.g.:
    ```json
    {
        "processors": [
            {
                "name": "csv-processor",
                "options": [
                    {
                        "name": "offering",
                        "type": "java.lang.String",
                        "description": "offering field desc",
                        "defaultValue": "offering-default-value"
                    },
                    {
                        "name": "sensor",
                        "type": "java.lang.String",
                        "description": "sensor field desc",
                        "defaultValue": "sensor-default-value"
                    },
                    {
                        "name": "sensormlurl",
                        "type": "java.lang.String",
                        "description": "sensormlurl field desc",
                        "defaultValue": "http://example.com/process-description.xml"
                    }
                ]
            }
        ]
    }
    ```

#### Sinks:

  * GET [http://localhost:8082/cnc/api/sinks](http://localhost:8082/cnc/api/sinks) with `Accept`header `application/json` - gets all registered sinks, e.g.:
    ```json
    {
          "sinks": [
            {
                "name": "db-sink",
                "options": [
                    {
                        "name": "offering",
                        "type": "java.lang.String",
                        "description": "offering field desc",
                        "defaultValue": "offering-default-value"
                    },
                    {
                        "name": "sensor",
                        "type": "java.lang.String",
                        "description": "sensor field desc",
                        "defaultValue": "sensor-default-value"
                    },
                    {
                        "name": "sensormlurl",
                        "type": "java.lang.String",
                        "description": "sensormlurl field desc",
                        "defaultValue": "http://example.com/process-description.xml"
                    },
                    {
                        "name": "password",
                        "type": "java.lang.String",
                        "description": "Login password of the database.",
                        "defaultValue": "null"
                    },
                    {
                        "name": "username",
                        "type": "java.lang.String",
                        "description": "Login username of the database.",
                        "defaultValue": "null"
                    },
                    {
                        "name": "url",
                        "type": "java.lang.String",
                        "description": "JDBC URL of the database.",
                        "defaultValue": "null"
                    }
                ]
            },
            {
                "name": "log-sink",
                "options": []
            }
          ]
    }
    ```

#### Streams:

  * GET [http://localhost:8082/cnc/api/streams](http://localhost:8082/cnc/api/streams) with `Accept`header `application/json` - gets all registered streams, e.g.:
    ```json
    {
        "streams": [
            {
                "name": "s27219dc2-a962-4566-85e9-fc24f0ef8aef",
                "status": "deployed",
                "definition": "mqtt-source-rabbit --url=mqtt://mosca --topics=airmar-rinville-1 | csv-processor --sensormlurl=http://cnc:8082/cnc/api/streams/s27219dc2-a962-4566-85e9-fc24f0ef8aef --offering=AIRMAR-RINVILLE-2/observations --sensor=AIRMAR-RINVILLE-2 | db-sink --sensormlurl=http://cnc:8082/cnc/api/streams/s27219dc2-a962-4566-85e9-fc24f0ef8aef --offering=AIRMAR-RINVILLE-2/observations --sensor=AIRMAR-RINVILLE-2 --url=jdbc:postgresql://database:5432/sos --username=postgres --password=****** "
            },
            {
                "name": "s8e72442f-9102-4f1b-a3cf-367053765e92",
                "status": "deployed",
                "definition": "mqtt-source-rabbit --url=mqtt://mosca --topics=airmar-rinville-1-generated | csv-processor --sensormlurl=http://cnc:8082/cnc/api/streams/s8e72442f-9102-4f1b-a3cf-367053765e92 --offering=AIRMAR-RINVILLE-1/observations --sensor=AIRMAR-RINVILLE-1 | db-sink --sensormlurl=http://cnc:8082/cnc/api/streams/s8e72442f-9102-4f1b-a3cf-367053765e92 --offering=AIRMAR-RINVILLE-1/observations --sensor=AIRMAR-RINVILLE-1 --url=jdbc:postgresql://database:5432/sos --username=postgres --password=****** "
            }
        ]
    }
    ```

  * GET [http://localhost:8082/cnc/api/streams/{streamName}](http://localhost:8082/cnc/api/streams/{streamName}) with `Accept` header `application/json` - gets the registered stream with name `streamName`, e.g.:
    ```json
    {
        "name": "s27219dc2-a962-4566-85e9-fc24f0ef8aef",
        "status": "deployed",
        "definition": "mqtt-source-rabbit --url=mqtt://mosca --topics=airmar-rinville-1 | csv-processor --sensormlurl=http://cnc:8082/cnc/api/streams/s27219dc2-a962-4566-85e9-fc24f0ef8aef --offering=AIRMAR-RINVILLE-2/observations --sensor=AIRMAR-RINVILLE-2 | db-sink --sensormlurl=http://cnc:8082/cnc/api/streams/s27219dc2-a962-4566-85e9-fc24f0ef8aef --offering=AIRMAR-RINVILLE-2/observations --sensor=AIRMAR-RINVILLE-2 --url=jdbc:postgresql://database:5432/sos --username=postgres --password=****** "
    }
    ```
    possible Responses:

    * 200 - OK
    * 404 - NOT FOUND: The Stream with name `streamName` is not found.

  * GET [http://localhost:8082/cnc/api/streams/{streamName}](http://localhost:8082/cnc/api/streams/{streamName})  `Accept` header `application/xml` - gets the sensorml processDescription of the registered stream with name `streamName`, e.g.:

    [Respone example](./docs/ProcessDescription_Example_AIRMAR-RINVILLE-2.xml) (linked to prevent duplicate content)

    possible Responses:
    * 200 - OK
    * 404 - NOT FOUND: "Stream with name `streamName` is not found." / "sensorML process description for stream `streamName` not found."

  * POST [http://localhost:8082/cnc/api/streams](http://localhost:8082/cnc/api/streams) with `Content-Type` header `application/xml` and request body containing a `AggregateProcess` description creates a  new undeployed stream.

    [RequestBody payload example](./docs/ProcessDescription_Example_AIRMAR-RINVILLE-2.xml) (linked to prevent duplicate content)

    possible Responses:

* 201 - Created with json response of the created Stream, e.g.
  ```json
  {
      "name": "sb68a63d8-cc63-4ce3-9212-09b7a1f47740",
      "status": "undeployed",
      "definition": "mqtt-source-rabbit --url=mqtt://mosca --topics=spiddal-fluorometer | csv-processor | db-sink"
  }
  ```
    * 400 - BAD REQUEST: "swe:Text definition `optionUrl` requires a hashtag ( # ) option."
    * 400 - BAD REQUEST: "Option `appOptionName` is not supported by source `sourceName`."
    * 404 - NOT FOUND: "No supported Source found for DataRecord definition `dataRecordDefinition`."
    * 404 - NOT FOUND: "DataRecord definition `dataRecordDefinition` is supposed to be supported by Source `sourceName`, but Source `sourceName` not found."
    * 404 - BAD REQUEST: "The xml request body is no valid aggregateProcess sensorML description."
    * 409 - CONFLICT: "A stream with name `streamName` already exists."

* PUT [http://localhost:8082/cnc/api/streams/{streamName}](http://localhost:8082/cnc/api/streams/{streamName}) with `Content-Type` header `application/json` changes the deploy-status of the registered Stream 'streamName' according to the request body.
  Example Payload:
  ```json
  {
        "status": "deployed"
  }
  ```
  possible Responses:

    * 200 - OK: The Streams status has been changed to the requested status.
    * 202 - ACCEPTED: "The Stream {streamName} is currently `deploying` and thus, the resource status will not be changed."
    * 400 - BAD REQUEST: "Request is missing required field `status`."
    * 400 - BAD REQUEST: "The requested status `statusValue` is not supported. Supported status are: 'deployed' and 'undeployed'."
    * 404 - NOT FOUND: "Stream {streamName} not found."

* PUT [http://localhost:8082/cnc/api/streams/{streamName}](http://localhost:8082/cnc/api/streams/{streamName}) with `Content-Type` header `application/xml` changes the registered Stream 'streamName' according to the request body.
  Example Payload cf. POST on ```cnc/api/streams```


## Bugs and Feedback
Developer feedback goes a long way towards making this SWE-Ingestion-Service even better. Submit a bug report or request feature enhancements to [via mail to s.jirka@52north.org](mailto:s.jirka@52north.org?Subject=SWE-Ingestion-Service) or open an issue on this github repository.

## Documentation

* [Process description](https://github.com/52North/SWE-Ingestion-Service/blob/dev/docs/ProcessDescription.md)

## Credits

The development of the 52°North **SWE Ingestion Service** implementation was supported by several organizations and projects. Among other we would like to thank the following organisations and project

| Project/Logo | Description |
| :-------------: | :------------- |
| <a target="_blank" href="https://www.seadatanet.org/About-us/SeaDataCloud/"><img alt="SeaDataCloud Project Logo" align="middle" width="156" src="https://www.seadatanet.org/var/storage/images/_aliases/fullsize/media/seadatanet2-media/about-us/logos/sdc-logocolor-jpg/20633-1-eng-GB/SDC-LogoColor-JPG.jpg"/></a> | The development of this version of 52&deg;North smle was supported by the <a target="_blank" href="https://ec.europa.eu/programmes/horizon2020/">Horizon 2020</a> research project <a target="_blank" href="https://www.seadatanet.org/About-us/SeaDataCloud/">SeaDataCloud</a> (co-funded by the European Commission under the grant agreement n&deg;730960) |
