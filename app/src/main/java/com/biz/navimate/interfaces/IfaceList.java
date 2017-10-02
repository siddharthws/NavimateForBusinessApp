package com.biz.navimate.interfaces;

/**
 * Created by Siddharth on 29-09-2017.
 */

public class IfaceList {
    // Task List Item interface
    public interface Task {
        void onItemClick(com.biz.navimate.objects.Task task);
        void onSubmitFormClick(com.biz.navimate.objects.Task task);
    }

    // Task List Item interface
    public interface Lead {
        void onItemClick(com.biz.navimate.objects.Lead lead);
    }
}
