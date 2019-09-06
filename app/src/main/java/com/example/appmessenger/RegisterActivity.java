package com.example.appmessenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    MaterialEditText userName, email, passWord;
    Button btn_register; // nút Register

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //lấy dữ liệu từ các control xuống
        userName = findViewById(R.id.userName);
        email = findViewById(R.id.email);
        passWord = findViewById(R.id.passWord);

        auth = FirebaseAuth.getInstance();

        btn_register = findViewById(R.id.btn_register);

        //sự kiện khi button Register click
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //gán giá trị cho các trường từ biến khai báo trên
                String txt_userName = userName.getText().toString();
                String txt_email = email.getText().toString();
                String txt_passWord = passWord.getText().toString();

                if (TextUtils.isEmpty(txt_userName) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_passWord)){
                    Toast.makeText(RegisterActivity.this, "Tất cả các trường phải được điền đủ.",Toast.LENGTH_SHORT).show();
                }
                else if(txt_passWord.length() < 6)
                {
                    Toast.makeText(RegisterActivity.this, "Mật Khẩu phải có ít nhất 6 ký tự.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    register(txt_userName,txt_email,txt_passWord);
                }
            }
        });


    }


    //Hàm đăng ký rồi đăng nhập
    private void register(final String userName, String email , String passWord){
        auth.createUserWithEmailAndPassword(email, passWord).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    assert firebaseUser != null;
                    String userid = firebaseUser.getUid();

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id",userid);
                    hashMap.put("userName",userName);
                    hashMap.put("image","default");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }

                        }
                    });
                }else {
                    Toast.makeText(RegisterActivity.this,"Bạn Không Thể Đăng Nhập với Email Hoặc Mật Khẩu Này.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
