package ru.onyx.clipper.model;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import org.w3c.dom.Node;
import ru.onyx.clipper.data.PropertyGetter;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * User: Alex
 * Date: 15.03.12
 * Time: 11:24
 */
public class ReportCell extends BaseReportObject {


    /**
     * Custom constructor for wordsplitter tag
     *
     * @param pfixedHeight
     * @param text
     * @param htextalign
     * @param pFontName
     * @param pFontName
     * @param fontW
     * @param borderwidth
     */
    public ReportCell(float pfixedHeight, String text, int vtextalign, int htextalign, String pFontName, Float fontW, float borderwidth, float pleading, float[] paddings, Boolean usebPaddings, int[] bgColorp, int[] borderColorp, PropertyGetter pGetter, HashMap<String, ReportBaseFont> pFonts) {
        _fonts = pFonts;
        propertyGetter = pGetter;
        //First we unset all properties...
        SetParameters();
        // ...then - set our custom
        this.useBorderPadding = usebPaddings;
        this.cellMode = "text";
        this.text = text;
        this.borderWidth = borderwidth;
        this.leading = pleading;
        this.fixedHeight = pfixedHeight;
        this.horizontalTextAlignment = htextalign;
        this.verticalTextAlignment = vtextalign;
        this.horizontalAlignment = htextalign;
        this.fontName = pFontName;
        this.fontWeight = fontW;
        this._fonts = pFonts;
        this.bgColor = Arrays.toString(bgColorp).replace("[","").replace("]","").replace(", ",",");
        this.borderColor = Arrays.toString(borderColorp).replace("[","").replace("]","").replace(", ",",");
    }

    /**
     * Main constructor
     */
    public ReportCell(Node node, HashMap<String, ReportBaseFont> fonts, BaseReportObject pParent, PropertyGetter pGetter) throws ParseException {
        _fonts = fonts;
        parent = pParent;
        propertyGetter = pGetter;
        Load(node);
        LoadItems(node, fonts, this, pGetter);
    }

    public void SetParameters() {
        this.stopInherit = false;
        this.borderWidth = -1f;
        this.borderWidthBottom = -1f;
        this.borderWidthLeft = -1f;
        this.borderWidthRight = -1f;
        this.borderWidthTop = -1f;
        this.fixedHeight = -1f;
        this.minimumHeight = -1f;
        this.paddingBottom = -1f;
        this.paddingLeft = -1f;
        this.paddingRight = -1f;
        this.paddingTop = -1f;
        this.useBorderPadding = false;
        this.colSpan = -1;
        this.rowSpan = -1;
        this.indentationLeft = -1f;
        this.indentationRight = -1f;
        this.firstLineIndentation = -1f;
        this.spacingAfter = -1f;
        this.spacingBefore = -1f;
        this.bgColor = null;
        this.borderColor = null;
    }


    public PdfPCell getPdfObject() throws DocumentException, ParseException, IOException {
        String celltext = "";
        if (getText() != null) celltext = getText();

        if (getPropertyName() != null && customtext == null) {
            celltext = propertyGetter.GetProperty(getPropertyName());
            if (celltext == null)
            {
                celltext = getDefaultNullValue();
            }
            if (getDateFormat() != null && getToDateFormat() != null) {
                celltext = ConvertPropertyToSpecificDateFormat(celltext);
            }
        }

        if (getStringformat() != null) {
            if (!celltext.equals("")) {
                celltext = String.format(getStringformat(), Float.parseFloat(celltext));
            }
        }

        if (customtext != null) {
            celltext = customtext;
            if (getDateFormat() != null && getToDateFormat() != null) {
                celltext = ConvertPropertyToSpecificDateFormat(customtext);
            }

            if (getStringformat() != null) {
                    celltext = String.format(getStringformat(), Float.parseFloat(celltext));
            }
        }

        if (celltext.equalsIgnoreCase("true")) {
            celltext = "\uf0FE";
        }
        if (celltext.equalsIgnoreCase("false")) {
            celltext = "\uf0A8";
        }


        PdfPCell cell;
        if (getCellMode().equalsIgnoreCase("text")) {
            Paragraph par;
            Chunk ch;
            Font f = getFont();
            if (f != null) {
                int[] color = getTextColor();
                if(color != null) f.setColor(color[0],color[1],color[2]);
                ch = new Chunk(celltext, f);
                par = new Paragraph(ch);
            } else {
                par = new Paragraph(celltext);
            }

            if (getLeading() >= 0) par.setLeading(getLeading());
            par.setAlignment(getHorizontalTextAlignment());
            if (getIndentationLeft() >= 0) par.setIndentationLeft(getIndentationLeft());
            if (getIndentationRight() >= 0) par.setIndentationRight(getIndentationRight());
            if (getSpacingAfter() >= 0) par.setSpacingAfter(getSpacingAfter());
            if (getSpacingBefore() >= 0) par.setSpacingBefore(getSpacingBefore());
            if (getFirstLineIndentation() >= 0) par.setFirstLineIndent(getFirstLineIndentation());
            cell = new PdfPCell(par);
            cell.setHorizontalAlignment(getHorizontalTextAlignment());
            cell.setVerticalAlignment(getVerticalTextAlignment());
        } else if(getBGImage() != null) {

            Image img =  propertyGetter.GetImage(getBGImage(),getFileFolder(),getFileType());

            float[] scaleab = getScaleAbsolute();
            if(scaleab != null)
                img.scaleToFit(scaleab[0],scaleab[1]);


            Chunk ch = new Chunk(img,0,0,true);

            Phrase ph = new Phrase(ch);
            ph.setLeading(0);
           // cell = new PdfPCell(ph);
            cell = new PdfPCell(img);
        }
        else {
            cell = new PdfPCell();
        }
        if (getBorderWidth() >= 0) cell.setBorderWidth(getBorderWidth());
        if (getBorderWidthLeft() >= 0) cell.setBorderWidthLeft(getBorderWidthLeft());
        if (getBorderWidthRight() >= 0) cell.setBorderWidthRight(getBorderWidthRight());
        if (getBorderWidthTop() >= 0) cell.setBorderWidthTop(getBorderWidthTop());
        if (getBorderWidthBottom() >= 0) cell.setBorderWidthBottom(getBorderWidthBottom());
        if (getColSpan() >= 0) cell.setColspan(getColSpan());
        if (getRowSpan() >= 0) cell.setRowspan(getRowSpan());
        if (getFixedHeight() >= 0) cell.setFixedHeight(getFixedHeight());
        if (getMinimumHeight() >= 0) cell.setMinimumHeight(getMinimumHeight());

        if (getPaddingLeft() >= 0) cell.setPaddingLeft(getPaddingLeft());
        if (getPaddingRight() >= 0) cell.setPaddingRight(getPaddingRight());
        if (getPaddingTop() >= 0) cell.setPaddingTop(getPaddingTop());
        if (getPaddingBottom() >= 0) cell.setPaddingBottom(getPaddingBottom());

        int[] borderColor = getBorderColor();
        if(borderColor != null) cell.setBorderColor(new BaseColor(borderColor[0],borderColor[1],borderColor[2]));

        int[] borderColorTop = getBorderColorTop();
        if(borderColorTop != null) cell.setBorderColorTop(new BaseColor(borderColorTop[0],borderColorTop[1],borderColorTop[2]));

        int[] borderColorBottom = getBorderColorBottom();
        if(borderColorBottom != null) cell.setBorderColorBottom(new BaseColor(borderColorBottom[0],borderColorBottom[1],borderColorBottom[2]));

        int[] borderColorLeft = getBorderColorLeft();
        if(borderColorLeft != null) cell.setBorderColorLeft(new BaseColor(borderColorLeft[0],borderColorLeft[1],borderColorLeft[2]));

        int[] borderColorRight = getBorderColorRight();
        if(borderColorRight != null) cell.setBorderColorRight(new BaseColor(borderColorRight[0],borderColorRight[1],borderColorRight[2]));

        cell.setUseBorderPadding(getUseBorderPadding());

        int[] bgColor = getBGColor();
        if(bgColor != null) cell.setBackgroundColor(new BaseColor(bgColor[0],bgColor[1],bgColor[2]));

         cell.setVerticalAlignment(getVerticalTextAlignment());

        for (int y = 0; y < items.size(); y++) {
            cell.addElement(items.get(y).getPdfObject());
        }


        return cell;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
