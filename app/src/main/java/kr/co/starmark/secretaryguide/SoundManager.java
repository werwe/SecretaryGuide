package kr.co.starmark.secretaryguide;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

/**
 * Created by werwe on 2014. 2. 14..
 */
public class SoundManager {

    private SoundPool mPool;
    private Context mContext;

    private HashMap<Integer,Integer> mSoundMap;

    private SoundManager(Context context) {
        mSoundMap = new HashMap<Integer, Integer>();
        mPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        mContext = context;
    }

    public static SoundManager createInstance(Context context)
    {
        return new SoundManager(context);
    }

    public void load(int resid) {
        int soundid = mPool.load(mContext, resid, 1);
        mSoundMap.put(resid,soundid);
    }

    public void play(int resid)
    {
        mPool.play(mSoundMap.get(resid),1,10,10,0,1);
    }

    public void unload()
    {
        for(Integer value:mSoundMap.values()) {
            mPool.unload(value);
        }
    }
}
