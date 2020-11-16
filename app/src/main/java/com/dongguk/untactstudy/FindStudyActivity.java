package com.dongguk.untactstudy;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dongguk.untactstudy.Adapter.Recycler;
import com.dongguk.untactstudy.Model.studyRoomData;

import java.util.Arrays;
import java.util.List;

public class FindStudyActivity extends AppCompatActivity {
    private Recycler adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_findstudy);
        init();
        getData();
    }

    private void init() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new Recycler(getApplicationContext());

        recyclerView.setAdapter(adapter);
    }

    private void getData() {
        // 그냥 정상작동 확인용 예시입니다.
        List<String> listTitle = Arrays.asList("알고리즘 고급","토익","SW 역량테스트 A형 준비반","SW 마에스트로");
        List<String> listContent = Arrays.asList(
                "강한 연결 요소, 세그먼트 트리 느린 갱신, Heavy-light decomposition, splay tree 등",
                "카투사 가고 싶다",
                "인데 코로나 때문에 지금 안함 ㅋㅋ",
                "개 짱짱 스팩인데 지원할 사람? (본인 최종면접 광탈함 엌)"
        );

        for (int i = 0; i < listTitle.size(); i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            studyRoomData data = new studyRoomData();
            data.setTitle(listTitle.get(i));
            data.setContent(listContent.get(i));

            // 각 값이 들어간 data를 adapter에 추가합니다.
            adapter.addItem(data);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        adapter.notifyDataSetChanged();
    }
}
