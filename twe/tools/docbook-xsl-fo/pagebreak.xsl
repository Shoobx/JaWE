<?xml version='1.0'?>
<xsl:stylesheet version="1.0" 
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:fo="http://www.w3.org/1999/XSL/Format">

<xsl:template match="processing-instruction('breakbefore')">
	<fo:block break-before='page'/>
</xsl:template>

<xsl:template match="processing-instruction('breakafter')">
	<fo:block break-after='page'/>
</xsl:template>

</xsl:stylesheet>