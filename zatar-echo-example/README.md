# zatar-echo-example
Example client that connects to Zatar and exposes readable, writable, and executable resources.

Note: This README assumes that you have already read the README associated with ```zatar-hello-world```.

## Getting Started
As before, you will need to do the following:
* Create an avatar definition on Zatar using your intended model name. It must have the following properties in addition to the mandatory ones:
  * Name: "echo", Key: /11111/0/0, Type: "COMMAND"
  * Name: "echo text", Key: /11111/0/1, Type: "SETTING"
  * Name: "echo count", Key: /11111/0/2, Type: "SETTING"
* Create an avatar using the avatar definition above and your serial number.
* Create a device token and associate it to the model name that you used above.

> If you ran through the ```zatar-hello-world``` example, you can use the same avatar and definition. You may need to update the definition to include the new attributes.

Now build this project with
```
gradle buildEchoer
```

Before you run it, update the properties file ```echo.properties``` with your desired settings.
```
zatar.hostname=beta-devices.zatar.com
zatar.port=5684

tls.enabled=true
tls.protocol=TLSv1.2

device.manufacturer=Zebra Technologies
device.model=zatarechoer1
device.serial.number=ECHO123
device.token=example token
```

As before, ```device.model``` and ```device.serial.number``` must match the avatar and the definition. ```device.manufacturer``` can be whatever value you choose.

Now run
```
java -jar build/libs/zatar-echo-example-standalone.jar echo.properties
```

The output you should see should be similar to that of ```zatar-hello-world```, including a line saying "Device token was accepted".

At this point, your avatar should appear online in Zatar, and you're ready to interact with your "device".

## Interacting with the Device
This device is a simple echoer, so the interaction provided simple prints something out on the screen. You can see this by doing a PUT on the avatar as follows:
```
{
  "attributes": {
    "echo": ""
  }
}
```

This executes the "echo" command, which, per the avatar definition discussed above, causes the resource /11111/0/0 to be executed.

If you return to the terminal in which you ran the example client, you should see that it has printed the following:
```
=====================================================
hello
=====================================================
```

There are also two settings, "echo text" and "echo count", that modify the behavior of "echo". Per the avatar definition given above, those correspond to resources /11111/0/1 and /11111/0/2 respectively. You can see the default values of those resources reflected in the avatar below:
```
{
   ...
  "attributes": {
     ...
    "echo text": "hello",
    "echo count": "1"
     ...
  }
}
```
Notice that when you ran the "echo" command, it printed the string "hello" once.

To update those values, do another PUT on the avatar with your new values. For instance, if you want the "echo" command to print "greetings" four times, you would write this to the avatar.
```
{
  "attributes": {
    "echo text": "greetings",
    "echo count": "4"
  }
}
```

Now when you run the "echo" command again (the same way that you did before), you should see this:
```
=====================================================
greetings
greetings
greetings
greetings
=====================================================
```

## Code Tour
What we saw with all that echoing is two examples of writable resources, and one executable resource. Now we'll take a quick tour through the code that implements all this in order to see how it does it.

> This tour assumes familiarity with the ```zatar-hello-world``` example. Only differences between this and that will be pointed out.

We begin in ```EchoLwM2mDeviceMain```. You should recognize the three blocks that declare the object-resource mappings. Two are exactly the same as ```zatar-hello-world```, and there is one new one (lines 57-61):
```
		final Map<Integer, ResourceModel> echoerResources = new HashMap<Integer, ResourceModel>();
		echoerResources.put(0, new ResourceModel(0, "", Operations.E, false, false, Type.OPAQUE, "", "", ""));
		echoerResources.put(1, new ResourceModel(1, "", Operations.RW, false, false, Type.STRING, "", "", ""));
		echoerResources.put(2, new ResourceModel(2, "", Operations.RW, false, false, Type.INTEGER, "", "", ""));
		final ObjectModel echoerObjectModel = new ObjectModel(11111, "Echoer", "", false, true, echoerResources);
```

Things to note:
* The resource ID's 0, 1, and 2 correspond to the trailing digits in the avatar definition.
* Because 0 corresponds to a command, it uses ```Operations.E```. The other two resources correspond to writable settings, so they have ```Operations.RW```.
  * The valid ```Operations``` are all of the permutations of ```R```, ```W```, and ```E```, indicating which operations are permitted.
* For each entry of the ```echoerResource```, the key matches the first parameter of the value.
* The first parameter of the ```ObjectModel``` is 11111, the object ID for the Echoer object.
* The object model constructed here is then added to the object models map at line 66 (not shown here).

Jump to lines 68-70, where we see
```
		final ObjectsInitializer initializer = new ObjectsInitializer(new LwM2mModel(objectModels));
		initializer.setClassForObject(23854, DeviceToken.class);
		initializer.setClassForObject(11111, Echoer.class);
```

This maps the ```DeviceToken``` class to object 23854, which means that any valid requests sent to any resource under 23854 will be forwarded to an instance of that class. The next line does the same thing between object ID 11111 and the ```Echoer``` class.

> Classes that are mapped to objects must be subtypes of the ```LwM2mInstanceEnabler``` class.

The rest of the ```EchoLwM2mDeviceMain``` class is the same as in ```zatar-hello-world```, so we will now focus our attention on the ```Echoer``` class.

```
public class Echoer extends SimpleInstanceEnabler {
```
Note that it extends ```SimpleInstanceEnabler```. That class is defined in the library that we're using, and provides some useful default behavior.

Next, see the two fields and their default values:
```
	private String echoText = "hello";
	private int echoCount = 1;
```
These initial values are why the avatar started out with "hello" and "1" in the "echo text" and "echo count" attributes. If you wanted to change either of those defaults, you could change them here.

Now we turn our attention to the ```read``` method:
```
	@Override
	public ValueResponse read(final int resourceId) {
		switch (resourceId) {
			case 1:
				return new ValueResponse(ResponseCode.CONTENT, new LwM2mResource(1, Value.newStringValue(echoText)));
			case 2:
				return new ValueResponse(ResponseCode.CONTENT, new LwM2mResource(2, Value.newIntegerValue(echoCount)));
		}
		return super.read(resourceId);
	}
```

This method provides the data that actually shows up in the avatar for the two settings under object 11111. It should be easy to see ```read``` provides the right value based on which resource ID is requested; when resource ID 1 is requested, the echo text is returned, and when resource ID 2 is requested, the echo count is returned. The code here demonstrates the mechanism by which values of different data types are converted into LWM2M resources and then sent to the server.

```read``` is called when an avatar is first created, and periodically while a device is online. This is how a device reports information about itself to Zatar.

Moving on to ```write```:
```
	@Override
	public LwM2mResponse write(final int resourceId, final LwM2mResource res) {
		switch (resourceId) {
			case 1:
				@SuppressWarnings("unchecked")
				final Value<String> newText = (Value<String>) res.getValue();
				echoText = newText.value;
				fireResourceChange(resourceId);
				return new LwM2mResponse(ResponseCode.CHANGED);
			case 2:
				@SuppressWarnings("unchecked")
				final Value<Integer> newCount = (Value<Integer>) res.getValue();
				echoCount = newCount.value;
				fireResourceChange(resourceId);
				return new LwM2mResponse(ResponseCode.CHANGED);
		}
		return super.write(resourceId, res);
	}
```

As with ```read```, resource 1 corresponds to the echo text, and resource 2 corresponds to the echo count. The generically typed ```Value``` objects hold the values that the server intends to write.

Note especially the use of ```fireResourceChange```. That call informs the client that the resource's value has changed, and if any servers have requested observations on those resources, that they should be notified when appropriate. It is the client code's responsibility to call that method any time a resource value has changed.

```write``` is called any time a user or application executes a PUT on an avatar, trying to update attributes defined as settings. Which object is used and which resource ID is provided is determined by the LWM2M resource ID given in the avatar definition.

Lastly, the ```execute``` method:
```
	@Override
	public LwM2mResponse execute(final int resourceId, final byte[] payload) {
		if (resourceId == 0) {
			System.out.println("=====================================================");
			for (int i = 0; i < echoCount; i++) {
				System.out.println(echoText);
			}
			System.out.println("=====================================================");
			return new LwM2mResponse(ResponseCode.CHANGED);
		}
		return super.execute(resourceId, payload);
	}
```

As you can see, this method uses ```echoText``` and ```echoCount``` to determine what string to print out, and how many times. It is called whenever a user or application executes a PUT on an avatar with attributes defined as commands.
