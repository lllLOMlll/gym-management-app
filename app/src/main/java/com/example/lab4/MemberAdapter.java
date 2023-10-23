package com.example.lab4;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MemberAdapter extends ArrayAdapter<Member> {

    public MemberAdapter(Context context, ArrayList<Member> members) {
        super(context, 0, members);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Member member = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_member, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvDateOfBirth = convertView.findViewById(R.id.tvDateOfBirth);
        TextView tvAge = convertView.findViewById(R.id.tvAge);
        TextView tvAddress = convertView.findViewById(R.id.tvAddress);
        TextView tvCity = convertView.findViewById(R.id.tvCity);
        TextView tvProvince = convertView.findViewById(R.id.tvProvince);
        TextView tvBarcode = convertView.findViewById(R.id.tvBarcode);
        ImageView ivAvatar = convertView.findViewById(R.id.ivAvatar);

        tvName.setText(member.getFirstName() + " " + member.getLastName());
        tvAddress.setText("Address: " + member.getAddress());
        tvCity.setText("City: " + member.getCity());
        tvProvince.setText("Province: " + member.getProvince());
        tvBarcode.setText("Barcode: " + member.getCode());

        // Load the avatar image from the byte array and display it in the ImageView
        if (member.getAvatar() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(member.getAvatar(), 0, member.getAvatar().length);
            ivAvatar.setImageBitmap(bitmap);
        } else {
            ivAvatar.setImageResource(R.drawable.default_avatar);
        }

        return convertView;
    }
}
