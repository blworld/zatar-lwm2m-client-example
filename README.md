# zatar-lwm2m-client-example
Example projects using LWM2M/CoAP/TLS to connect to the Zatar IoT Platform

This codebase leverages work from the [Eclipse Californium](https://github.com/eclipse/californium) and [Eclipse Leshan](https://github.com/eclipse/leshan) projects for the basis of the CoAP and LWM2M implementations.  Zatar additionally uses the [CoAP/TLS draft 1](https://github.com/hannestschofenig/tschofenig-ids/blob/master/coap-tcp-tls/draft-tschofenig-core-coap-tcp-tls-00.txt) for device connectivity.  Zatar has forked the [Californium](https://github.com/zatar-iot/californium), [Californium Element-Connector](https://github.com/zatar-iot/californium.element-connector) and [Leshan](https://github.com/zatar-iot/leshan) projects to implement the CoAP/TLS draft 1.  The examples here use compiled versions of these projects (these versions can be differentiated from the master versions by their 99.0.*-SNAPSHOT version numbers).

## Learn
There are two subprojects, ```zatar-hello-world``` and ```zatar-echo-example```.  ```zatar-hello-world``` shows how to connect to Zatar and bring a device online.  ```zatar-echo-example``` goes a few steps further and provides examples of writable and executable resources as well as how to asynchronously send data from a device to Zatar by notifying on observations.

We recommend using ```zatar-hello-world``` first, just to connect your device to Zatar and ensure that your device token works. ```zatar-echo-example``` is then a larger example, showing all of the LWM2M features that Zatar takes advantage of.  Both subprojects have extensive documentation in their README.md files.
* [Zatar-Hello-World](zatar-hello-world/):  See how to build a LWM2M object model with the Leshan Client, how to register a device with Zatar and how to share a device token.
* [Zatar-Echo-Example](zatar-echo-example/):  See how to build writable and executable resources with the Leshan Client and how to notify Zatar asyncrhonously of a change in a device's state.

## Clone
To retrieve the code and subprojects discussed here, clone this repository:
```
git clone git@github.com:zatar-iot/zatar-lwm2m-client-example.git
```

## Import
You can import both projects into Eclipse or IntelliJ by importing the root project (```zatar-lwm2m-client-example```).
