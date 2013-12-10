<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<!-- ********************************************************************

     This file is part of Enhydra patch to the XSL DocBook Stylesheet 
     distribution.
   
     Author: Vladimir Radisic 01.06.2004.
     ******************************************************************** -->
     
  <!-- ================================================================== 
       Discards or not blank pages from the begining of the created PDF 
       document in case when docbook is created as book type - root tag 
       is book 
       ================================================================== -->
	<xsl:param name="enhydra.discard.emptypages" select="'true'"/>
	
	
  <!-- ================================================================== 
       Enables or disables usage of Enhydra specific font settings 
       ================================================================== -->
	<xsl:param name="enhydra.fontstyle.enable" select="'true'"/>


  <!-- ================================================================== 
       Sets font styles for specific text on the first page of PDF document
       in case when docbook is created as book type or article type 
       ================================================================== -->
  <xsl:attribute-set name="enhydra.titlepage.fontstyle.title">
    <xsl:attribute name="font-size">35pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <!--<xsl:attribute name="font-style">italic</xsl:attribute>-->
    <xsl:attribute name="color">#800000</xsl:attribute>
    <xsl:attribute name="text-align">center</xsl:attribute>
    <xsl:attribute name="font-family">sans-serif</xsl:attribute>
    <xsl:attribute name="space-before">18.6624pt</xsl:attribute>
  </xsl:attribute-set>	
  <xsl:attribute-set name="enhydra.titlepage.fontstyle.subtitle">
    <xsl:attribute name="font-size">20.736pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <!--<xsl:attribute name="font-style">italic</xsl:attribute>-->
    <xsl:attribute name="color">#000000</xsl:attribute>
    <xsl:attribute name="text-align">center</xsl:attribute>
    <xsl:attribute name="font-family">sans-serif</xsl:attribute>
    <xsl:attribute name="space-before">15.552pt</xsl:attribute>
  </xsl:attribute-set>	
  <xsl:attribute-set name="enhydra.titlepage.fontstyle.corpauthor">
    <xsl:attribute name="font-size">14.5pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
    <xsl:attribute name="color">#000000</xsl:attribute>
    <!--<xsl:attribute name="text-align">start</xsl:attribute>-->
    <xsl:attribute name="font-family">sans-serif</xsl:attribute>
    <xsl:attribute name="space-before">0.5em</xsl:attribute>
    <xsl:attribute name="space-after">0.5em</xsl:attribute>
    <!--<xsl:attribute name="keep-with-next">always</xsl:attribute>-->
  </xsl:attribute-set>	
  <xsl:attribute-set name="enhydra.titlepage.fontstyle.authorgroup">
    <xsl:attribute name="font-size">14.5pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
    <xsl:attribute name="color">#000000</xsl:attribute>
    <!--<xsl:attribute name="text-align">start</xsl:attribute>-->
    <xsl:attribute name="font-family">sans-serif</xsl:attribute>
    <xsl:attribute name="space-before">0.5em</xsl:attribute>
    <xsl:attribute name="space-after">0.5em</xsl:attribute>
  </xsl:attribute-set>	
  <xsl:attribute-set name="enhydra.titlepage.fontstyle.author">
    <xsl:attribute name="font-size">17.28pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
    <xsl:attribute name="color">#000000</xsl:attribute>
    <!--<xsl:attribute name="text-align">start</xsl:attribute>-->
    <xsl:attribute name="font-family">sans-serif</xsl:attribute>
    <xsl:attribute name="space-before">5em</xsl:attribute>
    <xsl:attribute name="space-after">0.5em</xsl:attribute>
    <!--<xsl:attribute name="keep-with-next">always</xsl:attribute>-->
  </xsl:attribute-set>	
  <xsl:attribute-set name="enhydra.titlepage.fontstyle.copyright">
  	<xsl:attribute name="font-size">12.5pt</xsl:attribute>
    <xsl:attribute name="color">#000000</xsl:attribute>
    <xsl:attribute name="text-align">left</xsl:attribute>
    <xsl:attribute name="font-family">Times Roman</xsl:attribute>
    <xsl:attribute name="space-before">15.552pt</xsl:attribute>
  </xsl:attribute-set>	


  <!-- ================================================================== 
       Set font styles for the 'chapter' text in case when docbook is 
       created as book type
       ================================================================== -->
  <xsl:attribute-set name="enhydra.chapter.fontstyle.title">
    <xsl:attribute name="font-size">24.8832pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <!--<xsl:attribute name="font-style">italic</xsl:attribute>-->
    <xsl:attribute name="color">#800000</xsl:attribute>
    <xsl:attribute name="text-align">start</xsl:attribute>
    <xsl:attribute name="font-family">sans-serif</xsl:attribute>
    <xsl:attribute name="keep-with-next">always</xsl:attribute>
    <xsl:attribute name="space-before">0.5em</xsl:attribute>
    <xsl:attribute name="space-after">0.5em</xsl:attribute>
  </xsl:attribute-set>	
  <xsl:attribute-set name="enhydra.chapter.fontstyle.subtitle">
    <xsl:attribute name="font-size">20.736pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
    <xsl:attribute name="color">#800000</xsl:attribute>
    <xsl:attribute name="text-align">start</xsl:attribute>
    <xsl:attribute name="font-family">sans-serif</xsl:attribute>
    <xsl:attribute name="space-before">0.5em</xsl:attribute>
  </xsl:attribute-set>	
  <xsl:attribute-set name="enhydra.chapter.fontstyle.corpauthor">
    <xsl:attribute name="font-size">14.4pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
    <xsl:attribute name="color">#008000</xsl:attribute>
    <!--<xsl:attribute name="text-align">start</xsl:attribute>-->
    <xsl:attribute name="font-family">sans-serif</xsl:attribute>
    <xsl:attribute name="space-before">0.5em</xsl:attribute>
    <xsl:attribute name="space-after">0.5em</xsl:attribute>
    <xsl:attribute name="keep-with-next">always</xsl:attribute>
  </xsl:attribute-set>	
  <xsl:attribute-set name="enhydra.chapter.fontstyle.authorgroup">
    <xsl:attribute name="font-size">14.4pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
    <xsl:attribute name="color">#008000</xsl:attribute>
    <!--<xsl:attribute name="text-align">start</xsl:attribute>-->
    <xsl:attribute name="font-family">sans-serif</xsl:attribute>
    <xsl:attribute name="space-before">0.5em</xsl:attribute>
    <xsl:attribute name="space-after">0.5em</xsl:attribute>
  </xsl:attribute-set>	
  <xsl:attribute-set name="enhydra.chapter.fontstyle.author">
    <xsl:attribute name="font-size">14.4pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
    <xsl:attribute name="color">#008000</xsl:attribute>
    <!--<xsl:attribute name="text-align">start</xsl:attribute>-->
    <xsl:attribute name="font-family">sans-serif</xsl:attribute>
    <xsl:attribute name="space-before">0.5em</xsl:attribute>
    <xsl:attribute name="space-after">0.5em</xsl:attribute>
    <xsl:attribute name="keep-with-next">always</xsl:attribute>
  </xsl:attribute-set>	
  <xsl:attribute-set name="enhydra.chapter.fontstyle.copyright">
    <xsl:attribute name="font-size">12.5pt</xsl:attribute>
    <xsl:attribute name="color">#000000</xsl:attribute>
    <xsl:attribute name="text-align">left</xsl:attribute>
    <xsl:attribute name="font-family">Times Roman</xsl:attribute>
    <xsl:attribute name="space-before">15.552pt</xsl:attribute>
  </xsl:attribute-set>	


  <!-- ================================================================== 
       Set font styles for the parts of 'section' text or parts of 'sect1'
       text in case when docbook is created as book type or article type
       ================================================================== -->
  <xsl:attribute-set name="enhydra.section.fontstyle.title">
    <xsl:attribute name="font-size">24.8832pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <!--<xsl:attribute name="font-style">italic</xsl:attribute>-->
    <xsl:attribute name="color">#800000</xsl:attribute>
    <xsl:attribute name="text-align">center</xsl:attribute>
    <xsl:attribute name="font-family">sans-serif</xsl:attribute>
    <xsl:attribute name="keep-with-next">always</xsl:attribute>
    <xsl:attribute name="space-before">0.5em</xsl:attribute>
    <xsl:attribute name="space-after">0.5em</xsl:attribute>
    <xsl:attribute name="margin-left">-4pc</xsl:attribute>
    <xsl:attribute name="margin-right">-4pc</xsl:attribute>
  </xsl:attribute-set>	
  <xsl:attribute-set name="enhydra.section.fontstyle.subtitle">
    <xsl:attribute name="font-size">20.736pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
    <xsl:attribute name="color">#800000</xsl:attribute>
    <xsl:attribute name="text-align">start</xsl:attribute>
    <xsl:attribute name="font-family">sans-serif</xsl:attribute>
    <xsl:attribute name="space-before">0.5em</xsl:attribute>
  </xsl:attribute-set>	
  <xsl:attribute-set name="enhydra.section.fontstyle.corpauthor">
    <xsl:attribute name="font-size">14.4pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
    <xsl:attribute name="color">#008000</xsl:attribute>
    <!--<xsl:attribute name="text-align">start</xsl:attribute>-->
    <xsl:attribute name="font-family">sans-serif</xsl:attribute>
    <xsl:attribute name="space-before">0.5em</xsl:attribute>
    <xsl:attribute name="space-after">0.5em</xsl:attribute>
    <xsl:attribute name="keep-with-next">always</xsl:attribute>
  </xsl:attribute-set>	
  <xsl:attribute-set name="enhydra.section.fontstyle.authorgroup">
    <xsl:attribute name="font-size">14.4pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
    <xsl:attribute name="color">#008000</xsl:attribute>
    <!--<xsl:attribute name="text-align">start</xsl:attribute>-->
    <xsl:attribute name="font-family">sans-serif</xsl:attribute>
    <xsl:attribute name="space-before">0.5em</xsl:attribute>
    <xsl:attribute name="space-after">0.5em</xsl:attribute>
  </xsl:attribute-set>	
  <xsl:attribute-set name="enhydra.section.fontstyle.author">
    <xsl:attribute name="font-size">14.4pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="font-style">italic</xsl:attribute>
    <xsl:attribute name="color">#008000</xsl:attribute>
    <!--<xsl:attribute name="text-align">start</xsl:attribute>-->
    <xsl:attribute name="font-family">sans-serif</xsl:attribute>
    <xsl:attribute name="space-before">0.5em</xsl:attribute>
    <xsl:attribute name="space-after">0.5em</xsl:attribute>
    <xsl:attribute name="keep-with-next">always</xsl:attribute>
  </xsl:attribute-set>	
  <xsl:attribute-set name="enhydra.section.fontstyle.copyright">
    <xsl:attribute name="font-size">12.5pt</xsl:attribute>
    <xsl:attribute name="color">#000000</xsl:attribute>
    <xsl:attribute name="text-align">left</xsl:attribute>
    <xsl:attribute name="font-family">Times Roman</xsl:attribute>
    <xsl:attribute name="space-before">15.552pt</xsl:attribute>
  </xsl:attribute-set>	
  
 
  <!-- ================================================================== 
       Set font styles for the generated Table of Contents
       ================================================================== -->
  <xsl:attribute-set name="enhydra.contenttable.fontstyle.title">
    <xsl:attribute name="font-size">20.736pt</xsl:attribute>
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <!--<xsl:attribute name="font-style">italic</xsl:attribute>-->
    <xsl:attribute name="color">#800000</xsl:attribute>
    <xsl:attribute name="text-align">center</xsl:attribute>
    <xsl:attribute name="font-family">sans-serif</xsl:attribute>
    <xsl:attribute name="keep-with-next">always</xsl:attribute>
    <!--<xsl:attribute name="space-before">0.5em</xsl:attribute>-->
    <xsl:attribute name="space-before.minimum">1em</xsl:attribute>
    <xsl:attribute name="space-before.optimum">1.5em</xsl:attribute>
    <xsl:attribute name="space-before.maximum">2em</xsl:attribute>
    <xsl:attribute name="space-after">0.5em</xsl:attribute>
    <xsl:attribute name="margin-left">-4pc</xsl:attribute>
  </xsl:attribute-set>	 
 
 
  <!-- ================================================================== 
       Enables or disables usage of Enhydra specific programlisting 
       settings 
       ================================================================== -->
	<xsl:param name="enhydra.programlisting.enable" select="'true'"/>

  <!-- ================================================================== 
       Set some of the properties connected to programlisting tag
       ================================================================== -->
  <xsl:attribute-set name="enhydra.programlisting.properties">
    <xsl:attribute name="color">black</xsl:attribute>
    <xsl:attribute name="background-color">#F0F0F0</xsl:attribute>
    <xsl:attribute name="wrap-option">wrap</xsl:attribute>
    
    <xsl:attribute name="font-size">7.5pt</xsl:attribute>
    <xsl:attribute name="font-family">monospace</xsl:attribute>    
    <xsl:attribute name="text-align">start</xsl:attribute>
    <xsl:attribute name="space-before.minimum">0.8em</xsl:attribute>
    <xsl:attribute name="space-before.optimum">1em</xsl:attribute>
    <xsl:attribute name="space-before.maximum">1.2em</xsl:attribute>
    <xsl:attribute name="space-after.minimum">0.8em</xsl:attribute>
    <xsl:attribute name="space-after.optimum">1em</xsl:attribute>
    <xsl:attribute name="space-after.maximum">1.2em</xsl:attribute> 
  
    <!--<xsl:attribute name="margin-left">-4pc</xsl:attribute>-->
  </xsl:attribute-set>	

</xsl:stylesheet>