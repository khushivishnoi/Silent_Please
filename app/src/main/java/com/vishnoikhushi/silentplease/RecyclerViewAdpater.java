package com.vishnoikhushi.silentplease;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.vishnoikhushi.silentplease.data.DbHandler;
import com.vishnoikhushi.silentplease.data.model.Contact;

import java.util.List;

public class RecyclerViewAdpater extends RecyclerView.Adapter<RecyclerViewAdpater.ViewHolder> {
    private Context context;
    private List<Contact> contactList;
    public RecyclerViewAdpater(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public RecyclerViewAdpater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_layout, parent, false);
        return new ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdpater.ViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.contactName.setText(contact.getName());
        holder.phoneNumber.setText(contact.getNumber());
        holder.allContactsLetter.setText(Character.toString(contact.getName().charAt(0)));
        if (position % 6 == 0) {
            holder.gd.setColor(Color.parseColor("#FDEFF4"));
        }
        if (position % 6 == 1) {
            holder.gd.setColor(Color.parseColor("#90E0EF"));
        }
        if (position % 6 == 2) {
            holder.gd.setColor(Color.parseColor("#FFEDDB"));
        }
        if (position % 6 == 3) {
            holder.gd.setColor(Color.parseColor("#B4CFB0"));
        }
        if (position % 6 == 4) {
            holder.gd.setColor(Color.parseColor("#AEFEFF"));
        }
        if (position % 6 == 5) {
            holder.gd.setColor(Color.parseColor("#C3DBD9"));
        }

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder   {
        public TextView contactName;
        public TextView phoneNumber;
        public ImageView circle;
        public TextView allContactsLetter;
        public View contactColor;
        public GradientDrawable gd;
        public DbHandler db;
        View view;
        public ViewHolder(@NonNull View itemView, final Context context) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contactName);
            phoneNumber = itemView.findViewById(R.id.contactNumber);
            allContactsLetter = (TextView) itemView.findViewById(R.id.contactLetter);
            contactColor = itemView.findViewById(R.id.contactColor);
            circle = (ImageView) itemView.findViewById(R.id.circle);
            gd = (GradientDrawable) circle.getBackground();
            db = new DbHandler(context);
            view = itemView;
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

                    alertDialog.setTitle("Remove");
                    alertDialog.setMessage("Are you sure you want remove this contact?");
                    alertDialog.setIcon(R.mipmap.ic_launcher_round);

                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Contact contact = new Contact(contactName.getText().toString().trim(), phoneNumber.getText().toString().trim());
                            db.deleteContact(contact);
                            int position = itemView.getVerticalScrollbarPosition();
                            notifyItemRemoved(position);

                        }
                    });

                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    alertDialog.show();
                    return true;
                }
            });
        }
    }
}
