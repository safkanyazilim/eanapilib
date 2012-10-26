<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html" indent="yes"/>
    <xsl:decimal-format decimal-separator="." grouping-separator="," />

    <xsl:key name="files" match="file" use="@name" />

    <!-- Based on CheckStyle XML Style Sheet by Rolf Wojtech <rolf@wojtech.de>          -->
    <!-- That XSL is checkstyle-noframes-severity-sorted.xsl available in               -->
    <!-- the CheckStyle distribution.                                                   -->
    <xsl:template match="checkstyle">
        <html>
            <head>
                <title>CheckStyle Issues</title>
                <style type="text/css">
                    .bannercell {
                        border: 0px;
                        padding: 0px;
                    }
                    body {
                        margin-left: 10;
                        margin-right: 10;
                        font:normal 80% arial,helvetica,sanserif;
                        background-color:#FFFFFF;
                        color:#000000;
                    }
                    .a td {
                        background: #efefef;
                    }
                    .b td {
                        background: #fff;
                    }
                    th, td {
                        text-align: left;
                        vertical-align: top;
                    }
                    th {
                        font-weight:bold;
                        background: #ccc;
                        color: black;
                    }
                    table, th, td {
                        font-size:100%;
                        border: none
                    }
                    h2 {
                        font-weight:bold;
                        font-size:140%;
                        margin-bottom: 5;
                    }
                    h3 {
                        font-size:100%;
                        font-weight:bold;
                        background: #525D76;
                        color: white;
                        text-decoration: none;
                        padding: 5px;
                        margin-right: 2px;
                        margin-left: 2px;
                        margin-bottom: 0;
                    }
                    h3 a {
                        color: white;
                    }
                    h3 a:visited {
                        color: #B2A790;
                    }
                    iframe#emptyFrame {
                        display: none;
                        visibility: hidden;
                    }
                </style>
            </head>
            <body onload="clearFrame();">
                <a name="top"></a>
                <table border="0" cellpadding="0" cellspacing="0" width="100%">
                    <tr>
                        <td>
                            <h2>CheckStyle Audit</h2>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            Designed for use with <a href='http://checkstyle.sourceforge.net/'>CheckStyle</a>
                            and <a href='http://jakarta.apache.org'>Ant</a>.
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <b>NOTE:</b> In order for the links to open up the file in IntelliJ IDEA, you must have the
                            <a href="http://www.atlassian.com/software/ide-connectors/overview">
                                Atlassian Connector for IntelliJ IDE
                            </a> plugin installed and turned on.
                        </td>
                    </tr>
                </table>
                <hr size="1"/>

                <!-- Summary part -->
                <xsl:apply-templates select="." mode="summary"/>
                <hr size="1" width="100%" align="left"/>

                <!-- Namespace summary part -->
                <xsl:apply-templates select="." mode="namespaceSummary"/>
                <hr size="1" width="100%" align="left"/>

                <!-- For each package create its part -->
                <xsl:apply-templates select="file[@name and generate-id(.) = generate-id(key('files', @name))]" />
            </body>
            <script type="text/javascript">
                function clearFrame() {
                iframe = document.getElementById("emptyFrame");
                iframe.src = "about:blank";
                }
            </script>
            <iframe id="emptyFrame" name="emptyFrame" height="0" width="0"/>
        </html>
    </xsl:template>

    <xsl:template match="file">
        <xsl:if test="count(error) &gt; 0">
            <h3 style="width: 94%;">
                File
                <a target="emptyFrame">
                    <xsl:attribute name="href">
                        <xsl:text>http://localhost:51235/file?file=src/</xsl:text>
                        <xsl:value-of select="substring-after(translate(@name, '\', '/'),'src/')" />
                    </xsl:attribute>
                    <xsl:value-of select="@name" />
                    <xsl:text> (</xsl:text>
                    <xsl:value-of select="count(error)" />
                    <xsl:text>)</xsl:text>
                </a>
            </h3>

            <table class="log" border="0" cellpadding="5" cellspacing="2" width="95%">
                <tr>
                    <th width="40px;">Line</th>
                    <th width="40px;">Column</th>
                    <th width="60px;">Severity</th>
                    <th>Error Description</th>
                    <th width="80px;">CheckStyle Module</th>
                </tr>
                <xsl:for-each select="key('files', @name)/error">
                    <xsl:sort data-type="number" order="ascending" select="@line"/>
                    <tr>
                        <xsl:call-template name="alternated-row"/>
                        <td>
                            <a target="emptyFrame">
                                <xsl:attribute name="href">
                                    <xsl:text>http://localhost:51235/file?file=src/</xsl:text>
                                    <xsl:value-of select="substring-after(translate(../@name, '\', '/'),'src/')" />
                                    <xsl:text>&amp;line=</xsl:text>
                                    <xsl:value-of select="@line - 1"/>
                                </xsl:attribute>
                                <xsl:value-of select="@line"/>
                            </a>
                        </td>
                        <td><xsl:value-of select="@column"/></td>
                        <td><xsl:value-of select="@severity"/></td>
                        <td><xsl:value-of select="@message"/></td>
                        <td><xsl:value-of select="@source"/></td>
                    </tr>
                </xsl:for-each>
            </table>
            <a href="#top">Back to top</a>
        </xsl:if>
    </xsl:template>

    <xsl:template match="checkstyle" mode="summary">
        <h3 style="width: 49%">Summary</h3>
        <xsl:variable name="fileCount" select="count(file[@name and generate-id(.) = generate-id(key('files', @name))])"/>
        <xsl:variable name="errorCount" select="count(file/error[@severity='error'])"/>
        <xsl:variable name="errorFileCount" select="count(/checkstyle/file/error[@severity='error' and position()= 1])"/>
        <xsl:variable name="warningCount" select="count(file/error[@severity='warning'])"/>
        <xsl:variable name="warningFileCount" select="count(/checkstyle/file/error[@severity='warning' and position()= 1])"/>
        <xsl:variable name="infoCount" select="count(file/error[@severity='info'])"/>
        <xsl:variable name="infoFileCount" select="count(/checkstyle/file/error[@severity='info' and position()= 1])"/>
        <table class="log" border="0" cellpadding="5" cellspacing="2" width="50%">
            <tr>
                <th>Files</th>
                <th>Errors</th>
                <th>Warnings</th>
                <th>Infos</th>
            </tr>
            <tr>
                <td><xsl:value-of select="$fileCount"/></td>
                <td><xsl:value-of select="$errorCount"/> (<xsl:value-of select="$errorFileCount"/> Files)</td>
                <td><xsl:value-of select="$warningCount"/> (<xsl:value-of select="$warningFileCount"/> Files)</td>
                <td><xsl:value-of select="$infoCount"/> (<xsl:value-of select="$infoFileCount"/> Files)</td>
            </tr>
        </table>
    </xsl:template>

    <xsl:template name="alternated-row">
        <xsl:attribute name="class">
            <xsl:if test="position() mod 2 = 1">a</xsl:if>
            <xsl:if test="position() mod 2 = 0">b</xsl:if>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="checkstyle" mode="namespaceSummary">
        <xsl:if test="count(//error) &gt; 0">
            <h3 style="width: 32%">CheckStyle Issues by Namespace</h3>
            <table class="log" border="0" cellpadding="5" cellspacing="2" width="33%">
                <tr>
                    <th>Namespace</th>
                    <th>Issue Count</th>
                </tr>
                <xsl:variable name="affiliateCount"
                              select="count(//file[contains(@name,'src\app\com\travelnow\affiliate\')]/error)"/>
                <xsl:if test="$affiliateCount &gt; 0">
                    <tr>
                        <td>Affiliate</td>
                        <td><xsl:value-of select="$affiliateCount"/></td>
                    </tr>
                </xsl:if>
                <xsl:variable name="channelMasterCount"
                              select="count(//file[contains(@name,'src\app\com\travelnow\channelMaster\')]/error)"/>
                <xsl:if test="$channelMasterCount &gt; 0">
                    <tr>
                        <td>ChannelMaster</td>
                        <td><xsl:value-of select="$channelMasterCount"/></td>
                    </tr>
                </xsl:if>
                <xsl:variable name="coreutilCount"
                              select="count(//file[contains(@name,'src\app\com\travelnow\coreutil\')]/error)"/>
                <xsl:if test="$coreutilCount &gt; 0">
                    <tr>
                        <td>Coreutil</td>
                        <td><xsl:value-of select="$coreutilCount"/></td>
                    </tr>
                </xsl:if>
                <xsl:variable name="customerCount"
                              select="count(//file[contains(@name,'src\app\com\travelnow\customer\')]/error)"/>
                <xsl:if test="$customerCount &gt; 0">
                    <tr>
                        <td>Customer</td>
                        <td><xsl:value-of select="$customerCount"/></td>
                    </tr>
                </xsl:if>
                <xsl:variable name="dataCount"
                              select="count(//file[contains(@name,'src\app\com\travelnow\data\')]/error)"/>
                <xsl:if test="$dataCount &gt; 0">
                    <tr>
                        <td>Data</td>
                        <td><xsl:value-of select="$dataCount"/></td>
                    </tr>
                </xsl:if>
                <xsl:variable name="ejbCount"
                              select="count(//file[contains(@name,'src\app\com\travelnow\ejb\')]/error)"/>
                <xsl:if test="$ejbCount &gt; 0">
                    <tr>
                        <td>Ejb</td>
                        <td><xsl:value-of select="$ejbCount"/></td>
                    </tr>
                </xsl:if>
                <xsl:variable name="externalCount"
                              select="count(//file[contains(@name,'src\app\com\travelnow\external\')]/error)"/>
                <xsl:if test="$externalCount &gt; 0">
                    <tr>
                        <td>External</td>
                        <td><xsl:value-of select="$externalCount"/></td>
                    </tr>
                </xsl:if>
                <xsl:variable name="geoCount"
                              select="count(//file[contains(@name,'src\app\com\travelnow\geo\')]/error)"/>
                <xsl:if test="$geoCount &gt; 0">
                    <tr>
                        <td>Geo</td>
                        <td><xsl:value-of select="$geoCount"/></td>
                    </tr>
                </xsl:if>
                <xsl:variable name="helperCount"
                              select="count(//file[contains(@name,'src\app\com\travelnow\helper\')]/error)"/>
                <xsl:if test="$helperCount &gt; 0">
                    <tr>
                        <td>Helper</td>
                        <td><xsl:value-of select="$helperCount"/></td>
                    </tr>
                </xsl:if>
                <xsl:variable name="i18nCount"
                              select="count(//file[contains(@name,'src\app\com\travelnow\i18n\')]/error)"/>
                <xsl:if test="$i18nCount &gt; 0">
                    <tr>
                        <td>i18n</td>
                        <td><xsl:value-of select="$i18nCount"/></td>
                    </tr>
                </xsl:if>
                <xsl:variable name="localeCount"
                              select="count(//file[contains(@name,'src\app\com\travelnow\locale\')]/error)"/>
                <xsl:if test="$localeCount &gt; 0">
                    <tr>
                        <td>Locale</td>
                        <td><xsl:value-of select="$localeCount"/></td>
                    </tr>
                </xsl:if>
                <xsl:variable name="logCount"
                              select="count(//file[contains(@name,'src\app\com\travelnow\log\')]/error)"/>
                <xsl:if test="$logCount &gt; 0">
                    <tr>
                        <td>Log</td>
                        <td><xsl:value-of select="$logCount"/></td>
                    </tr>
                </xsl:if>
                <xsl:variable name="mailCount"
                              select="count(//file[contains(@name,'src\app\com\travelnow\mail\')]/error)"/>
                <xsl:if test="$mailCount &gt; 0">
                    <tr>
                        <td>Mail</td>
                        <td><xsl:value-of select="$mailCount"/></td>
                    </tr>
                </xsl:if>
                <xsl:variable name="persistCount"
                              select="count(//file[contains(@name,'src\app\com\travelnow\persist\')]/error)"/>
                <xsl:if test="$persistCount &gt; 0">
                    <tr>
                        <td>Persist</td>
                        <td><xsl:value-of select="$persistCount"/></td>
                    </tr>
                </xsl:if>
                <xsl:variable name="servicesCount"
                              select="count(//file[contains(@name,'src\app\com\travelnow\services\')]/error)"/>
                <xsl:if test="$servicesCount &gt; 0">
                    <tr>
                        <td>Services</td>
                        <td><xsl:value-of select="$servicesCount"/></td>
                    </tr>
                </xsl:if>
                <xsl:variable name="servletCount"
                              select="count(//file[contains(@name,'src\app\com\travelnow\servlet\')]/error)"/>
                <xsl:if test="$servletCount &gt; 0">
                    <tr>
                        <td>Servlet</td>
                        <td><xsl:value-of select="$servletCount"/></td>
                    </tr>
                </xsl:if>
                <xsl:variable name="supplierCount"
                              select="count(//file[contains(@name,'src\app\com\travelnow\supplier\')]/error)"/>
                <xsl:if test="$supplierCount &gt; 0">
                    <tr>
                        <td>Supplier</td>
                        <td><xsl:value-of select="$supplierCount"/></td>
                    </tr>
                </xsl:if>
                <xsl:variable name="testCount"
                              select="count(//file[contains(@name,'src\test\')]/error)"/>
                <xsl:if test="$testCount &gt; 0">
                    <tr>
                        <td>Test</td>
                        <td><xsl:value-of select="$testCount"/></td>
                    </tr>
                </xsl:if>
                <xsl:variable name="uiCount"
                              select="count(//file[contains(@name,'src\app\com\travelnow\ui\')]/error)"/>
                <xsl:if test="$uiCount &gt; 0">
                    <tr>
                        <td>UI</td>
                        <td><xsl:value-of select="$uiCount"/></td>
                    </tr>
                </xsl:if>
                <xsl:variable name="utilCount"
                              select="count(//file[contains(@name,'src\app\com\travelnow\util\')]/error)"/>
                <xsl:if test="$utilCount &gt; 0">
                    <tr>
                        <td>Util</td>
                        <td><xsl:value-of select="$utilCount"/></td>
                    </tr>
                </xsl:if>
                <xsl:variable name="validationCount"
                              select="count(//file[contains(@name,'src\app\com\travelnow\validation\')]/error)"/>
                <xsl:if test="$validationCount &gt; 0">
                    <tr>
                        <td>Validation</td>
                        <td><xsl:value-of select="$validationCount"/></td>
                    </tr>
                </xsl:if>
                <xsl:variable name="xmlCount"
                              select="count(//file[contains(@name,'src\app\com\travelnow\xml\')]/error)"/>
                <xsl:if test="$xmlCount &gt; 0">
                    <tr>
                        <td>XML</td>
                        <td><xsl:value-of select="$xmlCount"/></td>
                    </tr>
                </xsl:if>
            </table>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>
