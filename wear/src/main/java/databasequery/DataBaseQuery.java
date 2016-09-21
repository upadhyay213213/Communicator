package databasequery;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.sql.Statement;
import java.util.ArrayList;

import model.MessageResponse;
import model.MessageResposneDatabase;

/**
 * Created by nupadhay on 5/17/2016.
 */
public class DataBaseQuery {

    public static void insertMessageToDB(MessageResponse response, Context context) {

        if (messageExists(response.getmID()) != true) {
            String query =
                    "INSERT INTO messages (id,subject,message,notifmessage,time,senderid,senderusername,senderemail,senderdisplayname,ismessageread)VALUES "
                            + "(?,?,?,?,?,?,?,?,?,?)";
            SQLiteDatabase db = DatabaseHelper.getSqliteDatabase();
            SQLiteStatement statement = db.compileStatement(query);
            statement.bindDouble(1, Double.parseDouble(response.getmID()));
            statement.bindString(2, response.getmSubject());
            statement.bindString(3, response.getmMessage());
            statement.bindString(4, response.getmNotif_Message());
            statement.bindString(5, response.getmTime());
            statement.bindString(6, response.getmSenderID());
            statement.bindString(7, response.getMmSenderUserName());
            statement.bindString(8, response.getmSenderEmail());
            statement.bindString(9, response.getmSenderDisplayName());
            statement.bindString(10, "false");
            statement.execute();
            statement.close();
        }
    }


    public  static void updateMessageStatus(String productID,String messageStatus){
           String query = "UPDATE messages set ismessageread=? WHERE id=?";
            SQLiteDatabase db = DatabaseHelper.getSqliteDatabase();
            SQLiteStatement statement = db.compileStatement(query);
            statement.bindString(1,messageStatus);
            statement.bindString(2, productID);
        statement.execute();
        statement.close();
        }




    public static ArrayList<MessageResposneDatabase> getMessageResponse(Context ctx) {
        ArrayList<MessageResponse> messageResponseArrayList = new ArrayList<>();
        String query = "Select * from messages ";
        SQLiteDatabase db = DatabaseHelper.getSqliteDatabase();
        Cursor cur = DatabaseHelper.executeSelectQuery(db, query, null);
        return parseMessageFromDb(cur);
    }

    private static ArrayList<MessageResposneDatabase> parseMessageFromDb(Cursor cur) {
        ArrayList<MessageResposneDatabase> messageResponseArrayList = new ArrayList<>();
        while (cur.moveToNext()) {
            MessageResposneDatabase messageResponse = new MessageResposneDatabase();
            messageResponse.setmID(cur.getString(cur.getColumnIndexOrThrow("id")));
            messageResponse.setmMessage(cur.getString(cur.getColumnIndexOrThrow("message")));
            messageResponse.setmTime(cur.getString(cur.getColumnIndexOrThrow("time")));
            messageResponse.setmSenderDisplayName(cur.getString(cur.getColumnIndexOrThrow("senderdisplayname")));
            messageResponse.setIsMessageRead(Boolean.parseBoolean(cur.getString(cur.getColumnIndexOrThrow("ismessageread"))));
            messageResponseArrayList.add(messageResponse);

        }
        cur.close();
        return messageResponseArrayList;

    }

    private static boolean messageExists(String id) {
        String query = "SELECT * FROM messages WHERE id=?";
        SQLiteDatabase db = DatabaseHelper.getSqliteDatabase();
        Cursor cur = DatabaseHelper.executeSelectQuery(db, query, new String[]{
                String.valueOf(id)
        });
        boolean exists = cur.getCount() > 0;
        cur.close();
        return exists;
    }

    public static ArrayList<MessageResposneDatabase> getNowMessagesFromDb() {
        Cursor cursor = getNowMessagestFromDatabase();
        return parseMessageFromDb(cursor);

    }

    private static Cursor getNowMessagestFromDatabase() {
        String query;
        query = "SELECT * FROM messages Where time = date('now','-5 days')  ";
        SQLiteDatabase db = DatabaseHelper.getSqliteDatabase();
        Cursor cur = DatabaseHelper.executeSelectQuery(db, query, null);
        return cur;

    }
}

