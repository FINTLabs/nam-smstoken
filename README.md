# nam-smstoken
SMS token authentication class for NetIQ Access Manager. 

This class is intended to be a addition to another authentication class. E.g. in conjunction with the 
`Secure Name/Password - Form` class.


# Install

* Download the `jar` (`nam-smstoken-x.x.x.jar`) from [lastest](https://github.com/Rogaland/nam-smstoken/releases/latest) and copy it to 
`/opt/novell/nam/idp/webapps/nidp/WEB-INF/lib`. See [Deploying Your Authentication Class](https://www.netiq.com/documentation/access-manager-43/nacm_enu/data/bb8bwzi.html)
in the NetIQ documentation for more information.

```shell
# Replace the url with the latest url
$ wget https://github.com/Rogaland/nam-smstoken/releases/download/v1.1.0/nam-smstoken-1.1.0.jar
$ cp nam-smstoken-1.1.0.jar /opt/novell/nam/idp/webapps/nidp/WEB-INF/lib
```

* In the `Create Authentication Class` dialog use the following settings:

| Name            | Value                                          |
|-----------------|------------------------------------------------|
| Display name    | A descriptive name                             |
| Java class      | `Other`                                        |
| Java class path | `no.rogfk.nam.idp.SMSTokenAuthenticationClass` |

* Create a `token.jsp file.` See the example in the `pages` directory of this project. You can use what ever filename on the 
`JSP` file you want. Just specify the `JSP` property on `class` or `method`.


## Configuration parameters

| Parameter                | Required | Default value                         | Description                                                                            | Example                                                                                                              |
|--------------------------|----------|---------------------------------------|----------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------|
| trace                    | `false`  | `false`                               | Debug logging to `catalina.out`                                                        |                                                                                                                      |
| allowSessionUser         | `false`  | `false`                               | Indicates if we should look for session user                                           | `true` / `false`                                                                                                     |
| phoneAttribute           | `false`  | `mobile`                              | The name of the mobile LDAP attribute                                                  | `mobileNumber`                                                                                                       |
| charsToken               | `false`  | `1234567890`                          | Characters to be used when generating `token`                                          | `1234567890`                                                                                                         |
| lengthToken              | `false`  | `4`                                   | Lenght of the `token`                                                                  | `4`                                                                                                                  |
| missingMobileMessage     | `true`   |                                       | The message to be shown if no mobile number is found for the user                      | `Unable to find a mobile number for your user. Goto <a href="https://me.site.com/mobile">Update you mobilenumber</a>`|
| gatewayDestName          | `true`   |                                       | Name of the URL parameter for the destination mobilenumber                             | `sMobile`                                                                                                            |
| gatewayError             | `true`   |                                       | String to look for in the gateway response if the gateway wasn't able to send the SMS  | `false`                                                                                                              |
| gatewayErrorRegex        | `false`  | `false`                               | `false` = line contains check. `true` = regular expression check                       | `true` / `false`                                                                                                     |
| gatewaySuccess           | `true`   |                                       | String to look for in the gateway response if the gateway successfully sent the SMS    | `true`                                                                                                               |
| gatewaySuccessRegex      | `false`  | `false`                               | `false` = line contains check. `true` = regular expression check                       | `true` / `false`                                                                                                     |
| gatewayMessageName       | `true`   |                                       | Name of the URL parameter for the message                                              | `sMessage`                                                                                                           |
| gatewayURL               | `true`   |                                       | Endpoint to the SMS gateway                                                            | `https://gateway.sms.org/SendSMS`                                                                                    |
| gatewayPasswordParameter | `true`   |                                       | The URL parameter for the gateway password                                             | `sPassword=topsecret`                                                                                                |
| gatewayUserParameter     | `true`   |                                       | The URL parameter for the gateway username                                             | `sUser=smsuser`                                                                                                      |
| gatewayExtraParameter1   | `false`  |                                       | The URL parameter for extra parameter                                                  | `extraParam=value`                                                                                                   |
| gatewayExtraParameter2   | `false`  |                                       | The URL parameter for extra parameter                                                  | `extraParam=value`                                                                                                   |

# Build from source

The documentation from [NetIQ](https://www.netiq.com/documentation/access-manager-43/nacm_enu/data/b8q8uws.html) 
states that you need `NAMCommon.jar` and `nidp.jar` to build the authentication class. This project has extensively
testing so you will also need to copy these `jar`'s from `/opt/novell/nam/idp/webapps/nidp/WEB-INF/lib` into the `netiq`
folder of the project to be able to build:
```
jcc.jar
jgroups-all.jar
jsso.jar
nxpe.jar
```

# References
[Identity Server Authentication API](https://www.netiq.com/documentation/access-manager-43/nacm_enu/data/b8q6tv9.html)
