#!/bin/bash

keytool -genkey -dname "cn=Luca Mari, ou=LM, o=Universit� Cattaneo - LIUC, c=IT" -alias stgk -keypass lucamari00 -keystore ./stgks -storepass lucamari00 -validity 365
