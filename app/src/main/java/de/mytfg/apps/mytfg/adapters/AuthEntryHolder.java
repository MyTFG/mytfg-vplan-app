package de.mytfg.apps.mytfg.adapters;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lb.auto_fit_textview.AutoResizeTextView;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.objects.Authentication;

import static android.view.View.GONE;

public class AuthEntryHolder extends RecyclerView.ViewHolder {
    private TextView timeout;
    private AutoResizeTextView devicename;
    private AutoResizeTextView device;
    private ImageView icon;
    private LinearLayout linearLayout;
    private TextView emptyText;
    private TextView lastUsed;
    private TextView lastGeo;
    private TextView lastIP;
    private TextView thisDevice;
    private TextView deviceInfo;

    private Context context;
    private CardView cardView;

    public AuthEntryHolder(View view) {
        super(view);
        context = view.getContext();
        cardView = (CardView) view;
        icon = (ImageView) view.findViewById(R.id.auth_entry_icon);
        linearLayout = (LinearLayout) view.findViewById(R.id.auth_entry_layout);
        emptyText = (TextView) view.findViewById(R.id.auth_entry_empty);

        timeout = view.findViewById(R.id.auth_entry_timeout);
        device = view.findViewById(R.id.auth_entry_device);
        //lastGeo = view.findViewById(R.id.auth_entry_lastgeo);
        //lastIP = view.findViewById(R.id.auth_entry_lastip);
        lastUsed = view.findViewById(R.id.auth_entry_lastused);
        devicename = view.findViewById(R.id.auth_entry_devicename);
        thisDevice = view.findViewById(R.id.auth_entry_thisdevice);
        deviceInfo = view.findViewById(R.id.auth_entry_deviceinfo);
    }

    public void update(Authentication auth) {
        update(auth, false);
    }

    public void update(final Authentication auth, boolean expanded) {
        if (auth == null) {
            linearLayout.setVisibility(GONE);
            emptyText.setVisibility(View.VISIBLE);
            cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
            return;
        }

        emptyText.setVisibility(GONE);
        linearLayout.setVisibility(View.VISIBLE);
        if (auth.isTimedout()) {
            timeout.setText(context.getString(R.string.manage_authentications_timedout_at, auth.getTimeoutDate()));
        } else {
            timeout.setText(context.getString(R.string.manage_authentications_timeout_at, auth.getTimeoutDate()));
        }
        devicename.setText(auth.getDevicename());
        device.setText(auth.getDevice());
        lastUsed.setText(context.getString(R.string.manage_authentications_lastused, auth.getLastUsedDate()));
        //lastGeo.setText(context.getString(R.string.manage_authentications_lastgeo, auth.getLastGeo()));
        //lastIP.setText(context.getString(R.string.manage_authentications_lastip, auth.getLastIp()));

        final MyTFGApi api = new MyTFGApi(context);
        if (api.getDevice().equals(auth.getDevice())) {
            thisDevice.setVisibility(View.VISIBLE);
        } else {
            thisDevice.setVisibility(View.GONE);
        }

        deviceInfo.setText(auth.getDeviceInfo());

        if ("ios".equals(auth.getDeviceOS())) {
            if ("tablet".equals(auth.getDeviceType())) {
                icon.setImageResource(R.drawable.authmanage_ios_tablet);
            } else {
                icon.setImageResource(R.drawable.authmanage_ios_phone);
            }
        } else {
            if ("tablet".equals(auth.getDeviceType())) {
                icon.setImageResource(R.drawable.authmanage_android_tablet);
            } else {
                icon.setImageResource(R.drawable.authmanage_android_phone);
            }
        }
    }

    public void setOnClickListener(CardView.OnClickListener listener) {
        cardView.setOnClickListener(listener);
    }
}
