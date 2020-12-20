package com.will.reader

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.will.reader.data.AppDataBase
import com.will.reader.data.BookRepository
import com.will.reader.viewmodel.AppViewModel

// TODO: 一些问题的注解
/*1. 阅读界面的跳转逻辑中，跳转后文章阅读页面底部的progress值总是与跳转值不同，例如输入11跳转后显示当前进度为11.03%
这是因为当前跳转逻辑找到的position是页首，而阅读界面底部的progress是根据页尾position计算的

*/
//todo
// 1.横屏阅读匹配
// 2.阅读界面菜单完善,编码选择等等
// 3.异形屏匹配,屏幕反转
// 4.阅读界面启动速度略慢，是全文件映射导致的，需要部分映射优化
// 5.文件Intent获取的Uri转绝对路径可行性差，考虑将二进制文件复制到app独有空间？
class MainActivity : AppCompatActivity() {
    private val appViewModel: AppViewModel by viewModels{
        object: ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return AppViewModel(BookRepository.getInstance(AppDataBase.getInstance(this@MainActivity).getBookDao())) as T
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appViewModel
    }


}