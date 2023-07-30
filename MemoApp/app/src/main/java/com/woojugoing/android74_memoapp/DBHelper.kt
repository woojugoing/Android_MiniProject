package com.woojugoing.android74_memoapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "memo.db", null, 1) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase?) {
        val sql1 = """
            CREATE TABLE PasswordTable(
                password_idx    INTEGER     PRIMARY KEY AUTOINCREMENT,
                password_data   TEXT        NOT NULL
            )
        """.trimIndent()

        val sql2 = """
            CREATE TABLE CategoryTable(
                category_idx    INTEGER     PRIMARY KEY AUTOINCREMENT,
                category_name   TEXT        NOT NULL
            )
        """.trimIndent()

        val sql3 = """
            CREATE TABLE MemoTable(
                memo_idx        INTEGER     PRIMARY KEY AUTOINCREMENT,
                memo_title      TEXT        NOT NULL,
                memo_content    TEXT        NOT NULL,
                memo_date       DATE        NOT NULL,
                memo_cat_idx    INTEGER     NOT NULL
            )
        """.trimIndent()

        sqLiteDatabase?.execSQL(sql1)
        sqLiteDatabase?.execSQL(sql2)
        sqLiteDatabase?.execSQL(sql3)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
}