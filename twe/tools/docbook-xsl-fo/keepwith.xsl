<?xml version='1.0'?>
<xsl:stylesheet version="1.0" 
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:fo="http://www.w3.org/1999/XSL/Format">


<xsl:template match="processing-instruction('keepwith')">
	<fo:block keep-with-previous='always' keep-with-next='always'/>
</xsl:template>

</xsl:stylesheet>