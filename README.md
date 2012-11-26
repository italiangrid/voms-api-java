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

VOMS Java API does not rely on logging anymore to provide information to the API user.
The API now provides listeners that receive interesting events related to VOMS attribute
certificate parsing and validation and interactions with remote VOMS servers.

## Getting started

### Validation 

With version 3.0 of the API, the validation interface changes significantly.
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
#### Getting more information about the validation outcome

The API just described returns a possibly empty list of validated VOMS attributes.
In order to get more information on the validation outcome (e.g. error messages etc. ) you now have 
two possibilities:

- register a `ValidationResultListener` on the validator and separate your error handling logic from
the main flow of your code
- use the `validateWithResult` method 

#### Setting a ValidationResultListener 

The interface of a ValidationResultListener is defined as follows:

```java
void notifyValidationResult(VOMSValidationResult result)
```

The VOMSValidationResult class provides info on the outcome of VOMS validation:

```java

VOMSValidationResult{

	boolean isValid();
	List<VOMSValidationErrorMessage> getValidationErrors();
	VOMSAttribute getAttributes();
}
```

You can register a ValidationResultListener at VOMSACValidator creation time:

```java
VOMSACValidator validator = VOMSValidators.newValidator(new ValidationResultListener() {
			
	public void notifyValidationResult(VOMSValidationResult result) {

		// Your code here
		...
			
		}});
```

or later with the `setValidationResultListener(ValidationResultListener l)` method.

### Using the `validateWithResult` method

```java
List<VOMSValidationResult> results = validator.validateWithResult(theChain);
	
for(VOMSValidationResult v: results){
	
	if ( v.isValid() ){
		VOMSAttribute attrs = v.getAttributes();
		...
	}else{
		// error handling code
	}
}
```

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
DefaultVOMSACRequest request = new DefaultVOMSACRequest();
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

