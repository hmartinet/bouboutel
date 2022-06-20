<!DOCTYPE html>
<!--
-->
<html class="${type} select-${select}">
    <head>
        <style>
            html.preview {
                background-color: grey;
            }
            body {
                font-family: Calibri;
                font-size: 11pt;
                line-height: 1;
                position: relative;
                top: 0;
                left: 0;
                margin: 0;
                padding: 0;
                overflow: hidden;
            }
            html.preview body {
                background-color: white;
                margin: 1cm;
                padding: 0;
                width: 148mm;
                height: 105mm;
            }
            html.preview .page-border {
                position: absolute;
                top: -10000px;
                left: -10000px;
                width: 100%;
                height: 100%;
                border: 10000px gray solid;
                z-index: 10;
            }
            html.preview.select-1 .page-border {
                position: absolute;
                top: -2px;
                left: -2px;
                width: 100%;
                height: 100%;
                border: 2px purple solid;
                z-index: 10;
            }
            .image-block {
                position: absolute;
                left: ${imageX}mm;
                top: ${imageY}mm;
            }
            img {
                display: block; 
                page-break-inside: avoid;
            }
            html.preview.select-1 img {
                position: absolute;
                top: -2px;
                left: -2px;
                width: 100%;
                height: 100%;
                border: 2px blue solid;
            }
            .main-block {
                position: absolute;
                display: table;
                left: ${showX}mm;
                top: ${showY}mm;
                margin: 0;
                padding: 0;
            }
            .price-block {
                position: absolute;
                left: ${priceX}mm;
                top: ${priceY}mm;
            }
            .one-entry-block {
                position: absolute;
                left: ${oneEntryX}mm;
                top: ${oneEntryY}mm;
            }
            html.preview .main-block,
            html.preview .one-entry-block,
            html.preview .price-block {
                padding-top: 1mm;
            }
            html.preview.select-2 .main-block,
            html.preview.select-4 .one-entry-block,
            html.preview.select-3 .price-block {
                border: 2px blue dotted;
                margin: -2px;
            }
            .h1 {
                font-size: 20pt;
            }
            .h2 {
                font-size: 14pt;
            }
            .bold {
                font-weight: 700;
            }
            .italic {
                font-style: italic;
            }
            .block > div {
                margin-bottom: 4pt;
            }
            .block > div:last-child {
                margin-bottom: 0;
            }
            @page {
                size: 148mm 105mm;
                margin: 0;
                padding: 0;
            }
        </style>
    </head>

    <body>
        <#if type == "preview">
          <div class="page-border"/>
        </#if>
        <div class="block image-block">
            <img alt="" src="data:image/jpeg;base64,${image}"/>
        </div>
        <div class="block price-block">
            <div class="h2 bold">${price}</div>
            <div>Billet no ${id}</div>
        </div>
        <div class="block main-block">
            <div class="h1 bold">${show}</div>
            <div class="h2 italic">${company}</div>
            <div class="h1 bold">${room}</div>
            <div>${street}, ${city}</div>
            <div class="h1 bold">${date}</div>
            <div class="h2 bold">${title}</div>
            <div>${comment}</div>
        </div>
        <div class="block one-entry-block">
            <div>1 entrée</div>
        </div>
    </body>
</html>
