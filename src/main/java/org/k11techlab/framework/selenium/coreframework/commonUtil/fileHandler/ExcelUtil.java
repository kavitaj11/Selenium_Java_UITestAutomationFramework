package org.k11techlab.framework.selenium.coreframework.commonUtil.fileHandler;

import org.k11techlab.framework.selenium.coreframework.exceptions.DataProviderException;
import org.k11techlab.framework.selenium.coreframework.commonUtil.StringUtil;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class ExcelUtil {
	private static int getFirstRow(Sheet s, boolean skipHeaderRow) {
		int row = 0;
		int l = s.getRows();
		for (row = 0; row < l; row++) {
			Cell[] cells = s.getRow(row);
			boolean isEmptyRow = true;
			for (Cell cell : cells) {
				if (StringUtil.isNotBlank(cell.getContents())) {
					isEmptyRow = false;
					break;
				}
			}
			if (!isEmptyRow) {
				if (!skipHeaderRow) {
					break;
				} else {
					skipHeaderRow = false;
				}
			}
		}
		return row;
	}

	private static int getFirstCol(Sheet s) {
		int l = s.getColumns();
		Cell[] cells = s.getRow(getFirstRow(s, false));
		for (int col = 0; col < l; col++) {
			Cell cell = cells[col];
			if (StringUtils.isNotBlank(cell.getContents())) {
				return col;
			}
		}
		return 0;
	}

	public static Object[][] getExcelData(String file, boolean headerRow, String sheetName) {
		Object[][] retobj = null;
		Workbook workbook = null;
		try {
			File f = new File(file);
			if (!f.exists() || !f.canRead()) {
				logger.error(" Can not read file " + f.getAbsolutePath() + " Returning empty dataset1");
				return new Object[][] {};
			}
			workbook = Workbook.getWorkbook(f);
			Sheet sheet = StringUtils.isNotBlank(sheetName) ? workbook.getSheet(sheetName) : workbook.getSheet(0);
			if (null == sheet) {
				throw new RuntimeException("Worksheet " + sheetName + " not found in " + f.getAbsolutePath());
			}
			int firstRow, firstCol, lastRow, colsCnt;
			firstRow = getFirstRow(sheet, headerRow);
			firstCol = getFirstCol(sheet);
			lastRow = sheet.getRows();
			colsCnt = sheet.getColumns();
			logger.info("Rows : " + lastRow);
			logger.info("Columns : " + colsCnt);
			retobj = new Object[lastRow - firstRow][colsCnt - firstCol];
			for (int row = firstRow; row < lastRow; row++) {
				Cell[] cells = sheet.getRow(row);
				for (int col = firstCol; col < cells.length; col++) {
					retobj[row - firstRow][col - firstCol] = cells[col].getContents();
				}
			}
		} catch (Exception e) {
			logger.error("Error while fetching dto from " + file, e);
			throw new DataProviderException("Error while fetching dto from " + file, e);
		} finally {
			try {
				workbook.close();
			} catch (Exception e2) {
				// skip exception
			}
		}
		return retobj;
	}

	/**
	 * @param file
	 * @param headerRow
	 * @return
	 */
	public static Object[][] getExcelData(String file, boolean headerRow) {
		return getExcelData(file, headerRow, "");
	}

	public static String[][] getTableData(String xlFilePath, String tableName, String sheetName) {
		String[][] tabArray = null;
		Workbook workbook = null;
		try {
			File f = new File(xlFilePath);
			if (!f.exists() || !f.canRead()) {
				logger.error(" Can not read file " + f.getAbsolutePath() + " Returning empty dataset1");
				return new String[][] {};
			}
			workbook = Workbook.getWorkbook(f);
			Sheet sheet = StringUtils.isNotBlank(sheetName) ? workbook.getSheet(sheetName) : workbook.getSheet(0);
			if (null == sheet) {
				throw new RuntimeException("Worksheet " + sheetName + " not found in " + f.getAbsolutePath());
			}

			int startRow, startCol, endRow, endCol, ci, cj;
			Cell tableStart = sheet.findCell(tableName);
			if (null == tableStart) {
				throw new RuntimeException(
						"Lable " + tableName + " for starting dto range not found in sheet " + sheet.getName());
			}

			startRow = tableStart.getRow();
			startCol = tableStart.getColumn();
			Cell tableEnd = sheet.findCell(tableName, startCol + 1, startRow + 1, 100, 64000, false);
			if (null == tableEnd) {
				throw new RuntimeException(
						"Lable " + tableName + " for ending dto range not found in sheet " + sheet.getName());
			}
			endRow = tableEnd.getRow();
			endCol = tableEnd.getColumn();
			logger.debug("startRow=" + startRow + ", endRow=" + endRow + ", " + "startCol=" + startCol + ", endCol="
					+ endCol);
			tabArray = new String[endRow - startRow - 1][endCol - startCol - 1];
			ci = 0;

			for (int i = startRow + 1; i < endRow; i++, ci++) {
				cj = 0;
				for (int j = startCol + 1; j < endCol; j++, cj++) {
					tabArray[ci][cj] = sheet.getCell(j, i).getContents();
				}
			}
		} catch (Exception e) {
			logger.error("error while fetching dto from " + xlFilePath, e);
			throw new DataProviderException("Error while fetching dto from " + xlFilePath, e);

		} finally {
			try {
				workbook.close();
			} catch (Exception e2) {
				// skip exception
			}
		}
		return (tabArray);
	}

	private static final Log logger = LogFactoryImpl.getLog(ExcelUtil.class);

	public static Object[][] getExcelDataAsMap(String file, String sheetName) {
		Object[][] retobj = null;
		Workbook workbook = null;
		try {
			File f = new File(file);
			if (!f.exists() || !f.canRead()) {
				logger.error(" Can not read file " + f.getAbsolutePath() + " Returning empty dataset1");
				return new Object[][] {};
			}
			workbook = Workbook.getWorkbook(f);
			Sheet sheet = StringUtils.isNotBlank(sheetName) ? workbook.getSheet(sheetName) : workbook.getSheet(0);
			if (null == sheet) {
				throw new RuntimeException("Worksheet " + sheetName + " not found in " + f.getAbsolutePath());
			}
			int firstRow, firstCol, lastRow, colsCnt;
			firstRow = getFirstRow(sheet, false);
			firstCol = getFirstCol(sheet);
			lastRow = sheet.getRows();
			colsCnt = sheet.getColumns();
			String[] colNames = new String[colsCnt - firstCol];

			logger.info("Rows : " + lastRow);
			logger.info("Columns : " + colsCnt);
			retobj = new Object[lastRow - (firstRow + 1)][1]; // skipped header
																// row
			for (int row = firstRow; row < lastRow; row++) {
				Cell[] cells = sheet.getRow(row);
				if (row == firstRow) {
					for (int col = firstCol; col < (firstCol + cells.length); col++) {
						colNames[col - firstCol] = cells[col].getContents().trim();
					}
				} else {
					HashMap<String, String> map = new HashMap<String, String>();
					for (int col = firstCol; col < (firstCol + cells.length); col++) {
						map.put(colNames[col - firstCol], cells[col].getContents());
					}
					retobj[row - (firstRow + 1)][0] = map;
				}
			}
		} catch (Exception e) {
			logger.error("Error while fetching dto from " + file, e);
			throw new DataProviderException("Error while fetching dto from " + file, e);
		} finally {
			try {
				workbook.close();
			} catch (Exception e2) {
				// skip exception
			}
		}
		return retobj;

	}

	public static Object[][] getTableDataAsMap(String xlFilePath, String tableName, String sheetName) {
		Object[][] tabArray = null;
		Workbook workbook = null;
		try {
			File f = new File(xlFilePath);
			if (!f.exists() || !f.canRead()) {
				logger.error(" Can not read file " + f.getAbsolutePath() + " Returning empty dataset1");
				return new String[][] {};
			}
			workbook = Workbook.getWorkbook(f);
			Sheet sheet = StringUtils.isNotBlank(sheetName) ? workbook.getSheet(sheetName) : workbook.getSheet(0);
			if (null == sheet) {
				throw new RuntimeException("Worksheet " + sheetName + " not found in " + f.getAbsolutePath());
			}

			int startRow, startCol, endRow, endCol, ci, cj;
			Cell tableStart = sheet.findCell(tableName);
			if (null == tableStart) {
				throw new RuntimeException(
						"Lable " + tableName + " for starting dto range not found in sheet " + sheet.getName());
			}

			startRow = tableStart.getRow();
			startCol = tableStart.getColumn();
			Cell tableEnd = sheet.findCell(tableName, startCol + 1, startRow + 1, 100, 64000, false);
			if (null == tableEnd) {
				throw new RuntimeException(
						"Lable " + tableName + " for ending dto range not found in sheet " + sheet.getName());
			}
			endRow = tableEnd.getRow();
			endCol = tableEnd.getColumn();
			logger.debug("startRow=" + startRow + ", endRow=" + endRow + ", " + "startCol=" + startCol + ", endCol="
					+ endCol);
			tabArray = new Object[endRow - startRow][1];
			ci = 0;
			String[] colNames = new String[endCol - startCol - 1];

			for (int i = startRow; i <= endRow; i++) {
				cj = 0;
				if (i == (startRow)) {
					for (int j = startCol + 1; j < endCol; j++, cj++) {
						colNames[cj] = sheet.getCell(j, i).getContents().trim();
						logger.debug("header[" + cj + "] : " + colNames[cj]);

					}
				} else {
					HashMap<String, String> map = new HashMap<String, String>();
					for (int j = startCol + 1; j < endCol; j++, cj++) {
						map.put(colNames[cj], sheet.getCell(j, i).getContents());
					}
					logger.debug("Record " + ci + ":" + map);
					tabArray[ci++][0] = map;

				}
			}
		} catch (Exception e) {
			logger.error("error while fetching dto from " + xlFilePath, e);
			throw new DataProviderException("Error while fetching dto from " + xlFilePath, e);

		} finally {
			try {
				workbook.close();
			} catch (Exception e2) {
				// skip exception
			}
		}

		return (tabArray);
	}

	public static void writeInExcelFile(String filePath, String data, int rowIndex, int colIndex) throws IOException {

			// Open existing file as an input stream
		FileInputStream fileInputStream = new FileInputStream(filePath);
		// create an object of Workbook and pass the FileInputStream object into it
		HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
		// use getSheetAt() to pass sheet number. Here index is 0.
		HSSFSheet sheet = workbook.getSheetAt(0);
		// Create a cell where we want to enter a value.
		Row row;
		// If there's no existing row, create one.
		if (sheet.getPhysicalNumberOfRows() < rowIndex + 1) {
			row = sheet.createRow(rowIndex);
		} else {
			row = sheet.getRow(rowIndex);
		}
		org.apache.poi.ss.usermodel.Cell cell = row.createCell(colIndex);
		// Now we need to find out the type of the value we want to enter.
		// For a string, we need to set the cell type as string
		cell.setCellType(CellType.STRING);
		cell.setCellValue(data);
		FileOutputStream fileOutputStream = new FileOutputStream(filePath);
		workbook.write(fileOutputStream);
		fileOutputStream.close();
	}

	public static String getCellData(String filePath, String colName, int rowNum) throws IOException {
		// Open existing file as an input stream
		FileInputStream fileInputStream = new FileInputStream(filePath);
		// create an object of Workbook and pass the FileInputStream object into it
		HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
		// use getSheetAt() to pass sheet number. Here index is 0.
		HSSFSheet sheet = workbook.getSheetAt(0);
		HSSFRow row = null;
		HSSFCell cell = null;
		int colNum=0;
		try
		{
			colNum = -1;
			row = sheet.getRow(0);
			for(int i = 0; i < row.getLastCellNum(); i++)
			{
				if(row.getCell(i).getStringCellValue().trim().equals(colName.trim()))
					colNum = i;
			}

			row = sheet.getRow(rowNum - 1);
			cell = row.getCell(colNum);

			return String.valueOf(cell.getStringCellValue());
			}
		catch(Exception e)
		{
			e.printStackTrace();
			return "row "+rowNum+" or column "+colNum +" does not exist  in Excel";
		}

	}

	public static int getColumnIndexFromColumnName(String filePath, String colName) throws IOException {
        // Open existing file as an input stream
        FileInputStream fileInputStream = new FileInputStream(filePath);
        // create an object of Workbook and pass the FileInputStream object into it
        HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
        // use getSheetAt() to pass sheet number. Here index is 0.
        HSSFSheet sheet = workbook.getSheetAt(0);
        HSSFRow row = null;
        HSSFCell cell = null;
        int colNum = 0;
        try {
            colNum = -1;
            row = sheet.getRow(0);
            for (int i = 0; i < row.getLastCellNum(); i++) {
                if (row.getCell(i).getStringCellValue().trim().equals(colName.trim())) {
					colNum = i;
					break;
				}
            }
            }catch(Exception ex) {
            ex.printStackTrace();
            }
      return colNum;
    }


	public static int getRowCount(String filePath) throws IOException {

		// Open existing file as an input stream
		FileInputStream fileInputStream = new FileInputStream(filePath);
		// create an object of Workbook and pass the FileInputStream object into it
		HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
		// use getSheetAt() to pass sheet number. Here index is 0.
		HSSFSheet sheet = workbook.getSheetAt(0);
		int rowCount = 0;
		Iterator<Row> iter = sheet.rowIterator();

		while (iter.hasNext()) {
			Row r = iter.next();
			if (!isRowEmpty(filePath, r)) {
				rowCount = r.getRowNum();
			}
		}

		return rowCount;
	}

	/**
	 * Determine whether a row is effectively completely empty - i.e. all cells either contain an empty string or nothing.
	 */
	private static  boolean isRowEmpty( String filePath, Row row ) throws IOException {
		if( row == null ){
			return true;
		}

		int cellCount = row.getLastCellNum() + 1;
		for( int i = 0; i < cellCount; i++ ){
			String cellValue = getCellValue( filePath, row, i );
			if( cellValue != null && cellValue.length() > 0 ){
				return false;
			}
		}
		return true;
	}

	/**
	 * Get the effective value of a cell, formatted according to the formatting of the cell.
	 * If the cell contains a formula, it is evaluated first, then the result is formatted.
	 *
	 * @param row the row
	 * @param columnIndex the cell's column index
	 * @return the cell's value
	 */
	private static String getCellValue(String filepath, Row row, int columnIndex ) throws IOException {
		// Open existing file as an input stream
		FileInputStream fileInputStream = new FileInputStream(filepath);
		// create an object of Workbook and pass the FileInputStream object into it
		HSSFWorkbook workbook = new HSSFWorkbook(fileInputStream);
		DataFormatter formatter = new DataFormatter( true );
		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
		String cellValue;
		org.apache.poi.ss.usermodel.Cell cell = row.getCell( columnIndex );
		if( cell == null ){
			// no dto in this cell
			cellValue = null;
		}
		else{
			if( cell.getCellTypeEnum()!= CellType.FORMULA ){
				// cell has a value, so format it into a string
				cellValue = formatter.formatCellValue( cell );
			}
			else {
				// cell has a formula, so evaluate it
				cellValue = formatter.formatCellValue( cell, evaluator );
			}
		}
		return cellValue;
	}


}
