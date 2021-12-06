# Taken from https://gitlab.com/gitlab-org/build/CNG/-/blob/master/alpine-certificates/scripts/bundle-certificates

# purge any existing state
echo "Purging old certificates"
if [ $(ls -1 /etc/ssl/certs/ | wc -l) -gt 0 ]; then
  ls /etc/ssl/certs/ | grep -v java | xargs -I placeholder rm -v /etc/ssl/certs/placeholder 
fi

# folder for java certificates needs to be created so that update-ca-certificates work
mkdir -p /etc/ssl/certs/java

echo "Updating certificates"
# Update the CA certificates store in /etc/ssl/certs
# - generated hashes
# - symlinks to various external locations
# - compiles ca-certificates.crt
update-ca-certificates


echo "De-reference symlinks"
# De-reference all symlinks that point outside of /etc/ssl/certs
# This is required because the /etc/ssl/ folder is actually a volume that
# will be mounted in other containers after the certificates are generated.
for f in /etc/ssl/certs/*.pem /etc/ssl/certs/*.crt ; do
  # read the symlinks origin
  origin=$(readlink -f $f) ;
  originPath=${origin%/*} ;
  # if outside of /etc/ssl/certs, cp to /etc/ssl/certs in place of symlink
  if [ "$originPath" != "/etc/ssl/certs" ]; then
    rm $f ;
    cp $origin $f
  fi
done
