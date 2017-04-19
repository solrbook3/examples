function processAdd(cmd) {
    doc = cmd.solrDoc;  // org.apache.solr.common.SolrInputDocument
    ctype = doc.getFieldValue("content_type")

    if (ctype.indexOf("text/html; charset=", 0) != -1) {
	charset = ctype.substr(19)
	doc.addField("charset", charset)
    }
}

function processDelete(cmd) {
  // no-op
}

function processMergeIndexes(cmd) {
  // no-op
}

function processCommit(cmd) {
  // no-op
}

function processRollback(cmd) {
  // no-op
}

function finish() {
  // no-op
}
