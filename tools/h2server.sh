#!/bin/bash
PATH=~/jdk-25.0.2/bin/:$PATH
java -jar tools/h2-2.4.240.jar -url jdbc:h2:./bank;MODE=Oracle;DEFAULT_NULL_ORDERING=HIGH
