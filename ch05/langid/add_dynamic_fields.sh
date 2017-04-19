#!/bin/sh

curl -X POST -H 'Content-type:application/json' --data-binary '{
  "add-dynamic-field":{
     "name":"*_en", "type":"text_en", "indexed":true, "stored":true }
}' http://localhost:8983/solr/multi_lang/schema

curl -X POST -H 'Content-type:application/json' --data-binary '{
  "add-dynamic-field":{
     "name":"*_de", "type":"text_de", "indexed":true, "stored":true }
}' http://localhost:8983/solr/multi_lang/schema

curl -X POST -H 'Content-type:application/json' --data-binary '{
  "add-dynamic-field":{
     "name":"*_fr", "type":"text_fr", "indexed":true, "stored":true }
}' http://localhost:8983/solr/multi_lang/schema

curl -X POST -H 'Content-type:application/json' --data-binary '{
  "add-dynamic-field":{
     "name":"*_ja", "type":"text_ja", "indexed":true, "stored":true }
}' http://localhost:8983/solr/multi_lang/schema
