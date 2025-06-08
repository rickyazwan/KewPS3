# KEW.PS-3 Sistem Pengurusan Stok - Android App

Aplikasi Android untuk pengurusan stok mengikut format KEW.PS-3 Pekeliling Perbendaharaan Malaysia AM 6.3.

## Ciri-ciri Utama

### 📊 Dashboard
- Paparan statistik stok secara real-time
- Jumlah item, terimaan, keluaran, dan stok rendah
- Senarai item stok dengan status (Normal, Perlu Reorder, Stok Rendah)
- Paparan maklumat item dengan butiran lengkap

### 📦 Pengurusan Item Stok (Bahagian A)
- Tambah item stok baru dengan maklumat lengkap:
  - No. Kad (auto-generated)
  - Nama stor dan perihal stok
  - No. kod dan unit pengukuran
  - Kumpulan (A/B) dan status pergerakan
  - Lokasi penyimpanan (gudang, baris, rak, tingkat, petak)
  - Paras stok (maksimum, menokok, minimum)

### 📋 Transaksi Stok (Bahagian B)
- Rekod transaksi terimaan dan keluaran
- Jenis dokumen: PK, BTB, BPSS, BPSI, BPIN
- Maklumat terimaan: pembekal, kuantiti, harga seunit
- Maklumat keluaran: penerima, kuantiti
- Sejarah transaksi lengkap dengan tarikh dan pegawai

### 📄 Integrasi Google Docs
- Integrasi dengan Google Docs untuk pengurusan dokumen online
- Akses terus ke template KEW.PS-3 di Google Docs
- Kongsi data dalam format teks untuk ditampal ke dokumen
- Simpan automatik ke cloud dan akses dari mana-mana peranti

## Teknologi yang Digunakan

- **Kotlin** - Bahasa pengaturcaraan utama
- **Jetpack Compose** - UI toolkit moden untuk Android
- **Room Database** - Penyimpanan data tempatan
- **Google Docs API** - Integrasi dengan Google Docs untuk pengurusan dokumen
- **Material Design 3** - Reka bentuk UI yang konsisten

## Struktur Aplikasi

```
app/
├── data/
│   ├── StockItem.kt          # Model data item stok
│   ├── Transaction.kt        # Model data transaksi
│   ├── StockItemDao.kt       # Interface akses data item stok
│   ├── TransactionDao.kt     # Interface akses data transaksi
│   └── KewPS3Database.kt     # Konfigurasi pangkalan data
├── repository/
│   └── KewPS3Repository.kt   # Lapisan akses data
├── viewmodel/
│   └── KewPS3ViewModel.kt    # Logik perniagaan dan pengurusan state
├── ui/screens/
│   ├── KewPS3App.kt          # Aplikasi utama dengan navigasi
│   ├── DashboardScreen.kt    # Skrin dashboard
│   ├── AddItemScreen.kt      # Skrin tambah item
│   ├── TransactionsScreen.kt # Skrin transaksi
│   └── ExportScreen.kt       # Skrin integrasi Google Docs
└── utils/
    └── GoogleDocsIntegration.kt   # Utiliti integrasi Google Docs
```

## Ciri-ciri Keselamatan

- **Penyimpanan Tempatan**: Semua data disimpan secara tempatan di peranti
- **Tiada Internet**: Aplikasi berfungsi sepenuhnya offline
- **Backup Data**: Data disimpan dalam pangkalan data SQLite yang boleh dibackup

## Keperluan Sistem

- Android 7.0 (API level 24) atau lebih tinggi
- Ruang penyimpanan: ~50MB
- RAM: 2GB atau lebih tinggi (disyorkan)

## Cara Menggunakan

### 1. Tambah Item Stok Baru
1. Pilih tab "Add Item"
2. Isi maklumat asas (nama stor, perihal stok, kod)
3. Pilih unit pengukuran dan kumpulan
4. Masukkan lokasi penyimpanan
5. Tetapkan paras stok (maksimum, menokok, minimum)
6. Tekan "Simpan Item"

### 2. Rekod Transaksi
1. Pilih tab "Transactions"
2. Pilih jenis dokumen dan masukkan no. dokumen
3. Pilih item stok dan jenis transaksi (terimaan/keluaran)
4. Isi maklumat transaksi mengikut jenis
5. Masukkan nama pegawai stor
6. Tekan "Rekod Transaksi"

### 3. Integrasi Google Docs
1. Pilih tab "Documents"
2. Pilih item stok untuk didokumentasikan
3. Tekan "Buka Google Docs" untuk akses template
4. Tekan "Kongsi Data" untuk menyalin data ke Google Docs

## Integrasi Google Docs KEW.PS-3

Data yang dikongsi ke Google Docs mengikuti format rasmi dengan:

### Maklumat Stok
- Maklumat asas item (No. Kad, nama stor, perihal stok)
- No. kod dan unit pengukuran
- Kumpulan dan status pergerakan
- Lokasi penyimpanan lengkap (gudang, baris, rak, tingkat, petak)
- Paras stok (maksimum, reorder, minimum)

### Transaksi Stok
- Jadual transaksi dengan tarikh dan jenis dokumen
- Maklumat terimaan dan keluaran
- Kuantiti, harga seunit, dan jumlah harga
- Nama pegawai yang mengendalikan

### Kelebihan Google Docs
- Simpan automatik ke cloud
- Akses dari mana-mana peranti
- Kolaborasi masa nyata
- Boleh dieksport ke PDF atau Word jika diperlukan

## Nota Penting

- **Backup Data**: Sila backup data secara berkala
- **Keselamatan**: Jangan kongsi maklumat sensitif melalui saluran tidak selamat
- **Kemaskini**: Pastikan aplikasi sentiasa dikemaskini untuk prestasi terbaik

## Sokongan

Untuk sebarang pertanyaan atau masalah teknikal, sila hubungi pasukan sokongan IT jabatan.

---

**Versi**: 1.0  
**Tarikh**: 2024  
**Pematuhan**: Pekeliling Perbendaharaan Malaysia AM 6.3 