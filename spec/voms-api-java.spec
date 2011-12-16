Name: voms-api-java
Version: 2.0.7
Release: 1%{?dist}
Summary: The Virtual Organisation Membership Service Java APIs

Group: System Environment/Libraries
License: ASL 2.0
URL: https://twiki.cnaf.infn.it/twiki/bin/view/VOMS
Source: %{name}-%{version}.tar.gz
BuildRoot:	%{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)

BuildArch:	noarch

BuildRequires:  maven
BuildRequires:  jpackage-utils
BuildRequires:  java-devel

BuildRequires:  maven-compiler-plugin
BuildRequires:  maven-install-plugin
BuildRequires:  maven-jar-plugin
BuildRequires:  maven-javadoc-plugin
BuildRequires:  maven-release-plugin
BuildRequires:  maven-resources-plugin
BuildRequires:  maven-surefire-plugin

Requires:       jpackage-utils
Requires:       bouncycastle >= 1.39
Requires:       jakarta-commons-cli
Requires:       jakarta-commons-lang
Requires:       log4j
Requires:       java

%description
The Virtual Organization Membership Service (VOMS) is an attribute authority
which serves as central repository for VO user authorization information,
providing support for sorting users into group hierarchies, keeping track of
their roles and other attributes in order to issue trusted attribute
certificates and SAML assertions used in the Grid environment for
authorization purposes.

This package provides a java client APIs for VOMS.

%package    javadoc
Summary:    Javadoc for the VOMS Java APIs
Group:      Documentation
BuildArch:  noarch
Requires:   jpackage-utils
Requires:   %{name} = %{version}-%{release}

%description javadoc
Virtual Organization Membership Service (VOMS) Java API Documentation.

%prep
%setup -q

%build
mvn javadoc:javadoc assembly:assembly

%install
tar -C $RPM_BUILD_ROOT -xvzf target/%{name}-%{version}.tar.gz

ln -s %{name}-%{version}.jar $RPM_BUILD_ROOT%{_javadir}/%{name}.jar
ln -s %{name}-%{version}.jar $RPM_BUILD_ROOT%{_javadir}/vomsjapi.jar

mv $RPM_BUILD_ROOT%{_javadocdir}/%{name} $RPM_BUILD_ROOT%{_javadocdir}/%{name}-%{version}
ln -s %{name}-%{version} $RPM_BUILD_ROOT%{_javadocdir}/%{name}

%clean
rm -rf $RPM_BUILD_ROOT

%files
%defattr(-,root,root,-)

%{_javadir}/%{name}.jar
%{_javadir}/%{name}-%{version}.jar

# Backward compatibility naming
%{_javadir}/vomsjapi.jar

%doc AUTHORS LICENSE

%files javadoc
%defattr(-,root,root,-)
%doc %{_javadocdir}/%{name}
%doc %{_javadocdir}/%{name}-%{version}

%changelog

* Fri Dec 16 2011 Andrea Ceccanti <andrea.ceccanti at cnaf.infn.it> - 2.0.7-1
- Self-managed packaging
- maven-based build
