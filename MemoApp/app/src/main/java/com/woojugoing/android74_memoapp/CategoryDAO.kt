package com.woojugoing.android74_memoapp

import android.content.Context

class CategoryDAO {

    companion object {

        fun insert(context: Context, categoryClass: CategoryClass) {
            val sql = """
            INSERT INTO CategoryTable (
                category_name
            )
            VALUES (?)
        """.trimIndent()

            val args = arrayOf(categoryClass.categoryName)

            val dbHelper = DBHelper(context)
            dbHelper.writableDatabase.execSQL(sql, args)
            dbHelper.close()
        }

        fun select(context: Context, categoryIdx: Int): CategoryClass? {
            val sql = """
            SELECT * FROM CategoryTable
            WHERE category_idx = ?
        """.trimIndent()

            val args = arrayOf("$categoryIdx")

            val dbHelper = DBHelper(context)
            val cursor = dbHelper.writableDatabase.rawQuery(sql, args)
            val chk = cursor.moveToNext()

            return if (chk) {
                val getIdx = cursor.getColumnIndex("category_idx")
                val getData = cursor.getColumnIndex("category_name")

                val categoryIdx = cursor.getInt(getIdx)
                val categoryName = cursor.getString(getData)

                CategoryClass(categoryIdx, categoryName)
            } else {
                null
            }
        }

        fun selectAll(context: Context): MutableList<CategoryClass> {

            val categoryList = mutableListOf<CategoryClass>()

            val sql = """
            SELECT * FROM CategoryTable
            ORDER BY category_idx DESC
        """.trimIndent()

            val dbHelper = DBHelper(context)
            val cursor = dbHelper.writableDatabase.rawQuery(sql, null)

            while (cursor.moveToNext()) {
                val getIdx = cursor.getColumnIndex("category_idx")
                val getData = cursor.getColumnIndex("category_name")

                val categoryIdx = cursor.getInt(getIdx)
                val categoryName = cursor.getString(getData)

                val categoryClass = CategoryClass(categoryIdx, categoryName)
                categoryList.add(categoryClass)
            }

            dbHelper.close()
            return categoryList
        }

        fun update(context: Context, categoryClass: CategoryClass) {
            val sql = """
            UPDATE CategoryTable
            SET category_name = ?
            WHERE category_idx = ?
        """.trimIndent()

            val args = arrayOf(categoryClass.categoryName, categoryClass.categoryIdx)

            val dbHelper = DBHelper(context)
            dbHelper.writableDatabase.execSQL(sql, args)
            dbHelper.close()
        }

        fun delete(context: Context, categoryIdx: Int) {
            val sql = """
            DELETE FROM CategoryTable
            WHERE category_idx = ?
        """.trimIndent()

            val args = arrayOf(categoryIdx)

            val dbHelper = DBHelper(context)
            dbHelper.writableDatabase.execSQL(sql, args)
            dbHelper.close()
        }
    }
}