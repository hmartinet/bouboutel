<!DOCTYPE html>
<!--
-->
<#assign I18n=statics['ch.poudriere.bouboutel.bundles.I18n']>

<html>
    <head>
        <style>
            body {
                font-family: Calibri;
                font-size: 11pt;
            }
            div.header {
                display: block;
                position: running(header);
                margin-top: 5mm;
                padding-bottom: 2px;
                border-bottom: 2px solid black;
                background-image: url("logo.png");
                background-size: contain;
                background-repeat: no-repeat;
                background-position: right;
            }
            div.header .title {
                font-weight: 500;
            }
            div.header .subtitle {
                margin: .125em 0;
                font-size: 10pt;
            }
            div.footer-left {
                display: block;
                font-size: 9pt;
                text-align: left;
                position: running(footer);
            }
            table {
                width: 100%;
                margin: 0;
                padding: 0;
                border-collapse: collapse;
                border-spacing: 0;
                -fs-table-paginate: paginate;
            }
            table thead tr {
                page-break-inside: avoid;
            }
            table thead tr th {
                background-color: #D3D3D3;
                padding: .2em .5em;
            }
            table tbody tr td {
                border-top: 1px solid #D3D3D3;
                padding: .2em .5em;
                vertical-align: top;
            }
            table tbody.total {
                page-break-inside: avoid;
                page-break-before: auto;
            }
            table tbody.total tr td {
                font-weight: 500;
            }
            table tbody.total tr.separator td {
                border-top: none;
                border-bottom: 3px black double;
                padding: .125em;
            }
            .right {
                text-align: right;
            }
            @page {
                size: A4;
                margin: 25mm 20mm 30mm;
                @top-center {
                    font-family: Calibri;
                    content: element(header);
                }
                @bottom-left {
                    font-family: Calibri;
                    content: element(footer);
                }
                @bottom-right {
                    font-family: Calibri;
                    font-size: 11pt;
                    content: "Page " counter(page) " / " counter(pages);
                }
            }
        </style>
    </head>

    <body>
        <div class="header">
            <div class="title">Réservations (extrait)</div>
        </div>
        <div class="footer-left">
            Imprimé le: ${I18n.formatDate(now)}
        </div>
        <div class="content">
            <table>
                <thead><tr>
                    <th>Nom</th>
                    <th class="right">Sièges</th>
                    <th>Commentaire</th>
                    <th>Spéctacle</th>
                </tr></thead>
                <tbody>
                    <#list bookings as booking><tr>
                        <td>${booking.customerName}</td>
                        <td class="right">${booking.nbSeats}</td>
                        <td>${booking.comment}</td>
                        <td><strong>${booking.performance.show.title}</strong><br/>
                        ${booking.performance.room.name}<br/>
                        ${I18n.formatDate(booking.performance.date)}</td>
                    </tr></#list>
                    <!-- To test page breaks
                    <#list 1..3 as x><tr>
                        <td>Name ${x}</td>
                        <td class="right">0</td>
                        <td></td>
                    </tr></#list>
                    -->
                </tbody>
            </table>
        </div>
    </body>
</html>