# Together Workflow Editor 
# Copyright (C) 2011 Together Teamsolutions Co., Ltd.
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or 
# (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful, 
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program. If not, see http://www.gnu.org/licenses
#
#######################################################################

#!/bin/bash

MWD=$(dirname $0)
version=$1
release=$2
buildtype=$3
buildid=$4
nameadditional=$5
version=${version:=2.0}
release=${release:=beta1}
buildtype=${buildtype:=community}
nameadditional=${nameadditional:=}
buildid=${buildid:=$(date +%Y%m%d-%H%M)}
name=twe
prefix=/usr/local

BID=
if test -n "$buildid"; then
   BID=$buildid
else
   BID=$(date +%Y%m%d-%H%M)
fi

# prepare RPM build environment
if [ ! -x $JAVA_HOME/bin/javac ]; then
    echo JAVA_HOME not set properly \"$JAVA_HOME\"
    exit 1
fi
test -w $PWD/$MWD/rpm && rm -fr $PWD/$MWD/rpm
if [ ! -w  ${RPM_ROOT:=$PWD/$MWD/rpm}/SOURCES ]; then
    if [ -w ${PWD}/${MWD} ]; then
	mkdir -p \
	    ${RPM_ROOT}/BUILD \
	    ${RPM_ROOT}/BUILDROOT \
	    ${RPM_ROOT}/RPMS/i386 \
	    ${RPM_ROOT}/RPMS/i686 \
	    ${RPM_ROOT}/RPMS/noarch \
	    ${RPM_ROOT}/SOURCES \
	    ${RPM_ROOT}/SPECS \
	    ${RPM_ROOT}/SRPMS \
	    ${RPM_ROOT}/tmp
	test -f ${HOME}/.rpmmacros && mv ${HOME}/.rpmmacros ${RPM_ROOT}
	cat > ${HOME}/.rpmmacros <<EOF
%_topdir        %(echo ${RPM_ROOT})
%_tmppath       %{_topdir}/tmp
EOF
    else
	echo Failed to write $RPM_ROOT/SOURCES and ${RPM_ROOT}/..
	exit 1
    fi
fi
cat >$RPM_ROOT/SPECS/twe.spec <<EOF
Summary: Graphical XPLD editor
Name: $name${nameadditional}
Version: $version
Release: $release
Vendor: Together Teamsolutions Co., Ltd.
URL: http://www.together.at/prod/workflow/twe
Source: %{name}-%{version}-%{release}.src.tar.gz
License: GPL 3
Group: Development/Tools
Icon: TWE.xpm
AutoReqProv: no
BuildRoot: /tmp/%name%nameadditional-%version-%release.build_root

# Relocatable version:
Prefix: $prefix
Requires: rpm > 4.0.2

%description
Together Workflow Editor (TWE) is a graphical application for Process Definition Modelling.
It is compatible with WfMC specification - XPDL (XML Process Definition Language) version 1.0 - 102502.

%prep
%setup -q -n $name$nameadditional-$version-$release.src

cp $PWD/build.properties $RPM_ROOT/BUILD/$name$nameadditional-$version-$release

%build
    while
        echo -e "\n\nPlease enter path to your "\
            "Java Development Kit - JDK directory [/usr/java/jdk1.6.0_21] \n\n"
        read jdkdir
        jdkdir=\${jdkdir:=/usr/java/jdk1.6.0_21}
        [ ! -d \$jdkdir ]
    do                                echo "Warning: No such directory: \$jdkdir"
    done
    JAVA_HOME=\${jdkdir}
    export JAVA_HOME
    export PATH=\$PATH:\$JAVA_HOME/bin
    chmod +x ./configure
    ./configure --jdkhome=\$JAVA_HOME
    make buildOutput

%install
    rm -rf \$RPM_BUILD_ROOT
    mkdir -p \$RPM_BUILD_ROOT/%prefix \$RPM_BUILD_ROOT/usr
    cp -pr output/* \$RPM_BUILD_ROOT/%prefix
    [ "x" != "${nameadditional}x" ] && mv \$RPM_BUILD_ROOT/%prefix/${name}-%{version}-%{release} \$RPM_BUILD_ROOT/%prefix/%{name}-%{version}-%{release}
    cp -pr installation/unix/usr/* \$RPM_BUILD_ROOT/usr
    sed -e 's/@na@/$nameadditional/g' -e 's/@v@/$version/g' -e 's/@r@/$release/g' \$RPM_BUILD_ROOT/usr/share/applications/togwe.desktop > \$RPM_BUILD_ROOT/usr/share/applications/togwe2.desktop
    mv -f \$RPM_BUILD_ROOT/usr/share/applications/togwe2.desktop \$RPM_BUILD_ROOT/usr/share/applications/togwe.desktop

%clean
rm -rf \$RPM_BUILD_ROOT

%files
%defattr(-,root,root)
$prefix/%{name}-%{version}-%{release}
/usr/share/applications/togwe.desktop
/usr/share/icons/togwe_48.png
/usr/share/pixmaps/togwe_48.png

%post
    if
           ! [[ x\$JAVA_HOME != x &&  -d \$JAVA_HOME ]]
    then
           if
                 JAVA_HOME=\$(which java)
                 [[ x\$JAVA_HOME != x ]]
           then
                 JAVA_HOME=\$(dirname \$JAVA_HOME)/..
           else
                 JAVA_HOME=\$(ls -d /usr/java/j*/bin/..|head -1 2>/dev/null)
           fi
    fi
    if
           ! [[ x\$JAVA_HOME != x &&  -d \$JAVA_HOME ]]
    then
       default_JAVA_HOME=\${default_JAVA_HOME:=/usr/java/jdk1.6.0_21}
       while
            echo -e "\n\nPlease enter path to your "\
            "Java Development Kit - JDK directory [\$default_JAVA_HOME] \c"
            read JAVA_HOME </dev/tty
            JAVA_HOME=\${JAVA_HOME:=\$default_JAVA_HOME}
            [ ! -d \$JAVA_HOME ]
       do
            echo "Warning: No such directory: \$JAVA_HOME"
       done
    fi
    JAVA_HOME=\$(cd \$JAVA_HOME; echo \$PWD)
    export JAVA_HOME

    cd \$RPM_INSTALL_PREFIX/%{name}-%{version}-%{release}
    chmod +x ./configure.sh
    ./configure.sh --jdkhome=\$JAVA_HOME
cat >/usr/share/applications/togwe.desktop <<DESKTOP

[Desktop Entry]
Exec=\$PWD/bin/run.sh
Icon=togwe_48
Terminal=false
Type=Application
Categories=Development;GNOME;GTK;Applications;X-Fedora;
StartupNotify=true
X-Desktop-File-Install-Version=0.15
MimeType=text/xml;text/plain;
Name=Together Workflow Editor
GenericName= XPDL editor
Comment=Compose, edit, and view XPDL 1.0 documents

DESKTOP
%postun
if
	[[ \$PWD !=  %prefix/%name%nameadditional-%version-%release ]]
then
	rm -rf  %prefix/%name%nameadditional-%version-%release
fi

EOF

#
#	Changed by Stefanovic Nenad 09.09.2003.
#
mkdir -p distribution/${name}-${version}-${release}_${BID}/$buildtype

mv ./distribution/${name}${nameadditional}-${version}-${release}.src.tar.gz ./distribution/${name}-${version}-${release}_${BID}/${buildtype}/${name}${nameadditional}-${version}-${release}.src.tar.gz || exit 1
cp ./distribution/${name}-${version}-${release}_${BID}/${buildtype}/${name}${nameadditional}-${version}-${release}.src.tar.gz ${RPM_ROOT}/SOURCES/${name}${nameadditional}-${version}-${release}.src.tar.gz || exit 1

cp input/bin/TWE.xpm ${RPM_ROOT}/SOURCES

echo $JAVA_HOME|rpmbuild -ba --define "_topdir ${RPM_ROOT}" --define "_tmppath ${RPM_ROOT}/tmp" --verbose --target noarch $RPM_ROOT/SPECS/twe.spec
cp ${RPM_ROOT}/RPMS/noarch/$name${nameadditional}-${version}-${release}.noarch.rpm distribution/${name}-${version}-${release}_${BID}/$buildtype || exit 1
cp ${RPM_ROOT}/SRPMS/$name${nameadditional}-${version}-${release}.src.rpm distribution/${name}-${version}-${release}_${BID}/$buildtype

mkdir -p tmp
cd tmp
rpm2cpio ${RPM_ROOT}/RPMS/noarch/$name${nameadditional}-${version}-${release}.noarch.rpm|cpio -id
# Adding read priviliges and taring
find . -type f -a -exec chmod a+r {} \;
find . -type d -a -exec chmod a+r {} \;
cd usr/local
tar czf ../../../distribution/${name}-${version}-${release}_${BID}/$buildtype/$name${nameadditional}-${version}-${release}.tar.gz .
cd ../../..
rm -fr tmp

if [ $buildtype = 'community' ]; then
	if [ -f licenses/License-TOG.txt ]; then
		rm -fr installation/Unix/rpm
		$0 $1 $2 twe $BID -tsl

	fi 
fi