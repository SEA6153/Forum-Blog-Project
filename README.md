# Web Blog Projesi

Bu proje Spring Boot tabanlı bir blog uygulamasıdır.

## Gereksinimler

- Java 17
- Maven
- PostgreSQL
- AWS Hesabı (S3 bucket için)

## Kurulum

1. Projeyi klonlayın:

```bash
git clone <proje-url>
cd Web-BlogProjectv2
```

2. Environment değişkenlerini ayarlayın:

   - `.env.example` dosyasını `.env` olarak kopyalayın
   - `.env` dosyasındaki değişkenleri kendi ortamınıza göre düzenleyin

3. PostgreSQL veritabanını kurun:

```bash
# PostgreSQL'i başlatın (Linux)
sudo systemctl start postgresql

# veya Windows için
# PostgreSQL servisini Windows Hizmetleri'nden başlatın

# Veritabanını oluşturun
createdb webProjectSEA
```

4. Upload dizinini oluşturun:

```bash
# Linux/Mac için
mkdir -p ~/web-blog-project/uploads

# Windows için
mkdir "%USERPROFILE%\web-blog-project\uploads"
```

5. Projeyi derleyin ve çalıştırın:

```bash
./mvnw spring-boot:run
```

## Environment Değişkenleri

Projenin çalışması için aşağıdaki environment değişkenlerinin ayarlanması gerekir:

- `DB_HOST`: PostgreSQL sunucu adresi
- `DB_PORT`: PostgreSQL port numarası
- `DB_NAME`: Veritabanı adı
- `DB_USERNAME`: Veritabanı kullanıcı adı
- `DB_PASSWORD`: Veritabanı şifresi
- `AWS_ACCESS_KEY`: AWS erişim anahtarı
- `AWS_SECRET_KEY`: AWS gizli anahtarı
- `AWS_REGION`: AWS bölgesi
- `AWS_BUCKET_NAME`: S3 bucket adı
- `FILE_UPLOAD_DIR`: Dosya yükleme dizini

## Notlar

- Hassas bilgileri (AWS anahtarları, veritabanı şifreleri vb.) asla git'e pushlamayın
- Her ortam için kendi `.env` dosyanızı oluşturun
- Uygulama varsayılan olarak 3011 portunda çalışır
