/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
package org.apache.poi.xssf.usermodel;

import junit.framework.TestCase;
import org.apache.poi.xssf.XSSFTestDataSamples;
import org.apache.poi.POIXMLDocumentPart;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTDrawing;

import java.util.List;
import java.io.IOException;

/**
 * @author Yegor Kozlov
 */
public class TestXSSFDrawing extends TestCase {
    public void testRead(){
        XSSFWorkbook wb = XSSFTestDataSamples.openSampleWorkbook("WithDrawing.xlsx");
        XSSFSheet sheet = wb.getSheetAt(0);
        //the sheet has one relationship and it is XSSFDrawing
        List<POIXMLDocumentPart> rels = sheet.getRelations();
        assertEquals(1, rels.size());
        assertTrue(rels.get(0) instanceof XSSFDrawing);

        XSSFDrawing drawing = (XSSFDrawing)rels.get(0);
        //sheet.createDrawingPatriarch() should return the same instance of XSSFDrawing
        assertSame(drawing, sheet.createDrawingPatriarch());
        String drawingId = drawing.getPackageRelationship().getId();

        //there should be a relation to this drawing in the worksheet
        assertTrue(sheet.getWorksheet().isSetDrawing());
        assertEquals(drawingId, sheet.getWorksheet().getDrawing().getId());

    }

    public void testNew(){
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();
        //multiple calls of createDrawingPatriarch should return the same instance of XSSFDrawing
        XSSFDrawing dr1 = sheet.createDrawingPatriarch();
        XSSFDrawing dr2 = sheet.createDrawingPatriarch();
        assertSame(dr1, dr2);

        List<POIXMLDocumentPart> rels = sheet.getRelations();
        assertEquals(1, rels.size());
        assertTrue(rels.get(0) instanceof XSSFDrawing);

        XSSFDrawing drawing = (XSSFDrawing)rels.get(0);
        String drawingId = drawing.getPackageRelationship().getId();

        //there should be a relation to this drawing in the worksheet
        assertTrue(sheet.getWorksheet().isSetDrawing());
        assertEquals(drawingId, sheet.getWorksheet().getDrawing().getId());

    }
    public void testMultipleDrawings(){
        XSSFWorkbook wb = new XSSFWorkbook();
        for (int i = 0; i < 3; i++) {
            XSSFSheet sheet = wb.createSheet();
            XSSFDrawing drawing = sheet.createDrawingPatriarch();
        }
        org.openxml4j.opc.Package pkg = wb.getPackage();
        assertEquals(3, pkg.getPartsByContentType(XSSFRelation.DRAWINGS.getContentType()).size());
    }
}
