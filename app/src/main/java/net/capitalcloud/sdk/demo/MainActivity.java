package net.capitalcloud.sdk.demo;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.capitalcloud.live.sdk.LiveManager;
import net.capitalcloud.live.sdk.SDKClient;
import net.capitalcloud.live.sdk.callback.ResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private LiveManager liveManager;
    private SDKClient client;
    private JSONArray lives;
    private ListView livesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        livesView = (ListView)this.findViewById(R.id.lives);
        client = SDKClient.getInstance(getApplication(),"3PthiUuTUAKoDR_0fWxO3KqPPkrenJqk1rwvQt_GsUrh4EGXVEZtHBQCVI-lvEKM");
        liveManager = client.getLiveManager();
        liveManager.list(new ResponseListener() {
            @Override
            public void onSuccess(String response) {
                try{
                    lives = new JSONArray(response);
                    livesView.setAdapter(new ListViewAdapter(lives));
                    livesView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                                long arg3) {
                            try{
                                JSONObject live = lives.getJSONObject(arg2);
//                                if(live.getString("status") == "1"){
//                                    Toast.makeText(MainActivity.this, "没有直播",
//                                            Toast.LENGTH_SHORT).show();
//                                    return;
//                                }
                                PlayActivity.gotoPlay(MainActivity.this,live.getString("b_url"), "",live.getString("name"), null);
                            }catch (Exception e){

                            }
                        }

                    });
                }catch (Exception e){

                }
            }

            @Override
            public void onFailure(Throwable throwable) {
            }
        });
    }
    public class ListViewAdapter extends BaseAdapter {
        View[] itemViews;
        public ListViewAdapter(JSONArray jsonArray) throws JSONException {
            itemViews = new View[jsonArray.length()];

            for (int i=0; i<itemViews.length; ++i){
                itemViews[i] = makeItemView(jsonArray.getJSONObject(i));
            }
        }

        public int getCount()   {
            return itemViews.length;
        }

        public View getItem(int position)   {
            return itemViews[position];
        }

        public long getItemId(int position) {
            return position;
        }

        private View makeItemView(JSONObject item) throws JSONException {
            LayoutInflater inflater = (LayoutInflater)MainActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.listview_item, null);
            TextView title = (TextView)itemView.findViewById(R.id.itemTitle);
            title.setText(item.getString("name"));
            ImageView image = (ImageView)itemView.findViewById(R.id.itemImage);
            if(item.getString("status") == "1"){
                image.setBackgroundResource(android.R.drawable.presence_video_away);
            }else{
                image.setBackgroundResource(android.R.drawable.presence_video_online);
            }
            return itemView;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            return itemViews[position];
        }
    }
}
