SUMMARY = "Linux kernel for ${MACHINE}"
SECTION = "kernel"
LICENSE = "GPLv2"

MACHINE_KERNEL_PR_append = ".0"

inherit kernel machine_kernel_pr

SRCDATE = "20150510"

SRC_URI[md5sum] = "40a1569a03da39935c596b726e4ee39a"
SRC_URI[sha256sum] = "b53fb490a8e23bacb6f2b9a9528cb5785004be2b5854c3b55c0a5b68fc67a154"

LIC_FILES_CHKSUM = "file://${S}/COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"


# By default, kernel.bbclass modifies package names to allow multiple kernels
# to be installed in parallel. We revert this change and rprovide the versioned
# package names instead, to allow only one kernel to be installed.
PKG_kernel-base = "kernel-base"
PKG_kernel-image = "kernel-image"
RPROVIDES_kernel-base = "kernel-${KERNEL_VERSION}"
RPROVIDES_kernel-image = "kernel-image-${KERNEL_VERSION}"

SRC_URI += "http://source.mynonpublic.com/xcore/linux-brcmstb-${PV}-${SRCDATE}.tar.gz \
    file://defconfig \
    "

S = "${WORKDIR}/linux-brcmstb-${PV}"

inherit kernel

export OS = "Linux"
KERNEL_OBJECT_SUFFIX = "ko"
KERNEL_OUTPUT = "vmlinuz"
KERNEL_IMAGETYPE = "vmlinuz"
KERNEL_IMAGEDEST = "/tmp"

FILES_kernel-image = "${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}"

kernel_do_install_append() {
    install -d ${D}${KERNEL_IMAGEDEST}
    install -m 0755 ${KERNEL_OUTPUT} ${D}${KERNEL_IMAGEDEST}
}

MTD_DEVICE_inihdx = "mtd1"
MTD_DEVICE_mbtwin = "mtd1"
MTD_DEVICE_bcm7358 = "mtd1"
MTD_DEVICE_inihde = "mtd2"
MTD_DEVICE_vp7358 = "mtd1"

pkg_postinst_kernel-image () {
    if [ "x$D" == "x" ]; then
        if [ -f /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}.gz ] ; then
            flash_erase /dev/${MTD_DEVICE} 0 0
            nandwrite -p /dev/${MTD_DEVICE} /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}.gz
            rm -f /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}.gz
        fi
    fi
    true
}