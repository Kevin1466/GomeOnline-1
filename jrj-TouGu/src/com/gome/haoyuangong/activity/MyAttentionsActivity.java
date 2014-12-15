package com.gome.haoyuangong.activity;

import com.gome.haoyuangong.R;
import com.gome.haoyuangong.UserInfo;
import com.gome.haoyuangong.layout.self.ActivityChange;
import com.gome.haoyuangong.layout.self.ItemWithInfo;
import com.gome.haoyuangong.layout.self.SelfView.UserType;
import com.gome.haoyuangong.layout.self.data.AttentionList;
import com.gome.haoyuangong.layout.self.data.AttentionList.AttentionItem;
import com.gome.haoyuangong.layout.self.data.AttentionList.Data;
import com.gome.haoyuangong.layout.self.data.FansList.FansItem;
import com.gome.haoyuangong.net.Request;
import com.gome.haoyuangong.net.RequestHandlerListener;
import com.gome.haoyuangong.net.Request.Method;
import com.gome.haoyuangong.net.url.NetUrlMyInfo;
import com.gome.haoyuangong.net.volley.ImageLoader;
import com.gome.haoyuangong.net.volley.JsonRequest;
import com.gome.haoyuangong.utils.StringUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MyAttentionsActivity extends ListViewActivity {
	ImageLoader imageLoader ;
	String pageID="0";
	String direction="f";
	int requestCount=20;
	String firstRecordId = "-1";
	boolean showloading = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageLoader = new ImageLoader(this);		
		setTitle("我的关注");
		setDividerHeight(0);
	}
	@Override
	protected void onLoad() {
		// TODO Auto-generated method stub
		super.onLoad();
		requestData(false);
	}
	protected void requestData(final boolean pull) {
		String url = "";
		if (getIntent().getIntExtra("usertype", -1) == UserType.utUserViewAdviser.ordinal())
			url = String.format(NetUrlMyInfo.ATTENTIONSURL, getIntent().getStringExtra("viewid"),pageID,direction,requestCount);
		else{
			if (direction.equals("f"))	
				url = String.format(NetUrlMyInfo.ATTENTIONSURL, UserInfo.getInstance().getUserId(),pageID,direction,requestCount);	
			else 
				url = String.format(NetUrlMyInfo.ATTENTIONSURL, UserInfo.getInstance().getUserId(),firstRecordId,direction,requestCount);
		}
		JsonRequest<AttentionList> request = new JsonRequest<AttentionList>(Method.GET, url,
				new RequestHandlerListener<AttentionList>(getContext()) {

					@Override
					public void onStart(Request request) {
						super.onStart(request);
						if (showloading)
							showLoading(request);
					}

					@Override
					public void onEnd(Request request) {
						super.onEnd(request);
						if (showloading)
							hideLoading(request);
						stopRefresh();
						stopLoadMore();
					}

					@Override
					public void onSuccess(String id, AttentionList data) {
						// TODO Auto-generated method stub
						try{
							if (pull)
								stopRefresh();
							stopLoadMore();
							if (data.getData().getList()==null || data.getData().getList().size() == 0){							
								setPullLoadEnable(false);
								if (pageID.equals("0")){
									showEmptyView();
									setEmptyText("暂无关注的投顾");
								}
								return;
							}
							else if (data.getData().getList().size() < requestCount){
								setPullLoadEnable(false);
							}
							else {
								setPullLoadEnable(true);
							}
							showDataView();
							if (pageID.equals("0"))
								clear();
							fillData(data.getData());
							String key = data.getData().getList().get(0).getPageId();
							if (firstRecordId.compareTo(key) < 0)
								firstRecordId = data.getData().getList().get(0).getPageId();
							pageID = data.getData().getList().get(data.getData().getList().size()-1).getPageId();
							reFresh();
						}
						catch(Exception e){
							
						}
						
					}
				}, AttentionList.class);
			send(request);
	}
	
	private void fillData(Data data) {
		for(int i=0;i<data.getList().size();i++){
			final ItemWithInfo item = new ItemWithInfo(this);	
			item.setBackgroundColor(getResources().getColor(R.color.divider_d0d0d0));
			AttentionItem attentionItem = data.getList().get(i);
			item.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (((AttentionItem)item.getTag()).getIsAdviser()==1){
						ActivityChange.ToAdviserHome(MyAttentionsActivity.this, ((AttentionItem)item.getTag()).getUserName(), 
								((AttentionItem)item.getTag()).getUserId());
					}
					else{
						ActivityChange.ToUserHome(MyAttentionsActivity.this, ((AttentionItem)item.getTag()).getUserName(), 
								((AttentionItem)item.getTag()).getUserId(),((AttentionItem)item.getTag()).getHeadImage());
					}
				}
			});
			item.setName(attentionItem.getUserName());
			if (attentionItem.getIsAdviser() == 1){
				item.setInfoText(StringUtils.isEmpty(attentionItem.getTypeDesc())? "":attentionItem.getTypeDesc() +" "+(StringUtils.isEmpty(attentionItem.getCompany())? "":attentionItem.getCompany()));
				if (attentionItem.getSignV() == 1)
					item.setHeadIcon(R.drawable.icon_v);
			}
			if (attentionItem.getSignV() == 1)
				item.setHeadIcon(R.drawable.icon_v);
			item.setHeadPicSize(120, 120);
			imageLoader.downLoadImage(attentionItem.getHeadImage(), item.getHeadPic());
//			item.setHeadAttachImage(R.drawable.icon_v);
			item.doLayout();
			item.setTag(attentionItem);
			if (direction.equals("f"))
				addItem(item);
			else
				addItem(item, 0);
		}
	}
	
	@Override
	public void OnStartLoadMore() {
		// TODO Auto-generated method stub
		super.OnStartLoadMore();
		direction = "f";
		requestCount = 20;
		showloading = false;
		requestData(false);
	}
	@Override
	public void OnStartRefresh() {
		// TODO Auto-generated method stub
		super.OnStartRefresh();
		direction = "f";
		pageID="0";
		requestCount = 20;
		showloading = false;
		requestData(true);
	}


}
