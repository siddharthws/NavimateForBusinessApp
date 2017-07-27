package com.biz.navimate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Base fragment class to be inherited by all fragments used in the app
 */

public abstract class BaseFragment extends Fragment
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "BASE_FRAGMENT";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    public BaseFragment()
    {
        // Required empty public constructor
    }

    // ----------------------- Abstracts ----------------------- //
    @Override
    public abstract View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
    }

    // ----------------------- Public APIs ----------------------- //
    // ----------------------- Private APIs ----------------------- //
}
