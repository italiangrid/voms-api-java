name=voms-api-java
spec=spec/$(name)3.spec
pom=pom.xml
version=$(shell grep "Version:" $(spec) | sed -e "s/Version://g" -e "s/[ \t]*//g")
pom_version=$(shell grep "<version>" $(pom) | head -1 | sed -e 's/<version>//g' -e 's/<\/version>//g' -e "s/[ \t]*//g")
release=1
rpmbuild_dir=$(shell pwd)/rpmbuild
tarbuild_dir=$(shell pwd)/tarbuild
stage_dir=dist
#mvn_settings=-s src/config/emi-build-settings.xml
mvn_settings=\%{nil}

.PHONY: stage etics clean rpm

all: 	dist rpm

prepare-spec:
		sed -e 's#@@MVN_SETTINGS@@#$(mvn_settings)#g' \
			-e 's#@@POM_VERSION@@#$(pom_version)#g' \
			spec/voms-api-java.spec.in > $(spec)

prepare-sources: prepare-spec
		rm -rf 	$(tarbuild_dir)
		mkdir -p $(tarbuild_dir)/$(name)
		cp -r AUTHORS LICENSE Makefile README.md pom.xml spec src $(tarbuild_dir)/$(name) 
		cd $(tarbuild_dir) && tar cvzf $(tarbuild_dir)/$(name)-$(version).tar.gz $(name)

clean:	
		rm -rf target $(rpmbuild_dir) $(tarbuild_dir) tgz RPMS dir $(spec)

dist:   prepare-sources

rpm:		
		mkdir -p 	$(rpmbuild_dir)/BUILD $(rpmbuild_dir)/RPMS \
					$(rpmbuild_dir)/SOURCES $(rpmbuild_dir)/SPECS \
					$(rpmbuild_dir)/SRPMS

		cp $(tarbuild_dir)/$(name)-$(version).tar.gz $(rpmbuild_dir)/SOURCES/$(name)-$(version).tar.gz
		rpmbuild --nodeps -v -ba $(spec) --define "_topdir $(rpmbuild_dir)" 
