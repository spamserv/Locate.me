package hr.etfos.josipvojak.locateme;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jvojak on 26.6.2016..
 */
public class UserAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<User> userlist;

    public UserAdapter(Context ctx, ArrayList<User> userlist) {
        super();
        this.ctx = ctx;
        this.userlist = userlist;
    }

    public int getCount() {
        return this.userlist.size();
    }

    @Override
    public Object getItem(int position) {
        return this.userlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = View.inflate(ctx, R.layout.list_item_user, null);
        }
        User current = userlist.get(position);
        Button btnAccept = (Button) convertView.findViewById(R.id.btnAccept);
        TextView tvEmail  = (TextView) convertView.findViewById(R.id.tvEmail);

        tvEmail.setText(current.getEmail());
        return convertView;
    }

    public void clear()
    {
        userlist.clear();
        notifyDataSetChanged();
    }

    public void add(User object)
    {
        userlist.add(object);
        notifyDataSetChanged();
    }
}
