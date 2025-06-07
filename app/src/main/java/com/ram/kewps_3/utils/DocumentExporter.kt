package com.ram.kewps_3.utils

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.ram.kewps_3.data.StockItem
import com.ram.kewps_3.data.Transaction
import org.apache.poi.xwpf.usermodel.*
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber
import java.io.File
import java.io.FileOutputStream
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*

class DocumentExporter(private val context: Context) {
    
    fun exportKEWPS3Document(stockItem: StockItem, transactions: List<Transaction>): File? {
        return try {
            val document = XWPFDocument()
            
            // Create document header
            createDocumentHeader(document, stockItem)
            
            // Create Section A (Stock Information)
            createSectionA(document, stockItem)
            
            // Create Section B (Transactions)
            createSectionB(document, stockItem, transactions)
            
            // Save document
            val fileName = "KEW_PS3_${stockItem.cardNo}_${stockItem.stockDescription.replace(Regex("[^a-zA-Z0-9]"), "_")}.docx"
            val documentsDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "KEW_PS3_Exports")
            if (!documentsDir.exists()) {
                documentsDir.mkdirs()
            }
            
            val file = File(documentsDir, fileName)
            val outputStream = FileOutputStream(file)
            document.write(outputStream)
            outputStream.close()
            document.close()
            
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun createDocumentHeader(document: XWPFDocument, stockItem: StockItem) {
        val headerParagraph = document.createParagraph()
        headerParagraph.alignment = ParagraphAlignment.CENTER
        val headerRun = headerParagraph.createRun()
        headerRun.setBold(true)
        headerRun.fontSize = 14
        headerRun.setText("DAFTAR STOK")
        
        val infoParagraph = document.createParagraph()
        val infoRun = infoParagraph.createRun()
        infoRun.setText("Nama Stor: ${stockItem.storeName}")
        infoRun.addTab()
        infoRun.addTab()
        infoRun.setText("Perihal Stok: ${stockItem.stockDescription}")
    }
    
    private fun createSectionA(document: XWPFDocument, stockItem: StockItem) {
        // Section A Header
        val sectionAParagraph = document.createParagraph()
        val sectionARun = sectionAParagraph.createRun()
        sectionARun.setBold(true)
        sectionARun.fontSize = 12
        sectionARun.setText("BAHAGIAN A")
        
        val cardNoParagraph = document.createParagraph()
        cardNoParagraph.alignment = ParagraphAlignment.RIGHT
        val cardNoRun = cardNoParagraph.createRun()
        cardNoRun.setBold(true)
        cardNoRun.setText("KEW.PS-3 No.Kad: ${stockItem.cardNo}")
        
        // Basic info table
        val basicTable = document.createTable(2, 4)
        basicTable.setWidth("100%")
        
        val headerRow = basicTable.getRow(0)
        setCellText(headerRow.getCell(0), "No. Kod", true)
        setCellText(headerRow.getCell(1), "Unit Pengukuran", true)
        setCellText(headerRow.getCell(2), "Kumpulan", true)
        setCellText(headerRow.getCell(3), "Pergerakan", true)
        
        val dataRow = basicTable.getRow(1)
        setCellText(dataRow.getCell(0), stockItem.codeNo, false)
        setCellText(dataRow.getCell(1), stockItem.unitMeasurement, false)
        setCellText(dataRow.getCell(2), stockItem.group, false)
        setCellText(dataRow.getCell(3), stockItem.movement, false)
        
        // Location table
        val locationTable = document.createTable(3, 6)
        locationTable.setWidth("100%")
        
        val locationHeaderRow = locationTable.getRow(0)
        setCellText(locationHeaderRow.getCell(0), "Lokasi Penyimpanan Stok", true)
        // Set colspan for the first cell to span across all 6 columns
        locationHeaderRow.getCell(0).ctTc.tcPr.gridSpan = CTDecimalNumber.Factory.newInstance()
        locationHeaderRow.getCell(0).ctTc.tcPr.gridSpan.`val` = BigInteger.valueOf(6)
        // Remove the other cells since they're now spanned
        for (i in 1 until 6) {
            locationHeaderRow.removeCell(1)
        }
        
        val locationLabelRow = locationTable.getRow(1)
        setCellText(locationLabelRow.getCell(0), "Gudang/Seksyen", true)
        setCellText(locationLabelRow.getCell(1), "Baris", true)
        setCellText(locationLabelRow.getCell(2), "Rak", true)
        setCellText(locationLabelRow.getCell(3), "Tingkat", true)
        setCellText(locationLabelRow.getCell(4), "Petak", true)
        setCellText(locationLabelRow.getCell(5), "Kod Lokasi Penuh", true)
        
        val locationDataRow = locationTable.getRow(2)
        setCellText(locationDataRow.getCell(0), stockItem.warehouse, false)
        setCellText(locationDataRow.getCell(1), stockItem.row, false)
        setCellText(locationDataRow.getCell(2), stockItem.rack, false)
        setCellText(locationDataRow.getCell(3), stockItem.level, false)
        setCellText(locationDataRow.getCell(4), stockItem.compartment, false)
        setCellText(locationDataRow.getCell(5), "${stockItem.warehouse}-${stockItem.row}/${stockItem.rack}/${stockItem.level}/${stockItem.compartment}", false)
        
        // Stock levels table
        val stockLevelsTable = document.createTable(2, 4)
        stockLevelsTable.setWidth("100%")
        
        val stockHeaderRow = stockLevelsTable.getRow(0)
        setCellText(stockHeaderRow.getCell(0), "PARAS STOK", true)
        setCellText(stockHeaderRow.getCell(1), "MAKSIMUM\n(Kuantiti)", true)
        setCellText(stockHeaderRow.getCell(2), "MENOKOK\n(Kuantiti)", true)
        setCellText(stockHeaderRow.getCell(3), "MINIMUM\n(Kuantiti)", true)
        
        val stockDataRow = stockLevelsTable.getRow(1)
        setCellText(stockDataRow.getCell(0), "", false)
        setCellText(stockDataRow.getCell(1), stockItem.maxStock.toString(), false)
        setCellText(stockDataRow.getCell(2), stockItem.reorderStock.toString(), false)
        setCellText(stockDataRow.getCell(3), stockItem.minStock.toString(), false)
    }
    
    private fun createSectionB(document: XWPFDocument, stockItem: StockItem, transactions: List<Transaction>) {
        // Section B Header
        val sectionBParagraph = document.createParagraph()
        val sectionBRun = sectionBParagraph.createRun()
        sectionBRun.setBold(true)
        sectionBRun.fontSize = 12
        sectionBRun.setText("BAHAGIAN B")
        
        val transactionsParagraph = document.createParagraph()
        val transactionsRun = transactionsParagraph.createRun()
        transactionsRun.setBold(true)
        transactionsRun.fontSize = 11
        transactionsRun.setText("Transaksi Stok")
        
        // Transactions table
        val transactionTable = document.createTable(transactions.size + 3, 11)
        transactionTable.setWidth("100%")
        
        // Header row 1
        val headerRow1 = transactionTable.getRow(0)
        setCellText(headerRow1.getCell(0), "Tarikh", true)
        setCellText(headerRow1.getCell(1), "No. PK/\nBTB/\nBPSS/\nBPSI/\nBPIN", true)
        setCellText(headerRow1.getCell(2), "Terima Daripada/\nKeluar Kepada", true)
        setCellText(headerRow1.getCell(3), "TERIMAAN", true)
        setCellText(headerRow1.getCell(6), "KELUARAN", true)
        setCellText(headerRow1.getCell(8), "BAKI", true)
        setCellText(headerRow1.getCell(10), "Nama Pegawai", true)
        
        // Header row 2
        val headerRow2 = transactionTable.getRow(1)
        setCellText(headerRow2.getCell(3), "Kuantiti", true)
        setCellText(headerRow2.getCell(4), "Seunit (RM)", true)
        setCellText(headerRow2.getCell(5), "Jumlah (RM)", true)
        setCellText(headerRow2.getCell(6), "Kuantiti", true)
        setCellText(headerRow2.getCell(7), "Jumlah (RM)", true)
        setCellText(headerRow2.getCell(8), "Kuantiti", true)
        setCellText(headerRow2.getCell(9), "Jumlah (RM)", true)
        
        // Balance forward row
        val balanceRow = transactionTable.getRow(2)
        setCellText(balanceRow.getCell(0), "Baki dibawa ke hadapan...........................", false)
        for (i in 1..10) {
            setCellText(balanceRow.getCell(i), "", false)
        }
        
        // Transaction rows
        var runningBalance = 0
        transactions.forEachIndexed { index, transaction ->
            val row = transactionTable.getRow(index + 3)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            
            setCellText(row.getCell(0), dateFormat.format(Date(transaction.date)), false)
            setCellText(row.getCell(1), "${transaction.documentType}-${transaction.documentNo}", false)
            setCellText(row.getCell(2), if (transaction.type == "terimaan") transaction.receivedFrom else transaction.issuedTo, false)
            
            if (transaction.type == "terimaan") {
                setCellText(row.getCell(3), transaction.quantity.toString(), false)
                setCellText(row.getCell(4), String.format("%.2f", transaction.unitPrice), false)
                setCellText(row.getCell(5), String.format("%.2f", transaction.totalPrice), false)
                setCellText(row.getCell(6), "", false)
                setCellText(row.getCell(7), "", false)
                runningBalance += transaction.quantity
            } else {
                setCellText(row.getCell(3), "", false)
                setCellText(row.getCell(4), "", false)
                setCellText(row.getCell(5), "", false)
                setCellText(row.getCell(6), transaction.quantity.toString(), false)
                setCellText(row.getCell(7), String.format("%.2f", transaction.totalPrice), false)
                runningBalance -= transaction.quantity
            }
            
            setCellText(row.getCell(8), runningBalance.toString(), false)
            setCellText(row.getCell(9), String.format("%.2f", runningBalance * transaction.unitPrice), false)
            setCellText(row.getCell(10), transaction.officerName, false)
        }
        
        // Add notes
        val notesParagraph = document.createParagraph()
        val notesRun = notesParagraph.createRun()
        notesRun.setBold(true)
        notesRun.fontSize = 10
        notesRun.setText("Nota:")
        notesRun.addBreak()
        notesRun.setText("PK = Pesanan Kerajaan")
        notesRun.addBreak()
        notesRun.setText("BTB = Borang Terimaan Barang-barang")
        notesRun.addBreak()
        notesRun.setText("BPSS = Borang Permohonan Stok (KEW.PS-7)")  
        notesRun.addBreak()
        notesRun.setText("BPSI = Borang Permohonan Stok (KEW.PS-8)")
        notesRun.addBreak()
        notesRun.setText("BPIN = Borang Pindahan Stok (KEW.PS-17)")
    }
    
    private fun setCellText(cell: XWPFTableCell, text: String, isBold: Boolean) {
        val paragraph = cell.paragraphs[0]
        paragraph.alignment = ParagraphAlignment.CENTER
        val run = paragraph.createRun()
        run.setText(text)
        run.setBold(isBold)
        run.fontSize = 11
    }
    
    fun shareDocument(file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(intent, "Kongsi Dokumen KEW.PS-3"))
    }
} 