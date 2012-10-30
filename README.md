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

#### Setting a ValidationResultListener

VOMS API now provides the ability of being informed of the outcome of each VOMS validation
by registering a '''ValidationResultListener'''. 

The interface of a '''ValidationResultListener''' is defined as follows:

'''java
void notifyValidationResult(VOMSValidationResult result, VOMSAttribute attributes)
'''

The VOMSValidationResult class provides info the outcome of VOMS validation:

'''java
VOMSValidationResult{

	boolean isValid();
	List<VOMSValidationErrorMessage> getValidationErrors();
}
'''

You can register a ValidationResultListener at VOMSACValidator creation time:

'''java
VOMSACValidator validator = VOMSValidators.newValidator(new ValidationResultListener() {
			
	public void notifyValidationResult(VOMSValidationResult result,	VOMSAttribute attributes) {

		// Your code here
		...
			
		}});
'''

### Requesting a VOMS AC from a server and creating a proxy out of it

In order to request a VOMS AC from a VOMS server you start by providing your
own credentials. To parse your credentials (certificate + private key) you will use
the 

```java
UserCredentials.loadCredentials(char[] keyPassword);
```
method. This methods looks for PEM or PKCS12 credentials in standard localtions 
and returns a CANL X509Credential.

```java

/* Load user's credentials */

X509Credential cred = UserCredentials.loadCredentials("passphrase".toCharArray());
```

To request an AC from a VOMS service, one has to create a VOMSACRequest in order to set
options for the requested AC, like its lifetime, the VO name or the requested VOMS fqans.

```java
VOMSACRequest request = new DefaultVOMSACRequest();
request.setLifetime(12);
request.setVoName("atlas");

VOMSACService service = new DefaultVOMSACService();
    
AttributeCertificate attributeCertificate = service.getVOMSAttributeCertificate(cred, request);
```

Creating a proxy containing the VOMS AC just obtained is trivial with the help of CANL
proxy generation utilities:

```java

ProxyCertificateOptions proxyOptions = new ProxyCertificateOptions(cred.getCertificateChain());
proxyOptions.setAttributeCertificates(new AttributeCertificate[] {attributeCertificate});

ProxyCertificate proxyCert = ProxyGenerator.generate(proxyOptions, cred.getKey());    
```
The proxy can then be saved to an output stream in PEM format using the 
```java
CredentialsUtils.saveCredentials(OutputStream os, X509Credential uc)
```
method, as shown in the following example:

```java
OutputStream os = new FileOutputStream("/tmp/savedProxy");
CredentialsUtils.saveCredentials(os, proxyCert.getCredential());
```


### Migrating from VOMS API Java v. 2.x

In order to use the new API update your pom.xml with the right dependencies (in case
you use maven).


```bash
<dependency>
  <groupId>org.italiangrid</groupId>
  <artifactId>voms-api-java</artifactId>
  <version>3.0-SNAPSHOT</version>
</dependency>
```

or install the VOMS API 3.0 packages (rpms or debs).

With the API v. 2.0.9 the following approach would be used to validate a VOMS AC:

```java
/* 2.0.x API packages */
import org.glite.voms.VOMSAttribute;
import org.glite.voms.VOMSValidator;

...

// Validated certificate chain  */
X509Certificate[] certchain = ...;
VOMSValidator validator = new VOMSValidator(certchain);
validator.validate();

List<VOMSAttribute> attrs = validator.getVOMSAttributes();

for (VOMSAttribute a : attrs)
	// Do something with the attribute
    ...
```

With version 3.0 the name of the packages to import has changed:

```java
/* 3.0.x API packages */
import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.VOMSValidators;
import org.italiangrid.voms.ac.VOMSACValidator;

// The VOMSACValidator interface provides access to VOMS AC validation logic.
// In order to obtain a validator use the VOMSValidators factory
VOMSACValidator validator = VOMSValidators.newValidator();

// An X.509 certcain obtained somehow
X509Certificate[] certChain = ...;

// Use the validate method to obtain a list of VOMSAttribute objects
List<VOMSAttribute> attrs = validator.validate(certChain);

for (VOMSAttribute a : attrs){
	// Do something with the attribute
	...
}

// Shutdown the validator. This should be called only when you're sure that
// you will not need the validator anymore. 
validator.shutdown();
```

## Documentation

More details on the new APIs can be found in the [Javadoc](http://italiangrid.github.com/voms-api-java/javadocs/3.x/index.html).

## Licence

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

