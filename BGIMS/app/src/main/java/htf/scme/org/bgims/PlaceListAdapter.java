package htf.scme.org.bgims;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;

import java.util.List;

/**
 * Created by Administrator on 2016/5/6.
 */
public class PlaceListAdapter extends BaseAdapter {

  List<PoiInfo> mList;
  LayoutInflater mInflater;
  int notifyTip ;
  private int selectedPosition=0;

private class MyViewHolder {
  TextView placeName;
  TextView placeAddree;
}

  public PlaceListAdapter(LayoutInflater mInflater , List<PoiInfo> mList) {
    super();
    this.mList = mList;
    this.mInflater = mInflater;
    notifyTip = -1 ;
  }


  /**
   * 设置第几个item被选择
   * @param notifyTip
   */
  public void setNotifyTip(int notifyTip) {
    this.notifyTip = notifyTip;
  }

  @Override
  public int getCount() {
    // TODO Auto-generated method stub
    return mList.size();
  }

  @Override
  public Object getItem(int position) {
    // TODO Auto-generated method stub

    return mList.get(position);
  }

  @Override
  public long getItemId(int position) {
    // TODO Auto-generated method stub
    return position;
  }
  public  void clearSelection(int p){
    selectedPosition = p;
  }
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    // TODO Auto-generated method stub
    MyViewHolder holder;
    if (convertView == null) {
      System.out.println("----aa-");
      convertView = mInflater.inflate(R.layout.maplistitem, parent, false);
      holder = new MyViewHolder();
      holder.placeName = (TextView) convertView
        .findViewById(R.id.mapname);
      holder.placeAddree = (TextView) convertView
        .findViewById(R.id.mapaddress);

      holder.placeName.setText(mList.get(position).name);
      holder.placeAddree.setText(mList.get(position).address);
      convertView.setTag(holder);
    } else {
      holder = (MyViewHolder) convertView.getTag();
    }
    holder.placeName.setText(mList.get(position).name);
    holder.placeAddree.setText(mList.get(position).address);
    //根据重新加载的时候第position条item是否是当前所选择的，选择加载不同的图片
    if(selectedPosition == position ){
      holder.placeName.setTextColor(Color.parseColor("#ff6501"));
      holder.placeAddree.setTextColor(Color.parseColor("#ff6501"));
    }
    else {
      holder.placeName.setTextColor(Color.parseColor("#000000"));
      holder.placeAddree.setTextColor(Color.parseColor("#8f605f5f"));
    }

    return convertView;
  }  }
