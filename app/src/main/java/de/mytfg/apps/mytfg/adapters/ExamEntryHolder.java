package de.mytfg.apps.mytfg.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lb.auto_fit_textview.AutoResizeTextView;

import java.util.List;

import de.mytfg.apps.mytfg.R;
import de.mytfg.apps.mytfg.objects.ExamEntry;
import de.mytfg.apps.mytfg.objects.VplanEntry;
import de.mytfg.apps.mytfg.tools.Settings;

import static android.view.View.GONE;

public class ExamEntryHolder extends RecyclerView.ViewHolder {
    private TextView text;

    private TextView date;
    private TextView month;
    private TextView year;
    private TextView day;
    private TextView block;

    private TextView teacher;
    private TextView subject;
    private TextView lessons;

    private LinearLayout layout;
    private TextView empty;

    private ImageView cross;
    private TextView canceled;

    private Context context;
    private CardView cardView;

    public ExamEntryHolder(View view) {
        super(view);
        context = view.getContext();
        cardView = (CardView) view;
        layout = (LinearLayout) view.findViewById(R.id.exam_layout);
        empty = view.findViewById(R.id.exam_empty);

        lessons = (TextView) view.findViewById(R.id.exam_lessons);
        subject = (TextView) view.findViewById(R.id.exam_subject);
        teacher = (TextView) view.findViewById(R.id.exam_teacher);

        text = (TextView) view.findViewById(R.id.exam_text);

        day = (TextView) view.findViewById(R.id.exam_dayname);
        date = (TextView) view.findViewById(R.id.exam_day);
        month = (TextView) view.findViewById(R.id.exam_month);
        year = (TextView) view.findViewById(R.id.exam_year);
        block = (TextView) view.findViewById(R.id.exam_block);

        cross = (ImageView) view.findViewById(R.id.exam_crossed);
        canceled = (TextView) view.findViewById(R.id.exam_rescheduled);
    }

    public void update(ExamEntry examEntry) {
        if (examEntry == null) {
            layout.setVisibility(GONE);
            empty.setVisibility(View.VISIBLE);
            return;
        }
        layout.setVisibility(View.VISIBLE);
        empty.setVisibility(GONE);

        cross.setVisibility(View.GONE);
        canceled.setVisibility(View.GONE);

        text.setText(examEntry.getText());
        teacher.setText(examEntry.getTeacher());
        subject.setText(examEntry.getSubject());
        lessons.setText(examEntry.getLessons());
        block.setText(examEntry.getBlock());

        day.setText(examEntry.getDayName());
        date.setText(examEntry.getDay());
        month.setText(examEntry.getMonth());
        year.setText(examEntry.getYear());



        if ("spanned".equals(examEntry.getType())) {
            block.setVisibility(GONE);
            teacher.setVisibility(GONE);
            lessons.setVisibility(GONE);
            subject.setVisibility(GONE);
            text.setVisibility(View.VISIBLE);
        } else {
            block.setVisibility(View.VISIBLE);
            teacher.setVisibility(View.VISIBLE);
            lessons.setVisibility(View.VISIBLE);
            subject.setVisibility(View.VISIBLE);
            text.setVisibility(GONE);
            if (examEntry.isCrossed()) {
                cross.setVisibility(View.VISIBLE);
                canceled.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setOnClickListener(CardView.OnClickListener listener) {
        cardView.setOnClickListener(listener);
    }

    public CardView getCardView() {
        return cardView;
    }

    public void clearAnimation()
    {
        cardView.clearAnimation();
    }
}
