#!/bin/bash

# 帮助
function help {
    echo "Usage: ./entrypoint.sh [compile|run] <BASE64 data>"
    echo "Examples:"
    echo "  ./entrypoint.sh compile <BASE64 file content>"
    echo "  ./entrypoint.sh run <BASE64 args>"
    exit 1
}

function decode_base64 {
  echo $1 | base64 -d
  if [ $? -ne 0 ]; then
    echo "Error: Failed to decode base64 data."
    exit 1
  fi
}

if [ -z $1 ]; then
	help
else
	if [ $1 != "compile" ] && [ $1 != "run" ]; then
		help
	else
		if [ $1 == "compile" ] && [ -z $2 ]; then
			help
		fi
	fi
fi

if [ $1 == "compile" ]; then
	decode_base64 "$2" > Main.java
	javac -encoding utf-8 Main.java
fi

if [ $1 == "run" ]; then
	decode_base64 "$2" | java Main

fi
