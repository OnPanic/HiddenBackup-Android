#!/usr/bin/env bash

API=16
GCC=4.8
NDK=$(cat local.properties |grep ndk.dir|awk -F '=' '{print $2}')
SYSPREFIX="${NDK}/platforms/android-${API}/arch-"
ARCH=arm
PREFIX=arm-linux-androideabi-
EXTERNAL_ROOT=$(pwd)/external
RSYNC_INSTALL_DIR=$(pwd)/app/src/main/res/raw/

export CC="${NDK}/toolchains/${PREFIX}${GCC}/prebuilt/linux-x86_64/bin/${PREFIX}gcc --sysroot=${SYSPREFIX}${ARCH}"
export CROSS_SYSROOT="${SYSPREFIX}${ARCH}"
export LDFLAGS=--sysroot="${SYSPREFIX}${ARCH}"

# Build dir
[ -d $EXTERNAL_ROOT ] || mkdir $EXTERNAL_ROOT
[ -d $EXTERNAL_ROOT/lib ] || mkdir $EXTERNAL_ROOT/lib
[ -d $EXTERNAL_ROOT/include/openssl ] || mkdir -p $EXTERNAL_ROOT/include/openssl
[ -d $RSYNC_INSTALL_DIR ] || mkdir $RSYNC_INSTALL_DIR

# Install dirs
[ -d $EXTERNAL_ROOT/rsync ] || git clone git://git.samba.org/rsync.git $EXTERNAL_ROOT/rsync
[ -d $EXTERNAL_ROOT/ssl ] || git clone https://github.com/openssl/openssl.git $EXTERNAL_ROOT/ssl
[ -d $EXTERNAL_ROOT/ssh ] || git clone https://github.com/openssh/openssh-portable.git $EXTERNAL_ROOT/ssh
[ -d $EXTERNAL_ROOT/zlib ] || wget http://zlib.net/zlib-1.2.11.tar.gz -O - | tar xzf - -C $EXTERNAL_ROOT
mv $EXTERNAL_ROOT/zlib-1.2.11 $EXTERNAL_ROOT/zlib

# Build Zlib
cd $EXTERNAL_ROOT/zlib-1.2.11
make clean
./configure --static
make
cp libz.a $EXTERNAL_ROOT/lib

# Build Ssl
cd $EXTERNAL_ROOT/ssl
make clean
./Configure android no-rc4 -D_MIPS_SZLONG=64 -DL_ENDIAN -I${SYSPREFIX}${ARCH}/usr/include -L${SYSPREFIX}${ARCH}/usr/lib
make depend
make build_libs
cp libcrypto.a $EXTERNAL_ROOT/lib/libcrypto.a
cp libssl.a $EXTERNAL_ROOT/lib/libssl.a
cp include/openssl/* $EXTERNAL_ROOT/include/openssl/

# Build Rsync
cd $EXTERNAL_ROOT/rsync
make distclean
support/git-set-file-times
./prepare-source
make clean
./configure CFLAGS="-static" --host="${ARCH}"
make
${NDK}/toolchains/${PREFIX}${GCC}/prebuilt/linux-x86_64/bin/${PREFIX}strip rsync
cp rsync $RSYNC_INSTALL_DIR


