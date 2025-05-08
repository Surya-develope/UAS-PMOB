BrainQuizz: Aplikasi Kuis Berdasarkan Quizziz

BrainQuizz adalah aplikasi kuis yang interaktif dan menyenangkan, dirancang untuk memberikan pengalaman belajar yang dinamis. Terinspirasi oleh platform kuis populer seperti Quizziz, BrainQuizz bertujuan untuk membantu siswa, pendidik, dan penggemar pengetahuan menguji pemahaman mereka dalam berbagai mata pelajaran sambil menawarkan lingkungan yang menyenangkan dan kompetitif.

Fitur Utama:
    1) Pendaftaran Pengguna dan Autentikasi:
       
    2) Pengguna dapat mendaftar dan masuk ke dalam sistem. Setiap pengguna memiliki detail unik seperti nama pengguna, email, dan hash kata sandi untuk akses yang aman.
       
    3) Pengguna diberi peran seperti siswa atau guru, yang menentukan hak akses mereka di dalam aplikasi.
       
    4) Kategori dan Tingkatan:
       
    5) Kategori_soal (Kategori) mengorganisir soal-soal kuis dalam berbagai subjek atau topik. Kategori ini membantu pengguna untuk memfilter dan fokus pada area yang mereka minati.
       
    6) Tingkatan (Tingkat) menentukan tingkat kesulitan kuis. Pengguna dapat memilih tingkat yang sesuai dengan pengetahuan dan pengalaman mereka.
       
    7) Kelas dan Partisipasi Siswa:
       
    8) Kelas dibuat untuk kuis kelompok, memungkinkan banyak pengguna untuk berpartisipasi bersama dalam lingkungan kuis yang kolaboratif atau kompetitif.
       
    9) Kelas_pengguna (Pengguna Kelas) menghubungkan siswa ke kelas mereka masing-masing, memungkinkan guru untuk mengelola daftar kelas dengan mudah.

Kuis dan Soal:
       
    1. Kuis adalah fitur utama, di mana pengguna dapat mengikuti ujian berdasarkan kategori dan tingkat kesulitan yang telah ditentukan. Setiap kuis terdiri dari beberapa soal yang dipilih dari kategori dan tingkat kesulitan yang dipilih.
       
    2. Soal membentuk inti dari kuis, di mana berbagai soal dengan beberapa pilihan jawaban disajikan kepada pengguna. Setiap soal memiliki opsi dan jawaban yang benar.
       
    3. Hasil dan Analisis:
       
    4. Hasil_kuis (Hasil Kuis) memberikan umpan balik kepada pengguna tentang kinerja mereka setelah menyelesaikan kuis. Ini termasuk perhitungan skor berdasarkan jawaban yang benar dan kinerja keseluruhan.
       
    5. Sistem melacak waktu penyelesaian setiap kuis dan menampilkan ringkasan jawaban pengguna.

Panel Admin dan Manajemen:

    1. Guru atau administrator dapat membuat dan mengelola kuis, mendefinisikan kategori soal, dan menyesuaikan tingkat kesulitan kuis. Mereka juga memiliki akses ke hasil dan dapat memantau kemajuan siswa.

Gambaran Umum Struktur Database:

    ![339c2675-aeda-4cc4-acd6-ecbd04add8a3](https://github.com/user-attachments/assets/5af6d057-ed50-4346-89a5-dddd4a2ec9ec)


    1. Skema database aplikasi mendukung entitas berikut:
       
    2. Pengguna: Menyimpan data pengguna seperti nama pengguna, email, peran, dan informasi kata sandi.
       
    3. Kelas: Mendefinisikan struktur kelas yang dapat diikuti oleh siswa dan guru.
       
    4. Kategori_soal: Kategori yang mengorganisir soal-soal kuis berdasarkan topik.
       
    5. Tingkatan: Mewakili berbagai tingkat kesulitan kuis.
       
    6. Kuis: Kuis yang sebenarnya, tempat pengguna berpartisipasi, dengan kaitan ke kategori dan tingkat kesulitan.
       
    7. Soal: Soal yang merupakan bagian dari setiap kuis, termasuk pilihan jawaban dan jawaban yang benar.
       
    8. Kelas_pengguna: Menghubungkan pengguna ke kelas yang mereka ikuti.
       
    9. Hasil_kuis: Menyimpan hasil untuk setiap pengguna setelah menyelesaikan kuis, termasuk skor dan waktu penyelesaian.
       
BrainQuizz bertujuan untuk memberikan platform yang mudah digunakan dan interaktif yang meningkatkan proses pembelajaran melalui kuis yang menarik. Aplikasi ini membantu siswa belajar secara efektif, sambil memungkinkan guru untuk menilai pengetahuan siswa dan melacak kemajuan mereka secara real-time.
