# zatar-lwm2m-client-example
Example "hello world" example of using LWM2M/CoAP/TLS to connect to the Zatar IoT Platform

## Getting Started
Before you start using the code here, make sure that you have done the following:
* Create an avatar definition on Zatar using your intended model name.
* Create an avatar using the avatar definition above and your serial number.
* Create a device token and associate it to the model name that you used above.

Now you're ready to dive into the code. First clone this repository with

```
git clone git@github.com:zatar-iot/zatar-lwm2m-client-example.git
```

Navigate into the directory ```zatar-lwm2m-client-example/zatar-hello-world/```, and build with

```
gradle buildHelloWorld
```

This will generate the jar at ```build/libs/zatar-hello-world-standalone.jar```.

Before you run this, you will need to update the properties file.
```
zatar.hostname=devices.zatar.com
zatar.port=5683

device.manufacturer=Zatar Example Devices, Inc
device.model=zatarhelloworld1
device.serial.number=ZHW12345
device.token=example token
```

Update ```device.model```, ```device.serial.number```, and ```device.token``` to the values you obtained earlier. Then run
```
java -jar build/libs/zatar-hello-world-standalone.jar hello-world.properties
```

The output should look like
```
[Boot] INFO:  setProperties(com.simontuffs.onejar.JarClassLoader@36fc4957)
[Boot] INFO:  using JarClassLoader: com.simontuffs.onejar.JarClassLoader
Jun 25, 2015 4:38:23 PM org.eclipse.californium.core.network.config.NetworkConfig createStandardWithFile
INFO: Loading standard properties from file Californium.properties
[JarClassLoader] INFO:  findResources(org/slf4j/impl/StaticLoggerBinder.class)
[JarClassLoader] INFO:  findResources: looking in [lib/gson-2.2.4.jar, lib/californium-core-99.0.0-SNAPSHOT.jar, lib/leshan-core-99.0.3-SNAPSHOT.jar, main/main.jar, lib/netty-all-4.1.0.Beta4.jar, lib/element-connector-99.0.0-SNAPSHOT.jar, lib/scandium-1.0.0-M3.jar, lib/leshan-client-core-99.0.3-SNAPSHOT.jar, /, lib/leshan-client-cf-99.0.3-SNAPSHOT.jar, lib/slf4j-api-1.7.12.jar]
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
Jun 25, 2015 4:38:23 PM org.eclipse.californium.core.CoapServer start
INFO: Starting server
Jun 25, 2015 4:38:23 PM org.eclipse.californium.core.network.tcp.TCPEndpoint start
INFO: Starting endpoint at lwm2m/192.168.56.14:5683
Jun 25, 2015 4:38:23 PM org.eclipse.californium.elements.tcp.client.TcpClientConnector start
INFO: Staring TCP CLIENT connector
Registered with ID: /rd/EW9hYExbEI
Device Token was accepted
Jun 25, 2015 4:38:24 PM org.eclipse.californium.core.CoapResource addObserveRelation
INFO: Successfully established observe relation between lwm2m/192.168.56.14:5683#8e59b5a2 and resource /3
Jun 25, 2015 4:38:24 PM org.eclipse.californium.core.CoapResource addObserveRelation
INFO: Successfully established observe relation between lwm2m/192.168.56.14:5683#8e59b5a3 and resource /3
Jun 25, 2015 4:38:24 PM org.eclipse.californium.core.CoapResource addObserveRelation
INFO: Successfully established observe relation between lwm2m/192.168.56.14:5683#8e59b5a4 and resource /3
Jun 25, 2015 4:38:24 PM org.eclipse.californium.core.CoapResource addObserveRelation
INFO: Successfully established observe relation between lwm2m/192.168.56.14:5683#8e59b5a5 and resource /23854
```

When you retrieve your avatar, it should appear online.

> There is a known bug where the first attempt to do this will not cause the avatar to appear online. This is fixed by terminating the hello world app and running it again.
