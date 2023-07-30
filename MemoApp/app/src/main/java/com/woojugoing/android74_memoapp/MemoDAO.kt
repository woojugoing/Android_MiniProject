package com.woojugoing.android74_memoapp

import android.content.Context

class MemoDAO {

    companion object {

        fun insert(context: Context, memoClass: MemoClass) {
            val sql = """
            INSERT INTO     MemoTable (
                memo_title, memo_content, memo_date, memo_cat_idx
            )
            VALUES (?, ?, ?, ?)
            """.trimIndent()

            val args = arrayOf(
                memoClass.memoTitle,
                memoClass.memoContent,
                memoClass.memoDate,
                memoClass.memoCatIdx
            )

            val dbHelper = DBHelper(context)
            dbHelper.writableDatabase.execSQL(sql, args)
            dbHelper.close()
        }

        fun select(context: Context, memoIdx: Int): MemoClass? {
            val sql = """
            SELECT * FROM   MemoTable
            WHERE           memo_idx = ?
            """.trimIndent()

            val args = arrayOf("$memoIdx")

            val dbHelper = DBHelper(context)
            val cursor = dbHelper.writableDatabase.rawQuery(sql, args)
            val chk = cursor.moveToNext()

            return if (chk) {
                val getIdx = cursor.getColumnIndex("memo_idx")
                val getTitle = cursor.getColumnIndex("memo_title")
                val getContent = cursor.getColumnIndex("memo_content")
                val getDate = cursor.getColumnIndex("memo_date")
                val getCatIdx = cursor.getColumnIndex("memo_cat_idx")

                val memoIdx = cursor.getInt(getIdx)
                val memoTitle = cursor.getString(getTitle)
                val memoContent = cursor.getString(getContent)
                val memoDate = cursor.getString(getDate)
                val memoCatIdx = cursor.getInt(getCatIdx)

                val memoClass = MemoClass(memoIdx, memoTitle, memoContent, memoDate, memoCatIdx)
                return memoClass

            } else {
                null
            }
        }

        fun selectAll(context: Context, categoryIdx: Int): MutableList<MemoClass> {
            val sql = """
            SELECT * FROM   MemoTable
            WHERE           memo_cat_idx = ?
            ORDER BY        memo_idx DESC
            """.trimIndent()
            val args = arrayOf("$categoryIdx")
            val memoList = mutableListOf<MemoClass>()

            val dbHelper = DBHelper(context)
            val cursor = dbHelper.writableDatabase.rawQuery(sql, args)

            while (cursor.moveToNext()) {
                val getIdx = cursor.getColumnIndex("memo_idx")
                val getTitle = cursor.getColumnIndex("memo_title")
                val getContent = cursor.getColumnIndex("memo_content")
                val getDate = cursor.getColumnIndex("memo_date")
                val getCatIdx = cursor.getColumnIndex("memo_cat_idx")

                val memoIdx = cursor.getInt(getIdx)
                val memoTitle = cursor.getString(getTitle)
                val memoContent = cursor.getString(getContent)
                val memoDate = cursor.getString(getDate)
                val memoCatIdx = cursor.getInt(getCatIdx)

                val memoClass = MemoClass(memoIdx, memoTitle, memoContent, memoDate, memoCatIdx)
                memoList.add(memoClass)
            }

            dbHelper.close()
            return memoList
        }

        fun update(context: Context, memoClass: MemoClass) {
            val sql = """
            UPDATE  MemoTable
            SET     memo_title = ?, memo_content = ?, memo_date = ?, memo_cat_idx = ?
            WHERE   memo_idx = ?
            """.trimIndent()

            val args = arrayOf(
                memoClass.memoTitle,
                memoClass.memoContent,
                memoClass.memoDate,
                memoClass.memoCatIdx,
                memoClass.memoIdx
            )

            val dbHelper = DBHelper(context)
            dbHelper.writableDatabase.execSQL(sql, args)
            dbHelper.close()
        }

        fun delete(context: Context, memoIdx: Int) {
            val sql = """
            DELETE FROM MemoTable
            WHERE       memo_idx = ?
        """.trimIndent()

            val args = arrayOf(memoIdx)

            val dbHelper = DBHelper(context)
            dbHelper.writableDatabase.execSQL(sql, args)
            dbHelper.close()
        }

        fun deleteMemoInCat(context: Context, categoryIdx: Int) {
            val sql = """
            DELETE FROM MemoTable
            WHERE       memo_cat_idx = ?
            """.trimIndent()

            val args = arrayOf(categoryIdx)

            val dbHelper = DBHelper(context)
            dbHelper.writableDatabase.execSQL(sql, args)
            dbHelper.close()
        }
    }
}