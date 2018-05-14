package com.heyzqt.pageloadlistviewdemo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by heyzqt on 2018/5/14.
 */

public class MyAdapter extends BaseAdapter {

	List<String> datas;

	private static final String TAG = "MyAdapter";

	public MyAdapter(List<String> datas) {
		this.datas = datas;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, final ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout
					.listview_item, parent, false);
			holder.content = convertView.findViewById(R.id.content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.content.setText(datas.get(position));
		return convertView;
	}

	private static class ViewHolder {
		TextView content;
	}

	public void setData(List<String> datas) {
		this.datas = datas;
		notifyDataSetChanged();
	}
}
