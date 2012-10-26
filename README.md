# VOMS Java API

Java binding for the Virtual Organization Membership Service (VOMS) API.

The VOMS API can be used for 
- validating attribute certifcates (ACs) inside a proxy and reading the attributes (VOSM FQANs or VOMS generic attributes)
- contacting a VOMS service in order to get an AC and for creating proxy certificates that contains an AC

## Installing

If using Maven, add the dependencies to your pom file

```bash
<dependency>
  <groupId>org.italiangrid</groupId>
  <artifactId>voms-api-java</artifactId>
  <version>${voms-api-java-version}</version>
</dependency>
```

### Configure logging

VOMS  Java API uses [Log4j](http://logging.apache.org/log4j/1.2/) for logging. 

In order to setup logging you first need to add the log4j1.2.xj jar file to your classpath.
If you use maven, you can use the following snippet:

```bash
<dependency>
  <groupId>log4j</groupId>
  <artifactId>log4j</artifactId>
  <version>1.2.17</version>
</dependency>
```

and have a log4j.properties file in the classpath:

```bash
# Root logger option
log4j.rootLogger=DEBUG, stdout
 
# Direct log messages to stdout
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target=System.out
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n
```
An example log4j.properties configuration file can be found under src/test/resources. 

## Getting started

### Validation 

In order to validate and access the VOMS attribute certificates in a given
certificate chain, you should use the 

```java
org.glite.voms.VOMSValidator
```
class.


```java
/* certificate chain may come either from loading (and validating)
   using BouncyCastle or from an authenticated HTTPS session */

X509Certificate[] theChain = ...;

VOMSValidator validator =  new VOMSValidator(theChain);
validator.validate();

List <VOMSAttribute> attrs = validator.getVOMSAttributes();

for ( VOMSAttribute voAttr : attrs ) {

    List <String> fqanAttrs = voAttr.getFullyQualifiedAttributes();
    // Do something with the fqans...
}
```

### Contacting a remote VOMS server and creating a proxy

Proxy creation functionalities are provided by the class VOMSProxyInit

```java
UserCredentials credentials = UserCredentials.instance("/home/vinz/.globus/usercert.pem", 
    "/home/vinz/.globus/userkey.pem", 
    "passphrase");
    
VOMSProxyInit vomsProxyInit = VOMSProxyInit.instance(credentials);

X509Certificate[] proxyCertificateChain = vomsProxyInit.getVomsProxy().getUserChain();
```

## Documentation

More details on the APIs can be found in the Javadoc.

## Licence

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

