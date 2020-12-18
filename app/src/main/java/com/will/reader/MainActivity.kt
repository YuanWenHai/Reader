package com.will.reader

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

//todo
// 1.横屏阅读匹配
// 2.阅读界面菜单完善,编码选择等等
// 3.异形屏匹配,屏幕反转
// 4.阅读界面启动速度略慢，是全文件映射导致的，需要部分映射优化
// 5.文件Intent获取的Uri转绝对路径可行性差，考虑将二进制文件复制到app独有空间？
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


}