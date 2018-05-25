package com.example.aj.commenton.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

@Database(entities = {AlbumEntity.class}, version = 1,  exportSchema = false)
public abstract class CommentOnDb extends RoomDatabase{

    public abstract AlbumDAO albumDAO();

    private static CommentOnDb instance;

    public static CommentOnDb databaseInstance(final Context context){
        if(instance == null){
            synchronized (CommentOnDb.class){
                if(instance == null){
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            CommentOnDb.class,"word_database"
                    ).addCallback(sRoomDatabaseCallback).build();
                }
            }
        }

        return instance;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){
        @Override
        public void onOpen (@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);
        }
    };

}
