package kr.co.starmark.secretaryguide;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraHostProvider;
import com.commonsware.cwac.camera.SimpleCameraHost;


public class CameraFragmentTestActivity extends Activity  implements CameraHostProvider{

//    private CameraDemoFragment current=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        current = new CameraDemoFragment();

//        getFragmentManager().beginTransaction()
//                .replace(R.id.container, current).commit();
    }

    @Override
    public CameraHost getCameraHost() {
        return(new SimpleCameraHost(this));
    }
}
