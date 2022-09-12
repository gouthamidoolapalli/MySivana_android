package com.mysivana.ui.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mysivana.R;
import com.mysivana.mvp.model.CryptoMenuResponse;
import com.mysivana.util.AppConstants;
import com.mysivana.util.OnCryptoMenuClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CryptoMenuAdapter extends RecyclerView.Adapter<CryptoMenuAdapter.CryptoViewHolder> {

    private List<CryptoMenuResponse.Value> menuList;
    private OnCryptoMenuClickListener onCryptoMenuClickListener;

    public CryptoMenuAdapter(OnCryptoMenuClickListener onCryptoMenuClickListener) {
        this.onCryptoMenuClickListener = onCryptoMenuClickListener;
    }

    public void updateMenu(List<CryptoMenuResponse.Value> menuList){
        this.menuList = menuList;
        notifyDataSetChanged();
    }

    @Override
    public CryptoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_crypto_menu, parent, false);
        return new CryptoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CryptoViewHolder holder, int position) {
        CryptoMenuResponse.Value value = menuList.get(position);
        holder.title.setText(value.getCryptoName());
        if(value.getCryptoCode().equalsIgnoreCase(AppConstants.CryptoCodes.BTC)){
            holder.title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bitcoin_original, 0, 0, 0);
        }else if(value.getCryptoCode().equalsIgnoreCase(AppConstants.CryptoCodes.ETH)){
            holder.title.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ethereum_original, 0, 0, 0);
        }
        holder.title.setTag(value);
    }

    @Override
    public int getItemCount() {
        return menuList!=null?menuList.size():0;
    }


    public class CryptoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_crypto_menu)
        public TextView title;

        public CryptoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            title.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onCryptoMenuClickListener.onItemClick((CryptoMenuResponse.Value) v.getTag());
        }
    }
}
