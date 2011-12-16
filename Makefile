name=voms-api-java
spec=spec/$(name).spec
version=$(shell grep "Version:" $(spec) | sed -e "s/Version://g" -e "s/[ \t]*//g")
release=1
rpmbuild_dir=$(shell pwd)/rpmbuild

.PHONY: etics clean rpm

all: 	rpm

clean:	
		rm -rf $(rpmbuild_dir) tgz RPMS 
		
rpm:	
		mkdir -p 	$(rpmbuild_dir)/BUILD $(rpmbuild_dir)/RPMS \
					$(rpmbuild_dir)/SOURCES $(rpmbuild_dir)/SPECS \
					$(rpmbuild_dir)/SRPMS
		cp target/$(name)-$(version)-src.tar.gz $(rpmbuild_dir)/SOURCES
		rpmbuild 	--nodeps -v -ba $(spec) \
					--define "_topdir $(rpmbuild_dir)"

etics: 	clean rpm
		mkdir -p tgz RPMS
		cp -r $(rpmbuild_dir)/RPMS/* $(rpmbuild_dir)/SRPMS/* RPMS
