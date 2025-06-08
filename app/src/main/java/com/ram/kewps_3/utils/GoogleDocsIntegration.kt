package com.ram.kewps_3.utils

import android.content.Context
import android.content.Intent
import android.content.ClipData
import android.content.ClipboardManager
import android.net.Uri
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.ram.kewps_3.data.StockItem
import com.ram.kewps_3.data.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.json.JSONArray
import com.google.android.gms.auth.GoogleAuthUtil

class GoogleDocsIntegration(private val context: Context) {
    
    companion object {
        private const val TAG = "GoogleDocsIntegration"
        private const val DOCUMENT_ID = "1pbYb5NFCo0Oh2a4--9aGyT7yq7Irox_1"
        private const val DOCS_API_BASE = "https://docs.googleapis.com/v1/documents"
    }
    
    private var lastGeneratedContent: String = ""
    private val httpClient = OkHttpClient()
    
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(
                Scope("https://www.googleapis.com/auth/documents"),
                Scope("https://www.googleapis.com/auth/drive.file")
            )
            .build()
        GoogleSignIn.getClient(context, gso)
    }
    
    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }
    
    fun isSignedIn(): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        return account != null && GoogleSignIn.hasPermissions(
            account, 
            Scope("https://www.googleapis.com/auth/documents"),
            Scope("https://www.googleapis.com/auth/drive.file")
        )
    }
    
    fun handleSignInResult(account: GoogleSignInAccount?) {
        Log.d(TAG, "Sign-in result handled: ${account?.email}")
    }
    
    suspend fun populateDocument(stockItem: StockItem, transactions: List<Transaction>): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val account = GoogleSignIn.getLastSignedInAccount(context)
                    ?: return@withContext Result.failure(Exception("Sila log masuk terlebih dahulu"))
                
                Log.d(TAG, "Starting document population for account: ${account.email}")
                
                // Generate the formatted content first
                val formattedContent = generateKEWPS3Content(stockItem, transactions)
                lastGeneratedContent = formattedContent
                
                // Try API approach first
                try {
                    Log.d(TAG, "Attempting Google Docs API approach...")
                    val accessToken = getAccessToken(account)
                    Log.d(TAG, "Access token obtained successfully")
                    
                    // Test document access first
                    testDocumentAccess(accessToken)
                    Log.d(TAG, "Document access test passed")
                    
                    // Clear existing content and insert new content
                    clearAndInsertContent(accessToken, formattedContent)
                    Log.d(TAG, "Content inserted successfully via API")
                    
                    // Open the document
                    openDocument()
                    
                    Result.success("Dokumen KEW.PS-3 telah berjaya diisi dan dibuka melalui API.")
                    
                } catch (apiError: Exception) {
                    Log.w(TAG, "API approach failed, falling back to clipboard method", apiError)
                    
                    // Fallback to clipboard + manual paste method
                    copyToClipboard(formattedContent)
                    openDocument()
                    
                    Result.success("Dokumen dibuka dan data disalin ke clipboard. Tekan dan tahan dalam dokumen, kemudian pilih 'Tampal' untuk memasukkan data.")
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Complete failure in document population", e)
                Result.failure(Exception("Gagal memproses dokumen: ${e.message}"))
            }
        }
    }
    
    private fun openDocument() {
        val googleDocsUrl = "https://docs.google.com/document/d/$DOCUMENT_ID/edit"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleDocsUrl))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
    
    private suspend fun testDocumentAccess(accessToken: String) {
        Log.d(TAG, "Testing document access...")
        try {
            getDocument(accessToken)
            Log.d(TAG, "Document access test successful")
        } catch (e: Exception) {
            Log.e(TAG, "Document access test failed", e)
            throw Exception("Gagal mengakses dokumen. Sila pastikan dokumen boleh diedit oleh akaun Google anda.")
        }
    }
    
    private suspend fun getAccessToken(account: GoogleSignInAccount): String {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Getting access token for account: ${account.email}")
                val token = GoogleAuthUtil.getToken(
                    context,
                    account.account!!,
                    "oauth2:https://www.googleapis.com/auth/documents https://www.googleapis.com/auth/drive.file"
                )
                Log.d(TAG, "Access token obtained, length: ${token.length}")
                token
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get access token", e)
                throw Exception("Gagal mendapatkan token akses: ${e.message}")
            }
        }
    }
    
    private suspend fun clearAndInsertContent(accessToken: String, content: String) {
        // First, get the document to find the end index
        val document = getDocument(accessToken)
        val endIndex = document.getJSONObject("body").getInt("endIndex") - 1
        
        // Create batch update request
        val requests = JSONArray()
        
        // Delete all existing content (except the last newline character)
        if (endIndex > 1) {
            val deleteRequest = JSONObject().apply {
                put("deleteContentRange", JSONObject().apply {
                    put("range", JSONObject().apply {
                        put("startIndex", 1)
                        put("endIndex", endIndex)
                    })
                })
            }
            requests.put(deleteRequest)
        }
        
        // Insert new content
        val insertRequest = JSONObject().apply {
            put("insertText", JSONObject().apply {
                put("location", JSONObject().apply {
                    put("index", 1)
                })
                put("text", content)
            })
        }
        requests.put(insertRequest)
        
        // Execute batch update
        val batchUpdateBody = JSONObject().apply {
            put("requests", requests)
        }
        
        val requestBody = batchUpdateBody.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$DOCS_API_BASE/$DOCUMENT_ID:batchUpdate")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()
        
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Failed to update document: ${response.code} ${response.message}")
            }
        }
    }
    
    private suspend fun getDocument(accessToken: String): JSONObject {
        val request = Request.Builder()
            .url("$DOCS_API_BASE/$DOCUMENT_ID")
            .addHeader("Authorization", "Bearer $accessToken")
            .get()
            .build()
        
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Failed to get document: ${response.code} ${response.message}")
            }
            
            return JSONObject(response.body!!.string())
        }
    }
    
    private fun copyToClipboard(text: String) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("KEW.PS-3 Data", text)
            clipboard.setPrimaryClip(clip)
        } catch (e: Exception) {
            Log.e(TAG, "Error copying to clipboard", e)
        }
    }
    
    private fun generateKEWPS3Content(stockItem: StockItem, transactions: List<Transaction>): String {
        val sb = StringBuilder()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        
        // Document header with proper formatting
        sb.appendLine("DOKUMEN KEW.PS-3")
        sb.appendLine("SISTEM PENGURUSAN STOK")
        sb.appendLine("Pekeliling Perbendaharaan Malaysia AM 6.3")
        sb.appendLine("================================================================")
        sb.appendLine()
        
        // BAHAGIAN A - Stock Information
        sb.appendLine("BAHAGIAN A - MAKLUMAT STOK")
        sb.appendLine("===================================")
        sb.appendLine()
        
        // Basic Information
        sb.appendLine("MAKLUMAT ASAS:")
        sb.appendLine("No. Kad Stok        : ${stockItem.cardNo}")
        sb.appendLine("Nama Stor           : ${stockItem.storeName}")
        sb.appendLine("Perihal Stok        : ${stockItem.stockDescription}")
        sb.appendLine("No. Kod             : ${stockItem.codeNo}")
        sb.appendLine("Unit Pengukuran     : ${stockItem.unitMeasurement}")
        sb.appendLine("Kumpulan            : ${stockItem.group}")
        sb.appendLine("Status Pergerakan   : ${stockItem.movement}")
        sb.appendLine()
        
        // Location Information
        sb.appendLine("LOKASI PENYIMPANAN:")
        sb.appendLine("Gudang              : ${stockItem.warehouse}")
        sb.appendLine("Baris               : ${stockItem.row}")
        sb.appendLine("Rak                 : ${stockItem.rack}")
        sb.appendLine("Tingkat             : ${stockItem.level}")
        sb.appendLine("Petak               : ${stockItem.compartment}")
        sb.appendLine()
        
        // Stock Levels
        sb.appendLine("PARAS STOK:")
        sb.appendLine("Stok Maksimum       : ${stockItem.maxStock} ${stockItem.unitMeasurement}")
        sb.appendLine("Stok Reorder        : ${stockItem.reorderStock} ${stockItem.unitMeasurement}")
        sb.appendLine("Stok Minimum        : ${stockItem.minStock} ${stockItem.unitMeasurement}")
        sb.appendLine("Baki Semasa         : ${stockItem.currentBalance} ${stockItem.unitMeasurement}")
        sb.appendLine("Jumlah Diterima     : ${stockItem.totalReceived} ${stockItem.unitMeasurement}")
        sb.appendLine("Jumlah Dikeluarkan  : ${stockItem.totalIssued} ${stockItem.unitMeasurement}")
        sb.appendLine()
        
        // BAHAGIAN B - Transactions
        if (transactions.isNotEmpty()) {
            sb.appendLine("BAHAGIAN B - TRANSAKSI STOK")
            sb.appendLine("===================================")
            sb.appendLine()
            
            // Transaction summary
            val totalReceipts = transactions.filter { it.type == "terimaan" }.sumOf { it.quantity }
            val totalIssues = transactions.filter { it.type == "keluaran" }.sumOf { it.quantity }
            val totalValue = transactions.sumOf { it.totalPrice }
            
            sb.appendLine("RINGKASAN TRANSAKSI:")
            sb.appendLine("Jumlah Terimaan     : $totalReceipts ${stockItem.unitMeasurement}")
            sb.appendLine("Jumlah Keluaran     : $totalIssues ${stockItem.unitMeasurement}")
            sb.appendLine("Jumlah Nilai        : RM ${String.format("%.2f", totalValue)}")
            sb.appendLine("Bilangan Transaksi  : ${transactions.size}")
            sb.appendLine()
            
            // Transaction details
            sb.appendLine("SENARAI TRANSAKSI:")
            sb.appendLine("================================================================")
            
            transactions.sortedBy { it.date }.forEach { transaction ->
                val date = dateFormat.format(Date(transaction.date))
                val party = if (transaction.type == "terimaan") transaction.receivedFrom else transaction.issuedTo
                
                sb.appendLine("Tarikh     : $date")
                sb.appendLine("Jenis      : ${transaction.type.uppercase()}")
                sb.appendLine("No. Dokumen: ${transaction.documentType} - ${transaction.documentNo}")
                sb.appendLine("Kuantiti   : ${transaction.quantity} ${stockItem.unitMeasurement}")
                sb.appendLine("Harga/Unit : RM ${String.format("%.2f", transaction.unitPrice)}")
                sb.appendLine("Jumlah     : RM ${String.format("%.2f", transaction.totalPrice)}")
                sb.appendLine("Pihak      : $party")
                sb.appendLine("Pegawai    : ${transaction.officerName}")
                sb.appendLine("----------------------------------------------------------------")
            }
            sb.appendLine()
            
            // Quarterly summary (if applicable)
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            sb.appendLine("RINGKASAN SUKU TAHUNAN ($currentYear):")
            sb.appendLine("================================================================")
            
            val quarterlyData = calculateQuarterlyData(transactions, currentYear)
            quarterlyData.forEach { (quarter, data) ->
                sb.appendLine("$quarter:")
                sb.appendLine("  Terimaan  : ${data.receiptQty} unit (RM ${String.format("%.2f", data.receiptValue)})")
                sb.appendLine("  Keluaran  : ${data.issueQty} unit (RM ${String.format("%.2f", data.issueValue)})")
                sb.appendLine()
            }
        }
        
        // Footer
        sb.appendLine("================================================================")
        sb.appendLine("Dihasilkan oleh: Sistem KEW.PS-3 Android")
        sb.appendLine("Tarikh Dijana  : ${SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())}")
        sb.appendLine("Referens       : M.S. 6/13")
        sb.appendLine()
        sb.appendLine("NOTA: Dokumen ini dijana secara automatik.")
        sb.appendLine("Sila semak ketepatan data sebelum kegunaan rasmi.")
        
        return sb.toString()
    }
    
    private fun calculateQuarterlyData(transactions: List<Transaction>, year: Int): Map<String, QuarterlyData> {
        val quarterlyMap = mutableMapOf<String, QuarterlyData>()
        
        for (i in 1..4) {
            quarterlyMap["Suku $i"] = QuarterlyData()
        }
        
        transactions.forEach { transaction ->
            val calendar = Calendar.getInstance().apply { timeInMillis = transaction.date }
            if (calendar.get(Calendar.YEAR) == year) {
                val month = calendar.get(Calendar.MONTH) + 1
                val quarter = when (month) {
                    in 1..3 -> "Suku 1"
                    in 4..6 -> "Suku 2"
                    in 7..9 -> "Suku 3"
                    else -> "Suku 4"
                }
                
                val data = quarterlyMap[quarter]!!
                if (transaction.type == "terimaan") {
                    data.receiptQty += transaction.quantity
                    data.receiptValue += transaction.totalPrice
                } else {
                    data.issueQty += transaction.quantity
                    data.issueValue += transaction.totalPrice
                }
            }
        }
        
        return quarterlyMap
    }
    
    data class QuarterlyData(
        var receiptQty: Int = 0,
        var receiptValue: Double = 0.0,
        var issueQty: Int = 0,
        var issueValue: Double = 0.0
    )
    
    suspend fun signOut() {
        withContext(Dispatchers.IO) {
            googleSignInClient.signOut()
        }
    }
    
    fun getLastGeneratedContent(): String {
        return lastGeneratedContent
    }
    
    // Alternative method: Create a new document instead of editing existing one
    suspend fun createNewDocument(stockItem: StockItem, transactions: List<Transaction>): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val account = GoogleSignIn.getLastSignedInAccount(context)
                    ?: return@withContext Result.failure(Exception("Sila log masuk terlebih dahulu"))
                
                Log.d(TAG, "Creating new document for account: ${account.email}")
                
                val formattedContent = generateKEWPS3Content(stockItem, transactions)
                lastGeneratedContent = formattedContent
                
                val accessToken = getAccessToken(account)
                val documentTitle = "KEW.PS-3 - ${stockItem.stockDescription} - ${SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())}"
                
                val newDocId = createDocument(accessToken, documentTitle, formattedContent)
                
                // Open the new document
                val googleDocsUrl = "https://docs.google.com/document/d/$newDocId/edit"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleDocsUrl))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                
                Result.success("Dokumen KEW.PS-3 baru telah berjaya dibuat dan dibuka.")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error creating new document", e)
                Result.failure(Exception("Gagal membuat dokumen baru: ${e.message}"))
            }
        }
    }
    
    private suspend fun createDocument(accessToken: String, title: String, content: String): String {
        Log.d(TAG, "Creating new document with title: $title")
        
        // Create document
        val createBody = JSONObject().apply {
            put("title", title)
        }
        
        val createRequest = Request.Builder()
            .url(DOCS_API_BASE)
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .post(createBody.toString().toRequestBody("application/json".toMediaType()))
            .build()
        
        val documentId = httpClient.newCall(createRequest).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Failed to create document: ${response.code} ${response.message}")
            }
            val responseJson = JSONObject(response.body!!.string())
            responseJson.getString("documentId")
        }
        
        Log.d(TAG, "Document created with ID: $documentId")
        
        // Insert content
        val insertRequest = JSONObject().apply {
            put("requests", JSONArray().apply {
                put(JSONObject().apply {
                    put("insertText", JSONObject().apply {
                        put("location", JSONObject().apply {
                            put("index", 1)
                        })
                        put("text", content)
                    })
                })
            })
        }
        
        val updateRequest = Request.Builder()
            .url("$DOCS_API_BASE/$documentId:batchUpdate")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .post(insertRequest.toString().toRequestBody("application/json".toMediaType()))
            .build()
        
        httpClient.newCall(updateRequest).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Failed to insert content: ${response.code} ${response.message}")
            }
        }
        
        Log.d(TAG, "Content inserted into new document")
        return documentId
    }
}