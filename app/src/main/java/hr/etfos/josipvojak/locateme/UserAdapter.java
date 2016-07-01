package hr.etfos.josipvojak.locateme;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jvojak on 26.6.2016..
 */
public class UserAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<User> userlist;
    Typeface pacifico_font;
    Typeface custom_font;

    public UserAdapter(Context ctx, ArrayList<User> userlist) {
        super();
        this.ctx = ctx;
        this.userlist = userlist;
        this.pacifico_font = Typeface.createFromAsset(ctx.getAssets(),  "fonts/Pacifico.ttf");
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
        TextView tvEmail  = (TextView) convertView.findViewById(R.id.tvEmail);
        TextView tvProfilePic = (TextView) convertView.findViewById(R.id.tvProfilePic);
        TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
        TextView tvStatus = (TextView) convertView.findViewById(R.id.tvStatus);
        TextView tvLastOnline = (TextView) convertView.findViewById(R.id.tvLastOnline);

        String firstLetter = current.getEmail().substring(0,1);
        tvProfilePic.setTypeface(pacifico_font);
        tvProfilePic.setText(firstLetter);

        tvEmail.setText(current.getEmail());
        tvUsername.setText(current.getUsername());
        tvStatus.setText(current.getStatus());
        tvLastOnline.setText(current.getLast_online());
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
