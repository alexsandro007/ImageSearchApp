package com.example.myapplication1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AuthorInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);

        TextView authorTextView = findViewById(R.id.author_text_view);
        authorTextView.setText("Разработал Ярмола А.О.\nГруппа: АС-63\n\nЗадача:\n" +
                "Разработка интерфейсов мобильных приложений\n" +
                "Необходимо разработать мобильное приложение, которое ищет в сети Интернет изображения по запросу пользователя, позволяет оценивать их, скачивать, и посещать интернет-страницы сайтов, на которых было найдено изображение.");

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Закрываем текущую активность и возвращаемся на MainActivity
            }
        });
    }
}
