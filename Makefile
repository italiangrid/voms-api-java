name=voms-api-java
spec=spec/$(name).spec
version=$(shell grep "Version:" $(spec) | sed -e "s/Version://g" -e "s/[ \t]*//g")
release=1
rpmbuild_dir=$(shell pwd)/rpmbuild
tarbuild_dir=$(shell pwd)/tarbuild
stage_dir=dist
settings_file=src/config/emi-build-settings.xml

.PHONY: stage etics clean rpm

all: 	dist rpm

prepare-spec:
		sed -e 's#@@BUILD_SETTINGS@@#$(settings_file)#g' \
			spec/voms-api-java.spec.in > spec/voms-api-java.spec

prepare-sources: prepare-spec
		rm -rf 	$(tarbuild_dir)
		mkdir -p $(tarbuild_dir)/$(name)
		cp -r AUTHORS LICENSE Makefile README.md pom.xml spec src $(tarbuild_dir)/$(name)
		cd $(tarbuild_dir) && tar cvzf $(tarbuild_dir)/$(name)-$(version).tar.gz .

clean:	
		rm -rf target $(rpmbuild_dir) $(tarbuild_dir) tgz RPMS dir spec/voms-api-java.spec

dist:   prepare-spec

rpm:		
		mkdir -p 	$(rpmbuild_dir)/BUILD $(rpmbuild_dir)/RPMS \
					$(rpmbuild_dir)/SOURCES $(rpmbuild_dir)/SPECS \
					$(rpmbuild_dir)/SRPMS

		cp target/$(name)-$(version)-src.tar.gz $(rpmbuild_dir)/SOURCES/$(name)-$(version).tar.gz
		rpmbuild --nodeps -v -ba $(spec) --define "_topdir $(rpmbuild_dir)" 

etics: 	dist rpm
		mkdir -p tgz RPMS
		cp target/*.tar.gz tgz
		cp -r $(rpmbuild_dir)/RPMS/* $(rpmbuild_dir)/SRPMS/* RPMS

stage:	 
	mkdir -p $(stage_dir)
	
	for r in $(shell find $(rpmbuild_dir)/RPMS -name '*.rpm') ; do \
		echo "Istalling `basename $$r` in $(stage_dir)..."; \
		pushd . ; cp $$r $(stage_dir); cd $(stage_dir); \
		rpm2cpio `basename $$r` | cpio -idm; \
		rm `basename $$r`; popd; \
	done
