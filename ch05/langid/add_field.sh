#!/bin/sh

curl -X POST -H 'Content-type:application/json' --data-binary '{
  "add-field":{
     "name":"language_s", "type":"string", "stored":true }
}' http://localhost:8983/solr/multi_lang/schema
