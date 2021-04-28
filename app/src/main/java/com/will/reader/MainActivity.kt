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
// 1.章节列表中的当前章节应高亮
// 2.章节列表与对应进度不匹配，应该是差了一个字节，需要改
// 3.异形屏匹配,屏幕反转
// 4.阅读界面启动速度略慢，是全文件映射导致的，需要部分映射优化
// 5.Android10 权限适配
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
        //there is a develop branch's change
        appViewModel
    }


}