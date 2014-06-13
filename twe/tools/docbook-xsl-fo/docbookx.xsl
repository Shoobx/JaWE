<?xml version='1.0'?>
<!--
    Common Design Style
    Copyright (C) 2014 Together Teamsolutions Co., Ltd.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses
-->
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
<xsl:include href="linebreak.xsl"/>
<xsl:include href="../docbook-xsl/xsl/fo/docbook.xsl"/>
</xsl:stylesheet>