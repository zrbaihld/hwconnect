package io.agora.tutorials1v1acall;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



    }


    public void onIntoRoom(View view) {
        String fromePhone = ((EditText) findViewById(R.id.from_phone)).getText().toString();
        String fromeName = ((EditText) findViewById(R.id.from_name)).getText().toString();
        Intent intent=new Intent(this, TestApiActivity.class);
        intent.putExtra("frome_phone",fromePhone);
        intent.putExtra("frome_name",fromeName);
        startActivity(intent);
    }


}
