package uno.moni.onex.core.core.util

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class ExcelParser {
    fun <T> parseExcel(
        file: MultipartFile,
        clazz: Class<T>,
        headRowIndex: Int
    ): MutableList<T> {
        val result = mutableListOf<T>()
        file.inputStream.use { inputStream ->
            val workbook = getWorkbook(file, inputStream)
            val sheet = workbook.getSheetAt(0)
            for (rowIndex in headRowIndex..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex)
                if (row != null) {
                    try {
                        val obj: T = createObject<T>(clazz, row, sheet)
                        if (obj != null) {
                            result.add(obj)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        return result
    }
    @Throws(IOException::class)
    private fun getWorkbook(file: MultipartFile, inputStream: InputStream?): Workbook {
        val fileName = file.originalFilename
        return if (fileName != null && fileName.endsWith(".xlsx")) {
            XSSFWorkbook(inputStream)
        } else if (fileName != null && fileName.endsWith(".xls")) {
            HSSFWorkbook(inputStream)
        } else {
            throw IllegalArgumentException("不支持的文件格式，仅支持 .xlsx 和 .xls 文件")
        }
    }
    private fun getMergedRegionIndex(sheet: Sheet, rowIndex: Int, colIndex: Int): Int {
        val numMergedRegions = sheet.numMergedRegions
        for (i in 0..<numMergedRegions) {
            val mergedRegion = sheet.getMergedRegion(i)
            if (mergedRegion.isInRange(rowIndex, colIndex)) {
                return i
            }
        }
        return -1
    }
    private fun getCellValue(sheet: Sheet, cell: Cell?, rowIndex: Int, colIndex: Int): String {
        var cell = cell
        val mergedRegionIndex: Int = getMergedRegionIndex(sheet, rowIndex, colIndex)
        if (mergedRegionIndex != -1) {
            val mergedRegion = sheet.getMergedRegion(mergedRegionIndex)
            val firstRow = mergedRegion.firstRow
            val firstCol = mergedRegion.firstColumn
            val firstRowData = sheet.getRow(firstRow)
            cell = firstRowData.getCell(firstCol)
        }

        if (cell == null) {
            return ""
        }
        println("${cell.cellType}")
        return when (cell.cellType) {
            CellType.STRING -> cell.stringCellValue
            CellType.NUMERIC -> if (DateUtil.isCellDateFormatted(cell)) {
                cell.dateCellValue.toString()
            } else {
                cell.numericCellValue.toString()
            }

            CellType.BOOLEAN -> cell.booleanCellValue.toString()
            CellType.FORMULA -> cell.cellFormula
            else -> ""
        }
    }
    @Throws(
        NoSuchMethodException::class,
        IllegalAccessException::class,
        InvocationTargetException::class,
        InstantiationException::class
    )
    private fun <T> createObject(clazz: Class<T>, row: Row, sheet: Sheet): T {
        val constructor = clazz.getConstructor()
        val obj = constructor.newInstance()
        val fields = clazz.getDeclaredFields()
        for (i in fields.indices) {
            val field = fields[i]
            field.setAccessible(true)
            val cell = row.getCell(i)
            val cellValue: String = getCellValue(sheet, cell, row.rowNum, i)
            setFieldValue(obj, field, cellValue)
        }
        return obj
    }

    @Throws(IllegalAccessException::class)
    private fun setFieldValue(obj: Any?, field: Field, value: String) {
        val fieldType = field.type
        when (fieldType) {
            String::class.java -> {
                field.set(obj, value)
            }
            Int::class.javaPrimitiveType, Int::class.java -> {
                try {
                    field.set(obj, value.toInt())
                } catch (e: NumberFormatException) {
                    field.set(obj, 0)
                }
            }
            Double::class.javaPrimitiveType, Double::class.java -> {
                try {
                    field.set(obj, value.toDouble())
                } catch (e: NumberFormatException) {
                    field.set(obj, 0.0)
                }
            }
            Boolean::class.javaPrimitiveType, Boolean::class.java -> {
                field.set(obj, value.toBoolean())
            }
            Date::class.java -> {
                try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                    field.set(obj, sdf.parse(value))
                } catch (e: ParseException) {
                    field.set(obj, null)
                }
            }
        }
    }
}