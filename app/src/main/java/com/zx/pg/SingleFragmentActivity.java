package com.zx.pg;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public abstract class SingleFragmentActivity extends AppCompatActivity {
    protected  abstract  Fragment createFragment();
    private  static final   String TAG="SingleFragmentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        Log.i(TAG, "onCreate: 18");

        FragmentManager fm=getSupportFragmentManager();//获取FragmentManager，管理fragment事务
        Log.i(TAG, "onCreate: 21");
        Fragment fragment=fm.findFragmentById(R.id.fragment_container);//找到一个fragment
        Log.i(TAG, "onCreate: 23");
        if(fragment==null){
            fragment= createFragment();//抽象类的子类会实现createFragment()
            fm.beginTransaction()            //fragment事务，执行添加fragment到队列的操作，并提交

                    .add(R.id.fragment_container,fragment)//容器ID，fragmentID
                    .commit();
            Log.i(TAG, "onCreate: 30");
        }
    }
}
