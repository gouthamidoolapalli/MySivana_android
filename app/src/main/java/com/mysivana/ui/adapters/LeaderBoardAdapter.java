package com.mysivana.ui.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mysivana.R;
import com.mysivana.custom.MSTextView;
import com.mysivana.mvp.model.ReferralBoardResponse;
import com.mysivana.util.SharedPrefsUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder> {


    private Activity context;
    private LayoutInflater mInflater;
    private List<ReferralBoardResponse.UserScore> userScores;
    private int userId;
    private int myRank;

    public LeaderBoardAdapter(Activity context) {
        this.context = context;
        userId = Integer.parseInt(SharedPrefsUtils.getLoggedUserObject(context).getValue().getUserId());
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_leader_board, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ReferralBoardResponse.UserScore userItem = userScores.get(position);

        if (position <= 2) {
            holder.winnerRankLayout.setVisibility(View.VISIBLE);
            holder.winnerRank3.setText((position + 1) + "");
            holder.winnerRank.setVisibility(View.INVISIBLE);
        } else {
            holder.winnerRankLayout.setVisibility(View.INVISIBLE);
            holder.winnerRank.setVisibility(View.VISIBLE);
            holder.winnerRank.setText((position + 1) + "");
        }
        if (myRank != -1 && (myRank-1)==position) {
            holder.itemParent.setBackgroundColor(ContextCompat.getColor(context, R.color.leader_item_gray));

        } else {
            holder.itemParent.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }
        if (userItem.getFullName() != null && !TextUtils.isEmpty(userItem.getFullName())) {
            holder.winnerNameTv.setText(userItem.getFullName());
        } else {
            holder.winnerNameTv.setText("");
        }
        if (userItem.getProfileUrl() != null && !TextUtils.isEmpty(userItem.getProfileUrl())) {
            Picasso.with(context).load(userItem.getProfileUrl()).resize(80, 80)
                    .placeholder(R.drawable.ic_user_avatar).into(holder.winnerImg);
        } else {
            holder.winnerImg.setImageResource(R.drawable.ic_user_avatar);
        }

        if (userItem.getMysivanaScore() > 0) {
            holder.winnerPoints.setText(userItem.getMysivanaScore()+"");
        } else {
            holder.winnerPoints.setText("0");
        }

    }


    @Override
    public int getItemCount() {
        return (userScores != null && userScores.size() > 0) ? userScores.size() : 0;
    }

    public void setMyLeaderBoardList(List<ReferralBoardResponse.UserScore> userScores) {
        this.userScores = userScores;
        notifyDataSetChanged();
    }

    public void setMyRank(int myRank) {
        this.myRank = myRank;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.winner_rank_layout)
        FrameLayout winnerRankLayout;
        @BindView(R.id.winner_rank_3)
        MSTextView winnerRank3;
        @BindView(R.id.winner_rank)
        MSTextView winnerRank;
        @BindView(R.id.winner_img)
        ImageView winnerImg;
        @BindView(R.id.winner_name_tv)
        MSTextView winnerNameTv;
        @BindView(R.id.winner_points)
        MSTextView winnerPoints;
        @BindView(R.id.item_parent)
        RelativeLayout itemParent;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
