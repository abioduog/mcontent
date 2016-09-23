/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mnewservice.mcontent.util;


import com.mnewservice.mcontent.domain.ContentFile;
import com.mnewservice.mcontent.domain.DeliverableStatus;
import com.mnewservice.mcontent.domain.DeliveryPipe;
import com.mnewservice.mcontent.domain.SeriesDeliverable;
import com.mnewservice.mcontent.manager.DeliveryPipeManager;
import com.mnewservice.mcontent.manager.FileManager;
import com.mnewservice.mcontent.manager.SeriesDeliverableManager;
import com.mnewservice.mcontent.repository.entity.AbstractDeliverableEntity;
import com.mnewservice.mcontent.web.ContentController;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import javax.activation.MimetypesFileTypeMap;
import org.apache.log4j.Logger;

import org.apache.poi.ss.usermodel.PictureData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFShape;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFPictureData;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;


public class StoreXLSXContentFile {
	 
	/**
	 * A dirty simple program that reads an Excel file.
	 * @author www.codejava.net
	 *
	 */
    
        private static final Logger LOG
            = Logger.getLogger(StoreXLSXContentFile.class);
        
	private final String TITLE = "Title";
	private final String MESSAGE = "Message";
	private final String CONTENT = "Content";
	private final String IMAGETAG ="{image}";
        //private long deliveryPipeId = 0;
        private String theme = "SERIES";
        private List validSheetsList = new ArrayList();
        

            
        // MAGICSTRING
	String URLTOFILESTORAGE = "/home/ubuntu1604/mcontent_files/";
        
        Map<Integer, Map<Integer, String>> allpicnames = new HashMap<Integer, Map<Integer, String>>();
        Map<Integer, Map<String, String>> allcontent = new HashMap<Integer, Map<String, String>>();
        
        int allpicscounter = 0;
        /*
        
        @Autowired
        private FileManager fileManager;
        
        @Autowired
        private SeriesDeliverableManager seriesManager;

        @Autowired
        private DeliveryPipeManager deliveryPipeManager;
        */
	
        public List validSheets(){
            return this.validSheetsList;
        }
        
        public Map<Integer, Map<Integer, String>> getAllPicnames(){
            return allpicnames;
        }
        public Map<Integer, Map<String, String>> getAllContents(){
            return allcontent;
        }

        public void handleUploadedXSLXFile(String excelfile){

            createContent(excelfile);
        }
	
        private void setUrlToFileStorage(String urltofilestorage){
            URLTOFILESTORAGE = urltofilestorage;
        }
        
	private File getFileByName(String filename){
            //System.out.println("Getting file byt name from default folder (" + URLTOFILESTORAGE + ")" + filename);
            return new File(URLTOFILESTORAGE + filename);
	}
        
        private File getFileByUrlAndName(String urlandfilename){
            //System.out.println("Getting file with url... " + urlandfilename);
            return new File(urlandfilename);
	}
	
	// Check worksheet, if it is valid
	// check headers from Column A
	// 1. Title
	// 2. Message
	// 3. Content
	// 3.1 Content should have something in column B 
	//     (could be from same row or lower row but has something 
	private boolean checkIfExcelSheetIsValid(Sheet currentSheet){

		try{
		// We need the worksheet rows to iterate
		Iterator<Row> checkiterator = currentSheet.iterator();
		// If empty sheet
		if(!checkiterator.hasNext())
			return false;
		// 1. Title should be first to find
		if(checkiterator.hasNext()) {
			//System.out.println(TITLE);
			Row nextRow = checkiterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			Cell cell = cellIterator.next();
			if(!cell.toString().equals(TITLE)) return false;
			cell = cellIterator.next();
			if(cell.toString().isEmpty()) return false;
				
		}
		// 2. Message is the second 
		if(checkiterator.hasNext()) {
			//System.out.println(MESSAGE);
			Row nextRow = checkiterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			Cell cell = cellIterator.next();
			if(!cell.toString().equals(MESSAGE)) return false;
			cell = cellIterator.next();
			if(cell.toString().isEmpty()) return false;

		}
		// 3. Content is the third
		if(checkiterator.hasNext()) {
			//System.out.println(CONTENT);
			Row nextRow = checkiterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			Cell cell = cellIterator.next();
			if(!cell.toString().equals(CONTENT)) return false;

			// 3.1 Does Content have any rows?
			if(checkiterator.hasNext()){
				return true;
			}
		}
		// by default, return false. If the worksheet is valid, it will return true earlier.
		return false;
		// Because any of elements can be missing, catch exception and return false
		}catch(NoSuchElementException nsee){
			return false;
		}
	}
	
	
	
    private void createContent(String excelfile) {

        // Get file by name 
        File file2013excel = getFileByName(excelfile);
        
        
        // If not found, next get it with path or url
        if (!file2013excel.exists()) {
            file2013excel = getFileByUrlAndName(excelfile);
        }
        
        // If not found, file loading has failed or removed manually
        if (!file2013excel.exists()) {
            System.out.println("File not found!");
            return;
        }

        // We need workbook from excel file
        XSSFWorkbook workbook = null;

        try {

            // We found file, so we load it to workbook 
            FileInputStream inputStream = new FileInputStream(file2013excel);
            workbook = new XSSFWorkbook(inputStream);
            
            // It might be old type excel-file, so we have to check it
            if (workbook.getWorkbookType().equals("XLSX")) {
                System.out.println("Not Excel 2013 -file!");
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("Total number of sheets: " + workbook.getNumberOfSheets());

        // Go through all workbook sheets
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Map<Integer, String> worksheetpictures = null;
            Map<String, String> worksheetcontent = null;
            
            System.out.println("Sheet nr: " + i + ", " + workbook.getSheetName(i) + ", valid = " + checkIfExcelSheetIsValid((XSSFSheet) workbook.getSheetAt(i)));
            // Check sheet
            if (checkIfExcelSheetIsValid((XSSFSheet) workbook.getSheetAt(i))) {
                validSheetsList.add(i);
                worksheetpictures = savePictures((XSSFSheet) workbook.getSheetAt(i));
                worksheetcontent = createDatabaseContent(worksheetpictures, (XSSFSheet) workbook.getSheetAt(i));
                this.allpicnames.put(i, worksheetpictures);
                this.allcontent.put(i, worksheetcontent);
            }

        }

    }
	
    // private Map<Integer, String> savePictures(XSSFSheet sheet)
    // private void createDatabaseContent(Map<Integer, String> pictures, XSSFSheet currentsheet) {
    private Map<String, String> createDatabaseContent(Map<Integer, String> pictures, XSSFSheet currentsheet) {
        
        Map<String, String> sheetcontent = new HashMap<String, String>();
        // Our worksheet is valid, so we can do this straight forward
        Iterator<Row> checkiterator = currentsheet.iterator();

        Row row = checkiterator.next();
        Cell cell = row.getCell(1);
        String title = cell.toString();
        row = checkiterator.next();
        cell = row.getCell(1);
        String message = cell.toString();
        //row = checkiterator.next();
        //cell = row.getCell(1);
        String content = "";

        StringBuffer sb = new StringBuffer();
        /*
		sb.append(System.getProperty("line.separator"));
		sb.append(row.getCell(1).toString());
		sb.append(System.getProperty("line.separator"));
         */
        
        int picindex = 0;
        String currentrowtext = "";
        while (checkiterator.hasNext()) {
            row = checkiterator.next();
            cell = row.getCell(1);
            currentrowtext = cell.toString();
            
            if (currentrowtext.indexOf(IMAGETAG) != -1) {
                // testissä printataan kaikki
                //System.out.println(pictures.get(picindex));
                // Add image 
                 sb.append(IMAGETAG);
                /*
                sb.append("<div class='content-image'>");
                sb.append("<img id='" + pictures.get(picindex).substring(0, pictures.get(picindex).indexOf(".")) + "' ");
                sb.append("src='/mContent/images/" + pictures.get(picindex) + "' ");
                sb.append("alt='" + pictures.get(picindex) + "'/>");
                sb.append("\\{image\\}");
                picindex++;
                */
            } else {

                // Add just text lines
                sb.append("<p>");
                sb.append(cell.toString());
                sb.append("</p>");
            }
        }

        content = new String(sb);
        
        // All ok, we can now create a new content
        System.out.println("Title: " + title);
        System.out.println("Message: " + message);
        System.out.println("Content: " + content);
        sheetcontent.put("Title", title);
        sheetcontent.put("Message", message);
        sheetcontent.put("Content", content);
        return sheetcontent;
    }

private Map<Integer, String> savePictures(XSSFSheet sheet) {

        System.out.println("Handling worksheet pictures...");

        XSSFDrawing dravingPatriarch = sheet.getDrawingPatriarch();

        Map<Integer, Map<String, byte[]>> pics = new HashMap<Integer, Map<String, byte[]>>();
        Map<Integer, String> picnames = new HashMap<Integer, String>();

        if (dravingPatriarch != null) {
            java.util.List<XSSFShape> shapes = dravingPatriarch.getShapes();

            for (XSSFShape shape : shapes) {
                if (shape instanceof XSSFPicture) {
                    XSSFPicture hssfPicture = (XSSFPicture) shape;
                    int picIndex = ((XSSFPicture) shape).getAnchor().getDx1();
                    String filename = ((XSSFPicture) shape).toString();
                    int row = hssfPicture.getClientAnchor().getRow1();
                    int col = hssfPicture.getClientAnchor().getCol1();
                    XSSFPictureData xssfPictureData = hssfPicture.getPictureData();
                    byte[] data = xssfPictureData.getData();
                    //System.out.println("Picture " + xssfPictureData.toString() + "(" + data.length + ")"
                    //		+ " with Filename: " + filename + " is located row: " + row + ", col: " + col);
                    Map<String, byte[]> currentpicwithextension = new HashMap<String, byte[]>();

                    // We accept only picture files
                    if (xssfPictureData.suggestFileExtension().equals("jpeg")
                            || xssfPictureData.suggestFileExtension().equals("jpg")
                            || xssfPictureData.suggestFileExtension().equals("png")
                            || xssfPictureData.suggestFileExtension().equals("gif")) {
                        //String nanoname = "pict" + System.nanoTime() + "." + xssfPictureData.suggestFileExtension();
                        // First put pict extension to map with data
                        currentpicwithextension.put(xssfPictureData.suggestFileExtension(), data);
                        // ...and add it to all pictures with rownumber
                        pics.put(new Integer(row), currentpicwithextension);
                    }
                }
            }

            // All good, we are ready to store pictures
            // First sort all pics with row number
            Map<Integer, Map<String, byte[]>> picsinorder = new TreeMap<Integer, Map<String, byte[]>>(pics);

            // Pictures are sorted
            int counter = 0;
            for (Map.Entry<Integer, Map<String, byte[]>> entry : picsinorder.entrySet()) {

                // Now take a picture from map with extension and store it
                Map<String, byte[]> picdata = picsinorder.get(entry.getKey());
                for (Map.Entry<String, byte[]> dataa : picdata.entrySet()) {
                    //System.out.println("Row: " +entry.getKey()+" -> Key: " + dataa.getKey() + ", koko = " + dataa.getValue().length);
                    //String originalname = "pict" + System.nanoTime() + "." + dataa.getKey().toString();

                    try {
                        String fileuuid = UUID.nameUUIDFromBytes(dataa.getValue()).toString();
                        String picturetostore = URLTOFILESTORAGE + fileuuid + "." + dataa.getKey();
                        FileOutputStream out = new FileOutputStream(picturetostore);
                        out.getFD().sync();
                        out.write(dataa.getValue());
                        out.close();
                         
                        //System.out.println("Stored picture: " + picturetostore + ", mimetype = image/" + dataa.getKey().toString() + ", fileuuid = " + fileuuid);
                        //picnames.put(counter++, nanoname);
                        picnames.put(counter++, (fileuuid + "." + dataa.getKey()));
                        //allpicnames.put(allpicscounter++, (fileuuid + "." + dataa.getKey()));
                        
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            }
        } else {
            System.out.println("No pictures on sheet");
        }
        return picnames;
    }
	

	
	   
	    private Workbook getWorkbook(FileInputStream inputStream, String excelFilePath)
	            throws IOException {
	        Workbook workbook = null;
	     
	        if (excelFilePath.endsWith("xlsx")) {
	            workbook = new XSSFWorkbook(inputStream);
	        } else if (excelFilePath.endsWith("xls")) {
	            workbook = new HSSFWorkbook(inputStream);
	        } else {
	            throw new IllegalArgumentException("The specified file is not Excel file");
	        }
	        return workbook;
	    }

            public static void main(String[] args) throws IOException {
                StoreXLSXContentFile sxcf = new StoreXLSXContentFile();
                sxcf.createContent("contentuploadtest.xlsx");
            }
	}

