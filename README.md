# nam-smstoken

# Configuration
| Parametert               | Default value                          | Description                                                                            | Example                                                                                                              |
|--------------------------|----------------------------------------|----------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------|
| allowSessionUser         | `false`                                | Indicates if we should look for session user                                           | `true` \ `false`                                                                                                     |
| phoneAttribute           | `mobile`                               | The name of the mobile LDAP attribute                                                  | `mobileNumber`                                                                                                       |
| charsToken               | `ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890` | Characters to be used when generating `token`                                          | `1234567890`                                                                                                         |
| lengthToken              | `6`                                    | Lenght of the `token`                                                                  | `4`                                                                                                                  |
| missingMobileMessage     |                                        | The message to be shown if no mobile number is found for the user                      | `Unable to find a mobile number for your user. Goto <a href="https://me.site.com/mobile">Update you mobilenumber</a>` |
| gatewayDestName          |                                        | Name of the URL parameter for the destination mobilenumber                             | `sMobile`                                                                                                            |
| gatewayError             |                                        | String to look for in the gateway response if the gateway wasn't able to send the SMS  | `false`                                                                                                              |
| gatewaySuccess           |                                        | String to look for in the gateway respone if the gateway successfully sent the SMS     | `true`                                                                                                               |
| gatewayMessageName       |                                        | Name of the URL parameter for the message                                              | `sMessage`                                                                                                           |
| gatewayURL               |                                        | Endpoint to the SMS gateway                                                            | `https://gateway.sms.org/SendSMS`                                                                                    |
| gatewayPasswordParameter |                                        | The URL parameter for the gateway password                                             | `sPassword=topsecret`                                                                                                |
| gatewayUserParameter     |                                        | The URL parameter for the gateway username                                             | `sUser=smsuser`                                                                                                      |
| gatewayExtraParameter1   |                                        | The URL parameter for extra parameter                                                  | `extraParam=value`                                                                                                   |
| gatewayExtraParameter2   |                                        | The URL parameter for extra parameter                                                  | `extraParam=value`                                                                                                   |



# Referances
[Identity Server Authentication API](https://www.netiq.com/documentation/access-manager-43/nacm_enu/data/b8q6tv9.html)
