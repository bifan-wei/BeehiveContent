package beehive.hw.com.beehivecontent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by bifan-wei
 */

public class TestActivity extends AppCompatActivity{
    HexagonImageView hexagonImageView;
    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        hexagonImageView = (HexagonImageView) findViewById(R.id.hexagonview);

    }


    public void onClick(View vkiew){
        ViewGroup.LayoutParams p = hexagonImageView.getLayoutParams();
        p.width = 644;
        p.height = 644;
        hexagonImageView.setLayoutParams(p);
        Log.e("hexagonImageView",hexagonImageView.getMeasuredWidth()+"");
        hexagonImageView.setImageResource(R.mipmap.pic);
    }
}
