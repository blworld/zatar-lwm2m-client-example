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
zatar.hostname=beta-devices.zatar.com
zatar.port=5683

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
[Boot] INFO:  setProperties(com.simontuffs.onejar.JarClassLoader@6d330af4)
[Boot] INFO:  using JarClassLoader: com.simontuffs.onejar.JarClassLoader
Jul 07, 2015 11:56:05 AM org.eclipse.californium.core.network.config.NetworkConfig createStandardWithFile
INFO: Loading standard properties from file Californium.properties
[JarClassLoader] INFO:  findResources(org/slf4j/impl/StaticLoggerBinder.class)
[JarClassLoader] INFO:  findResources: looking in [lib/gson-2.2.4.jar, lib/californium-core-99.0.0-SNAPSHOT.jar, lib/leshan-core-99.0.3-SNAPSHOT.jar, main/main.jar, lib/netty-all-4.1.0.Beta4.jar, lib/element-connector-99.0.0-SNAPSHOT.jar, lib/scandium-1.0.0-M3.jar, lib/leshan-client-core-99.0.3-SNAPSHOT.jar, /, lib/leshan-client-cf-99.0.3-SNAPSHOT.jar, lib/slf4j-api-1.7.12.jar]
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
Jul 07, 2015 11:56:05 AM org.eclipse.californium.core.CoapServer start
INFO: Starting server
Jul 07, 2015 11:56:05 AM org.eclipse.californium.core.network.tcp.TCPEndpoint start
INFO: Starting endpoint at lwm2m/192.168.56.14:5683
Jul 07, 2015 11:56:05 AM org.eclipse.californium.elements.tcp.client.TcpClientConnector start
INFO: Staring TCP CLIENT connector
Registered with ID: /rd/r9VIfCy1t9
Jul 07, 2015 11:56:06 AM org.eclipse.californium.core.CoapResource addObserveRelation
INFO: Successfully established observe relation between lwm2m/192.168.56.14:5683#edae95b4 and resource /23854
Jul 07, 2015 11:56:06 AM org.eclipse.californium.core.CoapResource addObserveRelation
INFO: Successfully established observe relation between lwm2m/192.168.56.14:5683#edae95b5 and resource /3
Jul 07, 2015 11:56:06 AM org.eclipse.californium.core.CoapResource addObserveRelation
INFO: Successfully established observe relation between lwm2m/192.168.56.14:5683#edae95b6 and resource /3
```

When you retrieve your avatar, it should appear online.

> There is a known bug where the first attempt to do this will not cause the avatar to appear online. This is fixed by terminating the hello world app and running it again.

Congratulations! You've created your first Zatar-enabled device!

## Hello World Tour

This example contains only the one file, ```ExampleLwM2mDeviceMain.java```. We'll start at ```main()```, beginning on line 34. Ignore the ```initProperties(...)``` line for now - that method (defined at the end of ```ExampleLwM2mDeviceMain.java```) opens and parses the properties file that you modified above.

So let's dive into the code defining the object model.

> The fundamental unit in LWM2M is a resource. Resources are grouped into objects. A device's object model is the definition of which resources get grouped into which objects, how to create instances of those objects, and how to read, write, and execute those resources.

Noting this, let's examine lines 37-40
```java
		final Map<Integer, ResourceModel> deviceResources = new HashMap<Integer, ResourceModel>();
		deviceResources.put(1, new ResourceModel(1, deviceModel, Operations.R, false, false, Type.STRING, "", "", ""));
		deviceResources.put(2, new ResourceModel(2, deviceSerialNumber, Operations.R, false, false, Type.STRING, "", "", ""));
		final ObjectModel deviceObjectModel = new ObjectModel(3, "Device", "", false, true, deviceResources);
```

This defines two resources and attaches them to object 3 (the object ID in the last line). Those resources are ```/3/0/1```, the Model, and ```/3/0/2```, the Serial Number. Things to note:
* The key matches the first parameter of the ```ResourceModel``` constructor. These each correspond to the resource ID, and must be the same, or reads and writes will produce inconsistent results.
* The second parameter of the ```ResourceModel``` constructor has the initial value of that resource. Here, we use the values provided in the properties file.
* The third parameter for each resource is ```Operations.R```. That tells the library that the Write and Execute commands are not supported for those resources. Further tutorials will explore what happens when that enumeration is set to something else. The valid values for that are any combination of "R", "W", and "E".

There is one additional required object model - 23854 - the Zebra object.
```java
		final Map<Integer, ResourceModel> devTokenResources = new HashMap<Integer, ResourceModel>();
		devTokenResources.put(0, new ResourceModel(0, deviceToken, Operations.R, false, false, Type.STRING, "", "", ""));
		final ObjectModel devTokenObjectModel = new ObjectModel(23854, "Zatar Device Token", "", false, true, devTokenResources);
```

This defines just one resource: ```/23854/0/0```, the Device Token that you created earlier.

To tie these objects together into a single object model, we use an ```ObjectsInitializer```:
```java
		final Map<Integer, ObjectModel> objectModels = new HashMap<>();
		objectModels.put(3, deviceObjectModel);
		objectModels.put(23854, devTokenObjectModel);
		final ObjectsInitializer initializer = new ObjectsInitializer(new LwM2mModel(objectModels));
```

Remember that ```initializer``` object. In more advanced applications, the ```ObjectsInitializer``` can be used to provide a link between resources and more customizable code. For now, we can use its default behavior.

Note: As with the ```ResourceModel```s above, the key for each ```ObjectModel``` entry must equal the object ID provided in the object's constructor.

Now that we've initialized the object model, we can actually connect and register to Zatar's LWM2M server.
```java
		final LwM2mClient client = new LeshanClientBuilder().
				setBindingMode(BindingMode.T).
				setServerAddress(new InetSocketAddress(zatarHostname, zatarPort)).
				setObjectsInitializer(initializer).
				build();
		client.start();

		final String endpoint = UUID.randomUUID().toString();
		final RegisterResponse response = client.send(new RegisterRequest(endpoint));
		final String registrationID = response.getRegistrationID();
		System.out.println("Registered with ID: " + registrationID);
```

This code creates the client using the ```LeshanClientBuilder```, sets the binding mode, uses the server information provided in the properties file, and attaches the ```ObjectsInitializer``` created above. The next chunk sends a ```RegisterRequest``` with a randomly generated endpoint, and prints the registration ID returned by the server.

Finally, we add a simple shutdown hook, so that on shutdown, the client sends a ```DeregisterRequest``` with the registration ID retrieved above, and then stops the client.
```java
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (registrationID != null) {
					System.out.println("\tDevice: Deregistering Client '" + registrationID + "'");
					client.send(new DeregisterRequest(registrationID));
					client.stop();
				}
			}
		});
```
