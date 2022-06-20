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
            div.content {
            }
            table {
                width: 100%;
                margin: 0;
                padding: 0;
                border-collapse: collapse;
                border-spacing: 0;
                -fs-table-paginate: paginate;
            }
            table thead tr th {
                background-color: #D3D3D3;
                padding: .2em .5em;
            }
            table tbody tr td {
                border-top: 1px solid #D3D3D3;
                padding: .2em .5em;
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
            <div class="title">Liste des réservations pour <i>${performance.show.title}</i></div>
            <div class="subtitle">${I18n.formatDate(performance.date)}, ${performance.room}</div>
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
                </tr></thead>
                <tbody>
                    <#list performance.bookingsSorted as booking><tr>
                        <td>${booking.customerName}</td>
                        <td class="right">${booking.nbSeats}</td>
                        <td>${booking.comment}</td>
                    </tr></#list>
                    <!-- To test page breaks
                    <#list 1..3 as x><tr>
                        <td>Name ${x}</td>
                        <td class="right">0</td>
                        <td></td>
                    </tr></#list>
                    -->
                </tbody>
                <tbody class="total">
                    <tr class="total separator">
                        <td colspan="3"/>
                    </tr>
                    <tr>
                        <td>Sièges réservés</td>
                        <td class="right">${performance.nbBookedSeats}</td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>Sièges vendus</td>
                        <td class="right">${performance.nbSoldSeats}</td>
                        <td></td>
                    </tr>
                    <tr>
                        <td>Sièges libres</td>
                        <td class="right">${performance.nbFreeSeats}</td>
                        <td></td>
                    </tr>
                </tbody>
            </table>
        </div>
    </body>
</html>