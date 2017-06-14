package de.mytfg.apps.mytfg.logic;

import android.content.Context;
import android.support.annotation.Nullable;

import de.mytfg.apps.mytfg.fragments.FragmentHolderLogic;
import de.mytfg.apps.mytfg.objects.Abbreviations;
import de.mytfg.apps.mytfg.objects.Exams;
import de.mytfg.apps.mytfg.objects.TfgEvents;
import de.mytfg.apps.mytfg.objects.TfgNews;
import de.mytfg.apps.mytfg.objects.Vplan;

public class LogicFactory {
    public static FragmentHolderLogic createLogic(String type, Context c, @Nullable String param) {
        switch (type) {
            default:
                return null;
            case "NewsLogic":
                TfgNews news = new TfgNews(c);
                return new NewsLogic(news, c);
            case "EventsLogic":
                TfgEvents events = new TfgEvents(c);
                return new EventsLogic(events, c);
            case "PlanLogic":
                Vplan plan = new Vplan(c, param);
                return new PlanLogic(plan);
            case "AbbreviationLogic":
                Abbreviations abbr = new Abbreviations(c, param);
                return new AbbreviationLogic(abbr);
            case "ExamLogic":
                Exams exams = new Exams(c);
                return new ExamLogic(exams, param);
        }
    }
}
