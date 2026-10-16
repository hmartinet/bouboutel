/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.models;

import ch.poudriere.bouboutel.bundles.I18n;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.time.LocalDateTime;
import java.util.StringTokenizer;

/**
 *
 * @author Hervé Martinet <herve.martinet@gmail.com>
 * @param <T>
 */
public class ModelListPrinter<T extends AbstractPrintableModel>
        implements Printable {
    private final ModelList<T> modelList;
    private String tableTitle = "";
    private String tableFooter = "";

    public ModelListPrinter(ModelList<T> modelList) {
        this.modelList = modelList;
    }

    /**
     * @return the tableTitle
     */
    public String getTableTitle() {
        return tableTitle;
    }

    /**
     * @param tableTitle the tableTitle to set
     */
    public void setTableTitle(String tableTitle) {
        this.tableTitle = tableTitle;
    }

    /**
     * @return the tableFooter
     */
    public String getTableFooter() {
        return tableFooter;
    }

    /**
     * @param tableFooter the tableFooter to set
     */
    public void setTableFooter(String tableFooter) {
        this.tableFooter = tableFooter;
    }

    @Override
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
        if (pageIndex * 40 > modelList.size()) {
            return Printable.NO_SUCH_PAGE;
        } else {

            /* We'll assume that Jav2D is available.
             */
            Graphics2D g2d = (Graphics2D) g;

            /* Move the origin from the corner of the Paper to the corner
            * of the imageable area.
             */
            g2d.
                    translate(pageFormat.getImageableX(), pageFormat.
                            getImageableY());

            int lc = 14;

            g2d.setFont(new Font("Monospaced", Font.BOLD, 12));

            StringTokenizer st = new StringTokenizer(getTableTitle(), "|");
            while (st.hasMoreTokens()) {
                g2d.drawString(st.nextToken(), 5, lc);
                lc += 14;
            }

            g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));

            g2d.drawString("(page " + (pageIndex + 1) + " imprimée le: "
                    + I18n.formatDate(LocalDateTime.now())
                    + ")", 5, lc);

            lc += 14;
            lc += 14;

            // for each line in the table
            for (int i = pageIndex * 40; i < (Math.min(modelList.size(),
                    ((pageIndex + 1) * 40))); i++) {
                System.out.println("PRINT: adding line " + i);
                String line = modelList.getByIndex(i).toPrintLine();
                g2d.drawString(line, 5, lc);
                lc += 14;

            }

            g2d.setFont(new Font("Monospaced", Font.BOLD, 10));
            lc += 14;
            if (((pageIndex + 1) * 40) > modelList.size()) {
                // print totals
                st = new StringTokenizer(getTableFooter(), "|");
                while (st.hasMoreTokens()) {
                    g2d.drawString(st.nextToken(), 5, lc);
                    lc += 14;
                }
            }

            return Printable.PAGE_EXISTS;
        }
    }
}
