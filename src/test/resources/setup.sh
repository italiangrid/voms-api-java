#!/bin/bash

set -e

if [ ! -e "openssl.conf" ]; then
  >&2 echo "The configuration file 'openssl.conf' doesn't exist in this directory"
  exit 1
fi

base_dir=$(cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd)
certs_dir="${base_dir}"/certs
ta_dir="${base_dir}"/trust-anchors
sha1_ta_dir="${base_dir}"/sha1-trust-anchors
md5_ta_dir="${base_dir}"/md5-trust-anchors
vomsdir="${base_dir}"/vomsdir

rm -rf certs/ trust-anchors/ md5-trust-anchors/ sha1-trust-anchors/ perm-test/ homes/pem-creds/ homes/pkcs12-creds/

# rm -rf "${certs_dir}"
mkdir -p "${certs_dir}"
# rm -rf "${ta_dir}"
mkdir -p "${ta_dir}"
# rm -rf "${sha1_ta_dir}"
mkdir -p "${sha1_ta_dir}"
# rm -rf "${md5_ta_dir}"
mkdir -p "${md5_ta_dir}"
# rm -rf "${vomsdir}"
mkdir -p "${vomsdir}"

[ -d "igi_test_ca" ] && remove_ca.sh igi_test_ca
export CA_NAME=igi_test_ca
make_ca.sh

for i in $(seq 0 5); do
  make_cert.sh test${i}
  cp igi_test_ca/certs/test${i}.* "${certs_dir}"
done

mkdir -p homes/pem-creds/.globus
cp certs/test0.*.pem homes/pem-creds/.globus
ln -sf test0.cert.pem homes/pem-creds/.globus/usercert.pem
ln -sf test0.key.pem  homes/pem-creds/.globus/userkey.pem

mkdir -p homes/pkcs12-creds/.globus
cp certs/test0.p12 homes/pkcs12-creds/.globus
ln -sf test0.p12   homes/pkcs12-creds/.globus/usercred.p12

mkdir -p perm-test
cp certs/test0.* perm-test
chmod 664 perm-test/test0.cert.pem
chmod 400 perm-test/test0.key.pem
chmod 600 perm-test/test0.p12
cp certs/test1.* perm-test
chmod 664 perm-test/test1.cert.pem
chmod 777 perm-test/test1.key.pem
chmod 777 perm-test/test1.p12
cp certs/test2.key.pem perm-test
chmod 600 perm-test/test2.key.pem

for c in revoked test_host_cnaf_infn_it test_host_2_cnaf_infn_it wilco_cnaf_infn_it; do
  make_cert.sh ${c}
  cp igi_test_ca/certs/${c}.* "${certs_dir}"
done
revoke_cert.sh revoked

faketime -f -1y make_cert.sh expired
cp igi_test_ca/certs/expired.* "${certs_dir}"

make_crl.sh
install_ca.sh igi_test_ca "${ta_dir}"
ca_subject_sha1=$(openssl x509 -in "${ta_dir}/igi_test_ca.pem" -noout -subject_hash)
ca_subject_md5=$(openssl x509 -in "${ta_dir}/igi_test_ca.pem" -noout -subject_hash_old)

cp ${ta_dir}/${ca_subject_sha1}.* "${sha1_ta_dir}"
cp ${ta_dir}/${ca_subject_md5}.* "${md5_ta_dir}"

mkdir -p "${vomsdir}"/test.vo
cp ${certs_dir}/test_host_cnaf_infn_it.cert.pem ${vomsdir}/test-host.cnaf.infn.it.pem

mkdir -p vomsdir-expired-aa-cert
cp ${certs_dir}/expired.cert.pem vomsdir-expired-aa-cert

mkdir -p vomsdir-fake-aa-cert
cp ${certs_dir}/test_host_2_cnaf_infn_it.cert.pem vomsdir-fake-aa-cert

# openssl x509 -in "${certs_dir}"/star_test_example.cert.pem -noout -subject -issuer -nameopt compat \
#   | sed -e 's/subject=//' -e 's/issuer=//' > "${vomsdir}"/test.vo/voms.example.lsc

