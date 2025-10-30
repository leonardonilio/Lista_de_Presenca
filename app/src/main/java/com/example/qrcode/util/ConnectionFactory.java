package com.example.qrcode.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ConnectionFactory extends SQLiteOpenHelper {
    public ConnectionFactory(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Eventos(id integer primary key autoincrement, "+
                "nomeEvento varchar(60), local varchar(100), data varchar(10), descricao varchar(200), organizador varchar(50), horarioInicio varchar(5), horarioFim varchar(5))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versaoAntiga, int versaoNova) {
        String sql = "DROP TABLE IF EXISTS Eventos";
        db.execSQL(sql);
        onCreate(db);
    }
}
