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

### 📄 Eksport Dokumen
- Eksport dokumen KEW.PS-3 dalam format Microsoft Word (.docx)
- Format mengikuti borang rasmi Pekeliling Perbendaharaan Malaysia
- Termasuk Bahagian A (Maklumat Stok) dan Bahagian B (Transaksi)
- Boleh dikongsi melalui aplikasi lain

## Teknologi yang Digunakan

- **Kotlin** - Bahasa pengaturcaraan utama
- **Jetpack Compose** - UI toolkit moden untuk Android
- **Room Database** - Penyimpanan data tempatan
- **Apache POI** - Penjanaan dokumen Microsoft Word
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
│   └── ExportScreen.kt       # Skrin eksport
└── utils/
    └── DocumentExporter.kt   # Utiliti eksport dokumen
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

### 3. Eksport Dokumen
1. Pilih tab "Export"
2. Pilih item stok untuk dieksport
3. Tekan "Eksport Dokumen KEW.PS-3 (DOCX)"
4. Dokumen akan dijana dan boleh dikongsi

## Format Dokumen KEW.PS-3

Dokumen yang dieksport mengikuti format rasmi dengan:

### Bahagian A - Maklumat Stok
- Maklumat asas item (kod, unit, kumpulan)
- Lokasi penyimpanan lengkap
- Paras stok (maksimum, menokok, minimum)
- Statistik terimaan dan keluaran suku tahunan

### Bahagian B - Transaksi Stok
- Jadual transaksi dengan tarikh dan dokumen
- Maklumat terimaan (kuantiti, harga seunit, jumlah)
- Maklumat keluaran (kuantiti, jumlah)
- Baki semasa dan nama pegawai

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