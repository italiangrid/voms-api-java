# SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
#
# SPDX-License-Identifier: Apache-2.0

# Remember to define the base_version and version_pom macros
%{!?base_version: %global base_version 0.0.0}
%{!?version_pom: %global version_pom 0.0.0}

Name: voms-api-java
Version: %{base_version}
Release: 1%{?dist}
Summary: The Virtual Organisation Membership Service Java APIs
Group: System Environment/Libraries
License: Apache-2.0
URL: https://github.com/italiangrid/voms-api-java

BuildArch: noarch

BuildRequires:  maven-openjdk17

Provides:       voms-api-java3 = %{version}
Requires:       canl-java >= 2.7
Requires:       java-17-openjdk-headless

%description
The Virtual Organization Membership Service (VOMS) is an attribute authority
which serves as central repository for VO user authorization information,
providing support for sorting users into group hierarchies, keeping track of
their roles and other attributes in order to issue trusted attribute
certificates and SAML assertions used in the Grid environment for
authorization purposes.

This package provides a Java client APIs for VOMS.

%package    javadoc
Summary:    Javadoc for the VOMS Java APIs
Group:      Documentation
BuildArch:  noarch
Requires:   %{name} = %{version}

%description javadoc
Virtual Organization Membership Service (VOMS) Java API Documentation.

%prep

%build
mvn %{?mvn_settings} -U -Dmaven.test.skip=true clean package

%install
rm -rf %{buildroot}
mkdir -p %{buildroot}%{_javadir}
mkdir -p %{buildroot}%{_javadocdir}/%{name}-%{version_pom}

install -m 644 target/%{name}-%{version_pom}.jar -t %{buildroot}%{_javadir}
ln -s %{name}-%{version_pom}.jar %{buildroot}%{_javadir}/%{name}.jar
cp -r target/javadoc/* %{buildroot}%{_javadocdir}/%{name}-%{version_pom}
ln -s %{name}-%{version_pom} %{buildroot}%{_javadocdir}/%{name}

%clean
rm -rf %{buildroot}

%files
%defattr(-,root,root,-)

%{_javadir}/%{name}.jar
%{_javadir}/%{name}-%{version_pom}.jar

%doc AUTHORS LICENSE

%files javadoc
%defattr(-,root,root,-)
%doc %{_javadocdir}/%{name}
%doc %{_javadocdir}/%{name}-%{version_pom}

%changelog
* Tue Mar 25 2025 Enrico Vianello <enrico.vianello at cnaf.infn.it> - 3.3.4-1
- Bumped packaging for 3.3.4 version

* Fri Jun 21 2024 Enrico Vianello <enrico.vianello at cnaf.infn.it> - 3.3.3-1
- Bumped packaging for 3.3.3 version

* Thu Jan 25 2018 Andrea Ceccanti <andrea.ceccanti at cnaf.infn.it> - 3.3.2-0
- Bumped packaging for 3.3.2 version

* Tue Jun 9 2015 Andrea Ceccanti <andrea.ceccanti at cnaf.infn.it> - 3.0.6-0
- Bumped packaging for 3.0.6 version

* Wed Dec 10 2014 Andrea Ceccanti <andrea.ceccanti at cnaf.infn.it> - 3.0.5-1
- Bumped packaging for 3.0.5 version

* Thu Dec 19 2013 Andrea Ceccanti <andrea.ceccanti at cnaf.infn.it> - 3.0.4-1
- Fix for https://issues.infn.it/browse/VOMS-455
- Caching certificate validator
- Refactored certificate validator builder
- Less stringent mutual exclusion on default AC parser and validator

* Thu Sep 26 2013 Andrea Ceccanti <andrea.ceccanti at cnaf.infn.it> - 3.0.2-1
- Fix for https://issues.infn.it/browse/VOMS-424
- Adoption of CANL 1.3.0
- Refactored internal AC generator

* Tue Aug 6 2013 Andrea Ceccanti <andrea.ceccanti at cnaf.infn.it> - 3.0.1-1
- Fix for https://issues.infn.it/jira/browse/VOMS-364

* Wed Oct 24 2012 Andrea Ceccanti <andrea.ceccanti at cnaf.infn.it> - 3.0.0-1
- New version of VOMS Java APIs based on CAnL
