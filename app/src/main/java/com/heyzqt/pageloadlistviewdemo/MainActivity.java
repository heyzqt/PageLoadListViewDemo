package com.heyzqt.pageloadlistviewdemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MyListView.OnRefreshListener {

	@BindView(R.id.listview)
	MyListView mListView;

	List<String> datas = new ArrayList<>();

	MyAdapter mAdapter;

	int curListAtPos = 5;

	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		initData();
		initView();
	}

	void initData() {
		for (int i = 0; i < 200; i++) {
			datas.add("item " + i);
		}

		List<String> lists = datas.subList(3, 6);
		System.out.println("hello");
	}

	void initView() {
		mAdapter = new MyAdapter(datas.subList(0, curListAtPos));
		mListView.setAdapter(mAdapter);
		mListView.setOnRefreshListener(this);

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 1) {
					mListView.hideHeaderView();
				}
			}
		});
	}

	@Override
	public void pullDownRefresh() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (curListAtPos > datas.size()) {
					curListAtPos = datas.size();
				}
				mAdapter.setData(datas.subList(0, curListAtPos));
				mListView.hideHeaderView();

			}
		}, 3000);
	}

	@Override
	public void pullUpLoad() {
		curListAtPos += 5;
		if (curListAtPos > datas.size()) {
			curListAtPos = datas.size();
		}

		new Handler().post(new Runnable() {
			@Override
			public void run() {
				mListView.showFooterView(true);
			}
		});

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				//如果数据加载成功
				mAdapter.setData(datas.subList(0, curListAtPos));
				mListView.showFooterView(false);
				if (curListAtPos < datas.size()) {
					mListView.finishLoad(false);
				}
				mListView.showFooterView(false);
			}
		}, 3000);
	}
}
