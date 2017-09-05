package de.mytfg.apps.mytfg.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.UUID;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.activities.MainActivity;
import de.mytfg.apps.mytfg.api.MyTFGApi;
import de.mytfg.apps.mytfg.api.SuccessCallback;
import de.mytfg.apps.mytfg.objects.Authentication;
import de.mytfg.apps.mytfg.objects.VplanEntry;

public class RecylcerAuthAdapter extends RecyclerView.Adapter<AuthEntryHolder> {
    private Context context;
    private ArrayList<Authentication> elements = new ArrayList<>();

    private int lastPosition = -1; // None
    private int expandedPosition = -1;

    private String unique;

    private int endOffset;

    public RecylcerAuthAdapter(Context c) {
        this.context = c;
        this.endOffset = endOffset;
        unique = UUID.randomUUID().toString();
    }

    public void addItem(Authentication item) {
        elements.add(item);
    }

    public int getCount() {
        return elements.size();
    }

    @Override
    public AuthEntryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.cardview_authentry, null);
        return new AuthEntryHolder(view);
    }

    @Override
    public void onBindViewHolder(final AuthEntryHolder holder, int position) {
        if (elements.size() == 0) {
            holder.update(null, false);
        } else {
            final Authentication entry = elements.get(position);
            holder.update(entry, false);
            final MyTFGApi api = new MyTFGApi(context);

            holder.setOnClickListener(new CardView.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("CARDVIEW", "CLICK");
                    if (api.getDevice().equals(entry.getDevice())) {
                        AlertDialog.Builder nodelete = new AlertDialog.Builder(context);
                        nodelete.setTitle(context.getString(R.string.manage_authentications_dlg_header));
                        nodelete.setMessage(context.getString(R.string.manage_authentications_dlg_this_device));
                        nodelete.setIcon(R.drawable.ic_menu_about_old);
                        AlertDialog abs = nodelete.create();
                        abs.show();
                    } else {
                        AlertDialog.Builder deleteDlg = new AlertDialog.Builder(context);
                        deleteDlg.setTitle(context.getString(R.string.manage_authentications_dlg_header));
                        deleteDlg.setMessage(context.getString(R.string.manage_authentications_dlg_remove_text));
                        deleteDlg.setIcon(R.drawable.auth_delete);
                        deleteDlg.setPositiveButton(context.getString(R.string.manage_authentications_dlg_remove), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final ProgressDialog progress = ProgressDialog.show(context,
                                        context.getString(R.string.please_wait),
                                        context.getString(R.string.manage_authentications_process),
                                        true);
                                entry.remove(new SuccessCallback() {
                                    @Override
                                    public void callback(boolean success) {
                                        progress.cancel();
                                        if (success) {
                                            ((MainActivity)context).getNavi().snackbar(context.getString(R.string.manage_authentications_remove_success));
                                        } else {
                                            ((MainActivity)context).getNavi().snackbar(context.getString(R.string.manage_authentications_remove_failed));
                                        }
                                    }
                                });
                            }
                        });
                        deleteDlg.setNegativeButton(context.getString(R.string.manage_authentications_dlg_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        AlertDialog abs = deleteDlg.create();
                        abs.show();
                    }
                }
            });

        }

        /*final CardView cardView = holder.getCardView();
        final TextView plan = holder.getPlan();
        final TextView subst = holder.getSubst();
        final TextView comment = holder.getComment();

        final String uniqueCardview = "frame_" + position + "_" + unique;
        final String uniquePlan = "fplan_" + position + "_" + unique;
        final String uniqueSubst = "subst_" + position + "_" + unique;
        final String uniqueComment = "comment_" + position + "_" + unique;
        ViewCompat.setTransitionName(cardView, uniqueCardview);
        ViewCompat.setTransitionName(plan, uniquePlan);
        ViewCompat.setTransitionName(subst, uniqueSubst);
        ViewCompat.setTransitionName(comment, uniqueComment);


        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Pair<String, View>> sharedElements = new HashMap<>();
                sharedElements.put("frame", new Pair<String, View>(uniqueCardview, cardView));
                sharedElements.put("plan", new Pair<String, View>(uniquePlan, plan));
                sharedElements.put("subst", new Pair<String, View>(uniqueSubst, subst));
                sharedElements.put("comment", new Pair<String, View>(uniqueComment, comment));

                PlanDetailFragment fragment = PlanDetailFragment.newInstance(entry.getVplan(), entry);
                ((MainActivity) context).getNavi().navigate(
                        fragment,
                        R.id.fragment_container,
                        sharedElements
                );
            }
        });*/
        //setAnimation(holder.getCardView(), position);
    }



    @Override
    public int getItemCount() {
        return Math.max(1, elements.size());
    }

    public interface PlanEntryAdapterEvents {
        void onEndReached(ArrayList<VplanEntry> elements);
    }

    public void clear() {
        elements.clear();
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
