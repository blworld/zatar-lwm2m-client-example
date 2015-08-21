# zatar-hello-world
Simple example client that connects to Zatar using LWM2M/CoAP/TLS

## Getting Started
Before you start using the code here, make sure that you have done the following:
* Create an avatar definition on Zatar using your intended model name.
* Create an avatar using the avatar definition above and your serial number.
* Create a device token and associate it to the model name that you used above.

Ensure that you are in the directory ```zatar-hello-world/```, and build the project with

```
gradle buildHelloWorld
```

This will generate the jar at ```build/libs/zatar-hello-world-standalone.jar```.

Before you run this, you will need to update the properties file.
```
zatar.hostname=beta-devices.zatar.com
zatar.port=5684

tls.enabled=true
tls.protocol=TLSv1.2

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
[Boot] INFO:  setProperties(com.simontuffs.onejar.JarClassLoader@670bb83e)
[Boot] INFO:  using JarClassLoader: com.simontuffs.onejar.JarClassLoader
[JarClassLoader] INFO:  findResources(org/slf4j/impl/StaticLoggerBinder.class)
[JarClassLoader] INFO:  findResources: looking in [lib/gson-2.2.4.jar, main/main.jar, lib/netty-all-4.1.0.Beta4.jar, lib/leshan-client-core-99.0.5-SNAPSHOT.jar, lib/californium-core-99.0.3-SNAPSHOT.jar, lib/element-connector-99.0.3-SNAPSHOT.jar, lib/scandium-1.0.0-M3.jar, lib/leshan-client-cf-99.0.5-SNAPSHOT.jar, /, lib/leshan-core-99.0.5-SNAPSHOT.jar, lib/slf4j-api-1.7.12.jar]
Aug 20, 2015 12:07:59 PM org.eclipse.californium.core.network.config.NetworkConfig createStandardWithFile
INFO: Loading standard properties from file Californium.properties
Aug 20, 2015 12:07:59 PM org.eclipse.californium.core.CoapServer start
INFO: Starting server
Aug 20, 2015 12:07:59 PM org.eclipse.californium.core.network.CoAPEndpoint start
INFO: Starting endpoint at lwm2m-josh-vm.zatar.com/192.168.56.14:5684
Aug 20, 2015 12:07:59 PM org.eclipse.californium.elements.tcp.client.TcpClientConnector start
INFO: Staring TCP CLIENT connector
Registered with ID: /rd/hWDNwxb9ca
Device token was accepted
Aug 20, 2015 12:08:01 PM org.eclipse.californium.core.CoapResource addObserveRelation
INFO: Successfully established observe relation between lwm2m-josh-vm.zatar.com/192.168.56.14:5684#a897ebc7 and resource /23854
Aug 20, 2015 12:08:01 PM org.eclipse.californium.core.CoapResource addObserveRelation
INFO: Successfully established observe relation between lwm2m-josh-vm.zatar.com/192.168.56.14:5684#a897ebc5 and resource /3
Aug 20, 2015 12:08:01 PM org.eclipse.californium.core.CoapResource addObserveRelation
INFO: Successfully established observe relation between lwm2m-josh-vm.zatar.com/192.168.56.14:5684#a897ebc6 and resource /3
```

When you retrieve your avatar, it should appear online.

Congratulations! You've created your first Zatar-enabled device!

> In case you see the message "Device token was rejected" instead of "Device token was accepted", make sure that you registered your token correctly through Zatar.

## Hello World Tour

This example contains only the one file, ```ExampleLwM2mDeviceMain.java```. We'll start at ```main()```, beginning on line 34. Ignore the ```initProperties(...)``` line for now - that method (defined at the end of ```ExampleLwM2mDeviceMain.java```) opens and parses the properties file that you modified above.

So let's dive into the code defining the object model.

> The fundamental unit in LWM2M is a resource. Resources are grouped into objects. A device's object model is the definition of which resources get grouped into which objects, how to create instances of those objects, and how to read, write, and execute those resources.

Noting this, let's examine lines 47-50
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
		devTokenResources.put(1, new ResourceModel(1, "-1", Operations.W, false, false, Type.INTEGER, "", "", ""));
		final ObjectModel devTokenObjectModel = new ObjectModel(23854, "Zatar Device Token", "", false, true, devTokenResources);
```

This also defines two resources: ```/23854/0/0```, the Device Token that you created earlier, and ```23854/0/1```, the Token Validation resource. Note that where the other resources used ```Operations.R```, the Validation resource uses ```Operations.W```. This is because that resource is designed to be written to only, not read or executed.

To tie these objects together into a single object model, we use an ```ObjectsInitializer```:
```java
		final Map<Integer, ObjectModel> objectModels = new HashMap<>();
		objectModels.put(3, deviceObjectModel);
		objectModels.put(23854, devTokenObjectModel);
		final ObjectsInitializer initializer = new ObjectsInitializer(new LwM2mModel(objectModels));
```

That ```initializer``` is used both to provide a single object for the whole client's object model, and also as a way to link more complex behavior to various operations.

Note: As with the ```ResourceModel```s above, the key for each ```ObjectModel``` entry must equal the object ID provided in the object's constructor.

By default, an object will report default values on Read operations, and will report success without doing anything on Writes or Executes. For object 3, our Device object, that's perfectly fine; we only have two resources, both of which only support Read. But for the Device Token object, we want to provide some feedback. The next line accomplishes that:
```
		initializer.setClassForObject(23854, DeviceToken.class);
```

This line links the ```DeviceToken``` class (defined at the bottom of the file) to object ID 23854. This is what allows ```zatar-hello-world``` to print out the "Device Token was accepted/rejected" message. Exactly how that is done is discussed in more detail in ```zatar-echo-example```.

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
