<?xml version="1.0" encoding="UTF-8"?>

<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
     version="1.0">
<xsl:output method="xml"/>




  <xsl:template match="*" >
    <xsl:choose>
				<xsl:when test="@condition = 'demo-configuration' " >
  	    	  <para>...full configuration information is available in the product version...</para>						
  	    </xsl:when>
  	    <xsl:when test="@condition = 'demo-details' " >
  	    	  <para>More details are available in the full product version documentation !</para>						
  	    </xsl:when>
  	    <xsl:when test="@condition = 'demo-details-table' " >
  	    	  <entry>More details are available in the full product version documentation !</entry>						
  	    </xsl:when>
  	    <xsl:when test="@condition = 'demo-emptytext' " >
  	    </xsl:when>
    <xsl:otherwise>
       <xsl:copy><xsl:apply-templates select="*|@*|text ()" /></xsl:copy>
    </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="@*|text ()" >
    <xsl:copy-of select="." />
  </xsl:template>
  
</xsl:transform>

