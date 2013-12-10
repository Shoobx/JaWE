<?xml version='1.0'?>
<xsl:stylesheet version="1.0" 
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xmlns:fo="http://www.w3.org/1999/XSL/Format">
				
<xsl:template match="processing-instruction('coverdetail')">
	<fo:block font-family='sans-serif,Symbol,ZapfDingbats' font-weight='bold' font-style='normal'
			  font-size='24.000pt' text-align='center' space-before='15.552pt'>
		@doctitle@
	</fo:block>
	<fo:block/>
    <fo:block font-family='sans-serif,Symbol,ZapfDingbats' font-weight='bold' font-style='normal'
			  font-size='18.000pt' text-align='center' space-before='15.552pt'>
		@buildid@
	</fo:block>
	<fo:block/>
</xsl:template>				
				
</xsl:stylesheet>