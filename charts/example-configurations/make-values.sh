#!/bin/bash

## Default values
FILE=renku-values.yaml

usage()
{
#	echo -e "This script will help you configure a valid renku values file. \nYou need to provide the domain, other arguments are optional."
#	echo		"If no Gitlab URL is provided, Gitlab will be included in the deployment values."
#	echo    "If Gitlab URL is provided, the application ID and secret need to be provided as well."
  echo -e "Usage: ./make-values.sh [ -d | --domain ] \n\t\t\t[ -o | --output-file ] \n\t\t\t[ -g | --gitlab-domain ] [ -i | --gitlab-appid ] [ -s | --gitlab-appsecret ]"
	exit 2
}


## Parse arguments
PARSED_ARGUMENTS=$(getopt -a -n make-values -o o:d:g:i:s: --long output-file:,domain:,gitlab-domain:,gitlab-appid:,gitlab-appsecret: -- "$@")
VALID_ARGUMENTS=$?
if [ "$VALID_ARGUMENTS" != "0" ]; then
  usage
fi

# echo "PARSED_ARGUMENTS is $PARSED_ARGUMENTS"
eval set -- "$PARSED_ARGUMENTS"
while :
do
  case "$1" in
    -o | --output-file)      FILE="$2"              ; shift 2 ;;
    -d | --domain)           DOMAIN="$2"            ; shift 2 ;;
    -g | --gitlab-domain)    GITLABDOMAIN="$2"      ; shift 2 ;;
    -i | --gitlab-appid)     GITLABAPPID="$2"       ; shift 2 ;;
		-s | --gitlab-appsecret) GITLABAPPSECRET="$2"   ; shift 2 ;;
    --) shift; break ;;
    *) echo "Unexpected option: $1 - this should not happen."
       usage ;;
  esac
done

#echo "FILE   : $FILE"
#echo "DOMAIN : $DOMAIN "

cp minimal-values.tmpl $FILE

## Configure domain
if [ -z "$DOMAIN" ]; then
	echo "No domain provided"
  usage
fi

sed -i "s/\[domain\]/$DOMAIN/g" $FILE
ingresstls=`echo ${DOMAIN}-tls | tr . -`
sed -i "s/\[ingress-tls\]/$ingresstls/g" $FILE


## Generate random strings for secrets

search=`grep "\[random" $FILE`

while [ -n "$search" ]
do
	pass=`grep -m 1 "\[random" $FILE`
	#echo "Generating secret for $pass"
	if [[ "$pass" == *"base64"* ]];
	then
		secret=`openssl rand -hex 8|base64`
		toreplace="\[randombase64\]"
  else
		secret=`openssl rand -hex 32`
		toreplace="\[random\]"
  fi
	sed -i "0,/$toreplace/s//$secret/" $FILE
	search=`grep "random" $FILE`
done

## Configure gitlab
if [ -z "$GITLABDOMAIN" ];
then
	echo "No external Gitlab provided, including it in this deployment"
	GITLABDOMAIN="$DOMAIN\/gitlab"
	gitlabenabled="true"
	gitlabprefix="\/gitlab"
	gitlabregistry="registry.$DOMAIN"
	registryingresstls=`echo registry-${DOMAIN}-tls | tr . -`

	GITLABAPPID=`openssl rand -hex 8|base64`
	sed -i "s/\[gitlab-application-clientID\]/$GITLABAPPID/g" $FILE
	GITLABAPPSECRET=`openssl rand -hex 8|base64`
	sed -i "s/\[gitlab-application-secret\]/$GITLABAPPSECRET/g" $FILE

else
	echo "External Gitlab provided $GITLABDOMAIN"

	if [ -z "$GITLABAPPID" ] || [ -z "$GITLABAPPSECRET" ]; then
		echo "When deploying against an external Gitlab, you need to provide an application ID and secret"
	  usage
	fi
	gitlabenabled="false"
	gitlabprefix="\/"
	gitlabregistry="registry.$GITLABDOMAIN"
	registryingresstls=`echo registry-${GITLABDOMAIN}-tls | tr . -`

	sed -i "s/\[gitlab-application-clientID\]/$GITLABAPPID/g" $FILE
	sed -i "s/\[gitlab-application-secret\]/$GITLABAPPSECRET/g" $FILE

fi

sed -i "s/\[gitlab-domain\]/$GITLABDOMAIN/g" $FILE
sed -i "s/\[gitlab-enabled\]/$gitlabenabled/g" $FILE
sed -i "s/\[gitlab-prefix\]/$gitlabprefix/g" $FILE
sed -i "s/\[gitlab-registry\]/$gitlabregistry/g" $FILE
sed -i "s/\[registry-ingress-tls\]/$registryingresstls/g" $FILE

echo "Your new renku values file is at $FILE"
