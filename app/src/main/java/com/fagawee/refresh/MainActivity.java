package com.fagawee.refresh;

import com.fagawee.refresh.impl.ArrowStyleRefreshAdapter;
import com.fagawee.refresh.impl.CircleStyleRefreshAdapter;
import com.fagawee.refresh.impl.WindMillStyleRefreshAdapter;
import com.fagawee.refresh.libs.AnimationAdapter;
import com.fagawee.refresh.libs.RefreshListener;
import com.fagawee.refresh.libs.RefreshView;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	private View headerview;
	private ListView listview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题
		setContentView(R.layout.fragment_main);
		headerview=View.inflate(MainActivity.this, R.layout.header_view, null);
		listview=(ListView) findViewById(R.id.listview);
		final RefreshView refresh = (RefreshView)findViewById(R.id.refresh);				
		TextView complete=(TextView)headerview.findViewById(R.id.complete);
		TextView header=(TextView)headerview.findViewById(R.id.header);
		TextView footer=(TextView) headerview.findViewById(R.id.footer);
		TextView arrow=(TextView) headerview.findViewById(R.id.arrow);
		TextView wind=(TextView) headerview.findViewById(R.id.wind);
		TextView circle=(TextView) headerview.findViewById(R.id.circle);
		arrow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ArrowStyleRefreshAdapter arrowadapter=new ArrowStyleRefreshAdapter(MainActivity.this);				
				refresh.setAdapter(arrowadapter);
			}
		});
		wind.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				WindMillStyleRefreshAdapter windadapter=new WindMillStyleRefreshAdapter(MainActivity.this);
				refresh.setAdapter(windadapter);
			}
		});
		circle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CircleStyleRefreshAdapter circleAdapter=new CircleStyleRefreshAdapter(MainActivity.this);
				refresh.setAdapter(circleAdapter);
			}
		});
		complete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				refresh.completeRefresh();
				
			}
		});
		header.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				refresh.pullToHeaderRefresh();
				
			}
		});
		footer.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				refresh.pullToFooterRefresh();
				
			}
		});
		
		
		
		arrow.performClick();
		refresh.setDuration(1000);
		refresh.setRefreshListener(new RefreshListener() {
			
			@Override
			public void onHeaderRefresh() {
				Toast.makeText(MainActivity.this, "-onHeaderRefresh-", Toast.LENGTH_SHORT).show();
				
			}
			
			@Override
			public void onFooterRefresh() {
				Toast.makeText(MainActivity.this, "-onFooterRefresh-", Toast.LENGTH_SHORT).show();
				
			}
		});
		
		listview.addHeaderView(headerview);
		ArrayAdapter<String> adapter=new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1);
		for (int i = 0; i < 25; i++) {
			adapter.add("第 "+(i+1)+" 条ITEM");
		}		
		listview.setAdapter(adapter);
	}
}
