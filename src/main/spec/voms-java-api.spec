Name:       vomsjapi
Version:    2.0.7
Release:    1%{?dist}
Summary:    Virtual Organization Membership Service Java API
Group:      Development/Libraries
License:    ASL 2.0
URL:        https://twiki.cnaf.infn.it/twiki/bin/view/VOMS

Source:     %{name}-%{version}.tar.gz
BuildRoot:  %{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)

BuildArch:	noarch

%description

The Virtual Organization Membership Service (VOMS) is an attribute authority
which serves as central repository for VO user authorization information,
providing support for sorting users into group hierarchies, keeping track of
their roles and other attributes in order to issue trusted attribute
certificates and SAML assertions used in the Grid environment for 
authorization purposes.

This package offers a java client API for VOMS.

%prep
%setup -q

%build

%install
rm -rf $RPM_BUILD_ROOT

%clean
rm -rf $RPM_BUILD_ROOT

%files
%defattr(-,root,root,-)
{_javadir}/voms-java-api.jar
{_javadir}/voms-java-api-%{version}.jar
{_javadir}/vomsjapi.jar

%doc AUTHORS LICENSE

%changelog 
* Fri Nov  4 2011 Andrea Ceccanti <andrea.ceccanti@cnaf.infn.it> - 2.0.7-1
- First maven-based repackaging 
