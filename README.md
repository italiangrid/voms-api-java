# VOMS Java API

Java binding for the Virtual Organization Membership Service (VOMS) API.

The VOMS API can be used for 
- validating attribute certifcates (ACs) inside a proxy and reading the attributes (VOSM FQANs or VOMS generic attributes)
- contacting a VOMS service in order to get an AC and for creating proxy certificates that contains an AC

## Installing

If using Maven, add the dependencies to your pom file

```bash
<dependency>
  <groupId>org.glite.voms</groupId>
  <artifactId>voms-api-java</artifactId>
  <version>${voms-api-java-version}</version>
</dependency>
```

### Configure logging

VOMS  Java API uses [SLF4J](http://www.slf4j.org/) for logging. SLF4J can direct your logging output 
to several logging frameworks (log4j version 1.2, JCL, Logback, and others). See the 
[SLF4J manual](http://www.slf4j.org/manual.html) for more details.

In order to setup logging you first need to add the slf4j jar file for your preferred binding
(e.g. slf4j-log4j12.jar in the case of log4j) to the classpath. Not doing so will cause an error like this

```bash
LF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
```

You also need to add the framework jar to your classpath. In case you are using Maven and log4j

```bash
<dependency>
  <groupId>org.slf4j</groupId>
  <artifactId>slf4j-log4j12</artifactId>
  <version>1.7.1</version>
</dependency>
<dependency>
  <groupId>log4j</groupId>
  <artifactId>log4j</artifactId>
  <version>1.2.17</version>
</dependency>
```

and configure your logging framework, e.g. having a log4j.proprties file in the classpath 
if using log4j

```bash
# Root logger option
log4j.rootLogger=DEBUG, stdout
 
# Direct log messages to stdout
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target=System.out
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n
```

or a logback.xml file if using Logback. An example Logback configuration file can be 
found under src/test/resources. 

## Getting started

### Validation 

The class for validating a certificate (or chain of certificates) is VOMSValidator. 
VOMSValidator is given a Java X509Certificate (or an  array of) and has methods for validating
the attribute certificates

```java
/* certificate may come either from loading (and validating)
   using BouncyCastle or from an authenticated HTTPS session */

VOMSValidator validator = new VOMSValidator(certificate);

validator.validate();
```

and query for the presence of attributes

```java
if(validator.getRoles(“aVo”).contains(“admin”)) {

  // allows admin actions
}
```

or retrieve the entire set of attributes

```java
List<String> attributes = validator.getVOMSAttributes();
    
for(String attribute : attributes) {
  System.out.println(attribute);
}
```

### Creating a Proxy

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

