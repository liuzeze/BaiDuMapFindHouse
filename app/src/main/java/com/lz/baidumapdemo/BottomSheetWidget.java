package com.lz.baidumapdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

/**
 * -----------作者----------日期----------变更内容-----
 * -          刘泽      2018-11-09       创建class
 */
public class BottomSheetWidget {
    private final View mView;
    private TextView mTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BottomSheetBehavior bottomSheetBehavior;

    public BottomSheetWidget(View view) {
        mView = view;
        initViews();
        initListeners();
    }


    /**
     * method to initialize the views
     */
    private void initViews() {
        bottomSheetBehavior = BottomSheetBehavior.from(mView);
        mTextView = (TextView) mView.findViewById(R.id.title);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swiperefreshlayout);
        SwipeMenuRecyclerView recyclerView = mView.findViewById(R.id.recyclerview);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);


        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new Holder(View.inflate(viewGroup.getContext(), R.layout.item_bubbo, null));
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

            }

            @Override
            public int getItemCount() {
                return 50;
            }

            class Holder extends RecyclerView.ViewHolder {

                public Holder(@NonNull View itemView) {
                    super(itemView);
                }
            }
        });

        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "1111", Toast.LENGTH_SHORT).show();
            }
        });

    }


    /**
     * method to initialize the listeners
     */
    private void initListeners() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        mTextView.getLayoutParams().height = 1;
                        mTextView.requestLayout();
                        break;
                    default:
                        break;
                }
            }


            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
                if (mView.getTop() <= 200) {
                    mTextView.getLayoutParams().height = 200 - mView.getTop();
                    mTextView.requestLayout();
                }
                if (mView.getTop() < 10) {
                    mTextView.animate().translationY(0).setDuration(200).start();
                }

            }
        });


    }

    public void collapsed() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void hide() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public boolean closeBottomSheet() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            return true;
        } else {
            mTextView.animate().translationY(-500).setDuration(200).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mSwipeRefreshLayout.setRefreshing(false);
                    mTextView.animate().setListener(null);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }).start();

            return false;
        }
    }
}
