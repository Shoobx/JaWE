<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:d="http://docbook.org/ns/docbook"
				xmlns:exsl="http://exslt.org/common"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:ng="http://docbook.org/docbook-ng"
                xmlns:db="http://docbook.org/ns/docbook"
                exclude-result-prefixes="db ng exsl d"
                version='1.0'>
				
<xsl:include href="coverdetail.xsl"/>				
<xsl:include href="keepwith.xsl"/>
<xsl:include href="pagebreak.xsl"/>
<xsl:include href="../docbook-xsl/xsl/fo/docbook.xsl"/>
				
</xsl:stylesheet>