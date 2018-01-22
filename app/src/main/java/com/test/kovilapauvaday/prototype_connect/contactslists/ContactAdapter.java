package com.test.kovilapauvaday.prototype_connect.contactslists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.kovilapauvaday.prototype_connect.R;
import com.test.kovilapauvaday.prototype_connect.users_amies_profile.User;

import java.util.List;

/**
 * Created by kovilapauvaday on 17/12/2017.
 */

public class ContactAdapter extends ArrayAdapter<User>{
    final int layoutResource;
    private final List<User> contactList;

    private static class ViewHolder {
        CheckBox checkBox;
        TextView textView;
        ImageView photo;
    }

    public ContactAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
        layoutResource = resource;
        contactList = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = createView();
        }

        final User user = getItem(position);
        ViewHolder holder = (ViewHolder)convertView.getTag();

        holder.textView.setText(user.getName());
        holder.checkBox.setChecked(user.isChecked());
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.switchCheckbox();
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    private View createView(){
        // Create item
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(layoutResource, null);

        ViewHolder holder = new ViewHolder();
        holder.textView = view.findViewById(R.id.textview_contact_name);
        holder.checkBox = view.findViewById(R.id.checkbox_contact);
        holder.photo = view.findViewById(R.id.imageview_contact_photo);

        view.setTag(holder);
        return view;
    }



}
