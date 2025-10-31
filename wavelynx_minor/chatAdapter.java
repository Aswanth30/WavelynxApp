package com.example.wavelynx_minor;

import android.content.Context;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import java.util.ArrayList;

public class chatAdapter extends ArrayAdapter<String> {

    Context context;
    ArrayList<String> messages;

    public chatAdapter(Context context, ArrayList<String> messages) {
        super(context, R.layout.chat_bubble, messages);
        this.context = context;
        this.messages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.chat_bubble, parent, false);

        TextView msgText = row.findViewById(R.id.message_text);
        String msg = messages.get(position);
        msgText.setText(msg);

        LinearLayout bubbleLayout = row.findViewById(R.id.bubble_layout);

        // Decide which side based on prefix
        if (msg.startsWith("Me:")) {
            msgText.setBackgroundResource(R.drawable.chat_bubble_right);
            bubbleLayout.setGravity(Gravity.END);
            msgText.setTextColor(Color.WHITE);
        } else if (msg.startsWith("Friend:")){
            msgText.setBackgroundResource(R.drawable.chat_bubble_left);
            bubbleLayout.setGravity(Gravity.START);
            msgText.setTextColor(Color.BLACK);
        }

        return row;
    }
}
