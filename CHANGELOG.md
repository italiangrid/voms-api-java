<!--
SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare

SPDX-License-Identifier: Apache-2.0
-->

# Changelog

## 3.4.0 (2026-09-28)

### Changed

* Change the default hash algorithm for OpenSSL-compatible trust-directory
  filenames from MD5 to SHA-1. MD5 remains available through
  `CertificateValidatorBuilder` for compatibility with legacy trust directories.
* Upgrade Bouncy Castle from 1.84 to 1.86.
* Upgrade CANL from 2.8.3 to 2.9.0.
* Replace calls to the deprecated `getKeyIdentifier()` method with
  `getKeyIdentifierOctets()`.
* Remove the redundant `VOMSProtocol` interface declaration from
  `LegacyProtocol`, which already inherits it through `AbstractVOMSProtocol`.

### Build and CI

* Upgrade JUnit Jupiter to 6.1.3.

## 3.3.8 (2026-04-21)

### Changed

* Bump to BC 1.84

## 3.3.7 (2025-12-18)

### Changed

* Support for AlmaLinux 10
* Signed RPMs on VOMS repository
* Support loading LSC files for selected VOs

## 3.3.6 (2025-07-31)

### Changed

* Bump to BC 1.81
* Fix ASN.1 encoding of policy authority info

## 3.3.5 (2025-04-10)

### Changed

* Bump to CANL 2.8.3 and BC 1.80

## 3.3.4 (2025-04-04)

### Changed

* Bump to Java 17, CANL 2.7.0 and BC 1.69

### Fixed

* Fix multi-chain LSC files by reading only the first chain
