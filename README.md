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

With version 3.0 of the API, the validation interface changes significantly.
A partially backward compatible VOMSValidator has been maintained to ease the transition
to the new API.
In order to validate VOMS attributes one has to do the following:



```java
/* certificate chain may come either from loading (and validating)
   using BouncyCastle or from an authenticated HTTPS session */

X509Certificate[] theChain = ...;

VOMSACValidator validator = VOMSValidators.newValidator();
List<VOMSAttribute> vomsAttrs =  validator.validate(theChain);
```

The VOMSAttribute interface provides access to all VOMS
attributes (i.e., FQANs and Generic attributes):

```java
if (vomsAttrs.size() > 0) {
	
	VOMSAttribute va = vomsAttrs.get(0);
	List<String> fqans = va.getFQANs();
	
	for (String f: fqans)
		System.out.println(f);

	List<VOMSGenericAttribute>	gas = va.getGenericAttributes();

	for (VOMSGenericAttribute g: gas)
		System.out.println(g);
}
```


### Creating a VOMS Proxy

UserCredentials class has been redesigned. It implements a default strategy to load X509 user credentials in PEM or PKCS12 format.
The default strategy looks for user credentials in standard locations.

```java

/* Load user's credentials */

X509Credential cred = UserCredentials.loadCredentials("passphrase".toCharArray());
```

Request an AC to a VOMS service, using vomses file in one of the default places and the standard implementation for the VOMS AC service.

```java
DefaultVOMSACRequest request = new DefaultVOMSACRequest();
request.setLifetime(12);
request.setVoName("voName");

VOMSACService service = new DefaultVOMSACService();
    
AttributeCertificate attributeCertificate = service.getVOMSAttributeCertificate(cred, request);
```


Proxy creation:

```java

/* Get the VOMS proxy */

ProxyCertificateOptions proxyOptions = new ProxyCertificateOptions(cred.getCertificateChain());
proxyOptions.setAttributeCertificates(new AttributeCertificate[] {attributeCertificate});

ProxyCertificate proxyCert = ProxyGenerator.generate(proxyOptions, cred.getKey());    
```


A new CredentialsUtils class provides the method ```saveCredentials(OutputStream os, X509Credential uc)```  which saves user credentials as a plain text PEM data.

```java

/* 
   Save the proxy
   Writes the user certificate chain first, then the user key.
*/

OutputStream os = new FileOutputStream("/tmp/savedProxy");
CredentialsUtils.saveCredentials(os, procyCert.getCredential());

```


### Migration workflow 

In order to use the new API update your pom.xml file with the right version.


```bash
<dependency>
  <groupId>org.italiangrid</groupId>
  <artifactId>voms-api-java</artifactId>
  <version>3.0-SNAPSHOT</version>
</dependency>
```


An example of how using the new API is reported below.
First, a VOMS proxy is loaded, then its attributes are validated.


Old approach:

```java
/* Old API packages */
import org.glite.voms.PKIUtils;
import org.glite.voms.VOMSAttribute;
import org.glite.voms.VOMSValidator;


/* Load a VOMS proxy and validates its attribute certificates */


public class MigrationTestOld {

  final static Logger log = LoggerFactory.getLogger(MigrationTestNew.class);

  public static void ValidationExample(String filename) throws FileNotFoundException, IOException, KeyStoreException,
      CertificateException {


    X509Certificate[] certchain = PKIUtils.loadCertificates(filename);

    VOMSValidator validator = new VOMSValidator(certchain);

    validator.validate();

    List<VOMSAttribute> attrs = validator.getVOMSAttributes();

    for (VOMSAttribute a : attrs)
      log.info("Attribute: " + a);

  }


  public static void main(String[] args) throws FileNotFoundException, IOException, KeyStoreException,
      CertificateException {
    ValidationExample("/tmp/x509up_u1000");
  }
}
```


New approach:

```java
/* New API packages */
import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.VOMSValidators;
import org.italiangrid.voms.ac.VOMSACValidator;

import eu.emi.security.authn.x509.impl.PEMCredential;


/* Load a VOMS proxy and validates its attribute certificates */

public class MigrationTestNew {

  final static Logger log = LoggerFactory.getLogger(MigrationTestNew.class);

  public static void ValidationExample(String filename) throws FileNotFoundException, IOException, KeyStoreException,
      CertificateException {

    VOMSACValidator validator = VOMSValidators.newValidator();

    PEMCredential c = new PEMCredential(new FileInputStream(filename), null);

    X509Certificate[] certChain = c.getCertificateChain();
    List<VOMSAttribute> attrs = validator.validate(certChain);

    for (VOMSAttribute a : attrs)
      log.info("Attribute: " + a);

    validator.shutdown();
  }


  public static void main(String[] args) throws FileNotFoundException, IOException, KeyStoreException,
      CertificateException {
    ValidationExample("/tmp/x509up_u1000");

  }
}

```



## Documentation

More details on the APIs can be found in the Javadoc.

## Licence

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

