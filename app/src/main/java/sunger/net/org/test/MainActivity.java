package sunger.net.org.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import org.net.sunger.widget.GalleryView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private GalleyAdapter mAdapter;
    private GalleryView mRecyclerview;
    private TextView mTextViewName;
    private ImageView mImageViewCover;
    private List<ImageEntity> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        mAdapter = new GalleyAdapter();
        mAdapter.setData(data);
        mRecyclerview = (GalleryView) findViewById(R.id.recyclerview);
        mRecyclerview.setAdapter(mAdapter);
        mTextViewName = (TextView) findViewById(R.id.textView_name);
        mImageViewCover=(ImageView) findViewById(R.id.imageView_cover);
        mRecyclerview.setOnItemSelectedListener(new GalleryView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                ImageEntity entity = data.get(position);
                 mTextViewName.setText(entity.getName());
                mImageViewCover.setImageResource(entity.getCover());
            }
        });

    }

    private void initData() {
        ImageEntity entity1 = new ImageEntity();
        entity1.setCover(R.mipmap.a);
        entity1.setName("美国队长");
        data.add(entity1);

        ImageEntity entity2 = new ImageEntity();
        entity2.setCover(R.mipmap.b);
        entity2.setName("奇幻森林");
        data.add(entity2);

        ImageEntity entity3 = new ImageEntity();
        entity3.setCover(R.mipmap.c);
        entity3.setName("魔宫魅影");
        data.add(entity3);


        ImageEntity entity4 = new ImageEntity();
        entity4.setCover(R.mipmap.d);
        entity4.setName("分歧者3");
        data.add(entity4);

        ImageEntity entity5 = new ImageEntity();
        entity5.setCover(R.mipmap.e);
        entity5.setName("谁的青春不迷茫");
        data.add(entity5);

        ImageEntity entity6 = new ImageEntity();
        entity6.setCover(R.mipmap.f);
        entity6.setName("猎神");
        data.add(entity6);

        ImageEntity entity7 = new ImageEntity();
        entity7.setCover(R.mipmap.g);
        entity7.setName("爱丽丝梦游仙境2");
        data.add(entity7);

    }

}
