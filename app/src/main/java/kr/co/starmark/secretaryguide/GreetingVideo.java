package kr.co.starmark.secretaryguide;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.TableInfo;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import dalvik.system.PathClassLoader;

/**
 * Created by starmark on 2014. 10. 16..
 */
@Table(name = "VideoRecords")
public class GreetingVideo extends Model implements Parcelable {
    private static final String TAG = "GreetingVideo Table";
    @Column(name = "path")
    public String path;

    @Column(name = "Type")
    public int type;//1,2,3

    @Column(name = "Side")
    public int side;//1-front,2-side

    @Column(name = "Date")
    public String date;

    public GreetingVideo() {
        super();
    }

    public GreetingVideo(String path, int type, int side, String date) {
        super();
        this.path = path;
        this.type = type;
        this.side = side;
        this.date = date;
    }

    public void log() {
        Log.d(TAG, "path:" + path);
        Log.d(TAG, "type:" + type);
        Log.d(TAG, "side:" + side);
        Log.d(TAG, "date:" + date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeInt(this.type);
        dest.writeInt(this.side);
        dest.writeString(this.date);
    }

    private GreetingVideo(Parcel in) {
        this.path = in.readString();
        this.type = in.readInt();
        this.side = in.readInt();
        this.date = in.readString();
    }

    public static final Parcelable.Creator<GreetingVideo> CREATOR = new Parcelable.Creator<GreetingVideo>() {
        public GreetingVideo createFromParcel(Parcel source) {
            return new GreetingVideo(source);
        }

        public GreetingVideo[] newArray(int size) {
            return new GreetingVideo[size];
        }
    };
}


