package de.mytfg.apps.mytfg.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.text.TextUtilsCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.objects.VplanEntry;
import de.mytfg.apps.mytfg.objects.VrrEntry;

/**
 * Provices detailed related to a VRR connection.
 */

public class VrrDetailFragment extends AuthenticationFragment {
    private VrrEntry vrrEntry;

    public static VrrDetailFragment newInstance(VrrEntry entry) {
        VrrDetailFragment fragment = new VrrDetailFragment();
        fragment.vrrEntry = entry;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vrr_detail, container, false);
        TextView direction = (TextView) view.findViewById(R.id.vrr_detail_direction);
        TextView line = (TextView) view.findViewById(R.id.vrr_detail_line);
        TextView delay = (TextView) view.findViewById(R.id.vrr_detail_delay);
        TextView arrival = (TextView) view.findViewById(R.id.vrr_detail_arrival);
        TextView route = (TextView) view.findViewById(R.id.vrr_detail_route);
        TextView sched = (TextView) view.findViewById(R.id.vrr_detail_sched);
        TextView real = (TextView) view.findViewById(R.id.vrr_detail_real);
        ImageView typeImg = (ImageView) view.findViewById(R.id.vrr_detail_img);
        TextView type = (TextView) view.findViewById(R.id.vrr_detail_type);
        TextView platform = (TextView) view.findViewById(R.id.vrr_detail_platform);

        if (vrrEntry == null) {
            Log.e("NULL", "VRR ENTRY IS NULL!");
            return view;
        }

        direction.setText(vrrEntry.getDirection());
        line.setText(vrrEntry.getLine());
        delay.setText(vrrEntry.getDelayText());
        arrival.setText(vrrEntry.getArrival());
        type.setText(vrrEntry.getType());
        route.setText(TextUtils.join(" - ", vrrEntry.getRoute()));
        sched.setText(vrrEntry.getSched());
        real.setText(vrrEntry.getDate());
        platform.setText(vrrEntry.getPlatform());
        if ("U-Bahn".equals(vrrEntry.getType())) {
            typeImg.setImageResource(R.drawable.ic_vrr_tram);
            ((MainActivity)getActivity()).getToolbarManager().setImage(R.drawable.tram_header, true);
        } else {
            typeImg.setImageResource(R.drawable.ic_vrr_bus);
            ((MainActivity)getActivity()).getToolbarManager().setImage(R.drawable.bus_header, true);
        }
        ((MainActivity)getActivity()).getToolbarManager().setTitle(vrrEntry.getType()).showBottomScrim();

        ViewCompat.setTransitionName(view, getArguments().getString("frame"));
        //ViewCompat.setTransitionName(direction, getArguments().getString("direction"));
        ViewCompat.setTransitionName(typeImg, getArguments().getString("typeImg"));
        //ViewCompat.setTransitionName(type, getArguments().getString("type"));
        ViewCompat.setTransitionName(line, getArguments().getString("line"));
        //ViewCompat.setTransitionName(arrival, getArguments().getString("arrival"));



        return view;
    }
}
