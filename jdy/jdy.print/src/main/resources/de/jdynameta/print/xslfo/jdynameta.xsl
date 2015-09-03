<?xml version="1.0" encoding="UTF-8"?>
<!--


    Copyright 2011 (C) Rainer Schneider,Roggenburg <schnurlei@googlemail.com>

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

-->
<xsl:stylesheet version="2.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns:fo="http://www.w3.org/1999/XSL/Format" >
	<xsl:output method="xml" encoding="utf-16"/>
	<xsl:output method="xml" indent="yes"/> 


<xsl:template match="/">

	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
		<fo:layout-master-set>
			<fo:simple-page-master master-name="Title"
				page-width="21cm"
				page-height="29.7cm"
				margin-top="1cm"
				margin-bottom="2cm"
				margin-left="3cm"
				margin-right="2.5cm">
				<fo:region-body margin-top="2cm" margin-bottom="1.5cm" />
				<fo:region-before region-name="header-region" extent="2cm"/>
				<fo:region-after extent="3cm"/>
			</fo:simple-page-master> 
		</fo:layout-master-set>	
	
		<fo:page-sequence master-reference="Title">
		
			<fo:static-content flow-name="header-region">
						<xsl:call-template name="HeaderTemplate"></xsl:call-template>
			</fo:static-content>
			
			<fo:flow flow-name="xsl-region-body">
			
				<fo:block space-before="10mm">
				<fo:table width="100%" border-style="none" >
					<fo:table-header>	
					<fo:table-row border-bottom-style="solid" border-bottom-width="3px" border-bottom-color="rgb(255,255,255)" font-size="10pt" font-family="Helvetica 65 Medium" text-transform="uppercase">
						<xsl:for-each select="ClassInfo/ValueModelList/ValueModel[1]/PrimitiveAttribute">					      
					    <fo:table-cell border-right-style="solid" border-right-width="3px" background-color="rgb(210,210,210)" border-right-color="rgb(255,255,255)" padding-top="3pt" padding-left="3pt" padding-right="3pt" padding-bottom="3pt" >
					      <fo:block wrap-option="wrap" text-align="right">
					      
							<xsl:call-template name="text_wrapper">
							   <xsl:with-param name="Text" select="@ExternalName"/>
							</xsl:call-template>					      
						      </fo:block>
					    </fo:table-cell>
						</xsl:for-each>					
					</fo:table-row>
					</fo:table-header>
				<fo:table-body>						
				 	<xsl:for-each select="ClassInfo/ValueModelList/ValueModel">
					   <fo:table-row border-bottom-style="solid" border-bottom-width="3px" border-bottom-color="rgb(255,255,255)" font-family="Helvetica 65 Medium" >
					 	      <xsl:for-each select="PrimitiveAttribute">
								    <fo:table-cell border-right-style="solid" border-right-width="3px" background-color="rgb(210,210,210)" border-right-color="rgb(255,255,255)" padding-top="3pt" padding-left="3pt" padding-right="3pt" padding-bottom="3pt" >
								      <fo:block wrap-option="wrap" text-align="right">
								      <fo:block><xsl:value-of select="node()/@value"/></fo:block>
								      </fo:block>
								    </fo:table-cell>
				    		  </xsl:for-each>
					  </fo:table-row>
			    	</xsl:for-each>
				</fo:table-body>
			   </fo:table>
			  </fo:block>
				<fo:block id="theEnd"/> 
			</fo:flow>
			 	
		</fo:page-sequence>
	 	
	</fo:root>			
 </xsl:template>	
 
	<xsl:template name="HeaderTemplate">
		<fo:table 
			table-layout="fixed" 
			width="auto" 
			font-size="10pt"
			font-family="Helvetica"
			space-before.optimum="6pt">
	    	<fo:table-column column-width="25%"/> 
	    	<fo:table-column column-width="50%"/> 
	    	<fo:table-column column-width="25%"/> 
			  <fo:table-body>
			   <fo:table-row border-bottom-style="solid">
			    <fo:table-cell column-number="1" display-align="after">
			     <fo:block> 	-Bild- </fo:block>
				</fo:table-cell> 
			    <fo:table-cell column-number="2" display-align="after">
			     <fo:block text-align="center"> 	JDynameta </fo:block>
				</fo:table-cell> 
				<fo:table-cell column-number="3" display-align="after"> 
				 <fo:block text-align="right">  Seite - <fo:page-number/> von <fo:page-number-citation ref-id="theEnd"/> </fo:block>
				</fo:table-cell> 
			   </fo:table-row>
			  </fo:table-body>
 		</fo:table>
	</xsl:template>	
	
<xsl:template name="text_wrapper">
<xsl:param name="Text"/>
<xsl:choose>
    <xsl:when test="string-length($Text)">
      <xsl:value-of select="substring($Text,1,5)"/>&#x200b;<xsl:call-template name="wrapper_helper">
        <xsl:with-param name="Text" select="substring($Text,6)"/>
      </xsl:call-template>
    </xsl:when>
    <xsl:otherwise></xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="wrapper_helper">
<xsl:param name="Text"/>
  <xsl:value-of select="substring($Text,1,5)"/>&#x200b;<xsl:call-template name="text_wrapper">
    <xsl:with-param name="Text" select="substring($Text,6)"/>
  </xsl:call-template>
</xsl:template>

	
 
</xsl:stylesheet>