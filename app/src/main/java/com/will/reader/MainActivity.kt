package com.will.reader

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.will.reader.data.AppDataBase
import com.will.reader.data.BookRepository
import com.will.reader.data.ChapterRepository
import com.will.reader.viewmodel.AppViewModel

//todo
// 3.异形屏匹配,屏幕反转
// 4.阅读界面启动速度略慢，是全文件映射导致的，需要部分映射优化
// 5.文件Intent获取的Uri转绝对路径可行性差，考虑将二进制文件复制到app独有空间？ 但contentResolver只能获取到二进制流，不能得到文件名，又该如何转存呢？
class MainActivity : AppCompatActivity() {
    private val appViewModel: AppViewModel by viewModels{
        object: ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return AppViewModel(
                    BookRepository.getInstance(AppDataBase.getInstance(this@MainActivity).getBookDao()),
                    ChapterRepository.getInstance(AppDataBase.getInstance(this@MainActivity).getChapterDao())
                    ) as T
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appViewModel
    }


}