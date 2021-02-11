#!/bin/bash
#find all the projects
rm -Rf ./rules/prj_*
for i in ` find .. -mindepth 1 -maxdepth 1 -type d | grep prj  | awk -F "/" '{ print $2 }'`;do
   mkdir -p ./rules/$i
   cp -rp ../$i/rules ./rules/$i/
  echo $i
done

customercode=${1}
mode=${2}

if [ -z "$customercode" ]
then
      customercode=dev1 
fi

echo "DevCode = ${customercode}"

#./run-setup.sh -p ${customercode} -r ${customercode} -n 10.123.123.123 ${mode}

os_type=""
if [[ "$OSTYPE" == "linux-gnu" ]]; then
    os_type="ubuntu"
elif [[ "$OSTYPE" == "darwin"* ]]; then
    os_type="mac"
fi

echo "OS Type is ${os_type}"

if [[ "${os_type}" == "mac" ]]; then
    IP=$(ifconfig | grep 10.123.123.123)
elif [[ "${os_type}" == "ubuntu" ]]; then
    IP=$(ip a | grep 10.123.123.123)
fi
echo $IP

#if ping -c 1 10.123.123.123 &> /dev/null
if [[ -z "$IP" ]]; then
    if [[ ${os_type} == "mac" ]]; then
        echo "OS TYPE: macOS"
        sudo ifconfig lo0 alias 10.123.123.123
    elif [[ ${os_type} == "ubuntu" ]]; then
        echo "OS TYPE: Linux"
        sudo ip address add 10.123.123.123 dev lo
    else
        echo "Cannot detect OS type, neither Linux nor macOS!"
        exit -1
    fi
else
    echo "10.123.123.123 exists"
fi

GENNY_ENV_DIR="$HOME/.genny"
cd ..
GENNY_DIR=$PWD
cd scripts 
#GENNY_DIR="~/projects/genny"
CREDENTIALS_DIR="$HOME/.genny/credentials"
CREDENTIALS="credentials"
cat $CREDENTIALS_DIR/credentials-$customercode/conf.env > ./genny.env
ENV_FILE=genny.env
$repo_url
$project
$ip
$project_realm

if [ ! -d "$CREDENTIALS_DIR" ]; then
    mkdir -p $CREDENTIALS_DIR
fi


ENV_FILE=$ENV_FILE docker-compose up -d
